<template>
  <view class="result-page">
    <!-- 初始加载 -->
    <view v-if="loading && !analysis && !isPendingTask" class="state-card">
      <view class="loading-icon">🔮</view>
      <view class="state-title gradient-text">正在解析你的心情</view>
      <view class="state-text">AI 正在感受你的情绪波动，请稍候...</view>
    </view>

    <!-- 任务进行中 -->
    <view v-else-if="isPendingTask" class="state-card">
      <view class="loading-icon">🫧</view>
      <view class="state-title gradient-text">{{ pendingTitle }}</view>
      <view class="state-text">{{ pendingText }}</view>
      <view class="state-tip" v-if="taskId">任务ID：{{ taskId }}</view>
      <button class="ghost-btn" @click="refreshTask">立即刷新</button>
    </view>

    <!-- 致命错误状态 -->
    <view v-else-if="fatalErrorMsg && !analysis" class="state-card error">
      <view class="state-icon">☁️</view>
      <view class="state-title error-text">解析遇到小乱流</view>
      <view class="state-text">{{ fatalErrorMsg }}</view>
      <button class="ghost-btn" @click="retryAnalyze">重新发起分析</button>
    </view>

    <!-- 结果展示 -->
    <view v-else class="content-fade-in">
      <!-- AI分析卡片 -->
      <view class="card ai-card">
        <view class="card-header">
          <view class="card-title">🤖 情绪解码</view>
          <view class="emotion-badge" :class="analysis ? getRiskClass(analysis.riskLevel) : 'risk-none'">
            {{ analysis ? getRiskText(analysis.riskLevel) : '心境平和' }}
          </view>
        </view>
        
        <view class="emotion-display">
          <view class="emotion-main">
            <text class="emotion-icon">{{ analysis ? getEmotionEmoji(analysis.primaryEmotion) : '🫧' }}</text>
            <text class="emotion-name">{{ analysis ? analysis.primaryEmotion : '--' }}</text>
            <text class="emotion-label">主导情绪</text>
          </view>
          <view class="emotion-arrow">〰️</view>
          <view class="emotion-sub">
            <text class="emotion-icon">{{ analysis ? getEmotionEmoji(analysis.secondaryEmotion) : '🫧' }}</text>
            <text class="emotion-name">{{ analysis ? analysis.secondaryEmotion : '--' }}</text>
            <text class="emotion-label">潜藏情绪</text>
          </view>
        </view>

        <view class="item-block">
          <view class="item-label">✨ 心境碎片 (关键词)</view>
          <view class="tag-wrap">
            <text v-for="(item, idx) in keywordList" :key="idx" class="tag">{{ item }}</text>
            <text v-if="keywordList.length === 0" class="empty-text">暂无碎片</text>
          </view>
        </view>

        <view class="summary-box">
          <view class="summary-header">💌 AI 寄语</view>
          <view class="summary">{{ analysis ? analysis.aiSummary : '生活明朗，万物可爱。' }}</view>
        </view>

        <view v-if="derivedErrorMsg" class="inline-notice">
          <text>{{ derivedErrorMsg }}</text>
        </view>
      </view>

      <!-- 情绪向量卡片 -->
      <view class="card vector-card">
        <view class="card-title">📊 情绪雷达</view>
        <view v-if="derivedLoading && !vector" class="section-empty">
          <view class="empty-title">正在生成情绪向量</view>
          <view class="empty-text">AI 已完成情绪分析，正在把感受转换成可视化雷达。</view>
        </view>
        <view v-else-if="vector" class="grid">
          <view v-for="item in vectorItems" :key="item.key" class="grid-item">
            <view class="grid-label">{{ item.label }}</view>
            <view class="grid-value">{{ formatVectorValue(vector ? vector[item.key] : null) }}</view>
            <view class="grid-bar">
              <view class="grid-bar-fill" :style="{ width: formatVectorPercent(vector ? vector[item.key] : null) }"></view>
            </view>
          </view>
        </view>
        <view v-else class="section-empty">
          <view class="empty-title">暂未生成情绪向量</view>
          <view class="empty-text">后端会自动生成情绪向量，如果刚完成分析，可以下拉刷新或稍后回来查看。</view>
        </view>
      </view>

      <!-- 天气映射卡片 -->
      <view class="card weather-card">
        <view class="card-title">🌤️ 你的心境天气</view>
        <view class="weather-display" v-if="weather">
          <view class="weather-main">
            <text class="weather-icon">{{ getWeatherEmoji(weather.weatherCode) }}</text>
            <view class="weather-info">
              <text class="weather-name">{{ weather.weatherName }}</text>
              <text class="weather-desc">{{ getWeatherDesc(weather.weatherCode) }}</text>
            </view>
          </view>
        </view>
        
        <view class="params-grid" v-if="weather">
          <view class="param-item">
            <text class="param-icon">☁️</text>
            <text class="param-val">{{ weather.cloudDensity || 0 }}</text>
            <text class="param-label">云量</text>
          </view>
          <view class="param-item">
            <text class="param-icon">💧</text>
            <text class="param-val">{{ weather.rainIntensity || 0 }}</text>
            <text class="param-label">降水</text>
          </view>
          <view class="param-item">
            <text class="param-icon">🌬️</text>
            <text class="param-val">{{ weather.windSpeed || 0 }}</text>
            <text class="param-label">风速</text>
          </view>
          <view class="param-item">
            <text class="param-icon">🌫️</text>
            <text class="param-val">{{ weather.fogIntensity || 0 }}</text>
            <text class="param-label">雾气</text>
          </view>
        </view>

        <view class="item-row" v-if="weather">
          <text class="item-label">天光色温</text>
          <text class="item-value highlight-pink">{{ weather.colorTemperature || '--' }} K</text>
        </view>
        <view class="item-row" v-if="weather">
          <text class="item-label">色彩饱和度</text>
          <text class="item-value highlight-blue">{{ weather.saturation || '--' }}%</text>
        </view>
        <view v-if="derivedLoading && !weather" class="section-empty">
          <view class="empty-title">正在生成你的心境天气</view>
          <view class="empty-text">正在把情绪向量映射成天气表现，请再等一下。</view>
        </view>
        <view v-else-if="!weather" class="section-empty">
          <view class="empty-title">天气结果还没准备好</view>
          <view class="empty-text">后端会自动生成心境天气，如果刚完成分析，可以下拉刷新或稍后再看。</view>
        </view>
      </view>

      <button class="primary-btn" @click="goWeather">{{ weather ? '进入我的气象站 🌈' : '先去气象站看看 🌈' }}</button>
    </view>
  </view>
