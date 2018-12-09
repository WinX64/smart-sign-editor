package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class EraseSubTool {

    private EraseSubTool() {
    }

    public static final class SignEraseSubTool extends SubTool {

        public SignEraseSubTool() {
            super("sign-erase", NameKey.TOOL_ERASE_SUBTOOL_SIGN_ERASE, Permissions.TOOL_ERASE_ALL, true, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            if (plugin.getVersionAdapter().isSignBeingEdited(sign)
                    && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            for (int i = 0; i < 4; i++) {
                sign.setLine(i, "");
            }
            sign.update();
            player.sendMessage(plugin.getSignMessage().get(NameKey.TOOL_SIGN_CLEARED));
            this.useCount++;
        }
    }

    public static final class LineEraseSubTool extends SubTool {

        public LineEraseSubTool() {
            super("line-erase", NameKey.TOOL_ERASE_SUBTOOL_LINE_ERASE, Permissions.TOOL_ERASE_LINE, true, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
            Player player = sPlayer.getPlayer();

            if (plugin.getVersionAdapter().isSignBeingEdited(sign)
                    && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            Vector intersection = MathUtil.getSightSignIntersection(player, sign);
            if (intersection == null) {
                player.sendMessage(plugin.getSignMessage().get(NameKey.INVALID_LINE));
                return;
            }

            int clickedLine = MathUtil.getSignLine(intersection, sign);

            sign.setLine(clickedLine, "");
            sign.update();
            player.sendMessage(plugin.getSignMessage().get(NameKey.TOOL_LINE_CLEARED));
            this.useCount++;
        }
    }
}
