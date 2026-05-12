# 移动端 AI 洞察工程化开发路线

## 1. 文档目标

本文档用于指导 MoodSphere 移动端后续开发，重点围绕五个 TabBar 页面中的 `洞察` 能力升级，采用工程化、可落地、当前主流的技术方案进行分阶段实现。

移动端五个 TabBar：

* 心境
* 星球
* 记录
* 洞察
* 我的

当前优先级以移动端体验为主，后端和 AI 服务作为移动端能力支撑。

---

## 2. 技术选型总览

### 2.1 移动端

* 框架：UniApp + Vue
* 状态管理：当前继续使用 Vuex；若后续升级 Vue3，推荐 Pinia
* 请求层：统一封装 `utils/request.js`
* 上传能力：`uni.uploadFile`
* 图表：优先使用 uCharts；复杂场景可评估 ECharts for UniApp
* 本地缓存：`uni.setStorageSync` / `uni.getStorageSync`
* UI 组织：页面组件化拆分，避免所有逻辑堆在单个 `.vue` 页面中

### 2.2 后端业务服务

* 框架：Spring Boot 3
* 管理基础：若依二开
* ORM：MyBatis
* 主数据库：MySQL
* 缓存与状态：Redis
* 定时任务：Quartz
* 接口文档：SpringDoc OpenAPI / Swagger

### 2.3 AI 服务

* 服务框架：FastAPI
* 模型调用：OpenAI-compatible SDK
* 模型供应商：优先接入通义千问 / 阿里云百炼
* 多模态能力：文本、图片、语音统一输入
* 降级策略：真实模型失败后回退规则引擎
* 调用追踪：记录 requestId、provider、modelName、promptVersion、耗时、失败原因

### 2.4 RAG 知识检索

* 向量数据库：Qdrant
* RAG 编排：LangChain Python
* Embedding 模型：bge-m3、DashScope embedding 或 OpenAI-compatible embedding
* 检索方式：第一版使用向量 TopK + metadata filter
* 数据来源：用户情绪记录、AI 分析结果、情绪向量、天气快照、历史报告、系统知识库

### 2.5 工程化

* 本地启动：Docker Compose
* API 文档：OpenAPI
* 日志追踪：统一 traceId
* AI 调用日志：模型调用全链路记录
* 配置管理：Prompt 模板、模型供应商、超时、降级策略可配置

---

## 3. 开发顺序总览

推荐按以下顺序推进：

1. 稳定记录到心境天气闭环
2. 重构洞察页组件结构
3. 接入洞察总览真实数据
4. 实现 AI 周报/月报生成
5. 实现 AI 对话助手
6. 实现 RAG 参考依据
7. 完善我的页隐私设置
8. 实现星球页轻量版
9. 最后补管理端 Prompt、知识库、模型调用日志

---

## 4. 第一阶段：稳定记录到心境天气闭环

### 4.1 目标

先保证移动端核心体验稳定：

```text
记录 -> AI 分析 -> 结果页 -> 心境首页刷新最新天气
```

### 4.2 移动端实现

涉及页面：

* `pages/record/index.vue`
* `pages/record/result.vue`
* `pages/weather/index.vue`

建议优化点：

* 记录页提交前统一校验文本、图片、语音至少一项存在
* 图片和语音上传失败时给出明确提示
* 提交后展示稳定的分析中动效
* 结果页所有字段做兜底，避免空数据导致页面异常
* 心境首页在返回后刷新最新天气
* 最近一次结果可做本地缓存，提升弱网体验

### 4.3 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 页面状态 | Vuex 或页面局部状态 |
| 请求封装 | `utils/request.js` |
| 文件上传 | `uni.uploadFile` |
| 分析状态 | 前端轮询 `analyzeStatus` |
| 本地缓存 | `uni.setStorageSync` |
| 失败重试 | 页面级 retry 按钮 |

### 4.4 后端建议

当前移动端分别调用分析、向量、天气生成接口。后续建议增加后端编排接口：

```text
POST /app/mood/record/submit
```

后端内部完成：

```text
保存记录 -> AI 分析 -> 生成情绪向量 -> 生成天气映射 -> 刷新天气快照
```

这样移动端只关心提交和展示，业务链路由后端保证一致性。

---

## 5. 第二阶段：重构洞察页组件结构

### 5.1 目标

将 `洞察` 从普通报告页升级为 AI 洞察中心。

路径沿用：

```text
pages/report/index.vue
```

TabBar 名称：

```text
洞察
```

### 5.2 页面结构

洞察页建议包含四个分区：

