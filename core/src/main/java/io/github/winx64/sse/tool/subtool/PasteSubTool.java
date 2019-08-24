package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public final class PasteSubTool {

    private PasteSubTool() {
    }

    public static final class SignPasteSubTool extends SubTool {

        public SignPasteSubTool() {
            super("sign-paste", NameKey.TOOL_PASTE_SUBTOOL_SIGN_PASTE, Permissions.TOOL_PASTE_ALL, true, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            if (sPlayer.getSignBuffer() == null) {
                player.sendMessage(plugin.getSignMessage().get(SignMessage.NameKey.EMPTY_SIGN_BUFFER));
                return;
            }

            if (plugin.getVersionAdapter().isSignBeingEdited(sign)
                    && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(plugin.getSignMessage().get(SignMessage.NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            for (int i = 0; i < 4; i++) {
                if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                    sign.setLine(i, sPlayer.getSignBuffer()[i]);
                } else {
                    sign.setLine(i, ChatColor.stripColor(sPlayer.getSignBuffer()[i]));
                }
            }
            sign.update();
            player.sendMessage(plugin.getSignMessage().get(SignMessage.NameKey.TOOL_SIGN_REPLACED));
            this.useCount++;
        }
    }

    public static final class LinePasteSubTool extends SubTool {

        public LinePasteSubTool() {
            super("line-paste", NameKey.TOOL_PASTE_SUBTOOL_LINE_PASTE, Permissions.TOOL_PASTE_LINE, true, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            if (sPlayer.getLineBuffer() == null) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.EMPTY_LINE_BUFFER));
                return;
            }

            if (plugin.getVersionAdapter().isSignBeingEdited(sign)
                    && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            org.bukkit.material.Sign signData = plugin.getVersionAdapter().buildSignMaterialData(sign);
            Vector intersection = MathUtil.getSightSignIntersection(player, sign.getLocation(), signData);
            if (intersection == null) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.INVALID_LINE));
                return;
            }
            int clickedLine = MathUtil.getSignLine(intersection, sign.getLocation(), signData);

            if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
                sign.setLine(clickedLine, sPlayer.getLineBuffer());
            } else {
                sign.setLine(clickedLine, ChatColor.stripColor(sPlayer.getLineBuffer()));
            }
            sign.update();
            player.sendMessage(plugin.getSignMessage().get(SignMessage.NameKey.TOOL_LINE_REPLACED, sPlayer.getLineBuffer()));
            this.useCount++;
        }
    }
}
