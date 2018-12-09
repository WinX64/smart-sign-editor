package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class CopySubTool {

    private CopySubTool() {
    }

    public static final class SignCopySubTool extends SubTool {

        public SignCopySubTool() {
            super("sign-copy", NameKey.TOOL_COPY_SUBTOOL_SIGN_COPY, Permissions.TOOL_COPY_ALL, false, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            for (int i = 0; i < 4; i++) {
                if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                    sPlayer.setSignBuffer(i, ChatColor.stripColor(sign.getLine(i)));
                } else {
                    sPlayer.setSignBuffer(i, sign.getLine(i));
                }
            }
            player.sendMessage(plugin.getSignMessage().get(NameKey.TOOL_SIGN_COPIED));
            this.useCount++;
        }
    }

    public static final class LineCopySubTool extends SubTool {

        public LineCopySubTool() {
            super("line-copy", NameKey.TOOL_COPY_SUBTOOL_LINE_COPY, Permissions.TOOL_COPY_LINE, false, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            Vector intersection = MathUtil.getSightSignIntersection(player, sign);
            if (intersection == null) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.INVALID_LINE));
                return;
            }
            int clickedLine = MathUtil.getSignLine(intersection, sign);

            if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                sPlayer.setLineBuffer(ChatColor.stripColor(sign.getLine(clickedLine)));
            } else {
                sPlayer.setLineBuffer(sign.getLine(clickedLine));
            }
            player.sendMessage(plugin.getSignMessage().get(NameKey.TOOL_LINE_COPIED, sPlayer.getLineBuffer()));
            this.useCount++;
        }
    }
}
