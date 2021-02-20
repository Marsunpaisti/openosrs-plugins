package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters;


import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.FairyRing;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;

public enum SecondLetter {
    I(0),
    J(3),
    K(2),
    L(1)
    ;

    public int getValue() {
        return value;
    }

    int value;
    SecondLetter(int value){
        this.value = value;
    }

    public static final int
            VARBIT = 3986,
            CLOCKWISE_CHILD = 21,
            ANTI_CLOCKWISE_CHILD = 22;

    private static int get(){
        return RSVarBit.get(VARBIT).getValue();
    }

    public boolean isSelected(){
        return get() == this.value;
    }

    public boolean turnTo(){
        int current = get();
        int target = getValue();
        if(current == target)
            return true;
        int diff = current - target;
        int abs = Math.abs(diff);
        if(abs == 2){
            return PUtils.random(0,1) == 0 ? turnClockwise(2) : turnAntiClockwise(2);
        } else if(diff == 3 || diff == -1){
            return turnClockwise(1);
        } else {
            return turnAntiClockwise(1);
        }
    }

    public static boolean turnClockwise(int rotations){
        if(rotations == 0)
            return true;
        RSInterface iface = getClockwise();
        final int value = get();
        return iface != null && iface.interact()
                && WaitFor.condition(2500, () -> get() != value ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS
                && turnClockwise(--rotations);
    }

    public static boolean turnAntiClockwise(int rotations){
        if(rotations == 0)
            return true;
        RSInterface iface = getAntiClockwise();
        final int value = get();
        return iface != null && iface.interact()
                && WaitFor.condition(2500, () -> get() != value ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS
                && turnAntiClockwise(--rotations);
    }

    private static RSInterface getClockwise() {
        return new RSInterface(PWidgets.get(FairyRing.INTERFACE_MASTER, CLOCKWISE_CHILD));
    }
    private static RSInterface getAntiClockwise() {
        return new RSInterface(PWidgets.get(FairyRing.INTERFACE_MASTER, ANTI_CLOCKWISE_CHILD));
    }

}