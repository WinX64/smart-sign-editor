package io.github.winx64.sse.tool;

import org.bukkit.block.Sign;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.SmartPlayer;

public abstract class Tool {

    protected final SmartSignEditor plugin;
    protected final ToolType type;

    protected final String primaryName;
    protected final String secondaryName;

    protected final String primaryPermission;
    protected final String secondaryPermission;

    protected ToolUsage primaryUsage;
    protected ToolUsage secondaryUsage;

    public Tool(SmartSignEditor plugin, ToolType type, String primaryName, String secondaryName,
	    String primaryPermission, String secondaryPermission) {
	this.plugin = plugin;
	this.type = type;

	this.primaryName = primaryName;
	this.secondaryName = secondaryName;

	this.primaryPermission = primaryPermission;
	this.secondaryPermission = secondaryPermission;

	this.primaryUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
	this.secondaryUsage = ToolUsage.SHIFT_RIGHT_CLICK;
    }

    public final ToolType getType() {
	return type;
    }

    public final ToolUsage getPrimaryUsage() {
	return primaryUsage;
    }

    public final ToolUsage getSecondaryUsage() {
	return secondaryUsage;
    }

    public final void setPrimaryUsage(ToolUsage primaryUsage) {
	this.primaryUsage = primaryUsage;
    }

    public final void setSecondaryUsage(ToolUsage secondaryUsage) {
	this.secondaryUsage = secondaryUsage;
    }

    public final String getPrimaryName() {
	return primaryName;
    }

    public final String getSecondaryName() {
	return secondaryName;
    }

    public final String getPrimaryPermission() {
	return primaryPermission;
    }

    public final String getSecondaryPermission() {
	return secondaryPermission;
    }

    public final boolean matchesUsage(ToolUsage usage) {
	return primaryUsage.matchesWith(usage) || secondaryUsage.matchesWith(usage);
    }

    public abstract void usePrimary(SmartPlayer sPlayer, Sign sign);

    public abstract void useSecondary(SmartPlayer sPlayer, Sign sign);

    public abstract boolean preSpecialHandling();
}
