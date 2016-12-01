package io.github.winx64.sse.handler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.winx64.sse.handler.versions.VersionAdapter_1_10_R1;
import io.github.winx64.sse.handler.versions.VersionAdapter_1_11_R1;
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

    private static final String NMS = "net.minecraft.server.";

    private static final List<String> UNSUPPORTED_VERSIONS = Arrays.asList("v1_4", "v1_5", "v1_6");

    private static final Map<String, Class<? extends VersionAdapter>> SUPPORTED_VERSIONS = new LinkedHashMap<String, Class<? extends VersionAdapter>>();

    static {
	SUPPORTED_VERSIONS.put("v1_7_R1", VersionAdapter_1_7_R1.class);
	SUPPORTED_VERSIONS.put("v1_7_R2", VersionAdapter_1_7_R2.class);
	SUPPORTED_VERSIONS.put("v1_7_R3", VersionAdapter_1_7_R3.class);
	SUPPORTED_VERSIONS.put("v1_7_R4", VersionAdapter_1_7_R4.class);
	SUPPORTED_VERSIONS.put("v1_8_R1", VersionAdapter_1_8_R1.class);
	SUPPORTED_VERSIONS.put("v1_8_R2", VersionAdapter_1_8_R2.class);
	SUPPORTED_VERSIONS.put("v1_8_R3", VersionAdapter_1_8_R3.class);
	SUPPORTED_VERSIONS.put("v1_9_R1", VersionAdapter_1_9_R1.class);
	SUPPORTED_VERSIONS.put("v1_9_R2", VersionAdapter_1_9_R2.class);
	SUPPORTED_VERSIONS.put("v1_10_R1", VersionAdapter_1_10_R1.class);
	SUPPORTED_VERSIONS.put("v1_11_R1", VersionAdapter_1_11_R1.class);
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
    public static VersionAdapter registerAdapter() {
	for (Entry<String, Class<? extends VersionAdapter>> entry : SUPPORTED_VERSIONS.entrySet()) {
	    try {
		if (Package.getPackage(NMS + entry.getKey()) == null) {
		    continue;
		}
		VersionAdapter signHandler = entry.getValue().newInstance();
		return signHandler;
	    } catch (Exception e) {
		continue;
	    }
	}
	return null;
    }
}
