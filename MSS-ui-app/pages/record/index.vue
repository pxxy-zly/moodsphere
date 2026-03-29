<template>
  <view class="record-page">
    <view class="container">
      
      <!-- Text Area Card -->
      <view class="input-card">
        <textarea
          v-model="form.contentText"
          class="content-input"
          maxlength="500"
          placeholder="今天发生了什么？"
          :show-confirm-bar="false"
        />
        <view class="input-actions">
          <view class="action-icons">
             <view class="icon-circle" @click="mockAction('语音输入')">
                <text class="icon-text">🎤</text>
             </view>
             <view class="icon-circle" @click="mockAction('图片上传')">
                <text class="icon-text">🖼️</text>
             </view>
          </view>
          <view class="counter">{{ form.contentText.length }}/500</view>
        </view>
      </view>

      <!-- Quick Mood Tags -->
      <view class="section-title">快捷情绪</view>
      <view class="tags-grid">
        <view 
          v-for="(tag, index) in moodTags" 
          :key="tag.t" 
          class="mood-tag" 
          :class="'tag-c' + (index % 4)"
          @click="appendTag(tag.t)"
        >
          <text class="tag-emoji">{{ tag.e }}</text>
          <text class="tag-label">{{ tag.t }}</text>
        </view>
      </view>

      <!-- Emotion Intensity Slider -->
      <view class="intensity-section">
        <view class="section-title flex-between">
          <text>情绪强度</text>
          <text class="intensity-val">{{ form.emotionIntensity }}</text>
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
      </view>

      <!-- Scene Tags -->
      <view class="section-title">生活场景</view>
      <view class="scene-tags">
        <view 
          v-for="sc in sceneTags" 
          :key="sc" 
          class="scene-pill" 
          @click="appendTag(sc)"
        >
          {{ sc }}
        </view>
      </view>

      <!-- Bottom Settings & Submit -->
      <view class="bottom-area">
        <view class="setting-row">
          <view class="setting-info">
            <text class="setting-label">公开到情绪星球</text>
            <text class="setting-desc">让更多人感受到你的共鸣</text>
          </view>
          <switch :checked="form.isPublic === 1" color="#FFB6C1" @change="handlePublicChange" style="transform:scale(0.8)"/>
        </view>

        <button class="submit-btn" :class="{'btn-loading': submitting}" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '生成天气中...' : '提交感受' }}
        </button>
      </view>

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
      },
      moodTags: [
        { t: '开心', e: '😊' },
        { t: '焦虑', e: '😰' },
        { t: '疲惫', e: '😮‍💨' },
        { t: '平静', e: '😌' }
      ],
      sceneTags: ['💼 工作', '📚 学习', '🏠 家庭', '🏃 运动', '☕ 休闲']
    }
  },
  methods: {
    appendTag(txt) {
      const cleanTxt = txt.replace(/.*? \s*/g, '').trim(); 
      if(this.form.contentText.includes(cleanTxt)) return;
      this.form.contentText += (this.form.contentText ? '，' : '') + cleanTxt;
    },
    mockAction(type) {
      if(this.$modal && this.$modal.msgError) {
        this.$modal.msgError(`${type} 暂未开放`);
      } else {
        uni.showToast({ title: `${type} 暂未开放`, icon: 'none' });
      }
    },
    handleIntensityChange(event) {
      this.form.emotionIntensity = event.detail.value
    },
    handlePublicChange(event) {
      this.form.isPublic = event.detail.value ? 1 : 0
    },
    async handleSubmit() {
      const contentText = (this.form.contentText || '').trim()
      if (!contentText) {
        if(this.$modal && this.$modal.msgError) this.$modal.msgError('请先输入记录内容');
        else uni.showToast({ title: '请先输入记录内容', icon: 'none' });
        return
      }
      if (this.submitting) {
        return
      }
      this.submitting = true
      
      if(this.$modal && this.$modal.loading) this.$modal.loading('正在生成天气结果，请稍候...');
      else uni.showLoading({ title: '生成中...' });

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

        if(this.$modal && this.$modal.closeLoading) this.$modal.closeLoading();
        else uni.hideLoading();

        this.$tab.navigateTo(`/pages/record/result?recordId=${recordId}`)
      } catch (error) {
        if(this.$modal && this.$modal.closeLoading) this.$modal.closeLoading();
        else uni.hideLoading();

        if(this.$modal && this.$modal.msgError) this.$modal.msgError(this.parseError(error));
        else uni.showToast({ title: this.parseError(error), icon: 'none' });
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
    }
  }
}
</script>

<style scoped>
.record-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FBFDFF 0%, #F5F7FA 100%);
  padding: 40rpx 32rpx;
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  box-sizing: border-box;
}

