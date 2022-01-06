package net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic;

import net.runelite.api.Skill;
import net.runelite.client.plugins.paistisuite.api.PSkills;
import net.runelite.client.plugins.paistisuite.api.PVars;

import static net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.Teleport.castSpell;


public enum Spell implements Validatable {

    VARROCK_TELEPORT    (SpellBook.Type.STANDARD, 25, "Varrock Teleport",    new RuneRequirement(1, RuneElement.LAW), new RuneRequirement(3, RuneElement.AIR),     new RuneRequirement(1, RuneElement.FIRE)),
    LUMBRIDGE_TELEPORT  (SpellBook.Type.STANDARD, 31, "Lumbridge Teleport",  new RuneRequirement(1, RuneElement.LAW), new RuneRequirement(3, RuneElement.AIR),     new RuneRequirement(1, RuneElement.EARTH)),
    FALADOR_TELEPORT    (SpellBook.Type.STANDARD, 37, "Falador Teleport",    new RuneRequirement(1, RuneElement.LAW), new RuneRequirement(3, RuneElement.AIR),     new RuneRequirement(1, RuneElement.WATER)),
    CAMELOT_TELEPORT    (SpellBook.Type.STANDARD, 45, "Camelot Teleport",    new RuneRequirement(1, RuneElement.LAW), new RuneRequirement(5, RuneElement.AIR)),
    ARDOUGNE_TELEPORT   (SpellBook.Type.STANDARD, 51, "Ardougne Teleport",   new RuneRequirement(2, RuneElement.LAW), new RuneRequirement(2, RuneElement.WATER)),
    KOUREND_TELEPORT	(SpellBook.Type.STANDARD, 69, "Kourend Castle Teleport",new RuneRequirement(2, RuneElement.LAW), new RuneRequirement(2, RuneElement.SOUL),new RuneRequirement(4, RuneElement.WATER), new RuneRequirement(5, RuneElement.FIRE)),

    ;

    private SpellBook.Type spellBookType;
    private int requiredLevel;
    private String spellName;
    private RuneRequirement[] recipe;

    Spell(SpellBook.Type spellBookType, int level, String spellName, RuneRequirement... recipe){
        this.spellBookType = spellBookType;
        this.requiredLevel = level;
        this.spellName = spellName;
        this.recipe = recipe;
    }

    public RuneRequirement[] getRecipe(){
        return recipe;
    }

    public String getSpellName() {
        return spellName;
    }

    public boolean cast() {
        return canUse() && castSpell(getSpellName(), "Cast");
    }

    @Override
    public boolean canUse(){
        if (SpellBook.getCurrentSpellBook() != spellBookType){
            return false;
        }
        if (requiredLevel > PSkills.getCurrentLevel(Skill.MAGIC)){
            return false;
        }
        if (this == ARDOUGNE_TELEPORT && PVars.getSetting(165) < 30){
            return false;
        }

        for (RuneRequirement pair : recipe){
            int amountRequiredForSpell = pair.getFirst();
            RuneElement runeElement = pair.getSecond();
            if (runeElement.getRuneCount() < amountRequiredForSpell){
                return false;
            }
        }
        return true;
    }

}