package com.moodsphere.record.mapper;

import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.moodsphere.record.domain.entity.BizMoodRecord;


public interface BizMoodRecordMapper
{
    
    int insertBizMoodRecord(BizMoodRecord bizMoodRecord);

    
    BizMoodRecord selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    
    BizMoodRecord selectById(@Param("id") Long id);

    
    BizMoodRecord selectLatestByUserId(@Param("userId") Long userId);

    
    int updateSubmitStatus(@Param("id") Long id, @Param("userId") Long userId, @Param("recordStatus") Integer recordStatus,
            @Param("analyzeStatus") Integer analyzeStatus, @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);

    
    int updateAnalyzeResult(@Param("id") Long id, @Param("analyzeStatus") Integer analyzeStatus,
            @Param("riskLevel") Integer riskLevel, @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);
}



