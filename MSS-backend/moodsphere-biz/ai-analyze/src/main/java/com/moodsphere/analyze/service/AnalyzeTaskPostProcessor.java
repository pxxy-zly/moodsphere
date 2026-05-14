package com.moodsphere.analyze.service;

/**
 * AI 分析成功后的后处理器
 */
public interface AnalyzeTaskPostProcessor
{
    /**
     * 后处理名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 处理分析结果
     *
     * @param recordId 记录ID
     * @param userId 用户ID
     * @param operator 操作人
     */
    void process(Long recordId, Long userId, String operator);
}
