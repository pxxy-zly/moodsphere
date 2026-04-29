import request from '@/utils/request'

// 记录模块
export function createMoodRecord(data) {
  return request({
    url: '/app/mood/record/create',
    method: 'post',
    data
  })
}

export function getMoodRecord(recordId) {
  return request({
    url: `/app/mood/record/${recordId}`,
    method: 'get'
  })
}

export function getLatestMoodRecord() {
  return request({
    url: '/app/mood/record/latest',
    method: 'get'
  })
}

export function submitMoodRecord(recordId) {
  return request({
    url: `/app/mood/record/${recordId}/submit`,
    method: 'post'
  })
}

// AI分析模块
export function runMoodAnalyze(recordId) {
  return request({
    url: '/app/mood/analyze/run',
    method: 'post',
    data: { recordId }
  })
}

export function getMoodAnalyzeResult(recordId) {
  return request({
    url: `/app/mood/analyze/result/${recordId}`,
    method: 'get'
  })
}

// 向量模块
export function buildMoodVector(recordId) {
  return request({
    url: '/app/mood/vector/build',
    method: 'post',
    data: { recordId }
  })
}

export function getMoodVector(recordId) {
  return request({
    url: `/app/mood/vector/${recordId}`,
    method: 'get'
  })
}

// 天气模块
export function generateMoodWeather(recordId) {
  return request({
    url: '/app/mood/weather/generate',
    method: 'post',
    data: { recordId }
  })
}

export function getMoodWeatherMapping(recordId) {
  return request({
    url: `/app/mood/weather/mapping/${recordId}`,
    method: 'get'
  })
}

export function getMoodWeatherToday() {
  return request({
    url: '/app/mood/weather/today',
    method: 'get'
  })
}

export function getMoodWeatherSnapshot(date) {
  return request({
    url: '/app/mood/weather/snapshot',
    method: 'get',
    params: { date }
  })
}

export function listMoodWeatherSnapshots(limit = 7) {
  return request({
    url: '/app/mood/weather/snapshot/history',
    method: 'get',
    params: { limit }
  })
}
