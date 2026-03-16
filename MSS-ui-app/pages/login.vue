<template>
  <view class="wechat-login-container">
    <view class="logo-content align-center justify-center flex">
      <image class="logo" :src="globalConfig.appInfo.logo" mode="widthFix"></image>
      <text class="title">MoodSphere</text>
    </view>

    <view class="desc">寰俊鎺堟潈鍚庡嵆鍙竴閿櫥褰?/view>

    <view class="action-btn">
      <button @click="handleWechatLogin" class="login-btn cu-btn block bg-green lg round">寰俊涓€閿櫥褰?/button>
    </view>

    <view class="agreement text-center">
      <text class="text-grey1">鐧诲綍鍗充唬琛ㄥ悓鎰?/text>
      <text @click="handleUserAgreement" class="text-blue">銆婄敤鎴锋湇鍔″崗璁€?/text>
      <text @click="handlePrivacy" class="text-blue">銆婇殣绉佸崗璁€?/text>
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
      this.$tab.reLaunch('/pages/index')
    }
  },
  methods: {
    handlePrivacy() {
      const site = this.globalConfig.appInfo.agreements[0]
      this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
    },
    handleUserAgreement() {
      const site = this.globalConfig.appInfo.agreements[1]
      this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
    },
    handleWechatLogin() {
      // #ifndef MP-WEIXIN
      this.$modal.msg('璇峰湪寰俊灏忕▼搴忕幆澧冧娇鐢ㄥ井淇′竴閿櫥褰?)
      return
      // #endif

      this.$modal.loading('鐧诲綍涓紝璇风◢鍊?..')
      uni.login({
        provider: 'weixin',
        success: res => {
          if (!res.code) {
            this.$modal.closeLoading()
            this.$modal.msgError('鏈幏鍙栧埌寰俊鐧诲綍鍑瘉')
            return
          }
          this.doWechatLogin(res.code)
        },
        fail: () => {
          this.$modal.closeLoading()
          this.$modal.msgError('寰俊鐧诲綍鎺堟潈澶辫触')
        }
      })
    },
    doWechatLogin(code) {
      this.$store.dispatch('Login', { code }).then(() => {
        return this.$store.dispatch('GetInfo')
      }).then(() => {
        this.$modal.closeLoading()
        this.$tab.reLaunch('/pages/index')
      }).catch(error => {
        this.$modal.closeLoading()
        const message = (error && (error.msg || error.message)) ? (error.msg || error.message) : '寰俊鐧诲綍澶辫触锛岃绋嶅悗閲嶈瘯'
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
