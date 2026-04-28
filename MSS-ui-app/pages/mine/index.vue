<template>
  <view class="mine-page">
    
    <!-- Top Dynamic Gradient Background Card (Represents Current Mood) -->
    <view class="hero-card">
      <view class="profile-section">
        <!-- Avatar -->
        <view class="avatar-wrap">
          <image class="avatar" src="https://api.dicebear.com/7.x/notionists/svg?seed=Felix&backgroundColor=f1f5f9" mode="aspectFill" />
        </view>
        <!-- Nickname -->
        <text class="nickname">克苏鲁的梦境</text>
        <!-- Glowing Badge -->
        <view class="badge-explorer">
          <text class="b-icon">✨</text>
          <text class="b-text">情绪探索者 Lv.3</text>
        </view>
      </view>

      <!-- Stats Row -->
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">128</text>
          <text class="stat-label">记录天数</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">14</text>
          <text class="stat-label">连续打卡</text>
        </view>
      </view>
    </view>

    <!-- My Favorite Weather (Horizontal Scroll) -->
    <view class="section-container">
      <text class="section-title">我的收藏天气</text>
      <scroll-view scroll-x class="weather-scroll" :show-scrollbar="false">
        <view class="scroll-content">
          <view class="weather-item w-sunset">
            <text class="w-icon">🌇</text>
            <text class="w-name">微光晚霞</text>
            <text class="w-date">2026.03.28</text>
          </view>
          <view class="weather-item w-rain">
            <text class="w-icon">🌧️</text>
            <text class="w-name">骤雨风暴</text>
            <text class="w-date">2026.03.20</text>
          </view>
          <view class="weather-item w-fog">
            <text class="w-icon">🌫️</text>
            <text class="w-name">薄雾清晨</text>
            <text class="w-date">2026.03.15</text>
          </view>
          <view class="scroll-pad"></view>
        </view>
      </scroll-view>
    </view>

    <!-- Settings List -->
    <view class="section-container">
      <text class="section-title">系统与偏好</text>
      
      <view class="settings-list">
        <!-- Item 1: Nav -->
        <view class="set-item" @click="mockClick('隐私设置')">
          <view class="set-left">
            <view class="set-icon i-priv"></view>
            <text class="set-label">隐私设置</text>
          </view>
          <text class="set-arrow">›</text>
        </view>
        
        <!-- Item 2: Switch -->
        <view class="set-item">
          <view class="set-left">
            <view class="set-icon i-anon"></view>
            <text class="set-label">匿名投影设置</text>
          </view>
          <switch checked color="#FF9A9E" style="transform: scale(0.85); margin-right: -10rpx;" />
        </view>

        <!-- Item 3: Analysis -->
        <view class="set-item" @click="mockClick('AI 分析偏好')">
          <view class="set-left">
            <view class="set-icon i-ai"></view>
            <text class="set-label">AI 分析偏好</text>
          </view>
          <view class="set-right">
            <text class="set-val">温柔陪伴</text>
            <text class="set-arrow">›</text>
          </view>
        </view>

        <!-- Item 4: About -->
        <view class="set-item no-border" @click="mockClick('关于')">
          <view class="set-left">
            <view class="set-icon i-about"></view>
            <text class="set-label">关于情绪星球</text>
          </view>
          <text class="set-arrow">›</text>
        </view>
      </view>
    </view>
    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
