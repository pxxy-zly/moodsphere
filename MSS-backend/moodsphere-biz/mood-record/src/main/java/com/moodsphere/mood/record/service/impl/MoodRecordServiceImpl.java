package com.moodsphere.mood.record.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.mood.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.mood.record.domain.entity.BizMoodRecord;
import com.moodsphere.mood.record.mapper.BizMoodRecordMapper;
import com.moodsphere.mood.record.service.IMoodRecordService;

/**
 * 情绪记录服务实现
 *
 * @author ruoyi
 */
@Service
public class MoodRecordServiceImpl implements IMoodRecordService
{
    /** 草稿 */
    private static final int RECORD_STATUS_DRAFT = 0;

    /** 已提交 */
    private static final int RECORD_STATUS_SUBMITTED = 1;

    /** 待分析 */
    private static final int ANALYZE_STATUS_PENDING = 0;

    /** 默认强度 */
    private static final int DEFAULT_INTENSITY = 5;

    /** 最小强度 */
    private static final int MIN_INTENSITY = 1;

    /** 最大强度 */
    private static final int MAX_INTENSITY = 10;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Override
    public Long createRecord(MoodRecordCreateBody body)
    {
        if (body == null || StringUtils.isEmpty(body.getContentText()))
        {
            throw new ServiceException("记录文本不能为空");
        }
        Long userId = SecurityUtils.getUserId();
        Date now = new Date();
        BizMoodRecord record = new BizMoodRecord();
        record.setUserId(userId);
        record.setSourceType(1);
        record.setRecordType(0);
        record.setContentText(body.getContentText().trim());
        record.setEmotionIntensity(normalizeIntensity(body.getEmotionIntensity()));
        record.setRecordTime(body.getRecordTime() == null ? now : body.getRecordTime());
        record.setRecordStatus(RECORD_STATUS_SUBMITTED);
        record.setAnalyzeStatus(ANALYZE_STATUS_PENDING);
        record.setIsPublic((body.getIsPublic() != null && body.getIsPublic() == 1) ? 1 : 0);
        record.setProvince(body.getProvince());
        record.setCity(body.getCity());
        record.setDistrict(body.getDistrict());
        record.setLocationName(body.getLocationName());
        record.setLongitude(body.getLongitude());
        record.setLatitude(body.getLatitude());
        record.setRiskLevel(0);
        record.setDelFlag(0);
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(now);
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(now);
        int rows = bizMoodRecordMapper.insertBizMoodRecord(record);
        if (rows <= 0 || record.getId() == null)
        {
            throw new ServiceException("创建记录失败");
        }
        return record.getId();
    }

    @Override
    public BizMoodRecord getRecord(Long recordId)
    {
        checkRecordId(recordId);
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, SecurityUtils.getUserId());
        if (record == null)
        {
            throw new ServiceException("记录不存在");
        }
        return record;
    }

    @Override
    public BizMoodRecord getLatestRecord()
    {
        return bizMoodRecordMapper.selectLatestByUserId(SecurityUtils.getUserId());
    }

    @Override
    public void submitRecord(Long recordId)
    {
        checkRecordId(recordId);
        int rows = bizMoodRecordMapper.updateSubmitStatus(recordId, SecurityUtils.getUserId(), RECORD_STATUS_SUBMITTED,
                ANALYZE_STATUS_PENDING, SecurityUtils.getUsername(), new Date());
        if (rows <= 0)
        {
            throw new ServiceException("记录不存在或无权限");
        }
    }

    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }

    private int normalizeIntensity(Integer intensity)
    {
        if (intensity == null)
        {
            return DEFAULT_INTENSITY;
        }
        return Math.max(MIN_INTENSITY, Math.min(MAX_INTENSITY, intensity));
    }
}
