package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class EraseToolCategory extends AbstractToolCategory {

    public EraseToolCategory(VersionAdapter adapter, SignConfiguration config, SignMessage message) {
        super(message, Message.TOOL_ERASE_NAME, Permissions.TOOL_ERASE);

        registerTool(new AbstractTool(adapter, message, Message.TOOL_ERASE_SUBTOOL_SIGN_ERASE,
                Permissions.TOOL_ERASE_ALL, true, false,
                config::getSignEraseToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Block clickedSign) {
                Player player = sPlayer.getPlayer();

                if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                    player.sendMessage(message.get(Message.OVERRIDE_NO_PERMISSION));
                    return;
                }

                Sign sign = (Sign) clickedSign.getState();
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, "");
                }
                sign.update();
                player.sendMessage(message.get(Message.TOOL_SIGN_CLEARED));
            }
        });

        registerTool(new AbstractTool(adapter, message, Message.TOOL_ERASE_SUBTOOL_LINE_ERASE,
                Permissions.TOOL_ERASE_LINE, true, false,
                config::getLineEraseToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Block clickedSign) {
                Player player = sPlayer.getPlayer();

                if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                    player.sendMessage(message.get(Message.OVERRIDE_NO_PERMISSION));
                    return;
                }

                runAfterLineValidation(player, clickedSign, (sign, clickedLine) -> {
                    sign.setLine(clickedLine, "");
                    sign.update();
                    player.sendMessage(message.get(Message.TOOL_LINE_CLEARED));
                });
            }
        });
    }
}