export default {
  onShow() {
    this.syncTabBarSelected()
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
    mockClick(name) {
      uni.showToast({
        title: `进入 ${name}`,
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped>
/* Page Layout */
.mine-page {
  min-height: 100vh;
  background: #F8FAFC;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  padding-bottom: 220rpx;
}

/* Hero Card */
.hero-card {
  padding: 120rpx 40rpx 60rpx;
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  border-radius: 0 0 60rpx 60rpx;
  box-shadow: 0 16rpx 40rpx rgba(255, 154, 158, 0.25);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.hero-card::before {
  content: ''; position: absolute; top: -50%; right: -20%;
  width: 100%; height: 100%; border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.25) 0%, transparent 60%);
  pointer-events: none;
}
.hero-card::after {
  content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 50%;
  background: linear-gradient(0deg, rgba(0,0,0,0.05), transparent);
  pointer-events: none;
}

/* Profile Section */
.profile-section {
  display: flex; flex-direction: column; align-items: center; z-index: 2;
}

.avatar-wrap {
  width: 180rpx; height: 180rpx;
  border-radius: 50%;
  padding: 8rpx;
  background: rgba(255, 255, 255, 0.5);
  box-shadow: 0 8rpx 30rpx rgba(0,0,0,0.08);
  margin-bottom: 24rpx;
}
.avatar { width: 100%; height: 100%; border-radius: 50%; background: #fff;}

.nickname {
  font-size: 40rpx; font-weight: 600; color: #FFFFFF;
  margin-bottom: 20rpx; letter-spacing: 2rpx; text-shadow: 0 4rpx 10rpx rgba(0,0,0,0.1);
}

/* Glowing Explorer Badge */
.badge-explorer {
  display: flex; align-items: center;
  padding: 10rpx 28rpx;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 40rpx;
  box-shadow: 0 6rpx 20rpx rgba(255, 255, 255, 0.3), inset 0 2rpx 10rpx rgba(255, 255, 255, 0.7);
}
.b-icon { font-size: 26rpx; margin-right: 12rpx; text-shadow: 0 0 10rpx rgba(255,255,255,0.8);}
.b-text { font-size: 24rpx; color: #FFF; font-weight: 600; letter-spacing: 2rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);}

/* Stats Row */
.stats-row {
  display: flex; align-items: center; justify-content: center;
  width: 100%; margin-top: 60rpx; z-index: 2;
}
.stat-item {
  display: flex; flex-direction: column; align-items: center;
  flex: 1;
}
.stat-num {
  font-size: 60rpx; font-weight: 600; color: #FFF; font-family: "DIN Condensed", -apple-system, sans-serif;
  text-shadow: 0 4rpx 12rpx rgba(0,0,0,0.15); margin-bottom: 8rpx;
}
.stat-label {
  font-size: 24rpx; color: rgba(255, 255, 255, 0.95); font-weight: 400; letter-spacing: 2rpx;
}
.stat-divider {
  width: 2rpx; height: 50rpx; background: rgba(255, 255, 255, 0.35); border-radius: 2rpx;
}

/* Common Section */
.section-container {
  padding: 0 40rpx;
  margin-top: 50rpx;
}
.section-title {
  font-size: 32rpx; font-weight: 600; color: #1E293B; letter-spacing: 2rpx;
  margin-bottom: 30rpx; display: block; padding-left: 8rpx;
}

/* Horizonal Weather Scroll */
.weather-scroll {
  width: 100vw; 
  margin-left: -40rpx; /* breakout of padding */
  white-space: nowrap;
  padding-bottom: 24rpx; 
}
.scroll-content {
  padding: 0 40rpx;
  display: inline-flex;
}

.weather-item {
  display: inline-flex; flex-direction: column; justify-content: center; align-items: flex-start;
  width: 250rpx; height: 300rpx;
  border-radius: 40rpx;
  padding: 36rpx; box-sizing: border-box;
  margin-right: 28rpx;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 16rpx 40rpx rgba(136, 152, 170, 0.06);
  position: relative; overflow: hidden;
  transition: transform 0.2s;
}
.weather-item:active { transform: scale(0.96); }

/* Internal fake gradient background for aesthetic depth */
.weather-item::before {
  content:''; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  opacity: 0.12; z-index: 0; pointer-events: none;
}
.w-sunset::before { background: linear-gradient(135deg, #FF9A9E, #FECFEF); }
.w-rain::before { background: linear-gradient(135deg, #A1C4FD, #C2E9FB); }
.w-fog::before { background: linear-gradient(135deg, #E2E8F0, #F8FAFC); filter: blur(4rpx);}

.w-icon { font-size: 64rpx; margin-bottom: 24rpx; position: relative; z-index: 1;}
.w-name { font-size: 30rpx; font-weight: 600; color: #2D3748; margin-bottom: 12rpx; position: relative; z-index: 1; letter-spacing: 2rpx;}
.w-date { font-size: 24rpx; color: #94A3B8; font-weight: 500; font-family: monospace; position: relative; z-index: 1;}

.scroll-pad { width: 12rpx; display: inline-block; }

/* Settings List */
.settings-list {
  background: #FFFFFF;
  border-radius: 40rpx;
  padding: 16rpx 40rpx;
  box-shadow: 0 16rpx 48rpx rgba(136, 152, 170, 0.05);
}
.set-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 36rpx 0;
  border-bottom: 1px solid #F1F5F9;
}
.set-item:active { opacity: 0.7; }
.no-border { border-bottom: none !important; }

.set-left { display: flex; align-items: center; }

/* CSS drawn minimalist line icons */
.set-icon { margin-right: 28rpx; position: relative; opacity: 0.7; }
/* Privacy Shield */
.i-priv {
  width: 30rpx; height: 34rpx;
  border: 3rpx solid #64748B; border-radius: 4rpx 4rpx 8rpx 8rpx;
  margin-top: 6rpx;
}
.i-priv::before {
  content: ''; position: absolute; top: -12rpx; left: 4rpx; width: 16rpx; height: 12rpx;
  border: 3rpx solid #64748B; border-bottom: none; border-radius: 8rpx 8rpx 0 0;
}
/* Mask/Anonymous */
.i-anon {
  width: 32rpx; height: 32rpx;
  border: 3rpx solid #64748B; border-radius: 50%;
}
.i-anon::after {
  content: ''; position: absolute; top: 10rpx; left: 8rpx; width: 5rpx; height: 5rpx;
  background: #64748B; border-radius: 50%; box-shadow: 12rpx 0 0 #64748B;
}
/* AI Star */
.i-ai {
  width: 24rpx; height: 24rpx;
  border: 3rpx solid #64748B; border-radius: 6rpx;
  transform: rotate(45deg); margin: 6rpx 6rpx 0 6rpx;
}
.i-ai::after {
  content: ''; position: absolute; top: 4rpx; left: 4rpx; right: 4rpx; bottom: 4rpx;
  border: 3rpx solid #64748B; border-radius: 2rpx;
}
/* Info */
.i-about {
  width: 32rpx; height: 32rpx;
  border: 3rpx solid #64748B; border-radius: 50%;
  display: flex; justify-content: center; align-items: center;
}
.i-about::before {
  content: 'i'; font-size: 20rpx; color: #64748B; font-family: serif; font-weight: bold; padding-top: 2rpx;
}

.set-label { font-size: 30rpx; color: #334155; font-weight: 500; letter-spacing: 2rpx;}

.set-right { display: flex; align-items: center; }
.set-val { font-size: 26rpx; color: #94A3B8; margin-right: 16rpx; font-weight: 500;}
.set-arrow { font-size: 34rpx; color: #CBD5E1; font-weight: 300; padding-bottom: 6rpx; }

</style>
