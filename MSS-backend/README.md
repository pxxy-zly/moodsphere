# MoodSphere Station 后端（MSS-backend）

## 项目简介

心境气象站（MoodSphere Station，MSS）是一款基于 Spring Boot 3、Vue 3 与 UniApp 构建的全栈实验型应用。项目通过文本、语音、图片等多模态方式采集用户情绪输入，结合 AI 大模型进行语义分析与情绪向量提取，并将结果映射为动态天气参数，形成个性化的“情绪天气”数字景观。系统同时支持基于 Three.js 的群体情绪数字地球展示，以可视化方式呈现匿名群体情绪的空间分布与动态变化，适用于心理健康辅助、情绪记录、社交互动及数字艺术展示等场景。

本仓库为 MSS 后端服务，基于若依 Spring Boot 3 初始代码扩展，目标是提供完整的业务闭环：

- 记录采集：接收文本、语音、图片等多模态输入
- 智能分析：输出结构化情绪结果而非直接展示文案
- 天气映射：将情绪向量转换为可视化天气参数
- 报告沉淀：生成日报、周报、月报等长期洞察
- 群体投影：输出匿名聚合数据用于星球可视化

## 技术栈

- Spring Boot 3
- Spring Security + JWT
- MyBatis
- Redis
- MySQL

## 仓库模块（现有）

- `moodsphere-admin`：启动模块与 Web API 入口
- `moodsphere-framework`：框架能力与基础配置
- `moodsphere-system`：系统管理与业务基础模块
- `moodsphere-common`：通用组件与工具类
- `moodsphere-quartz`：定时任务模块
- `moodsphere-generator`：代码生成模块
- `moodsphere-biz`：业务聚合模块（承载业务子模块）

## 业务模块（已初始化骨架）

- `system-user`：用户注册、登录鉴权、账号安全、Token 生命周期
- `record`：情绪记录主流程、草稿、编辑、分页查询
- `asset`：图片/音频素材上传、存储、元数据管理
- `ai-analyze`：多模态 AI 分析、关键词提取、风险语义识别
- `vector`：情绪向量归一化、平滑、置信度计算
- `weather-engine`：情绪天气映射规则、快照生成、视觉参数输出
- `report`：日报/周报/月报、趋势统计、摘要生成
- `globe`：匿名投影、区域聚合、时序流数据输出
- `risk`：风险分级、预警记录、审核标记
- `notify`：提醒与通知通道管理
- `config`：标签、映射规则、提示词模板等配置中心
- `stat`：运营统计、模型调用监控、成功率与成本分析

## 接口分层建议

- Controller：对接 UniApp 移动端与 Vue 管理端
- Service：承载核心业务编排与事务控制
- Domain/Entity：表达业务对象与状态
- Mapper/Repository：执行数据访问与持久化
- Engine：独立承载分析、向量、天气、报告、星球等计算内核

## MVP 优先范围

- 用户与认证
- 情绪记录
- 素材上传
- AI 情绪分析
- 情绪向量建模
- 情绪天气映射
- 情绪报告
- 配置中心

## 本地运行

1. 准备 JDK 17、MySQL、Redis
2. 导入 `sql` 目录下初始化脚本
3. 按需修改 `moodsphere-admin/src/main/resources` 下配置文件
4. 在项目根目录执行打包并启动

```bash
mvn clean package
java -jar moodsphere-admin/target/moodsphere-admin.jar
```

## 协同说明

- 建议与 PC 管理端（MSS-ui-admin）和移动端（MSS-ui-app）配套联调
- 当前代码基于若依初始工程，后续将围绕 MSS 业务逐步模块化演进
