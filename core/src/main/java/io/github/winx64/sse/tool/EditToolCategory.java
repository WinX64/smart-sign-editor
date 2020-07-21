package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class EditToolCategory extends AbstractToolCategory {

    public EditToolCategory(VersionAdapter adapter, SignConfiguration config, SignMessage message) {
        super(message, Message.TOOL_EDIT_NAME, Permissions.TOOL_EDIT);

        registerTool(new AbstractTool(adapter, message, Message.TOOL_EDIT_NAME, Permissions.TOOL_EDIT,
                true, true, config::getEditToolUsage) {
            @Override
            public void use(SmartPlayer sPlayer, Sign clickedSign) {
                Player player = sPlayer.getPlayer();

                if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                    player.sendMessage(message.get(Message.OVERRIDE_NO_PERMISSION));
                    return;
                }

                String[] noColors = new String[4];
                for (int i = 0; i < 4; i++) {
                    noColors[i] = clickedSign.getLine(i).replace(ChatColor.COLOR_CHAR, '&');
                }

                Location location = clickedSign.getLocation();
                adapter.updateSignText(player, location, noColors);
                adapter.openSignEditor(player, location);
            }
        });
    }
}
