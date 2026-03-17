<template>
  <view class="wechat-login-container">
    <view class="logo-content align-center justify-center flex">
      <image class="logo" :src="globalConfig.appInfo.logo" mode="widthFix"></image>
      <text class="title">MoodSphere</text>
    </view>

    <view class="desc">微信授权后即可一键登录</view>

    <view class="action-btn">
      <button @click="handleWechatLogin" class="login-btn cu-btn block bg-green lg round">微信一键登录</button>
    </view>

    <view class="agreement text-center">
      <text class="text-grey1">登录即代表同意</text>
      <text @click="handleUserAgreement" class="text-blue">《用户服务协议》</text>
      <text @click="handlePrivacy" class="text-blue">《隐私协议》</text>
    </view>
  </view>
</template>

<script>
import { getToken } from '@/utils/auth'

export default {
  data() {
    return {
      globalConfig: getApp().globalData.config
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
      // #ifndef MP-WEIXIN
      this.$modal.msg('请在微信小程序环境使用微信一键登录')
      return
      // #endif

      this.$modal.loading('登录中，请稍候...')
      uni.login({
        provider: 'weixin',
        success: res => {
          if (!res.code) {
            this.$modal.closeLoading()
            this.$modal.msgError('未获取到微信登录凭证')
            return
          }
          this.doWechatLogin(res.code)
        },
        fail: () => {
          this.$modal.closeLoading()
          this.$modal.msgError('微信登录授权失败')
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
        const message = (error && (error.msg || error.message)) ? (error.msg || error.message) : '微信登录失败，请稍后重试'
        this.$modal.msgError(message)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
page {
  background-color: #ffffff;
}

.wechat-login-container {
  width: 100%;
  padding-top: 20%;

  .logo-content {
    width: 100%;
    font-size: 21px;
    text-align: center;

    .logo {
      width: 100rpx;
      height: 100rpx;
      border-radius: 8rpx;
    }

    .title {
      margin-left: 12rpx;
      font-weight: 600;
    }
  }

  .desc {
    margin-top: 36rpx;
    text-align: center;
    color: #666;
    font-size: 28rpx;
  }

  .action-btn {
    margin: 120rpx auto 0;
    width: 80%;

    .login-btn {
      height: 88rpx;
      line-height: 88rpx;
      font-size: 32rpx;
    }
  }

  .agreement {
    margin-top: 48rpx;
    color: #666;
    font-size: 24rpx;

    .text-blue {
      margin: 0 8rpx;
    }
  }
}
</style>