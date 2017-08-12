/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2017
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import io.github.winx64.sse.handler.versions.VersionAdapter_1_10_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_11_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_12_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_7_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_7_R2;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_7_R3;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_7_R4;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_8_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_8_R2;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_8_R3;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_9_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_9_R2;

public final class VersionHandler {

	private static final List<String> UNSUPPORTED_VERSIONS;

	private static final Map<String, Class<? extends VersionAdapter>> SUPPORTED_VERSIONS;

	static {
		Map<String, Class<? extends VersionAdapter>> supportedVersions = new HashMap<>();
		List<String> unsupportedVersions = Arrays.asList("v1_4", "v1_5", "v1_6");

		supportedVersions.put("v1_7_R1", VersionAdapter_1_7_R1.class);
		supportedVersions.put("v1_7_R2", VersionAdapter_1_7_R2.class);
		supportedVersions.put("v1_7_R3", VersionAdapter_1_7_R3.class);
		supportedVersions.put("v1_7_R4", VersionAdapter_1_7_R4.class);
		supportedVersions.put("v1_8_R1", VersionAdapter_1_8_R1.class);
		supportedVersions.put("v1_8_R2", VersionAdapter_1_8_R2.class);
		supportedVersions.put("v1_8_R3", VersionAdapter_1_8_R3.class);
		supportedVersions.put("v1_9_R1", VersionAdapter_1_9_R1.class);
		supportedVersions.put("v1_9_R2", VersionAdapter_1_9_R2.class);
		supportedVersions.put("v1_10_R1", VersionAdapter_1_10_R1.class);
		supportedVersions.put("v1_11_R1", VersionAdapter_1_11_R1.class);
		supportedVersions.put("v1_12_R1", VersionAdapter_1_12_R1.class);

		UNSUPPORTED_VERSIONS = Collections.unmodifiableList(unsupportedVersions);
		SUPPORTED_VERSIONS = Collections.unmodifiableMap(supportedVersions);
	}

	private VersionHandler() {}

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
	public static VersionAdapter getAdapter(String version) {
		Class<? extends VersionAdapter> adapterClass = SUPPORTED_VERSIONS.get(version);
		if (adapterClass != null) {
			try {
				return adapterClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {}
		}
		return null;
	}

	/**
	 * Gets the package version of the implementation
	 * 
	 * @return The version, or null if it's not utilizing the correct format
	 *         post 1.4
	 */
	public static String getVersion() {
		String[] entries = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
		if (entries.length == 4 && entries[3].startsWith("v")) {
			return entries[3];
		}
		return null;
	}
}
