import Vue from 'vue'
import Vuex from 'vuex'
import user from '@/store/modules/user'
import app from '@/store/modules/app'
import getters from './getters'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    user,
    app
  },
  getters
})

export default store
