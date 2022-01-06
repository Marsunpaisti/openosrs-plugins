package net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.magic;


import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;

import java.util.Arrays;

public class SpellBook {

    private static final int SPELLBOOK_VARBIT = 4070;

    public enum Type {
        STANDARD(0),
        ANCIENT(1),
        LUNAR(2),
        ARCEUUS(3);

        private final int varbit;

        Type(int varbit) {
            this.varbit = varbit;
        }

        public boolean isInUse() {
            RSVarBit varBit = RSVarBit.get(SPELLBOOK_VARBIT);

            return varBit.getValue() == varbit;
        }
    }

    private static Type currentSpellBook = null;

    public static void resetCurrenSpellBook() {
        currentSpellBook = null;
    }

    private static void checkCurrentSpellBook(){
        currentSpellBook = Arrays.stream(Type.values()).filter(Type::isInUse).findAny().orElse(null);
    }

    public static Type getCurrentSpellBook() {
        if (currentSpellBook == null) {
            checkCurrentSpellBook();
        }
        return currentSpellBook;
    }
}