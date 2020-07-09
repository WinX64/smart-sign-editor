package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.data.SmartPlayer;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public final class EraseSubTool {

    private EraseSubTool() {
    }

    public static final class SignEraseSubTool extends SubTool {

        public SignEraseSubTool() {
            super("sign-erase", NameKey.TOOL_ERASE_SUBTOOL_SIGN_ERASE, Permissions.TOOL_ERASE_ALL, true, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();

            if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(signMessage.get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            Sign sign = (Sign) clickedSign.getState();
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, "");
            }
            sign.update();
            player.sendMessage(signMessage.get(NameKey.TOOL_SIGN_CLEARED));
            this.useCount++;
        }
    }

    public static final class LineEraseSubTool extends SubTool {

        public LineEraseSubTool() {
            super("line-erase", NameKey.TOOL_ERASE_SUBTOOL_LINE_ERASE, Permissions.TOOL_ERASE_LINE, true, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();

            if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
                player.sendMessage(signMessage.get(NameKey.OVERRIDE_NO_PERMISSION));
                return;
            }

            runAfterLineValidation(adapter, signMessage, player, clickedSign, (sign, clickedLine) -> {
                sign.setLine(clickedLine, "");
                sign.update();
                player.sendMessage(signMessage.get(NameKey.TOOL_LINE_CLEARED));
                this.useCount++;
            });
        }
    }
}
