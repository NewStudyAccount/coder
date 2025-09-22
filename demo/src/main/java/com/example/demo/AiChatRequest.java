package com.example.demo;

/**
 * AI 聊天请求参数
 */
public class AiChatRequest {
    /**
     * 用户问题
     */
    private String question;

    /**
     * 温度参数（可选，控制生成随机性）
     */
    private Double temperature;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}