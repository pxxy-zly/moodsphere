<template>
  <view class="weather-page">
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-icon">🗂️</text>
        <text class="loading-text">正在加载天气快照...</text>
      </view>
    </view>

    <view class="bg-css-layer" :style="cssBgStyle">
      <view class="cloud cloud-1" :style="{ background: currentTheme.cloud1 }"></view>
      <view class="cloud cloud-2" :style="{ background: currentTheme.cloud2 }"></view>
      <view class="cloud cloud-3" :style="{ background: currentTheme.cloud3 }"></view>
    </view>
    <canvas canvas-id="snapshotCanvas" class="bg-canvas"></canvas>

    <swiper class="weather-swiper" :current="currentIndex" @change="onSwiperChange" :circular="false">
      <swiper-item v-for="(item, index) in snapshotList" :key="index">
        <scroll-view scroll-y class="scroll-container">
          <view class="top-spacer"></view>
          <view
            class="content-wrapper"
            :class="{ 'content-wrapper--dark-text': shouldUseDarkText(item) }"
          >
            <view class="date-header">
              <text class="date-text">{{ item.date }} {{ item.isToday ? ' (今日)' : '' }} · 天气快照</text>
            </view>

            <view class="hero-section">
              <text class="weather-title">{{ item.title }}</text>
            </view>

            <view class="glass-card ai-card">
              <view class="card-title-row">
                <text class="card-icon">✧</text>
                <text class="card-title">快照说明</text>
              </view>
              <text class="ai-text">{{ item.aiSummary }}</text>
            </view>

            <view class="params-grid">
              <view class="param-box">
                <text class="p-val">{{ item.params.temp }}<text class="p-unit">°C</text></text>
                <text class="p-lbl">情绪温度</text>
              </view>
              <view class="param-box">
                <text class="p-val">{{ item.params.pressure }}<text class="p-unit">hPa</text></text>
                <text class="p-lbl">情绪气压</text>
              </view>
              <view class="param-box">
                <text class="p-val">{{ item.params.wind }}<text class="p-unit">km/h</text></text>
                <text class="p-lbl">情绪风速</text>
              </view>
              <view class="param-box">
                <text class="p-val">{{ item.params.humidity }}<text class="p-unit">%</text></text>
                <text class="p-lbl">情绪湿度</text>
              </view>
            </view>

            <view class="row-cards">
              <view class="glass-card flex-2">
                <view class="card-title-row">
                  <text class="card-icon">🧭</text>
                  <text class="card-title">当天记录</text>
                </view>
                <text class="suggest-text">{{ item.lastRecord }}</text>
              </view>
              <view class="glass-card flex-1 trend-box">
                <view class="card-title-row">
                  <text class="card-title">滑动提示</text>
                </view>
                <view class="trend-icon">{{ item.trendIcon }}</view>
                <text class="trend-label">{{ item.trendTxt }}</text>
              </view>
            </view>

            <view class="actions-area">
              <button class="detail-btn" @click="goBackDynamic">回到动态天气</button>
            </view>

            <view class="bottom-spacer"></view>
          </view>
        </scroll-view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
import { getMoodWeatherSnapshot, listMoodWeatherSnapshots } from '@/api/mood'

const WEATHER_THEMES = {
  sunny: { bg1: '#FFE4B5', bg2: '#FFDAB9', bg3: '#FFEFD5', cloud1: '#FFD700', cloud2: '#FFA500', cloud3: '#FFE4C4' },
  breeze: { bg1: '#E6F3FF', bg2: '#B3D9FF', bg3: '#99CCFF', cloud1: '#FFFFFF', cloud2: '#E6F3FF', cloud3: '#CCE5FF' },
  cloudy: { bg1: '#D3D3D3', bg2: '#C0C0C0', bg3: '#A9A9A9', cloud1: '#D3D3D3', cloud2: '#C0C0C0', cloud3: '#A9A9A9' },
  rain: { bg1: '#4A5568', bg2: '#2D3748', bg3: '#1A202C', cloud1: '#4A5568', cloud2: '#2D3748', cloud3: '#1A202C' },
  storm: { bg1: '#1A1A2E', bg2: '#16213E', bg3: '#0F0F1A', cloud1: '#2D3748', cloud2: '#1A202C', cloud3: '#0F0F1A' },
  mist: { bg1: '#A0AEC0', bg2: '#CBD5E0', bg3: '#E2E8F0', cloud1: '#E2E8F0', cloud2: '#EDF2F7', cloud3: '#F7FAFC' }
}

