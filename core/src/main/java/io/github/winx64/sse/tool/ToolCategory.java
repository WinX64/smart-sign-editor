package io.github.winx64.sse.tool;

public interface ToolCategory {

    String getName();

    String getPermission();

    Tool getToolByUsage(ToolUsage usage);
}
