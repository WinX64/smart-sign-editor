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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;
import io.github.winx64.sse.tool.ToolUsage;

public final class SignConfiguration {

    private static final int CONFIG_VERSION = 2;

    private final SmartSignEditor plugin;
    private final File configFile;

    private Material toolMaterial;

    private boolean usingExtendedTool;
    private int extendedToolReach;

    private Set<String> specialSigns;

    public SignConfiguration(SmartSignEditor plugin) {
	this.plugin = plugin;
	this.configFile = new File(plugin.getDataFolder(), "config.yml");

	this.toolMaterial = Material.FEATHER;

	this.usingExtendedTool = true;
	this.extendedToolReach = 10;

	this.specialSigns = new HashSet<String>();
    }

    public Material getToolMaterial() {
	return toolMaterial;
    }

    public boolean isUsingExtendedTool() {
	return usingExtendedTool;
    }

    public int getExtendedToolReach() {
	return extendedToolReach;
    }

    public boolean isSpecialSign(String text) {
	return specialSigns.contains(text.toLowerCase());
    }

    public boolean loadConfiguration() {
	try {
	    plugin.saveDefaultConfig();
	    if (!ensureCorrectVersion(true)) {
		plugin.log(Level.SEVERE, "[Config] Could not load the correct version configuration!", CONFIG_VERSION);
		return false;
	    }
	    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

	    Material toolMaterial = Material.getMaterial(config.getString("tool-item", null));
	    if (toolMaterial == null) {
		plugin.log(Level.WARNING,
			"[Config] The specified material for the tools is not valid. Using default FEATHER");
	    } else {
		this.toolMaterial = toolMaterial;
	    }

	    loadToolUsages(config.getConfigurationSection("tool-usages"));

	    loadExtendedTool(config.getConfigurationSection("extended-tool"));

	    List<String> specialSigns = config.getStringList("special-signs");
	    if (specialSigns.isEmpty()) {
		plugin.log(Level.WARNING, "[Config] No special signs detected!");
	    } else {
		for (String sign : specialSigns) {
		    this.specialSigns.add(sign.toLowerCase());
		}
	    }

	    plugin.log(Level.INFO, "[Config] Configuration loaded successfully!");
	    return true;
	} catch (Exception e) {
	    plugin.log(Level.SEVERE, "An error occurred while trying to load the configuration! Details below:");
	    e.printStackTrace();
	    return false;
	}
    }

    private boolean ensureCorrectVersion(boolean saveAndRetry) {
	int currentVersion = plugin.getConfig().getInt("config-version", -1);
	if (currentVersion == -1 && saveAndRetry) {
	    plugin.log(Level.WARNING, "[Config] The configuration version is missing. Did you erase it by accident?");
	    plugin.log(Level.INFO, "[Config] Creating an up to date one...");
	    plugin.saveResource("config.yml", true);
	    plugin.reloadConfig();
	    return ensureCorrectVersion(false);
	}

	if (currentVersion != CONFIG_VERSION) {
	    if (saveAndRetry) {
		plugin.log(Level.WARNING, "[Config] Outdated configuration detected. Preparing to create a new one...");
		if (!moveOldConfiguration()) {
		    plugin.log(Level.WARNING, "[Config] Failed to move old configuration. Overwritting it...");
		}
		plugin.saveResource("config.yml", true);
		plugin.reloadConfig();
		return ensureCorrectVersion(false);
	    } else {
		return false;
	    }
	}

	return true;
    }