const WEATHER_TITLES = {
  sunny: '晴空万里',
  breeze: '微风轻拂',
  cloudy: '多云转阴',
  rain: '绵绵细雨',
  storm: '骤雨风暴',
  mist: '薄雾朦胧'
}

const WEATHER_TYPES = {
  sunny: 'sunset',
  breeze: 'fog',
  cloudy: 'fog',
  rain: 'rainstorm',
  storm: 'rainstorm',
  mist: 'fog'
}

export default {
  data() {
    return {
      currentIndex: 0,
      canvasWidth: 0,
      canvasHeight: 0,
      animFrameId: null,
      particles: [],
      ctx: null,
      loading: true,
      snapshotList: []
    }
  },
  computed: {
    currentTheme() {
      const weather = this.snapshotList[this.currentIndex]
      if (!weather) return WEATHER_THEMES.cloudy
      const code = weather.weatherCode || 'cloudy'
      return WEATHER_THEMES[code] || WEATHER_THEMES.cloudy
    },
    cssBgStyle() {
      return `background: linear-gradient(145deg, ${this.currentTheme.bg1} 0%, ${this.currentTheme.bg2} 50%, ${this.currentTheme.bg3} 100%);`
    }
  },
  onLoad() {
    this.initSystemInfo()
  },
  onReady() {
    this.ctx = uni.createCanvasContext('snapshotCanvas', this)
  },
  onShow() {
    this.loadSnapshotData()
  },
  onHide() {
    this.stopAnimation()
  },
  onUnload() {
    this.stopAnimation()
  },
  methods: {
    initSystemInfo() {
      const sysInfo = uni.getSystemInfoSync()
      this.canvasWidth = sysInfo.windowWidth
      this.canvasHeight = sysInfo.windowHeight
    },
    async loadSnapshotData() {
      this.loading = true
      try {
        const todayDate = this.getTodayString()
        const [todayRes, historyRes] = await Promise.all([
          getMoodWeatherSnapshot(todayDate),
          listMoodWeatherSnapshots(15)
        ])
        this.buildSnapshotList(todayRes.data || null, historyRes.data || [])
      } catch (e) {
        console.error('加载快照失败', e)
        this.snapshotList = [this.createEmptySnapshot()]
      } finally {
        this.loading = false
        this.initCanvasEffect()
      }
    },
    buildSnapshotList(todaySnapshot, snapshotHistory) {
      const todayDate = this.getTodayString()
      const list = []
      let effectiveToday = todaySnapshot
      if (!effectiveToday && Array.isArray(snapshotHistory)) {
        effectiveToday = snapshotHistory.find(item => this.normalizeSnapshotDate(item.snapshotDate) === todayDate) || null
      }
      if (effectiveToday) {
        list.push(this.createSnapshotCard(effectiveToday, true))
      }
      if (Array.isArray(snapshotHistory)) {
        snapshotHistory
          .filter(item => this.normalizeSnapshotDate(item.snapshotDate) !== todayDate)
          .forEach(item => list.push(this.createSnapshotCard(item, false)))
      }
      this.snapshotList = list.length > 0 ? list : [this.createEmptySnapshot()]
      this.currentIndex = 0
    },
    createSnapshotCard(snapshot, isToday) {
      const weatherCode = snapshot.weatherCode || 'cloudy'
      const recordCount = snapshot.recordCount || 0
      return {
        date: this.normalizeSnapshotDate(snapshot.snapshotDate),
        isToday: isToday,
        weatherCode: weatherCode,
        type: WEATHER_TYPES[weatherCode] || 'fog',
        title: snapshot.weatherName || WEATHER_TITLES[weatherCode] || '多云',
        aiSummary: this.getUserFacingSummary(snapshot.aiSummary, weatherCode, snapshot.primaryEmotion, isToday),
        params: {
          temp: this.estimateTemp(weatherCode),
          pressure: this.estimatePressure(weatherCode),
          wind: snapshot.windSpeed || 3,
          humidity: this.estimateHumidity(weatherCode, snapshot.rainIntensity)
        },
        lastRecord: recordCount > 0 ? `当日记录 ${recordCount} 次` : '当日无有效记录',
        trendIcon: isToday ? '👈' : '🕰️',
        trendTxt: isToday ? '左滑查看历史快照' : '继续左滑查看更早日期'
      }
    },
    createEmptySnapshot() {
      return {
        date: this.getTodayString(),
        isToday: true,
        weatherCode: 'cloudy',
        type: 'fog',
        title: '暂无天气快照',
        aiSummary: '今日还没有可展示的天气快照，请先完成心情记录。',
        params: { temp: '--', pressure: '--', wind: '--', humidity: '--' },
        lastRecord: '暂无记录',
        trendIcon: '📝',
        trendTxt: '先去记录心情'
      }
    },
    getTodayString() {
      const now = new Date()
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    },
    normalizeSnapshotDate(snapshotDate) {
      if (!snapshotDate) return this.getTodayString()
      if (typeof snapshotDate === 'string') return snapshotDate.slice(0, 10)
      const d = new Date(snapshotDate)
      if (Number.isNaN(d.getTime())) return this.getTodayString()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    estimateTemp(code) {
      const temps = { sunny: 28, breeze: 22, cloudy: 18, rain: 15, storm: 12, mist: 16 }
      return temps[code] || 20
    },
    estimatePressure(code) {
      const pressures = { sunny: 1018, breeze: 1015, cloudy: 1012, rain: 1005, storm: 995, mist: 1010 }
      return pressures[code] || 1012
    },
    estimateHumidity(code, rainIntensity) {
      if (rainIntensity) return Math.min(95, 50 + rainIntensity * 8)
      const humidities = { sunny: 40, breeze: 55, cloudy: 65, rain: 85, storm: 95, mist: 80 }
      return humidities[code] || 60
    },
    getUserFacingSummary(rawSummary, weatherCode, primaryEmotion, isToday) {
      if (rawSummary && !this.isReportLikeSummary(rawSummary)) {
        return rawSummary
      }
      return this.buildSnapshotSummary(weatherCode, primaryEmotion, isToday)
    },
    isReportLikeSummary(summary) {
      const text = String(summary || '')
      const reportWords = ['用户表达', '心理风险', '强度', '/10', '健康状态', '自然状态', '诊断', '属于', '无风险', '低风险']
      return reportWords.some(word => text.includes(word))
    },
    buildSnapshotSummary(weatherCode, primaryEmotion, isToday) {
      const prefix = isToday ? '今日聚合后的心境快照' : '这一天的心境快照'
      const emotionText = this.getEmotionLabel(primaryEmotion)
      const summaries = {
        sunny: `${prefix}像一段明亮晴天，${emotionText}留下了比较舒展的痕迹。回看时，可以记住当时让你变轻的东西。`,
        breeze: `${prefix}像微风经过，${emotionText}让节奏保持柔和。适合回看那些曾经让你安定的小细节。`,
        cloudy: `${prefix}带着一点云层，${emotionText}并不完全清晰。它已经留下线索，等你慢慢回看。`,
        rain: `${prefix}像一场慢雨，${emotionText}需要被温柔接住。回看时，可以关注当时最需要被照顾的部分。`,
        storm: `${prefix}风声比较重，${emotionText}曾集中出现。可以结合详细分析慢慢拆开，不必一次想完。`,
        mist: `${prefix}像薄雾停留，${emotionText}让判断变慢。适合温和复盘，而不是急着给自己下结论。`
      }
      return summaries[weatherCode] || `${prefix}已经留下。你可以慢慢回看它，不急着立刻解释所有感受。`
    },
    getEmotionLabel(emotion) {
      const labels = {
        happy: '愉悦',
        anxious: '焦虑',
        tired: '疲惫',
        calm: '平静',
        wronged: '委屈',
        expect: '期待',
        lonely: '孤独',
        irritable: '烦躁',
        sad: '低落',
        warm: '温暖',
        confused: '困惑',
        hopeful: '希望感'
      }
      return labels[emotion] || '当前情绪'
    },
    shouldUseDarkText(item) {
      const code = item?.weatherCode || 'cloudy'
      const theme = WEATHER_THEMES[code] || WEATHER_THEMES.cloudy
      const luminance = this.getAverageLuminance([theme.bg1, theme.bg2, theme.bg3])
      return luminance > 0.42
    },
    getAverageLuminance(colors) {
      const validColors = colors.filter(Boolean)
      if (!validColors.length) return 0
      const total = validColors.reduce((sum, color) => sum + this.getHexLuminance(color), 0)
      return total / validColors.length
    },
    getHexLuminance(hex) {
      const normalized = hex.replace('#', '')
      if (normalized.length !== 6) return 0
      const rgb = [0, 2, 4].map(start => parseInt(normalized.slice(start, start + 2), 16) / 255)
      const linear = rgb.map(value => value <= 0.03928 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4))
      return 0.2126 * linear[0] + 0.7152 * linear[1] + 0.0722 * linear[2]
    },
    onSwiperChange(e) {
      this.currentIndex = e.detail.current
      this.initCanvasEffect()
    },
    initCanvasEffect() {
      if (this.animFrameId) clearInterval(this.animFrameId)
      this.particles = []
      const activeType = this.snapshotList[this.currentIndex]?.type || 'fog'
      if (activeType === 'rainstorm') {
        for (let i = 0; i < 80; i++) {
          this.particles.push({
            x: Math.random() * this.canvasWidth,
            y: Math.random() * this.canvasHeight,
            length: Math.random() * 25 + 10,
            speedY: Math.random() * 15 + 15,
            speedX: (Math.random() - 0.5) * 3
          })
        }
      } else {
        for (let i = 0; i < 25; i++) {
          this.particles.push({
            x: Math.random() * this.canvasWidth,
            y: Math.random() * this.canvasHeight,
            size: Math.random() * 2.5 + 1.5,
            speedY: -(Math.random() * 0.8 + 0.3),
            alpha: Math.random() * 0.5 + 0.2
          })
        }
      }
      this.animFrameId = setInterval(() => {
        this.drawFrame(activeType)
      }, 1000 / 30)
    },
    drawFrame(type) {
      if (!this.ctx) return
      this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight)
      if (type === 'rainstorm') {
        if (Math.random() < 0.03) {
          this.ctx.setFillStyle('rgba(255, 255, 255, 0.5)')
          this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight)
        }
        this.ctx.setLineWidth(1.5)
        this.ctx.setStrokeStyle('rgba(200, 200, 220, 0.7)')
        this.particles.forEach(p => {
          p.y += p.speedY
          p.x += p.speedX
          if (p.y > this.canvasHeight) {
            p.y = -30
            p.x = Math.random() * this.canvasWidth
          }
          this.ctx.beginPath()
          this.ctx.moveTo(p.x, p.y)
          this.ctx.lineTo(p.x + p.speedX, p.y + p.length)
          this.ctx.stroke()
        })
      } else {
        this.particles.forEach(p => {
          p.y += p.speedY
          if (p.y < -20) p.y = this.canvasHeight + 20
          this.ctx.beginPath()
          this.ctx.arc(p.x, p.y, p.size, 0, 2 * Math.PI)
          this.ctx.setFillStyle(`rgba(255, 255, 255, ${p.alpha})`)
          this.ctx.fill()
        })
      }
      this.ctx.draw(false)
    },
    stopAnimation() {
      if (this.animFrameId) {
        clearInterval(this.animFrameId)
        this.animFrameId = null
      }
    },
    goBackDynamic() {
      const pages = getCurrentPages()
      if (Array.isArray(pages) && pages.length > 1) {
        this.$tab.navigateBack()
      } else {
        this.$tab.switchTab('/pages/weather/index')
      }
    }
  }
}
</script>

