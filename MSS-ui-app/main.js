import Vue from 'vue'
import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import './permission' // permission
import { getDicts } from "@/api/system/dict/data"
import customTabBar from './custom-tab-bar/index.vue'

Vue.use(plugins)

Vue.config.productionTip = false
Vue.prototype.$store = store
Vue.prototype.getDicts = getDicts
Vue.component('custom-tab-bar', customTabBar)

App.mpType = 'app'

const app = new Vue({
  ...App
})

app.$mount()
