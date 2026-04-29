<template>
  <view class="mss-tabbar-container">
    <view class="mss-tabbar-shell">
      <view class="mss-weather-blur"></view>
      <view class="mss-weather-blur blur-two"></view>

      <view class="mss-tabbar-row">
        <view
          v-for="(item, index) in tabs"
          :key="item.pagePath"
          class="mss-tab-item"
          :class="{ 'is-center': item.center }"
          @tap="switchTab(item, index)"
        >
          <view v-if="item.center" class="mss-center-wrapper">
            <view class="mss-center-glow" :class="{ active: selected === index }"></view>
            <view class="mss-center-btn" :class="{ active: selected === index }">
              <image class="mss-center-icon" :src="selected === index ? item.selectedIconPath : item.iconPath" mode="aspectFit"></image>
            </view>
            <text class="mss-tab-label center" :class="{ active: selected === index }">{{ item.text }}</text>
          </view>

          <view v-else class="mss-normal-wrapper">
            <view class="mss-icon-bubble" :class="{ active: selected === index }">
              <image
                class="mss-icon-image"
                :src="selected === index ? item.selectedIconPath : item.iconPath"
                mode="aspectFit"
              ></image>
            </view>
            <text class="mss-tab-label" :class="{ active: selected === index }">{{ item.text }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      tabs: [
        {
          pagePath: '/pages/weather/index',
          text: '天气',
          iconPath: '/static/images/tabbar/weather.svg',
          selectedIconPath: '/static/images/tabbar/weather.svg'
        },
        {
          pagePath: '/pages/globe/index',
          text: '星球',
          iconPath: '/static/images/tabbar/globe.svg',
          selectedIconPath: '/static/images/tabbar/globe.svg'
        },
        {
          pagePath: '/pages/record/index',
          text: '记录',
          center: true,
          iconPath: '/static/images/tabbar/record.svg',
          selectedIconPath: '/static/images/tabbar/record.svg'
        },
        {
          pagePath: '/pages/report/index',
          text: '报告',
          iconPath: '/static/images/tabbar/report.svg',
          selectedIconPath: '/static/images/tabbar/report.svg'
        },
        {
          pagePath: '/pages/mine/index',
          text: '我的',
          iconPath: '/static/images/tabbar/mine.svg',
          selectedIconPath: '/static/images/tabbar/mine.svg'
        }
      ]
    }
  },
  computed: {
    selected() {
      return this.$store.state.app.tabBarSelected
    }
  },
  methods: {
    switchTab(item, index) {
      if (!item || !item.pagePath) return
      if (this.selected === index) return

      this.$store.dispatch('setTabBarSelected', index)
      uni.switchTab({
        url: item.pagePath
      })
    }
  }
}
</script>

<style scoped>
.mss-tabbar-container {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  padding: 0 22rpx 12rpx;
  padding: 0 22rpx calc(env(safe-area-inset-bottom) + 12rpx);
  pointer-events: none;
}

.mss-tabbar-shell {
  position: relative;
  height: 148rpx;
  border-radius: 46rpx;
  background: rgba(241, 249, 255, 0.56);
  border: 1px solid rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(26px) saturate(130%);
  -webkit-backdrop-filter: blur(26px) saturate(130%);
  box-shadow: 0 16rpx 44rpx rgba(108, 151, 181, 0.18), inset 0 1px 0 rgba(255, 255, 255, 0.6);
  overflow: visible;
  pointer-events: auto;
}

.mss-weather-blur {
  position: absolute;
  width: 300rpx;
  height: 140rpx;
  left: -40rpx;
  top: -40rpx;
  border-radius: 200rpx;
  background: linear-gradient(135deg, rgba(180, 243, 255, 0.5), rgba(218, 239, 255, 0.12));
  filter: blur(30rpx);
  pointer-events: none;
}

.mss-weather-blur.blur-two {
  left: auto;
  right: -30rpx;
  top: -34rpx;
  width: 260rpx;
  background: linear-gradient(145deg, rgba(170, 230, 255, 0.35), rgba(255, 255, 255, 0.16));
}

.mss-tabbar-row {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 0 8rpx 12rpx;
}

.mss-tab-item {
  flex: 1;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-end;
}

.mss-tab-item.is-center {
  transform: translateY(-30rpx);
}

.mss-normal-wrapper,
.mss-center-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.mss-icon-bubble {
  width: 62rpx;
  height: 62rpx;
  border-radius: 31rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.25s ease;
}

.mss-icon-bubble.active {
  background: linear-gradient(135deg, rgba(120, 222, 232, 0.34), rgba(149, 219, 255, 0.35));
  border-color: rgba(118, 210, 233, 0.45);
  box-shadow: 0 8rpx 20rpx rgba(108, 193, 221, 0.22);
}

.mss-center-wrapper {
  position: relative;
}

.mss-center-glow {
  position: absolute;
  width: 128rpx;
  height: 128rpx;
  top: -18rpx;
  border-radius: 64rpx;
  background: radial-gradient(circle, rgba(71, 222, 220, 0.42), rgba(71, 222, 220, 0));
  filter: blur(18rpx);
  opacity: 0.7;
}

.mss-center-glow.active {
  animation: center-breathe 2.7s ease-in-out infinite;
}

.mss-center-btn {
  position: relative;
  z-index: 2;
  width: 108rpx;
  height: 108rpx;
  border-radius: 54rpx;
  background: linear-gradient(145deg, #29d7c7 0%, #57c8ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.86);
  box-shadow: 0 12rpx 30rpx rgba(55, 191, 220, 0.35), inset 0 -8rpx 18rpx rgba(31, 127, 181, 0.22);
}

.mss-center-btn.active {
  animation: center-pulse 2.7s ease-in-out infinite;
}

.mss-icon-image {
  width: 40rpx;
  height: 40rpx;
}

.mss-center-icon {
  width: 48rpx;
  height: 48rpx;
  opacity: 0.96;
}

.mss-tab-label {
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1;
  color: #8c99ad;
  letter-spacing: 1rpx;
}

.mss-tab-label.active {
  color: #228fb4;
}

.mss-tab-label.center {
  margin-top: 10rpx;
  font-size: 23rpx;
  color: #5a7388;
}

.mss-tab-label.center.active {
  color: #1389b0;
}

@keyframes center-breathe {
  0% {
    opacity: 0.62;
    transform: scale(0.94);
  }
  50% {
    opacity: 1;
    transform: scale(1.08);
  }
  100% {
    opacity: 0.62;
    transform: scale(0.94);
  }
}

@keyframes center-pulse {
  0% {
    box-shadow: 0 10rpx 28rpx rgba(55, 191, 220, 0.24), inset 0 -8rpx 18rpx rgba(31, 127, 181, 0.18);
  }
  50% {
    box-shadow: 0 14rpx 36rpx rgba(55, 191, 220, 0.42), inset 0 -8rpx 18rpx rgba(31, 127, 181, 0.26);
  }
  100% {
    box-shadow: 0 10rpx 28rpx rgba(55, 191, 220, 0.24), inset 0 -8rpx 18rpx rgba(31, 127, 181, 0.18);
  }
}
</style>