<style scoped>
.weather-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  color: #fff;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(145deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
}

.loading-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
  animation: float 2s ease-in-out infinite;
}

.loading-text {
  font-size: 28rpx;
  opacity: 0.9;
  letter-spacing: 2rpx;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10rpx); }
}

.bg-css-layer {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  transition: background 0.8s ease-in-out;
  z-index: 1;
}

.cloud {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.8;
  transition: background 0.8s ease-in-out;
  animation: floatBg 20s infinite ease-in-out alternate;
}

.cloud-1 {
  width: 800rpx; height: 800rpx;
  top: -200rpx; left: -200rpx;
}

.cloud-2 {
  width: 700rpx; height: 700rpx;
  bottom: 10%; right: -200rpx;
  animation-delay: -5s;
}

.cloud-3 {
  width: 900rpx; height: 900rpx;
  bottom: -300rpx; left: -100rpx;
  animation-delay: -10s;
}

@keyframes floatBg {
  0% { transform: scale(1) translate(0, 0); }
  50% { transform: scale(1.05) translate(30rpx, -40rpx); }
  100% { transform: scale(0.95) translate(-30rpx, 40rpx); }
}

.bg-canvas {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.weather-swiper {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  height: 100%;
  z-index: 5;
}

.scroll-container {
  width: 100%;
  height: 100%;
}

.content-wrapper {
  padding: 0 40rpx;
  display: flex;
  flex-direction: column;
}

.content-wrapper--dark-text {
  color: #273a63;
}

.top-spacer { height: 8vh; }
.bottom-spacer { height: 8vh; }

.date-header { text-align: center; margin-bottom: 24rpx; opacity: 0.8; border-radius: 20rpx; }
.date-text { font-size: 26rpx; letter-spacing: 2rpx; background: rgba(0,0,0,0.15); padding: 8rpx 20rpx; border-radius: 30rpx; }

.hero-section {
  text-align: center;
  margin-bottom: 52rpx;
  margin-top: 20rpx;
}
.weather-title {
  font-size: 72rpx;
  font-weight: 300;
  letter-spacing: 6rpx;
  text-shadow: 0 6rpx 20rpx rgba(0,0,0,0.15);
}

.content-wrapper--dark-text .date-header {
  opacity: 1;
}

.content-wrapper--dark-text .date-text {
  color: #5d6680;
  background: rgba(255, 255, 255, 0.5);
  text-shadow: none;
}

.content-wrapper--dark-text .weather-title {
  color: #fff;
  text-shadow: 0 8rpx 24rpx rgba(95, 75, 40, 0.2);
}

.content-wrapper--dark-text .glass-card,
.content-wrapper--dark-text .param-box {
  background: rgba(255, 255, 255, 0.36);
  border-color: rgba(255, 255, 255, 0.56);
  box-shadow: 0 18rpx 38rpx rgba(116, 91, 42, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.36);
}

.content-wrapper--dark-text .card-title,
.content-wrapper--dark-text .card-icon,
.content-wrapper--dark-text .ai-text,
.content-wrapper--dark-text .suggest-text,
.content-wrapper--dark-text .p-val,
.content-wrapper--dark-text .p-unit,
.content-wrapper--dark-text .p-lbl,
.content-wrapper--dark-text .trend-label {
  color: #273a63;
  opacity: 1;
  text-shadow: 0 1rpx 0 rgba(255, 255, 255, 0.36);
}

.content-wrapper--dark-text .detail-btn {
  color: #2563eb;
  background: rgba(255, 255, 255, 0.82);
  border-color: rgba(37, 99, 235, 0.16);
  box-shadow: 0 12rpx 28rpx rgba(45, 92, 201, 0.13);
}

.glass-card {
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 40rpx;
  padding: 36rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 20rpx 40rpx rgba(0,0,0,0.08), inset 0 0 20rpx rgba(255,255,255,0.1);
}

.card-title-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
  opacity: 0.9;
}
.card-icon { font-size: 32rpx; margin-right: 12rpx; color: #fff;}
.card-title { font-size: 28rpx; font-weight: 500; letter-spacing: 2rpx; }

.ai-text, .suggest-text {
  font-size: 30rpx;
  line-height: 1.7;
  font-weight: 300;
  color: #fff;
  opacity: 0.95;
  letter-spacing: 1rpx;
}

.params-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
  margin-bottom: 30rpx;
}
.param-box {
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 36rpx;
  padding: 40rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.p-val { font-size: 56rpx; font-weight: 300; font-family: "DIN Condensed", -apple-system, sans-serif; }
.p-unit { font-size: 24rpx; margin-left: 8rpx; opacity: 0.8; font-family: sans-serif; }
.p-lbl { font-size: 26rpx; opacity: 0.8; margin-top: 12rpx; letter-spacing: 2rpx; }

.row-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 40rpx;
  align-items: stretch;
}
.flex-2 { flex: 2; margin-bottom: 0 !important; }
.flex-1 { flex: 1; margin-bottom: 0 !important; }
.trend-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.trend-icon { font-size: 64rpx; margin: 16rpx 0; }
.trend-label { font-size: 24rpx; opacity: 0.9; }

.actions-area {
  display: flex;
  justify-content: center;
}

.detail-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: #fff;
  border-radius: 50rpx;
  padding: 0 60rpx;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 30rpx;
  letter-spacing: 4rpx;
  font-weight: 300;
}
.detail-btn:after { display: none; }
</style>
