<template>
  <view class="result-page">
    <view v-if="loading" class="state-card">
      <view class="loading-icon">🔮</view>
      <view class="state-title">正在解析你的心情</view>
      <view class="state-text">AI 正在分析你的情绪，请稍候...</view>
    </view>

    <view v-else-if="errorMsg" class="state-card error">
      <view class="state-icon">⚠️</view>
      <view class="state-title">加载失败</view>
      <view class="state-text">{{ errorMsg }}</view>
      <button class="ghost-btn" @click="loadData">重新加载</button>
    </view>

    <view v-else>
      <!-- AI分析卡片 -->
      <view class="card ai-card">
        <view class="card-header">
          <view class="card-title">🤖 AI 情绪分析</view>
          <view class="emotion-badge" :class="getRiskClass(analysis ? analysis.riskLevel : null)">
            {{ getRiskText(analysis ? analysis.riskLevel : null) }}
          </view>
        </view>
        
        <view class="emotion-display">
          <view class="emotion-main">
            <text class="emotion-icon">{{ getEmotionEmoji(analysis ? analysis.primaryEmotion : '') }}</text>
            <text class="emotion-name">{{ analysis ? analysis.primaryEmotion : '--' }}</text>
            <text class="emotion-label">主情绪</text>
          </view>
          <view class="emotion-arrow">→</view>
          <view class="emotion-sub">
            <text class="emotion-icon">{{ getEmotionEmoji(analysis ? analysis.secondaryEmotion : '') }}</text>
            <text class="emotion-name">{{ analysis ? analysis.secondaryEmotion : '--' }}</text>
            <text class="emotion-label">次情绪</text>
          </view>
        </view>

        <view class="item-block">
          <view class="item-label">📌 关键词</view>
          <view class="tag-wrap">
            <text v-for="(item, idx) in keywordList" :key="idx" class="tag">{{ item }}</text>
            <text v-if="keywordList.length === 0" class="empty-text">暂无关键词</text>
          </view>
        </view>

        <view class="summary-box">
          <view class="summary-header">💭 AI 摘要</view>
          <view class="summary">{{ analysis ? analysis.aiSummary : '--' }}</view>
        </view>
      </view>

      <!-- 情绪向量卡片 -->
      <view class="card vector-card">
        <view class="card-title">📊 情绪向量</view>
        <view class="grid">
          <view v-for="item in vectorItems" :key="item.key" class="grid-item">
            <view class="grid-label">{{ item.label }}</view>
            <view class="grid-value">{{ formatVectorValue(vector ? vector[item.key] : null) }}</view>
            <view class="grid-bar">
              <view class="grid-bar-fill" :style="{ width: formatVectorPercent(vector ? vector[item.key] : null) }"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 天气映射卡片 -->
      <view class="card weather-card">
        <view class="card-title">🌤️ 天气映射</view>
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
            <text class="param-label">雨量</text>
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
          <text class="item-label">色温</text>
          <text class="item-value gradient-text">{{ weather.colorTemperature || '--' }} K</text>
        </view>
        <view class="item-row" v-if="weather">
          <text class="item-label">饱和度</text>
          <text class="item-value">{{ weather.saturation || '--' }}%</text>
        </view>
      </view>

      <button class="primary-btn" @click="goWeather">🌈 去看我的天气</button>
    </view>
  </view>
</template>

<script>
import { getMoodAnalyzeResult, getMoodVector, getMoodWeatherMapping } from '@/api/mood'

