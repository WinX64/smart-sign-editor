package io.github.winx64.sse.configuration;

import io.github.winx64.sse.SmartSignEditor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.logging.Level;

public abstract class BaseConfiguration {

    protected final SmartSignEditor plugin;
    private final String prefix;

    private final String configFileName;
    private final File configFile;
    private final String configVersionKey;
    private final int configVersion;

    FileConfiguration config;
    FileConfiguration defaultConfig;

    BaseConfiguration(SmartSignEditor plugin, String prefix, String configFileName, String configVersionKey,
                      int configVersion) {
        this.plugin = plugin;
        this.prefix = prefix;

        this.configFileName = configFileName;
        this.configFile = new File(plugin.getDataFolder(), configFileName);
        this.configVersionKey = configVersionKey;
        this.configVersion = configVersion;
    }

    public final boolean initializeConfiguration() {
        try {
            loadDefaultFileConfiguration();
            loadFileConfiguration();

            prepareConfiguration();

            plugin.log(Level.INFO, "[%s] Configuration loaded successfully!", prefix);
            return true;
        } catch (ConfigurationException e) {
            plugin.log(Level.SEVERE, e, "[%s] An error occurred while trying to load the configuration! Details below:",
                    prefix);
            return false;
        }
    }

    private void loadFileConfiguration() throws ConfigurationException {
        if (!configFile.exists()) {
            plugin.log(Level.INFO, "[%s] Configuration file doesn't exist! Creating a new one...", prefix);
            plugin.saveResource(configFileName, true);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        int currentVersion = config.getInt(configVersionKey, -1);
        if (currentVersion != configVersion) {
            String newFileName = configFileName + "." + System.currentTimeMillis() + ".old";
            File newFile = new File(plugin.getDataFolder(), newFileName);
            try {
                Files.move(configFile.toPath(), newFile.toPath());
            } catch (IOException e) {
                throw new ConfigurationException(String.format("Failed to rename the old configuration file to \"%s\"",
                        newFileName), e);
            }
            plugin.log(Level.INFO, "[%s] The old \"%s\" is now \"%s\"", prefix, configFileName, newFileName);
            plugin.saveResource(configFileName, true);
            this.config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    private void loadDefaultFileConfiguration() throws ConfigurationException {
        try (InputStream input = plugin.getResource(configFileName)) {
            if (input == null) {
                throw new ConfigurationException(
                        "The default configuration file doesn't exist inside the plugin's jar file!");
            }
            this.defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
            int defaultConfigVersion = defaultConfig.getInt(configVersionKey, -1);
            if (defaultConfigVersion != configVersion) {
                throw new ConfigurationException(String.format("Version mismatch between the current version(%d) and " +
                                "default configuration's version(%d)!", configVersion, defaultConfigVersion));
            }
        } catch (IOException ignored) { }
    }

    protected abstract void prepareConfiguration() throws ConfigurationException;
}
