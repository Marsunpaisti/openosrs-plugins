package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.Skill;

public class PSkills {

    public static int getActualLevel(Skill skill){
        return PUtils.getClient().getRealSkillLevel(skill);
    }

    public static int getCurrentLevel(Skill skill){
        return PUtils.getClient().getBoostedSkillLevel(skill);
    }

    public static int getXp(Skill skill) {
        return PUtils.getClient().getSkillExperience(skill);
    }
}
