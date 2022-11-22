package io.github.dailystruggle.thethuum.tools;

import io.github.dailystruggle.thethuum.Plugin;

public class Version {
    private static String version = null;
    private static Integer intVersion = null;

    public static String getServerVersion() {
        if(version == null) {
            version = Plugin.getInstance().getServer().getClass().getPackage().getName();
            version = version.replaceAll("[-+^.a-zA-Z]*","");
        }

        return version;
    }

    public static Integer getServerIntVersion() {
        if(intVersion == null) {
            String[] splitVersion = getServerVersion().split("_");
            if(splitVersion.length == 0) {
                intVersion = 0;
            }
            else if (splitVersion.length == 1) {
                intVersion = Integer.valueOf(splitVersion[0]);
            }
            else {
                intVersion = Integer.valueOf(splitVersion[1]);
            }
        }
        return intVersion;
    }
}
