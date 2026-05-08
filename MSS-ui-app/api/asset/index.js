import upload from '@/utils/upload'

export function uploadMoodImage(filePath) {
  return upload({
    url: '/app/mood/asset/upload/image',
    filePath
  }).then(res => res.data)
}

export function uploadMoodVoice(filePath, duration = 0) {
  return upload({
    url: '/app/mood/asset/upload/voice',
    filePath,
    formData: {
      duration
    }
  }).then(res => res.data)
}
