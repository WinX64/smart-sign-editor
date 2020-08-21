package io.github.winx64.sse.tool;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A registry that holds all tool categories on SmartSignEditor
 *
 * @author WinX64
 */
@Experimental
public interface ToolRegistry {

    /**
     * Gets the default tool category in this registry
     * @return the tool category
     */
    @NotNull
    ToolCategory getDefaultToolCategory();

    /**
     * Gets an immutable list of all the registered categories
     * @return the tool categories
     */
    @NotNull
    List<ToolCategory> getToolCategories();
}
