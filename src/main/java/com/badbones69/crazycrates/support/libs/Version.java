package com.badbones69.crazycrates.support.libs;

import org.bukkit.Bukkit;
import java.util.Arrays;

/**
 * @Author Ome_R
 */
public enum Version {

    v1_8(18),
    v1_9(19),
    v1_10(110),
    v1_11(111),
    v1_12(112),
    v1_13(113),
    v1_14(114),
    v1_15(115),
    v1_16(116),
    v1_17(117),
    v1_18(118),
    v1_19(119),

    UNKNOWN(-1);

    private static final Version currentVersion;
    private static final String bukkitVersion;
    private static final boolean legacy;

    static {
        bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        String[] sections = version.split("_");
        currentVersion = Version.getSafe(sections[0] + "_" + sections[1]);
        legacy = isLessThan(Version.v1_13);
    }

    private final int code;

    Version(int code) {
        this.code = code;
    }

    public static Version getSafe(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException error) {
            return UNKNOWN;
        }
    }

    public static boolean isAtLeast(Version version) {
        return isValidVersion(version) && currentVersion.code >= version.code;
    }

    public static boolean isLessThan(Version version) {
        return isValidVersion(version) && currentVersion.code < version.code;
    }

    public static boolean isEquals(Version version) {
        return isValidVersion(version) && currentVersion.code == version.code;
    }

    public static boolean isLegacy() {
        return legacy;
    }

    public static String getBukkitVersion() {
        return bukkitVersion;
    }

    public static Version[] getByOrder() {
        Version[] versions = Arrays.copyOfRange(values(), 0, currentVersion.ordinal() + 1);

        for (int i = 0; i < versions.length / 2; i++) {
            Version temp = versions[i];
            versions[i] = versions[versions.length - i - 1];
            versions[versions.length - i - 1] = temp;
        }

        return versions;
    }

    private static boolean isValidVersion(Version compareVersion) {
        return currentVersion != UNKNOWN && compareVersion != UNKNOWN;
    }
}