</template>

<script>
import {
  createMoodAnalyzeTask,
  getMoodAnalyzeResult,
  getMoodAnalyzeTask,
  getMoodVector,
  getMoodWeatherMapping
} from '@/api/mood'

export default {
  data() {
    return {
      recordId: 0,
      taskId: 0,
      loading: false,
      errorMsg: '',
      analyzeStatus: 0,
      taskStatus: '',
      pollIntervalMs: 2500,
      taskErrorCode: '',
      taskErrorMessage: '',
      analysis: null,
      vector: null,
      weather: null,
      pollTimer: null,
      pollTimes: 0,
      maxPollTimes: 48,
      derivedLoading: false,
      derivedErrorMsg: '',
      vectorItems: [
        { key: 'valence', label: '愉悦度' },
        { key: 'arousal', label: '激活度' },
        { key: 'anxiety', label: '焦虑度' },
        { key: 'calmness', label: '平静度' },
        { key: 'loneliness', label: '孤独度' },
        { key: 'fatigue', label: '疲惫度' },
        { key: 'anger', label: '愤怒度' },
        { key: 'hope', label: '希望值' },
        { key: 'confidence', label: '掌控感' }
      ]
    }
  },
  computed: {
    keywordList() {
      if (!this.analysis || !this.analysis.emotionKeywords) return []
      const keywords = this.analysis.emotionKeywords
      if (keywords.includes('、')) return keywords.split('、').filter(item => item.trim())
      return keywords.split(',').filter(item => item.trim())
    },
    isPendingTask() {
      if (this.errorMsg) {
        return false
      }
      return this.analyzeStatus === 0 || ['QUEUED', 'RUNNING', 'PENDING'].includes(this.taskStatus)
    },
    fatalErrorMsg() {
      return this.taskErrorMessage || this.errorMsg
    },
    pendingTitle() {
      const titleMap = {
        QUEUED: '正在排队进入分析',
        RUNNING: 'AI 正在解析你的情绪',
        PENDING: '正在准备分析任务'
      }
      return titleMap[this.taskStatus] || '正在解析你的心情'
    },
    pendingText() {
      const textMap = {
        QUEUED: '记录已保存，系统正在排队处理你的情绪分析任务。',
        RUNNING: '文字、图片和语音线索正在被理解，向量和天气会在分析完成后自动生成。',
        PENDING: '任务正在准备中，请稍候。'
      }
      return textMap[this.taskStatus] || 'AI 正在感受你的情绪波动，请稍候...'
    }
  },
  onLoad(options) {
    this.recordId = Number(options.recordId || 0)
    this.taskId = Number(options.taskId || 0)
    if (!this.recordId) {
      this.errorMsg = '心境信号丢失了 (缺少recordId)'
      return
    }
    this.loadData()
  },
  onUnload() {
    this.clearPolling()
  },
  methods: {
    async loadData(options = {}) {
      const silent = options.silent === true
      if (!silent) {
        this.loading = true
        this.errorMsg = ''
      }
      try {
        await this.fetchAnalyzeStatus()
        if (this.isPendingTask) {
          this.vector = null
          this.weather = null
          this.derivedErrorMsg = ''
          this.startPolling()
          return
        }
        this.clearPolling()
        if (this.analyzeStatus === 2 || this.taskStatus === 'FAIL') {
          this.errorMsg = this.taskErrorMessage || 'AI分析小憩中了，请返回重试'
          return
        }
        await this.ensureDerivedData()
      } catch (error) {
        this.errorMsg = this.parseError(error)
      } finally {
        if (!silent) {
          this.loading = false
        }
      }
    },
    async fetchAnalyzeStatus() {
      let res = null
      if (this.taskId) {
        try {
          res = await getMoodAnalyzeTask(this.taskId)
        } catch (error) {
          res = null
        }
      }
      if (!res) {
        res = await getMoodAnalyzeResult(this.recordId)
      }
      const payload = this.normalizeAnalyzePayload(res)
      if (payload.taskId) {
        this.taskId = payload.taskId
      }
      this.analyzeStatus = payload.analyzeStatus
      this.taskStatus = payload.taskStatus
      this.pollIntervalMs = payload.pollIntervalMs
      this.taskErrorCode = payload.errorCode
      this.taskErrorMessage = payload.errorMessage
      this.analysis = payload.result
    },
    normalizeAnalyzePayload(response) {
      const payload = (response && response.data) || {}
      return {
        taskId: Number(payload.taskId || 0),
        taskStatus: payload.taskStatus || this.normalizeTaskStatus(payload.analyzeStatus),
        analyzeStatus: payload.analyzeStatus == null ? 0 : Number(payload.analyzeStatus),
        pollIntervalMs: Number(payload.pollIntervalMs || 2500),
        errorCode: payload.errorCode || '',
        errorMessage: payload.errorMessage || '',
        result: payload.result || null
      }
    },
    startPolling() {
      this.clearPolling()
      this.pollTimes = 0
      this.scheduleNextPoll()
    },
    scheduleNextPoll() {
      this.clearPolling()
      const delay = Math.max(1500, Number(this.pollIntervalMs || 2500))
      this.pollTimer = setTimeout(() => {
        this.pollTask()
      }, delay)
    },
    async pollTask() {
      this.pollTimes += 1
      try {
        await this.fetchAnalyzeStatus()
        if (this.isPendingTask) {
          if (this.pollTimes >= this.maxPollTimes) {
            this.errorMsg = '分析时间比平时久一些，你可以稍后回来继续查看'
            this.clearPolling()
            return
          }
          this.scheduleNextPoll()
          return
        }
        this.clearPolling()
        if (this.analyzeStatus === 2 || this.taskStatus === 'FAIL') {
          this.errorMsg = this.taskErrorMessage || 'AI分析失败，请重新发起分析'
          return
        }
        await this.ensureDerivedData()
      } catch (error) {
        if (this.pollTimes >= this.maxPollTimes) {
          this.errorMsg = this.parseError(error)
          this.clearPolling()
          return
        }
        this.scheduleNextPoll()
      }
    },
    clearPolling() {
      if (this.pollTimer) {
        clearTimeout(this.pollTimer)
        this.pollTimer = null
      }
    },
    async refreshTask() {
      this.errorMsg = ''
      this.loading = true
      try {
        await this.loadData({ silent: false })
      } finally {
        this.loading = false
      }
    },
    async ensureDerivedData() {
      if (this.derivedLoading) {
        return
      }
      this.derivedLoading = true
      this.derivedErrorMsg = ''
      try {
        await this.fetchExistingDerivedData(3)
        if (!this.vector || !this.weather) {
          this.derivedErrorMsg = '分析已完成，衍生结果正在同步中，请稍后刷新查看'
        }
      } catch (error) {
        this.derivedErrorMsg = this.parseError(error)
      } finally {
        this.derivedLoading = false
      }
    },
    async fetchExistingDerivedData(retries = 1) {
      for (let index = 0; index < retries; index += 1) {
        const [vectorRes, weatherRes] = await Promise.allSettled([
          getMoodVector(this.recordId),
          getMoodWeatherMapping(this.recordId)
        ])
        this.vector = vectorRes.status === 'fulfilled' ? (vectorRes.value.data || null) : null
        this.weather = weatherRes.status === 'fulfilled' ? (weatherRes.value.data || null) : null
        if (this.vector && this.weather) {
          return
        }
        if (index < retries - 1) {
          await this.sleep(800)
        }
      }
    },
    async retryAnalyze() {
      this.clearPolling()
      this.loading = true
      this.errorMsg = ''
      this.taskErrorCode = ''
      this.taskErrorMessage = ''
      this.vector = null
      this.weather = null
      try {
        const res = await createMoodAnalyzeTask(this.recordId)
        const payload = this.normalizeAnalyzePayload(res)
        this.taskId = payload.taskId
        this.taskStatus = payload.taskStatus
        this.analyzeStatus = payload.analyzeStatus
        this.analysis = payload.result
        this.pollIntervalMs = payload.pollIntervalMs
        this.startPolling()
      } catch (error) {
        this.errorMsg = this.parseError(error)
      } finally {
        this.loading = false
      }
    },
    normalizeTaskStatus(analyzeStatus) {
      if (analyzeStatus === 1) return 'SUCCESS'
      if (analyzeStatus === 2) return 'FAIL'
      return 'PENDING'
    },
    formatVectorValue(value) {
      if (value === null || value === undefined || value === '') return '--'
      const numberValue = Number(value)
      return Number.isNaN(numberValue) ? value : numberValue.toFixed(2)
    },
    formatVectorPercent(value) {
      if (value === null || value === undefined || value === '') return '0%'
      const numberValue = Number(value)
      return Number.isNaN(numberValue) ? '0%' : (numberValue * 100).toFixed(0) + '%'
    },
    getRiskClass(riskLevel) {
      if (riskLevel === null || riskLevel === undefined) return 'risk-none'
      if (riskLevel >= 3) return 'risk-high'
      if (riskLevel >= 1) return 'risk-medium'
      return 'risk-low'
    },
    getRiskText(riskLevel) {
      if (riskLevel === null || riskLevel === undefined) return '安全'
      if (riskLevel >= 3) return '雨暴风狂'
      if (riskLevel >= 1) return '泛起涟漪'
      return '微风拂面'
    },
    getEmotionEmoji(emotion) {
      const emojiMap = {
        happy: '🥰', calm: '🍃', sad: '🌧️', anxious: '🍂',
        irritable: '🌩️', lonely: '🌌', tired: '🥀', confused: '🌫️',
        hopeful: '✨', warm: '☀️', wronged: '💧', expect: '🌱'
      }
      return emojiMap[emotion] || '🫧'
    },
    getWeatherEmoji(code) {
      const emojiMap = {
        sunny: '☀️', breeze: '🍃', cloudy: '⛅',
        rain: '🌧️', storm: '🌩️', mist: '🌫️'
      }
      return emojiMap[code] || '🌈'
    },
    getWeatherDesc(code) {
      const descMap = {
        sunny: '阳光洒满心房', breeze: '轻风拂过原野', cloudy: '云朵遮住心事',
        rain: '淅沥小雨润物', storm: '情绪风暴过境', mist: '思绪漫入晨雾'
      }
      return descMap[code] || '未知的神秘气象'
    },
    parseError(error) {
      if (!error) return '请求失败，请稍后重试'
      if (typeof error === 'string') {
        if (error === '500') return '服务暂时有点忙，请稍后重试'
        return error
      }
      if (typeof error === 'number') return '请求失败，请稍后重试'
      if (error.msg) return error.msg
      if (error.message) return error.message
      return '请求失败，请稍后重试'
    },
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms))
    },
    goWeather() {
      this.$tab.switchTab('/pages/weather/index')
    }
  }
}
</script>