/* Card */
.input-card {
  background: #FFFFFF;
  border-radius: 36rpx;
  padding: 40rpx;
  box-shadow: 0 16rpx 48rpx rgba(136, 152, 170, 0.05);
  margin-bottom: 50rpx;
}
.content-input {
  width: 100%;
  height: 220rpx;
  font-size: 32rpx;
  color: #2D3748;
  line-height: 1.6;
}
.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30rpx;
  border-top: 1px solid #F0F4F8;
  padding-top: 24rpx;
}
.action-icons { display: flex; gap: 24rpx; }
.icon-circle {
  width: 72rpx; height: 72rpx;
  border-radius: 50%;
  background: #F7FAFC;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.02);
  transition: all 0.2s;
}
.icon-circle:active {
  background: #E2E8F0;
  transform: scale(0.95);
}
.icon-text { font-size: 32rpx; }
.counter { font-size: 24rpx; color: #A0AEC0; font-family: sans-serif; }

/* Sections */
.section-title {
  font-size: 30rpx; font-weight: 600; color: #2D3748;
  margin-bottom: 24rpx; padding-left: 10rpx; letter-spacing: 2rpx;
}
.flex-between { display: flex; justify-content: space-between; align-items: baseline; }
.intensity-val { font-size: 40rpx; font-family: "DIN Condensed", sans-serif; color: #FF9A9E; }

/* Macaron tags */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  margin-bottom: 50rpx;
}
.mood-tag {
  height: 160rpx;
  border-radius: 32rpx;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  font-size: 28rpx; font-weight: 500;
  color: #4A5568;
  box-shadow: 0 12rpx 24rpx rgba(0,0,0,0.04), inset 0 6rpx 16rpx rgba(255,255,255,0.8);
  transition: transform 0.2s;
}
.mood-tag:active { transform: scale(0.95); box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.02); }
.tag-emoji { font-size: 48rpx; margin-bottom: 12rpx; }
.tag-label { letter-spacing: 2rpx; }

/* Macaron palettes */
.tag-c0 { background: linear-gradient(135deg, #FFF0F0 0%, #FFE4E1 100%); } /* Pinkish */
.tag-c1 { background: linear-gradient(135deg, #FDF7FF 0%, #E6E6FA 100%); } /* Lavender */
.tag-c2 { background: linear-gradient(135deg, #F0FFFF 0%, #E0FFFF 100%); } /* Light Cyan */
.tag-c3 { background: linear-gradient(135deg, #FAFFFC 0%, #F5FFFA 100%); } /* Mint */

/* Slider */
.intensity-section { margin-bottom: 50rpx; }
.slider-wrapper {
  position: relative;
  height: 60rpx;
  display: flex; align-items: center;
  padding: 0 10rpx;
}
.gradient-track {
  position: absolute;
  top: 50%; left: 24rpx; right: 24rpx;
  height: 12rpx; transform: translateY(-50%);
  border-radius: 6rpx;
  background: linear-gradient(90deg, #A8EDE9 0%, #FED6E3 50%, #FF9A9E 100%);
  z-index: 1;
}
.real-slider {
  width: 100%;
  margin: 0;
  z-index: 2;
}

/* Scene Pills */
.scene-tags { display: flex; flex-wrap: wrap; gap: 24rpx; margin-bottom: 60rpx; }
.scene-pill {
  padding: 20rpx 44rpx;
  background: #FFFFFF;
  border-radius: 50rpx;
  font-size: 28rpx; color: #4A5568;
  box-shadow: 0 6rpx 16rpx rgba(136,152,170,0.06);
  border: 1px solid #F0F4F8;
  letter-spacing: 2rpx;
  transition: all 0.2s;
}
.scene-pill:active { background: #F7FAFC; transform: scale(0.96); }

/* Bottom Area */
.setting-row {
  display: flex; justify-content: space-between; align-items: center;
  background: #fff; padding: 30rpx 40rpx; border-radius: 36rpx;
  box-shadow: 0 10rpx 30rpx rgba(136,152,170,0.05);
  margin-bottom: 50rpx;
}
.setting-info { display: flex; flex-direction: column; }
.setting-label { font-size: 30rpx; color: #2D3748; font-weight: 500; letter-spacing: 2rpx; }
.setting-desc { font-size: 24rpx; color: #A0AEC0; margin-top: 8rpx; }

.submit-btn {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  color: #fff;
  border-radius: 60rpx;
  height: 104rpx;
  line-height: 104rpx;
  font-size: 34rpx; font-weight: 500; letter-spacing: 4rpx;
  box-shadow: 0 16rpx 40rpx rgba(255, 154, 158, 0.35);
  margin-bottom: 60rpx; /* Give room at bottom */
}
.submit-btn:after { display: none; }
.submit-btn:active { transform: translateY(4rpx); box-shadow: 0 8rpx 20rpx rgba(255, 154, 158, 0.2); }
</style>
