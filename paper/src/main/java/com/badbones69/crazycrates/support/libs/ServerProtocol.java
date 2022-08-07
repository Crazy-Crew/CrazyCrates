package com.badbones69.crazycrates.support.libs;

import com.badbones69.crazycrates.api.CrazyManager;

/**
 * @Author Badbones69
 */
public enum ServerProtocol {

    TOO_OLD(-1),
    v1_18_R1(1181),
    v1_18_R2(1182),
    v1_19(1191),
    TOO_NEW(-2);

    private static ServerProtocol currentProtocol;
    private static ServerProtocol latest;

    private final int versionProtocol;

    ServerProtocol(int versionProtocol) {
        this.versionProtocol = versionProtocol;
    }

    public static ServerProtocol getCurrentProtocol(CrazyCrates plugin) {

        String serVer = plugin.getServer().getClass().getPackage().getName();

        int serProt = Integer.parseInt(
                serVer.substring(
                        serVer.lastIndexOf('.') + 1
                ).replace("_", "").replace("R", "").replace("v", "")
        );

        for (ServerProtocol protocol : values()) {
            if (protocol.versionProtocol == serProt) {
                currentProtocol = protocol;
                break;
            }
        }

        if (currentProtocol == null) currentProtocol = ServerProtocol.TOO_NEW;

        return currentProtocol;
    }

    public static boolean isLegacy() {
        return isOlder(ServerProtocol.v1_18_R1);
    }

    public static ServerProtocol getLatestProtocol() {

        if (latest != null) return latest;

        ServerProtocol old = ServerProtocol.TOO_OLD;

        for (ServerProtocol protocol : values()) {
            if (protocol.compare(old) == 1) {
                old = protocol;
            }
        }

        return old;
    }

    public static boolean isAtLeast(ServerProtocol protocol, CrazyCrates plugin) {
        if (currentProtocol == null) getCurrentProtocol(plugin);
        int proto = currentProtocol.versionProtocol;
        return proto >= protocol.versionProtocol || proto == -2;
    }

    public static boolean isNewer(ServerProtocol protocol, CrazyCrates plugin) {
        if (currentProtocol == null) getCurrentProtocol(plugin);
        return currentProtocol.versionProtocol > protocol.versionProtocol || currentProtocol.versionProtocol == -2;
    }

    public static boolean isSame(ServerProtocol protocol, CrazyCrates plugin) {
        if (currentProtocol == null) getCurrentProtocol(plugin);
        return currentProtocol.versionProtocol == protocol.versionProtocol;
    }

    public static boolean isOlder(ServerProtocol protocol, CrazyCrates plugin) {
        if (currentProtocol == null) getCurrentProtocol(plugin);
        int proto = currentProtocol.versionProtocol;
        return proto < protocol.versionProtocol || proto == -1;
    }

    public int compare(ServerProtocol protocol) {
        int result = -1;
        int current = versionProtocol;
        int check = protocol.versionProtocol;

        if (current > check || check == -2) {
            result = 1;
        } else if (current == check) {
            result = 0;
        }

        return result;
    }
}