<style scoped>
/* 核心色彩变量：蓝粉交织 */
.result-page {
  min-height: 100vh;
  padding: 30rpx;
  /* 柔和的蓝粉天空渐变背景 */
  background: linear-gradient(160deg, #F0F7FF 0%, #F8F4FF 50%, #FFF0F5 100%);
  padding-bottom: 80rpx;
}

/* --- 卡片通用样式 (毛玻璃质感) --- */
.card, .state-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 36rpx;
  padding: 36rpx;
  box-shadow: 0 12rpx 40rpx rgba(92, 156, 230, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.6);
  margin-bottom: 28rpx;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card:active {
  transform: scale(0.98);
}

.card-title {
  color: #2C3E50;
  font-size: 34rpx;
  font-weight: 600;
  margin-bottom: 24rpx;
  display: flex;
  align-items: center;
  letter-spacing: 2rpx;
}

.card-title::before {
  content: '';
  width: 8rpx;
  height: 32rpx;
  /* 标题左侧装饰线：蓝粉渐变 */
  background: linear-gradient(180deg, #5C9CE6 0%, #FF8DA1 100%);
  border-radius: 6rpx;
  margin-right: 16rpx;
}

/* --- 状态视图 (加载/错误) --- */
.state-card {
  text-align: center;
  padding: 80rpx 40rpx;
  margin-top: 100rpx;
}

.state-title {
  font-size: 36rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.gradient-text {
  background: linear-gradient(135deg, #5C9CE6 0%, #FF8DA1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.error-text {
  color: #FF8DA1;
}

.state-text {
  font-size: 28rpx;
  color: #8A98A8;
  line-height: 1.6;
}

.state-tip {
  margin-top: 18rpx;
  font-size: 24rpx;
  color: #A3AFC0;
}

/* --- AI 解析卡片 --- */
.ai-card {
  position: relative;
  overflow: hidden;
}

.ai-card::after {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300rpx;
  height: 300rpx;
  background: radial-gradient(circle, rgba(92, 156, 230, 0.1) 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  position: relative;
  z-index: 1;
}

.emotion-badge {
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
  font-size: 22rpx;
  font-weight: 600;
  letter-spacing: 1rpx;
}

/* 风险等级契合自然意象 */
.emotion-badge.risk-none { background: rgba(92, 156, 230, 0.1); color: #5C9CE6; }
.emotion-badge.risk-low { background: rgba(16, 185, 129, 0.1); color: #10B981; }
.emotion-badge.risk-medium { background: rgba(245, 166, 35, 0.1); color: #F5A623; }
.emotion-badge.risk-high { background: rgba(255, 141, 161, 0.15); color: #FF6B81; }

.emotion-display {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 0;
  position: relative;
  z-index: 1;
}

.emotion-main, .emotion-sub {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 28rpx 40rpx;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(92, 156, 230, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.8);
  flex: 1;
}

.emotion-icon { font-size: 72rpx; margin-bottom: 12rpx; }
.emotion-name { font-size: 32rpx; font-weight: 600; color: #2C3E50; margin-bottom: 6rpx; }
.emotion-label { font-size: 22rpx; color: #8A98A8; }

.emotion-arrow {
  font-size: 36rpx;
  color: #B4C6D8;
  opacity: 0.6;
}

.item-block {
  margin-top: 32rpx;
  position: relative;
  z-index: 1;
}

.item-label {
  color: #8A98A8;
  font-size: 26rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag {
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
  /* 标签蓝粉渐变背景 */
  background: linear-gradient(135deg, rgba(92, 156, 230, 0.08) 0%, rgba(255, 141, 161, 0.08) 100%);
  color: #5C9CE6;
  font-size: 24rpx;
  font-weight: 500;
}

.summary-box {
  margin-top: 32rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.4) 100%);
  border-radius: 28rpx;
  border-left: 6rpx solid #FF8DA1;
  position: relative;
  z-index: 1;
}

.summary-header { font-size: 24rpx; color: #FF8DA1; font-weight: 600; margin-bottom: 12rpx; }
.summary { font-size: 28rpx; color: #4A5568; line-height: 1.7; }

.inline-notice {
  margin-top: 24rpx;
  padding: 20rpx 24rpx;
  border-radius: 24rpx;
  background: rgba(255, 141, 161, 0.08);
  color: #D96B82;
  font-size: 24rpx;
  line-height: 1.6;
}

/* --- 情绪雷达 (向量卡片) --- */
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  margin-top: 20rpx;
}

.grid-item {
  background: rgba(255, 255, 255, 0.6);
  border-radius: 24rpx;
  padding: 24rpx 16rpx;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.grid-label { font-size: 22rpx; color: #8A98A8; margin-bottom: 10rpx; }
.grid-value { font-size: 30rpx; font-weight: 700; color: #2C3E50; font-family: "DIN Condensed", sans-serif; }

.grid-bar {
  height: 8rpx;
  background: rgba(92, 156, 230, 0.1);
  border-radius: 4rpx;
  margin-top: 12rpx;
  overflow: hidden;
}

.grid-bar-fill {
  height: 100%;
  /* 进度条：从蓝到粉 */
  background: linear-gradient(90deg, #5C9CE6 0%, #FF8DA1 100%);
  border-radius: 4rpx;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.section-empty {
  text-align: center;
  padding: 24rpx 12rpx 8rpx;
}

.empty-title {
  color: #2C3E50;
  font-size: 30rpx;
  font-weight: 600;
}

.empty-text {
  margin-top: 12rpx;
  color: #8A98A8;
  font-size: 24rpx;
  line-height: 1.7;
}

/* --- 天气映射卡片 --- */
.weather-display {
  display: flex;
  justify-content: center;
  margin-bottom: 30rpx;
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 30rpx;
  padding: 30rpx 50rpx;
  background: linear-gradient(135deg, rgba(92, 156, 230, 0.05) 0%, rgba(255, 141, 161, 0.05) 100%);
  border-radius: 36rpx;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.weather-icon { font-size: 88rpx; }
.weather-name { font-size: 40rpx; font-weight: 700; color: #2C3E50; }
.weather-desc { font-size: 24rpx; color: #8A98A8; margin-top: 6rpx; }

.params-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-bottom: 30rpx;
}

.param-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20rpx;
}

.param-icon { font-size: 36rpx; margin-bottom: 10rpx; }
.param-val { font-size: 32rpx; font-weight: 700; color: #2C3E50; }
.param-label { font-size: 20rpx; color: #8A98A8; margin-top: 6rpx; }

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1px dashed rgba(92, 156, 230, 0.15);
}
.item-row:last-child { border-bottom: none; }

.highlight-pink { color: #FF8DA1; font-weight: 600; font-size: 30rpx; }
.highlight-blue { color: #5C9CE6; font-weight: 600; font-size: 30rpx; }

/* --- 按钮样式 --- */
.primary-btn {
  width: 100%;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
  height: 96rpx;
  line-height: 96rpx;
  margin-top: 40rpx;
  /* 按钮渐变：蓝粉交织 */
  background: linear-gradient(135deg, #5C9CE6 0%, #FF8DA1 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 12rpx 30rpx rgba(255, 141, 161, 0.25);
  transition: all 0.3s ease;
}

.primary-btn:active {
  transform: translateY(4rpx);
  box-shadow: 0 6rpx 16rpx rgba(255, 141, 161, 0.2);
}

.primary-btn::after { display: none; }

.ghost-btn {
  margin-top: 24rpx;
  background: transparent;
  color: #5C9CE6;
  border: 2rpx solid #5C9CE6;
  border-radius: 50rpx;
  height: 88rpx;
  line-height: 84rpx;
}

.retry-btn {
  width: 320rpx;
}

/* --- 动画 --- */
.content-fade-in { animation: fadeIn 0.6s ease-out forwards; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(20rpx); } to { opacity: 1; transform: translateY(0); } }

.loading-icon {
  font-size: 88rpx;
  margin-bottom: 30rpx;
  animation: float 2.5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-16rpx) scale(1.05); }
}
</style>
