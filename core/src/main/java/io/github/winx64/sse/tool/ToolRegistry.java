package io.github.winx64.sse.tool;

public interface ToolRegistry {

    ToolCategory getDefaultToolCategory();

    ToolCategory switchToolCategory(ToolCategory reference, boolean forward);
}
