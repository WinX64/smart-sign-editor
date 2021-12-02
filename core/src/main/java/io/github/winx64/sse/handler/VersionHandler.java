package io.github.winx64.sse.handler;

import io.github.winx64.sse.handler.versions.*;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * Helper class to handle the implementation's adapter and version checking
 *
 * @author WinX64
 */
public final class VersionHandler {

    private static final List<String> UNSUPPORTED_VERSIONS;

    private static final Map<String, Class<? extends VersionAdapter>> SUPPORTED_VERSIONS;

    static {
        Map<String, Class<? extends VersionAdapter>> supportedVersions = new HashMap<>();
        List<String> unsupportedVersions = Arrays.asList("v1_4", "v1_5", "v1_6", "v1_7");

        supportedVersions.put("v1_8_R1", VersionAdapter_1_8_R1.class);
        supportedVersions.put("v1_8_R2", VersionAdapter_1_8_R2.class);
        supportedVersions.put("v1_8_R3", VersionAdapter_1_8_R3.class);
        supportedVersions.put("v1_9_R1", VersionAdapter_1_9_R1.class);
        supportedVersions.put("v1_9_R2", VersionAdapter_1_9_R2.class);
        supportedVersions.put("v1_10_R1", VersionAdapter_1_10_R1.class);
        supportedVersions.put("v1_11_R1", VersionAdapter_1_11_R1.class);
        supportedVersions.put("v1_12_R1", VersionAdapter_1_12_R1.class);
        supportedVersions.put("v1_13_R1", VersionAdapter_1_13_R1.class);
        supportedVersions.put("v1_13_R2", VersionAdapter_1_13_R2.class);
        supportedVersions.put("v1_14_R1", VersionAdapter_1_14_R1.class);
        supportedVersions.put("v1_15_R1", VersionAdapter_1_15_R1.class);
        supportedVersions.put("v1_16_R1", VersionAdapter_1_16_R1.class);
        supportedVersions.put("v1_16_R2", VersionAdapter_1_16_R2.class);
        supportedVersions.put("v1_16_R3", VersionAdapter_1_16_R3.class);
        supportedVersions.put("v1_17_R1", VersionAdapter_1_17_R1.class);
        supportedVersions.put("v1_18_R1", VersionAdapter_1_18_R1.class);

        UNSUPPORTED_VERSIONS = Collections.unmodifiableList(unsupportedVersions);
        SUPPORTED_VERSIONS = Collections.unmodifiableMap(supportedVersions);
    }

    private VersionHandler() {
    }

    /**
     * Checks if this version will receive support any time soon
     *
     * @param version The server package version
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
     * supported
     */
    public static VersionAdapter getAdapter(String version) {
        Class<? extends VersionAdapter> adapterClass = SUPPORTED_VERSIONS.get(version);
        if (adapterClass != null) {
            try {
                return adapterClass.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Gets the package version of the implementation
     *
     * @return The version, or null if it's not utilizing the correct format
     * post 1.4
     */
    public static String getVersion() {
        String[] entries = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if (entries.length == 4 && entries[3].startsWith("v")) {
            return entries[3];
        }
        return null;
    }
}
