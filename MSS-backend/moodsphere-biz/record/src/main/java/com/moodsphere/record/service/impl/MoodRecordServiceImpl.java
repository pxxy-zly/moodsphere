package com.moodsphere.record.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;
import com.moodsphere.record.service.IMoodRecordService;

/**
 * 情绪记录服务实现类
 */
@Service
public class MoodRecordServiceImpl implements IMoodRecordService
{
    /** 记录状态：已提交 */
    private static final int RECORD_STATUS_SUBMITTED = 1;

    /** 分析状态：待分析 */
    private static final int ANALYZE_STATUS_PENDING = 0;

    /** 默认情绪强度 */
    private static final int DEFAULT_INTENSITY = 5;

    /** 最小情绪强度 */
    private static final int MIN_INTENSITY = 1;

    /** 最大情绪强度 */
    private static final int MAX_INTENSITY = 10;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    /**
     * 创建情绪记录
     * 
     * @param body 创建请求体
     * @return 记录ID
     */
    @Override
    public Long createRecord(MoodRecordCreateBody body)
    {
        if (body == null)
        {
            throw new ServiceException("请求参数不能为空");
        }
        String contentText = body.getContentText() == null ? null : body.getContentText().trim();
        if (StringUtils.isEmpty(contentText))
        {
            throw new ServiceException("记录文本不能为空");
        }

        Long userId = SecurityUtils.getUserId();
        String username = defaultUsername(SecurityUtils.getUsername());
        Date now = new Date();

        BizMoodRecord record = new BizMoodRecord();
        record.setUserId(userId);
        record.setSourceType(1);
        record.setRecordType(0);
        record.setContentText(contentText);
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
        record.setCreateBy(username);
        record.setCreateTime(now);
        record.setUpdateBy(username);
        record.setUpdateTime(now);

        int rows = bizMoodRecordMapper.insertBizMoodRecord(record);
        if (rows <= 0 || record.getId() == null)
        {
            throw new ServiceException("创建记录失败");
        }
        return record.getId();
    }

    /**
     * 获取记录详情
     * 
     * @param recordId 记录ID
     * @return 记录实体
     */
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

    /**
     * 获取最新记录
     * 
     * @return 最新记录实体
     */
    @Override
    public BizMoodRecord getLatestRecord()
    {
        return bizMoodRecordMapper.selectLatestByUserId(SecurityUtils.getUserId());
    }

    /**
     * 提交记录
     * 
     * @param recordId 记录ID
     */
    @Override
    public void submitRecord(Long recordId)
    {
        checkRecordId(recordId);
        int rows = bizMoodRecordMapper.updateSubmitStatus(recordId, SecurityUtils.getUserId(), RECORD_STATUS_SUBMITTED,
                ANALYZE_STATUS_PENDING, defaultUsername(SecurityUtils.getUsername()), new Date());
        if (rows <= 0)
        {
            throw new ServiceException("记录不存在或无权限");
        }
    }

    /**
     * 检查记录ID
     * 
     * @param recordId 记录ID
     */
    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }

    /**
     * 统一将情绪强度限制在 1~10 区间。
     * 
     * @param intensity 情绪强度
     * @return 标准化后的强度
     */
    private int normalizeIntensity(Integer intensity)
    {
        if (intensity == null)
        {
            return DEFAULT_INTENSITY;
        }
        return Math.max(MIN_INTENSITY, Math.min(MAX_INTENSITY, intensity));
    }

    /**
     * 获取默认用户名
     * 
     * @param username 用户名
     * @return 默认用户名
     */
    private String defaultUsername(String username)
    {
        return StringUtils.isEmpty(username) ? "system" : username;
    }
}