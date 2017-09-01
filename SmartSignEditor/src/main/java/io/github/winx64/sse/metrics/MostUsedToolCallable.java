/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2017
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
package io.github.winx64.sse.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.tool.Tool;

public class MostUsedToolCallable implements Callable<Map<String, Map<String, Integer>>> {

	private final SmartSignEditor plugin;

	public MostUsedToolCallable(SmartSignEditor plugin) {
		this.plugin = plugin;
	}

	@Override
	public Map<String, Map<String, Integer>> call() throws Exception {
		Map<String, Map<String, Integer>> map = new HashMap<>();
		Map<String, Integer> subMap = new HashMap<>();

		Tool tool = plugin.getMostUsedTool();
		if (tool.getTotalUseCount() > 0) {
			map.put(tool.getType().getName(), subMap);

			if (tool.getPrimaryUseCount() >= tool.getSecondaryUseCount()) {
				subMap.put(tool.getPrimaryName(), 1);
			} else {
				subMap.put(tool.getSecondaryName(), 1);
			}
		}

		return map;
	}

}
