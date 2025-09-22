package com.example.demo;

/**
 * AI 聊天响应结果
 */
public class AiChatResponse {
    /**
     * AI 回复内容
     */
    private String answer;

    /**
     * 模型名称（可选）
     */
    private String model;

    /**
     * 响应耗时（毫秒，可选）
     */
    private Long durationMs;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}