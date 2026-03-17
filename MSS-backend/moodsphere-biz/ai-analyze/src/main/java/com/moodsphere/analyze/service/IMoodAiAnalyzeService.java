package com.moodsphere.analyze.service;

import com.moodsphere.analyze.domain.vo.MoodAnalyzeResultVo;

public interface IMoodAiAnalyzeService
{
    MoodAnalyzeResultVo runAnalyze(Long recordId);

    MoodAnalyzeResultVo getAnalyzeResult(Long recordId);
}


