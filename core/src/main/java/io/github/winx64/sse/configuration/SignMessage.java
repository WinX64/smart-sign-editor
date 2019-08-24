package io.github.winx64.sse.configuration;

import io.github.winx64.sse.SmartSignEditor;
import org.bukkit.ChatColor;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public final class SignMessage extends BaseConfiguration {

    private static final int MESSAGES_VERSION = 3;

    private Map<NameKey, String> defaultMessages;
    private Map<NameKey, String> loadedMessages;

    public SignMessage(SmartSignEditor plugin) {
        super(plugin, "Messages", "messages.yml", "messages-version", MESSAGES_VERSION);

        this.defaultMessages = new EnumMap<>(NameKey.class);
        this.loadedMessages = new EnumMap<>(NameKey.class);
    }

    public String get(NameKey nameKey) {
        return loadedMessages.get(nameKey);
    }

    public String getDefault(NameKey nameKey) {
        return defaultMessages.get(nameKey);
    }

    public String get(NameKey nameKey, String... arguments) {
        String message = loadedMessages.get(nameKey);
        for (int i = 0; i < Math.min(nameKey.parameters.length, arguments.length); i++) {
            message = message.replace(nameKey.parameters[i], arguments[i]);
        }
        return message;
    }

    @Override
    protected void prepareConfiguration() throws ConfigurationException {
        for (NameKey nameKey : NameKey.values()) {
            String path = nameKey.getPath();
            if (!defaultConfig.contains(path)) {
                throw new ConfigurationException(String.format("Missing message \"%s\" from the default messages. " +
                        "Unable to continue!", path));
            }

            defaultMessages.put(nameKey, ChatColor.translateAlternateColorCodes('&', defaultConfig.getString(path)));
            if (!config.contains(path)) {
                plugin.log(Level.WARNING, "Missing message \"%s\". Using default value!", path);

                loadedMessages.put(nameKey, defaultMessages.get(nameKey));
                continue;
            }
            String message = config.getString(path);
            for (String parameter : nameKey.parameters) {
                if (!message.contains(parameter)) {
                    plugin.log(Level.WARNING, "Missing parameter \"%s\" for message \"%s\"!", parameter, path);
                }
            }
            this.loadedMessages.put(nameKey, ChatColor.translateAlternateColorCodes('&', config.getString(path)));
        }
    }

    public enum NameKey {

        COMMAND_RELOAD_SUCCESS("command.reload.success"),
        COMMAND_RELOAD_FAILURE("command.reload.failure"),
        COMMAND_NO_CONSOLE("command.no-console-allowed"),
        TOOL_NO_PERMISSION("tool.no-tool-permission", "tool"),
        SUB_TOOL_NO_PERMISSION("tool.no-sub-tool-permission", "sub-tool"),
        INVALID_LINE("tool.invalid-sign-line"),
        OVERRIDE_NO_PERMISSION("tool.override-no-permission"),
        TOOL_CHANGED("tool.tool-changed", "tool"),
        TOOL_EDIT_NAME("tool.edit.name"),
        TOOL_COPY_NAME("tool.copy.name"),
        TOOL_COPY_SUBTOOL_SIGN_COPY("tool.copy.subtool.sign-copy"),
        TOOL_COPY_SUBTOOL_LINE_COPY("tool.copy.subtool.line-copy"),
        TOOL_SIGN_COPIED("tool.copy.sign-copied"),
        TOOL_LINE_COPIED("tool.copy.line-copied", "line"),
        TOOL_PASTE_NAME("tool.paste.name"),
        TOOL_PASTE_SUBTOOL_SIGN_PASTE("tool.paste.subtool.sign-paste"),
        TOOL_PASTE_SUBTOOL_LINE_PASTE("tool.paste.subtool.line-paste"),
        TOOL_SIGN_REPLACED("tool.paste.sign-replaced"),
        TOOL_LINE_REPLACED("tool.paste.line-replaced", "line"),
        EMPTY_LINE_BUFFER("tool.paste.no-line-copied"),
        EMPTY_SIGN_BUFFER("tool.paste.no-sign-copied"),
        TOOL_ERASE_NAME("tool.erase.name"),
        TOOL_ERASE_SUBTOOL_SIGN_ERASE("tool.erase.subtool.sign-erase"),
        TOOL_ERASE_SUBTOOL_LINE_ERASE("tool.erase.subtool.line-erase"),
        TOOL_SIGN_CLEARED("tool.erase.sign-cleared"),
        TOOL_LINE_CLEARED("tool.erase.line-cleared");

        private final String path;
        private final String[] parameters;

        NameKey(String path, String... parameters) {
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
