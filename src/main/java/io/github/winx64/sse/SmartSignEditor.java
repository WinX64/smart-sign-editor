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
package io.github.winx64.sse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.winx64.sse.handler.VersionHandler;
import io.github.winx64.sse.listener.PlayerInOutListener;
import io.github.winx64.sse.listener.SignChangeListener;
import io.github.winx64.sse.listener.SignInteractionListener;
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
 * @author Lucas
 *
 */
public final class SmartSignEditor extends JavaPlugin {

    private Logger logger;
    private Map<UUID, SmartPlayer> smartPlayers;

    private SignConfiguration signConfig;
    private VersionHandler versionHandler;
    private Map<ToolType, Tool> tools;

    @Override
    public void onEnable() {
	this.logger = getLogger();

	this.versionHandler = VersionHandler.hookInternally();
	if (versionHandler == null) {
	    String currentVersion = getServer().getClass().getPackage().getName();
	    currentVersion = currentVersion.substring(currentVersion.lastIndexOf('.') + 1);
	    logger.severe("The current server implementation version is not supported!");
	    if (currentVersion.charAt(0) != 'v' || VersionHandler.isVersionUnsupported(currentVersion)) {
		logger.severe("Your current version is " + currentVersion
			+ ". This is an outdaded version that has no support for the plugin. Please, update you server!");
	    } else {
		logger.severe("Your current version is " + currentVersion
			+ ". This is a newer version that still not supported. Ask the author to provide support for it!");
	    }
	    getServer().getPluginManager().disablePlugin(this);
	    return;
	} else {
	    logger.info("Hooked internals with success! Using " + versionHandler.getClass().getSimpleName());
	}

	this.smartPlayers = new HashMap<UUID, SmartPlayer>();

	this.tools = new HashMap<ToolType, Tool>();
	this.tools.put(ToolType.EDIT, new EditTool(this));
	this.tools.put(ToolType.COPY, new CopyTool(this));
	this.tools.put(ToolType.PASTE, new PasteTool(this));
	this.tools.put(ToolType.ERASE, new EraseTool(this));
	this.tools.put(null, new ToolChange(this));

	this.signConfig = new SignConfiguration(this);
	if (!signConfig.loadConfiguration()) {
	    logger.severe("Failed to load the configuration. The plugin will be disabled to avoid further damage!");
	    getServer().getPluginManager().disablePlugin(this);
	    return;
	}

	for (Player player : versionHandler.getOnlinePlayers()) {
	    smartPlayers.put(player.getUniqueId(), new SmartPlayer(player));
	}

	PluginManager pm = getServer().getPluginManager();
	pm.registerEvents(new PlayerInOutListener(this), this);
	pm.registerEvents(new SignChangeListener(this), this);
	pm.registerEvents(new SignInteractionListener(this), this);
    }

    public SignConfiguration getSignConfig() {
	return signConfig;
    }

    public Tool getTool(ToolType type) {
	return tools.get(type);
    }

    public void log(Level level, String format, Object... objects) {
	logger.log(level, String.format(format, objects));
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

    public VersionHandler getVersionHandler() {
	return versionHandler;
    }

    public boolean checkBuildPermission(Player player, Sign sign) {
	BlockBreakEvent event = new BlockBreakEvent(sign.getBlock(), player);
	Bukkit.getPluginManager().callEvent(event);
	return !event.isCancelled();
    }
}
