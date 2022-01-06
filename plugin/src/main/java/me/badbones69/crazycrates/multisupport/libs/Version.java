package me.badbones69.crazycrates.multisupport.libs;

import me.badbones69.crazycrates.api.CrazyManager;

public enum Version {
    
    TOO_OLD(-1),
    v1_18_R1(1181),
    TOO_NEW(-2);
    
    private static Version currentVersion;
    private static Version latest;
    private final int versionInteger;
    
    Version(int versionInteger) {
        this.versionInteger = versionInteger;
    }
    
    /**
     * @return Get the server's Minecraft version.
     */
    public static Version getCurrentVersion() {
        if (currentVersion == null) {
            String ver = CrazyManager.getJavaPlugin().getServer().getClass().getPackage().getName();
            int v = Integer.parseInt(ver.substring(ver.lastIndexOf('.') + 1).replace("_", "").replace("R", "").replace("v", ""));
            for (Version version : values()) {
                if (version.getVersionInteger() == v) {
                    currentVersion = version;
                    break;
                }
            }
            if (v > Version.getLatestVersion().getVersionInteger()) {
                currentVersion = Version.getLatestVersion();
            }
            if (currentVersion == null) {
                currentVersion = Version.TOO_NEW;
            }
        }
        return currentVersion;
    }
    
    /**
     * Get the latest version allowed by the Version class.
     * @return The latest version.
     */
    public static Version getLatestVersion() {
        if (latest == null) {
            Version v = Version.TOO_OLD;
            for (Version version : values()) {
                if (version.comparedTo(v) == 1) {
                    v = version;
                }
            }
            return v;
        } else {
            return latest;
        }
    }
    
    /**
     *
     * @return The server's minecraft version as an integer.
     */
    public int getVersionInteger() {
        return this.versionInteger;
    }
    
    /**
     * This checks if the current version is older, newer, or is the checked version.
     * @param version The version you are checking.
     * @return -1 if older, 0 if the same, and 1 if newer.
     */
    public int comparedTo(Version version) {
        int result = -1;
        int current = this.getVersionInteger();
        int check = version.getVersionInteger();
        if (current > check || check == -2) {// check is newer then current
            result = 1;
        } else if (current == check) {// check is the same as current
            result = 0;
        } else if (check == -1) {// check is older then current
            result = -1;
        }
        return result;
    }
    
    /**
     * Checks to see if the current version is newer then the checked version.
     * @param version The version you are checking.
     * @return True if newer then the checked version and false if the same or older.
     */
    public static boolean isNewer(Version version) {
        if (currentVersion == null) getCurrentVersion();
        return currentVersion.versionInteger > version.versionInteger || currentVersion.versionInteger == -2;
    }
    
    /**
     * Checks to see if the current version is the same as the checked version.
     * @param version The version you are checking.
     * @return True if both the current and checked version is the same and false if otherwise.
     */
    public static boolean isSame(Version version) {
        if (currentVersion == null) getCurrentVersion();
        return currentVersion.versionInteger == version.versionInteger;
    }
    
    /**
     * Checks to see if the current version is older than the checked version.
     * @param version The version you are checking.
     * @return True if older than the checked version and false if the same or newer.
     */
    public static boolean isOlder(Version version) {
        if (currentVersion == null) getCurrentVersion();
        return currentVersion.versionInteger < version.versionInteger || currentVersion.versionInteger == -1;
    }
}