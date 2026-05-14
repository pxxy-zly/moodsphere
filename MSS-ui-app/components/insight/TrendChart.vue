<template>
  <view class="trend-chart-container">
    <view class="chart-header">
      <text class="chart-title">7天情绪波动趋势</text>
    </view>
    <view class="chart-body">
      <!-- Mock chart using CSS/SVG for UI presentation -->
      <view class="y-axis">
        <text>100</text>
        <text>75</text>
        <text>50</text>
        <text>25</text>
        <text>0</text>
      </view>
      <view class="chart-content">
        <view class="grid-lines">
          <view class="g-line"></view>
          <view class="g-line"></view>
          <view class="g-line"></view>
          <view class="g-line"></view>
          <view class="g-line"></view>
        </view>
        <view class="chart-svg-wrap">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 40" preserveAspectRatio="none" class="chart-svg">
            <defs>
              <linearGradient id="chartGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stop-color="#4ADE80" stop-opacity="0.2"/>
                <stop offset="100%" stop-color="#4ADE80" stop-opacity="0"/>
              </linearGradient>
            </defs>
            <path d="M0,20 L15,30 L30,22 L45,35 L60,25 L75,18 L90,15 L100,15" fill="none" stroke="#4ADE80" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M0,20 L15,30 L30,22 L45,35 L60,25 L75,18 L90,15 L100,15 L100,40 L0,40 Z" fill="url(#chartGrad)"/>
            <circle cx="0" cy="20" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="15" cy="30" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="30" cy="22" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="45" cy="35" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="60" cy="25" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="75" cy="18" r="1.5" fill="#FFF" stroke="#4ADE80" stroke-width="1"/>
            <circle cx="90" cy="15" r="1.5" fill="#4ADE80"/>
          </svg>
        </view>
        <view class="x-axis">
          <text>5/11</text>
          <text>5/12</text>
          <text>5/13</text>
          <text>5/14</text>
          <text>5/15</text>
          <text>5/16</text>
          <text>5/17</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'TrendChart'
}
</script>

<style scoped>
.trend-chart-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chart-header {
  margin-bottom: 20rpx;
}
.chart-title {
  font-size: 24rpx;
  color: #64748B;
}
.chart-body {
  flex: 1;
  display: flex;
  position: relative;
}
.y-axis {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-size: 20rpx;
  color: #CBD5E1;
  padding-right: 12rpx;
  height: calc(100% - 30rpx);
}
.chart-content {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
}
.grid-lines {
  position: absolute;
  top: 10rpx; left: 0; right: 0; bottom: 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  z-index: 1;
}
.g-line {
  border-bottom: 1px dashed #F1F5F9;
  width: 100%;
}
.chart-svg-wrap {
  flex: 1;
  position: relative;
  z-index: 2;
  margin-top: 10rpx;
  margin-bottom: 30rpx;
}
.chart-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}
.x-axis {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  font-size: 18rpx;
  color: #CBD5E1;
  margin-left: -10rpx;
}
.x-axis text {
  flex: 1;
  text-align: center;
  white-space: nowrap;
}
</style>