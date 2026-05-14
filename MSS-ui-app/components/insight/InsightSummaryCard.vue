<template>
  <view class="insight-summary-card">
    <view class="card-header">
      <view class="header-left">
        <text class="icon-stars">✨</text>
        <text class="title">今日 AI 洞察</text>
      </view>
      <view class="header-right">
        <text class="update-time">更新于 09:30</text>
      </view>
    </view>

    <view class="card-content">
      <view class="content-left">
        <view class="weather-section">
          <view class="weather-main">
            <text class="weather-icon">⛅</text>
            <text class="weather-name">低压多云</text>
          </view>
          <text class="weather-desc">整体平稳，注意能量波动</text>
        </view>

        <view class="metrics-section">
          <view class="metric-item">
            <view class="metric-title">
              <text class="icon-wave">〰</text>
              <text>情绪波动指数</text>
            </view>
            <view class="metric-val">
              <text class="val-num num-blue">42</text>
              <text class="val-unit">/100</text>
            </view>
            <view class="metric-tag tag-blue">平稳</view>
          </view>
          <view class="metric-item">
            <view class="metric-title">
              <text class="icon-smile">🙂</text>
              <text>主导情绪</text>
            </view>
            <view class="metric-val">
              <text class="val-text text-green">平静</text>
            </view>
            <text class="metric-sub">占比 48%</text>
          </view>
        </view>
      </view>

      <view class="content-right">
        <TrendChart />
      </view>
    </view>
  </view>
</template>

<script>
import TrendChart from './TrendChart.vue'

export default {
  name: 'InsightSummaryCard',
  components: {
    TrendChart
  }
}
</script>

<style scoped>
.insight-summary-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 30rpx;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}
.header-left {
  display: flex;
  align-items: center;
}
.icon-stars {
  font-size: 32rpx;
  color: #60A5FA;
  margin-right: 12rpx;
}
.title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1E293B;
}
.header-right {
  display: flex;
  align-items: center;
  color: #94A3B8;
  font-size: 24rpx;
}
.header-right::after {
  content: '';
  display: inline-block;
  width: 24rpx;
  height: 24rpx;
  margin-left: 10rpx;
  border: 2rpx solid #94A3B8;
  border-top-color: transparent;
  border-radius: 50%;
  transform: rotate(45deg);
}
.card-content {
  display: flex;
  gap: 20rpx;
}
.content-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.weather-section {
  margin-bottom: 30rpx;
}
.weather-main {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}
.weather-icon {
  font-size: 64rpx;
  margin-right: 16rpx;
}
.weather-name {
  font-size: 36rpx;
  font-weight: 600;
  color: #1E293B;
}
.weather-desc {
  font-size: 24rpx;
  color: #64748B;
}
.metrics-section {
  display: flex;
  gap: 20rpx;
}
.metric-item {
  flex: 1;
}
.metric-title {
  display: flex;
  align-items: center;
  font-size: 22rpx;
  color: #64748B;
  margin-bottom: 12rpx;
  white-space: nowrap;
}
.icon-wave { color: #60A5FA; margin-right: 8rpx; font-weight: bold;}
.icon-smile { color: #4ADE80; margin-right: 8rpx; }
.metric-val {
  margin-bottom: 8rpx;
  display: flex;
  align-items: baseline;
  white-space: nowrap;
}
.val-num {
  font-size: 40rpx;
  font-weight: 700;
  line-height: 1;
}
.num-blue { color: #3B82F6; }
.val-unit {
  font-size: 22rpx;
  color: #94A3B8;
  margin-left: 4rpx;
}
.val-text {
  font-size: 36rpx;
  font-weight: 600;
  line-height: 1;
}
.text-green { color: #10B981; }
.metric-tag {
  display: inline-block;
  font-size: 20rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.tag-blue {
  background: #EFF6FF;
  color: #3B82F6;
}
.metric-sub {
  font-size: 22rpx;
  color: #94A3B8;
}
.content-right {
  flex: 1;
  background: #F8FAFC;
  border-radius: 24rpx;
  padding: 20rpx;
}
</style>