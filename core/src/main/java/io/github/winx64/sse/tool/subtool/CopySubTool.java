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

public final class CopySubTool {

    private CopySubTool() {
    }

    public static final class SignCopySubTool extends SubTool {

        public SignCopySubTool() {
            super("sign-copy", NameKey.TOOL_COPY_SUBTOOL_SIGN_COPY, Permissions.TOOL_COPY_ALL, false, false, ToolUsage.NO_SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();
            Sign sign = (Sign) clickedSign.getState();

            for (int i = 0; i < 4; i++) {
                if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                    sPlayer.setSignBuffer(i, ChatColor.stripColor(sign.getLine(i)));
                } else {
                    sPlayer.setSignBuffer(i, sign.getLine(i));
                }
            }

            player.sendMessage(signMessage.get(NameKey.TOOL_SIGN_COPIED));
            this.useCount++;
        }
    }

    public static final class LineCopySubTool extends SubTool {

        public LineCopySubTool() {
            super("line-copy", NameKey.TOOL_COPY_SUBTOOL_LINE_COPY, Permissions.TOOL_COPY_LINE, false, false, ToolUsage.SHIFT_RIGHT_CLICK);
        }

        @Override
        public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
            Player player = sPlayer.getPlayer();

            runAfterLineValidation(adapter, signMessage, player, clickedSign, ((sign, clickedLine) -> {
                if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                    sPlayer.setLineBuffer(ChatColor.stripColor(sign.getLine(clickedLine)));
                } else {
                    sPlayer.setLineBuffer(sign.getLine(clickedLine));
                }
                player.sendMessage(signMessage.get(NameKey.TOOL_LINE_COPIED, sPlayer.getLineBuffer()));
                this.useCount++;
            }));
        }
    }
}
