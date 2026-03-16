# MoodSphere Station 移动端（MSS-ui-app）

## 项目简介

心境气象站（MoodSphere Station，MSS）是一款基于 Spring Boot 3、Vue 3 与 UniApp 构建的全栈实验型应用。项目通过文本、语音、图片等多模态方式采集用户情绪输入，结合 AI 大模型进行语义分析与情绪向量提取，并将结果映射为动态天气参数，形成个性化的“情绪天气”数字景观。系统同时支持基于 Three.js 的群体情绪数字地球展示，以可视化方式呈现匿名群体情绪的空间分布与动态变化，适用于心理健康辅助、情绪记录、社交互动及数字艺术展示等场景。

本仓库为 MSS 移动端应用，基于若依 UniApp 初始代码扩展，主要承载：

- 用户日常情绪记录与查看
- 文本、语音、图片等多模态输入入口
- 个性化“情绪天气”结果展示
- 个人成长轨迹与情绪互动能力

## 产品定位

MSS 移动端定位为“情绪记录 + 情绪可视化 + 轻陪伴 + 个人成长”的日常应用，设计上兼顾：

- 快速记录
- 实时反馈
- 历史复盘
- 轻度互动
- 沉浸体验

## TabBar 结构（推荐）

- 天气：`/pages/weather/index`
- 记录：`/pages/record/index`
- 星球：`/pages/globe/index`
- 报告：`/pages/report/index`
- 我的：`/pages/mine/index`

## TabBar 页面职责

- 天气页：展示当前情绪天气主视觉、AI 摘要、参数简报、趋势小卡
- 记录页：承载文本/语音/图片输入、标签与强度、公开星球开关
- 星球页：展示匿名群体情绪投影与区域热力、时间维度切换
- 报告页：展示今日/周/月趋势、波动指数、关键词云、复盘洞察
- 我的页：管理个人资料、隐私设置、提醒配置、消息与帮助

## 非 TabBar 页面蓝图

- 账号体系：启动页、登录页、注册引导页
- 记录流程：语音转写页、图片分析页、记录成功页、分析结果页
- 天气流程：天气详情页、历史天气回放页、天气分享页
- 报告流程：日报详情页、周报详情页、月报详情页、关键词云详情页
- 星球流程：星球详情页、区域情绪详情页、我的投影记录页
- 个人中心：资料页、隐私设置页、消息中心页、提醒设置页、帮助反馈页、勋章成就页

## 页面路由建议

- `pages/weather/index`
- `pages/weather/detail`
- `pages/record/index`
- `pages/record/voice`
- `pages/record/image`
- `pages/record/result`
- `pages/globe/index`
- `pages/globe/detail`
- `pages/report/index`
- `pages/report/day`
- `pages/report/week`
- `pages/report/month`
- `pages/mine/index`
- `pages/mine/profile`
- `pages/mine/privacy`
- `pages/mine/message`
- `pages/mine/remind`
- `pages/auth/login`
- `pages/auth/register`
- `pages/common/splash`

## 技术栈

- UniApp
- Vue
- uni-ui
- Vuex（当前工程）

## 运行方式

1. 使用 HBuilderX 导入项目目录
2. 配置后端服务地址
3. 运行到 H5、Android、iOS 或小程序平台进行调试

## 与后端模块对应关系

- 天气页：`mood-record` + `mood-vector` + `mood-weather-engine` + `mood-report`
- 记录页：`mood-record` + `mood-asset` + `mood-ai-analyze`
- 星球页：`mood-globe` + `mood-stat`
- 报告页：`mood-report` + `mood-vector` + `mood-stat`
- 我的页：`system-user` + `mood-notify` + `mood-config`

## MVP 建议范围

优先上线页面：

- 天气页
- 记录页
- 报告页
- 我的页
- 登录页
- 情绪分析结果页
- 天气详情页

优先上线后端能力：

- 用户与认证
- 情绪记录
- 素材上传
- AI 情绪分析
- 情绪向量
- 情绪天气映射
- 情绪报告
- 配置中心

## 说明

- 当前代码基于若依移动端初始工程，后续将按页面蓝图逐步切换为 MSS 业务页面
- 建议与后端服务（MSS-backend）配套联调
