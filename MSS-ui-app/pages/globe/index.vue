<template>
  <view class="globe-page">
    <view class="space-bg">
      <view class="nebula nebula-coral"></view>
      <view class="nebula nebula-mint"></view>
      <view class="nebula nebula-amber"></view>
      <view
        v-for="star in starField"
        :key="star.id"
        class="star"
        :style="{
          top: star.top,
          left: star.left,
          width: star.size,
          height: star.size,
          animationDuration: star.duration,
          animationDelay: star.delay
        }"
      ></view>
    </view>

    <view class="header">
      <view>
        <text class="eyebrow">MOOD GLOBE</text>
        <text class="page-title">此刻的情绪星球</text>
      </view>
      <view class="live-pill">
        <view class="live-dot"></view>
        <text>模拟实时</text>
      </view>
    </view>

    <view class="header-controls">
      <view class="glass-toggle">
        <view class="toggle-item" :class="{ active: scope === 'global' }" @click="setScope('global')">全球</view>
        <view class="toggle-item" :class="{ active: scope === 'national' }" @click="setScope('national')">全国</view>
      </view>

      <view class="glass-toggle">
        <view class="toggle-item" :class="{ active: time === 'realtime' }" @click="setTime('realtime')">实时</view>
        <view class="toggle-item" :class="{ active: time === 'today' }" @click="setTime('today')">今日</view>
      </view>
    </view>

    <view class="analysis-card glass-panel floating-soft">
      <view class="analysis-mark">✦</view>
      <view class="analysis-copy">
        <text class="analysis-label">{{ scopeLabel }} · {{ timeLabel }}</text>
        <text class="analysis-text">{{ currentInsight }}</text>
      </view>
    </view>

    <view class="earth-container">
      <view class="orbit orbit-one"></view>
      <view class="orbit orbit-two"></view>
      <view class="earth-aura"></view>
      <view class="earth-sphere">
        <view class="earth-texture">
          <view class="continent continent-one"></view>
          <view class="continent continent-two"></view>
          <view class="continent continent-three"></view>
          <view class="heat-spot spot-coral"></view>
          <view class="heat-spot spot-mint"></view>
          <view class="heat-spot spot-amber"></view>
          <view class="heat-spot spot-violet"></view>
        </view>
        <view class="latitude latitude-one"></view>
        <view class="latitude latitude-two"></view>
        <view class="earth-gloss"></view>
        <view class="earth-shadow"></view>
      </view>

      <view class="signal-chip chip-left glass-panel">
        <text class="chip-value">{{ activeMoodStats.resonance }}</text>
        <text class="chip-label">共鸣</text>
      </view>
      <view class="signal-chip chip-right glass-panel">
        <text class="chip-value">{{ activeMoodStats.warmth }}</text>
        <text class="chip-label">暖流</text>
      </view>
    </view>

    <view class="mood-stream">
      <view
        v-for="item in visibleMoods"
        :key="item.id"
        class="mood-bubble glass-panel"
        :class="['mood-bubble--' + item.tone, 'mood-layer--' + item.layer, 'float-path-' + item.path]"
        :style="{
          top: item.top,
          left: item.left,
          right: item.right,
          animationDuration: item.duration,
          animationDelay: item.delay
        }"
      >
        <view class="mood-avatar">{{ item.avatar }}</view>
        <view class="mood-content">
          <text class="mood-meta">{{ item.city }} · {{ item.minutes }}</text>
          <text class="mood-text">{{ item.text }}</text>
        </view>
        <view class="mood-tail"></view>
      </view>
    </view>

    <view class="bottom-panel glass-panel">
      <view class="panel-stat">
        <text class="panel-value">{{ activeMoodStats.online }}</text>
        <text class="panel-label">正在漂浮</text>
      </view>
      <view class="panel-divider"></view>
      <view class="panel-stat">
        <text class="panel-value">{{ activeMoodStats.primary }}</text>
        <text class="panel-label">当前高频</text>
      </view>
      <view class="release-button" @click="showReleaseToast">
        <text class="release-icon">＋</text>
      </view>
    </view>

    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
