# 记录页提交加载体验优化方案（方案1）

## Summary
- 目标：将 `MSS-ui-app/pages/record/index.vue` 当前“普通 loading 等待”改造成更有体验感的“情绪生成中”全屏过渡层。
- 已选方案：方案 1，即在记录页内实现全屏轻量遮罩层，配合分阶段文案与步骤点提示，不改后端接口，不新增复杂动画引擎。
- 成功标准：
  - 用户提交记录后，不再只看到系统 `loading`
  - 页面展示 3 段式的情绪生成流程提示
  - 仍保持现有提交流程与结果页跳转逻辑不变
  - 失败时能正确关闭遮罩并显示错误

## Current State Analysis

### 当前提交流程
- 记录页提交入口在：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 当前 `handleSubmit` 里仍是前台串行等待：
  1. `createMoodRecord`
  2. `runMoodAnalyze`
  3. `buildMoodVector`
  4. `generateMoodWeather`
  5. 成功后跳结果页
- 纯素材无文本时，会在创建记录后直接结束，不进入分析链路。

### 当前加载体验
- 页面级按钮 loading 只有：
  - `submitting`
  - 提交按钮文案切换
- 全局 loading 使用：
  - `$modal.loading('生成中...')`
  - `$modal.closeLoading()`
- 这会让用户感知成“在等接口”，缺少产品主题中的“情绪正在变成天气”的体验。

### 可复用现状
- 项目里已有页面级全屏 loading 遮罩模式：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\weather\index.vue`
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\weather\snapshot.vue`
- 项目里已有全局 loading 插件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\plugins\modal.js`
- 项目里已有结果页三态结构：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\result.vue`

### 设计约束
- 本次仅做前端交互体验优化，不调整后端接口、不重构为异步提交结果页轮询方案。
- 优先在 `record/index.vue` 内完成，避免本次任务扩大到抽公共组件。
- 保持现有文本/图片/语音提交逻辑、素材处理逻辑和成功跳转逻辑不变。

## Proposed Changes

### 1. 在记录页新增“情绪生成中”全屏遮罩层
- 文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 新增页面状态：
  - `showAnalyzeOverlay`
  - `analyzeStage`
  - `analyzeStageText`
  - `analyzeStageDesc`
  - `analyzeSteps`
- 在模板中新增一个全屏覆盖层，定位在页面根节点内、`custom-tab-bar` 之上。
- 遮罩层内容包含：
  - 中央圆形/渐变呼吸视觉元素
  - 主标题：当前阶段文案
  - 副标题：更柔和的说明文字
  - 底部 3 个步骤点或简化步骤条

### 2. 阶段文案与流程映射
- 本次固定为 3 个阶段，和当前实际请求顺序对应：
  1. `收集此刻的心情`
     - 对应：`createMoodRecord`
     - 副文案示例：`先把这一刻好好保存下来`
  2. `识别情绪里的线索`
     - 对应：`runMoodAnalyze`
     - 副文案示例：`正在理解文字、图片或语音里的感受`
  3. `生成你的专属天气`
     - 对应：`buildMoodVector` + `generateMoodWeather`
     - 副文案示例：`把情绪慢慢酿成今天的天气`
- 对于“仅素材无文本”分支：
  - 只展示第 1 阶段
  - 成功后直接关闭遮罩并提示“素材记录已保存，文本分析可稍后补充”

### 3. 提交流程中的状态切换
- 文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 处理方式：
  - 提交开始时：
    - 打开 `showAnalyzeOverlay`
    - 初始化到第 1 阶段
  - `createMoodRecord` 成功后：
    - 若有文本，切到第 2 阶段
    - 若无文本，结束流程
  - `runMoodAnalyze` 成功后：
    - 切到第 3 阶段
  - `buildMoodVector`、`generateMoodWeather` 结束后：
    - 关闭遮罩
    - 清空表单
    - 跳转结果页
- 失败时：
  - 关闭遮罩
  - 关闭 loading
  - 保持表单内容不清空
  - 走现有错误提示逻辑

### 4. Loading 使用策略调整
- 记录页仍保留：
  - `submitting` 控制提交按钮禁用与文案
- 记录页不再依赖用户可见的系统 `uni.showLoading` 作为主反馈方式。
- 可以保留 `$modal.loading / closeLoading` 作为兜底，但实际体验应以页面内 overlay 为主。
- 实施时优先移除提交分析分支里“对用户可见”的通用 loading，避免与页面遮罩重叠。

### 5. 样式与视觉方案
- 文件：
  - `d:\aZLY\A-MSS\02-code\MSS-ui-app\pages\record\index.vue`
- 样式方向：
  - 遮罩背景：半透明浅色渐变 + 模糊背景
  - 中心视觉：柔和蓝粉渐变圆球，附带轻微呼吸动画
  - 文案层次：
    - 主文案更明显
    - 副文案弱一层
  - 步骤提示：
    - 3 个圆点/胶囊点
    - 当前步骤高亮
    - 已完成步骤使用更深色或更亮发光态
- 复用参考：
  - 天气页/快照页已有 `loading-overlay` 模式，但本次直接内聚在记录页，不抽公共组件

### 6. 建议的数据结构
- `analyzeSteps` 建议固定为：
  - `{ key: 'save', label: '保存', title: '收集此刻的心情', desc: '先把这一刻好好保存下来' }`
  - `{ key: 'analyze', label: '识别', title: '识别情绪里的线索', desc: '正在理解文字、图片或语音里的感受' }`
  - `{ key: 'weather', label: '天气', title: '生成你的专属天气', desc: '把情绪慢慢酿成今天的天气' }`
- 当前阶段切换可通过统一方法实现：
  - `setAnalyzeStage(index)`

### 7. 边界场景
- 仅素材提交：
  - 只展示第 1 阶段
  - 不切换到分析/天气阶段
- 请求异常：
  - 必须关闭遮罩
  - 不清空用户输入
- 重复点击提交：
  - 继续由 `submitting` 防重
- 页面关闭或跳转：
  - 遮罩状态应在成功跳转或失败后恢复初始值

## Assumptions & Decisions
- 决策：本次不改为“提交后立刻跳结果页，由结果页轮询”的方案，仍沿用当前同步提交链路。
- 决策：本次不抽公共 loading 组件，直接在 `record/index.vue` 内完成。
- 决策：分阶段文案与阶段切换由前端基于当前请求顺序控制，不依赖后端返回额外进度。
- 决策：仅素材提交沿用现有业务逻辑，不强行接入分析阶段。
- 假设：当前页面已有的 `submitting`、`uploading`、`recording` 状态足够配合遮罩层，不需新增全局状态管理。

## Verification steps
- 交互验证
  - 提交文本记录时，出现全屏“情绪生成中”遮罩
  - 遮罩文案能按阶段切换
  - 成功后关闭遮罩并跳转结果页
- 纯素材验证
  - 纯图片/纯语音提交时，只显示第 1 阶段
  - 成功后关闭遮罩并提示素材已保存
- 异常验证
  - 任一步请求失败，遮罩关闭
  - 表单内容与已选素材保留
- UI 验证
  - 遮罩不与底部 `submit-dock`、`custom-tab-bar` 冲突
  - 小程序端无明显卡顿或闪烁