    private boolean moveOldConfiguration() {
	try {
	    String newFileName = "config-old-" + System.currentTimeMillis() + ".yml";
	    File newFile = new File(plugin.getDataFolder(), newFileName);
	    plugin.log(Level.INFO, "[Config] The old configuration is now \"%s\"", newFileName);
	    configFile.renameTo(newFile);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    private void loadExtendedTool(ConfigurationSection config) {
	if (config == null) {
	    plugin.log(Level.WARNING, "[Config] Extended tool section does not exist!");
	    return;
	}

	this.usingExtendedTool = config.getBoolean("enabled", true);
	this.extendedToolReach = config.getInt("reach", 10);
	if (extendedToolReach < 1) {
	    plugin.log(Level.WARNING, "[Config] The extended tool reach must be of at least 1. Defaulting it to 10");
	    this.extendedToolReach = 10;
	}
    }

    private void loadToolUsages(ConfigurationSection config) {
	if (config == null || config.getKeys(false).isEmpty()) {
	    plugin.log(Level.WARNING, "[Config] Tool usage section does not exist.");
	    return;
	}

	for (ToolConfiguration toolConfig : ToolConfiguration.values()) {
	    ConfigurationSection toolSection = config.getConfigurationSection(toolConfig.section);
	    if (toolSection == null || toolSection.getKeys(false).isEmpty()) {
		plugin.log(Level.WARNING, "[Config] Tool usage section for the %s tool doesn't exist!",
			toolConfig.type.getName());
		continue;
	    }
	    loadToolUsage(plugin.getTool(toolConfig.type), toolSection, toolConfig.primaryUsage,
		    toolConfig.secondaryUsage);
	}
    }

    private void loadToolUsage(Tool tool, ConfigurationSection config, String primaryKey, String secondaryKey) {
	try {
	    String primaryUsageValue = config.getString(primaryKey);
	    String secondaryUsageValue = secondaryKey == null ? null : config.getString(secondaryKey);

	    if (primaryUsageValue == null) {
		plugin.log(Level.WARNING, "[Config] Key \"%s.%s\" not found. Using default value %s",
			config.getCurrentPath(), primaryKey, ToolUsage.NO_SHIFT_RIGHT_CLICK);
		primaryUsageValue = ToolUsage.NO_SHIFT_RIGHT_CLICK.name();
	    }
	    if (secondaryUsageValue == null && secondaryKey != null) {
		plugin.log(Level.WARNING, "[Config] Key \"%s.%s\" not found. Using default value %s",
			config.getCurrentPath(), secondaryKey, ToolUsage.SHIFT_RIGHT_CLICK);
		secondaryUsageValue = ToolUsage.SHIFT_RIGHT_CLICK.name();
	    }

	    ToolUsage primaryUsage = ToolUsage.getToolUsage(primaryUsageValue);
	    ToolUsage secondaryUsage = ToolUsage.getToolUsage(secondaryUsageValue);

	    if (primaryUsage == null) {
		plugin.log(Level.WARNING, "[Config] Invalid tool usage \"%s\". Using default value %s",
			primaryUsageValue, ToolUsage.NO_SHIFT_RIGHT_CLICK);
		primaryUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
	    }
	    if (secondaryUsage == null && secondaryKey != null) {
		plugin.log(Level.WARNING, "[Config] Invalid tool usage \"%s\". Using default value %s",
			secondaryUsageValue, ToolUsage.SHIFT_RIGHT_CLICK);
		secondaryUsage = ToolUsage.SHIFT_RIGHT_CLICK;
	    }

	    if (secondaryKey != null && primaryUsage.conflictsWith(secondaryUsage)) {
		plugin.log(Level.WARNING,
			"[Config] Tool usages %s and %s conflicts with eachother. Using default values %s and %s",
			primaryUsage, secondaryUsage, ToolUsage.NO_SHIFT_RIGHT_CLICK, ToolUsage.SHIFT_RIGHT_CLICK);
		primaryUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
		secondaryUsage = ToolUsage.SHIFT_RIGHT_CLICK;
	    }

	    tool.setPrimaryUsage(primaryUsage);
	    tool.setSecondaryUsage(secondaryKey == null ? primaryUsage : secondaryUsage);
	} catch (Exception e) {
	    plugin.log(Level.WARNING,
		    "An error occurred while trying to load the tool usages for the %s Tool! Details below:",
		    tool.getType().getName());
	    e.printStackTrace();
	}
    }

    private static enum ToolConfiguration {

	EDIT(ToolType.EDIT, "edit-tool", "sign-edit-usage", null),
	COPY(ToolType.COPY, "copy-tool", "sign-copy-usage", "line-copy-usage"),
	PASTE(ToolType.PASTE, "paste-tool", "sign-paste-usage", "line-paste-usage"),
	ERASE(ToolType.ERASE, "erase-tool", "sign-erase-usage", "line-erase-usage"),
	CHANGE(null, "tool-change", "next-tool-usage", "previous-tool-usage");

	private final ToolType type;
	private final String section;
	private final String primaryUsage;
	private final String secondaryUsage;

	private ToolConfiguration(ToolType type, String section, String primaryUsage, String secondaryUsage) {
	    this.type = type;
	    this.section = section;
	    this.primaryUsage = primaryUsage;
	    this.secondaryUsage = secondaryUsage;
	}
    }
}