* 总览
* 对话
* 报告
* 知识

第一版优先实现 `总览`。

### 5.3 推荐组件拆分

```text
components/insight/InsightSummaryCard.vue
components/insight/TrendChart.vue
components/insight/EmotionRadar.vue
components/insight/PatternCard.vue
components/insight/AiChatEntry.vue
components/insight/RagSourceCard.vue
components/insight/ReportEntry.vue
```

API 层新增：

```text
api/insight/index.js
```

### 5.4 总览页内容

* 今日 AI 洞察卡
* 近 7 天情绪趋势
* 主导情绪
* 情绪波动指数
* 高频关键词
* 本周模式发现
* AI 对话入口
* AI 报告生成入口

### 5.5 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 趋势图 | uCharts |
| 雷达图 | uCharts |
| 分区切换 | segmented control |
| 快捷入口 | 卡片组件 |
| 空状态 | 统一 Empty 组件 |
| 周期切换 | 7 天、30 天、自定义 |

---

## 6. 第三阶段：接入洞察总览真实数据

### 6.1 目标

让洞察页从 UI 原型变成真实数据驱动。

### 6.2 推荐接口

```text
GET /app/insight/overview
GET /app/insight/trend
GET /app/insight/patterns
GET /app/mood/weather/snapshot/history
```

### 6.3 返回数据建议

`/app/insight/overview` 建议返回：

```json
{
  "summary": "这周你的心境多次出现低压多云，工作场景与疲惫感关联更强。",
  "dominantEmotion": "anxious",
  "dominantEmotionName": "焦虑",
  "weatherName": "低压多云",
  "recordCount": 12,
  "streakDays": 5,
  "waveIndex": 68,
  "keywords": ["工作", "疲惫", "睡眠"],
  "topScenes": [
    { "scene": "work", "name": "工作", "count": 4 }
  ]
}
```

### 6.4 状态处理

移动端必须覆盖：

* 加载中
* 无记录
* 数据不足
* 加载失败
* AI 洞察生成中

---

## 7. 第四阶段：AI 报告生成

### 7.1 目标

先实现报告生成，因为它比 AI 对话更容易形成稳定演示闭环。

移动端流程：

```text
洞察页 -> 点击生成周报 -> 生成中 -> 报告详情 -> 历史报告列表
```

### 7.2 推荐接口

```text
POST /app/insight/report/generate
GET /app/insight/report/list
GET /app/insight/report/detail/{id}
POST /app/insight/report/regenerate/{id}
```

### 7.3 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 报告生成 | 后端异步任务 |
| 生成状态 | 移动端轮询 |
| 报告正文 | MySQL 存储 |
| 图表数据 | `statJson` 结构化存储 |
| AI 文案 | Python AI 服务生成 |
| 引用来源 | `biz_report_source` |

### 7.4 报告内容

* 时间范围
* 记录数量
* 主导情绪
* 情绪天气序列
* 情绪趋势图
* 高频关键词
* 场景触发排行
* 异常波动点
* AI 总结
* 个性化建议
* 参考依据

### 7.5 关键原则

AI 报告生成不要同步阻塞移动端。推荐：

```text
提交生成任务 -> 返回 reportId -> 前端轮询状态 -> 生成成功后进入详情
```

---

## 8. 第五阶段：AI 对话助手

### 8.1 目标

在洞察页加入“问问 Mood AI”，基于用户个人数据回答问题。

第一版不做复杂 Agent，先做快捷问题 + 单轮或轻多轮问答。

### 8.2 快捷问题

* 分析我最近 7 天状态
* 为什么我晚上容易低落？
* 哪些场景最影响我？
* 给我三个调整建议
* 帮我生成本周总结

### 8.3 推荐接口

```text
POST /app/ai/chat/session
GET /app/ai/chat/session/list
GET /app/ai/chat/message/list
POST /app/ai/chat/message/send
DELETE /app/ai/chat/session/{id}
```

### 8.4 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 对话 UI | 气泡列表 + 输入框 + 快捷问题 chips |
| 会话历史 | MySQL |
| AI 回复 | Python FastAPI |
| 上下文范围 | 默认近 7 天，可切换近 30 天 |
| 回复输出 | 第一版完整返回；后续升级 SSE |
| 风险处理 | 后端识别高风险并触发预警 |

### 8.5 表设计建议

* `biz_ai_conversation`
* `biz_ai_message`
* `biz_ai_model_call_log`

---

## 9. 第六阶段：RAG 参考依据

### 9.1 目标

让 AI 对话和 AI 报告拥有可解释来源，避免“像在瞎说”。

