package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class LocalAiController {

    @Autowired
    private LocalAiService localAiService;

    /**
     * GET 示例接口：调用本地大模型进行问答（兼容原有用法）
     * @param question 用户问题
     * @return 模型回复
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        AiChatRequest req = new AiChatRequest();
        req.setQuestion(question);
        AiChatResponse resp = localAiService.chat(req);
        return resp.getAnswer();
    }

    /**
     * 推荐用法：POST，支持更多参数，返回结构体
     */
    @PostMapping("/chat")
    public AiChatResponse chatPost(@RequestBody AiChatRequest req) {
        return localAiService.chat(req);
    }
}
