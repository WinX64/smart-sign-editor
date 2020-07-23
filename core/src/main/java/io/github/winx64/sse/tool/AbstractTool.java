package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.util.MathUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTool implements Tool {

    private final VersionAdapter adapter;
    private final SignMessage message;

    private final Message toolName;
    private final String toolPermission;

    private final boolean modifiesWorld;
    private final boolean requiresSpecialHandling;

    private final Supplier<ToolUsage> usageAccessor;

    public AbstractTool(VersionAdapter adapter, SignMessage message, Message toolName, String toolPermission,
                        boolean modifiesWorld, boolean requiresSpecialHandling, Supplier<ToolUsage> usageAccessor) {
        this.adapter = adapter;
        this.message = message;

        this.toolName = toolName;
        this.toolPermission = toolPermission;

        this.modifiesWorld = modifiesWorld;
        this.requiresSpecialHandling = requiresSpecialHandling;
        this.usageAccessor = usageAccessor;
    }

    protected ToolUsage getUsage() {
        return usageAccessor.get();
    }

    @Override
    public @NotNull String getName() {
        return message.get(toolName);
    }

    @Override
    public @NotNull String getPermission() {
        return toolPermission;
    }

    @Override
    public boolean modifiesWorld() {
        return modifiesWorld;
    }

    @Override
    public boolean requiresPriorSpecialHandling() {
        return requiresSpecialHandling;
    }

    protected void runAfterLineValidation(Player player, Sign clickedSign, Consumer<Integer> postAction) {
        SignData signData = adapter.getSignData(clickedSign);
        Vector intersection = MathUtil.getSightSignIntersection(player, clickedSign.getLocation(), signData);
        if (intersection == null) {
            player.sendMessage(message.get(Message.INVALID_LINE));
            return;
        }
        int clickedLine = MathUtil.getSignLine(intersection, clickedSign.getLocation(), signData);
        postAction.accept(clickedLine);
    }
}
