package io.github.winx64.sse.tool;

import org.bukkit.block.Sign;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages;
import io.github.winx64.sse.player.SmartPlayer;

public abstract class Tool {

	protected final SmartSignEditor plugin;
	protected final SignMessages signMessages;
	protected final ToolType type;

	protected final String primaryName;
	protected final String secondaryName;

	protected final String primaryPermission;
	protected final String secondaryPermission;

	protected final boolean modifiesWorld;
	protected final boolean preSpecialHandling;

	protected ToolUsage primaryUsage;
	protected ToolUsage secondaryUsage;

	protected int primaryUseCount;
	protected int secondaryUseCount;

	public Tool(SmartSignEditor plugin, ToolType type, String primaryName, String secondaryName,
			String primaryPermission, String secondaryPermission, boolean modifiesWorld, boolean preSpecialHandling) {
		this.plugin = plugin;
		this.signMessages = plugin.getSignMessages();
		this.type = type;

		this.primaryName = primaryName;
		this.secondaryName = secondaryName;

		this.primaryPermission = primaryPermission;
		this.secondaryPermission = secondaryPermission;

		this.modifiesWorld = modifiesWorld;
		this.preSpecialHandling = preSpecialHandling;

		this.primaryUsage = ToolUsage.NO_SHIFT_RIGHT_CLICK;
		this.secondaryUsage = ToolUsage.SHIFT_RIGHT_CLICK;

		this.primaryUseCount = 0;
		this.secondaryUseCount = 0;
	}

	public final ToolType getType() {
		return type;
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

	public final boolean modifiesWorld() {
		return modifiesWorld;
	}

	public final boolean requiresPreSpecialHandling() {
		return preSpecialHandling;
	}

	public final boolean matchesUsage(ToolUsage usage) {
		return primaryUsage.matchesWith(usage) || secondaryUsage.matchesWith(usage);
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

	public final int getPrimaryUseCount() {
		return primaryUseCount;
	}

	public final int getSecondaryUseCount() {
		return secondaryUseCount;
	}

	public final int getTotalUseCount() {
		return primaryUseCount + secondaryUseCount;
	}

	public abstract void usePrimary(SmartPlayer sPlayer, Sign sign);

	public abstract void useSecondary(SmartPlayer sPlayer, Sign sign);
}
