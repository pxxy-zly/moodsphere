<template>
  <view class="weather-page">
    <!-- Loading State -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-icon">🌤️</text>
        <text class="loading-text">正在获取天气...</text>
      </view>
    </view>

    <!-- Immersive Fixed Background CSS Layer (Changes with Swiper) -->
    <view class="bg-css-layer" :style="cssBgStyle">
      <view class="cloud cloud-1" :style="{ background: currentTheme.cloud1 }"></view>
      <view class="cloud cloud-2" :style="{ background: currentTheme.cloud2 }"></view>
      <view class="cloud cloud-3" :style="{ background: currentTheme.cloud3 }"></view>
    </view>
    
    <!-- Immersive Fixed Background Canvas Layer (For Rain, Lightning, Particles) -->
    <canvas canvas-id="weatherCanvas" class="bg-canvas"></canvas>

    <!-- Overlay Layer for Interaction -->
    <swiper class="weather-swiper" :current="currentIndex" @change="onSwiperChange" :circular="false">
      <swiper-item v-for="(item, index) in weatherList" :key="index">
        <!-- Vertical Scroll for each day's content -->
        <scroll-view scroll-y class="scroll-container">
          <!-- Spacer for visual breathing room -->
          <view class="top-spacer"></view>
          
          <view class="content-wrapper">
             <!-- Top Data Header -->
             <view class="date-header">
               <text class="date-text">{{ item.date }} {{ item.isToday ? ' (今日)' : '' }}</text>
             </view>

             <!-- Main Visual Header -->
             <view class="hero-section">
               <text class="weather-title">{{ item.title }}</text>
             </view>

             <!-- AI Summary Card -->
             <view class="glass-card ai-card">
               <view class="card-title-row">
                 <text class="card-icon">✧</text>
                 <text class="card-title">AI 情绪摘要</text>
               </view>
               <text class="ai-text">{{ item.aiSummary }}</text>
             </view>

             <!-- Weather Parameter Grid -->
             <view class="params-grid">
               <!-- Temp -->
               <view class="param-box">
                 <text class="p-val">{{ item.params.temp }}<text class="p-unit">°C</text></text>
                 <text class="p-lbl">情绪温度</text>
               </view>
               <!-- Pressure -->
               <view class="param-box">
                 <text class="p-val">{{ item.params.pressure }}<text class="p-unit">hPa</text></text>
                 <text class="p-lbl">情绪气压</text>
               </view>
               <!-- Wind -->
               <view class="param-box">
                 <text class="p-val">{{ item.params.wind }}<text class="p-unit">km/h</text></text>
                 <text class="p-lbl">情绪风速</text>
               </view>
               <!-- Humidity -->
               <view class="param-box">
                 <text class="p-val">{{ item.params.humidity }}<text class="p-unit">%</text></text>
                 <text class="p-lbl">情绪湿度</text>
               </view>
             </view>

             <!-- AI Suggestion & Trend -->
             <view class="row-cards">
               <!-- AI Suggestion -->
               <view class="glass-card flex-2">
                 <view class="card-title-row">
                   <text class="card-icon">✎</text>
                   <text class="card-title">治愈建议</text>
                 </view>
                 <text class="suggest-text">{{ item.aiSuggest }}</text>
               </view>
               <!-- Trend -->
               <view class="glass-card flex-1 trend-box">
                 <view class="card-title-row">
                   <text class="card-title">变化趋势</text>
                 </view>
                 <view class="trend-icon">{{ item.trendIcon }}</view>
                 <text class="trend-label">{{ item.trendTxt }}</text>
               </view>
             </view>

             <!-- Last Record -->
             <view class="last-record-text">上次记录时间：{{ item.lastRecord }}</view>
             
             <!-- Action Buttons -->
             <view class="actions-area">
               <!-- Only show Record on today -->
               <view class="record-btn-core" @click="goRecord" v-if="item.isToday">
                 <text class="record-text">记录心情</text>
               </view>
               
               <button class="detail-btn" @click="goDetail(item)">进入详细分析</button>
             </view>

             <!-- Bottom Spacer -->
             <view class="bottom-spacer"></view>
          </view>
        </scroll-view>
      </swiper-item>
    </swiper>
    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
import { getMoodWeatherToday, getMoodWeatherMapping, getMoodAnalyzeResult, getMoodRecord, getLatestMoodRecord } from '@/api/mood'

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

