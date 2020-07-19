package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractToolCategory implements ToolCategory {

    private final SignMessage message;

    private final SignMessage.Message categoryName;
    private final String categoryPermission;

    private final List<AbstractTool> tools = new ArrayList<>();

    public AbstractToolCategory(SignMessage message, SignMessage.Message categoryName, String categoryPermission) {
        this.message = message;

        this.categoryName = categoryName;
        this.categoryPermission = categoryPermission;
    }

    @Override
    public String getName() {
        return message.get(categoryName);
    }

    @Override
    public String getPermission() {
        return categoryPermission;
    }

    @Override
    public Tool getToolByUsage(ToolUsage usage) {
        return tools.stream().filter(tool -> usage.matchesWith(tool.getUsage())).findFirst().orElse(null);
    }

    protected final void registerTool(AbstractTool tool) {
        this.tools.add(tool);
    }
}
