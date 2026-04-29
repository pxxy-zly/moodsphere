<template>
  <view class="report-page">
    <view class="header-section">
      <text class="page-title">情绪洞察报告</text>
      
      <!-- Minimalist Segment Control -->
      <view class="segment-control">
        <view class="seg-item" :class="{active: range === 'day'}" @click="range = 'day'">今日</view>
        <view class="seg-item" :class="{active: range === 'week'}" @click="range = 'week'">本周</view>
        <view class="seg-item" :class="{active: range === 'month'}" @click="range = 'month'">本月</view>
        <view class="seg-slider" :class="'pos-' + range"></view>
      </view>
    </view>

    <scroll-view scroll-y class="scroll-area" :show-scrollbar="false">
      <!-- Main Trend Chart Card -->
      <view class="base-card trend-card">
        <view class="card-header">
          <text class="c-title">情绪波动趋势</text>
          <text class="c-sub">综合评分 8.2</text>
        </view>
        <view class="chart-container">
          <!-- Real SVG background acting as chart area -->
          <view class="chart-area"></view>
          <!-- Grid lines -->
          <view class="grid-lines">
             <view class="g-line"></view><view class="g-line"></view><view class="g-line"></view>
          </view>
          <view class="chart-labels">
            <text>周一</text><text>周二</text><text>周三</text><text>周四</text><text>周五</text><text>周六</text><text>周日</text>
          </view>
        </view>
      </view>

      <!-- 3 Small Cards Row -->
      <view class="row-cards">
        <!-- Donut Chart -->
        <view class="base-card flex-card">
          <text class="s-title">高频情绪</text>
          <view class="donut-chart"></view>
          <view class="legend-row">
            <text class="dot d1"></text><text>平静 45%</text>
          </view>
        </view>

        <!-- Gauge Chart -->
        <view class="base-card flex-card">
          <text class="s-title">波动指数</text>
          <view class="gauge-chart">
            <view class="gauge-inner">
               <text class="g-val">68</text>
            </view>
            <view class="gauge-needle"></view>
          </view>
          <text class="s-desc">稳定向好</text>
        </view>
      </view>

      <!-- Weather Calendar -->
      <view class="base-card cal-card">
         <text class="c-title">天气日历视图</text>
         <view class="cal-grid">
           <view v-for="d in 14" :key="d" class="cal-day">
             <text class="d-num">{{ d }}</text>
             <text class="d-icon">{{ d % 3 === 0 ? '🌧️' : (d % 2 === 0 ? '☁️' : '☀️') }}</text>
           </view>
         </view>
      </view>

      <!-- Ranked List / Highlight -->
      <view class="list-section">
        <text class="sec-title">高频触发场景</text>
        <view class="base-card list-card">
          <view class="list-item" v-for="(item, i) in rankList" :key="i">
            <text class="r-name">{{ item.name }}</text>
            <view class="r-bar-bg">
              <view class="r-bar-fill" :style="'width:' + item.percent + '%'"></view>
            </view>
            <text class="r-val">{{ item.percent }}%</text>
          </view>
        </view>
      </view>

      <!-- Highlights -->
      <view class="list-section">
        <text class="sec-title">我的高光时刻</text>
        <view class="base-card highlight-card">
          <view class="hlt-icon">✨</view>
          <view class="hlt-content">
            <text class="hlt-title">本周最开心的时刻</text>
            <text class="hlt-desc">“周三下午，终于把那个困扰已久的难题解决了，阳光刚好洒在桌面上。”</text>
          </view>
        </view>
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>
    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
