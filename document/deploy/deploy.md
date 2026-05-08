**完整部署流程（Docker + 域名 + HTTPS + Python AI并入）**

**0. 你将得到的结果**

- 访问 `https://lingdone.cn` 打开管理端前端
- API 走 `https://lingdone.cn/prod-api/*`，由 Nginx 反代到后端容器
- MySQL、Redis、后端、Python AI、Nginx 全部在同一台服务器容器化运行
- 后端通过内网地址 `http://ai:9011` 调用 AI（不对公网暴露 AI 端口）
- 配置集中在 `/opt/mss/.env`、`/opt/mss/backend/backend.env`、`/opt/mss/ai/ai.env`

***

**1. DNS 与服务器前置准备**

- DNS 解析：在域名控制台把 `lingdone.cn` 的 A 记录指向服务器公网 IP
- 服务器系统推荐 Ubuntu 22.04 或 CentOS 7/8
- 防火墙只开放端口：
  - `80`、`443` 对外开放
  - `22`（SSH）
  - `3306`、`6379` 不建议对公网开放

***

**2. 安装 Docker 与 Compose**
Ubuntu 示例：

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
```

检查版本：

```bash
docker --version
docker compose version
```

***

**3. 目录结构规划**

```bash
sudo mkdir -p /opt/mss/backend
sudo mkdir -p /opt/mss/frontend
sudo mkdir -p /opt/mss/ai
sudo mkdir -p /opt/mss/nginx/certbot
sudo mkdir -p /opt/mss/mysql
sudo mkdir -p /opt/mss/redis
sudo mkdir -p /data/moodsphere/upload
```

***

**4. 后端打包与上传**
在本地 `MSS-backend` 根目录执行：

```bash
mvn clean package '-Dmaven.test.skip=true'
```

上传 `moodsphere-admin.jar` 到服务器：

```
/opt/mss/backend/moodsphere-admin.jar
```

***

**5. 前端打包与上传**
在本地 `MSS-ui-admin` 根目录执行：

```bash
npm install
npm run build:prod
```

上传 `dist/` 到服务器：

```
/opt/mss/frontend/
```

***

**6. 上传 Python AI 服务代码**
把本地目录 `MSS-backend/py-ai-service` 上传到：

```
/opt/mss/ai/
```

然后在服务器创建 `/opt/mss/ai/Dockerfile`：

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app ./app
EXPOSE 9011
CMD ["uvicorn","app.main:app","--host","0.0.0.0","--port","9011","--workers","2"]
```

***

**7. 生成环境变量文件**
**7.1** **`/opt/mss/.env`**

```env
# Domain
DOMAIN=lingdone.cn
TZ=Asia/Shanghai

# MySQL
MYSQL_ROOT_PASSWORD=CHANGE_ME_ROOT_PASSWORD
MYSQL_DATABASE=moodsphere

# Redis
REDIS_PASSWORD=CHANGE_ME_REDIS_PASSWORD
```

**7.2** **`/opt/mss/backend/backend.env`**

```env
SPRING_PROFILES_ACTIVE=druid

# MySQL (Druid)
SPRING_DATASOURCE_DRUID_MASTER_URL=jdbc:mysql://mysql:3306/moodsphere?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
SPRING_DATASOURCE_DRUID_MASTER_USERNAME=root
SPRING_DATASOURCE_DRUID_MASTER_PASSWORD=CHANGE_ME_ROOT_PASSWORD

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=CHANGE_ME_REDIS_PASSWORD
SPRING_DATA_REDIS_DATABASE=0

# JWT
TOKEN_SECRET=CHANGE_ME_TOKEN_SECRET

# Upload path
RUOYI_PROFILE=/data/moodsphere/upload

# 微信小程序配置
WECHAT_MINIAPP_APPID=wx313bd8559c50679f
WECHAT_MINIAPP_SECRET=5d32b184c9c25ec542ea24a1c5255180

# Python AI
MOOD_AI_PY_ENABLED=true
MOOD_AI_PY_BASE_URL=http://ai:9011
MOOD_AI_PY_ANALYZE_PATH=/v1/mood/analyze
MOOD_AI_PY_TOKEN=CHANGE_ME_INTERNAL_TOKEN
MOOD_AI_PY_TIMEOUT_MS=8000
MOOD_AI_PY_FALLBACK_TO_MOCK=true
```

**7.3** **`/opt/mss/ai/ai.env`**

```env
# AI service internal auth
AI_API_TOKEN=CHANGE_ME_INTERNAL_TOKEN

# Bailian model
AI_USE_BAILIAN=true
AI_FALLBACK_TO_RULE=true
AI_MODEL_NAME=qwen-plus
AI_MODEL_VERSION=latest
AI_PROMPT_VERSION=p1-bailian-json

# Alibaba Cloud Bailian (DashScope OpenAI-compatible)
DASHSCOPE_API_KEY=CHANGE_ME_DASHSCOPE_API_KEY
DASHSCOPE_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
```

说明：

