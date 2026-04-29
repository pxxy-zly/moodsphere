import config from '@/config'

const app = {
  state: {
    tabBarSelected: 0 // 当前选中的 tabBar 索引
  },

  mutations: {
    SET_TABBAR_SELECTED: (state, index) => {
      state.tabBarSelected = index
    }
  },

  actions: {
    setTabBarSelected({ commit }, index) {
      commit('SET_TABBAR_SELECTED', index)
    }
  }
}

export default app
