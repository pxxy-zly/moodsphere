<template>
  <view class="globe-page">
    
    <!-- Deep Starry Background -->
    <view class="stars-bg">
      <view class="star" style="top: 10%; left: 20%; animation-duration: 3s;"></view>
      <view class="star" style="top: 30%; left: 80%; animation-duration: 5s; animation-delay: 1s;"></view>
      <view class="star" style="top: 60%; left: 15%; animation-duration: 4s; animation-delay: 2s;"></view>
      <view class="star" style="top: 80%; left: 70%; animation-duration: 6s;"></view>
      <view class="star" style="top: 40%; left: 50%; animation-duration: 3.5s;"></view>
    </view>

    <!-- Top Navigation Toggles -->
    <view class="header-controls">
      <view class="glass-toggle">
        <view class="toggle-item" :class="{ 'active': scope === 'global' }" @click="scope = 'global'">全球</view>
        <view class="toggle-item" :class="{ 'active': scope === 'national' }" @click="scope = 'national'">全国</view>
      </view>
      
      <view class="spacer"></view>

      <view class="glass-toggle">
        <view class="toggle-item" :class="{ 'active': time === 'realtime' }" @click="time = 'realtime'">实时</view>
        <view class="toggle-item" :class="{ 'active': time === 'today' }" @click="time = 'today'">今日</view>
      </view>
    </view>

    <!-- Floating Analysis Card -->
    <view class="analysis-card glass-panel floating-anim">
      <text class="analysis-icon">✨</text>
      <text class="analysis-text">今夜东亚区域呈现轻雾型压抑波动</text>
    </view>

    <!-- 3D Mood Earth -->
    <view class="earth-container">
      <view class="earth-sphere">
        <!-- Inner Rotating Heatmap Spots -->
        <view class="earth-texture">
          <view class="heat-spot spot-orange"></view>
          <view class="heat-spot spot-cyan"></view>
          <view class="heat-spot spot-blue"></view>
          <view class="heat-spot spot-pink"></view>
        </view>
        <!-- Atmosphere Shadow Overlay -->
        <view class="earth-shadow"></view>
      </view>
      <!-- Outer Aura -->
      <view class="earth-aura"></view>
    </view>

    <!-- Jellyfish Bullets (Anonymous Moods) -->
    <view class="bullet-container">
      <view class="bullet bullet-1 glass-panel">
        <text class="b-avatar">🐳</text>
        <text class="b-text">深呼吸，想要逃离一阵子...</text>
        <view class="jelly-tail"></view>
      </view>
      
      <view class="bullet bullet-2 glass-panel">
        <text class="b-avatar">🌻</text>
        <text class="b-text">终于把项目做完了！好累~</text>
        <view class="jelly-tail"></view>
      </view>

      <view class="bullet bullet-3 glass-panel">
        <text class="b-avatar">🍂</text>
        <text class="b-text">一个人发呆的夜，也是自由的。</text>
        <view class="jelly-tail"></view>
      </view>

       <view class="bullet bullet-4 glass-panel">
        <text class="b-avatar">☕</text>
        <text class="b-text">咖啡有点苦，但能清醒面对明天</text>
        <view class="jelly-tail"></view>
      </view>
    </view>

    <!-- Bottom Interaction Hint -->
    <view class="bottom-hint">
      <text class="hint-text">长按星球 释放你的心情共鸣</text>
      <view class="pulse-ring"></view>
    </view>
    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
export default {
  data() {
    return {
      scope: 'global', // global | national
      time: 'today'   // realtime | today
    }
  },
  onShow() {
    this.$store.dispatch('setTabBarSelected', 1)
  }
}
</script>

<style scoped>
/* Page Layout */
.globe-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  background: radial-gradient(circle at 50% 100%, #151C33 0%, #060912 80%, #030409 100%);
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Starry Background */
.stars-bg {
  position: absolute; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}
.star {
  position: absolute;
  width: 6rpx; height: 6rpx;
  background: #fff; border-radius: 50%;
  box-shadow: 0 0 10rpx #fff;
  opacity: 0.1;
  animation: twinkle linear infinite alternate;
}
@keyframes twinkle {
  0% { opacity: 0.1; transform: scale(0.8); }
  100% { opacity: 0.8; transform: scale(1.2); }
}

/* Glass panel utility */
.glass-panel {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.3);
}

/* Header Controls */
.header-controls {
  position: relative;
  z-index: 10;
  width: 100%;
  padding: 100rpx 40rpx 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
}
.spacer { width: 40rpx; }

.glass-toggle {
  display: flex;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 40rpx;
  padding: 8rpx;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.toggle-item {
  padding: 12rpx 36rpx;
  border-radius: 34rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
  transition: all 0.3s ease;
  letter-spacing: 2rpx;
}
.toggle-item.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.2);
}

/* Floating Analysis Card */
.analysis-card {
  position: relative;
  z-index: 10;
  margin-top: 20rpx;
  padding: 24rpx 40rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
}
.analysis-icon { font-size: 32rpx; margin-right: 16rpx; }
.analysis-text { font-size: 26rpx; color: #E2E8F0; letter-spacing: 2rpx; font-weight: 300; }

.floating-anim {
  animation: slowFloat 5s ease-in-out infinite alternate;
}
@keyframes slowFloat {
  0% { transform: translateY(0); }
  100% { transform: translateY(-16rpx); }
}

/* 3D Mood Earth */
.earth-container {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -46%);
  width: 580rpx; height: 580rpx;
  z-index: 5;
}

