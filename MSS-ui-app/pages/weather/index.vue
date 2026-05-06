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
          
          <view
            class="content-wrapper"
            :class="{
              'content-wrapper--empty': !item.hasRecord,
              'content-wrapper--dark-text': shouldUseDarkText(item)
            }"
          >
             <!-- Top Data Header -->
             <view class="date-header">
               <text class="date-text">{{ item.date }} {{ item.isToday ? ' (今日)' : '' }}</text>
             </view>

             <!-- Main Visual Header -->
             <view class="hero-section">
               <text class="weather-title">{{ item.title }}</text>
             </view>

             <!-- Empty State -->
             <view v-if="!item.hasRecord" class="glass-card empty-state-card">
               <view class="empty-mark">♡</view>
               <text class="empty-title">心境天气还未生成</text>
               <text class="empty-text">完成一次心情记录后，这里会呈现你的今日心境、情绪指标和分析入口。</text>
             </view>

             <!-- AI Summary Card -->
             <view v-else class="glass-card ai-card">
               <view class="card-title-row">
                 <text class="card-icon">✧</text>
                 <text class="card-title">AI 情绪摘要</text>
               </view>
               <text class="ai-text">{{ item.aiSummary }}</text>
             </view>

             <!-- Weather Parameter Grid -->
             <view v-if="item.hasRecord" class="params-grid">
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
             <view v-if="item.hasRecord" class="row-cards">
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
             <view v-if="item.hasRecord" class="last-record-text">上次记录时间：{{ item.lastRecord }}</view>
             
             <!-- Action Buttons -->
             <view class="actions-area">
               <button class="action-btn action-btn--ghost" @click="goTodaySnapshot">今日快照</button>
               <button
                 class="action-btn action-btn--primary"
                 :class="{ 'action-btn--disabled': !item.recordId }"
                 @click="goDetail(item)"
               >详细分析</button>
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
import { getMoodWeatherToday } from '@/api/mood'

