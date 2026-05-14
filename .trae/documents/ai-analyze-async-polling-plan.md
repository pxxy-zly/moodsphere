# AI 情绪分析异步任务 + 轮询重构方案

## Summary
- 目标：把当前 `SpringBoot -> FastAPI` 的同步阻塞分析链路，重构为 `SpringBoot 主控的异步任务 + 前端轮询`。
- 主决策：
  - `SpringBoot` 负责任务创建、状态持久化、异步调度、结果落库和对外查询。
  - `FastAPI` 保持“单次分析执行器”角色，不维护任务状态，不新增异步任务中心。
  - 对外接口采用“两套并存”：
    - 保留兼容接口：`POST /app/mood/analyze/run`、`GET /app/mood/analyze/result/{recordId}`
    - 新增标准任务接口：`POST /app/mood/analyze/tasks`、`GET /app/mood/analyze/tasks/{taskId}`
- 技术路线：首版不引入 MQ；用现有 Spring 线程池 + MySQL 任务表 + Redis 分布式锁/幂等保护实现。

## Current State Analysis

### Java 侧现状
- [MoodAiAnalyzeController.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/controller/MoodAiAnalyzeController.java) 只有两个接口：
  - `/run` 直接触发分析并返回结果
  - `/result/{recordId}` 读取记录表与结果表
- [IMoodAiAnalyzeService.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/service/IMoodAiAnalyzeService.java) 只有 `runAnalyze()` 和 `getAnalyzeResult()` 两个同步语义方法。
- [MoodAiAnalyzeServiceImpl.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/service/impl/MoodAiAnalyzeServiceImpl.java) 在一个事务里完成：
  - 校验记录归属与文本内容
  - 调用 [PythonMoodAnalyzeClient.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/service/client/PythonMoodAnalyzeClient.java)
  - 将结果 upsert 到 `biz_ai_analysis_result`
  - 更新 `biz_mood_record.analyze_status/risk_level`
- 当前 `analyze_status` 仅表示最终分析状态：`0 待分析 / 1 成功 / 2 失败`，没有“任务排队/执行中”层面的状态表达。
- [BizAiAnalysisResultMapper.xml](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/resources/mapper/analyze/BizAiAnalysisResultMapper.xml) 仅按 `record_id` 维护一份最新结果，不保存任务历史。
- [BizMoodRecordMapper.xml](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/record/src/main/resources/mapper/record/BizMoodRecordMapper.xml) 的 `updateAnalyzeResult` 只能更新最终状态与风险级别，不适合表达任务生命周期。