const MOCK_MOOD_STREAM = [
  { id: 1, avatar: '☕', city: '上海', minutes: '1分钟前', text: '忙完这一阵，想安静地喝一口热的。', tone: 'warm', layer: 'near', top: '55%', left: '-420rpx', duration: '25s', delay: '-3s', path: 'upRight' },
  { id: 2, avatar: '🌙', city: '成都', minutes: '3分钟前', text: '有点低落，但今晚的风很温柔。', tone: 'calm', layer: 'mid', top: '36%', right: '-430rpx', duration: '29s', delay: '-11s', path: 'upLeft' },
  { id: 3, avatar: '🎧', city: '广州', minutes: '5分钟前', text: '耳机里那首歌刚好救了我一下。', tone: 'hope', layer: 'near', top: '61%', left: '-450rpx', duration: '28s', delay: '-16s', path: 'orbitRight' },
  { id: 4, avatar: '🌧', city: '杭州', minutes: '8分钟前', text: '今天很累，允许自己早点关机。', tone: 'rain', layer: 'mid', top: '49%', right: '-430rpx', duration: '31s', delay: '-19s', path: 'orbitLeft' },
  { id: 5, avatar: '✨', city: '北京', minutes: '12分钟前', text: '突然觉得事情也许会慢慢变好。', tone: 'hope', layer: 'mid', top: '24%', right: '-360rpx', duration: '33s', delay: '-8s', path: 'crossLeft' },
  { id: 6, avatar: '🍃', city: '厦门', minutes: '16分钟前', text: '散步十分钟，脑子终于松了一点。', tone: 'calm', layer: 'far', top: '70%', left: '-320rpx', duration: '35s', delay: '-22s', path: 'riseRight' },
  { id: 7, avatar: '🌻', city: '南京', minutes: '18分钟前', text: '今天收到一句肯定，心里亮了一小块。', tone: 'warm', layer: 'near', top: '42%', left: '-440rpx', duration: '27s', delay: '-7s', path: 'upRight' },
  { id: 8, avatar: '🫧', city: '苏州', minutes: '22分钟前', text: '不想解释太多，只想把自己放轻一点。', tone: 'calm', layer: 'mid', top: '58%', right: '-440rpx', duration: '32s', delay: '-24s', path: 'upLeft' },
  { id: 9, avatar: '📎', city: '武汉', minutes: '26分钟前', text: '任务还在，但我先把呼吸找回来。', tone: 'rain', layer: 'far', top: '32%', left: '-330rpx', duration: '38s', delay: '-28s', path: 'crossRight' },
  { id: 10, avatar: '🕯', city: '西安', minutes: '31分钟前', text: '给自己留一盏小灯，今晚不硬撑。', tone: 'warm', layer: 'mid', top: '66%', right: '-420rpx', duration: '34s', delay: '-13s', path: 'riseLeft' },
  { id: 11, avatar: '🌊', city: '青岛', minutes: '36分钟前', text: '海边的风把很多烦躁吹散了。', tone: 'calm', layer: 'near', top: '47%', left: '-460rpx', duration: '30s', delay: '-21s', path: 'orbitRight' },
  { id: 12, avatar: '🪐', city: '重庆', minutes: '42分钟前', text: '人很多，心有点挤，想慢慢退出来。', tone: 'rain', layer: 'mid', top: '28%', right: '-450rpx', duration: '36s', delay: '-30s', path: 'orbitLeft' },
  { id: 13, avatar: '🍵', city: '长沙', minutes: '48分钟前', text: '一杯热茶，让混乱稍微有了边界。', tone: 'warm', layer: 'far', top: '18%', left: '-300rpx', duration: '40s', delay: '-18s', path: 'crossRight' },
  { id: 14, avatar: '🚌', city: '天津', minutes: '53分钟前', text: '通勤路上发呆，也算把今天消化了一点。', tone: 'calm', layer: 'mid', top: '52%', right: '-430rpx', duration: '30s', delay: '-5s', path: 'upLeft' },
  { id: 15, avatar: '🌤', city: '昆明', minutes: '58分钟前', text: '事情没有全好，但我愿意再试一次。', tone: 'hope', layer: 'near', top: '39%', left: '-440rpx', duration: '26s', delay: '-15s', path: 'upRight' },
  { id: 16, avatar: '🧩', city: '合肥', minutes: '1小时前', text: '困惑还在，可我好像找到第一块拼图。', tone: 'hope', layer: 'mid', top: '63%', right: '-430rpx', duration: '33s', delay: '-27s', path: 'orbitLeft' },
  { id: 17, avatar: '📚', city: '郑州', minutes: '1小时前', text: '读到一句话，突然被轻轻接住。', tone: 'warm', layer: 'far', top: '44%', left: '-320rpx', duration: '37s', delay: '-9s', path: 'crossRight' },
  { id: 18, avatar: '🌫', city: '沈阳', minutes: '1小时前', text: '脑袋像起雾，今天先不做重大决定。', tone: 'rain', layer: 'mid', top: '60%', left: '-430rpx', duration: '34s', delay: '-31s', path: 'riseRight' },
  { id: 19, avatar: '🧡', city: '福州', minutes: '1小时前', text: '被朋友惦记了一下，心里软下来了。', tone: 'warm', layer: 'near', top: '34%', right: '-450rpx', duration: '28s', delay: '-17s', path: 'upLeft' },
  { id: 20, avatar: '🎐', city: '无锡', minutes: '1小时前', text: '今天不算顺利，但我没有责怪自己。', tone: 'calm', layer: 'mid', top: '67%', left: '-440rpx', duration: '36s', delay: '-25s', path: 'riseRight' },
  { id: 21, avatar: '🌌', city: '哈尔滨', minutes: '2小时前', text: '夜很长，但也许明天会轻一点。', tone: 'hope', layer: 'far', top: '22%', right: '-320rpx', duration: '42s', delay: '-35s', path: 'crossLeft' },
  { id: 22, avatar: '🥐', city: '深圳', minutes: '2小时前', text: '给明早买了面包，像给自己留了期待。', tone: 'hope', layer: 'mid', top: '46%', left: '-430rpx', duration: '31s', delay: '-12s', path: 'orbitRight' },
  { id: 23, avatar: '🪴', city: '宁波', minutes: '2小时前', text: '植物长出新叶，我也想慢慢恢复。', tone: 'calm', layer: 'near', top: '57%', right: '-450rpx', duration: '29s', delay: '-23s', path: 'orbitLeft' },
  { id: 24, avatar: '💭', city: '南昌', minutes: '2小时前', text: '今天有点敏感，但这也是我在认真生活。', tone: 'rain', layer: 'far', top: '73%', right: '-330rpx', duration: '39s', delay: '-33s', path: 'riseLeft' }
]

