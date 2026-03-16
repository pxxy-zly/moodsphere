import request from '@/utils/request'

// 账号密码登录（兼容保留）
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/login',
    headers: {
      isToken: false
    },
    method: 'post',
    data
  })
}

export function wechatMiniappLogin(data) {
  return request({
    url: '/app/auth/wechat/miniapp/login',
    headers: {
      isToken: false
    },
    method: 'post',
    data
  })
}

export function getWechatMiniappInfo() {
  return request({
    url: '/app/auth/wechat/miniapp/info',
    method: 'get'
  })
}

export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

export function getCodeImg() {
  return request({
    url: '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}
