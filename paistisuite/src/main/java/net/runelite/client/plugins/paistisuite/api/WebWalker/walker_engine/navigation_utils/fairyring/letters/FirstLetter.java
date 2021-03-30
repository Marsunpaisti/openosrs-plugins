package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.letters;


import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.fairyring.FairyRing;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;

public enum FirstLetter {
    A(0),
    B(3),
    C(2),
    D(1)
    ;
    int value;
    FirstLetter(int value){
        this.value = value;
    }


    public int getValue(){
        return value;
    }

    public static final int
            VARBIT = 3985,
            CLOCKWISE_CHILD = 19,
            ANTI_CLOCKWISE_CHILD = 20;

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
        Widget w = PWidgets.get(WidgetInfo.FAIRY_RING_LEFT_ORB_CLOCKWISE);
        final int value = get();
        return PInteraction.widget(w, "Rotate clockwise")
                && WaitFor.condition(2500, () -> get() != value ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS
                && turnClockwise(--rotations);
    }

    public static boolean turnAntiClockwise(int rotations){
        if(rotations == 0)
            return true;
        Widget w = PWidgets.get(WidgetInfo.FAIRY_RING_LEFT_ORB_COUNTER_CLOCKWISE);
        final int value = get();
        return PInteraction.widget(w, "Rotate counter-clockwise")
                && WaitFor.condition(2500, () -> get() != value ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS
                && turnAntiClockwise(--rotations);
    }
}