export default {
  data() {
    return {
      recordId: 0,
      loading: false,
      errorMsg: '',
      analyzeStatus: 0,
      analysis: null,
      vector: null,
      weather: null,
      pollTimer: null,
      pollTimes: 0,
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
      if (!this.analysis || !this.analysis.emotionKeywords) {
        return []
      }
      const keywords = this.analysis.emotionKeywords
      if (keywords.includes('、')) {
        return keywords.split('、').filter(item => item.trim())
      }
      return keywords.split(',').filter(item => item.trim())
    }
  },
  onLoad(options) {
    this.recordId = Number(options.recordId || 0)
    if (!this.recordId) {
      this.errorMsg = '缺少recordId参数'
      return
    }
    this.loadData()
  },
  onUnload() {
    this.clearPolling()
  },
  methods: {
    async loadData() {
      this.loading = true
      this.errorMsg = ''
      try {
        await this.fetchAnalyze()
        await this.fetchVectorAndWeather()
        if (this.analyzeStatus === 0) {
          this.startPolling()
        } else if (this.analyzeStatus === 2) {
          this.errorMsg = 'AI分析失败，请返回记录页重试'
        }
      } catch (error) {
        this.errorMsg = this.parseError(error)
      } finally {
        this.loading = false
      }
    },
    async fetchAnalyze() {
      const res = await getMoodAnalyzeResult(this.recordId)
      const payload = res.data || {}
      this.analyzeStatus = payload.analyzeStatus == null ? 0 : payload.analyzeStatus
      this.analysis = payload.result || null
    },
    async fetchVectorAndWeather() {
      try {
        const [vectorRes, weatherRes] = await Promise.all([
          getMoodVector(this.recordId),
          getMoodWeatherMapping(this.recordId)
        ])
        this.vector = vectorRes.data || null
        this.weather = weatherRes.data || null
      } catch (error) {
        // 允许在分析进行中无向量/天气结果
        if (this.analyzeStatus !== 0) {
          throw error
        }
      }
    },
    startPolling() {
      this.clearPolling()
      this.pollTimes = 0
      this.pollTimer = setInterval(async () => {
        this.pollTimes += 1
        try {
          await this.fetchAnalyze()
          if (this.analyzeStatus === 1) {
            this.clearPolling()
            await this.fetchVectorAndWeather()
          }
          if (this.analyzeStatus === 2 || this.pollTimes >= 8) {
            this.clearPolling()
          }
        } catch (error) {
          this.clearPolling()
        }
      }, 2500)
    },
    clearPolling() {
      if (this.pollTimer) {
        clearInterval(this.pollTimer)
        this.pollTimer = null
      }
    },
    formatVectorValue(value) {
      if (value === null || value === undefined || value === '') {
        return '--'
      }
      const numberValue = Number(value)
      if (Number.isNaN(numberValue)) {
        return value
      }
      return numberValue.toFixed(2)
    },
    formatVectorPercent(value) {
      if (value === null || value === undefined || value === '') {
        return '0%'
      }
      const numberValue = Number(value)
      if (Number.isNaN(numberValue)) {
        return '0%'
      }
      return (numberValue * 100).toFixed(0) + '%'
    },
    getRiskClass(riskLevel) {
      if (riskLevel === null || riskLevel === undefined) return 'risk-none'
      if (riskLevel >= 3) return 'risk-high'
      if (riskLevel >= 1) return 'risk-medium'
      return 'risk-low'
    },
    getRiskText(riskLevel) {
      if (riskLevel === null || riskLevel === undefined) return '安全'
      if (riskLevel >= 3) return '⚠️ 高风险'
      if (riskLevel >= 1) return '⚡ 中风险'
      return '✅ 低风险'
    },
    getEmotionEmoji(emotion) {
      const emojiMap = {
        happy: '😊', calm: '😌', sad: '😢', anxious: '😰',
        irritable: '😠', lonely: '🥺', tired: '😩', confused: '😕',
        hopeful: '🌟', warm: '🥰', wronged: '😤', expect: '🤔'
      }
      return emojiMap[emotion] || '😐'
    },
    getWeatherEmoji(code) {
      const emojiMap = {
        sunny: '☀️', breeze: '🌤️', cloudy: '☁️',
        rain: '🌧️', storm: '⛈️', mist: '🌫️'
      }
      return emojiMap[code] || '🌈'
    },
    getWeatherDesc(code) {
      const descMap = {
        sunny: '阳光明媚', breeze: '微风轻拂', cloudy: '多云转阴',
        rain: '绵绵细雨', storm: '骤雨风暴', mist: '薄雾朦胧'
      }
      return descMap[code] || '未知天气'
    },
    parseError(error) {
      if (!error) {
        return '请求失败，请稍后重试'
      }
      if (typeof error === 'string') {
        return error
      }
      if (error.msg) {
        return error.msg
      }
      if (error.message) {
        return error.message
      }
      return '请求失败，请稍后重试'
    },
    goWeather() {
      this.$tab.switchTab('/pages/weather/index')
    }
  }
}
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  padding: 28rpx;
  background: linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 50%, #F0F4FF 100%);
}

