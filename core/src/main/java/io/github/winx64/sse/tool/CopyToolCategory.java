package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CopyToolCategory extends AbstractToolCategory {

    public CopyToolCategory(VersionAdapter adapter, SignConfiguration config, SignMessage message) {
        super(message, Message.TOOL_COPY_NAME, Permissions.TOOL_COPY);

        registerTool(new AbstractTool(adapter, message, Message.TOOL_COPY_SUBTOOL_SIGN_COPY, Permissions.TOOL_COPY_ALL,
                false, false, config::getSignCopyToolUsage) {
            @Override
            public void use(@NotNull SmartPlayer sPlayer, @NotNull Sign clickedSign) {
                Player player = sPlayer.getPlayer();

                boolean canCopyColors = player.hasPermission(Permissions.TOOL_COPY_COLORS);
                List<String> newSignBuffer = Arrays.stream(clickedSign.getLines())
                        .map(line -> canCopyColors ? line : ChatColor.stripColor(line))
                        .collect(Collectors.toList());
                sPlayer.setSignBuffer(newSignBuffer);

                player.sendMessage(message.get(Message.TOOL_SIGN_COPIED));
            }
        });

        registerTool(new AbstractTool(adapter, message, Message.TOOL_COPY_SUBTOOL_LINE_COPY, Permissions.TOOL_COPY_LINE,
                false, false, config::getLineCopyToolUsage) {
            @Override
            public void use(@NotNull SmartPlayer sPlayer, @NotNull Sign clickedSign) {
                Player player = sPlayer.getPlayer();

                runAfterLineValidation(player, clickedSign, (clickedLine -> {
                    if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
                        sPlayer.setLineBuffer(ChatColor.stripColor(clickedSign.getLine(clickedLine)));
                    } else {
                        sPlayer.setLineBuffer(clickedSign.getLine(clickedLine));
                    }
                    player.sendMessage(message.get(Message.TOOL_LINE_COPIED, sPlayer.getLineBuffer()));
                }));
            }
        });
    }
}
