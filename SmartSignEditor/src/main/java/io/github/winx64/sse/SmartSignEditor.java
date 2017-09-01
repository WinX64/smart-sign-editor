package io.github.winx64.sse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bstats.bukkit.Metrics.DrilldownPie;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.winx64.sse.commands.CommandReload;
import io.github.winx64.sse.commands.CommandTool;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessages;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.handler.VersionHandler;
import io.github.winx64.sse.listeners.PlayerInOutListener;
import io.github.winx64.sse.listeners.SignChangeListener;
import io.github.winx64.sse.listeners.SignInteractionListener;
import io.github.winx64.sse.metrics.MostUsedToolCallable;
import io.github.winx64.sse.metrics.ToolUseCountCallable;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;
import io.github.winx64.sse.tool.tools.CopyTool;
import io.github.winx64.sse.tool.tools.EditTool;
import io.github.winx64.sse.tool.tools.EraseTool;
import io.github.winx64.sse.tool.tools.PasteTool;
import io.github.winx64.sse.tool.tools.ToolChange;

/**
 * SmartSignEditor's main class
 * 
 * @author WinX64
 *
 */
public final class SmartSignEditor extends JavaPlugin {

	private static final String MOST_USED_TOOL_CHART_NAME = "mostUsedTool";
	private static final String TOOL_USE_COUNT_CHART_NAME = "toolUseCount";

	private final Map<UUID, SmartPlayer> smartPlayers;

	private final Logger logger;
	private final SignConfiguration signConfig;
	private final SignMessages signMessages;
	private final Map<ToolType, Tool> tools;

	private VersionAdapter versionAdapter;

	public SmartSignEditor() {
		this.logger = getLogger();
		this.smartPlayers = new HashMap<>();
		this.signConfig = new SignConfiguration(this);
		this.signMessages = new SignMessages(this);
		this.tools = new HashMap<>();
	}

	@Override
	public void onEnable() {
		String currentVersion = VersionHandler.getVersion();
		this.versionAdapter = VersionHandler.getAdapter(currentVersion);
		if (versionAdapter == null) {
			logger.severe("The current server version is not supported!");
			if (currentVersion == null) {
				logger.severe("Your current version is unknown. It may be due to a severely outdated server.");
			} else if (VersionHandler.isVersionUnsupported(currentVersion)) {
				logger.severe("Your current version is " + currentVersion
						+ ". Get off your dinosaur and update your server!");
			} else {
				logger.severe("Your current version is " + currentVersion
						+ ". This is a newer version that still not supported. Ask the author to provide support for it!");
			}
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			logger.info("Registered version adapter with success! Using " + versionAdapter.getClass().getSimpleName());
		}

		this.tools.put(ToolType.EDIT, new EditTool(this));
		this.tools.put(ToolType.COPY, new CopyTool(this));
		this.tools.put(ToolType.PASTE, new PasteTool(this));
		this.tools.put(ToolType.ERASE, new EraseTool(this));
		this.tools.put(null, new ToolChange(this));

		if (!signConfig.loadConfiguration()) {
			logger.severe("Failed to load the configuration. The plugin will be disabled to avoid further damage!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!signMessages.loadMessages()) {
			logger.severe("Failed to load the messages. The plugin is unable to function correctly without them!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		for (Player player : versionAdapter.getOnlinePlayers()) {
			smartPlayers.put(player.getUniqueId(), new SmartPlayer(player));
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerInOutListener(this), this);
		pm.registerEvents(new SignChangeListener(this), this);
		pm.registerEvents(new SignInteractionListener(this), this);

		this.getCommand("sse").setExecutor(new CommandTool(this));
		this.getCommand("sse-reload").setExecutor(new CommandReload(this));

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new DrilldownPie(MOST_USED_TOOL_CHART_NAME, new MostUsedToolCallable(this)));
		metrics.addCustomChart(new DrilldownPie(TOOL_USE_COUNT_CHART_NAME, new ToolUseCountCallable(this)));
	}

	public SignConfiguration getSignConfig() {
		return signConfig;
	}

	public SignMessages getSignMessages() {
		return signMessages;
	}

	public Tool getTool(ToolType type) {
		return tools.get(type);
	}

	public Tool getMostUsedTool() {
		Tool mostUsed = null;
		for (Tool tool : tools.values()) {
			if (tool.getType() == null) {
				continue;
			}
			if (mostUsed == null || (tool.getTotalUseCount()) > mostUsed.getTotalUseCount()) {
				mostUsed = tool;
			}
		}
		return mostUsed;
	}

	public void log(Level level, String format, Object... objects) {
		log(level, null, format, objects);
	}

	public void log(Level level, Exception e, String format, Object... objects) {
		logger.log(level, String.format(format, objects), e);
	}

	public void registerSmartPlayer(SmartPlayer sPlayer) {
		smartPlayers.put(sPlayer.getUniqueId(), sPlayer);
	}

	public SmartPlayer getSmartPlayer(UUID uniqueId) {
		return smartPlayers.get(uniqueId);
	}

	public void unregisterSmartPlayer(UUID uniqueId) {
		smartPlayers.remove(uniqueId);
	}

	public VersionAdapter getVersionAdapter() {
		return versionAdapter;
	}
}