const VISIBLE_MOOD_LIMIT = 12

const STAR_FIELD = [
  { id: 1, top: '8%', left: '18%', size: '4rpx', duration: '3.2s', delay: '0s' },
  { id: 2, top: '15%', left: '78%', size: '6rpx', duration: '5.4s', delay: '-1s' },
  { id: 3, top: '25%', left: '42%', size: '3rpx', duration: '4.4s', delay: '-2s' },
  { id: 4, top: '41%', left: '9%', size: '5rpx', duration: '5.8s', delay: '-3s' },
  { id: 5, top: '49%', left: '88%', size: '4rpx', duration: '3.8s', delay: '-1.5s' },
  { id: 6, top: '62%', left: '17%', size: '7rpx', duration: '6.2s', delay: '-4s' },
  { id: 7, top: '72%', left: '74%', size: '4rpx', duration: '4.6s', delay: '-2.2s' },
  { id: 8, top: '84%', left: '34%', size: '5rpx', duration: '5.2s', delay: '-3.4s' },
  { id: 9, top: '90%', left: '62%', size: '3rpx', duration: '4s', delay: '-1.8s' },
  { id: 10, top: '33%', left: '67%', size: '5rpx', duration: '6.8s', delay: '-4.8s' }
]

const INSIGHTS = {
  global: {
    realtime: '此刻温暖与疲惫交替上升，夜间城市出现明显的自我安抚需求。',
    today: '今天全球心情以平静、疲惫和期待为主，晚间共鸣正在变得柔和。'
  },
  national: {
    realtime: '国内实时情绪正在从忙碌转向放松，东部沿海有一小片希望感升温。',
    today: '今日全国记录里，平静占比最高，焦虑在通勤和收尾时段短暂抬头。'
  }
}

