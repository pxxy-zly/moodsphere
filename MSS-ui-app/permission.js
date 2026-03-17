import { getToken } from '@/utils/auth'

const loginPage = '/pages/auth/login'
const homePage = '/pages/weather/index'

// 页面白名单
const whiteList = [
  loginPage,
  '/pages/common/webview/index',
  '/sub-auth/privacy',
  '/sub-auth/terms',
  '/sub-auth/forgot',
  '/sub-auth/guide'
]

function checkWhite(url) {
  const path = (url || '').split('?')[0]
  return whiteList.indexOf(path) !== -1
}

const interceptors = ['navigateTo', 'redirectTo', 'reLaunch', 'switchTab']
interceptors.forEach(item => {
  uni.addInterceptor(item, {
    invoke(to) {
      const path = (to.url || '').split('?')[0]
      if (getToken()) {
        if (path === loginPage) {
          uni.switchTab({ url: homePage })
          return false
        }
        return true
      }

      if (checkWhite(to.url)) {
        return true
      }
      uni.reLaunch({ url: loginPage })
      return false
    },
    fail(err) {
      console.log(err)
    }
  })
})