<template>
  <view class="report-page">
    <!-- Header -->
    <view class="header-section">
      <view class="title-row">
        <view class="title-info">
          <text class="page-title">洞察</text>
          <text class="page-subtitle">AI 看见你的情绪模式</text>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="scroll-area" :show-scrollbar="false">
      <!-- Top Card -->
      <InsightSummaryCard />

      <!-- Segment Control -->
      <view class="segment-control">
        <view class="seg-item" :class="{active: currentTab === 'overview'}" @click="currentTab = 'overview'">总览</view>
        <view class="seg-item" :class="{active: currentTab === 'chat'}" @click="currentTab = 'chat'">对话</view>
        <view class="seg-item" :class="{active: currentTab === 'report'}" @click="currentTab = 'report'">报告</view>
        <view class="seg-item" :class="{active: currentTab === 'knowledge'}" @click="currentTab = 'knowledge'">知识</view>
        <view class="seg-slider" :class="'pos-' + currentTab"></view>
      </view>

      <view v-show="currentTab === 'overview'">
        <!-- Chat Entry -->
        <AiChatEntry />

        <!-- RAG Source -->
        <RagSourceCard />

        <!-- Report Generation -->
        <ReportEntry />
      </view>
      
      <view v-show="currentTab !== 'overview'" class="empty-state">
        <text class="empty-text">功能开发中...</text>
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>

    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
import InsightSummaryCard from '@/components/insight/InsightSummaryCard.vue'
import AiChatEntry from '@/components/insight/AiChatEntry.vue'
import RagSourceCard from '@/components/insight/RagSourceCard.vue'
import ReportEntry from '@/components/insight/ReportEntry.vue'

export default {
  components: {
    InsightSummaryCard,
    AiChatEntry,
    RagSourceCard,
    ReportEntry
  },
  data() {
    return {
      currentTab: 'overview' // overview, chat, report, knowledge
    }
  },
  onShow() {
    this.$store.dispatch('setTabBarSelected', 3)
  }
}
</script>

<style scoped>
/* Base Layout */
.report-page {
  width: 100vw;
  height: 100vh;
  /* background matching the image: light blue to white gradient */
  background: linear-gradient(180deg, #E6F0FA 0%, #F4F8FB 20%, #F8FAFC 100%);
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  display: flex;
  flex-direction: column;
}

/* Header */
.header-section {
  padding: 100rpx 40rpx 40rpx;
  z-index: 10;
}
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-title {
  font-size: 60rpx;
  font-weight: 600;
  color: #1E293B;
  letter-spacing: 2rpx;
  display: block;
  margin-bottom: 12rpx;
}
.page-subtitle {
  font-size: 26rpx;
  color: #64748B;
}

/* Scroll Area */
.scroll-area {
  flex: 1;
  padding: 0 30rpx;
  box-sizing: border-box;
}

/* Segment Control */
.segment-control {
  position: relative;
  display: flex;
  background: #FFFFFF;
  border-radius: 40rpx;
  padding: 8rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.02);
}
.seg-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 30rpx;
  font-weight: 500;
  color: #64748B;
  position: relative;
  z-index: 2;
  transition: color 0.3s;
}
.seg-item.active {
  color: #1E293B;
  font-weight: 600;
}
.seg-slider {
  position: absolute;
  bottom: 12rpx;
  left: 0;
  width: 25%;
  height: 6rpx;
  display: flex;
  justify-content: center;
  z-index: 1;
  transition: transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}
.seg-slider::after {
  content: '';
  width: 32rpx;
  height: 6rpx;
  background: #3B82F6;
  border-radius: 6rpx;
}
/* Slider positions for 4 items */
.pos-overview { transform: translateX(0); }
.pos-chat { transform: translateX(100%); }
.pos-report { transform: translateX(200%); }
.pos-knowledge { transform: translateX(300%); }

.empty-state {
  padding: 100rpx 0;
  text-align: center;
}
.empty-text {
  font-size: 28rpx;
  color: #94A3B8;
}

.bottom-spacer {
  height: 220rpx;
}
</style>