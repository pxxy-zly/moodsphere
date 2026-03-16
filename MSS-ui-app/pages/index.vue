<template>
  <view class="home-container">
    <image class="avatar" :src="avatar"></image>
    <view class="nickname">浣犲ソ锛寋{ name || '蹇冩儏浼欎即' }}</view>
    <view class="desc">寰俊涓€閿櫥褰曞凡鎺ュ叆锛堝悗绔綋鍓嶈繑鍥炴ā鎷熸暟鎹級</view>

    <button class="action-btn refresh-btn" @click="refreshInfo">鍒锋柊鐢ㄦ埛淇℃伅</button>
    <button class="action-btn logout-btn" @click="handleLogout">閫€鍑虹櫥褰?/button>
  </view>
</template>

<script>
export default {
  data() {
    return {
      name: '',
      avatar: '/static/logo.png'
    }
  },
  onShow() {
    this.syncFromStore()
  },
  methods: {
    syncFromStore() {
      this.name = this.$store.getters.name
      this.avatar = this.$store.getters.avatar || '/static/logo.png'
    },
    refreshInfo() {
      this.$store.dispatch('GetInfo').then(() => {
        this.syncFromStore()
        this.$modal.msgSuccess('鐢ㄦ埛淇℃伅宸叉洿鏂?)
      }).catch(() => {
        this.$modal.msgError('鑾峰彇鐢ㄦ埛淇℃伅澶辫触')
      })
    },
    handleLogout() {
      this.$modal.confirm('纭畾閫€鍑虹櫥褰曞悧锛?).then(() => {
        this.$store.dispatch('LogOut').finally(() => {
          this.$tab.reLaunch('/pages/login')
        })
      })
    }
  }
}
</script>

<style scoped>
.home-container {
  padding: 60rpx 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar {
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  margin-top: 80rpx;
}

.nickname {
  margin-top: 36rpx;
  font-size: 40rpx;
  font-weight: 600;
  color: #333;
}

.desc {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #666;
  text-align: center;
}

.action-btn {
  width: 100%;
  margin-top: 32rpx;
  border-radius: 50rpx;
  font-size: 30rpx;
}

.refresh-btn {
  margin-top: 100rpx;
  background: #1989fa;
  color: #fff;
}

.logout-btn {
  background: #f56c6c;
  color: #fff;
}
</style>
