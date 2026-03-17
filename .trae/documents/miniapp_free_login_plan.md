# 个人免费小程序一键登录完善计划（MSS）

## 目标

* 实现“`code -> openid -> 自定义 token`”的免费登录闭环。

* 保持个人主体可用，不引入企业资质依赖，不接入付费能力。

* 可选接入用户信息授权（头像、昵称）用于体验增强，并符合微信授权规范。

## 范围与约束

* 仅使用小程序 `APPID`、`SECRET`、`js_code` 换取 `openid/session_key`。

* 登录态由后端签发业务 token（现有 JWT 体系）维护。

* 不实现手机号一键登录（该能力依赖额外能力和合规流程，不在本次范围）。

* 前端授权文案与交互遵守“最小必要、可拒绝、可撤回”的原则。

* 基于你已新增的两张业务表落地登录与用户扩展信息：

  * `biz_user_auth`：第三方登录绑定（`auth_type/openid/unionid/session_key/last_login_*`）。

  * `biz_user_info`：用户扩展信息（昵称、头像、性别、情绪统计、匿名投影等）。

## 实施步骤

### 1. 后端登录链路改造（核心）

* 在 `system-user` 模块梳理并固化小程序登录入口：`POST /app/auth/wechat/miniapp/login`。

* 服务层改为真实调用微信 `jscode2session`：

  * 输入：前端 `code`。

  * 输出：`openid`、`session_key`、`unionid(可选)`。

* 增加参数与返回校验：

  * `code` 为空直接返回业务错误。

  * 微信返回 `errcode/errmsg` 时转为统一错误码与可读提示。

* 建立“`openid -> 平台用户`”映射策略（明确落到两表）：

  * 先按 `biz_user_auth(auth_type='wechat_mp', openid)` 查询绑定关系。

  * 若存在：取 `user_id`，更新 `session_key`、`last_login_time`、`last_login_ip`。

  * 若不存在：

    * 创建 `sys_user` 基础账号（默认用户名规则与默认角色）。

    * 写入 `biz_user_auth` 绑定记录（含 `openid/unionid/session_key`）。

    * 初始化 `biz_user_info`（`nick_name/avatar/gender/emotion_level/anonymous_projection` 等默认值）。

* 接入现有 `TokenService` 生成业务 token，并返回标准登录结果（token、用户摘要、权限）。

### 2. 数据库与持久层落地（新增）

* 使用你提供的 DDL 作为基线，确认索引与约束有效：

  * `biz_user_auth` 唯一键 `uk_auth_type_openid(auth_type, openid)`。

  * `biz_user_info` 唯一键 `uk_user_id(user_id)`。

* 新增 MyBatis 实体、Mapper、Service：

  * `BizUserAuth`：负责三方身份绑定与登录态字段更新。

  * `BizUserInfo`：负责用户扩展资料初始化与更新。

* 事务边界：

  * “首次登录建号”流程（`sys_user + biz_user_auth + biz_user_info`）使用单事务提交。

  * 任一环节失败统一回滚，避免孤儿数据。

### 3. 后端配置与安全加固

* 在后端配置中新增或确认微信配置项：

  * `wechat.miniapp.appid`

  * `wechat.miniapp.secret`

  * 超时与重试配置（如 connect/read timeout、重试次数）。

* 确保 `secret` 不入库、不打印日志、不回传前端。

* 对外接口保持匿名访问仅限登录端点；用户信息端点继续走 JWT 鉴权。

* 增加接口限流/防刷策略（按 IP 或设备标识）以避免 code 接口滥用。

* `biz_user_auth.session_key` 建议加密或脱敏存储，禁止明文在日志输出。

### 4. 前端登录页与调用链对齐

* 统一登录入口为 `pages/auth/login`，仅在微信小程序环境触发 `uni.login`。

* 提交 `code` 至后端登录接口，成功后写入 token 并拉取用户信息。

* 保持现有路由守卫：

  * 未登录访问受限页跳转登录页。

  * 已登录访问登录页回跳首页（天气页）。

* 失败态分层提示：

  * 网络错误、微信授权失败、后端业务错误分别提示。

### 5. 可选用户信息授权（体验增强）

* 登录成功后提供“完善资料”入口（非强制）：

  * 通过微信规范能力获取用户公开信息（按当前平台规范选用合规 API）。

  * 用户拒绝时不影响主流程登录与核心功能使用。

* 后端增加“更新资料”接口，仅更新 `biz_user_info` 允许字段（昵称、头像、性别、签名等）。

* 前端增加授权状态缓存与可重试入口，避免频繁弹授权。

### 6. 数据模型与可观测性

* `biz_user_auth` 重点字段维护策略：

  * 登录时更新：`session_key`、`last_login_time`、`last_login_ip`、`update_time`。

  * 软删与状态位：`auth_status`、`del_flag` 参与查询条件，避免失效绑定被误用。

* `biz_user_info` 重点字段维护策略：

  * 初始化：`emotion_level=1`、`record_days=0`、`total_records=0`、`anonymous_projection=1`。

  * 业务更新：记录模块完成后更新 `record_days/total_records/last_record_time`。

* 增加登录审计日志：

  * 登录结果、失败原因分类、耗时、客户端类型。

* 指标埋点：

  * 登录成功率、微信接口失败率、token 续期命中率。

### 7. 联调与验收

* 联调场景：

  * 首次登录自动建号。

  * 二次登录复用账号。

  * 无效/过期 `code` 的错误处理。

  * token 过期后自动回登录页。

  * 校验两表数据一致性（`biz_user_auth.user_id` 与 `biz_user_info.user_id` 均可关联到 `sys_user.user_id`）。

* 验收标准：

  * 小程序端可在无企业资质前提下完成登录。

  * 登录成功后可稳定访问鉴权接口。

  * 用户拒绝资料授权不影响进入主流程。

  * 关键错误有可读提示，后端日志可定位问题。

## 交付顺序

1. 先完成两表持久层与事务建号链路（`sys_user + biz_user_auth + biz_user_info`）。
2. 后端 `code -> openid -> token` 打通并可用。
3. 前端登录调用链与路由守卫完成适配。
4. 可选资料授权能力上线（默认关闭可灰度）。
5. 完成联调、错误回归与验收清单确认。

## 风险与回退

* 风险：微信接口波动、`secret` 配置错误、授权策略变动。

* 回退方案：

  * 保留当前 mock 登录开关用于联调兜底。

  * 新链路按配置开关启用，可快速切回旧逻辑。

