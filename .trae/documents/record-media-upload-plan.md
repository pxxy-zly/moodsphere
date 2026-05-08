# 记录页语音/图片上传打通方案

## Summary
- 目标：确认 `MSS-ui-app/pages/record/index.vue` 的语音、图片上传是否已经和 `MSS-backend/moodsphere-biz` 打通；若未打通，给出按当前仓库结构可直接落地的设计方案。
- 结论：当前仅“文本记录 -> AI 分析 -> 向量 -> 天气”链路已打通；语音/图片上传、素材落库、记录与素材绑定、多模态分析输入均未打通。
- 推荐方案：在 `moodsphere-biz/asset` 模块内补齐素材业务层，前端记录页接入媒体选择与上传，后端复用若依现有本地文件存储能力作为第一阶段存储实现，再由记录创建/提交接口关联素材。

## Current State Analysis

### 前端现状
- 记录页语音、图片按钮当前只执行占位逻辑，点击提示“暂未开放”，没有选择媒体、录音、上传、预览、删除逻辑。
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 记录提交仅发送 `contentText`、`emotionIntensity`、`isPublic`，随后调用：
  - `createMoodRecord`
  - `runMoodAnalyze`
  - `buildMoodVector`
  - `generateMoodWeather`
  - 相关文件：`d:\aZLY\A-MSS\02-code\MSS-ui-app\api\mood\index.js`
- 前端仓库里已有通用上传工具，基于 `uni.uploadFile`，但记录页未接入。
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\utils\upload.js`
- 当前 README/需求对记录页包含图片、语音上传有描述，但实际 `pages.json` 和记录页业务还未实现完整上传闭环。

### 后端现状
- `moodsphere-biz/asset` 模块只有 `pom.xml`，没有 controller/service/entity/mapper。
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-biz\asset\pom.xml`
- 记录创建 DTO 仅支持文本类字段，不支持 `assetIds`、`images`、`voiceUrl`、`voiceDuration` 等素材字段。
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-biz\record\src\main\java\com\moodsphere\record\domain\dto\MoodRecordCreateBody.java`
- 记录创建服务当前固定 `recordType = 0`，本质仅支持文本记录。
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-biz\record\src\main\java\com\moodsphere\record\service\impl\MoodRecordServiceImpl.java`
- `moodsphere-biz` 中未发现图片/语音素材上传控制器，也未发现 `biz_mood_asset` 的 Java 实现。
- 仓库已有若依通用上传接口 `/common/upload`，可以把文件存到本地 `profile/upload` 并返回 URL。
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-admin\src\main\java\com\moodsphere\web\controller\common\CommonController.java`
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-admin\src\main\resources\application.yml`
- 数据库已经预留 `biz_mood_asset` 表，说明业务模型考虑过素材，但业务代码未接入。
  - `d:\aZLY\A-MSS\02-code\MSS-backend\sql\init.sql`

### 需求对照结论
- 需求文档明确要求：
  - 图片上传：上传图片、返回 URL、保存素材信息、支持缩略图
  - 语音上传：上传语音、保存文件地址与时长、预留语音转写
  - 记录页支持文本、语音、图片
- 当前实现与需求差距：
  - 前端按钮未接入真实上传
  - 后端无素材业务模块
  - 记录接口未承载素材
  - AI 请求模型仍仅支持文本

## Proposed Changes

### 方案原则
- 第一阶段先打通“上传 + 素材落库 + 记录绑定 + 页面可用”。
- AI 多模态增强只做接口预留，不在第一阶段强制实现复杂模型处理。
- 存储先复用若依现有本地上传能力，避免一次性引入 OSS/MinIO 改造。
- 业务接口统一收口到 `moodsphere-biz/asset`，不要让前端直接依赖 `/common/upload` 作为长期方案。

### 后端改造

