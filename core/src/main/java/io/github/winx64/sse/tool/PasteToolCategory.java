package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class PasteToolCategory extends AbstractToolCategory {

    public PasteToolCategory(VersionAdapter adapter, SignConfiguration config, SignMessage message) {
        super(message, Message.TOOL_PASTE_NAME, Permissions.TOOL_PASTE);

        registerTool(new AbstractTool(adapter, message, Message.TOOL_PASTE_SUBTOOL_SIGN_PASTE,
                Permissions.TOOL_PASTE_ALL, true, false,
                config::getSignPasteToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Sign clickedSign) {
                Player player = sPlayer.getPlayer();

                if (sPlayer.getSignBuffer() == null) {
                    player.sendMessage(message.get(Message.EMPTY_SIGN_BUFFER));
                    return;
                }

                if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                    player.sendMessage(message.get(Message.OVERRIDE_NO_PERMISSION));
                    return;
                }

                for (int i = 0; i < 4; i++) {
                    if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                        clickedSign.setLine(i, sPlayer.getSignBuffer()[i]);
                    } else {
                        clickedSign.setLine(i, ChatColor.stripColor(sPlayer.getSignBuffer()[i]));
                    }
                }
                clickedSign.update();
                player.sendMessage(message.get(Message.TOOL_SIGN_REPLACED));
            }
        });

        registerTool(new AbstractTool(adapter, message, Message.TOOL_PASTE_SUBTOOL_LINE_PASTE,
                Permissions.TOOL_PASTE_LINE, true, false,
                config::getLinePasteToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Sign clickedSign) {
                Player player = sPlayer.getPlayer();

                if (sPlayer.getLineBuffer() == null) {
                    player.sendMessage(message.get(Message.EMPTY_LINE_BUFFER));
                    return;
                }

                if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                    player.sendMessage(message.get(Message.OVERRIDE_NO_PERMISSION));
                    return;
                }

                runAfterLineValidation(player, clickedSign, clickedLine -> {
                    if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                        clickedSign.setLine(clickedLine, sPlayer.getLineBuffer());
                    } else {
                        clickedSign.setLine(clickedLine, ChatColor.stripColor(sPlayer.getLineBuffer()));
                    }
                    clickedSign.update();
                    player.sendMessage(message.get(Message.TOOL_LINE_REPLACED, sPlayer.getLineBuffer()));
                });
            }
        });
    }
}
