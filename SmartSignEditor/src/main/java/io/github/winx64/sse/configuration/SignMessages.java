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
package io.github.winx64.sse.configuration;

import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.winx64.sse.SmartSignEditor;

public final class SignMessages {

	private static final String MESSAGES_FILE_NAME = "messages.yml";
	private static final String OLD_MESSAGES_FILE_NAME = "messages-old-%d.yml";
	private static final String MESSAGES_VERSION_KEY = "messages-version";
	private static final int MESSAGES_VERSION = 2;

	private final SmartSignEditor plugin;
	private final File messagesFile;
	private FileConfiguration messages;
	private FileConfiguration defaultMessages;

	private Map<Message, String> loadedMessages;

	public SignMessages(SmartSignEditor plugin) {
		this.plugin = plugin;
		this.messagesFile = new File(plugin.getDataFolder(), MESSAGES_FILE_NAME);

		this.loadedMessages = new EnumMap<>(Message.class);
	}

	public String get(Message messageType) {
		return loadedMessages.get(messageType);
	}

	public String get(Message messageType, String... arguments) {
		String message = loadedMessages.get(messageType);
		for (int i = 0; i < Math.min(messageType.parameters.length, arguments.length); i++) {
			message = message.replace(messageType.parameters[i], arguments[i]);
		}
		return message;
	}

	public boolean loadMessages() {
		try {
			if (!messagesFile.exists()) {
				plugin.log(Level.INFO, "[Messages] Messages file not found. Creating a new one...");
				plugin.saveResource(MESSAGES_FILE_NAME, true);
			}
			this.messages = YamlConfiguration.loadConfiguration(messagesFile);
			if (messages.getKeys(false).size() == 0) {
				plugin.log(Level.SEVERE, "[Messages] Empty configuration! Did any error happen while parsing it?");
				return false;
			}

			if (!ensureCorrectVersion(true)) {
				plugin.log(Level.SEVERE, "[Messages] Could not load the correct version of the messages!",
						MESSAGES_VERSION);
				return false;
			}

			if (!loadDefaultMessages()) {
				plugin.log(Level.SEVERE, "[Messages] The default %s is missing from the plugin's jar!",
						MESSAGES_FILE_NAME);
				return false;
			}

			for (Message messageType : Message.values()) {
				String path = messageType.getPath();
				if (!this.messages.contains(path)) {
					plugin.log(Level.WARNING, "Missing message \"%s\". Using default value!", path);

					if (!this.defaultMessages.contains(path)) {
						plugin.log(Level.SEVERE,
								"Missing message \"%s\" from the default messages. Unable to continue!", path);
						return false;
					}
					this.loadedMessages.put(messageType,
							ChatColor.translateAlternateColorCodes('&', defaultMessages.getString(path)));
					continue;
				}
				String message = messages.getString(path);
				for (String parameter : messageType.parameters) {
					if (!message.contains(parameter)) {
						plugin.log(Level.WARNING, "Missing parameter \"%s\" for message \"%s\"!", parameter, path);
					}
				}
				this.loadedMessages.put(messageType,
						ChatColor.translateAlternateColorCodes('&', messages.getString(path)));
			}

			plugin.log(Level.INFO, "[Messages] Messages loaded successfully!");
			return true;
		} catch (Exception e) {
			plugin.log(Level.SEVERE, e, "An error occurred while trying to load the messages! Details below:");
			return false;
		}
	}

	private boolean loadDefaultMessages() {
		try (InputStream input = plugin.getResource(MESSAGES_FILE_NAME)) {
			this.defaultMessages = this.plugin.getVersionAdapter().loadFromResource(input);
			if (defaultMessages.getInt(MESSAGES_VERSION_KEY) != MESSAGES_VERSION) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean ensureCorrectVersion(boolean saveAndRetry) {
		int currentVersion = messages.getInt(MESSAGES_VERSION_KEY, -1);
		if (currentVersion == -1 && saveAndRetry) {
			plugin.log(Level.WARNING, "[Messages] The messages version is missing. Did you erase it by accident?");
			plugin.log(Level.INFO, "[Messages] Creating an up to date one...");
			plugin.saveResource(MESSAGES_FILE_NAME, true);
			this.messages = YamlConfiguration.loadConfiguration(messagesFile);
			return ensureCorrectVersion(false);
		}

		if (currentVersion != MESSAGES_VERSION) {
			if (saveAndRetry) {
				plugin.log(Level.WARNING, "[Messages] Outdated messages detected. Preparing to create a new one...");
				if (!moveOldMessages()) {
					plugin.log(Level.WARNING, "[Messages] Failed to move old Messages. Overwritting it...");
				}
				plugin.saveResource(MESSAGES_FILE_NAME, true);
				this.messages = YamlConfiguration.loadConfiguration(messagesFile);
				return ensureCorrectVersion(false);
			} else {
				return false;
			}
		}

		return true;
	}

	private boolean moveOldMessages() {
		try {
			String newFileName = String.format(OLD_MESSAGES_FILE_NAME, System.currentTimeMillis());
			File newFile = new File(plugin.getDataFolder(), newFileName);
			plugin.log(Level.INFO, "[Messages] The old %s is now \"%s\"", MESSAGES_FILE_NAME, newFileName);
			messagesFile.renameTo(newFile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static enum Message {

		COMMAND_RELOAD_SUCCESS("command.reload.success"),
		COMMAND_RELOAD_FAILURE("command.reload.failure"),
		COMMAND_NO_CONSOLE("command.no-console-allowed"),
		TOOL_NO_PERMISSION("tool.no-tool-permission", "tool"),
		SUB_TOOL_NO_PERMISSION("tool.no-sub-tool-permission", "sub-tool"),
		INVALID_LINE("tool.invalid-sign-line"),
		OVERRIDE_NO_PERMISSION("tool.override-no-permission"),
		TOOL_CHANGED("tool.tool-changed", "tool"),
		TOOL_SIGN_COPIED("tool.copy.sign-copied"),
		TOOL_LINE_COPIED("tool.copy.line-copied", "line"),
		TOOL_SIGN_REPLACED("tool.paste.sign-replaced"),
		TOOL_LINE_REPLACED("tool.paste.line-replaced", "line"),
		EMPTY_LINE_BUFFER("tool.paste.no-line-copied"),
		EMPTY_SIGN_BUFFER("tool.paste.no-sign-copied"),
		TOOL_SIGN_CLEARED("tool.erase.sign-cleared"),
		TOOL_LINE_CLEARED("tool.erase.line-cleared");

		private final String path;
		private final String[] parameters;

		private Message(String path, String... parameters) {
			this.path = path;
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = "{" + parameters[i] + "}";
			}
			this.parameters = parameters;
		}

		public String getPath() {
			return path;
		}
	}
}
