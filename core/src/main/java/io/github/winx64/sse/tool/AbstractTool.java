package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.Message;
import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
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
    public String getName() {
        return message.get(toolName);
    }

    @Override
    public String getPermission() {
        return toolPermission;
    }

    @Override
    public boolean modifiesWorld() {
        return modifiesWorld;
    }

    @Override
    public boolean requiresSpecialHandling() {
        return requiresSpecialHandling;
    }

    protected void runAfterLineValidation(Player player, Block clickedSign, BiConsumer<Sign, Integer> postAction) {
        SignData signData = adapter.getSignData(clickedSign);
        Vector intersection = MathUtil.getSightSignIntersection(player, clickedSign.getLocation(), signData);
        if (intersection == null) {
            player.sendMessage(message.get(Message.INVALID_LINE));
            return;
        }
        int clickedLine = MathUtil.getSignLine(intersection, clickedSign.getLocation(), signData);
        Sign sign = (Sign) clickedSign.getState();
        postAction.accept(sign, clickedLine);
    }
}
