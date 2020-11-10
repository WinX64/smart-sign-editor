package io.github.winx64.sse.tool;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of tools that perform an action in common
 *
 * @author WinX64
 */
@Experimental
public interface ToolCategory {

    /**
     * Gets the name of this tool category
     *
     * @return the tool category's name
     */
    @NotNull
    String getName();

    /**
     * Gets the general necessary permission for tools in this category to be used
     *
     * @return the tool category's permission
     */
    @NotNull
    String getPermission();

    /**
     * Gets the tool bound to the specified tool usage
     *
     * @param usage the tool usage
     * @return the bound tool, or null if there's none
     * @throws NullPointerException if usage is null
     */
    @Nullable
    Tool getToolByUsage(@NotNull ToolUsage usage);
}
