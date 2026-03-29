<template>
  <view class="weather-page">
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
  </view>
</template>

<script>
// 这里可以按需引入 API，例如 import { getMoodWeatherList } from '@/api/mood'

export default {
  data() {
    return {
      currentIndex: 0,
      canvasWidth: 0,
      canvasHeight: 0,
      animFrameId: null,
      particles: [],
      ctx: null,
      // 全新扩充的强健 Mock 数据骨架
      weatherList: [
        {
          date: '2026-03-29', 
          type: 'sunset',
          title: '微光晚霞',
          theme: { bg1: '#FFDFD3', bg2: '#F5D3E5', bg3: '#D4E4F9', cloud1: '#FFBFA3', cloud2: '#E2C2F4', cloud3: '#BFE4FA' },
          aiSummary: '你今天有些疲惫，也带着一点想被理解的期待。微光透过晚霞，希望能照亮你的心房。',
          params: { temp: '26', pressure: '1012', wind: '3.6', humidity: '45' },
          lastRecord: '今天 18:30',
          aiSuggest: '建议今晚早些休息，可以听一些舒缓轻音乐，或者看一本轻松的书，明天会更好。',
          trendIcon: '📈',
          trendTxt: '逐步向好',
          isToday: true,
        },
        {
          date: '2026-03-28', 
          type: 'rainstorm',
          title: '骤雨风暴',
          theme: { bg1: '#2D3748', bg2: '#1A202C', bg3: '#4A5568', cloud1: '#4A5568', cloud2: '#2D3748', cloud3: '#1A202C' },
          aiSummary: '昨天经历了情感的起伏，像是一阵风暴席卷而过。允许自己释放，风暴过后会有彩虹。',
          params: { temp: '15', pressure: '998', wind: '22', humidity: '88' },
          lastRecord: '昨天 21:15',
          aiSuggest: '风暴已过，给自己泡杯热茶，深呼吸几次，让情绪渐渐平复下来。',
          trendIcon: '🌧️',
          trendTxt: '波动剧烈',
          isToday: false,
        },
        {
          date: '2026-03-27', 
          type: 'fog',
          title: '静夜薄雾',
          theme: { bg1: '#A0AEC0', bg2: '#CBD5E0', bg3: '#E2E8F0', cloud1: '#E2E8F0', cloud2: '#EDF2F7', cloud3: '#F7FAFC' },
          aiSummary: '前天有一股平静且朦胧的思绪，像轻纱薄雾，不张扬却又挥之不去。',
          params: { temp: '18', pressure: '1008', wind: '1.2', humidity: '65' },
          lastRecord: '前天 23:40',
          aiSuggest: '如果你感到迷茫，不要急于寻找答案，有时候在薄雾中静行，也是一种沉淀。',
          trendIcon: '☁️',
          trendTxt: '平缓低沉',
          isToday: false,
        }
      ]
    }
  },
  computed: {
    currentTheme() {
      return this.weatherList[this.currentIndex] ? this.weatherList[this.currentIndex].theme : this.weatherList[0].theme;
    },
    cssBgStyle() {
      return `background: linear-gradient(145deg, ${this.currentTheme.bg1} 0%, ${this.currentTheme.bg2} 50%, ${this.currentTheme.bg3} 100%);`;
    }
  },
  onLoad() {
    this.initSystemInfo();
  },
  onReady() {
    this.ctx = uni.createCanvasContext('weatherCanvas', this);
    this.initCanvasEffect();
  },
  onHide() {
    if (this.animFrameId) {
      clearInterval(this.animFrameId);
    }
  },
  onShow() {
    if (this.ctx && this.weatherList) {
      this.initCanvasEffect();
    }
  },
  onUnload() {
    if (this.animFrameId) {
      clearInterval(this.animFrameId);
    }
  },
  methods: {
    initSystemInfo() {
      const sysInfo = uni.getSystemInfoSync();
      this.canvasWidth = sysInfo.windowWidth;
      this.canvasHeight = sysInfo.windowHeight;
    },
    onSwiperChange(e) {
      this.currentIndex = e.detail.current;
      this.initCanvasEffect();
    },
    initCanvasEffect() {
      if (this.animFrameId) clearInterval(this.animFrameId);
      this.particles = [];
      const activeType = this.weatherList[this.currentIndex].type;
      
      if (activeType === 'sunset' || activeType === 'fog') {
        // 柔和粒子飘升
        for (let i = 0; i < 25; i++) {
          this.particles.push({
            x: Math.random() * this.canvasWidth,
            y: Math.random() * this.canvasHeight,
            size: Math.random() * 2.5 + 1.5,
            speedY: -(Math.random() * 0.8 + 0.3),
            alpha: Math.random() * 0.5 + 0.2
          });
        }
      } else if (activeType === 'rainstorm') {
        // 雨滴与闪电储备
        for (let i = 0; i < 80; i++) {
          this.particles.push({
            x: Math.random() * this.canvasWidth,
            y: Math.random() * this.canvasHeight,
            length: Math.random() * 25 + 10,
            speedY: Math.random() * 15 + 15,
            speedX: (Math.random() - 0.5) * 3
          });
        }
      }

      this.animFrameId = setInterval(() => {
        this.drawFrame(activeType);
      }, 1000 / 30); // 30fps
    },
    drawFrame(type) {
      if (!this.ctx) return;
      // 清空画布
      this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

      if (type === 'sunset' || type === 'fog') {
        this.particles.forEach(p => {
          p.y += p.speedY;
          if (p.y < -20) p.y = this.canvasHeight + 20;
          this.ctx.beginPath();
          this.ctx.arc(p.x, p.y, p.size, 0, 2 * Math.PI);
          this.ctx.setFillStyle(`rgba(255, 255, 255, ${p.alpha})`);
          this.ctx.fill();
        });
      } 
      else if (type === 'rainstorm') {
        // 随机闪电
        if (Math.random() < 0.03) {
          this.ctx.setFillStyle('rgba(255, 255, 255, 0.5)');
          this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
        }
        // 绘制雨滴
        this.ctx.setLineWidth(1.5);
        this.ctx.setStrokeStyle('rgba(200, 200, 220, 0.7)');
        this.particles.forEach(p => {
          p.y += p.speedY;
          p.x += p.speedX;
          if (p.y > this.canvasHeight) {
            p.y = -30;
            p.x = Math.random() * this.canvasWidth;
          }
          this.ctx.beginPath();
          this.ctx.moveTo(p.x, p.y);
          this.ctx.lineTo(p.x + p.speedX, p.y + p.length);
          this.ctx.stroke();
        });
      }
      
      // 注意传 false 否则会重复 clear
      this.ctx.draw(false);
    },
    goRecord() {
      this.$tab.switchTab('/pages/record/index');
    },
    goDetail(item) {
      // 占位跳转逻辑，您可在这里绑定真实路由
      uni.showToast({ title: `查看 ${item.date} 的详细分析`, icon: 'none' });
      // this.$tab.navigateTo(`/pages/analysis/index?date=${item.date}`)
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
