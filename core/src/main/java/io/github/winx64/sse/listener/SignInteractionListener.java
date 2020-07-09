package io.github.winx64.sse.listener;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.data.SmartPlayer;
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

import java.util.HashMap;
import java.util.Map;

public final class SignInteractionListener implements Listener {

    private final SmartSignEditor plugin;
    private final SignConfiguration signConfig;
    private final SignMessage signMessage;

    private final Map<PlayerInteractEvent, BlockState> blockStates;

    public SignInteractionListener(SmartSignEditor plugin) {
        this.plugin = plugin;
        this.signConfig = plugin.getSignConfig();
        this.signMessage = plugin.getSignMessage();
        this.blockStates = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SmartPlayer sPlayer = plugin.getSmartPlayer(player.getUniqueId());
        if (sPlayer == null) {
            return;
        }

        Tool tool = sPlayer.getTool();
        Block block = event.getClickedBlock();
        BlockState state = block == null ? null : block.getState();
        Action action = event.getAction();
        ToolUsage usage = ToolUsage.getToolUsage(action, player.isSneaking());

        if (action == Action.PHYSICAL) {
            return;
        }

        if (!plugin.getVersionAdapter().shouldProcessEvent(event)) {
            return;
        }

        if (!this.signConfig.matchesItem(player.getItemInHand())) {
            return;
        }

        if (block == null && player.hasPermission(Permissions.EXTENDED_TOOL) && signConfig.isUsingExtendedTool()) {
            BlockIterator iterator = new BlockIterator(player, signConfig.getExtendedToolReach());
            while (iterator.hasNext()) {
                Block tracedBlock = iterator.next();
                state = tracedBlock.getState();
                if (state instanceof Sign) {
                    block = tracedBlock;
                    break;
                }
            }
        }

        if (block == null || !(state instanceof Sign)) {
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
        } else {
            SubTool subTool = tool.matchesUsage(usage);
            if (subTool == null) {
                return;
            }

            if (!player.hasPermission(tool.getPermission())) {
                player.sendMessage(signMessage.get(SignMessage.NameKey.TOOL_NO_PERMISSION, tool.getName(signMessage)));
                return;
            }
            if (!player.hasPermission(subTool.getPermission())) {
                player.sendMessage(signMessage.get(SignMessage.NameKey.SUB_TOOL_NO_PERMISSION, subTool.getName(signMessage)));
                return;
            }

            Sign sign = (Sign) state;
            if (subTool.modifiesWorld() && !checkBuildPermission(player, sign)) {
                return;
            }

            event.setCancelled(true);

            if (subTool.requiresPreSpecialHandling()) {
                this.handleSpecialSigns(event, sign);
            }
            subTool.use(plugin, sPlayer, sign);
            if (!subTool.requiresPreSpecialHandling()) {
                this.handleSpecialSigns(event, sign);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void postProcess(PlayerInteractEvent event) {
        BlockState lastSignState = blockStates.remove(event);
        if (lastSignState != null) {
            lastSignState.update();
        }
    }

    private void handleSpecialSigns(PlayerInteractEvent event, Sign sign) {
        String firstLine = ChatColor.stripColor(sign.getLine(0));
        if (signConfig.isSpecialSign(firstLine)) {
            blockStates.put(event, sign.getBlock().getState());
            sign.setLine(0, firstLine);
            sign.update(true);
        }
    }

    private boolean checkBuildPermission(Player player, Sign sign) {
        BlockBreakEvent event = new BlockBreakEvent(sign.getBlock(), player);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
}