.earth-sphere {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #1A365D 0%, #0A1128 80%);
  position: relative;
  overflow: hidden; 
  box-shadow: 0 0 60rpx rgba(30, 144, 255, 0.25);
}

/* Top shading for sphere illusion */
.earth-shadow {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  border-radius: 50%;
  background: inset -40rpx -40rpx 100rpx rgba(0, 0, 0, 0.8), 
              inset 20rpx 20rpx 60rpx rgba(255, 255, 255, 0.1);
  box-shadow: inset -60rpx -60rpx 100rpx rgba(0, 0, 0, 0.9),
              inset 30rpx 30rpx 80rpx rgba(0, 255, 255, 0.15);
  pointer-events: none;
  z-index: 3;
}

/* The rotating surface layer */
.earth-texture {
  position: absolute;
  top: -50%; left: -50%;
  width: 200%; height: 200%;
  animation: earthSpin 45s linear infinite;
  z-index: 1;
}
@keyframes earthSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Glowing Map Spots */
.heat-spot {
  position: absolute;
  border-radius: 50%;
  filter: blur(60rpx);
  opacity: 0.85;
}
.spot-orange { background: #FF8C00; width: 400rpx; height: 300rpx; top: 30%; left: 20%; mix-blend-mode: screen; opacity: 0.7;}
.spot-cyan   { background: #00FFFF; width: 350rpx; height: 400rpx; bottom: 20%; right: 20%; opacity: 0.5; }
.spot-blue   { background: #4169E1; width: 500rpx; height: 150rpx; top: 10%; left: 50%; opacity: 0.6; }
.spot-pink   { background: #FF69B4; width: 200rpx; height: 300rpx; bottom: 30%; left: 10%; opacity: 0.4; }

/* Pulse Aura around the Earth */
.earth-aura {
  position: absolute;
  top: -10%; left: -10%;
  width: 120%; height: 120%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 255, 255, 0.1) 0%, transparent 60%);
  z-index: 0;
  pointer-events: none;
  animation: pulseAura 4s infinite alternate ease-in-out;
}
@keyframes pulseAura {
  0% { transform: scale(1); opacity: 0.5; }
  100% { transform: scale(1.1); opacity: 1; }
}

/* Jellyfish Bullets */
.bullet-container {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 6;
  pointer-events: none; /* Let clicks pass to earth */
}

.bullet {
  position: absolute;
  display: flex; align-items: center;
  padding: 16rpx 32rpx;
  border-radius: 50rpx;
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 300;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2), 0 0 20rpx rgba(0, 255, 255, 0.15);
  animation: jellyFloat linear infinite alternate;
}

.b-avatar { font-size: 28rpx; margin-right: 12rpx; opacity: 0.9; }
.b-text { letter-spacing: 2rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.5); }

/* The glowing jellyfish tail */
.jelly-tail {
  position: absolute;
  bottom: -30rpx; left: 50%; transform: translateX(-50%);
  width: 30rpx; height: 50rpx;
  background: radial-gradient(ellipse at top, rgba(255,255,255,0.4), transparent 70%);
  filter: blur(8rpx);
  animation: jellyTailSwish 2s infinite alternate ease-in-out;
}
@keyframes jellyTailSwish {
  0% { transform: translateX(-50%) skewX(-10deg); height: 40rpx; opacity: 0.5; }
  100% { transform: translateX(-50%) skewX(10deg); height: 60rpx; opacity: 0.9; }
}

/* Individual bullet placements */
.bullet-1 { top: 22%; left: -20rpx; animation-duration: 9s; }
.bullet-2 { top: 32%; right: 10rpx; animation-duration: 11s; animation-delay: -3s; }
.bullet-3 { bottom: 25%; left: 30rpx; animation-duration: 10s; animation-delay: -5s; }
.bullet-4 { bottom: 35%; right: -30rpx; animation-duration: 12s; animation-delay: -7s; }

@keyframes jellyFloat {
  0% { transform: translate(0, 0) scale(0.95); }
  100% { transform: translate(30rpx, -50rpx) scale(1.05); }
}

/* Bottom Hint */
.bottom-hint {
  position: absolute;
  bottom: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
  opacity: 0.9;
}
.hint-text {
  font-size: 22rpx;
  letter-spacing: 6rpx;
  color: #83A1DB;
  font-weight: 300;
  margin-bottom: 24rpx;
}
.pulse-ring {
  width: 60rpx; height: 60rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(131, 161, 219, 0.4);
  box-shadow: 0 0 20rpx rgba(131, 161, 219, 0.6);
  animation: ringPulse 2.5s infinite ease-in-out;
}
@keyframes ringPulse {
  0% { transform: scale(0.8); opacity: 1; border-width: 4rpx; }
  100% { transform: scale(2); opacity: 0; border-width: 0rpx; }
}
</style>
