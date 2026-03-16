import config from '@/config'
import storage from '@/utils/storage'
import constant from '@/utils/constant'
import { isHttp, isEmpty } from '@/utils/validate'
import { wechatMiniappLogin, logout, getWechatMiniappInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import defAva from '@/static/images/profile.jpg'

const baseUrl = config.baseUrl

function normalizeAvatar(avatar) {
  if (!isHttp(avatar)) {
    return isEmpty(avatar) ? defAva : baseUrl + avatar
  }
  return avatar
}

function commitUserInfo(commit, res) {
  const user = (res && res.user) || {}
  const userId = isEmpty(user.userId) ? '' : user.userId
  const userName = isEmpty(user.nickName) ? (isEmpty(user.userName) ? '' : user.userName) : user.nickName
  const avatar = normalizeAvatar(user.avatar || '')

  if (res && res.roles && res.roles.length > 0) {
    commit('SET_ROLES', res.roles)
    commit('SET_PERMISSIONS', res.permissions || [])
  } else {
    commit('SET_ROLES', ['ROLE_DEFAULT'])
    commit('SET_PERMISSIONS', [])
  }

  commit('SET_ID', userId)
  commit('SET_NAME', userName)
  commit('SET_AVATAR', avatar)
}

const user = {
  state: {
    token: getToken(),
    id: storage.get(constant.id),
    name: storage.get(constant.name),
    avatar: storage.get(constant.avatar),
    roles: storage.get(constant.roles),
    permissions: storage.get(constant.permissions)
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_ID: (state, id) => {
      state.id = id
      storage.set(constant.id, id)
    },
    SET_NAME: (state, name) => {
      state.name = name
      storage.set(constant.name, name)
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
      storage.set(constant.avatar, avatar)
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
      storage.set(constant.roles, roles)
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
      storage.set(constant.permissions, permissions)
    }
  },

  actions: {
    Login({ commit }, loginBody) {
      return new Promise((resolve, reject) => {
        wechatMiniappLogin(loginBody).then(res => {
          if (!res.token) {
            reject(new Error('Missing token from wechat login response'))
            return
          }

          setToken(res.token)
          commit('SET_TOKEN', res.token)
          commitUserInfo(commit, res)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    GetInfo({ commit }) {
      return new Promise((resolve, reject) => {
        getWechatMiniappInfo().then(res => {
          commitUserInfo(commit, res)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    LogOut({ commit }) {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          removeToken()
          storage.clean()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}

export default user
