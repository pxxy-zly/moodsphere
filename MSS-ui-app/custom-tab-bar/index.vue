<template>
  <view class="custom-tabbar-wrapper">
    <view class="tab-pill">
      <view
        v-for="(item, index) in tabs"
        :key="item.pagePath"
        class="tab-item"
        :class="{ 'tab-item--active': activeIndex === index }"
        @tap="handleSwitchTab(item, index)"
      >
        <view class="icon-box">
          <image
            class="tab-icon"
            :src="item.iconPath"
            mode="aspectFit"
          ></image>
        </view>

        <view class="text-box" :class="{ 'text-box--active': activeIndex === index }">
          <text class="tab-text">{{ item.text }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    currentPath: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tabs: [
        {
          pagePath: '/pages/weather/index',
          text: '心境',
          iconPath: '/static/images/tabbar/weather.svg'
        },
        {
          pagePath: '/pages/globe/index',
          text: '星球',
          iconPath: '/static/images/tabbar/globe.svg'
        },
        {
          pagePath: '/pages/record/index',
          text: '记录',
          iconPath: '/static/images/tabbar/record.svg'
        },
        {
          pagePath: '/pages/report/index',
          text: '洞察',
          iconPath: '/static/images/tabbar/report.svg'
        },
        {
          pagePath: '/pages/mine/index',
          text: '我的',
          iconPath: '/static/images/tabbar/mine.svg'
        }
      ]
    }
  },
  computed: {
    selected() {
      return this.$store.state.app.tabBarSelected
    },
    activeIndex() {
      if (this.currentPath) {
        const normalizedPath = this.currentPath.startsWith('/') ? this.currentPath : `/${this.currentPath}`
        const index = this.tabs.findIndex(item => item.pagePath === normalizedPath)
        if (index > -1) return index
      }
      return this.selected
    }
  },
  methods: {
    handleSwitchTab(item, index) {
      if (!item || !item.pagePath) return
      if (this.activeIndex === index) return

      this.$store.dispatch('setTabBarSelected', index)
      uni.switchTab({
        url: item.pagePath
      })
    }
  }
}
</script>

<style scoped>
.custom-tabbar-wrapper {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  padding: 0 36rpx calc(env(safe-area-inset-bottom) + 20rpx);
  pointer-events: none;
}

.tab-pill {
  height: 104rpx;
  padding: 14rpx 18rpx;
  border-radius: 60rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.82);
  box-shadow: 0 16rpx 42rpx rgba(45, 92, 201, 0.14), 0 8rpx 24rpx rgba(255, 101, 171, 0.1);
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  pointer-events: auto;
}

.tab-item {
  min-width: 74rpx;
  height: 76rpx;
  padding: 0 18rpx;
  border-radius: 42rpx;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.28s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.tab-item--active {
  min-width: 150rpx;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1), rgba(255, 105, 180, 0.16));
}

.icon-box {
  position: relative;
  z-index: 1;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 48rpx;
}

.tab-icon {
  width: 40rpx;
  height: 40rpx;
  opacity: 0.58;
  filter: grayscale(1);
  transition: all 0.25s ease;
}

.tab-item--active .tab-icon {
  opacity: 1;
  filter: grayscale(0);
  transform: translateY(-1rpx) scale(1.06);
}

.tab-item--active .icon-box::after {
  content: '';
  position: absolute;
  top: -2rpx;
  right: -6rpx;
  z-index: -1;
  width: 22rpx;
  height: 22rpx;
  border-radius: 50%;
  background: #ff69b4;
  box-shadow: 0 4rpx 12rpx rgba(255, 105, 180, 0.32);
  animation: dot-pop 0.28s cubic-bezier(0.18, 0.89, 0.32, 1.24) both;
}

.text-box {
  max-width: 0;
  opacity: 0;
  overflow: hidden;
  white-space: nowrap;
  transition: all 0.28s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.text-box--active {
  max-width: 96rpx;
  opacity: 1;
  margin-left: 10rpx;
}

.tab-text {
  font-size: 27rpx;
  line-height: 1;
  font-weight: 700;
  color: #2563eb;
}

@keyframes dot-pop {
  0% {
    opacity: 0;
    transform: scale(0);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
