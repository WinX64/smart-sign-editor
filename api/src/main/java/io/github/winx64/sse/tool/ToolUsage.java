package io.github.winx64.sse.tool;

import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the different ways to use a certain tool
 *
 * @author WinX64
 */
public enum ToolUsage {

    /**
     * Right-click action while crouching
     */
    SHIFT_RIGHT_CLICK,
    /**
     * Right-click action without crouching
     */
    NO_SHIFT_RIGHT_CLICK,
    /**
     * Left-click action while crouching
     */
    SHIFT_LEFT_CLICK,
    /**
     * Left-click action without crouching
     */
    NO_SHIFT_LEFT_CLICK,
    /**
     * General right-click action
     */
    RIGHT_CLICK,
    /**
     * General left-click action
     */
    LEFT_CLICK;

    private static final List<Action> VALID_ACTIONS = Collections.unmodifiableList(Arrays.asList(
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK));

    private static final Map<Boolean, Map<Boolean, ToolUsage>> USAGES;

    static {
        SHIFT_RIGHT_CLICK.parent = RIGHT_CLICK;
        NO_SHIFT_RIGHT_CLICK.parent = RIGHT_CLICK;
        SHIFT_LEFT_CLICK.parent = LEFT_CLICK;
        NO_SHIFT_LEFT_CLICK.parent = LEFT_CLICK;

        Map<Boolean, ToolUsage> rightClick = new HashMap<>();
        rightClick.put(true, SHIFT_RIGHT_CLICK);
        rightClick.put(false, NO_SHIFT_RIGHT_CLICK);

        Map<Boolean, ToolUsage> leftClick = new HashMap<>();
        leftClick.put(true, SHIFT_LEFT_CLICK);
        leftClick.put(false, NO_SHIFT_LEFT_CLICK);

        Map<Boolean, Map<Boolean, ToolUsage>> usages = new HashMap<>();
        usages.put(true, Collections.unmodifiableMap(rightClick));
        usages.put(false, Collections.unmodifiableMap(leftClick));
        USAGES = Collections.unmodifiableMap(usages);
    }

    private ToolUsage parent;

    /**
     * Gets the corresponding tool usage by the given parameters
     * @param action the player's action
     * @param isSneaking whether the player is crouching
     * @throws NullPointerException if action is null
     * @throws IllegalArgumentException if the given action is not right/left click block/air action
     * @return the tool usage
     */
    @NotNull
    public static ToolUsage getByAction(@NotNull Action action, boolean isSneaking) {
        if (!VALID_ACTIONS.contains(Objects.requireNonNull(action))) {
            throw new IllegalArgumentException("Invalid action");
        }
        boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        return USAGES.get(isRightClick).get(isSneaking);
    }

    /**
     * Gets the tool usage by the given name
     * @param name the name
     * @throws NullPointerException if name is null
     * @return the tool usage, or null if it doesn't exist
     */
    @Nullable
    public static ToolUsage getByName(@NotNull String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Verifies if this tool usage is a child of the given target
     * @param target the target
     * @throws NullPointerException if target is null
     * @return whether or not this usage is a child
     */
    public boolean isChildOf(@NotNull ToolUsage target) {
        return this == target || this.parent == target;
    }

    /**
     * Verifies if this tool usage is the parent of the given target
     * @param target the target
     * @throws NullPointerException if target is null
     * @return whether or not this usage is the parent
     */
    public boolean isParentOf(@NotNull ToolUsage target) {
        return this == target || target.parent == this;
    }
}
