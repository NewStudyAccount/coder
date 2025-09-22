package com.example.demo;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalAiService {

    @Autowired
    private OpenAiChatModel chatModel;

    /**
     * 调用本地大模型进行问答
     * @param req 请求参数
     * @return 响应结果
     */
    public AiChatResponse chat(AiChatRequest req) {
        long start = System.currentTimeMillis();

        // 构建 Prompt
        PromptTemplate promptTemplate = new PromptTemplate("请用中文简要回答：{question}");
        Prompt prompt = promptTemplate.create(req.getQuestion());

        // 构建模型参数
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withTemperature(req.getTemperature() != null ? req.getTemperature() : 0.7)
                .build();

        // 调用模型
        String answer;
        try {
            answer = chatModel.call(prompt, options).getResult().getOutput();
        } catch (Exception e) {
            answer = "AI服务异常：" + e.getMessage();
        }

        long duration = System.currentTimeMillis() - start;

        AiChatResponse resp = new AiChatResponse();
        resp.setAnswer(answer);
        resp.setModel("local-llm");
        resp.setDurationMs(duration);
        return resp;
    }
}