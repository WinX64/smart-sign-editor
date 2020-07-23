package io.github.winx64.sse;

import io.github.winx64.sse.command.CommandReload;
import io.github.winx64.sse.command.CommandTool;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.handler.VersionHandler;
import io.github.winx64.sse.listener.SignInteractionListener;
import io.github.winx64.sse.player.PlayerRegistry;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.player.SmartPlayerImpl;
import io.github.winx64.sse.tool.CopyToolCategory;
import io.github.winx64.sse.tool.EditToolCategory;
import io.github.winx64.sse.tool.EraseToolCategory;
import io.github.winx64.sse.tool.PasteToolCategory;
import io.github.winx64.sse.tool.ToolCategory;
import io.github.winx64.sse.tool.ToolRegistry;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SmartSignEditor's main class
 *
 * @author WinX64
 */
public final class SmartSignEditor extends JavaPlugin implements PlayerRegistry, ToolRegistry {

    private static final int PLUGIN_ID = 36;

    private final Map<UUID, SmartPlayer> smartPlayers;
    private final List<ToolCategory> registeredTools;

    private final Logger logger;
    private final SignConfiguration signConfig;
    private final SignMessage signMessage;

    private VersionAdapter versionAdapter;

    public SmartSignEditor() {
        this.logger = getLogger();
        this.smartPlayers = new HashMap<>();
        this.registeredTools = new ArrayList<>();
        this.signConfig = new SignConfiguration(this);
        this.signMessage = new SignMessage(this);
    }

    @Override
    public void onEnable() {
        if (!hookVersionAdapter()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerDefaultTools();

        registerListeners();

        registerCommands();

        for (Player player : Bukkit.getOnlinePlayers()) {
            smartPlayers.put(player.getUniqueId(), new SmartPlayerImpl(player, getDefaultToolCategory()));
        }

        registerServices();

        new Metrics(this, PLUGIN_ID);
    }

    private boolean hookVersionAdapter() {
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
            return false;
        }
        logger.info("Registered version adapter with success! Using " + versionAdapter.getClass().getSimpleName());

        if (!signConfig.initializeConfiguration()) {
            logger.severe("Failed to load the configuration. The plugin will be disabled to avoid further damage!");
            return false;
        }

        if (!signMessage.initializeConfiguration()) {
            logger.severe("Failed to load the messages. The plugin is unable to function correctly without them!");
            return false;
        }
        return true;
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                smartPlayers.put(player.getUniqueId(), new SmartPlayerImpl(player, getDefaultToolCategory()));
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                smartPlayers.remove(event.getPlayer().getUniqueId());
            }
        }, this);
        pm.registerEvents(new SignInteractionListener(this, this, signConfig, signMessage, versionAdapter), this);
    }

    private void registerCommands() {
        this.getCommand("sse").setExecutor(new CommandTool(signConfig, signMessage));
        this.getCommand("sse-reload").setExecutor(new CommandReload(signConfig, signMessage));
    }

    private void registerServices() {
        Bukkit.getServicesManager().register(PlayerRegistry.class, this, this, ServicePriority.Normal);
        Bukkit.getServicesManager().register(ToolRegistry.class, this, this, ServicePriority.Normal);
    }

    private void registerDefaultTools() {
        registeredTools.add(new EditToolCategory(versionAdapter, signConfig, signMessage));
        registeredTools.add(new CopyToolCategory(versionAdapter, signConfig, signMessage));
        registeredTools.add(new PasteToolCategory(versionAdapter, signConfig, signMessage));
        registeredTools.add(new EraseToolCategory(versionAdapter, signConfig, signMessage));
    }

    @Override
    public SmartPlayer getPlayer(@NotNull Player player) {
        return smartPlayers.get(player.getUniqueId());
    }

    @Override
    public @NotNull ToolCategory getDefaultToolCategory() {
        return registeredTools.get(0);
    }

    @Override
    public @NotNull List<ToolCategory> getToolCategories() {
        return Collections.unmodifiableList(registeredTools);
    }

    public void log(Level level, String format, Object... objects) {
        log(level, null, format, objects);
    }

    public void log(Level level, Exception e, String format, Object... objects) {
        logger.log(level, String.format(format, objects), e);
    }
}
