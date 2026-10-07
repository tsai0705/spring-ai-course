package com.example.springaicourse.lab2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lab 2：Tool Calling。
 * - /lab2/no-tool    沒掛工具，模型不知道現在時間，只能亂猜或拒答（失敗版）
 * - /lab2/with-tool  掛上 TimeTools，模型會呼叫 getCurrentTime() 後再回答（成功版）
 */
@RestController
@RequestMapping("/lab2")
public class ToolController {

    private final ChatClient chatClient;
    private final TimeTools timeTools;

    public ToolController(ChatClient.Builder builder, TimeTools timeTools) {
        this.chatClient = builder
                // 小模型預設常用簡體中文回答，用 system prompt 固定語言
                .defaultSystem("請用繁體中文回答")
                .build();
        this.timeTools = timeTools;
    }

    @GetMapping("/no-tool")
    public String noTool(@RequestParam String q) {
        return chatClient.prompt().user(q).call().content();
    }

    @GetMapping("/with-tool")
    public String withTool(@RequestParam String q) {
        return chatClient.prompt()
                .user(q)
                // .tools() 把物件裡所有 @Tool 方法註冊給這次請求；
                // 模型決定要呼叫時，Spring AI 會執行方法並把結果送回模型產生最終答案
                .tools(timeTools)
                .call()
                .content();
    }
}
