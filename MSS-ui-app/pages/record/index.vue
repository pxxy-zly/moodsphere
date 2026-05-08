<template>
  <view class="record-page">
    <scroll-view scroll-y class="record-scroll" :show-scrollbar="false">
      <view class="hero-section">
        <text class="hero-kicker">MOOD RECORD</text>
        <text class="hero-title">今天想记录什么？</text>
        <text class="hero-desc">写几句也可以，不需要整理得很完整。</text>
      </view>

      <view class="input-card">
        <textarea
          v-model="form.contentText"
          class="content-input"
          maxlength="500"
          placeholder="比如：今天会议结束后松了一口气，但也有点空..."
          placeholder-class="content-placeholder"
          :show-confirm-bar="false"
          :auto-height="false"
        />
        <view class="input-actions">
          <view class="action-icons">
            <view class="upload-action upload-action-voice" :class="{ 'upload-action-recording': recording }" @click="handleChooseVoice">
              <view class="upload-action-icon">
                <image v-if="!recording" class="upload-action-icon-image" src="/static/images/record/voice.svg" mode="aspectFit"></image>
                <text v-else class="icon-text icon-text-stop">停</text>
              </view>
              <view class="upload-action-copy">
                <text class="upload-action-title">{{ recording ? '停止录音' : '语音记录' }}</text>
                <text class="upload-action-desc">{{ recording ? '再次点击结束上传' : '点击录音后上传' }}</text>
              </view>
            </view>
            <view class="upload-action upload-action-image" @click="handleChooseImage">
              <view class="upload-action-icon">
                <image class="upload-action-icon-image" src="/static/images/record/image.svg" mode="aspectFit"></image>
              </view>
              <view class="upload-action-copy">
                <text class="upload-action-title">图片上传</text>
                <text class="upload-action-desc">相册或拍照上传</text>
              </view>
            </view>
          </view>
          <view class="counter" :class="{ 'counter-warn': form.contentText.length > 430 }">
            {{ form.contentText.length }}/500
          </view>
        </view>
        <view v-if="recording || recordingHint" class="recording-status" :class="{ 'recording-active': recording }">
          <text class="recording-dot" v-if="recording"></text>
          <text>{{ recording ? `录音中 ${formatVoiceDuration(recordingSeconds)}` : recordingHint }}</text>
        </view>
      </view>

      <view v-if="uploading || imageAssets.length || voiceAsset" class="panel-card asset-card">
        <view class="section-title-row">
          <view>
            <text class="section-title">已添加素材</text>
            <text class="section-desc">图片和语音会随本次记录一起保存</text>
          </view>
          <view v-if="uploading" class="uploading-tag">上传中...</view>
        </view>

        <view v-if="imageAssets.length" class="asset-subtitle">图片</view>
        <view v-if="imageAssets.length" class="image-grid">
          <view v-for="(item, index) in imageAssets" :key="item.assetId || item.fileUrl || index" class="image-item">
            <image class="image-thumb" :src="item.thumbnailUrl || item.fileUrl" mode="aspectFill" @click="previewImages(index)"></image>
            <view class="remove-badge" @click.stop="removeImageAsset(index)">×</view>
          </view>
        </view>

        <view v-if="voiceAsset" class="asset-subtitle voice-subtitle">语音</view>
        <view v-if="voiceAsset" class="voice-card">
          <view class="voice-meta">
            <text class="voice-icon">🎧</text>
            <view class="voice-text">
              <text class="voice-name">{{ voiceAsset.originalFileName || '语音素材' }}</text>
              <text class="voice-desc">{{ formatVoiceDuration(voiceAsset.duration) }}</text>
            </view>
          </view>
          <view class="remove-text" @click="removeVoiceAsset">移除</view>
        </view>
      </view>

      <view class="panel-card intensity-card">
        <view class="section-title-row">
          <view>
            <text class="section-title">情绪强度</text>
            <text class="section-desc">{{ intensityText }}</text>
          </view>
          <view class="intensity-badge">{{ form.emotionIntensity }}</view>
        </view>
        <view class="slider-wrapper">
          <view class="gradient-track"></view>
          <slider
            class="real-slider"
            :value="form.emotionIntensity"
            :min="1"
            :max="10"
            :step="1"
            activeColor="transparent"
            backgroundColor="transparent"
            block-color="#ffffff"
            block-size="28"
            @change="handleIntensityChange"
          />
        </view>
        <view class="slider-labels">
          <text>轻微</text>
          <text>明显</text>
          <text>强烈</text>
        </view>
      </view>

      <view class="panel-card quick-card">
        <view class="section-title-row">
          <view>
            <text class="section-title">快捷选择</text>
            <text class="section-desc">点选后会自动加入记录内容</text>
          </view>
          <view class="clear-tags" v-if="selectedTagMap.length" @click="clearSelectedTags">清空</view>
        </view>

        <view class="sub-title">快捷情绪</view>
        <view class="chip-grid">
          <view
            v-for="tag in moodTags"
            :key="tag.t"
            class="mood-chip"
            :class="[{ 'chip-selected': isTagSelected(tag.t) }, 'chip-tone-' + tag.tone]"
            @click="toggleTag(tag.t)"
          >
            <text class="chip-emoji">{{ tag.e }}</text>
            <text class="chip-text">{{ tag.t }}</text>
          </view>
        </view>

        <view class="sub-title scene-title">生活场景</view>
        <view class="scene-tags">
          <view
            v-for="sc in sceneTags"
            :key="sc.t"
            class="scene-pill"
            :class="{ 'scene-pill-selected': isTagSelected(sc.t) }"
            @click="toggleTag(sc.t)"
          >
            <text class="scene-emoji">{{ sc.e }}</text>
            <text>{{ sc.t }}</text>
          </view>
        </view>
      </view>

      <view class="panel-card privacy-card">
        <view class="privacy-copy">
          <text class="section-title">匿名漂到情绪星球</text>
          <text class="section-desc">仅展示匿名心情片段，不展示个人身份。</text>
        </view>
        <switch :checked="form.isPublic === 1" color="#5C9CE6" @change="handlePublicChange" style="transform:scale(0.82)" />
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>

    <view class="submit-dock">
      <button class="submit-btn" :class="{ 'btn-loading': submitting }" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '生成天气中...' : '提交感受' }}
      </button>
    </view>

    <view v-if="showAnalyzeOverlay" class="analyze-overlay">
      <view class="analyze-overlay__glow"></view>
      <view class="analyze-overlay__content">
        <view class="analyze-orb">
          <view class="analyze-orb__inner"></view>
        </view>
        <text class="analyze-overlay__kicker">MOOD WEATHER</text>
        <text class="analyze-overlay__title">{{ analyzeStageText }}</text>
        <text class="analyze-overlay__desc">{{ analyzeStageDesc }}</text>
        <view class="analyze-progress">
          <view class="analyze-progress__track">
            <view class="analyze-progress__bar" :style="{ width: `${analyzeProgress}%` }"></view>
          </view>
          <text class="analyze-progress__text">{{ analyzeProgress }}%</text>
        </view>
        <view class="analyze-steps">
          <view
            v-for="(step, index) in analyzeSteps"
            :key="step.key"
            class="analyze-step"
            :class="{
              'analyze-step--active': index === analyzeStage,
              'analyze-step--done': index < analyzeStage
            }"
          >
            <view class="analyze-step__dot"></view>
            <text class="analyze-step__label">{{ step.label }}</text>
          </view>
        </view>
      </view>
    </view>

    <custom-tab-bar ref="customTabBar"></custom-tab-bar>
  </view>
