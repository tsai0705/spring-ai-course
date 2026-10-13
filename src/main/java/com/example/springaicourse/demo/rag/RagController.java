package com.example.springaicourse.demo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 講師示範：RAG（檢索增強生成），學生看就好，不用實作。
 * - /demo/rag/no-rag    模型沒看過公司內部規則，只能憑空回答（失敗版）
 * - /demo/rag/with-rag  先從向量庫找出相關段落，附在 prompt 裡再問模型（成功版）
 */
// 講師示範用：啟動時加 --spring.profiles.active=rag，需要本機 Ollama 與 nomic-embed-text
@Profile("rag")
@RestController
@RequestMapping("/demo/rag")
public class RagController {

    private final ChatClient plainClient;
    private final ChatClient ragClient;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {
        // 用 system prompt 固定回答語言為繁體中文（兩個 client 都加，對照才公平）
        this.plainClient = builder.clone()
                .defaultSystem("請用繁體中文回答")
                .build();

        // QuestionAnswerAdvisor 呼叫前用問題去向量庫做相似度搜尋，
        // 把找到的段落塞進 prompt，模型就能根據文件內容回答
        this.ragClient = builder.clone()
                .defaultSystem("請用繁體中文回答")
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    @GetMapping("/no-rag")
    public String noRag(@RequestParam String q) {
        return plainClient.prompt().user(q).call().content();
    }

    @GetMapping("/with-rag")
    public String withRag(@RequestParam String q) {
        return ragClient.prompt().user(q).call().content();
    }
}
