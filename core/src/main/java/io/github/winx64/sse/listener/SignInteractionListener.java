package io.github.winx64.sse.listener;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.player.PlayerRegistry;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolCategory;
import io.github.winx64.sse.tool.ToolRegistry;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

public final class SignInteractionListener implements Listener {

    private final PlayerRegistry playerRegistry;
    private final ToolRegistry toolRegistry;
    private final SignConfiguration signConfig;
    private final SignMessage signMessage;
    private final VersionAdapter adapter;

    private BlockState lastInteractedSign;

    public SignInteractionListener(PlayerRegistry playerRegistry, ToolRegistry toolRegistry,
                                   SignConfiguration signConfig, SignMessage signMessage, VersionAdapter adapter) {
        this.playerRegistry = playerRegistry;
        this.toolRegistry = toolRegistry;
        this.signConfig = signConfig;
        this.signMessage = signMessage;
        this.adapter = adapter;

        this.lastInteractedSign = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(Permissions.TOOL_EDIT_COLORS)) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        SmartPlayer sPlayer = playerRegistry.getPlayer(player);

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
        ToolCategory currentCategory = sPlayer.getSelectedToolCategory();
        ToolCategory newCategory = currentCategory;
        do {
           newCategory = toolRegistry.switchToolCategory(newCategory, forward);
        } while (newCategory != currentCategory && !player.hasPermission(newCategory.getPermission()));

        if (!player.hasPermission(newCategory.getPermission())) {
            return;
        }

        sPlayer.setSelectedToolCategory(newCategory);
        sPlayer.getPlayer().sendMessage(signMessage.get(Message.TOOL_CHANGED, newCategory.getName()));
    }

    private boolean handleUseTool(SmartPlayer sPlayer, Block clickedBlock, Action action) {
        Player player = sPlayer.getPlayer();

        ToolCategory selectedCategory = sPlayer.getSelectedToolCategory();
        ToolUsage usage = ToolUsage.getToolUsage(action, player.isSneaking());
        Tool tool = selectedCategory.getToolByUsage(usage);
        if (tool == null) {
            return false;
        }

        if (!player.hasPermission(selectedCategory.getPermission())) {
            player.sendMessage(signMessage.get(Message.TOOL_NO_PERMISSION, selectedCategory.getName()));
            return false;
        }
        if (!player.hasPermission(tool.getPermission())) {
            player.sendMessage(signMessage.get(Message.SUB_TOOL_NO_PERMISSION, tool.getName()));
            return false;
        }

        if (tool.modifiesWorld() && !checkBuildPermission(player, clickedBlock)) {
            return false;
        }

        if (tool.requiresSpecialHandling()) {
            this.handleSpecialSigns(clickedBlock);
        }

        Sign clickedSign = (Sign) clickedBlock.getState();
        tool.use(sPlayer, clickedSign);

        if (!tool.requiresSpecialHandling()) {
            this.handleSpecialSigns(clickedBlock);
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
