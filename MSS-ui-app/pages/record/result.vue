<template>
  <view class="result-page">
    <view v-if="loading" class="state-card">正在加载分析结果...</view>

    <view v-else-if="errorMsg" class="state-card error">
      <view class="state-title">加载失败</view>
      <view class="state-text">{{ errorMsg }}</view>
      <button class="ghost-btn" @click="loadData">重试</button>
    </view>

    <view v-else>
      <view class="card">
        <view class="card-title">AI分析</view>
        <view class="item-row">
          <text class="item-label">主情绪</text>
          <text class="item-value">{{ analysis ? analysis.primaryEmotion : '--' }}</text>
        </view>
        <view class="item-row">
          <text class="item-label">次情绪</text>
          <text class="item-value">{{ analysis ? analysis.secondaryEmotion : '--' }}</text>
        </view>
        <view class="item-row">
          <text class="item-label">风险等级</text>
          <text class="item-value">{{ analysis ? analysis.riskLevel : '--' }}</text>
        </view>
        <view class="item-block">
          <view class="item-label">关键词</view>
          <view class="tag-wrap">
            <text v-for="(item, idx) in keywordList" :key="idx" class="tag">{{ item }}</text>
            <text v-if="keywordList.length === 0" class="empty-text">暂无关键词</text>
          </view>
        </view>
        <view class="item-block">
          <view class="item-label">AI摘要</view>
          <view class="summary">{{ analysis ? analysis.aiSummary : '--' }}</view>
        </view>
      </view>

      <view class="card">
        <view class="card-title">情绪向量</view>
        <view class="grid">
          <view v-for="item in vectorItems" :key="item.key" class="grid-item">
            <view class="grid-label">{{ item.label }}</view>
            <view class="grid-value">{{ formatVectorValue(vector ? vector[item.key] : null) }}</view>
          </view>
        </view>
      </view>

      <view class="card">
        <view class="card-title">天气映射</view>
        <view class="item-row">
          <text class="item-label">天气</text>
          <text class="item-value">{{ weather ? weather.weatherName : '--' }}</text>
        </view>
        <view class="item-row">
          <text class="item-label">天空/降雨/风/雾</text>
          <text class="item-value small">
            {{ weather ? `${weather.skyType}/${weather.rainIntensity}/${weather.windSpeed}/${weather.fogIntensity}` : '--' }}
          </text>
        </view>
        <view class="item-row">
          <text class="item-label">色温</text>
          <text class="item-value">{{ weather ? weather.colorTemperature : '--' }}</text>
        </view>
        <view class="item-row">
          <text class="item-label">饱和度</text>
          <text class="item-value">{{ weather ? weather.saturation : '--' }}</text>
        </view>
      </view>

      <button class="primary-btn" @click="goWeather">去看我的天气</button>
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
      return this.analysis.emotionKeywords.split(',').filter(item => item)
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
      return numberValue.toFixed(4)
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
  background: linear-gradient(180deg, #eff6ff 0%, #ffffff 78%);
}

.state-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 36rpx;
  color: #334155;
  box-shadow: 0 10rpx 22rpx rgba(15, 23, 42, 0.07);
}

.state-card.error {
  color: #b91c1c;
}

.state-title {
  font-size: 32rpx;
  font-weight: 600;
}

.state-text {
  margin-top: 16rpx;
  font-size: 28rpx;
}

.card {
  margin-bottom: 20rpx;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 26rpx;
  box-shadow: 0 10rpx 22rpx rgba(15, 23, 42, 0.07);
}

.card-title {
  color: #0f172a;
  font-size: 32rpx;
  font-weight: 600;
  margin-bottom: 18rpx;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10rpx;
}

.item-block {
  margin-top: 16rpx;
}

.item-label {
  color: #64748b;
  font-size: 26rpx;
}

.item-value {
  color: #0f172a;
  font-size: 28rpx;
  font-weight: 600;
}

.item-value.small {
  font-size: 25rpx;
}

.tag-wrap {
  margin-top: 10rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.tag {
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  background: #e0ecff;
  color: #1d4ed8;
  font-size: 24rpx;
}

.empty-text {
  color: #94a3b8;
  font-size: 24rpx;
}

.summary {
  margin-top: 10rpx;
  color: #334155;
  font-size: 27rpx;
  line-height: 1.6;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.grid-item {
  border-radius: 14rpx;
  background: #f8fafc;
  padding: 12rpx;
}

.grid-label {
  font-size: 22rpx;
  color: #64748b;
}

.grid-value {
  margin-top: 6rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: #0f172a;
}

.primary-btn,
.ghost-btn {
  border-radius: 44rpx;
  font-size: 30rpx;
}

.primary-btn {
  margin-top: 16rpx;
  background: #1989fa;
  color: #ffffff;
}

.ghost-btn {
  margin-top: 22rpx;
  background: #ffffff;
  color: #1989fa;
  border: 1rpx solid #1989fa;
}
</style>
