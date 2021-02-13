package net.runelite.client.plugins.paistisuite.api;

public class PVars {
    public static int[] getSettingArray() {
        int[] settingArray = PUtils.getClient().getVarps();
        if (settingArray == null) {
            return new int[0];
        }
        return settingArray.clone();
    }

    public static int getSetting(final int setting) {
        int[] settings = getSettingArray();
        if (setting < settings.length) {
            return settings[setting];
        }
        return -1;
    }
}
