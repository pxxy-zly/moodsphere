package com.moodsphere.asset.domain.vo;

public class MoodAssetBindSummaryVo
{
    private boolean hasImage;

    private boolean hasVoice;

    private int maxVoiceDuration;

    public boolean isHasImage()
    {
        return hasImage;
    }

    public void setHasImage(boolean hasImage)
    {
        this.hasImage = hasImage;
    }

    public boolean isHasVoice()
    {
        return hasVoice;
    }

    public void setHasVoice(boolean hasVoice)
    {
        this.hasVoice = hasVoice;
    }

    public int getMaxVoiceDuration()
    {
        return maxVoiceDuration;
    }

    public void setMaxVoiceDuration(int maxVoiceDuration)
    {
        this.maxVoiceDuration = maxVoiceDuration;
    }
}
