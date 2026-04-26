**完整部署流程（Docker + 域名 + HTTPS + 环境变量文件）**

**0. 你将得到的结果**
- 访问 `https://lingdone.cn` 打开管理端前端
- API 走 `https://lingdone.cn/prod-api/*`，由 Nginx 反代到后端容器
- MySQL、Redis、后端、Nginx 都在 Docker 容器中运行
- 配置集中在 `/opt/mss/.env` 与 `/opt/mss/backend/backend.env`

---

**1. DNS 与服务器前置准备**
- DNS 解析：在域名控制台把 `lingdone.cn` 的 A 记录指向服务器公网 IP
- 服务器系统推荐 Ubuntu 22.04 或 CentOS 7/8
- 防火墙只开放端口：
  - `80`、`443` 对外开放
  - `22`（SSH）
  - `3306`、`6379` 不建议对公网开放

---

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

---

**3. 目录结构规划**
```bash
sudo mkdir -p /opt/mss/backend
sudo mkdir -p /opt/mss/frontend
sudo mkdir -p /opt/mss/nginx/certbot
sudo mkdir -p /opt/mss/mysql
sudo mkdir -p /opt/mss/redis
sudo mkdir -p /data/moodsphere/upload
```

---

**4. 后端打包与上传**
在本地 `MSS-backend` 根目录执行：
```bash
mvn clean package '-Dmaven.test.skip=true'
```

上传 `moodsphere-admin.jar` 到服务器：
```
/opt/mss/backend/moodsphere-admin.jar
```

---

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

---

**6. 生成环境变量文件**
**6.1 `/opt/mss/.env`**
```env
# Domain
DOMAIN=lingdone.cn
TZ=Asia/Shanghai

# MySQL
MYSQL_ROOT_PASSWORD=G8mVv5u3Qz9Hk2pT
MYSQL_DATABASE=moodsphere

# Redis
REDIS_PASSWORD=R3d1sP@ssw0rdY7
```

**6.2 `/opt/mss/backend/backend.env`**
```env
SPRING_PROFILES_ACTIVE=druid

# MySQL (Druid)
SPRING_DATASOURCE_DRUID_MASTER_URL=jdbc:mysql://mysql:3306/moodsphere?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
SPRING_DATASOURCE_DRUID_MASTER_USERNAME=root
SPRING_DATASOURCE_DRUID_MASTER_PASSWORD=G8mVv5u3Qz9Hk2pT

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=R3d1sP@ssw0rdY7
SPRING_DATA_REDIS_DATABASE=0

# JWT
TOKEN_SECRET=9XxkT2aV6nQp8cL4rZ0sH1mF5eJ7uB3

# Upload path
RUOYI_PROFILE=/data/moodsphere/upload
```

说明：
- 这些环境变量会覆盖后端配置文件中的默认值
- `TOKEN_SECRET` 与密码建议上线后再手动替换成你自己的

---

**7. 编写 docker-compose**
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

---

**8. 配置 Nginx**
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

---

**9. 申请 HTTPS 证书**
Standalone 模式
```bash
docker run --rm \
  -p 80:80 \
  -v /etc/letsencrypt:/etc/letsencrypt \
  certbot/certbot certonly --standalone \
  -d lingdone.cn \
  --email 1513503118@qq.com \
  --agree-tos \
  --non-interactive
```

---

**10. 启动全部服务**
```bash
docker compose -f /opt/mss/docker-compose.yml up -d
```

检查容器状态：
```bash
docker ps
```

---

**11. 初始化数据库**
把 SQL 上传到服务器，例如 `/opt/mss/sql/`，再导入：

```bash
docker exec -i mss-mysql mysql -uroot -pG8mVv5u3Qz9Hk2pT moodsphere < /opt/mss/sql/init.sql
```

---

**12. 验证**
- 访问 `https://lingdone.cn`
- 浏览器控制台查看 API 是否走 `/prod-api/*`
- 查看后端日志：
```bash
docker logs -f mss-backend
```

---

**13. 常用运维命令**
- 重启：
```bash
docker compose -f /opt/mss/docker-compose.yml restart
```
- 只重启后端：
```bash
docker restart mss-backend
```
- 查看 Nginx 配置是否生效：
```bash
docker exec -it mss-nginx nginx -t
```

---

**14. 证书续期**
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

---

**15. 安全建议**
- 修改 `.env` 和 `backend.env` 的默认密码与密钥
- 数据库和 Redis 不要暴露公网
- Druid 控制台不建议暴露公网，如需开放务必改密码或关闭

---

如果你需要我把以上内容**直接生成成脚本文件**（如 `init.sh`、`deploy.sh`），或想让我把证书自动续期写成 cron，我可以继续补齐。