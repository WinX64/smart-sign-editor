package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.data.SignData;
import io.github.winx64.sse.data.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;

public abstract class SubTool {

    private final String id;
    private final NameKey nameKey;
    private final String permission;
    private final boolean modifiesWorld;
    private final boolean preSpecialHandling;
    protected int useCount;
    private ToolUsage usage;

    public SubTool(String id, NameKey nameKey, String permission, boolean modifiesWorld, boolean preSpecialHandling, ToolUsage usage) {
        this.id = id;
        this.nameKey = nameKey;
        this.permission = permission;
        this.modifiesWorld = modifiesWorld;
        this.preSpecialHandling = preSpecialHandling;

        this.usage = usage;
        this.useCount = 0;
    }

    public final String getId() {
        return id;
    }

    public final String getName(SignMessage signMessage) {
        return signMessage.get(nameKey);
    }

    public final String getPermission() {
        return permission;
    }

    public final boolean modifiesWorld() {
        return modifiesWorld;
    }

    public final boolean requiresPreSpecialHandling() {
        return preSpecialHandling;
    }

    public final ToolUsage getUsage() {
        return usage;
    }

    public final void setUsage(ToolUsage usage) {
        this.usage = usage;
    }

    public abstract void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign);

    protected static void runAfterLineValidation(VersionAdapter adapter, SignMessage signMessage, Player player,
                                                 Block clickedSign, BiConsumer<Sign, Integer> postAction) {
        SignData signData = adapter.getSignData(clickedSign);
        Vector intersection = MathUtil.getSightSignIntersection(player, clickedSign.getLocation(), signData);
        if (intersection == null) {
            player.sendMessage(signMessage.get(NameKey.INVALID_LINE));
            return;
        }
        int clickedLine = MathUtil.getSignLine(intersection, clickedSign.getLocation(), signData);
        Sign sign = (Sign) clickedSign.getState();
        postAction.accept(sign, clickedLine);
    }
}
