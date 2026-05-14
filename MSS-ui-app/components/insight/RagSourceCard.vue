<template>
  <view class="rag-source-card">
    <view class="section-header">
      <view class="title-wrap">
        <view class="icon-book">📖</view>
        <view>
          <text class="title">参考依据</text>
          <text class="subtitle">AI 基于以下信息进行分析</text>
        </view>
      </view>
    </view>
    <scroll-view class="source-list" scroll-x :show-scrollbar="false">
      <view class="source-item">
        <view class="item-icon bg-green">
          <text>📋</text>
        </view>
        <view class="item-info">
          <text class="item-name">近7天记录</text>
          <text class="item-desc">12 条</text>
        </view>
        <text class="icon-arrow">›</text>
      </view>
      
      <view class="source-item">
        <view class="item-icon bg-blue">
          <text>📈</text>
        </view>
        <view class="item-info">
          <text class="item-name">情绪向量趋势</text>
          <text class="item-desc">已更新</text>
        </view>
        <text class="icon-arrow">›</text>
      </view>
      
      <view class="source-item">
        <view class="item-icon bg-teal">
          <text>📚</text>
        </view>
        <view class="item-info">
          <text class="item-name">压力调节知识库</text>
          <text class="item-desc">236篇</text>
        </view>
        <text class="icon-arrow">›</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  name: 'RagSourceCard'
}
</script>

<style scoped>
.rag-source-card {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 30rpx;
}
.section-header {
  margin-bottom: 24rpx;
}
.title-wrap {
  display: flex;
  align-items: flex-start;
}
.icon-book {
  width: 56rpx;
  height: 56rpx;
  background: #10B981;
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
.source-list {
  white-space: nowrap;
  width: 100%;
}
.source-item {
  display: inline-flex;
  align-items: center;
  background: #F8FAFC;
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  margin-right: 20rpx;
  width: 300rpx;
  border: 1px solid #F1F5F9;
}
.source-item:last-child {
  margin-right: 0;
}
.item-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}
.bg-green { background: #D1FAE5; }
.bg-blue { background: #DBEAFE; }
.bg-teal { background: #CCFBF1; }
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.item-name {
  font-size: 26rpx;
  font-weight: 500;
  color: #334155;
  margin-bottom: 4rpx;
}
.item-desc {
  font-size: 22rpx;
  color: #94A3B8;
}
.icon-arrow {
  color: #CBD5E1;
  font-size: 32rpx;
  margin-left: 8rpx;
}
</style>