</template>

<script>
import { createMoodRecord, runMoodAnalyze, buildMoodVector, generateMoodWeather } from '@/api/mood'
import { uploadMoodImage, uploadMoodVoice } from '@/api/asset'

export default {
  data() {
    return {
      submitting: false,
      uploading: false,
      recording: false,
      recordingSeconds: 0,
      recordingHint: '',
      showAnalyzeOverlay: false,
      analyzeStage: 0,
      analyzeStageText: '',
      analyzeStageDesc: '',
      analyzeProgress: 0,
      selectedTagMap: [],
      imageAssets: [],
      voiceAsset: null,
      form: {
        contentText: '',
        emotionIntensity: 5,
        isPublic: 0
      },
      moodTags: [
        { t: '开心', e: '😊', tone: 'sun' },
        { t: '平静', e: '😌', tone: 'calm' },
        { t: '焦虑', e: '😰', tone: 'mist' },
        { t: '疲惫', e: '😮‍💨', tone: 'rain' },
        { t: '委屈', e: '💧', tone: 'rain' },
        { t: '孤独', e: '🌙', tone: 'night' },
        { t: '烦躁', e: '🌩️', tone: 'storm' },
        { t: '期待', e: '🌱', tone: 'calm' },
        { t: '困惑', e: '🌫️', tone: 'mist' },
        { t: '温暖', e: '☀️', tone: 'sun' }
      ],
      sceneTags: [
        { t: '工作', e: '💼' },
        { t: '学习', e: '📚' },
        { t: '家庭', e: '🏠' },
        { t: '关系', e: '🤝' },
        { t: '通勤', e: '🚌' },
        { t: '睡眠', e: '🛌' },
        { t: '运动', e: '🏃' },
        { t: '饮食', e: '🍜' },
        { t: '独处', e: '🫧' },
        { t: '社交', e: '🎈' }
      ],
      analyzeSteps: [
        { key: 'save', label: '保存', title: '收集此刻的心情', desc: '先把这一刻好好保存下来' },
        { key: 'analyze', label: '识别', title: '识别情绪里的线索', desc: '正在理解文字、图片或语音里的感受' },
        { key: 'weather', label: '天气', title: '生成你的专属天气', desc: '把情绪慢慢酿成今天的天气' }
      ]
    }
  },
  computed: {
    intensityText() {
      const value = this.form.emotionIntensity
      if (value <= 3) return `${value} · 轻微浮现`
      if (value <= 6) return `${value} · 有一点明显`
      if (value <= 8) return `${value} · 很需要被看见`
      return `${value} · 强烈涌上来`
    }
  },
  onShow() {
    this.$store.dispatch('setTabBarSelected', 2)
  },
  onLoad() {
    this.initRecorderManager()
  },
  onHide() {
    this.closeAnalyzeOverlay()
    this.stopRecordingIfNeeded(true)
  },
  onUnload() {
    this.closeAnalyzeOverlay()
    this.stopRecordingIfNeeded(true)
  },
  methods: {
    async handleChooseImage() {
      if (this.uploading) return
      try {
        const res = await this.chooseImageFiles()
        const filePaths = (res.tempFilePaths || []).slice(0, Math.max(0, 3 - this.imageAssets.length))
        if (!filePaths.length) return
        this.uploading = true
        for (const filePath of filePaths) {
          const asset = await uploadMoodImage(filePath)
          this.imageAssets.push(asset)
        }
      } catch (error) {
        if (error && error.errMsg && error.errMsg.includes('cancel')) return
        uni.showToast({ title: this.parseError(error), icon: 'none' })
      } finally {
        this.uploading = false
      }
    },
    async handleChooseVoice() {
      if (this.uploading) return
      if (this.recorderManager) {
        if (this.recording) {
          this.stopVoiceRecord()
        } else {
          await this.startVoiceRecord()
        }
        return
      }
      await this.chooseVoiceFileAndUpload()
    },
    initRecorderManager() {
      if (typeof uni.getRecorderManager !== 'function') return
      this.recorderManager = uni.getRecorderManager()
      if (this._recorderBound) return
      this._recorderBound = true
      this.recorderManager.onStop(async (res) => {
        const shouldDiscard = this._discardRecording === true
        this._discardRecording = false
        this.clearRecordTimer()
        this.recording = false
        if (shouldDiscard) {
          this.recordingHint = ''
          return
        }
        if (!res || !res.tempFilePath) {
          this.recordingHint = '录音失败，请重试'
          return
        }
        try {
          this.uploading = true
          this.recordingHint = '语音上传中...'
          const duration = Math.max(1, Math.round((res.duration || this.recordingSeconds * 1000) / 1000))
          this.voiceAsset = await uploadMoodVoice(res.tempFilePath, duration)
          this.recordingHint = '语音已添加'
        } catch (error) {
          this.recordingHint = ''
          uni.showToast({ title: this.parseError(error), icon: 'none' })
        } finally {
          this.uploading = false
        }
      })
      this.recorderManager.onError(() => {
        this.clearRecordTimer()
        this.recording = false
        this.recordingHint = '录音失败，请重试'
      })
    },
    async startVoiceRecord() {
      try {
        await this.ensureRecordAuth()
      } catch (error) {
        if (error && error.errMsg && error.errMsg.includes('cancel')) return
        uni.showToast({ title: '需要录音权限才能使用语音记录', icon: 'none' })
        return
      }
      this.initRecorderManager()
      if (!this.recorderManager) {
        await this.chooseVoiceFileAndUpload()
        return
      }
      this.removeVoiceAsset()
      this.recordingHint = '再次点击麦克风可结束录音'
      this.recordingSeconds = 0
      this.recording = true
      this.startRecordTimer()
      this.recorderManager.start({
        duration: 60000,
        sampleRate: 16000,
        numberOfChannels: 1,
        encodeBitRate: 96000,
        format: 'mp3'
      })
    },
    stopVoiceRecord() {
      if (!this.recorderManager || !this.recording) return
      this.recorderManager.stop()
    },
    async chooseVoiceFileAndUpload() {
      if (this.uploading) return
      try {
        const file = await this.chooseVoiceFile()
        if (!file || !file.path) return
        this.uploading = true
        this.voiceAsset = await uploadMoodVoice(file.path, file.time || 0)
      } catch (error) {
        if (error && error.errMsg && error.errMsg.includes('cancel')) return
        uni.showToast({ title: this.parseError(error), icon: 'none' })
      } finally {
        this.uploading = false
      }
    },
    ensureRecordAuth() {
      return new Promise((resolve, reject) => {
        uni.authorize({
          scope: 'scope.record',
          success: resolve,
          fail: reject
        })
      })
    },
    startRecordTimer() {
      this.clearRecordTimer()
      this._recordTimer = setInterval(() => {
        this.recordingSeconds += 1
      }, 1000)
    },
    clearRecordTimer() {
      if (this._recordTimer) {
        clearInterval(this._recordTimer)
        this._recordTimer = null
      }
    },
    stopRecordingIfNeeded(discard = false) {
      if (!this.recorderManager || !this.recording) return
      this._discardRecording = discard
      this.recorderManager.stop()
    },
    chooseImageFiles() {
      return new Promise((resolve, reject) => {
        uni.chooseImage({
          count: 3,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera'],
          success: resolve,
          fail: reject
        })
      })
    },
    chooseVoiceFile() {
      return new Promise((resolve, reject) => {
        uni.chooseMessageFile({
          count: 1,
          type: 'file',
          extension: ['mp3', 'wav', 'm4a', 'aac'],
          success: (res) => resolve((res.tempFiles || [])[0]),
          fail: reject
        })
      })
    },
    previewImages(currentIndex) {
      uni.previewImage({
        urls: this.imageAssets.map(item => item.fileUrl),
        current: this.imageAssets[currentIndex].fileUrl
      })
    },
    removeImageAsset(index) {
      this.imageAssets.splice(index, 1)
    },
    removeVoiceAsset() {
      this.voiceAsset = null
      if (!this.recording) {
        this.recordingHint = ''
      }
    },
    formatVoiceDuration(duration) {
      const total = Number(duration || 0)
      if (!total) return '时长待获取'
      const minutes = String(Math.floor(total / 60)).padStart(2, '0')
      const seconds = String(total % 60).padStart(2, '0')
      return `${minutes}:${seconds}`
    },
    isTagSelected(tag) {
      return this.selectedTagMap.includes(tag)
    },
    toggleTag(tag) {
      if (this.isTagSelected(tag)) {
        this.selectedTagMap = this.selectedTagMap.filter(item => item !== tag)
        return
      }
      this.selectedTagMap.push(tag)
      this.appendTag(tag)
    },
    clearSelectedTags() {
      this.selectedTagMap = []
    },
    appendTag(txt) {
      const cleanTxt = String(txt || '').trim()
      if (!cleanTxt || this.form.contentText.includes(cleanTxt)) return
      const nextText = this.form.contentText + (this.form.contentText ? '，' : '') + cleanTxt
      if (nextText.length > 500) {
        uni.showToast({ title: '内容已接近上限', icon: 'none' })
        return
      }
      this.form.contentText = nextText
    },
    mockAction(type) {
      if (this.$modal && this.$modal.msgError) {
        this.$modal.msgError(`${type} 暂未开放`)
      } else {
        uni.showToast({ title: `${type} 暂未开放`, icon: 'none' })
      }
    },
    handleIntensityChange(event) {
      this.form.emotionIntensity = event.detail.value
    },
    handlePublicChange(event) {
      this.form.isPublic = event.detail.value ? 1 : 0
    },
    openAnalyzeOverlay() {
      this.showAnalyzeOverlay = true
      this.analyzeProgress = 6
      this.setAnalyzeStage(0)
      this.startAnalyzeProgress()
    },
    closeAnalyzeOverlay() {
      this.stopAnalyzeProgress()
      this.showAnalyzeOverlay = false
      this.analyzeStage = 0
      this.analyzeStageText = ''
      this.analyzeStageDesc = ''
      this.analyzeProgress = 0
    },
    setAnalyzeStage(index) {
      const step = this.analyzeSteps[index] || this.analyzeSteps[0]
      this.analyzeStage = index
      this.analyzeStageText = step.title
      this.analyzeStageDesc = step.desc
      const minProgressMap = [12, 46, 78]
      const minProgress = minProgressMap[index] || 12
      if (this.analyzeProgress < minProgress) {
        this.analyzeProgress = minProgress
      }
    },
    startAnalyzeProgress() {
      this.stopAnalyzeProgress()
      this._analyzeProgressTimer = setInterval(() => {
        const maxProgressMap = [36, 72, 94]
        const maxProgress = maxProgressMap[this.analyzeStage] || 94
        if (this.analyzeProgress < maxProgress) {
          const delta = this.analyzeStage === 2 ? 1 : 2
          this.analyzeProgress = Math.min(maxProgress, this.analyzeProgress + delta)
        }
      }, 280)
    },
    stopAnalyzeProgress() {
      if (this._analyzeProgressTimer) {
        clearInterval(this._analyzeProgressTimer)
        this._analyzeProgressTimer = null
      }
    },
    async handleSubmit() {
      const contentText = (this.form.contentText || '').trim()
      const assetIds = this.collectAssetIds()
      if (!contentText && !assetIds.length) {
        if (this.$modal && this.$modal.msgError) this.$modal.msgError('请先输入内容，或上传图片/语音')
        else uni.showToast({ title: '请先输入内容，或上传图片/语音', icon: 'none' })
        return
      }
      if (this.submitting || this.uploading) {
        return
      }
      this.submitting = true
      this.openAnalyzeOverlay()

      try {
        const createRes = await createMoodRecord({
          contentText,
          emotionIntensity: this.form.emotionIntensity,
          isPublic: this.form.isPublic,
          assetIds,
          voiceDuration: this.voiceAsset ? this.voiceAsset.duration : 0
        })
        const recordId = createRes.recordId
        if (!recordId) {
          throw new Error('未获取到记录ID')
        }

        if (!contentText) {
          this.closeAnalyzeOverlay()
          uni.showToast({ title: '素材记录已保存，文本分析可稍后补充', icon: 'none' })
          this.resetForm()
          return
        }

        this.setAnalyzeStage(1)
        await runMoodAnalyze(recordId)
        this.setAnalyzeStage(2)
        await buildMoodVector(recordId)
        await generateMoodWeather(recordId)

        this.closeAnalyzeOverlay()
        this.resetForm()
        this.$tab.navigateTo(`/pages/record/result?recordId=${recordId}`)
      } catch (error) {
        this.closeAnalyzeOverlay()
        if (this.$modal && this.$modal.msgError) this.$modal.msgError(this.parseError(error))
        else uni.showToast({ title: this.parseError(error), icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
    parseError(error) {
      if (!error) return '提交失败，请稍后重试'
      if (typeof error === 'string') return error
      if (error.msg) return error.msg
      if (error.message) return error.message
      return '提交失败，请稍后重试'
    },
    collectAssetIds() {
      const assetIds = this.imageAssets.map(item => item.assetId)
      if (this.voiceAsset && this.voiceAsset.assetId) {
        assetIds.push(this.voiceAsset.assetId)
      }
      return assetIds
    },
    resetForm() {
      this.stopRecordingIfNeeded(true)
      this.form.contentText = ''
      this.form.emotionIntensity = 5
      this.form.isPublic = 0
      this.selectedTagMap = []
      this.imageAssets = []
      this.voiceAsset = null
      this.recording = false
      this.recordingSeconds = 0
      this.recordingHint = ''
      this.clearRecordTimer()
    }
  }
}
</script>

<style scoped>
.record-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  background:
    radial-gradient(circle at 8% 8%, rgba(92, 156, 230, 0.14) 0%, transparent 32%),
    linear-gradient(180deg, #f3f8ff 0%, #f8fbff 50%, #ffffff 100%);
  box-sizing: border-box;
}

.record-scroll {
  width: 100%;
  height: 100%;
}

.hero-section {
  padding: 78rpx 36rpx 28rpx;
}

.hero-kicker {
  display: block;
  margin-bottom: 12rpx;
  color: #5c9ce6;
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
}

.hero-title {
  display: block;
  color: #233044;
  font-size: 48rpx;
  font-weight: 800;
  line-height: 1.2;
}

.hero-desc {
  display: block;
  margin-top: 14rpx;
  color: #7b8798;
  font-size: 27rpx;
  line-height: 1.45;
}

.input-card,
.panel-card {
  margin: 0 32rpx 28rpx;
  background: rgba(255, 255, 255, 0.78);
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 32rpx;
  box-shadow: 0 14rpx 40rpx rgba(92, 156, 230, 0.08);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-sizing: border-box;
}

.input-card {
  padding: 34rpx 34rpx 26rpx;
}

.content-input {
  width: 100%;
  height: 380rpx;
  font-size: 31rpx;
  color: #263449;
  line-height: 1.7;
}

.content-placeholder {
  color: #a8b2c2;
  font-size: 30rpx;
  line-height: 1.65;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 22rpx;
  padding-top: 22rpx;
  border-top: 1rpx solid rgba(151, 166, 190, 0.16);
}

.action-icons {
  display: flex;
  flex: 1;
  gap: 16rpx;
}

.upload-action {
  min-width: 0;
  flex: 1;
  min-height: 92rpx;
  padding: 14rpx 16rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  background: rgba(248, 251, 255, 0.92);
  border: 1rpx solid rgba(151, 166, 190, 0.14);
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.88), 0 6rpx 18rpx rgba(92, 156, 230, 0.06);
  transition: all 0.2s ease;
}

.upload-action:active {
  transform: scale(0.95);
  box-shadow: 0 4rpx 12rpx rgba(92, 156, 230, 0.08);
}

.upload-action-voice {
  background: linear-gradient(135deg, rgba(242, 248, 255, 0.98) 0%, rgba(236, 243, 255, 0.94) 100%);
}

.upload-action-image {
  background: linear-gradient(135deg, rgba(255, 249, 244, 0.98) 0%, rgba(255, 244, 236, 0.94) 100%);
}

.upload-action-recording {
  background: linear-gradient(135deg, rgba(255, 239, 243, 0.98) 0%, rgba(255, 231, 237, 0.96) 100%);
  border-color: rgba(255, 109, 135, 0.22);
  box-shadow: 0 8rpx 22rpx rgba(255, 109, 135, 0.14);
}

.upload-action-icon {
  width: 56rpx;
  height: 56rpx;
  margin-right: 12rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.8);
  flex-shrink: 0;
}

.upload-action-icon-image {
  width: 30rpx;
  height: 30rpx;
}

.upload-action-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.upload-action-title {
  color: #233044;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.25;
  white-space: nowrap;
}

.upload-action-desc {
  margin-top: 2rpx;
  color: #7d899b;
  font-size: 20rpx;
  line-height: 1.25;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.icon-text {
  font-size: 26rpx;
  font-weight: 700;
  color: #ff6d87;
}

.icon-text-stop {
  letter-spacing: 2rpx;
}

.recording-status {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
  color: #7d899b;
  font-size: 24rpx;
}

.recording-active {
  color: #ff6d87;
}

.recording-dot {
  width: 14rpx;
  height: 14rpx;
  margin-right: 10rpx;
  border-radius: 50%;
  background: #ff6d87;
  box-shadow: 0 0 0 10rpx rgba(255, 109, 135, 0.12);
}

.counter {
  color: #97a3b6;
  font-size: 24rpx;
}

.counter-warn {
  color: #ff8da1;
}

.panel-card {
  padding: 30rpx 32rpx;
}

.asset-card {
  padding-bottom: 26rpx;
}

.uploading-tag {
  color: #5c9ce6;
  font-size: 24rpx;
  font-weight: 600;
}

.asset-subtitle {
  color: #7d899b;
  font-size: 25rpx;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.voice-subtitle {
  margin-top: 24rpx;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.image-item {
  position: relative;
  height: 180rpx;
  border-radius: 24rpx;
  overflow: hidden;
}

.image-thumb {
  width: 100%;
  height: 100%;
  display: block;
  background: #edf4ff;
}

.remove-badge {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(35, 48, 68, 0.72);
  color: #fff;
  font-size: 26rpx;
}

.voice-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx;
  border-radius: 24rpx;
  background: #f5f9ff;
  border: 1rpx solid rgba(92, 156, 230, 0.12);
}

.voice-meta {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.voice-icon {
  font-size: 34rpx;
  margin-right: 18rpx;
}

.voice-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.voice-name {
  color: #263449;
  font-size: 27rpx;
  font-weight: 600;
  max-width: 420rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.voice-desc {
  margin-top: 6rpx;
  color: #7d899b;
  font-size: 22rpx;
}

.remove-text {
  color: #5c9ce6;
  font-size: 24rpx;
  font-weight: 600;
  padding-left: 20rpx;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  color: #263449;
  font-size: 31rpx;
  font-weight: 700;
  line-height: 1.25;
}

.section-desc {
  display: block;
  margin-top: 9rpx;
  color: #7d899b;
  font-size: 24rpx;
  line-height: 1.45;
}

.intensity-badge {
  min-width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 800;
  background: #5c9ce6;
  box-shadow: 0 10rpx 24rpx rgba(92, 156, 230, 0.18);
}

.slider-wrapper {
  position: relative;
  height: 62rpx;
  display: flex;
  align-items: center;
}

.gradient-track {
  position: absolute;
  top: 50%;
  left: 24rpx;
  right: 24rpx;
  height: 12rpx;
  transform: translateY(-50%);
  border-radius: 999rpx;
  background: linear-gradient(90deg, #dbeafe 0%, #93c5fd 52%, #5c9ce6 100%);
  z-index: 1;
}

.real-slider {
  width: 100%;
  margin: 0;
  z-index: 2;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  color: #97a3b6;
  font-size: 22rpx;
  padding: 0 8rpx;
}

.quick-card {
  padding-bottom: 34rpx;
}

.clear-tags {
  color: #5c9ce6;
  font-size: 24rpx;
  font-weight: 600;
  padding: 10rpx 0 10rpx 20rpx;
}

.sub-title {
  margin: 4rpx 0 18rpx;
  color: #7d899b;
  font-size: 25rpx;
  font-weight: 600;
}

.scene-title {
  margin-top: 30rpx;
}

.chip-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16rpx;
}

.mood-chip {
  min-height: 120rpx;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #4a5568;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
}

.mood-chip:active,
.scene-pill:active {
  transform: scale(0.96);
}

.chip-emoji {
  font-size: 38rpx;
  margin-bottom: 9rpx;
}

.chip-text {
  font-size: 24rpx;
  font-weight: 600;
}

.chip-tone-sun {
  background: linear-gradient(135deg, #fff0f5 0%, #ffe3ec 100%);
}

.chip-tone-calm {
  background: linear-gradient(135deg, #edf7ff 0%, #e2f0ff 100%);
}

.chip-tone-mist {
  background: linear-gradient(135deg, #f6f8fb 0%, #eef2f7 100%);
}

.chip-tone-rain {
  background: linear-gradient(135deg, #eef5ff 0%, #e4eefb 100%);
}

.chip-tone-night {
  background: linear-gradient(135deg, #eef2ff 0%, #e8edff 100%);
}

.chip-tone-storm {
  background: linear-gradient(135deg, #fff0f5 0%, #ffe5ec 100%);
}

.chip-selected {
  border-color: rgba(92, 156, 230, 0.72);
  box-shadow: 0 10rpx 24rpx rgba(92, 156, 230, 0.14);
}

.scene-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.scene-pill {
  display: flex;
  align-items: center;
  padding: 17rpx 24rpx;
  border-radius: 999rpx;
  color: #4a5568;
  font-size: 26rpx;
  font-weight: 600;
  background: #ffffff;
  border: 1rpx solid rgba(151, 166, 190, 0.16);
  box-shadow: 0 8rpx 18rpx rgba(92, 156, 230, 0.05);
  transition: all 0.2s ease;
}

.scene-pill-selected {
  color: #2563a8;
  border-color: rgba(92, 156, 230, 0.66);
  background: #edf6ff;
}

.scene-emoji {
  margin-right: 8rpx;
}

.privacy-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx 28rpx 30rpx 32rpx;
}

.privacy-copy {
  flex: 1;
  padding-right: 20rpx;
}

.bottom-spacer {
  height: 260rpx;
}

.submit-dock {
  position: fixed;
  left: 0;
  right: 0;
  bottom: calc(112rpx + env(safe-area-inset-bottom));
  z-index: 90;
  padding: 26rpx 32rpx 30rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(248, 251, 255, 0.92) 42%, #ffffff 100%);
  box-sizing: border-box;
}

.analyze-overlay {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(243, 248, 255, 0.82) 0%, rgba(248, 251, 255, 0.95) 100%);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
}

.analyze-overlay__glow {
  position: absolute;
  width: 520rpx;
  height: 520rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(92, 156, 230, 0.18) 0%, rgba(255, 181, 202, 0.14) 45%, rgba(255, 255, 255, 0) 76%);
  filter: blur(12rpx);
}

.analyze-overlay__content {
  position: relative;
  z-index: 1;
  width: 100%;
  padding: 0 72rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.analyze-orb {
  width: 220rpx;
  height: 220rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(110, 173, 243, 0.28) 0%, rgba(255, 188, 208, 0.24) 100%);
  box-shadow: 0 18rpx 60rpx rgba(92, 156, 230, 0.16);
  animation: analyze-orb-float 2.8s ease-in-out infinite;
}

.analyze-orb__inner {
  width: 126rpx;
  height: 126rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #76aff1 0%, #ffb8cb 100%);
  box-shadow: inset 0 10rpx 16rpx rgba(255, 255, 255, 0.32);
}

.analyze-overlay__kicker {
  margin-top: 34rpx;
  color: #5c9ce6;
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.analyze-overlay__title {
  margin-top: 18rpx;
  color: #223247;
  font-size: 42rpx;
  font-weight: 800;
  line-height: 1.3;
}

.analyze-overlay__desc {
  margin-top: 14rpx;
  color: #6f8096;
  font-size: 27rpx;
  line-height: 1.6;
}

.analyze-progress {
  width: 100%;
  max-width: 520rpx;
  margin-top: 34rpx;
}

.analyze-progress__track {
  width: 100%;
  height: 12rpx;
  border-radius: 999rpx;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: inset 0 1rpx 4rpx rgba(92, 156, 230, 0.08);
}

.analyze-progress__bar {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #76aff1 0%, #95c2f7 45%, #ffb8cb 100%);
  box-shadow: 0 6rpx 18rpx rgba(118, 175, 241, 0.22);
  transition: width 0.35s ease;
}

.analyze-progress__text {
  display: block;
  margin-top: 12rpx;
  text-align: right;
  color: #7d899b;
  font-size: 22rpx;
  font-weight: 600;
}

.analyze-steps {
  width: 100%;
  margin-top: 26rpx;
  display: flex;
  justify-content: center;
  gap: 18rpx;
}

.analyze-step {
  min-width: 132rpx;
  padding: 16rpx 18rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.62);
  border: 1rpx solid rgba(151, 166, 190, 0.12);
  transition: all 0.2s ease;
}

.analyze-step--active {
  background: rgba(92, 156, 230, 0.14);
  border-color: rgba(92, 156, 230, 0.28);
  box-shadow: 0 10rpx 24rpx rgba(92, 156, 230, 0.1);
}

.analyze-step--done {
  background: rgba(255, 255, 255, 0.86);
  border-color: rgba(92, 156, 230, 0.2);
}

.analyze-step__dot {
  width: 14rpx;
  height: 14rpx;
  margin-right: 10rpx;
  border-radius: 50%;
  background: #b4c6dc;
}

.analyze-step--active .analyze-step__dot,
.analyze-step--done .analyze-step__dot {
  background: #5c9ce6;
}

.analyze-step__label {
  color: #4c6078;
  font-size: 24rpx;
  font-weight: 600;
}

@keyframes analyze-orb-float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-8rpx) scale(1.03);
  }
}

.submit-btn {
  width: 100%;
  height: 98rpx;
  line-height: 98rpx;
  margin: 0;
  border-radius: 999rpx;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
  background: #5c9ce6;
  box-shadow: 0 16rpx 34rpx rgba(92, 156, 230, 0.22);
}

.submit-btn:after {
  display: none;
}

.submit-btn:active {
  transform: scale(0.98);
}

.submit-btn[disabled],
.btn-loading {
  opacity: 0.72;
}
</style>
