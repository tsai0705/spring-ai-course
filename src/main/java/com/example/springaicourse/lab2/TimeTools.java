package com.example.springaicourse.lab2;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Lab 2 的工具類別：把 Java 方法暴露給模型呼叫。
 * 模型自己不知道現在幾點，需要時會請 Spring AI 幫它執行這個方法。
 */
@Component
public class TimeTools {

    // @Tool 的 description 會送給模型，模型靠它判斷「什麼時候該呼叫這個工具」，要寫清楚
    @Tool(description = "取得目前的日期與時間（台北時區 Asia/Taipei）")
    public String getCurrentTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Taipei"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
    }
}
