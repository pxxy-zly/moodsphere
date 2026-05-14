<template>
  <view class="ai-chat-entry">
    <view class="chat-header">
      <view class="chat-info">
        <view class="title-wrap">
          <view class="icon-chat">💬</view>
          <text class="title">问问 Mood AI</text>
        </view>
        <text class="subtitle">把你的感受或疑问告诉我吧</text>
      </view>
      <view class="robot-avatar">
        <!-- Using emoji as placeholder for robot image -->
        <text class="robot-icon">🤖</text>
        <view class="chat-bubble">...</view>
      </view>
    </view>
    <view class="quick-questions">
      <view class="question-chip">
        <text class="chip-icon">📊</text>
        <text>分析本周状态</text>
      </view>
      <view class="question-chip">
        <text class="chip-icon">🌙</text>
        <text>为什么晚上低落？</text>
      </view>
      <view class="question-chip">
        <text class="chip-icon">📄</text>
        <text>生成周报</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AiChatEntry'
}
</script>

<style scoped>
.ai-chat-entry {
  background: linear-gradient(135deg, #F5F3FF 0%, #FFFFFF 100%);
  border-radius: 32rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 30rpx;
  border: 1px solid #EDE9FE;
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}
.chat-info {
  flex: 1;
}
.title-wrap {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}
.icon-chat {
  width: 56rpx;
  height: 56rpx;
  background: #8B5CF6;
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
}
.subtitle {
  font-size: 24rpx;
  color: #64748B;
  padding-left: 72rpx;
}
.robot-avatar {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  background: #E0E7FF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
}
.chat-bubble {
  position: absolute;
  top: -10rpx;
  left: -20rpx;
  background: #C4B5FD;
  color: white;
  font-size: 16rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx 20rpx 0 20rpx;
  font-weight: bold;
}
.quick-questions {
  display: flex;
  gap: 16rpx;
  overflow-x: auto;
  padding-bottom: 4rpx;
}
.question-chip {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  border-radius: 30rpx;
  padding: 12rpx 24rpx;
  font-size: 24rpx;
  color: #475569;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.02);
}
.chip-icon {
  margin-right: 8rpx;
  font-size: 26rpx;
}
</style>