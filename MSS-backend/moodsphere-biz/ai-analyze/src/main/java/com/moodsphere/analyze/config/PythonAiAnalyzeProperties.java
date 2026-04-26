package com.moodsphere.analyze.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Python AI 分析服务配置
 */
@Component
@ConfigurationProperties(prefix = "moodsphere.ai.python")
public class PythonAiAnalyzeProperties
{
    /**
     * 是否启用 Python AI 分析服务
     */
    private boolean enabled = false;

    /**
     * Python 服务基础地址，例如 http://127.0.0.1:9001
     */
    private String baseUrl;

    /**
     * 分析接口路径
     */
    private String analyzePath = "/v1/mood/analyze";

    /**
     * 调用超时时间（毫秒）
     */
    private long timeoutMs = 8000L;

    /**
     * 鉴权 token（可选）
     */
    private String token;

    /**
     * Python 调用失败时是否回退本地 mock 规则
     */
    private boolean fallbackToMock = true;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getAnalyzePath()
    {
        return analyzePath;
    }

    public void setAnalyzePath(String analyzePath)
    {
        this.analyzePath = analyzePath;
    }

    public long getTimeoutMs()
    {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs)
    {
        this.timeoutMs = timeoutMs;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public boolean isFallbackToMock()
    {
        return fallbackToMock;
    }

    public void setFallbackToMock(boolean fallbackToMock)
    {
        this.fallbackToMock = fallbackToMock;
    }
}
