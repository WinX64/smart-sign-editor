/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2016
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.winx64.sse.tool;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.block.Action;

/**
 * Represents the diffent ways to use a certain tool
 * 
 * @author Lucas
 *
 */
public enum ToolUsage {

	SHIFT_RIGHT_CLICK,
	NO_SHIFT_RIGHT_CLICK,
	SHIFT_LEFT_CLICK,
	NO_SHIFT_LEFT_CLICK,
	RIGHT_CLICK,
	LEFT_CLICK;

	private static final List<List<ToolUsage>> USAGES = new ArrayList<List<ToolUsage>>();

	static {
		SHIFT_RIGHT_CLICK.parent = RIGHT_CLICK;
		NO_SHIFT_RIGHT_CLICK.parent = RIGHT_CLICK;
		SHIFT_LEFT_CLICK.parent = LEFT_CLICK;
		NO_SHIFT_LEFT_CLICK.parent = LEFT_CLICK;

		List<ToolUsage> rightUsage = new ArrayList<ToolUsage>();
		rightUsage.add(NO_SHIFT_RIGHT_CLICK);
		rightUsage.add(SHIFT_RIGHT_CLICK);

		List<ToolUsage> leftUsage = new ArrayList<ToolUsage>();
		leftUsage.add(NO_SHIFT_LEFT_CLICK);
		leftUsage.add(SHIFT_LEFT_CLICK);

		USAGES.add(rightUsage);
		USAGES.add(leftUsage);
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
		return values()[(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK ? 0 : 2)
				+ (isSneaking ? 0 : 1)];
	}

	public static ToolUsage getToolUsage(String name) {
		try {
			return valueOf(name);
		} catch (Exception e) {
			return null;
		}
	}
}
