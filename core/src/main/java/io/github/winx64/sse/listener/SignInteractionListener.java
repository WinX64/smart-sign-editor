package io.github.winx64.sse.listener;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.data.PlayerRepository;
import io.github.winx64.sse.data.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

public final class SignInteractionListener implements Listener {

    private final PlayerRepository repository;
    private final SignConfiguration signConfig;
    private final SignMessage signMessage;
    private final VersionAdapter adapter;

    private BlockState lastInteractedSign;

    public SignInteractionListener(SmartSignEditor plugin) {
        this.repository = plugin;
        this.signConfig = plugin.getSignConfig();
        this.signMessage = plugin.getSignMessage();
        this.adapter = plugin.getVersionAdapter();

        this.lastInteractedSign = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        SmartPlayer sPlayer = repository.getPlayer(player);

        if (sPlayer == null) {
            return;
        }
        if (action == Action.PHYSICAL) {
            return;
        }
        if (!adapter.shouldProcessEvent(event)) {
            return;
        }

        if (!this.signConfig.matchesItem(player.getItemInHand())) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        //Player clicked air, let's use ray-tracing to get the sign they're looking at
        if (clickedBlock == null) {
            //Only if the feature is enabled and the player has permission
            if (signConfig.isUsingExtendedTool() && player.hasPermission(Permissions.EXTENDED_TOOL)) {
                BlockIterator iterator = new BlockIterator(player, signConfig.getExtendedToolReach());
                while (iterator.hasNext()) {
                    clickedBlock = iterator.next();

                    //If a sign is found, stop tracing
                    if (adapter.isSign(clickedBlock)) {
                        break;
                    }
                }
            }
        }

        if (clickedBlock == null || !adapter.isSign(clickedBlock)) {
            handleChangeTool(sPlayer);
        } else {
            event.setCancelled(handleUseTool(sPlayer, clickedBlock, action));
        }
    }

    private void handleChangeTool(SmartPlayer sPlayer) {
        Player player = sPlayer.getPlayer();
        boolean forward = !player.isSneaking();
        Tool currentTool = sPlayer.getTool();
        Tool newTool = currentTool;
        do {
            newTool = forward ? newTool.getNextToolMode() : newTool.getPreviousToolMode();
        } while (newTool != currentTool && !player.hasPermission(newTool.getPermission()));

        if (!player.hasPermission(newTool.getPermission())) {
            return;
        }

        sPlayer.setTool(newTool);
        sPlayer.getPlayer().sendMessage(signMessage.get(NameKey.TOOL_CHANGED, newTool.getName(signMessage)));
    }

    private boolean handleUseTool(SmartPlayer sPlayer, Block clickedSign, Action action) {
        Player player = sPlayer.getPlayer();
        Tool tool = sPlayer.getTool();
        ToolUsage usage = ToolUsage.getToolUsage(action, player.isSneaking());
        SubTool subTool = tool.matchesUsage(usage);
        if (subTool == null) {
            return false;
        }

        if (!player.hasPermission(tool.getPermission())) {
            player.sendMessage(signMessage.get(NameKey.TOOL_NO_PERMISSION, tool.getName(signMessage)));
            return false;
        }
        if (!player.hasPermission(subTool.getPermission())) {
            player.sendMessage(signMessage.get(NameKey.SUB_TOOL_NO_PERMISSION, subTool.getName(signMessage)));
            return false;
        }

        if (subTool.modifiesWorld() && !checkBuildPermission(player, clickedSign)) {
            return false;
        }

        if (subTool.requiresPreSpecialHandling()) {
            this.handleSpecialSigns(clickedSign);
        }
        subTool.use(adapter, signMessage, sPlayer, clickedSign);
        if (!subTool.requiresPreSpecialHandling()) {
            this.handleSpecialSigns(clickedSign);
        }

        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void postProcess(PlayerInteractEvent event) {
        if (lastInteractedSign != null) {
            lastInteractedSign.update();
            lastInteractedSign = null;
        }
    }

    private void handleSpecialSigns(Block clickedSign) {
        Sign sign = (Sign) clickedSign.getState();
        String firstLine = ChatColor.stripColor(sign.getLine(0));
        if (signConfig.isSpecialSign(firstLine)) {
            lastInteractedSign = clickedSign.getState();
            sign.setLine(0, firstLine);
            sign.update(true);
        }
    }

    private boolean checkBuildPermission(Player player, Block clickedSign) {
        BlockBreakEvent event = new BlockBreakEvent(clickedSign, player);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
}
