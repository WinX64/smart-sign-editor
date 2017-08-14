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
