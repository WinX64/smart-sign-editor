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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.winx64.sse.handler.VersionHandler;
import io.github.winx64.sse.listener.PlayerInOutListener;
import io.github.winx64.sse.listener.SignChangeListener;
import io.github.winx64.sse.listener.SignInteractionListener;
import io.github.winx64.sse.player.SmartPlayer;

/**
 * SmartSignEditor's main class
 * 
 * @author Lucas
 *
 */
public class SmartSignEditor extends JavaPlugin {

    private Logger logger;
    private Map<UUID, SmartPlayer> smartPlayers;

    private VersionHandler versionHandler;

    private Set<String> specialSigns;

    @Override
    public void onEnable() {
	this.logger = getLogger();
	this.smartPlayers = new HashMap<UUID, SmartPlayer>();
	this.specialSigns = new HashSet<String>();

	this.versionHandler = VersionHandler.hookInternally();
	if (versionHandler == null) {
	    String currentVersion = getServer().getClass().getPackage().getName();
	    currentVersion = currentVersion.substring(currentVersion.lastIndexOf('.') + 1);
	    logger.severe("The current server implementation version is not supported!");
	    if (currentVersion.charAt(0) != 'v' || VersionHandler.isVersionUnsupported(currentVersion)) {
		logger.severe("Your current version is " + currentVersion
			+ ". This is an outdaded version that has no support for the plugin. Please, update you server!");
	    } else {
		logger.severe(
			"Your current version is " + currentVersion + ". Ask the author to provide support for it!");
	    }
	    getServer().getPluginManager().disablePlugin(this);
	    return;
	} else {
	    logger.info("Hooked internals with success! Using " + versionHandler.getClass().getSimpleName());
	}

	if (!loadConfiguration()) {
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

    /**
     * Saves and loads the plugin configuration
     * 
     * @return Whether something wrong happened or the load was successful
     */
    private boolean loadConfiguration() {
	try {
	    this.saveDefaultConfig();
	    FileConfiguration config = this.getConfig();

	    if (config.getInt("config-version") != 1) {
		logger.severe("Wrong configuration version!");
		return false;
	    }

	    List<String> specialSigns = config.getStringList("special-signs");
	    for (String sign : specialSigns) {
		this.specialSigns.add(sign.toLowerCase());
	    }

	    return true;
	} catch (Exception e) {
	    logger.severe("An error occurred while trying to load the configuration! Details below:");
	    e.printStackTrace();
	}
	return false;
    }

    /**
     * Registers a SmartPlayer for a newly-logged Player
     * 
     * @param sPlayer
     *            The new SmartPlayer
     */
    public void registerSmartPlayer(SmartPlayer sPlayer) {
	smartPlayers.put(sPlayer.getUniqueId(), sPlayer);
    }

    /**
     * Gets the SmartPlayer linked by this UUID
     * 
     * @param uniqueId
     *            The unique id
     * @return The SmartPlayer instance, or null if none was found
     */
    public SmartPlayer getSmartPlayer(UUID uniqueId) {
	return smartPlayers.get(uniqueId);
    }

    /**
     * Unregisters a SmartPlayer
     * 
     * @param uniqueId
     */
    public void unregisterSmartPlayer(UUID uniqueId) {
	smartPlayers.remove(uniqueId);
    }

    /**
     * Gets the current VersionHandler hooked to the internals
     * 
     * @return The handler
     */
    public VersionHandler getVersionHandler() {
	return versionHandler;
    }

    /**
     * Checks if the specified text is the header of a special sign, used by
     * other plugins. Helpful to avoid conflicts
     * 
     * @param text
     *            The sign's first line
     * @return Whether it is special or not
     */
    public boolean isSpecialSign(String text) {
	return specialSigns.contains(text.toLowerCase());
    }
}