export default {
  data() {
    return {
      range: 'week', // day, week, month
      rankList: [
        { name: '工作任务', percent: 65 },
        { name: '通勤路上', percent: 42 },
        { name: '个人成长', percent: 28 },
        { name: '家庭聚餐', percent: 15 }
      ]
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
  background: #F8FAFC; /* Clean, soft light greyish white */
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  display: flex;
  flex-direction: column;
}

/* Header */
.header-section {
  padding: 100rpx 40rpx 30rpx;
  background: #FFFFFF;
  z-index: 10;
  box-shadow: 0 4rpx 20rpx rgba(136,152,170,0.03);
}
.page-title {
  font-size: 44rpx; font-weight: 600; color: #1A202C; letter-spacing: 2rpx;
  margin-bottom: 30rpx; display: block;
}

/* Segment Control */
.segment-control {
  position: relative;
  display: flex;
  background: #F1F5F9;
  border-radius: 40rpx;
  padding: 8rpx;
}
.seg-item {
  flex: 1; text-align: center; padding: 14rpx 0;
  font-size: 26rpx; font-weight: 500; color: #64748B;
  position: relative; z-index: 2; transition: color 0.3s;
}
.seg-item.active { color: #1E293B; }
.seg-slider {
  position: absolute; top: 8rpx; bottom: 8rpx; left: 8rpx;
  width: calc(33.33% - 5rpx);
  background: #FFFFFF; border-radius: 34rpx;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.04);
  z-index: 1; transition: transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}
.pos-day { transform: translateX(0); }
.pos-week { transform: translateX(100%); width: 33.33%;}
.pos-month { transform: translateX(200%); width: calc(33.33% - 5rpx); }

/* Scroll Area */
.scroll-area {
  flex: 1; padding: 30rpx 40rpx; box-sizing: border-box;
}

/* Base Card Style */
.base-card {
  background: #FFFFFF;
  border-radius: 36rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(136, 152, 170, 0.05); /* Extremely soft shadow */
  margin-bottom: 30rpx;
}

/* Typography components */
.c-title { font-size: 32rpx; font-weight: 600; color: #2D3748; }
.c-sub { font-size: 24rpx; color: #A0AEC0; float: right; margin-top: 6rpx; font-weight: 500; }
.s-title { font-size: 26rpx; font-weight: 600; color: #4A5568; margin-bottom: 24rpx; }

/* Chart Area */
.chart-container {
  height: 320rpx; margin-top: 40rpx; position: relative;
}
.chart-area {
  position: absolute; top: 0; left: 0; right: 0; height: 260rpx;
  /* An elegant SVG area chart with a gradient line and semi-transparent fill plotted using pure CSS encoded DataURI */
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 40' preserveAspectRatio='none'%3E%3Cdefs%3E%3ClinearGradient id='grad' x1='0%25' y1='0%25' x2='100%25' y2='0%25'%3E%3Cstop offset='0%25' stop-color='%23FF9A9E' stop-opacity='0.4'/%3E%3Cstop offset='50%25' stop-color='%23FECFEF' stop-opacity='0.3'/%3E%3Cstop offset='100%25' stop-color='%23A1C4FD' stop-opacity='0.1'/%3E%3C/linearGradient%3E%3ClinearGradient id='lineG' x1='0%25' y1='0%25' x2='100%25' y2='0%25'%3E%3Cstop offset='0%25' stop-color='%23FF9A9E'/%3E%3Cstop offset='50%25' stop-color='%23FECFEF'/%3E%3Cstop offset='100%25' stop-color='%23A1C4FD'/%3E%3C/linearGradient%3E%3C/defs%3E%3Cpath d='M0,35 C15,20 30,30 45,15 C60,0 80,25 100,5 L100,40 L0,40 Z' fill='url(%23grad)'/%3E%3Cpath d='M0,35 C15,20 30,30 45,15 C60,0 80,25 100,5' fill='none' stroke='url(%23lineG)' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E") no-repeat;
  background-size: 100% 100%;
  animation: fadeUp 1s ease-out;
}
.grid-lines {
  position: absolute; top: 0; left: 0;  right: 0; height: 260rpx;
  display: flex; flex-direction: column; justify-content: space-between;
}
.g-line { border-bottom: 2rpx dashed #F1F5F9; width: 100%; }
.chart-labels {
  position: absolute; bottom: 0; left: 0; right: 0;
  display: flex; justify-content: space-between;
  font-size: 22rpx; color: #94A3B8;
}

/* Row Cards */
.row-cards { display: flex; gap: 30rpx; margin-bottom: 30rpx; }
.flex-card {
  flex: 1; margin-bottom: 0; padding: 36rpx;
  display: flex; flex-direction: column; justify-content: center; align-items: center;
}

/* Donut Chart */
.donut-chart {
  width: 170rpx; height: 170rpx; border-radius: 50%;
  background: conic-gradient(#FFD1D1 0% 30%, #E6E6FA 30% 55%, #E0FFFF 55% 85%, #F1F5F9 85% 100%);
  position: relative; margin-bottom: 20rpx;
}
.donut-chart::after {
  content: ''; position: absolute; top: 22%; left: 22%; right: 22%; bottom: 22%;
  background: #FFF; border-radius: 50%; box-shadow: inset 0 2rpx 8rpx rgba(136,152,170,0.1);
}
.legend-row { font-size: 24rpx; color: #718096; display: flex; align-items: center; }
.dot { width: 14rpx; height: 14rpx; background: #FFD1D1; border-radius: 50%; margin-right: 10rpx; }

/* Gauge Chart */
.gauge-chart {
  width: 180rpx; height: 90rpx;
  border-radius: 90rpx 90rpx 0 0;
  background: conic-gradient(from 180deg at 50% 100%, 
    #A1C4FD 0deg, #FECFEF 90deg, #FFD1D1 180deg
  );
  position: relative; overflow: hidden; margin-bottom: 24rpx;
}
.gauge-inner {
  position: absolute; top: 16rpx; left: 16rpx; right: 16rpx; bottom: 0;
  background: #FFF; border-radius: 70rpx 70rpx 0 0;
  display: flex; justify-content: center; align-items: flex-end; padding-bottom: 4rpx;
}
.g-val { font-size: 40rpx; font-weight: 600; color: #2D3748; font-family: "DIN Condensed", sans-serif;}
.gauge-needle {
  position: absolute; bottom: -8rpx; left: 50%; width: 6rpx; height: 66rpx;
  background: #4A5568; transform-origin: bottom center; transform: rotate(55deg);
  border-radius: 6rpx; margin-left: -3rpx; box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.15);
  transition: transform 1s cubic-bezier(0.4, 0.0, 0.2, 1);
}
.s-desc { font-size: 24rpx; color: #A1C4FD; font-weight: 600; letter-spacing: 2rpx; }

/* Calendar Grid */
.cal-grid {
  display: grid; grid-template-columns: repeat(7, 1fr);
  gap: 16rpx; margin-top: 36rpx;
}
.cal-day {
  display: flex; flex-direction: column; align-items: center;
  padding: 16rpx 0; background: #F8FAFC; border-radius: 16rpx;
}
.d-num { font-size: 20rpx; color: #94A3B8; margin-bottom: 8rpx; font-weight: 500;}
.d-icon { font-size: 32rpx; }

/* List Section */
.sec-title { font-size: 30rpx; font-weight: 600; color: #1E293B; margin: 10rpx 0 24rpx; display: block; padding-left: 8rpx;}
.list-card { padding: 40rpx; }
.list-item { display: flex; align-items: center; margin-bottom: 30rpx; }
.list-item:last-child { margin-bottom: 0; }
.r-name { font-size: 26rpx; color: #4A5568; width: 140rpx; font-weight: 500;}
.r-bar-bg { flex: 1; height: 16rpx; background: #F1F5F9; border-radius: 8rpx; margin: 0 24rpx; overflow: hidden;}
.r-bar-fill { height: 100%; background: linear-gradient(90deg, #A8EDE9 0%, #FED6E3 100%); border-radius: 8rpx; }
.r-val { font-size: 26rpx; color: #64748B; width: 60rpx; text-align: right; font-weight: 500;}

/* Highlight Card */
.highlight-card {
  display: flex; align-items: flex-start;
  background: linear-gradient(135deg, #FFFFFF 0%, #F8FAFC 100%);
  border: 1px solid #F1F5F9; padding: 40rpx;
}
.hlt-icon { font-size: 44rpx; margin-right: 24rpx; text-shadow: 0 4rpx 10rpx rgba(255, 215, 0, 0.4);}
.hlt-content { flex: 1; }
.hlt-title { font-size: 28rpx; font-weight: 600; color: #2D3748; margin-bottom: 12rpx; display: block;}
.hlt-desc { font-size: 26rpx; color: #64748B; line-height: 1.6; font-style: italic;}

.bottom-spacer { height: 220rpx; }

@keyframes fadeUp {
  0% { opacity: 0; transform: translateY(10rpx); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>
