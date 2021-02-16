package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

@Slf4j
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

    public static Integer getVarbitValue(final int varbitId){
        return PUtils.clientOnly(() -> {
            return PUtils.getClient().getVarbitValue(varbitId);
        }, "getVarbitValue");
    }
}