const EMOTION_SUGGESTS = {
  happy: '保持这份愉悦，可以尝试与朋友分享你的好心情',
  calm: '心境平和，适合做些放松的活动，如阅读或冥想',
  anxious: '焦虑情绪需要关注，尝试深呼吸或进行轻度运动',
  sad: '允许自己感受情绪，可以听些舒缓的音乐或与朋友倾诉',
  irritable: '情绪有些急躁，建议暂时离开让你烦躁的环境',
  lonely: '孤独感来袭，可以主动联系朋友或家人',
  default: '建议今晚早些休息，可以听一些舒缓轻音乐，明天会更好'
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
      todayWeather: null,
      weatherList: []
    }
  },
  computed: {
    currentTheme() {
      const weather = this.weatherList[this.currentIndex]
      if (!weather) return WEATHER_THEMES.cloudy
      const code = weather.weatherCode || 'cloudy'
      return WEATHER_THEMES[code] || WEATHER_THEMES.cloudy
    },
    cssBgStyle() {
      return `background: linear-gradient(145deg, ${this.currentTheme.bg1} 0%, ${this.currentTheme.bg2} 50%, ${this.currentTheme.bg3} 100%);`;
    }
  },
  onLoad() {
    this.initSystemInfo()
    this.loadWeatherData()
  },
  onReady() {
    this.ctx = uni.createCanvasContext('weatherCanvas', this)
  },
  onHide() {
    this.stopAnimation()
  },
  onShow() {
    this.syncTabBarSelected()
    this.loadWeatherData()
  },
  onUnload() {
    this.stopAnimation()
  },
  methods: {
    syncTabBarSelected() {
      this.$nextTick(() => {
        const tabBar = this.$refs && this.$refs.customTabBar
        if (tabBar && typeof tabBar.syncSelectedByRoute === 'function') {
          tabBar.syncSelectedByRoute()
        }
      })
    },
    initSystemInfo() {
      const sysInfo = uni.getSystemInfoSync()
      this.canvasWidth = sysInfo.windowWidth
      this.canvasHeight = sysInfo.windowHeight
    },
    async loadWeatherData() {
      this.loading = true
      try {
        const todayRes = await getMoodWeatherToday()
        if (todayRes.data) {
          this.todayWeather = todayRes.data
          this.buildWeatherList(todayRes.data)
        } else {
          this.weatherList = [this.createEmptyWeather()]
        }
        this.initCanvasEffect()
      } catch (e) {
        console.error('加载天气数据失败', e)
        this.weatherList = [this.createEmptyWeather()]
      } finally {
        this.loading = false
      }
    },
    buildWeatherList(snapshot) {
      const now = new Date()
      const todayStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
      
      const weatherCode = snapshot.weatherCode || 'cloudy'
      const primaryEmotion = this.parseCombinedVector(snapshot.combinedVector).primaryEmotion || 'calm'
      
      const today = {
        date: todayStr,
        type: WEATHER_TYPES[weatherCode] || 'fog',
        title: WEATHER_TITLES[weatherCode] || '多云',
        theme: WEATHER_THEMES[weatherCode] || WEATHER_THEMES.cloudy,
        aiSummary: snapshot.aiSummary || this.getEmotionSuggest(primaryEmotion),
        params: {
          temp: this.estimateTemp(weatherCode),
          pressure: this.estimatePressure(weatherCode),
          wind: snapshot.windSpeed || 3,
          humidity: this.estimateHumidity(weatherCode, snapshot.rainIntensity)
        },
        lastRecord: this.formatRecordTime(snapshot.recordTime),
        aiSuggest: this.getEmotionSuggest(primaryEmotion),
        trendIcon: snapshot.recordCount > 1 ? '📈' : '📍',
        trendTxt: snapshot.recordCount > 1 ? '今日多记录' : '今日单次',
        isToday: true,
        snapshot: snapshot
      }
      
      this.weatherList = [today]
      this.currentIndex = 0
    },
    parseCombinedVector(vectorJson) {
      if (!vectorJson) return {}
      try {
        return typeof vectorJson === 'string' ? JSON.parse(vectorJson) : vectorJson
      } catch {
        return {}
      }
    },
    createEmptyWeather() {
      return {
        date: this.getTodayString(),
        type: 'fog',
        title: '等待天气',
        theme: WEATHER_THEMES.cloudy,
        aiSummary: '今日暂无心情记录，去记录一条吧',
        params: { temp: '--', pressure: '--', wind: '--', humidity: '--' },
        lastRecord: '--',
        aiSuggest: '点击下方按钮，记录你的第一份心情',
        trendIcon: '🌱',
        trendTxt: '开始旅程',
        isToday: true
      }
    },
    getTodayString() {
      const now = new Date()
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    },
    formatRecordTime(time) {
      if (!time) return '暂无记录'
      const d = new Date(time)
      return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
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
    getEmotionSuggest(emotion) {
      return EMOTION_SUGGESTS[emotion] || EMOTION_SUGGESTS.default
    },
    onSwiperChange(e) {
      this.currentIndex = e.detail.current
      this.initCanvasEffect()
    },
    initCanvasEffect() {
      if (this.animFrameId) clearInterval(this.animFrameId)
      this.particles = []
      const activeType = this.weatherList[this.currentIndex]?.type || 'fog'
      
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
    goRecord() {
      this.$tab.switchTab('/pages/record/index')
    },
    goDetail(item) {
      if (item.snapshot && item.snapshot.recordId) {
        this.$tab.navigateTo(`/pages/record/result?recordId=${item.snapshot.recordId}`)
      } else {
        this.fetchLatestRecordAndNavigate()
      }
    },
    async fetchLatestRecordAndNavigate() {
      try {
        const res = await getLatestMoodRecord()
        const latestRecord = res.data
        if (latestRecord && latestRecord.id) {
          this.$tab.navigateTo(`/pages/record/result?recordId=${latestRecord.id}`)
        } else {
          uni.showToast({ title: '暂无详细记录，请先记录心情', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '获取记录失败', icon: 'none' })
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

/* Loading Overlay */
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

/* Base gradients and clouds */
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
  animation-delay: 0s;
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

/* Canvas Layer */
.bg-canvas {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none; /* 让它仅仅呈现视觉，不挡住上面元素的滑动事件 */
}

/* Swiper Content */
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

.top-spacer { height: 12vh; }
.bottom-spacer { height: 16vh; }

/* typography */
.date-header { text-align: center; margin-bottom: 24rpx; opacity: 0.7; border-radius: 20rpx; }
.date-text { font-size: 26rpx; letter-spacing: 4rpx; background: rgba(0,0,0,0.1); padding: 8rpx 20rpx; border-radius: 30rpx; }

.hero-section {
  text-align: center;
  margin-bottom: 60rpx;
  margin-top: 20rpx;
}
.weather-title {
  font-size: 88rpx;
  font-weight: 300;
  letter-spacing: 8rpx;
  text-shadow: 0 6rpx 20rpx rgba(0,0,0,0.15);
}

/* Glass Card Global */
.glass-card {
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 40rpx;
  padding: 36rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 20rpx 40rpx rgba(0,0,0,0.08), inset 0 0 20rpx rgba(255,255,255,0.1);
  transition: all 0.3s ease;
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

/* Grid */
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
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.06);
}
.p-val { font-size: 56rpx; font-weight: 300; font-family: "DIN Condensed", -apple-system, sans-serif; text-shadow: 0 4rpx 10rpx rgba(0,0,0,0.1);}
.p-unit { font-size: 24rpx; margin-left: 8rpx; opacity: 0.8; font-family: sans-serif; }
.p-lbl { font-size: 26rpx; opacity: 0.8; margin-top: 12rpx; letter-spacing: 2rpx; }

/* Row Cards */
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
  padding: 20rpx 0;
}
.trend-icon { font-size: 64rpx; margin: 16rpx 0; text-shadow: 0 6rpx 12rpx rgba(0,0,0,0.1); }
.trend-label { font-size: 26rpx; opacity: 0.9; }

/* Last record */
.last-record-text {
  text-align: center;
  font-size: 26rpx;
  opacity: 0.6;
  margin-bottom: 50rpx;
  font-weight: 300;
}

/* Action Area */
.actions-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.record-btn-core {
  width: 220rpx;
  height: 220rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 40rpx rgba(255, 255, 255, 0.4), inset 0 0 30rpx #fff;
  border: 4rpx solid #fff;
  animation: pulse-glow 3s infinite ease-in-out;
  transition: transform 0.2s;
  margin-bottom: 50rpx;
}
.record-btn-core:active { transform: scale(0.92); }
.record-text {
  font-size: 32rpx; color: #DB7093; font-weight: 600; letter-spacing: 4rpx;
}

@keyframes pulse-glow {
  0% { box-shadow: 0 0 0 0 rgba(255,255,255,0.6); }
  70% { box-shadow: 0 0 0 40rpx rgba(255,255,255,0); }
  100% { box-shadow: 0 0 0 0 rgba(255,255,255,0); }
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
.detail-btn:active { background: rgba(255, 255, 255, 0.3); }
</style>
