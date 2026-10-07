package com.example.springaicourse.lab1;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lab 1：ChatClient 基本用法與結構化輸出。
 * - /lab1/chat       問什麼答什麼，回傳純字串
 * - /lab1/book-text  請模型推薦書，但只拿到一段自由文字（失敗版：不好解析）
 * - /lab1/book       同樣的問題加上 .entity()，直接拿到 List<Book>（成功版）
 */
@RestController
@RequestMapping("/lab1")
public class ChatController {

    private final ChatClient chatClient;

    // Spring AI 自動設定好 ChatClient.Builder（已綁定 application.yml 的 Ollama 模型），
    // 我們 build 出一個 ChatClient 重複使用即可
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                // 小模型預設常用簡體中文回答，用 system prompt 固定語言
                .defaultSystem("請用繁體中文回答")
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String q) {
        return chatClient.prompt()   // 開始組一個請求
                .user(q)             // 使用者訊息
                .call()              // 同步呼叫模型（串流用 .stream()）
                .content();          // 只取回應文字
    }

    @GetMapping("/book-text")
    public String bookText(@RequestParam String topic) {
        // 沒有指定輸出格式：模型愛怎麼寫就怎麼寫，程式很難穩定解析
        return chatClient.prompt()
                .user("請推薦 3 本關於「" + topic + "」的書，包含書名與作者")
                .call()
                .content();
    }

    @GetMapping("/book")
    public List<Book> book(@RequestParam String topic) {
        return chatClient.prompt()
                .user("請推薦 3 本關於「" + topic + "」的書")
                .call()
                // .entity() 會在 prompt 加上格式指示，並把回應 JSON 轉成 Java 物件；
                // 泛型 List 要用 ParameterizedTypeReference 保留型別資訊
                .entity(new ParameterizedTypeReference<List<Book>>() {
                });
    }
}
