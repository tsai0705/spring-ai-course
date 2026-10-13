package com.example.springaicourse.lab4;

import java.util.Set;

import org.springframework.ai.mcp.McpToolFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 安全設定：filesystem Server 也有 write_file、move_file 等會改檔案的工具。
 * 這個過濾器只放行唯讀工具，其餘一律不讓模型看到。
 */
@Configuration
public class McpToolConfig {

    private static final Set<String> READ_ONLY_TOOLS = Set.of(
            "read_text_file",
            "read_multiple_files",
            "list_directory",
            "list_directory_with_sizes",
            "directory_tree",
            "search_files",
            "get_file_info",
            "list_allowed_directories");

    @Bean
    public McpToolFilter readOnlyFilter() {
        return (connection, tool) -> READ_ONLY_TOOLS.contains(tool.name());
    }
}