.state-card {
  background: #ffffff;
  border-radius: 32rpx;
  padding: 48rpx 36rpx;
  color: #334155;
  box-shadow: 0 8rpx 32rpx rgba(79, 70, 229, 0.08);
  border: 1px solid rgba(99, 102, 241, 0.08);
  text-align: center;
}

.state-card.error {
  color: #DC2626;
  border-color: rgba(220, 38, 38, 0.1);
  background: linear-gradient(135deg, #FEF2F2 0%, #FFFFFF 100%);
}

.state-title {
  font-size: 36rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.state-card.error .state-title {
  background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.state-text {
  margin-top: 16rpx;
  font-size: 28rpx;
  color: #64748B;
  line-height: 1.6;
}

.card {
  margin-bottom: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 32rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(79, 70, 229, 0.06);
  border: 1px solid rgba(99, 102, 241, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 16rpx rgba(79, 70, 229, 0.04);
}

.card-title {
  color: #1E1B4B;
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
  background: linear-gradient(180deg, #4F46E5 0%, #7C3AED 100%);
  border-radius: 4rpx;
  margin-right: 16rpx;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
  padding: 16rpx 0;
  border-bottom: 1px solid #F1F5F9;
}

.item-row:last-child {
  border-bottom: none;
}

.item-block {
  margin-top: 20rpx;
}

.item-label {
  color: #64748B;
  font-size: 26rpx;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.item-value {
  color: #1E1B4B;
  font-size: 30rpx;
  font-weight: 600;
  letter-spacing: 1rpx;
}

.item-value.small {
  font-size: 24rpx;
  color: #7C3AED;
  font-weight: 500;
}

.tag-wrap {
  margin-top: 12rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag {
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%);
  color: #4F46E5;
  font-size: 24rpx;
  font-weight: 500;
  border: 1px solid rgba(79, 46, 229, 0.1);
  transition: transform 0.2s ease;
}

.tag:active {
  transform: scale(0.95);
}

.empty-text {
  color: #94A3B8;
  font-size: 24rpx;
  font-style: italic;
}

.summary {
  margin-top: 12rpx;
  color: #475569;
  font-size: 28rpx;
  line-height: 1.8;
  padding: 20rpx;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  border-radius: 20rpx;
  border-left: 4rpx solid #7C3AED;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 8rpx;
}

.grid-item {
  border-radius: 20rpx;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  padding: 20rpx 16rpx;
  text-align: center;
  border: 1px solid #E2E8F0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.grid-item:active {
  transform: scale(0.95);
  box-shadow: 0 4rpx 12rpx rgba(79, 70, 229, 0.1);
}

.grid-label {
  font-size: 22rpx;
  color: #64748B;
  margin-bottom: 8rpx;
  font-weight: 500;
}

.grid-value {
  font-size: 26rpx;
  font-weight: 700;
  color: #4F46E5;
  font-family: "DIN Condensed", -apple-system, sans-serif;
}

.primary-btn,
.ghost-btn {
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: 500;
  letter-spacing: 2rpx;
  height: 96rpx;
  line-height: 96rpx;
  margin-top: 24rpx;
  transition: all 0.3s ease;
  box-shadow: 0 8rpx 24rpx rgba(79, 70, 229, 0.2);
}

.primary-btn {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  color: #ffffff;
  border: none;
}

.primary-btn:active {
  transform: translateY(4rpx);
  box-shadow: 0 4rpx 12rpx rgba(79, 70, 229, 0.3);
}

.primary-btn::after {
  display: none;
}

.ghost-btn {
  margin-top: 16rpx;
  background: rgba(255, 255, 255, 0.9);
  color: #4F46E5;
  border: 2rpx solid #4F46E5;
  box-shadow: none;
}

.ghost-btn:active {
  background: #EEF2FF;
}

/* 动画效果 */
.card {
  animation: fadeInUp 0.5s ease-out;
}

.card:nth-child(2) {
  animation-delay: 0.1s;
}

.card:nth-child(3) {
  animation-delay: 0.2s;
}

.card:nth-child(4) {
  animation-delay: 0.3s;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 风险等级高亮 */
.risk-high {
  color: #DC2626;
  font-weight: 700;
}

.risk-medium {
  color: #F59E0B;
  font-weight: 700;
}

.risk-low {
  color: #10B981;
  font-weight: 700;
}

/* Loading 状态 */
.loading-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.8; }
  50% { transform: scale(1.1); opacity: 1; }
}

.state-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

/* AI 分析卡片增强 */
.ai-card {
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.03) 0%, rgba(124, 58, 237, 0.05) 100%);
  border-color: rgba(79, 70, 229, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.emotion-badge {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.emotion-badge.risk-high {
  background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%);
  color: #DC2626;
}

.emotion-badge.risk-medium {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  color: #D97706;
}

.emotion-badge.risk-low {
  background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
  color: #059669;
}

.emotion-badge.risk-none {
  background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%);
  color: #4F46E5;
}

.emotion-display {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32rpx 0;
  gap: 32rpx;
}

.emotion-main, .emotion-sub {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 40rpx;
  background: rgba(79, 70, 229, 0.05);
  border-radius: 24rpx;
  border: 1px solid rgba(79, 70, 229, 0.1);
  min-width: 160rpx;
}

.emotion-icon {
  font-size: 64rpx;
  margin-bottom: 12rpx;
}

.emotion-name {
  font-size: 32rpx;
  font-weight: 700;
  color: #1E1B4B;
  margin-bottom: 4rpx;
  text-transform: capitalize;
}

.emotion-label {
  font-size: 22rpx;
  color: #64748B;
}

.emotion-arrow {
  font-size: 48rpx;
  color: #A5B4FC;
  font-weight: 300;
}

.summary-box {
  background: rgba(124, 58, 237, 0.05);
  border-radius: 20rpx;
  padding: 20rpx;
  margin-top: 16rpx;
}

.summary-header {
  font-size: 24rpx;
  color: #7C3AED;
  font-weight: 600;
  margin-bottom: 12rpx;
}

/* 向量卡片增强 */
.vector-card {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.03) 0%, rgba(6, 182, 212, 0.05) 100%);
  border-color: rgba(16, 185, 129, 0.1);
}

.vector-card .card-title::before {
  background: linear-gradient(180deg, #10B981 0%, #06B6D4 100%);
}

.grid-bar {
  height: 6rpx;
  background: #E2E8F0;
  border-radius: 3rpx;
  margin-top: 8rpx;
  overflow: hidden;
}

.grid-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #10B981 0%, #06B6D4 100%);
  border-radius: 3rpx;
  transition: width 0.5s ease-out;
}

/* 天气卡片增强 */
.weather-card {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.03) 0%, rgba(251, 146, 60, 0.05) 100%);
  border-color: rgba(245, 158, 11, 0.1);
}