### 9.2 检索来源

个人数据：

* 情绪记录
* AI 分析结果
* 情绪向量
* 天气快照
* 历史报告

系统知识：

* 情绪调节知识
* 睡眠与压力科普
* CBT 基础方法
* 正念和呼吸练习

### 9.3 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 文档切片 | LangChain splitters |
| 向量化 | bge-m3 或 DashScope embedding |
| 向量库 | Qdrant |
| 检索 | TopK + metadata filter |
| 用户隔离 | payload 存 `userId` |
| 引用展示 | 移动端折叠卡 |

### 9.4 移动端展示格式

AI 回答下方展示 `参考依据`：

```json
{
  "answer": "你最近的低落更集中在夜间，可能和睡眠压力有关。",
  "sources": [
    {
      "type": "mood_record",
      "title": "近7天记录",
      "summary": "夜间记录中低落和疲惫出现 4 次",
      "refId": 1001
    },
    {
      "type": "knowledge",
      "title": "睡眠与情绪调节",
      "summary": "睡眠不足可能提升情绪波动"
    }
  ]
}
```

注意：移动端不要直接展示完整敏感原文，只展示摘要和来源类型。

---

## 10. 第七阶段：我的页隐私设置

### 10.1 目标

让 AI 能力可控，补齐隐私和个性化设置。

### 10.2 功能清单

* 个性化分析开关
* 匿名投影开关
* 是否保存原始素材
* 每日提醒时间
* 周报提醒开关
* 数据导出
* 隐私说明

### 10.3 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 用户配置 | `biz_user_config` |
| 设置页 | UniApp 表单 + switch |
| 数据导出 | 后端生成 JSON/Excel |
| 隐私协议 | 静态页面 |
| 提醒 | UniApp 本地提醒 + 后端配置 |

---

## 11. 第八阶段：星球页轻量版

### 11.1 目标

移动端先做 2D 情绪星球，不优先做复杂 3D。

### 11.2 功能清单

* 匿名情绪点
* 区域情绪统计
* 情绪分类分布
* 今日星球天气
* 我的投影记录入口

### 11.3 技术实现

| 功能 | 技术方案 |
| --- | --- |
| 情绪点流 | Canvas 或普通气泡动画 |
| 区域统计 | `/app/globe/regionStats` |
| 热力展示 | SVG/Canvas 地图 |
| 匿名脱敏 | 后端处理 |
| 我的投影 | 列表页 |

不建议当前阶段投入复杂 Three.js。移动端小程序环境下 3D 成本高，稳定性和展示收益不成比例。

---

## 12. 暂缓开发项

以下功能不建议当前优先开发：

* 复杂 3D 星球
* 大屏可视化
* 完整知识库后台
* 多模型管理后台
* 复杂成就系统
* 过重的 Agent 编排

这些能力可以作为后续增强，不应阻塞移动端核心体验。

---

## 13. 推荐里程碑

### 13.1 第 1 周

* 稳定记录提交
* 稳定 AI 分析结果页
* 心境首页刷新最新天气
* 补齐错误状态和空状态

### 13.2 第 2 周

* 重构洞察页 UI
* 拆分洞察组件
* 接入趋势图和总览 mock 数据

### 13.3 第 3 周

* 接洞察总览真实接口
* 实现 AI 周报生成
* 实现报告详情页

### 13.4 第 4 周

* 实现 AI 对话助手
* 增加快捷问题
* 增加参考依据折叠卡

### 13.5 第 5 周

* 补我的页隐私设置
* 补星球页轻量版
* 统一视觉、动效和空状态

---

## 14. 项目包装重点

最终对外展示时，重点讲这条链路：

```text
多模态情绪记录
-> AI 结构化情绪分析
-> 情绪向量建模
-> 情绪天气可视化
-> AI 洞察总览
-> RAG 增强对话
-> AI 周报/月报生成
```

推荐项目描述：

```text
MoodSphere 是一个基于多模态大模型的 AI 情绪洞察平台。系统支持文本、图片、语音情绪记录，通过 AI 生成结构化情绪分析结果，并映射为动态情绪天气；同时基于个人情绪数据和心理知识库提供 RAG 增强对话、AI 周报/月报和长期情绪趋势洞察。
```

---

## 15. 当前最优先任务

当前最应该优先完成：

```text
记录一次心情后，用户能在心境页看到天气，在洞察页看到 AI 对趋势的解释，并能生成一份周报。
```

这条体验最能体现 AI 应用全栈开发者的工程能力和产品完整度。
