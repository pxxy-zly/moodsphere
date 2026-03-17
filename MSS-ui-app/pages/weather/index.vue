<template>
  <view class="weather-page">
    <view class="header">情绪天气</view>
    <view class="card">
      <text class="city">当前用户：{{ name || '未命名用户' }}</text>
      <text class="weather">今日心情天气：晴（模拟）</text>
      <text class="temp">体感温度：26℃（模拟）</text>
    </view>

    <button class="action-btn" @click="refreshInfo">刷新用户信息</button>
    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </view>
</template>

<script>
export default {
  data() {
    return {
      name: ''
    }
  },
  onShow() {
    this.syncFromStore()
  },
  methods: {
    syncFromStore() {
      this.name = this.$store.getters.name
    },
    refreshInfo() {
      this.$store.dispatch('GetInfo').then(() => {
        this.syncFromStore()
        this.$modal.msgSuccess('用户信息已更新')
      }).catch(() => {
        this.$modal.msgError('获取用户信息失败')
      })
    },
    handleLogout() {
      this.$modal.confirm('确定退出登录吗？').then(() => {
        this.$store.dispatch('LogOut').finally(() => {
          this.$tab.reLaunch('/pages/auth/login')
        })
      })
    }
  }
}
</script>

<style scoped>
.weather-page {
  min-height: 100vh;
  padding: 40rpx;
  background: linear-gradient(180deg, #eef6ff 0%, #ffffff 100%);
}

.header {
  font-size: 44rpx;
  font-weight: 600;
  color: #1f2d3d;
}

.card {
  margin-top: 40rpx;
  padding: 36rpx;
  border-radius: 24rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(25, 137, 250, 0.12);
  display: flex;
  flex-direction: column;
}

.city {
  font-size: 30rpx;
  color: #333;
}

.weather {
  margin-top: 16rpx;
  font-size: 36rpx;
  color: #1989fa;
  font-weight: 600;
}

.temp {
  margin-top: 16rpx;
  font-size: 28rpx;
  color: #666;
}

.action-btn,
.logout-btn {
  margin-top: 30rpx;
  border-radius: 50rpx;
  font-size: 30rpx;
}

.action-btn {
  margin-top: 80rpx;
  background: #1989fa;
  color: #fff;
}

.logout-btn {
  background: #f56c6c;
  color: #fff;
}
</style>