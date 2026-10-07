package com.example.springaicourse.lab1;

/**
 * 結構化輸出的目標型別。
 * Spring AI 會依據這個 record 的欄位產生 JSON Schema 附在 prompt 裡，
 * 再把模型回傳的 JSON 轉成這個物件。
 */
public record Book(String title, String author) {
}
