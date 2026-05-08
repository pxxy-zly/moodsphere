**运维迭代发布（已稳定运行后的日常更新）**

适用场景：服务已按 [deploy.md](file:///d:/aZLY/A-MSS/02-code/document/deploy/deploy.md) 在服务器稳定运行（Docker Compose：MySQL、Redis、backend、ai、nginx），现在需要小步迭代更新。

本文只给“关键指令 + 最小流程”。默认服务器目录结构如下：

- `/opt/mss/docker-compose.yml`
- `/opt/mss/backend/moodsphere-admin.jar`
- `/opt/mss/frontend/`（Nginx 静态资源目录）
- `/opt/mss/ai/`（AI 服务 build context）
- `/data/moodsphere/upload`（上传图片/语音目录，backend 与 nginx 都需要挂载）

***

**0. 迭代前检查（建议每次先做）**

```bash
docker ps
docker compose -f /opt/mss/docker-compose.yml ps
docker compose -f /opt/mss/docker-compose.yml logs --tail=200 backend
docker compose -f /opt/mss/docker-compose.yml logs --tail=200 ai
```

如果要确认 Nginx 配置正常：

```bash
docker exec -it mss-nginx nginx -t
```

***

**1. 仅更新后端（替换 JAR）**

关键点：backend 容器把 `/opt/mss/backend` 挂载到容器 `/app`，替换宿主机的 JAR 后重启容器即可生效。

1. 本地构建新 JAR（在开发机执行）：

```bash
cd /path/to/02-code/MSS-backend
mvn clean package -Dmaven.test.skip=true
```

构建产物默认位置：

- `MSS-backend/moodsphere-admin/target/moodsphere-admin.jar`

1. 备份旧包（建议保留可回滚版本）：

```bash
cd /opt/mss/backend
cp moodsphere-admin.jar "moodsphere-admin.jar.bak_$(date +%F_%H%M%S)"
```

1. 上传新包覆盖：

- 把本地构建产物上传到：`/opt/mss/backend/moodsphere-admin.jar`

1. 重启后端：

```bash
docker restart mss-backend
```

1. 观察启动日志：

```bash
docker logs -f mss-backend
```

**后端回滚（JAR）**

```bash
cd /opt/mss/backend
ls -lt moodsphere-admin.jar.bak_*
cp moodsphere-admin.jar.bak_YYYY-MM-DD_HHMMSS moodsphere-admin.jar
docker restart mss-backend
docker logs -f mss-backend
```

***

**2. 仅更新管理端前端（替换 dist）**

关键点：Nginx 静态目录挂载的是 `/opt/mss/frontend`，更新文件通常无需重启容器。

1. 上传本地构建产物 `dist/` 到服务器（覆盖到 `/opt/mss/frontend/`）。
2. 常用同步命令（在服务器执行，按你的实际上传路径替换）：

```bash
rsync -av --delete /path/to/dist/ /opt/mss/frontend/
```

1. 如需强制生效（例如你改了 Nginx 配置或缓存策略），可 reload/restart：

```bash
docker exec -it mss-nginx nginx -s reload
docker logs --tail=200 mss-nginx
```

***

**3. 更新 AI 服务（Python 代码/依赖变更）**

关键点：AI 容器来自 `build: context: /opt/mss/ai`，更新代码或 requirements 后，需要重新 build 并重建容器；仅 `docker restart` 不会应用新镜像。

1. 上传/更新 `/opt/mss/ai/` 下的代码与 `requirements.txt`
2. 重新构建并重建 ai：

```bash
docker compose -f /opt/mss/docker-compose.yml build ai
docker compose -f /opt/mss/docker-compose.yml up -d --no-deps ai
```

1. 检查日志与健康：

```bash
docker logs -f mss-ai
docker exec mss-ai python -c "import urllib.request;print(urllib.request.urlopen('http://127.0.0.1:9011/health').read().decode())"
```

**AI 回滚**

- 如果你通过“覆盖代码 + 重新 build”发布：建议发布前把 `/opt/mss/ai/` 打包备份一份（或用 git tag/分支），回滚时还原代码后重复 build/up。
- 如果你有镜像标签策略：可改 `docker-compose.yml` 为固定 `image: your-ai:tag`，然后通过切换 tag 回滚。

***

**4. 修改配置（env / compose / nginx）后如何生效**

关键点：`env_file` 只在“创建容器”时读取，单纯 `docker restart` 通常不会加载新 env；需要重建容器。

**4.1 修改 backend.env 后**

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --no-deps --force-recreate backend
docker logs -f mss-backend
```

**4.2 修改 ai.env 后**

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --no-deps --force-recreate ai
docker logs -f mss-ai
```

**4.3 修改 docker-compose.yml 后**

```bash
docker compose -f /opt/mss/docker-compose.yml up -d
docker compose -f /opt/mss/docker-compose.yml ps
```

如果新增了 volume（例如给 nginx 增加 `/data/moodsphere/upload:/data/moodsphere/upload`），建议强制重建对应容器：

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --force-recreate nginx
docker exec -it mss-nginx ls -l /data/moodsphere/upload
```

**4.4 修改 Nginx 配置（default.conf）后**

```bash
docker exec -it mss-nginx nginx -t
docker exec -it mss-nginx nginx -s reload
docker logs --tail=200 mss-nginx
```

***

**5. 日常排障关键指令**

查看状态：

```bash
docker ps
docker compose -f /opt/mss/docker-compose.yml ps
docker stats --no-stream
```

看日志（按服务替换 backend/ai/nginx/mysql/redis）：

```bash
docker compose -f /opt/mss/docker-compose.yml logs -f --tail=200 backend
docker compose -f /opt/mss/docker-compose.yml logs -f --tail=200 ai
docker compose -f /opt/mss/docker-compose.yml logs -f --tail=200 nginx
```

进入容器排查（示例：backend）：

```bash
docker exec -it mss-backend sh
```

*** 

**5.1 上传图片/语音资源排障**

现象：

- 移动端上传成功但图片不回显
- AI 分析回退，`fallbackReason` 类似 `The image format is illegal and cannot be opened`
- `https://lingdone.cn/profile/upload/...png` 返回 `text/html`

根因通常是 `/profile/**` 被前端 `try_files ... /index.html` 吃掉了，返回的是前端 HTML，不是图片。

**正确检查顺序：**

1. 确认后端实际上传目录：

```bash
docker exec -it mss-backend sh -c 'echo $RUOYI_PROFILE && ls -l /data/moodsphere/upload/upload | head'
```

1. 确认 nginx 容器也挂载了同一个目录：

```bash
docker exec -it mss-nginx ls -l /data/moodsphere/upload/upload
```

如果 nginx 容器看不到文件，需要在 `docker-compose.yml` 的 nginx 服务增加：

```yaml
volumes:
  - /data/moodsphere/upload:/data/moodsphere/upload
```

然后重建 nginx：

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --force-recreate nginx
```

1. 确认 nginx 配置包含 `/profile/` 静态映射：

```bash
docker exec -it mss-nginx nginx -T | grep -A8 "location /profile"
```

应包含：

```nginx
location /profile/ {
    alias /data/moodsphere/upload/;
    try_files $uri =404;
}
```

1. 验证公网 URL：

```bash
curl -I https://lingdone.cn/profile/upload/2026/05/08/your-image.png
```

正确结果：

```text
HTTP/1.1 200 OK
Content-Type: image/png
```

错误结果与含义：

- `Content-Type: text/html`：仍然命中了前端 HTML，检查 nginx 配置文件路径、reload 是否生效、`location /profile/` 是否写在 `location /` 前
- `404`：nginx 配置生效了，但 `alias` 目录或文件路径不对
- `403`：目录权限不足，检查宿主机 `/data/moodsphere/upload` 权限

*** 

**5.2 AI 多模态超时排障**

Qwen-Omni 图片/语音分析通常超过 8 秒。服务器配置建议：

```env
MOOD_AI_PY_TIMEOUT_MS=60000
```

修改 `/opt/mss/backend/backend.env` 后，需要重建 backend 容器让 env 生效：

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --no-deps --force-recreate backend
docker logs -f mss-backend
```

如果移动端仍显示接口超时，需要同步检查小程序端 `runMoodAnalyze` 的请求超时是否大于后端 AI 超时。

***

**6. 数据库备份/恢复（常用）**

只给关键指令，按实际密码替换。

**6.1 备份**

```bash
docker exec mss-mysql mysqldump -uroot -pYOUR_ROOT_PASSWORD moodsphere > "/opt/mss/mysql/backup_$(date +%F_%H%M%S).sql"
```

**6.2 恢复**

```bash
docker exec -i mss-mysql mysql -uroot -pYOUR_ROOT_PASSWORD moodsphere < /opt/mss/mysql/backup_YYYY-MM-DD_HHMMSS.sql
```

***

**7. 一次“标准发布”最小清单**

1. 先看状态与日志：`docker ps`、`docker compose ... logs --tail=200 backend`
2. 更新对应产物：
   - 后端：替换 `/opt/mss/backend/moodsphere-admin.jar`
   - 前端：覆盖 `/opt/mss/frontend/`
   - AI：更新 `/opt/mss/ai/` 后 `compose build/up`
3. 只重启必要服务：
   - 后端：`docker restart mss-backend`
   - AI：`docker compose ... build ai && docker compose ... up -d --no-deps ai`
   - Nginx 配置变更：`nginx -t` + `nginx -s reload`
4. 发布后观察：`docker logs -f mss-backend`、`docker logs -f mss-ai`
