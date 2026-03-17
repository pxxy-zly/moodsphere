# 小程序默认角色与 PC 端隔离改造计划（MSS）

## 目标
- 将小程序一键登录首次建号的默认角色从 `common` 调整为 `app_user`（`role_id=100`）。
- 保证 `app_user` 账号不能通过 PC 端账号密码登录。
- 保持现有若依登录体系可用，不影响 `admin/common/ops_manager` 等后台账号登录。

## 现状结论（基于当前代码）
- 小程序首次登录时在 `insertDefaultRole` 中固定使用 `DEFAULT_ROLE_ID = 2L`，并校验 `role_key=common` 后写入用户角色关系。
- PC 端登录走 `SysLoginService -> UserDetailsServiceImpl`，目前仅校验用户存在、状态、密码，不区分角色来源。

## 实施步骤

### 1. 明确并固化小程序默认角色策略
- 将 `WechatMiniappAuthServiceImpl` 中默认角色常量改为：
  - `DEFAULT_ROLE_ID = 100L`
  - `DEFAULT_ROLE_KEY_APP_USER = "app_user"`
- 调整 `insertDefaultRole`：
  - 按 `role_id=100` 查询角色；
  - 校验 `status='0'` 且 `role_key='app_user'`；
  - 满足条件时调用 `sysUserService.insertUserAuth(userId, new Long[]{100L})`。
- 增加失败处理：
  - 若 `role_id=100` 不存在或被停用，抛业务异常（不要静默跳过），避免创建“无角色”小程序账号。

### 2. 明确首次建号字段来源与约束
- 保持首次建号流程不变（`sys_user + biz_user_auth + biz_user_info` 单事务）。
- 仅在角色初始化阶段切换到 `app_user`，其余字段（昵称、头像、匿名投影初值等）保持现有逻辑。
- 对 `firstLogin` 返回值维持原有语义，避免影响移动端现有跳转。

### 3. 增加 PC 端登录拦截（角色隔离）
- 在 `UserDetailsServiceImpl.loadUserByUsername` 的用户状态校验后、密码校验前新增角色拦截逻辑：
  - 查询该用户角色权限集合（`ISysRoleService.selectRolePermissionByUserId(userId)`）；
  - 若仅包含 `app_user` 或包含 `app_user` 且不允许后台登录，则抛出 `ServiceException("移动端账号不允许登录管理后台")`。
- 记录登录失败审计信息（沿用若依异步登录日志机制），确保运维可追踪。

### 4. 防止误伤后台账号的判定规则
- 判定优先采用“角色键包含 `app_user` 且不含后台白名单角色”：
  - 后台白名单可先设为：`admin/common/ops_manager/ai_strategist/safety_officer/data_analyst`。
- 仅当账号判定为“纯移动端身份”时拦截 PC 登录，避免混合账号被误挡。

### 5. 配置化增强（可选但建议）
- 新增可配置项（默认开启）：
  - `security.pc-login.block-app-user: true`
  - `security.pc-login.admin-role-keys: admin,common,ops_manager,ai_strategist,safety_officer,data_analyst`
- 通过配置控制隔离策略，便于后续角色模型调整而不改代码。

### 6. 验证方案
- 编译验证：
  - `mvn -pl moodsphere-biz/system-user,moodsphere-framework,moodsphere-admin -am -DskipTests compile`
- 功能验证用例：
  1. 小程序首次登录：应分配 `role_id=100(role_key=app_user)`；
  2. 小程序二次登录：应复用账号并更新 `biz_user_auth` 登录信息；
  3. 使用 `app_user` 账号走 `/login`：应被拒绝并返回明确提示；
  4. 使用 `common/admin` 账号走 `/login`：应可正常登录；
  5. 登录日志中应有拦截记录，便于审计。

### 7. 数据与上线注意事项
- 上线前确认 `sys_role` 中 `role_id=100, role_key=app_user, status='0'` 存在且有效。
- 对历史小程序账号进行一次排查：
  - 若存在已绑定 `common` 的小程序用户，可按策略迁移到 `app_user`。
- 回滚策略：
  - 仅回滚“PC 拦截逻辑”和默认角色常量，不影响已创建的 `biz_user_auth/biz_user_info` 数据。

## 交付结果定义
- 小程序新注册用户默认角色为 `app_user`。
- `app_user` 账号无法登录 PC 管理端。
- 后台角色账号不受影响，系统可正常编译与运行。
