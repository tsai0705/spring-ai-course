package com.example.springaicourse.lab3;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lab 3：Chat Memory 與 Advisor。
 * 模型本身沒有記憶，每次請求都是全新對話。
 * - /lab3/no-memory    每次獨立，問「我叫什麼名字」會答不出來（失敗版）
 * - /lab3/with-memory  用 MessageChatMemoryAdvisor 自動把歷史訊息帶進 prompt（成功版）
 * 兩邊都掛 SimpleLoggerAdvisor，搭配 application.yml 的 DEBUG 設定可在 console 看到實際送出的請求與回應。
 */
@RestController
@RequestMapping("/lab3")
public class MemoryController {

    private final ChatClient plainClient;
    private final ChatClient memoryClient;

    public MemoryController(ChatClient.Builder builder) {
        // 記憶體內的對話記憶，只保留最近 10 則訊息（視窗），避免 prompt 無限變長
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        // Advisor 是請求的攔截器：在呼叫模型前後插入額外行為。
        // SimpleLoggerAdvisor 只負責印 log，方便看清楚送給模型的內容
        this.plainClient = builder.clone()
                // 小模型預設常用簡體中文回答，用 system prompt 固定語言
                .defaultSystem("請用繁體中文回答")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        // MessageChatMemoryAdvisor 呼叫前讀出歷史訊息放進 prompt，呼叫後把新的問答存回記憶
        this.memoryClient = builder.clone()
                // 小模型預設常用簡體中文回答，用 system prompt 固定語言
                .defaultSystem("請用繁體中文回答")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/no-memory")
    public String noMemory(@RequestParam String q) {
        return plainClient.prompt().user(q).call().content();
    }

    @GetMapping("/with-memory")
    public String withMemory(@RequestParam String id, @RequestParam String q) {
        return memoryClient.prompt()
                .user(q)
                // conversationId 決定用哪一份對話記憶，不同 id 的使用者彼此看不到對方的內容
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .call()
                .content();
    }
}
