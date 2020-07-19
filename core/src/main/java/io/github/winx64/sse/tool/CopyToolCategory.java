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

public class CopyToolCategory extends AbstractToolCategory {

    public CopyToolCategory(VersionAdapter adapter, SignConfiguration config, SignMessage message) {
        super(message, Message.TOOL_COPY_NAME, Permissions.TOOL_COPY);

        registerTool(new AbstractTool(adapter, message, Message.TOOL_COPY_SUBTOOL_SIGN_COPY, Permissions.TOOL_COPY_ALL,
                false, false, config::getSignCopyToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Block clickedSign) {
                Player player = sPlayer.getPlayer();
                Sign sign = (Sign) clickedSign.getState();

                for (int i = 0; i < 4; i++) {
                    if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                        sPlayer.setSignBuffer(i, ChatColor.stripColor(sign.getLine(i)));
                    } else {
                        sPlayer.setSignBuffer(i, sign.getLine(i));
                    }
                }

                player.sendMessage(message.get(Message.TOOL_SIGN_COPIED));
            }
        });

        registerTool(new AbstractTool(adapter, message, Message.TOOL_COPY_SUBTOOL_LINE_COPY, Permissions.TOOL_COPY_LINE,
                false, false, config::getLineCopyToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Block clickedSign) {
                Player player = sPlayer.getPlayer();

                runAfterLineValidation(player, clickedSign, ((sign, clickedLine) -> {
                    if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                        sPlayer.setLineBuffer(ChatColor.stripColor(sign.getLine(clickedLine)));
                    } else {
                        sPlayer.setLineBuffer(sign.getLine(clickedLine));
                    }
                    player.sendMessage(message.get(Message.TOOL_LINE_COPIED, sPlayer.getLineBuffer()));
                }));
            }
        });
    }
}
