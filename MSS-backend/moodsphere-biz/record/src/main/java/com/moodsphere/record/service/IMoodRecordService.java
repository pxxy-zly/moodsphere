package com.moodsphere.record.service;

import com.moodsphere.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.record.domain.entity.BizMoodRecord;
public interface IMoodRecordService
{
    Long createRecord(MoodRecordCreateBody body);

    BizMoodRecord getRecord(Long recordId);

    BizMoodRecord getLatestRecord();

    void submitRecord(Long recordId);
}

