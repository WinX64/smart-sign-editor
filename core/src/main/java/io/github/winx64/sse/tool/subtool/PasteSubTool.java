package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.data.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;


public final class PasteSubTool {

    private PasteSubTool() {
    }

    public static final class SignPasteSubTool extends SubTool {

        public SignPasteSubTool() {
            super("sign-paste", NameKey.TOOL_PASTE_SUBTOOL_SIGN_PASTE, Permissions.TOOL_PASTE_ALL, true, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();

            if (sPlayer.getSignBuffer() == null) {
                player.sendMessage(signMessage.get(SignMessage.NameKey.EMPTY_SIGN_BUFFER));
                return;
            }

            if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(signMessage.get(SignMessage.NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            Sign sign = (Sign) clickedSign.getState();
            for (int i = 0; i < 4; i++) {
                if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                    sign.setLine(i, sPlayer.getSignBuffer()[i]);
                } else {
                    sign.setLine(i, ChatColor.stripColor(sPlayer.getSignBuffer()[i]));
                }
            }
            sign.update();
            player.sendMessage(signMessage.get(SignMessage.NameKey.TOOL_SIGN_REPLACED));
            this.useCount++;
        }
    }

    public static final class LinePasteSubTool extends SubTool {

        public LinePasteSubTool() {
            super("line-paste", NameKey.TOOL_PASTE_SUBTOOL_LINE_PASTE, Permissions.TOOL_PASTE_LINE, true, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();

            if (sPlayer.getLineBuffer() == null) {
                player.sendMessage(signMessage.get(NameKey.EMPTY_LINE_BUFFER));
                return;
            }

            if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(signMessage.get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            runAfterLineValidation(adapter, signMessage, player, clickedSign, (sign, clickedLine) -> {
                if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                    sign.setLine(clickedLine, sPlayer.getLineBuffer());
                } else {
                    sign.setLine(clickedLine, ChatColor.stripColor(sPlayer.getLineBuffer()));
                }
                sign.update();
                player.sendMessage(signMessage.get(SignMessage.NameKey.TOOL_LINE_REPLACED, sPlayer.getLineBuffer()));
                this.useCount++;
            });
        }
    }
}
