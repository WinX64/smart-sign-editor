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
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.Tool;
import org.bstats.bukkit.Metrics;
import org.bstats.bukkit.Metrics.DrilldownPie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toMap;

/**
 * SmartSignEditor's main class
 *
 * @author WinX64
 */
public final class SmartSignEditor extends JavaPlugin {

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

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new DrilldownPie("mostUsedTool", this::getMostUsedToolMetric));
        metrics.addCustomChart(new DrilldownPie("toolUseCount", this::getToolUseCountMetric));
    }

    public SignConfiguration getSignConfig() {
        return signConfig;
    }

    public SignMessage getSignMessage() {
        return signMessage;
    }

    private Map<String, Map<String, Integer>> getMostUsedToolMetric() {
        Tool mostUsedTool = null;
        for (Tool tool : Tool.values()) {
            if (mostUsedTool == null || mostUsedTool.getTotalUseCount() < tool.getTotalUseCount()) {
                mostUsedTool = tool;
            }
        }

        if (mostUsedTool == null) {
            return emptyMap();
        }

        SubTool mostUsedSubTool = null;
        for (SubTool subTool : mostUsedTool.getSubTools().values()) {
            if (mostUsedSubTool == null || mostUsedSubTool.getUseCount() < subTool.getUseCount()) {
                mostUsedSubTool = subTool;
            }
        }

        if (mostUsedSubTool == null) {
            return emptyMap();
        }

        String toolName = signMessage.getDefault(mostUsedTool.getNameKey());
        String subToolName = signMessage.getDefault(mostUsedSubTool.getNameKey());
        return singletonMap(toolName, singletonMap(subToolName, 1));
    }

    private Map<String, Map<String, Integer>> getToolUseCountMetric() {
        return stream(Tool.values()).collect(toMap(
                (tool) -> signMessage.getDefault(tool.getNameKey()),
                (tool) -> tool.getSubTools().values().stream().collect(toMap(
                        (subTool) -> signMessage.getDefault(subTool.getNameKey()),
                        SubTool::getUseCount))));
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
