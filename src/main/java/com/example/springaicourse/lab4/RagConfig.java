package com.example.springaicourse.lab4;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Lab 4 的準備工作：啟動時把請假規則文件切塊、轉成向量、放進記憶體向量庫。
 * SimpleVectorStore 只存在記憶體，重啟就清空，課堂示範夠用；正式環境會換成 pgvector 等。
 */
@Configuration
public class RagConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel,
            @Value("classpath:docs/leave-policy.txt") Resource policy) {
        // EmbeddingModel 就是 application.yml 設定的 nomic-embed-text，負責把文字轉成向量
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // 讀檔成 Document
        List<Document> documents = new TextReader(policy).get();

        // 切成小塊：整份文件塞進 prompt 太長，切塊後只取最相關的幾塊
        List<Document> chunks = new TokenTextSplitter().apply(documents);

        // add 會對每個區塊呼叫 embedding 模型算出向量，再存進向量庫
        store.add(chunks);
        return store;
    }
}
