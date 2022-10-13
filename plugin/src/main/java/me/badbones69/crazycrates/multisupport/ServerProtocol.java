package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.api.CrazyManager;

/**
 * @Author Badbones69
 */
public enum ServerProtocol {

    TOO_OLD(1),
    v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
    v1_9_R1(191), v1_9_R2(192),
    v1_10_R1(1101),
    v1_11_R1(1111),
    v1_12_R1(1121),
    v1_13_R2(1132),
    v1_14_R1(1141),
    v1_15_R1(1151),
    v1_16_R1(1161), v1_16_R2(1162),v1_16_R3(1163),
    v1_17_R1(1171),
    v1_18_R2(1182),
    TOO_NEW(-2);

    private static ServerProtocol currentProtocol;
    private static ServerProtocol latest;

    private final int versionProtocol;

    private static final CrazyManager crazyManager = CrazyManager.getInstance();

    ServerProtocol(int versionProtocol) {
        this.versionProtocol = versionProtocol;
    }

    public static ServerProtocol getCurrentProtocol() {

        String serVer = crazyManager.getPlugin().getServer().getClass().getPackage().getName();

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
        return isOlder(ServerProtocol.v1_13_R2);
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

    public static boolean isAtLeast(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        int proto = currentProtocol.versionProtocol;
        return proto >= protocol.versionProtocol || proto == -2;
    }

    public static boolean isNewer(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        return currentProtocol.versionProtocol > protocol.versionProtocol || currentProtocol.versionProtocol == -2;
    }

    public static boolean isSame(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
        return currentProtocol.versionProtocol == protocol.versionProtocol;
    }

    public static boolean isOlder(ServerProtocol protocol) {
        if (currentProtocol == null) getCurrentProtocol();
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