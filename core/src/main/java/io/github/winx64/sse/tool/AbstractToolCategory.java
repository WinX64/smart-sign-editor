package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String getName() {
        return message.get(categoryName);
    }

    @Override
    public @NotNull String getPermission() {
        return categoryPermission;
    }

    @Override
    public Tool getToolByUsage(@NotNull ToolUsage usage) {
        return tools.stream().filter(tool -> usage.isChildOf(tool.getUsage())).findFirst().orElse(null);
    }

    protected final void registerTool(AbstractTool tool) {
        this.tools.add(tool);
    }
}