- `MOOD_AI_PY_TOKEN` 与 `AI_API_TOKEN` 必须一致
- `DASHSCOPE_API_KEY` 必填
- 这些环境变量会覆盖后端与 AI 服务默认配置

***

**8. 编写 docker-compose**
创建 `/opt/mss/docker-compose.yml`：

```yaml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    container_name: mss-mysql
    restart: always
    env_file:
      - /opt/mss/.env
    environment:
      TZ: ${TZ}
    ports:
      - "3306:3306"
    volumes:
      - /opt/mss/mysql:/var/lib/mysql

  redis:
    image: redis:7
    container_name: mss-redis
    restart: always
    env_file:
      - /opt/mss/.env
    command: ["redis-server","--appendonly","yes","--requirepass","${REDIS_PASSWORD}"]
    ports:
      - "6379:6379"
    volumes:
      - /opt/mss/redis:/data

  ai:
    build:
      context: /opt/mss/ai
      dockerfile: Dockerfile
    container_name: mss-ai
    restart: always
    env_file:
      - /opt/mss/ai/ai.env
    expose:
      - "9011"

  backend:
    image: eclipse-temurin:17-jre
    container_name: mss-backend
    restart: always
    working_dir: /app
    env_file:
      - /opt/mss/backend/backend.env
    volumes:
      - /opt/mss/backend:/app
      - /data/moodsphere/upload:/data/moodsphere/upload
    command: ["java","-jar","/app/moodsphere-admin.jar"]
    depends_on:
      - mysql
      - redis
      - ai

  nginx:
    image: nginx:stable
    container_name: mss-nginx
    restart: always
    env_file:
      - /opt/mss/.env
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /opt/mss/frontend:/usr/share/nginx/html
      - /opt/mss/nginx/default.conf:/etc/nginx/conf.d/default.conf
      - /opt/mss/nginx/certbot:/var/www/certbot
      - /etc/letsencrypt:/etc/letsencrypt
    depends_on:
      - backend
```

> 如果宿主机已有 MySQL/Redis，请删除 `3306:3306`、`6379:6379` 端口映射，避免冲突。

***

**9. 配置 Nginx**
创建 `/opt/mss/nginx/default.conf`：

```nginx
server {
    listen 80;
    server_name lingdone.cn;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl;
    server_name lingdone.cn;

    ssl_certificate /etc/letsencrypt/live/lingdone.cn/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/lingdone.cn/privkey.pem;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

***

**10. 申请 HTTPS 证书**
Standalone 模式：

```bash
docker run --rm \
  -p 80:80 \
  -v /etc/letsencrypt:/etc/letsencrypt \
  certbot/certbot certonly --standalone \
  -d lingdone.cn \
  --email your-email@example.com \
  --agree-tos \
  --non-interactive
```

***

**11. 启动全部服务**

```bash
docker compose -f /opt/mss/docker-compose.yml up -d --build
```

检查容器状态：

```bash
docker ps
```

***

**12. 初始化数据库**
把 SQL 上传到服务器，例如 `/opt/mss/sql/`，再导入：

```bash
docker exec -i mss-mysql mysql -uroot -pCHANGE_ME_ROOT_PASSWORD moodsphere < /opt/mss/sql/init.sql
```

***

**13. 验证**

- 访问 `https://lingdone.cn`
- 浏览器控制台查看 API 是否走 `/prod-api/*`
- 查看后端日志：

```bash
docker logs -f mss-backend
```

- 查看 AI 日志：

```bash
docker logs -f mss-ai
```

- 验证 AI 容器健康：

```bash
docker exec mss-ai python -c "import urllib.request;print(urllib.request.urlopen('http://127.0.0.1:9011/health').read().decode())"
```

***

**14. 常用运维命令**

- 日常迭代发布（只更新后端/前端/AI、回滚、env 生效方式）：[ops-iterate.md](file:///d:/aZLY/A-MSS/02-code/document/deploy/ops-iterate.md)

- 重启全部：

```bash
docker compose -f /opt/mss/docker-compose.yml restart
```

- 只重启后端：

```bash
docker restart mss-backend
```

- 只重启 AI：

```bash
docker restart mss-ai
```

- 查看 Nginx 配置是否生效：

```bash
docker exec -it mss-nginx nginx -t
```

***

**15. 证书续期**
Let’s Encrypt 默认 90 天，需要定期续期。手动续期示例：

```bash
docker run --rm \
  -v /opt/mss/nginx/certbot:/var/www/certbot \
  -v /etc/letsencrypt:/etc/letsencrypt \
  certbot/certbot renew
```

续期后重载 Nginx：

```bash
docker exec -it mss-nginx nginx -s reload
```

***

**16. 安全建议**

- 修改 `.env`、`backend.env`、`ai.env` 的默认密码与密钥
- 数据库、Redis、AI 端口不要暴露公网
- `DASHSCOPE_API_KEY` 只放服务器环境变量文件，不要写入代码仓库
- Druid 控制台不建议暴露公网，如需开放务必改密码或关闭

***

如果你要，我可以继续补一份 `deploy.sh`，把创建目录、写 env、启动 compose 一次跑完。