const WEATHER_THEMES = {
  empty: { bg1: '#F7FBFF', bg2: '#EAF2FF', bg3: '#FFFFFF', cloud1: '#FFFFFF', cloud2: '#CFE1FF', cloud3: '#EAF2FF' },
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
  tired: '疲惫感出现时，可以先降低任务密度，给身体一点真实的休息',
  sad: '允许自己感受情绪，可以听些舒缓的音乐或与朋友倾诉',
  irritable: '情绪有些急躁，建议暂时离开让你烦躁的环境',
  lonely: '孤独感来袭，可以主动联系朋友或家人',
  wronged: '委屈感值得被看见，可以先写下发生了什么，再决定是否表达',
  expect: '期待感正在升起，适合把想做的事拆成一个容易开始的小步骤',
  warm: '温暖感很珍贵，可以把它留给自己，也可以传递给在意的人',
  confused: '困惑时不急着马上判断，先列出你确定和不确定的部分',
  hopeful: '希望感是很好的能量，适合给今天安排一个轻巧但明确的行动',
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
      dynamicWeather: null,
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
  },
  onReady() {
    this.ctx = uni.createCanvasContext('weatherCanvas', this)
  },
  onHide() {
    this.stopAnimation()
  },
  onShow() {
    this.$store.dispatch('setTabBarSelected', 0)
    this.loadWeatherData()
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
    async loadWeatherData() {
      this.loading = true
      try {
        const dynamicRes = await getMoodWeatherToday()
        this.dynamicWeather = dynamicRes.data || null
        this.buildWeatherList(this.dynamicWeather)
        this.initCanvasEffect()
      } catch (e) {
        console.error('加载天气数据失败', e)
        this.weatherList = [this.createEmptyDynamicWeather()]
      } finally {
        this.loading = false
      }
    },
    buildWeatherList(dynamicMapping) {
      this.weatherList = dynamicMapping ? [this.createDynamicWeatherCard(dynamicMapping)] : [this.createEmptyDynamicWeather()]
      this.currentIndex = 0
    },
    createEmptyDynamicWeather() {
      return {
        date: this.getTodayString(),
        type: 'fog',
        weatherCode: 'empty',
        title: '等待心境',
        theme: WEATHER_THEMES.empty,
        aiSummary: '完成一次心情记录后，这里会生成你的今日心境天气。',
        params: { temp: '--', pressure: '--', wind: '--', humidity: '--' },
        lastRecord: '--',
        aiSuggest: '记录后将展示你的情绪趋势和治愈建议。',
        trendIcon: '🌱',
        trendTxt: '开始旅程',
        isToday: true,
        recordId: null,
        hasRecord: false
      }
    },
    createDynamicWeatherCard(mapping) {
      const weatherCode = mapping.weatherCode || 'cloudy'
      return {
        date: this.getTodayString(),
        type: WEATHER_TYPES[weatherCode] || 'fog',
        weatherCode: weatherCode,
        title: mapping.weatherName || WEATHER_TITLES[weatherCode] || '多云',
        theme: WEATHER_THEMES[weatherCode] || WEATHER_THEMES.cloudy,
        aiSummary: this.getUserFacingSummary(mapping.aiSummary, weatherCode, mapping.primaryEmotion),
        params: {
          temp: this.estimateTemp(weatherCode),
          pressure: this.estimatePressure(weatherCode),
          wind: mapping.windSpeed || 3,
          humidity: this.estimateHumidity(weatherCode, mapping.rainIntensity)
        },
        lastRecord: this.formatRecordTime(mapping.updateTime || mapping.createTime),
        aiSuggest: this.getEmotionSuggest(mapping.primaryEmotion),
        trendIcon: '📍',
        trendTxt: '今日最新一条',
        isToday: true,
        recordId: mapping.recordId || mapping.id || null,
        hasRecord: true
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
    getUserFacingSummary(rawSummary, weatherCode, primaryEmotion) {
      if (rawSummary && !this.isReportLikeSummary(rawSummary)) {
        return rawSummary
      }
      return this.buildWeatherSummary(weatherCode, primaryEmotion)
    },
    isReportLikeSummary(summary) {
      const text = String(summary || '')
      const reportWords = ['用户表达', '心理风险', '强度', '/10', '健康状态', '自然状态', '诊断', '属于', '无风险', '低风险']
      return reportWords.some(word => text.includes(word))
    },
    buildWeatherSummary(weatherCode, primaryEmotion) {
      const emotionText = this.getEmotionLabel(primaryEmotion)
      const summaries = {
        sunny: `今天的你像一段明亮的晴天，${emotionText}来得很自然。可以把这份轻盈记下来，留给之后的自己回看。`,
        breeze: `今天的你像被微风轻轻托住，${emotionText}里有一点稳定感。慢一点整理节奏，也是一种照顾。`,
        cloudy: `今天的你心里有些云层，${emotionText}不一定很尖锐，却值得被看见。先不用急着把答案想清楚。`,
        rain: `今天的你像走在一场细雨里，${emotionText}需要被温柔接住。允许自己慢一点，也是一种照顾。`,
        storm: `今天的你心里风声有些重，${emotionText}让注意力变得紧绷。先给自己一个缓冲，再处理具体问题。`,
        mist: `今天的你像站在薄雾里，${emotionText}让方向感变慢。别急着下结论，先陪自己待一会儿。`
      }
      return summaries[weatherCode] || '今天的你已经留下了一份心境天气。它不需要被立刻解释，只需要被温柔地看见。'
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
    goTodaySnapshot() {
      this.$tab.navigateTo('/pages/weather/snapshot')
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
    goDetail(item) {
      if (item.recordId) {
        this.$tab.navigateTo(`/pages/record/result?recordId=${item.recordId}`)
      } else {
        uni.showToast({ title: '暂无分析数据', icon: 'none' })
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
  background: linear-gradient(145deg, #5C9CE6 0%, #2563EB 100%);
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

.content-wrapper--empty {
  color: #273a63;
}

.content-wrapper--dark-text {
  color: #273a63;
}

.top-spacer { height: 12vh; }
.bottom-spacer { height: 300rpx; }

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

.content-wrapper--empty .date-text {
  color: #5f6f93;
  background: rgba(255, 255, 255, 0.68);
}

.content-wrapper--empty .weather-title {
  color: #2563eb;
  text-shadow: 0 8rpx 22rpx rgba(37, 99, 235, 0.16);
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

.content-wrapper--empty.content-wrapper--dark-text .weather-title {
  color: #2563eb;
  text-shadow: 0 8rpx 22rpx rgba(37, 99, 235, 0.16);
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
.content-wrapper--dark-text .trend-label,
.content-wrapper--dark-text .last-record-text {
  color: #273a63;
  opacity: 1;
  text-shadow: 0 1rpx 0 rgba(255, 255, 255, 0.36);
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

.empty-state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 52rpx 44rpx;
  background: rgba(255, 255, 255, 0.58);
  border-color: rgba(255, 255, 255, 0.86);
  box-shadow: 0 18rpx 44rpx rgba(45, 92, 201, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.72);
}

.empty-mark {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 22rpx;
  color: #ff69b4;
  font-size: 46rpx;
  background: rgba(255, 105, 180, 0.14);
}

.empty-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 18rpx;
}

.empty-text {
  font-size: 28rpx;
  line-height: 1.65;
  color: #60708e;
  text-align: center;
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
  position: fixed;
  left: 36rpx;
  right: 36rpx;
  bottom: calc(env(safe-area-inset-bottom) + 146rpx);
  z-index: 80;
  display: flex;
  align-items: center;
}

.action-btn {
  flex: 1;
  margin: 0;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 48rpx;
  padding: 0;
  box-sizing: border-box;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  white-space: nowrap;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 12rpx 28rpx rgba(45, 92, 201, 0.13);
}

.action-btn + .action-btn {
  margin-left: 18rpx;
}

.action-btn:after {
  display: none;
}

.action-btn--ghost {
  color: #2563eb;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(37, 99, 235, 0.16);
}

.action-btn--primary {
  color: #fff;
  background: #2563eb;
  border: 1px solid rgba(255, 255, 255, 0.46);
  box-shadow: 0 14rpx 32rpx rgba(37, 99, 235, 0.2);
}

.action-btn--disabled {
  opacity: 0.62;
}

.action-btn:active {
  transform: scale(0.98);
}
</style>
