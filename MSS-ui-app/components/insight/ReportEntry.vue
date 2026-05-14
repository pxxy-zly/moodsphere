<template>
  <view class="report-entry-card">
    <view class="section-header">
      <view class="title-wrap">
        <view class="icon-doc">📄</view>
        <view>
          <text class="title">AI 报告生成</text>
          <text class="subtitle">选择你需要的报告类型</text>
        </view>
      </view>
    </view>
    
    <view class="report-grid">
      <view class="report-item bg-orange">
        <text class="r-icon text-orange">☀️</text>
        <text class="r-name">日报</text>
        <text class="r-desc">今日概览</text>
      </view>
      <view class="report-item bg-blue">
        <text class="r-icon text-blue">📅</text>
        <text class="r-name">周报</text>
        <text class="r-desc">趋势洞察</text>
      </view>
      <view class="report-item bg-purple">
        <text class="r-icon text-purple">🗓️</text>
        <text class="r-name">月报</text>
        <text class="r-desc">深度分析</text>
      </view>
      <view class="report-item bg-rose">
        <text class="r-icon text-rose">⚙️</text>
        <text class="r-name">自定义</text>
        <text class="r-desc">按需生成</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ReportEntry'
}
</script>

<style scoped>
.report-entry-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 30rpx;
}
.section-header {
  margin-bottom: 30rpx;
}
.title-wrap {
  display: flex;
  align-items: flex-start;
}
.icon-doc {
  width: 56rpx;
  height: 56rpx;
  background: #F59E0B;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin-right: 16rpx;
}
.title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1E293B;
  display: block;
  margin-bottom: 4rpx;
}
.subtitle {
  font-size: 24rpx;
  color: #64748B;
}
.report-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}
.report-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 10rpx;
  border-radius: 24rpx;
}
.r-icon {
  font-size: 48rpx;
  margin-bottom: 12rpx;
}
.r-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #334155;
  margin-bottom: 6rpx;
}
.r-desc {
  font-size: 20rpx;
  color: #64748B;
}
.bg-orange { background: #FFF7ED; }
.bg-blue { background: #EFF6FF; }
.bg-purple { background: #F5F3FF; }
.bg-rose { background: #FFF1F2; }
</style>