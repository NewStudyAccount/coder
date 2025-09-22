package com.example.demo;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class LocalAiController {

    @Autowired
    private OpenAiChatModel chatModel;

    /**
     * 示例接口：调用本地大模型进行问答
     * @param question 用户问题
     * @return 模型回复
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        PromptTemplate promptTemplate = new PromptTemplate("请用中文简要回答：{question}");
        Prompt prompt = promptTemplate.create(question);
        return chatModel.call(prompt).getResult().getOutput();
    }
}