const STATS = {
  global: {
    realtime: { online: '12.8k', resonance: '72%', warmth: '41%', primary: '平静' },
    today: { online: '86.4k', resonance: '68%', warmth: '46%', primary: '疲惫' }
  },
  national: {
    realtime: { online: '3.6k', resonance: '76%', warmth: '53%', primary: '期待' },
    today: { online: '24.1k', resonance: '71%', warmth: '49%', primary: '平静' }
  }
}

export default {
  data() {
    return {
      scope: 'global',
      time: 'realtime',
      moodStream: MOCK_MOOD_STREAM,
      starField: STAR_FIELD
    }
  },
  computed: {
    scopeLabel() {
      return this.scope === 'global' ? '全球' : '全国'
    },
    timeLabel() {
      return this.time === 'realtime' ? '实时' : '今日'
    },
    currentInsight() {
      return INSIGHTS[this.scope][this.time]
    },
    activeMoodStats() {
      return STATS[this.scope][this.time]
    },
    visibleMoods() {
      const scopeOffset = this.scope === 'national' ? 6 : 0
      const timeOffset = this.time === 'today' ? 12 : 0
      const offset = (scopeOffset + timeOffset) % this.moodStream.length
      const orderedList = this.moodStream.slice(offset).concat(this.moodStream.slice(0, offset))
      return orderedList.slice(0, VISIBLE_MOOD_LIMIT)
    }
  },
  onShow() {
    this.$store.dispatch('setTabBarSelected', 1)
  },
  methods: {
    setScope(value) {
      this.scope = value
    },
    setTime(value) {
      this.time = value
    },
    showReleaseToast() {
      uni.showToast({
        title: '心情释放入口待接入',
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped>
.globe-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  background:
    radial-gradient(circle at 50% 72%, rgba(47, 231, 205, 0.18) 0%, transparent 38%),
    linear-gradient(158deg, #120d21 0%, #101827 45%, #081018 100%);
}

.space-bg,
.mood-stream {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.space-bg {
  z-index: 0;
}

.nebula {
  position: absolute;
  border-radius: 50%;
  filter: blur(70rpx);
  opacity: 0.46;
  animation: nebulaFloat 14s ease-in-out infinite alternate;
}

.nebula-coral {
  width: 360rpx;
  height: 360rpx;
  top: 6%;
  left: -120rpx;
  background: #ff7b7b;
}

.nebula-mint {
  width: 460rpx;
  height: 460rpx;
  right: -180rpx;
  top: 33%;
  background: #43e8c8;
  animation-delay: -5s;
}

.nebula-amber {
  width: 300rpx;
  height: 300rpx;
  left: 18%;
  bottom: 6%;
  background: #ffc85a;
  opacity: 0.22;
  animation-delay: -8s;
}

.star {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 0 14rpx rgba(255, 255, 255, 0.72);
  animation: twinkle linear infinite alternate;
}

.header {
  position: relative;
  z-index: 12;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 92rpx 36rpx 24rpx;
  box-sizing: border-box;
}

.eyebrow {
  display: block;
  margin-bottom: 12rpx;
  color: rgba(167, 243, 208, 0.78);
  font-size: 20rpx;
  letter-spacing: 3rpx;
  font-weight: 700;
}

.page-title {
  display: block;
  max-width: 430rpx;
  color: #f8fbff;
  font-size: 44rpx;
  line-height: 1.18;
  font-weight: 700;
}

.live-pill {
  display: flex;
  align-items: center;
  margin-top: 6rpx;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  color: rgba(255, 255, 255, 0.82);
  font-size: 22rpx;
  background: rgba(255, 255, 255, 0.1);
  border: 1rpx solid rgba(255, 255, 255, 0.14);
}

.live-dot {
  width: 12rpx;
  height: 12rpx;
  margin-right: 10rpx;
  border-radius: 50%;
  background: #57f2b9;
  box-shadow: 0 0 18rpx rgba(87, 242, 185, 0.9);
  animation: livePulse 1.8s ease-in-out infinite;
}

.header-controls {
  position: relative;
  z-index: 12;
  display: flex;
  justify-content: space-between;
  padding: 0 36rpx;
  box-sizing: border-box;
}

.glass-toggle {
  display: flex;
  padding: 6rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.09);
  border: 1rpx solid rgba(255, 255, 255, 0.13);
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.12), 0 12rpx 28rpx rgba(0, 0, 0, 0.16);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.toggle-item {
  min-width: 92rpx;
  padding: 13rpx 18rpx;
  border-radius: 999rpx;
  color: rgba(255, 255, 255, 0.58);
  font-size: 24rpx;
  font-weight: 700;
  text-align: center;
  transition: all 0.24s ease;
}

.toggle-item.active {
  color: #111827;
  background: linear-gradient(135deg, #f9fafb 0%, #baf7e8 100%);
  box-shadow: 0 8rpx 18rpx rgba(67, 232, 200, 0.22);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.11);
  border: 1rpx solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 18rpx 42rpx rgba(0, 0, 0, 0.24), inset 0 1rpx 0 rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

.analysis-card {
  position: relative;
  z-index: 12;
  display: flex;
  align-items: center;
  margin: 26rpx 36rpx 0;
  padding: 24rpx 26rpx;
  border-radius: 28rpx;
  box-sizing: border-box;
}

.analysis-mark {
  width: 58rpx;
  height: 58rpx;
  margin-right: 18rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #101827;
  font-size: 30rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #ffd166 0%, #57f2b9 100%);
}

.analysis-copy {
  flex: 1;
  min-width: 0;
}

.analysis-label {
  display: block;
  margin-bottom: 8rpx;
  color: rgba(255, 255, 255, 0.55);
  font-size: 21rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
}

.analysis-text {
  display: block;
  color: rgba(255, 255, 255, 0.92);
  font-size: 27rpx;
  line-height: 1.45;
}

.earth-container {
  position: absolute;
  z-index: 5;
  top: 49%;
  left: 50%;
  width: 590rpx;
  height: 590rpx;
  transform: translate(-50%, -45%);
}

.earth-sphere {
  position: absolute;
  top: 48rpx;
  left: 48rpx;
  right: 48rpx;
  bottom: 48rpx;
  border-radius: 50%;
  overflow: hidden;
  background:
    radial-gradient(circle at 36% 28%, rgba(255, 255, 255, 0.26) 0%, transparent 18%),
    radial-gradient(circle at 64% 68%, #1b3258 0%, #111d38 44%, #090f1d 100%);
  box-shadow:
    0 0 58rpx rgba(77, 224, 210, 0.26),
    0 0 120rpx rgba(255, 122, 117, 0.12),
    inset -58rpx -70rpx 110rpx rgba(0, 0, 0, 0.72),
    inset 28rpx 20rpx 64rpx rgba(255, 255, 255, 0.12);
}

.earth-texture {
  position: absolute;
  top: -45%;
  left: -45%;
  width: 190%;
  height: 190%;
  animation: earthSpin 52s linear infinite;
}

.continent,
.heat-spot {
  position: absolute;
  border-radius: 50%;
}

.continent {
  opacity: 0.34;
  filter: blur(10rpx);
  background: linear-gradient(135deg, rgba(96, 211, 148, 0.9), rgba(250, 204, 100, 0.4));
}

.continent-one {
  width: 330rpx;
  height: 170rpx;
  top: 33%;
  left: 16%;
  transform: rotate(-18deg);
}

.continent-two {
  width: 250rpx;
  height: 310rpx;
  right: 20%;
  bottom: 16%;
  transform: rotate(24deg);
}

.continent-three {
  width: 210rpx;
  height: 120rpx;
  top: 18%;
  right: 28%;
  transform: rotate(12deg);
}

.heat-spot {
  filter: blur(42rpx);
  mix-blend-mode: screen;
}

.spot-coral {
  width: 250rpx;
  height: 190rpx;
  left: 26%;
  top: 35%;
  background: rgba(255, 122, 117, 0.82);
}

.spot-mint {
  width: 260rpx;
  height: 240rpx;
  right: 20%;
  bottom: 24%;
  background: rgba(67, 232, 200, 0.58);
}

.spot-amber {
  width: 210rpx;
  height: 170rpx;
  left: 44%;
  top: 12%;
  background: rgba(255, 200, 90, 0.58);
}

.spot-violet {
  width: 190rpx;
  height: 220rpx;
  left: 14%;
  bottom: 24%;
  background: rgba(155, 135, 245, 0.46);
}

.latitude {
  position: absolute;
  left: 8%;
  right: 8%;
  height: 1rpx;
  border-radius: 50%;
  border-top: 1rpx solid rgba(255, 255, 255, 0.13);
}

.latitude-one {
  top: 38%;
  transform: rotate(8deg);
}

.latitude-two {
  top: 61%;
  transform: rotate(-10deg);
}

.earth-gloss,
.earth-shadow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  pointer-events: none;
}

.earth-gloss {
  background: radial-gradient(circle at 32% 24%, rgba(255, 255, 255, 0.28) 0%, transparent 24%);
}

.earth-shadow {
  box-shadow:
    inset -72rpx -68rpx 110rpx rgba(0, 0, 0, 0.78),
    inset 22rpx 20rpx 66rpx rgba(109, 250, 224, 0.14);
}

.earth-aura {
  position: absolute;
  top: 10rpx;
  left: 10rpx;
  right: 10rpx;
  bottom: 10rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(67, 232, 200, 0.2) 0%, rgba(255, 122, 117, 0.08) 35%, transparent 66%);
  animation: auraPulse 4.8s ease-in-out infinite alternate;
}

.orbit {
  position: absolute;
  border-radius: 50%;
  border: 1rpx solid rgba(255, 255, 255, 0.13);
}

.orbit-one {
  top: 22rpx;
  left: 22rpx;
  right: 22rpx;
  bottom: 22rpx;
  transform: rotate(-18deg);
}

.orbit-two {
  top: 84rpx;
  left: 12rpx;
  right: 12rpx;
  bottom: 84rpx;
  transform: rotate(24deg);
  border-color: rgba(87, 242, 185, 0.18);
}

.signal-chip {
  position: absolute;
  z-index: 4;
  min-width: 128rpx;
  padding: 16rpx 20rpx;
  border-radius: 24rpx;
  box-sizing: border-box;
}

.chip-left {
  left: 0;
  top: 36%;
}

.chip-right {
  right: 2rpx;
  top: 52%;
}

.chip-value {
  display: block;
  color: #fff;
  font-size: 30rpx;
  font-weight: 800;
}

.chip-label {
  display: block;
  margin-top: 4rpx;
  color: rgba(255, 255, 255, 0.58);
  font-size: 21rpx;
}

.mood-stream {
  z-index: 8;
}

.mood-bubble {
  position: absolute;
  display: flex;
  align-items: center;
  max-width: 470rpx;
  min-height: 82rpx;
  padding: 15rpx 24rpx 15rpx 16rpx;
  border-radius: 999rpx;
  box-sizing: border-box;
  opacity: 0;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform, opacity;
}

.mood-bubble--warm {
  border-color: rgba(255, 209, 102, 0.28);
  background: rgba(255, 209, 102, 0.12);
}

.mood-bubble--calm {
  border-color: rgba(87, 242, 185, 0.25);
  background: rgba(87, 242, 185, 0.1);
}

.mood-bubble--hope {
  border-color: rgba(255, 122, 117, 0.28);
  background: rgba(255, 122, 117, 0.11);
}

.mood-bubble--rain {
  border-color: rgba(155, 135, 245, 0.28);
  background: rgba(155, 135, 245, 0.11);
}

.mood-layer--near {
  max-width: 500rpx;
  opacity: 0;
}

.mood-layer--mid {
  max-width: 440rpx;
  min-height: 76rpx;
  padding: 13rpx 22rpx 13rpx 15rpx;
}

.mood-layer--far {
  max-width: 360rpx;
  min-height: 62rpx;
  padding: 10rpx 18rpx 10rpx 12rpx;
}

.mood-avatar {
  flex: 0 0 54rpx;
  width: 54rpx;
  height: 54rpx;
  margin-right: 14rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  background: rgba(255, 255, 255, 0.14);
}

.mood-layer--far .mood-avatar {
  flex-basis: 42rpx;
  width: 42rpx;
  height: 42rpx;
  margin-right: 10rpx;
  font-size: 22rpx;
}

.mood-content {
  min-width: 0;
}

.mood-meta {
  display: block;
  margin-bottom: 4rpx;
  color: rgba(255, 255, 255, 0.48);
  font-size: 19rpx;
  line-height: 1.1;
}

.mood-layer--far .mood-meta {
  display: none;
}

.mood-text {
  display: block;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.3;
}

.mood-layer--far .mood-text {
  color: rgba(255, 255, 255, 0.72);
  font-size: 21rpx;
}

.mood-tail {
  position: absolute;
  left: 46rpx;
  bottom: -22rpx;
  width: 42rpx;
  height: 42rpx;
  border-radius: 50%;
  filter: blur(10rpx);
  opacity: 0.58;
}

.mood-bubble--warm .mood-tail {
  background: #ffd166;
}

.mood-bubble--calm .mood-tail {
  background: #57f2b9;
}

.mood-bubble--hope .mood-tail {
  background: #ff7a75;
}

.mood-bubble--rain .mood-tail {
  background: #9b87f5;
}

.float-path-upRight { animation-name: floatUpRight; }
.float-path-upLeft { animation-name: floatUpLeft; }
.float-path-orbitRight { animation-name: floatOrbitRight; }
.float-path-orbitLeft { animation-name: floatOrbitLeft; }
.float-path-crossRight { animation-name: floatCrossRight; }
.float-path-crossLeft { animation-name: floatCrossLeft; }
.float-path-riseRight { animation-name: floatRiseRight; }
.float-path-riseLeft { animation-name: floatRiseLeft; }

.bottom-panel {
  position: absolute;
  z-index: 12;
  left: 36rpx;
  right: 36rpx;
  bottom: calc(env(safe-area-inset-bottom) + 154rpx);
  min-height: 116rpx;
  padding: 18rpx 20rpx 18rpx 28rpx;
  border-radius: 34rpx;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.panel-stat {
  flex: 1;
}

.panel-value {
  display: block;
  color: #fff;
  font-size: 34rpx;
  font-weight: 800;
}

.panel-label {
  display: block;
  margin-top: 5rpx;
  color: rgba(255, 255, 255, 0.54);
  font-size: 22rpx;
}

.panel-divider {
  width: 1rpx;
  height: 52rpx;
  margin: 0 24rpx;
  background: rgba(255, 255, 255, 0.16);
}

.release-button {
  width: 74rpx;
  height: 74rpx;
  margin-left: 20rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #101827;
  background: linear-gradient(135deg, #ffd166 0%, #57f2b9 100%);
  box-shadow: 0 12rpx 28rpx rgba(87, 242, 185, 0.24);
}

.release-icon {
  font-size: 42rpx;
  line-height: 1;
  font-weight: 300;
}

.floating-soft {
  animation: slowFloat 5s ease-in-out infinite alternate;
}

@keyframes twinkle {
  0% { opacity: 0.24; transform: scale(0.72); }
  100% { opacity: 0.94; transform: scale(1.18); }
}

@keyframes nebulaFloat {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(34rpx, -28rpx) scale(1.06); }
}

@keyframes livePulse {
  0%, 100% { opacity: 0.45; transform: scale(0.86); }
  50% { opacity: 1; transform: scale(1.18); }
}

@keyframes slowFloat {
  0% { transform: translateY(0); }
  100% { transform: translateY(-12rpx); }
}

@keyframes earthSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes auraPulse {
  0% { opacity: 0.58; transform: scale(0.96); }
  100% { opacity: 1; transform: scale(1.08); }
}

@keyframes floatUpRight {
  0% { opacity: 0; transform: translate3d(0, 70rpx, 0) scale(0.94); }
  12% { opacity: 0.94; }
  52% { opacity: 0.9; transform: translate3d(54vw, -34rpx, 0) scale(1); }
  88% { opacity: 0.82; }
  100% { opacity: 0; transform: translate3d(118vw, -128rpx, 0) scale(0.98); }
}

@keyframes floatUpLeft {
  0% { opacity: 0; transform: translate3d(0, 76rpx, 0) scale(0.94); }
  12% { opacity: 0.92; }
  52% { opacity: 0.88; transform: translate3d(-52vw, -30rpx, 0) scale(1); }
  88% { opacity: 0.8; }
  100% { opacity: 0; transform: translate3d(-118vw, -122rpx, 0) scale(0.98); }
}

@keyframes floatOrbitRight {
  0% { opacity: 0; transform: translate3d(0, 34rpx, 0) scale(0.96); }
  10% { opacity: 0.96; }
  38% { transform: translate3d(36vw, -58rpx, 0) scale(1.03); }
  68% { opacity: 0.88; transform: translate3d(72vw, 18rpx, 0) scale(0.98); }
  100% { opacity: 0; transform: translate3d(122vw, -46rpx, 0) scale(0.94); }
}

@keyframes floatOrbitLeft {
  0% { opacity: 0; transform: translate3d(0, 30rpx, 0) scale(0.96); }
  10% { opacity: 0.96; }
  38% { transform: translate3d(-34vw, -56rpx, 0) scale(1.03); }
  68% { opacity: 0.88; transform: translate3d(-72vw, 20rpx, 0) scale(0.98); }
  100% { opacity: 0; transform: translate3d(-122vw, -42rpx, 0) scale(0.94); }
}

@keyframes floatCrossRight {
  0% { opacity: 0; transform: translate3d(0, 12rpx, 0) scale(0.86); }
  14% { opacity: 0.62; }
  84% { opacity: 0.58; }
  100% { opacity: 0; transform: translate3d(112vw, -32rpx, 0) scale(0.88); }
}

@keyframes floatCrossLeft {
  0% { opacity: 0; transform: translate3d(0, 10rpx, 0) scale(0.86); }
  14% { opacity: 0.62; }
  84% { opacity: 0.58; }
  100% { opacity: 0; transform: translate3d(-112vw, -30rpx, 0) scale(0.88); }
}

@keyframes floatRiseRight {
  0% { opacity: 0; transform: translate3d(0, 92rpx, 0) scale(0.88); }
  16% { opacity: 0.74; }
  72% { opacity: 0.68; transform: translate3d(32vw, -38vh, 0) scale(0.94); }
  100% { opacity: 0; transform: translate3d(46vw, -58vh, 0) scale(0.82); }
}

@keyframes floatRiseLeft {
  0% { opacity: 0; transform: translate3d(0, 92rpx, 0) scale(0.88); }
  16% { opacity: 0.74; }
  72% { opacity: 0.68; transform: translate3d(-32vw, -38vh, 0) scale(0.94); }
  100% { opacity: 0; transform: translate3d(-46vw, -58vh, 0) scale(0.82); }
}
</style>
