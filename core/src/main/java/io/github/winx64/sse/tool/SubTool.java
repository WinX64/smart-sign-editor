package io.github.winx64.sse.tool;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.SmartPlayer;
import org.bukkit.block.Sign;

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

    public final NameKey getNameKey() {
        return nameKey;
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

    public final int getUseCount() {
        return useCount;
    }

    public abstract void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign);
}