.weather-card .card-title::before {
  background: linear-gradient(180deg, #F59E0B 0%, #FB923C 100%);
}

.weather-display {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 40rpx;
  background: rgba(245, 158, 11, 0.08);
  border-radius: 24rpx;
  border: 1px solid rgba(245, 158, 11, 0.15);
}

.weather-icon {
  font-size: 80rpx;
}

.weather-info {
  display: flex;
  flex-direction: column;
}

.weather-name {
  font-size: 40rpx;
  font-weight: 700;
  color: #1E1B4B;
}

.weather-desc {
  font-size: 24rpx;
  color: #64748B;
  margin-top: 4rpx;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin: 24rpx 0;
}

.param-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 8rpx;
  background: #F8FAFC;
  border-radius: 16rpx;
  border: 1px solid #E2E8F0;
}

.param-icon {
  font-size: 32rpx;
  margin-bottom: 8rpx;
}

.param-val {
  font-size: 28rpx;
  font-weight: 700;
  color: #1E1B4B;
  font-family: "DIN Condensed", -apple-system, sans-serif;
}

.param-label {
  font-size: 20rpx;
  color: #64748B;
  margin-top: 4rpx;
}

.gradient-text {
  background: linear-gradient(135deg, #F59E0B 0%, #EF4444 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}
</style>
