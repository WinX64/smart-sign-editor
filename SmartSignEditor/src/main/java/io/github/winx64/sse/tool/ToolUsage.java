package io.github.winx64.sse.tool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.block.Action;

/**
 * Represents the diffent ways to use a certain tool
 * 
 * @author WinX64
 *
 */
public enum ToolUsage {

	SHIFT_RIGHT_CLICK, NO_SHIFT_RIGHT_CLICK, SHIFT_LEFT_CLICK, NO_SHIFT_LEFT_CLICK, RIGHT_CLICK, LEFT_CLICK;

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

	public ToolUsage getParent() {
		return parent;
	}

	public boolean matchesWith(ToolUsage target) {
		return this == target || this.parent == target;
	}

	public boolean conflictsWith(ToolUsage toolUsage) {
		return this == toolUsage || this.parent == toolUsage || toolUsage.parent == this;
	}

	public static boolean conflicts(ToolUsage toolUsageOne, ToolUsage toolUsageTwo) {
		return toolUsageOne.conflictsWith(toolUsageTwo);
	}

	public static ToolUsage getToolUsage(Action action, boolean isSneaking) {
		boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
		return USAGES.get(isRightClick).get(isSneaking);
	}

	public static ToolUsage getToolUsage(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}
}
