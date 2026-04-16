<template>
  <view class="login-page">
    <view class="bg-glow"></view>
    
    <view class="wechat-login-container">
      <view class="logo-section">
        <view class="logo-wrapper">
          <image class="logo" :src="globalConfig.appInfo.logo" mode="aspectFill"></image>
        </view>
        <text class="title">MoodSphere</text>
        <view class="subtitle">让情绪像天气一样被看见</view>
      </view>

      <view class="action-card">
        <view class="desc">记录你的第一场情绪气象</view>
        
        <view class="action-btn">
          <button @click="handleWechatLogin" class="login-btn">
            <text class="cuIcon-weixin icon"></text>
            微信一键登录
          </button>
        </view>
        
        <view class="agreement-section">
          <label class="checkbox-wrapper" @click="isAgreed = !isAgreed">
            <checkbox :checked="isAgreed" color="#7BCBCA" style="transform:scale(0.7)" />
            <text class="text-grey">我已阅读并同意</text>
          </label>
          <view class="links">
            <text @click="handleUserAgreement" class="text-blue">《用户服务协议》</text>
            <text class="divider">&</text>
            <text @click="handlePrivacy" class="text-blue">《隐私协议》</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="footer-tips">探索内心世界的经纬度</view>
  </view>
</template>

<script>
import { getToken } from '@/utils/auth'

export default {
  data() {
    return {
      globalConfig: getApp().globalData.config,
      isAgreed: false // 新增协议勾选状态
    }
  },
  onLoad() {
    if (getToken()) {
      this.$tab.switchTab('/pages/weather/index')
    }
  },
  methods: {
    handlePrivacy() {
      this.$tab.navigateTo('/sub-auth/privacy')
    },
    handleUserAgreement() {
      this.$tab.navigateTo('/sub-auth/terms')
    },
    handleWechatLogin() {
      if (!this.isAgreed) {
        this.$modal.msg('请先阅读并勾选协议')
        return
      }
      
      // #ifndef MP-WEIXIN
      this.$modal.msg('请在微信小程序环境使用')
      return
      // #endif

      this.$modal.loading('连接气象站...')
      uni.login({
        provider: 'weixin',
        success: res => {
          if (!res.code) {
            this.$modal.closeLoading()
            this.$modal.msgError('未获取到凭证')
            return
          }
          this.doWechatLogin(res.code)
        },
        fail: () => {
          this.$modal.closeLoading()
          this.$modal.msgError('授权失败')
        }
      })
    },
    doWechatLogin(code) {
      this.$store.dispatch('Login', { code }).then(() => {
        return this.$store.dispatch('GetInfo')
      }).then(() => {
        this.$modal.closeLoading()
        this.$tab.switchTab('/pages/weather/index')
      }).catch(error => {
        this.$modal.closeLoading()
        const message = (error && (error.msg || error.message)) ? (error.msg || error.message) : '登录失败，请重试'
        this.$modal.msgError(message)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background-color: #f8fbff;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 背景光晕动效 */
.bg-glow {
  position: absolute;
  top: -100rpx;
  right: -100rpx;
  width: 600rpx;
  height: 600rpx;
  background: radial-gradient(circle, rgba(123, 203, 202, 0.2) 0%, rgba(255, 255, 255, 0) 70%);
  filter: blur(50rpx);
  animation: pulse 8s infinite alternate;
}

@keyframes pulse {
  from { transform: scale(1); opacity: 0.5; }
  to { transform: scale(1.2); opacity: 0.8; }
}

.wechat-login-container {
  width: 100%;
  z-index: 1;
  padding: 0 60rpx;
}

.logo-section {
  margin-top: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .logo-wrapper {
    width: 160rpx;
    height: 160rpx;
    background: #fff;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 20rpx 40rpx rgba(123, 203, 202, 0.15);
    
    .logo {
      width: 100rpx;
      height: 100rpx;
    }
  }
  
  .title {
    margin-top: 40rpx;
    font-size: 48rpx;
    font-weight: 700;
    color: #2c3e50;
    letter-spacing: 2rpx;
  }
  
  .subtitle {
    margin-top: 16rpx;
    font-size: 26rpx;
    color: #95a5a6;
    letter-spacing: 4rpx;
  }
}

.action-card {
  margin-top: 120rpx;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 48rpx;
  padding: 60rpx 40rpx;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.03);

  .desc {
    text-align: center;
    color: #7f8c8d;
    font-size: 28rpx;
    margin-bottom: 60rpx;
  }
}

.login-btn {
  width: 100%;
  height: 100rpx;
  line-height: 100rpx;
  background: linear-gradient(135deg, #7bcbca 0%, #4facfe 100%);
  color: #fff;
  border: none;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 24rpx rgba(79, 172, 254, 0.3);
  
  &:active {
    transform: scale(0.98);
    opacity: 0.9;
  }
  
  .icon {
    font-size: 40rpx;
    margin-right: 16rpx;
  }
}

.agreement-section {
  margin-top: 40rpx;
  
  .checkbox-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #bdc3c7;
  }
  
  .links {
    margin-top: 10rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 24rpx;
    
    .text-blue {
      color: #7bcbca;
      font-weight: 500;
    }
    
    .divider {
      color: #eee;
      margin: 0 10rpx;
    }
  }
}

.footer-tips {
  position: absolute;
  bottom: 60rpx;
  font-size: 22rpx;
  color: #dcdde1;
  letter-spacing: 10rpx;
  text-transform: uppercase;
}
</style>