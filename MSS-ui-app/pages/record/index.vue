<template>
  <view class="record-page">
    <view class="panel">
      <view class="panel-title">记录此刻情绪</view>
      <textarea
        v-model="form.contentText"
        class="content-input"
        maxlength="500"
        placeholder="今天发生了什么？先写下你最真实的感受"
      />
      <view class="counter">{{ form.contentText.length }}/500</view>

      <view class="form-row">
        <text class="label">情绪强度</text>
        <text class="value">{{ form.emotionIntensity }}</text>
      </view>
      <slider
        class="slider"
        :value="form.emotionIntensity"
        :min="1"
        :max="10"
        :step="1"
        activeColor="#1989fa"
        @change="handleIntensityChange"
      />

      <view class="form-row switch-row">
        <text class="label">公开到星球</text>
        <switch :checked="form.isPublic === 1" color="#1989fa" @change="handlePublicChange" />
      </view>

      <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '提交中...' : '提交并生成天气' }}
      </button>
    </view>
  </view>
</template>

<script>
import { createMoodRecord, runMoodAnalyze, buildMoodVector, generateMoodWeather } from '@/api/mood'

export default {
  data() {
    return {
      submitting: false,
      form: {
        contentText: '',
        emotionIntensity: 5,
        isPublic: 0
      }
    }
  },
  methods: {
    handleIntensityChange(event) {
      this.form.emotionIntensity = event.detail.value
    },
    handlePublicChange(event) {
      this.form.isPublic = event.detail.value ? 1 : 0
    },
    async handleSubmit() {
      const contentText = (this.form.contentText || '').trim()
      if (!contentText) {
        this.$modal.msgError('请先输入记录内容')
        return
      }
      if (this.submitting) {
        return
      }
      this.submitting = true
      this.$modal.loading('正在生成结果，请稍候...')
      try {
        const createRes = await createMoodRecord({
          contentText,
          emotionIntensity: this.form.emotionIntensity,
          isPublic: this.form.isPublic
        })
        const recordId = createRes.recordId
        if (!recordId) {
          throw new Error('未获取到记录ID')
        }

        await runMoodAnalyze(recordId)
        await buildMoodVector(recordId)
        await generateMoodWeather(recordId)

        this.$modal.closeLoading()
        this.$tab.navigateTo(`/pages/record/result?recordId=${recordId}`)
      } catch (error) {
        this.$modal.closeLoading()
        this.$modal.msgError(this.parseError(error))
      } finally {
        this.submitting = false
      }
    },
    parseError(error) {
      if (!error) {
        return '提交失败，请稍后重试'
      }
      if (typeof error === 'string') {
        return error
      }
      if (error.msg) {
        return error.msg
      }
      if (error.message) {
        return error.message
      }
      return '提交失败，请稍后重试'
    }
  }
}
</script>

<style scoped>
.record-page {
  min-height: 100vh;
  padding: 32rpx;
  background: linear-gradient(180deg, #f5f9ff 0%, #ffffff 80%);
}

.panel {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 10rpx 24rpx rgba(16, 70, 128, 0.08);
}

.panel-title {
  font-size: 36rpx;
  color: #1d2b3a;
  font-weight: 600;
}

.content-input {
  margin-top: 24rpx;
  width: 100%;
  min-height: 260rpx;
  border-radius: 16rpx;
  background: #f7f9fc;
  padding: 22rpx;
  box-sizing: border-box;
  font-size: 30rpx;
  line-height: 1.6;
}

.counter {
  margin-top: 12rpx;
  text-align: right;
  color: #94a3b8;
  font-size: 24rpx;
}

.form-row {
  margin-top: 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  color: #334155;
  font-size: 28rpx;
}

.value {
  color: #1989fa;
  font-size: 30rpx;
  font-weight: 600;
}

.slider {
  margin-top: 10rpx;
}

.switch-row {
  margin-top: 12rpx;
}

.submit-btn {
  margin-top: 40rpx;
  background: #1989fa;
  color: #ffffff;
  border-radius: 48rpx;
  font-size: 30rpx;
}

.submit-btn[disabled] {
  opacity: 0.7;
}
</style>
