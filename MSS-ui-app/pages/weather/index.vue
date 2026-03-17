<template>
  <view class="weather-page">
    <view class="header">今日天气快照</view>

    <view v-if="loading" class="card">正在加载今日快照...</view>

    <view v-else-if="!snapshot" class="card">
      <view class="weather-name">今日暂无快照</view>
      <view class="desc">先去记录一条情绪，系统会自动生成你的天气。</view>
      <button class="record-btn" @click="goRecord">去记录</button>
    </view>

    <view v-else class="card">
      <view class="weather-name">{{ snapshot.weatherName || '--' }}</view>
      <view class="desc">天气编码：{{ snapshot.weatherCode || '--' }}</view>

      <view class="row">
        <text>天空</text>
        <text>{{ formatNum(snapshot.skyType) }}</text>
      </view>
      <view class="row">
        <text>云层</text>
        <text>{{ formatNum(snapshot.cloudDensity) }}</text>
      </view>
      <view class="row">
        <text>降雨</text>
        <text>{{ formatNum(snapshot.rainIntensity) }}</text>
      </view>
      <view class="row">
        <text>风速</text>
        <text>{{ formatNum(snapshot.windSpeed) }}</text>
      </view>
      <view class="row">
        <text>雾气</text>
        <text>{{ formatNum(snapshot.fogIntensity) }}</text>
      </view>
      <view class="row">
        <text>色温</text>
        <text>{{ formatNum(snapshot.colorTemperature) }}</text>
      </view>
      <view class="row">
        <text>饱和度</text>
        <text>{{ formatNum(snapshot.saturation) }}</text>
      </view>
      <view class="row">
        <text>今日记录数</text>
        <text>{{ formatNum(snapshot.recordCount) }}</text>
      </view>
    </view>

    <button class="refresh-btn" @click="loadToday">刷新</button>
  </view>
</template>

<script>
import { getMoodWeatherToday } from '@/api/mood'

export default {
  data() {
    return {
      loading: false,
      snapshot: null
    }
  },
  onShow() {
    this.loadToday()
  },
  methods: {
    async loadToday() {
      this.loading = true
      try {
        const res = await getMoodWeatherToday()
        this.snapshot = res.data || null
      } catch (error) {
        this.snapshot = null
        this.$modal.msgError('获取今日快照失败')
      } finally {
        this.loading = false
      }
    },
    formatNum(value) {
      if (value === null || value === undefined || value === '') {
        return '--'
      }
      return value
    },
    goRecord() {
      this.$tab.switchTab('/pages/record/index')
    }
  }
}
</script>

<style scoped>
.weather-page {
  min-height: 100vh;
  padding: 32rpx;
  background: linear-gradient(180deg, #eef6ff 0%, #ffffff 100%);
}

.header {
  font-size: 42rpx;
  font-weight: 600;
  color: #1f2d3d;
}

.card {
  margin-top: 32rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(25, 137, 250, 0.12);
}

.weather-name {
  font-size: 40rpx;
  color: #1989fa;
  font-weight: 700;
}

.desc {
  margin-top: 10rpx;
  color: #64748b;
  font-size: 26rpx;
}

.row {
  margin-top: 16rpx;
  display: flex;
  justify-content: space-between;
  color: #334155;
  font-size: 28rpx;
}

.refresh-btn,
.record-btn {
  margin-top: 26rpx;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.refresh-btn {
  background: #1989fa;
  color: #ffffff;
}

.record-btn {
  background: #ffffff;
  color: #1989fa;
  border: 1rpx solid #1989fa;
}
</style>
