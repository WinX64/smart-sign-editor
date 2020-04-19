package io.github.winx64.sse;

import io.github.winx64.sse.command.CommandReload;
import io.github.winx64.sse.command.CommandTool;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.handler.VersionHandler;
import io.github.winx64.sse.listener.PlayerInOutListener;
import io.github.winx64.sse.listener.SignChangeListener;
import io.github.winx64.sse.listener.SignInteractionListener;
import io.github.winx64.sse.player.SmartPlayer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SmartSignEditor's main class
 *
 * @author WinX64
 */
public final class SmartSignEditor extends JavaPlugin {

    private static final int PLUGIN_ID = 36;

    private final Map<UUID, SmartPlayer> smartPlayers;

    private final Logger logger;
    private final SignConfiguration signConfig;
    private final SignMessage signMessage;

    private VersionAdapter versionAdapter;

    public SmartSignEditor() {
        this.logger = getLogger();
        this.smartPlayers = new HashMap<>();
        this.signConfig = new SignConfiguration(this);
        this.signMessage = new SignMessage(this);
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
                logger.severe("Your current version is " + currentVersion + ". This is a newer version that is " +
                        "still not supported. Ask the author to provide support for it!");
            }
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        logger.info("Registered version adapter with success! Using " + versionAdapter.getClass().getSimpleName());

        if (!signConfig.initializeConfiguration()) {
            logger.severe("Failed to load the configuration. The plugin will be disabled to avoid further damage!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!signMessage.initializeConfiguration()) {
            logger.severe("Failed to load the messages. The plugin is unable to function correctly without them!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            smartPlayers.put(player.getUniqueId(), new SmartPlayer(player));
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerInOutListener(this), this);
        pm.registerEvents(new SignChangeListener(), this);
        pm.registerEvents(new SignInteractionListener(this), this);

        this.getCommand("sse").setExecutor(new CommandTool(this));
        this.getCommand("sse-reload").setExecutor(new CommandReload(this));

        new Metrics(this, PLUGIN_ID);
    }

    public SignConfiguration getSignConfig() {
        return signConfig;
    }

    public SignMessage getSignMessage() {
        return signMessage;
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
