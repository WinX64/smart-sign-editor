/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2016
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.winx64.sse.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import io.github.winx64.sse.handler.versions.VersionHandler_1_10_R1;
import io.github.winx64.sse.handler.versions.VersionHandler_1_7_R1;
import io.github.winx64.sse.handler.versions.VersionHandler_1_7_R2;
import io.github.winx64.sse.handler.versions.VersionHandler_1_7_R3;
import io.github.winx64.sse.handler.versions.VersionHandler_1_7_R4;
import io.github.winx64.sse.handler.versions.VersionHandler_1_8_R1;
import io.github.winx64.sse.handler.versions.VersionHandler_1_8_R2;
import io.github.winx64.sse.handler.versions.VersionHandler_1_8_R3;
import io.github.winx64.sse.handler.versions.VersionHandler_1_9_R1;
import io.github.winx64.sse.handler.versions.VersionHandler_1_9_R2;

public abstract class VersionHandler {

    private static final String NMS = "net.minecraft.server.";

    /**
     * Older client version that have no support for the plugin's mechanisms
     */
    private static final List<String> UNSUPPORTED_VERSIONS = Arrays.asList("v1_4", "v1_5", "v1_6");

    /**
     * Official supported versions by the current version of plugin
     */
    private static final Map<String, Class<? extends VersionHandler>> SUPPORTED_VERSIONS = new LinkedHashMap<String, Class<? extends VersionHandler>>();

    static {
	SUPPORTED_VERSIONS.put("v1_7_R1", VersionHandler_1_7_R1.class);
	SUPPORTED_VERSIONS.put("v1_7_R2", VersionHandler_1_7_R2.class);
	SUPPORTED_VERSIONS.put("v1_7_R3", VersionHandler_1_7_R3.class);
	SUPPORTED_VERSIONS.put("v1_7_R4", VersionHandler_1_7_R4.class);
	SUPPORTED_VERSIONS.put("v1_8_R1", VersionHandler_1_8_R1.class);
	SUPPORTED_VERSIONS.put("v1_8_R2", VersionHandler_1_8_R2.class);
	SUPPORTED_VERSIONS.put("v1_8_R3", VersionHandler_1_8_R3.class);
	SUPPORTED_VERSIONS.put("v1_9_R1", VersionHandler_1_9_R1.class);
	SUPPORTED_VERSIONS.put("v1_9_R2", VersionHandler_1_9_R2.class);
	SUPPORTED_VERSIONS.put("v1_10_R1", VersionHandler_1_10_R1.class);
    }

    /**
     * Checks if this version will receive support anytime soon
     * 
     * @param version
     *            The server package version
     * @return Whether the specified version will ever receive support or not
     */
    public static boolean isVersionUnsupported(String version) {
	for (String unsupportedVersion : UNSUPPORTED_VERSIONS) {
	    if (version.startsWith(unsupportedVersion)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Selects the correct version handler to handle the minecraft internals
     * 
     * @return The hooked version handler, or null if the current version is not
     *         supported
     */
    public static VersionHandler hookInternally() {
	for (Entry<String, Class<? extends VersionHandler>> entry : SUPPORTED_VERSIONS.entrySet()) {
	    try {
		if (Package.getPackage(NMS + entry.getKey()) == null) {
		    continue;
		}
		VersionHandler signHandler = entry.getValue().newInstance();
		return signHandler;
	    } catch (Exception e) {
		continue;
	    }
	}
	return null;
    }

    /**
     * Updates a the text of a sign for a specific player
     * 
     * @param player
     *            The player
     * @param sign
     *            The sign
     * @param text
     *            The new lines of text
     */
    public abstract void updateSignText(Player player, Sign sign, String[] text);

    /**
     * Set the player as the one editing the sign and opens the sign editor for
     * them
     * 
     * @param player
     *            The player
     * @param sign
     *            The sign
     */
    public abstract void openSignEditor(Player player, Sign sign);
    
    /**
     * Checks if the specified sign is currently being edited by a player or not
     * @param sign The sign
     * @return Whether the sign is being edited or not
     */
    public abstract boolean isSignBeingEdited(Sign sign);

    /**
     * Older builds don't have the new method, and newer builds don't have the
     * old method. This one should work for all of them.
     * 
     * @return The online players
     */
    public abstract Collection<? extends Player> getOnlinePlayers();
}