#### 1. 在 `asset` 模块补齐素材业务闭环
- 新增实体：
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-biz\asset\src\main\java\com\moodsphere\asset\domain\entity\BizMoodAsset.java`
- 新增 DTO/VO：
  - 上传响应 DTO
  - 记录绑定素材 DTO
  - 查询素材列表 VO
- 新增 Mapper：
  - `BizMoodAssetMapper.java`
  - `BizMoodAssetMapper.xml`
- 新增 Service：
  - `IMoodAssetService.java`
  - `MoodAssetServiceImpl.java`
- 新增 Controller：
  - `MoodAssetController.java`

#### 2. 新增素材上传接口
- 推荐接口：
  - `POST /app/mood/asset/upload/image`
  - `POST /app/mood/asset/upload/voice`
- 接口职责：
  - 接收 `MultipartFile`
  - 校验文件类型与大小
  - 调用若依现有上传工具保存文件
  - 生成素材记录并写入 `biz_mood_asset`
  - 返回 `assetId`、`fileUrl`、`fileName`、`duration`、`thumbnailUrl`

#### 3. 文件类型与元数据处理
- 图片：
  - 允许 `jpg/jpeg/png/webp`
  - 记录 `fileType=image`
  - 预留 `thumbnailUrl`，首版可直接为空或与原图同值
- 语音：
  - 扩展白名单支持 `mp3/wav/m4a/aac`
  - 记录 `fileType=voice`
  - 首版前端传 `duration`，后端做基础校验后落库
- 若当前若依上传白名单不支持音频，需要在文件类型工具类上补充音频扩展名支持。

#### 4. 记录模块补充素材绑定能力
- 修改创建 DTO：
  - `MoodRecordCreateBody.java`
- 新增字段建议：
  - `List<Long> assetIds`
  - `Integer recordType`
  - `Integer voiceDuration`
- 修改记录创建服务：
  - 校验文本可为空，但文本、图片、语音至少有一种
  - 根据素材类型自动推导或校验 `recordType`
  - 记录创建成功后，将 `biz_mood_asset.record_id` 回填，或通过关系更新完成绑定

#### 5. AI 分析链路预留
- 第一阶段：
  - 仍以 `contentText` 为主分析输入
  - 图片/语音素材只完成上传、落库、展示
- 第二阶段预留：
  - 语音先走转写，转写文本补充到分析输入
  - 图片可后续接 OCR/视觉标签摘要
- 涉及文件（预留，不一定首阶段改）：
  - `d:\aZLY\A-MSS\02-code\MSS-backend\moodsphere-biz\ai-analyze\src\main\java\com\moodsphere\analyze\domain\dto\PythonAnalyzeRequest.java`
  - `d:\aZLY\A-MSS\02-code\MSS-backend\py-ai-service\app\schemas.py`

### 前端改造

#### 1. 记录页 UI 与状态
- 文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 增加本地状态：
  - `imageAssets`
  - `voiceAsset`
  - `uploading`
  - `recording`
- 页面能力：
  - 图片选择、预览、删除
  - 语音录制/选择、时长展示、删除
  - 上传中状态提示
  - 提交前校验“文本/图片/语音至少有一个”

#### 2. 前端 API 封装
- 新增文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\api\asset\index.js`
- 封装接口：
  - `uploadMoodImage(filePath)`
  - `uploadMoodVoice(filePath, duration)`
- 实现上复用：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\utils\upload.js`

#### 3. 记录提交流程调整
- 现有流程：
  - 创建记录 -> 分析 -> 向量 -> 天气
- 调整后流程：
  1. 先完成图片/语音上传，拿到 `assetIds`
  2. 调用创建记录接口，提交 `contentText + emotionIntensity + isPublic + assetIds`
  3. 创建成功后继续分析、向量、天气流程

#### 4. 结果页可选增强
- 文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\result.vue`
- 首阶段可不改
- 若要增强，可显示：
  - 已上传图片缩略图
  - 语音时长及播放入口

### 接口与数据流设计

#### 图片上传
- 前端调用：`uploadMoodImage(filePath)`
- 后端返回：
  - `assetId`
  - `fileUrl`
  - `fileType=image`
  - `thumbnailUrl`

#### 语音上传
- 前端调用：`uploadMoodVoice(filePath, duration)`
- 后端返回：
  - `assetId`
  - `fileUrl`
  - `fileType=voice`
  - `duration`

#### 记录创建
- 请求体建议：
  - `contentText`
  - `emotionIntensity`
  - `isPublic`
  - `assetIds`
  - `recordType`
- 服务端规则：
  - 文本为空时，只要 `assetIds` 非空仍允许创建
  - 全空则拒绝

## Assumptions & Decisions
- 决策：首阶段不直接让前端长期依赖 `/common/upload`，而是在 `moodsphere-biz/asset` 增加业务上传接口，对前端提供稳定业务语义。
- 决策：存储层先复用若依当前本地磁盘上传机制，不引入对象存储改造。
- 决策：语音时长首阶段由前端采集并传后端，后端只做基础校验与落库。
- 决策：AI 多模态分析不作为首阶段打通的完成标准，先确保素材采集与记录绑定闭环可用。
- 假设：`biz_mood_asset` 表字段可满足首阶段需求，无需新增表；若实现时发现缺字段，再追加数据库变更方案。
- 假设：当前小程序端可使用 `uni.chooseImage` 与录音能力；若录音能力受平台约束，可先实现“从已有音频文件上传”或仅支持图片首发。

## Verification steps
- 前端验证
  - 记录页可选择图片并显示预览
  - 记录页可录制或选择语音并显示时长
  - 删除素材后 UI 与提交参数同步变化
  - 文本为空但有图片/语音时允许提交
- 后端验证
  - 图片上传接口返回 `assetId/fileUrl`
  - 语音上传接口返回 `assetId/fileUrl/duration`
  - `biz_mood_asset` 有正确落库
  - 创建记录后素材与 `record_id` 正确绑定
- 端到端验证
  - 提交“纯文本”“文本+图片”“文本+语音”“仅图片/仅语音”四类场景
  - 记录创建成功后仍可继续跑分析、向量、天气接口
  - 旧的纯文本记录能力不回归