### Python 侧现状
- [main.py](file:///d:/aZLY/A-MSS/02-code/MSS-backend/py-ai-service/app/main.py) 暴露：
  - `GET /health`
  - `POST /v1/mood/analyze`
- [schemas.py](file:///d:/aZLY/A-MSS/02-code/MSS-backend/py-ai-service/app/schemas.py) 定义的是“单次请求 -> 单次响应”模型，没有任务 ID / 状态查询模型。
- Python 服务会在单个请求中完成：
  - 多模态素材规整
  - 调用 Bailian / 回退规则引擎
  - 返回完整分析结果
- Python 服务本身适合继续作为同步执行器，不适合成为跨服务任务中心。

### 可复用基础设施
- [ThreadPoolConfig.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-framework/src/main/java/com/moodsphere/framework/config/ThreadPoolConfig.java) 已提供 `threadPoolTaskExecutor`。
- [RedisCache.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-common/src/main/java/com/moodsphere/common/core/redis/RedisCache.java) 可用于任务锁与短期状态缓存。
- 仓库已有 SQL 目录 `MSS-backend/sql/`，适合新增本次表结构脚本。

### 当前实现的核心问题
- 同步阻塞：`/run` 依赖 Python 与大模型耗时，接口响应时间不可控。
- 事务过大：外部 HTTP 调用和数据库写入耦合在同一业务动作里，失败边界不清晰。
- 无任务历史：无法区分“排队中/执行中/失败可重试/最终成功”。
- 无重复提交治理：同一 `recordId` 可被重复触发，当前没有活动任务幂等控制。
- 无崩溃恢复：服务重启或线程中断后，没有中间任务的恢复/补偿机制。

## Proposed Changes

### 1. 数据模型与状态机

#### 1.1 新增任务表
- 新增 SQL 脚本：`d:\aZLY\A-MSS\02-code\MSS-backend\sql\ry_20260512_ai_analyze_task.sql`
- 新增表：`biz_ai_analyze_task`
- 建议字段：
  - `id` bigint 主键
  - `task_no` varchar(32) 任务号，唯一
  - `record_id` bigint，关联心情记录
  - `user_id` bigint，冗余保存任务归属
  - `task_status` int，`0 QUEUED / 1 RUNNING / 2 SUCCESS / 3 FAIL`
  - `fail_code` varchar(64)，失败分类，如 `PYTHON_TIMEOUT`、`LLM_ERROR`、`SYSTEM_ERROR`
  - `fail_message` varchar(500)，用户可读或运维可读错误
  - `retry_count` int，首版仅记录，默认不自动重试
  - `request_snapshot` longtext，提交时的请求快照
  - `response_snapshot` longtext，成功时的结果快照
  - `provider` / `model_name` / `model_version` / `prompt_version`
  - `request_id` varchar(64)，透传 Python 请求号
  - `analysis_cost_ms` int
  - `queued_at` / `started_at` / `finished_at`
  - `create_by` / `create_time` / `update_by` / `update_time` / `del_flag`
- 索引建议：
  - 唯一索引：`uk_task_no(task_no)`
  - 普通索引：`idx_record_user(record_id, user_id)`
  - 普通索引：`idx_status_create(task_status, create_time)`

#### 1.2 状态映射规则
- 任务状态 `task_status` 负责轮询语义：
  - `QUEUED`
  - `RUNNING`
  - `SUCCESS`
  - `FAIL`
- 记录状态 `biz_mood_record.analyze_status` 继续兼容旧前端：
  - `QUEUED/RUNNING -> 0`
  - `SUCCESS -> 1`
  - `FAIL -> 2`
- 兼容接口返回时，优先以“最新任务状态”为准；只有最新任务为 `SUCCESS` 时才返回 `biz_ai_analysis_result` 结果体，避免旧成功结果污染新的任务轮询。

#### 1.3 幂等策略
- 同一 `recordId + userId` 在存在 `QUEUED/RUNNING` 任务时，提交接口不新建任务，直接返回当前活动任务。
- 不做取消接口；用户再次发起分析时，只有在上一任务 `SUCCESS/FAIL` 后才允许新建任务。

### 2. Java 侧改造

#### 2.1 新增领域对象、Mapper、VO
- 在 `ai-analyze` 模块新增以下文件：
  - `src/main/java/com/moodsphere/analyze/domain/entity/BizAiAnalyzeTask.java`
  - `src/main/java/com/moodsphere/analyze/mapper/BizAiAnalyzeTaskMapper.java`
  - `src/main/resources/mapper/analyze/BizAiAnalyzeTaskMapper.xml`
  - `src/main/java/com/moodsphere/analyze/domain/vo/MoodAnalyzeTaskVo.java`
  - `src/main/java/com/moodsphere/analyze/domain/vo/MoodAnalyzeSubmitVo.java`
- `MoodAnalyzeTaskVo` 字段固定为：
  - `taskId`
  - `taskNo`
  - `recordId`
  - `taskStatus`
  - `analyzeStatus`
  - `pollIntervalMs`，固定返回 `2000`
  - `result`
  - `errorCode`
  - `errorMessage`
  - `requestId`
  - `provider`
  - `modelName`
  - `modelVersion`
  - `promptVersion`
  - `analysisCostMs`
  - `queuedAt`
  - `startedAt`
  - `finishedAt`
- `MoodAnalyzeSubmitVo` 直接复用上述字段结构，不再单独设计不同响应格式，避免兼容接口与标准接口分叉。

#### 2.2 服务接口重构
- 修改 [IMoodAiAnalyzeService.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/service/IMoodAiAnalyzeService.java)：
  - `MoodAnalyzeTaskVo submitAnalyzeTask(Long recordId);`
  - `MoodAnalyzeTaskVo getAnalyzeTaskByTaskId(Long taskId);`
  - `MoodAnalyzeTaskVo getAnalyzeTaskByRecordId(Long recordId);`
- 保留旧方法名会误导调用方继续认为是同步执行，因此不建议继续保留 `runAnalyze()` 作为服务层主入口。

#### 2.3 服务实现拆分
- 重构 [MoodAiAnalyzeServiceImpl.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/service/impl/MoodAiAnalyzeServiceImpl.java) 为三段职责：
  - `submitAnalyzeTask()`：校验记录、查活动任务、创建任务、把记录状态置为 `0`、提交异步执行
  - `executeAnalyzeTask(taskId)`：后台线程执行真实 Python 调用与结果落库
  - `buildTaskVo()`：统一拼装轮询响应
- 关键实现规则：
  - 提交时把当前 `record` + `mediaAssets` 请求快照序列化保存到 `request_snapshot`
  - 后台线程先获取 Redis 锁：`mood:analyze:task:lock:{taskId}`，TTL = `python timeout + 60s`
  - 获取锁后，通过 Mapper 原子更新 `QUEUED -> RUNNING`；若更新行数为 0，则直接退出，防止重复执行
  - Python 成功后：
    - upsert `biz_ai_analysis_result`
    - 更新 `biz_ai_analyze_task` 为 `SUCCESS`
    - 更新 `biz_mood_record.analyze_status = 1` 与最新 `risk_level`
  - Python 失败后：
    - 更新 `biz_ai_analyze_task` 为 `FAIL`
    - 更新 `biz_mood_record.analyze_status = 2`
    - 不覆盖旧 `biz_ai_analysis_result` 内容，只在查询层屏蔽旧结果
- 事务边界：
  - 提交任务与更新记录状态使用一个短事务
  - 后台执行结果落库使用独立事务
  - 不允许外部 HTTP 调用与提交事务绑定在一起

#### 2.4 异步执行器
- 新增文件：
  - `src/main/java/com/moodsphere/analyze/service/AnalyzeTaskDispatcher.java`
  - `src/main/java/com/moodsphere/analyze/config/AiAnalyzeTaskProperties.java`
  - `src/main/java/com/moodsphere/analyze/init/AiAnalyzeTaskRecoveryRunner.java`
- `AnalyzeTaskDispatcher` 注入 `threadPoolTaskExecutor`，统一负责 `taskId` 异步投递。
- `AiAnalyzeTaskProperties` 新增配置项：
  - `queuePollIntervalMs=2000`
  - `taskLockSeconds=120`
  - `staleRunningSeconds=180`
  - `startupRecoveryBatchSize=100`
- `AiAnalyzeTaskRecoveryRunner` 在 SpringBoot 启动后执行一次：
  - 将超时的 `RUNNING` 任务置为 `FAIL`，错误码 `SYSTEM_RESTARTED`
  - 将遗留的 `QUEUED` 任务重新提交到线程池
- 首版不做定时补偿任务；启动恢复足够覆盖本轮重构目标。

#### 2.5 Controller 改造
- 修改 [MoodAiAnalyzeController.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/controller/MoodAiAnalyzeController.java)：
  - `POST /run` 调用 `submitAnalyzeTask(recordId)`，返回 `taskId/taskStatus/pollIntervalMs`
  - `GET /result/{recordId}` 调用 `getAnalyzeTaskByRecordId(recordId)`，返回最新任务状态和结果
- 新增标准任务控制器：
  - `src/main/java/com/moodsphere/analyze/controller/MoodAiAnalyzeTaskController.java`
- 新接口固定为：
  - `POST /app/mood/analyze/tasks`
  - `GET /app/mood/analyze/tasks/{taskId}`
- 两套接口返回体完全一致，减少前端与后端维护成本。

#### 2.6 Mapper 调整
- 修改 [BizAiAnalysisResultMapper.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/mapper/BizAiAnalysisResultMapper.java) 与 [BizAiAnalysisResultMapper.xml](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/resources/mapper/analyze/BizAiAnalysisResultMapper.xml)：
  - 保留 `selectByRecordId`
  - 可选新增 `selectByRecordIdForUpdate`，仅在需要串行覆盖结果时使用
- 修改 [BizMoodRecordMapper.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/record/src/main/java/com/moodsphere/record/mapper/BizMoodRecordMapper.java) 与 [BizMoodRecordMapper.xml](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/record/src/main/resources/mapper/record/BizMoodRecordMapper.xml)：
  - 保留 `updateAnalyzeResult`
  - 新增 `markAnalyzeQueued(id, updateBy, updateTime)`
  - 新增 `selectByIdAndUserId` 的结果复用，不再改签名

### 3. Python 侧改造

#### 3.1 保持同步执行器定位
- 保持 [main.py](file:///d:/aZLY/A-MSS/02-code/MSS-backend/py-ai-service/app/main.py) 的 `/v1/mood/analyze` 不变，不新增 Python 任务 API。
- 保持 [schemas.py](file:///d:/aZLY/A-MSS/02-code/MSS-backend/py-ai-service/app/schemas.py) 的输入输出主体不变，避免双边同时大改。

#### 3.2 仅做稳定性增强
- 在 `app/main.py` 中补强以下点：
  - 所有异常统一映射为明确的 HTTP 4xx/5xx，便于 Java 侧做 `fail_code` 分类
  - 日志固定打印 `recordId`、`traceId/requestId`、模型、媒体数量、耗时
  - 当 Bailian 失败且走规则回退时，`rawResponse.fallbackReason` 保持稳定字段名，供 Java 透传到任务结果
- Python 不需要持久化任务状态，也不需要轮询接口。

### 4. 接口契约

#### 4.1 提交任务
- 兼容接口：`POST /app/mood/analyze/run`
- 标准接口：`POST /app/mood/analyze/tasks`
- 请求体继续复用 [MoodAnalyzeRunBody.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/domain/dto/MoodAnalyzeRunBody.java)

#### 4.2 查询任务
- 兼容接口：`GET /app/mood/analyze/result/{recordId}`
- 标准接口：`GET /app/mood/analyze/tasks/{taskId}`

#### 4.3 统一响应示例
```json
{
  "taskId": 101,
  "taskNo": "AAT202605120001",
  "recordId": 88,
  "taskStatus": "RUNNING",
  "analyzeStatus": 0,
  "pollIntervalMs": 2000,
  "result": null,
  "errorCode": null,
  "errorMessage": null,
  "requestId": null,
  "provider": null,
  "modelName": null,
  "modelVersion": null,
  "promptVersion": null,
  "analysisCostMs": null,
  "queuedAt": "2026-05-12T10:00:00",
  "startedAt": "2026-05-12T10:00:01",
  "finishedAt": null
}
```

### 5. 迁移与兼容策略
- 第 1 步：先上线任务表、Mapper、服务层、标准任务接口。
- 第 2 步：把旧 `/run` 改为“提交即返回”，把旧 `/result/{recordId}` 改为“查询最新任务”。
- 第 3 步：前端优先兼容旧接口；新页面或新小程序版本可直接切标准任务接口。
- 第 4 步：待全部客户端迁移稳定后，再评估是否下线旧接口。

## Assumptions & Decisions
- 数据库为 MySQL，允许在 `MSS-backend/sql/` 增加新建表脚本。
- SpringBoot 仍是统一业务入口，Python 服务不直接暴露给前端。
- 首版目标是“异步化 + 轮询 + 可恢复”，不是“分布式任务平台”；因此不引入 MQ。
- 任务真源在 MySQL，不把 Redis 作为唯一状态存储；Redis 只用于执行锁与防重。
- 同一记录仅允许一个活动任务，避免并发分析导致结果互相覆盖。
- 最新任务未成功时，不返回旧分析结果，防止前端误把陈旧结果当作本次任务结果。
- Python 调用超时时间继续复用现有 [PythonAiAnalyzeProperties.java](file:///d:/aZLY/A-MSS/02-code/MSS-backend/moodsphere-biz/ai-analyze/src/main/java/com/moodsphere/analyze/config/PythonAiAnalyzeProperties.java) 的 `timeoutMs`，任务锁与恢复窗口基于该值派生。

## Verification Steps
- 数据库验证：
  - 执行新 SQL，确认 `biz_ai_analyze_task` 建表、索引、生效。
- 提交流程验证：
  - 调用 `/app/mood/analyze/run`，确认接口快速返回 `taskId`，不再等待 Python 完成。
  - 重复提交同一 `recordId`，确认返回同一个活动任务而不是创建重复任务。
- 轮询验证：
  - 在 Python 正常时，轮询 `/result/{recordId}` 或 `/tasks/{taskId}`，状态流转应为 `QUEUED -> RUNNING -> SUCCESS`。
  - 在 Python 超时/报错时，状态应为 `QUEUED -> RUNNING -> FAIL`，并带 `errorCode/errorMessage`。
- 落库验证：
  - 成功后 `biz_ai_analysis_result` 被更新，`biz_mood_record.analyze_status=1`
  - 失败后 `biz_mood_record.analyze_status=2`，旧结果不被删除
- 恢复验证：
  - 提交任务后重启 SpringBoot，确认遗留 `QUEUED` 任务被重新投递，超时 `RUNNING` 任务被标记失败
- 回归验证：
  - `py-ai-service` 的 `/v1/mood/analyze` 保持原有同步可用
  - 旧前端只改轮询逻辑即可继续使用 `/run + /result/{recordId}`
