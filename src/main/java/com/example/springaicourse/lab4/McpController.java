package com.example.springaicourse.lab4;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lab 4：MCP（Model Context Protocol）。
 * - /lab4/no-mcp    沒掛任何工具，模型看不到 mcp-files 資料夾（失敗版）
 * - /lab4/with-mcp  把 MCP Server 提供的工具掛上去，模型自己去讀檔案（成功版）
 */
@RestController
@RequestMapping("/lab4")
public class McpController {

    private final ChatClient plainClient;
    private final ChatClient mcpClient;
    private final ToolCallbackProvider mcpTools;

    // ToolCallbackProvider 由 Spring AI 依 application.yml 的 MCP 設定自動建立，
    // 裡面就是 filesystem Server 提供的工具
    public McpController(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        // 用 system prompt 固定回答語言為繁體中文（兩個 client 都加，對照才公平）
        this.plainClient = builder.clone()
                .defaultSystem("請用繁體中文回答")
                .build();

        this.mcpClient = builder.clone()
                .defaultSystem("請用繁體中文回答")
                .build();
        this.mcpTools = mcpTools;
    }

    @GetMapping("/no-mcp")
    public String noMcp(@RequestParam String q) {
        return plainClient.prompt().user(q).call().content();
    }

    @GetMapping("/with-mcp")
    public String withMcp(@RequestParam String q) {
        // 每次請求才把 MCP 工具掛上去，模型需要時會自己決定呼叫哪個工具。
        // 這樣第一次連 Server 發生在請求當下，不會拖慢或擋住應用程式啟動
        return mcpClient.prompt().user(q).toolCallbacks(mcpTools).call().content();
    }
}
