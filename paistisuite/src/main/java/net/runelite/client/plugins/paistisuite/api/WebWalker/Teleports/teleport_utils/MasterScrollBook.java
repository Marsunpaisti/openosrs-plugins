package net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.teleport_utils;

import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.helpers.RSItemHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.NPCInteraction;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSVarBit;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;

import java.util.HashMap;
import java.util.Map;

public class MasterScrollBook {

    public static final int
            INTERFACE_MASTER = 597, DEFAULT_VARBIT = 5685,
            SELECT_OPTION_MASTER = 219, SELECT_OPTION_CHILD = 1,
            GAMETABS_INTERFACE_MASTER = 161;
    private static Map<String, Integer> cache = new HashMap<String, Integer>();

    public enum Teleports {
        NARDAH(5672,"Nardah", TeleportScrolls.NARDAH.getLocation()),
        DIGSITE(5673,"Digsite", TeleportScrolls.DIGSITE.getLocation()),
        FELDIP_HILLS(5674,"Feldip Hills", TeleportScrolls.FELDIP_HILLS.getLocation()),
        LUNAR_ISLE(5675,"Lunar Isle", TeleportScrolls.LUNAR_ISLE.getLocation()),
        MORTTON(5676,"Mort'ton", TeleportScrolls.MORTTON.getLocation()),
        PEST_CONTROL(5677,"Pest Control", TeleportScrolls.PEST_CONTROL.getLocation()),
        PISCATORIS(5678,"Piscatoris", TeleportScrolls.PISCATORIS.getLocation()),
        TAI_BWO_WANNAI(5679,"Tai Bwo Wannai", TeleportScrolls.TAI_BWO_WANNAI.getLocation()),
        ELF_CAMP(5680,"Elf Camp", TeleportScrolls.ELF_CAMP.getLocation()),
        MOS_LE_HARMLESS(5681,"Mos Le'Harmless", TeleportScrolls.MOS_LE_HARMLESS.getLocation()),
        LUMBERYARD(5682,"Lumberyard", TeleportScrolls.LUMBERYARD.getLocation()),
        ZULANDRA(5683,"Zul-Andra", TeleportScrolls.ZULANDRA.getLocation()),
        KEY_MASTER(5684,"Key Master", TeleportScrolls.KEY_MASTER.getLocation()),
        REVENANT_CAVES(6056,"Revenant cave", TeleportScrolls.REVENANT_CAVES.getLocation()),
        WATSON(8253, "Watson", TeleportScrolls.WATSON.getLocation());

        private int varbit;
        private String name;
        private RSTile destination;
        Teleports(int varbit, String name, RSTile destination){
            this.varbit = varbit;
            this.name = name;
            this.destination = destination;
        }

        //Returns the number of scrolls stored in the book.
        public int getCount(){
            RSVarBit var = RSVarBit.get(varbit);
            return var != null ? var.getValue() : 0;
        }

        //Returns the name of the teleport.
        public String getName(){
            return name;
        }

        //Returns the destination that the teleport will take you to.
        public RSTile getDestination(){
            return destination;
        }

        //Sets the teleport as the default left-click option of the book.
        public boolean setAsDefault(){
            if(NPCInteraction.isConversationWindowUp()){
                String text = getDefaultTeleportText();
                if(text.contains(this.getName())){
                    NPCInteraction.handleConversation("Yes");
                    return true;
                }
            }
            if(!isOpen()){
                openBook();
            }
            RSInterface target = getInterface(this);
            if(target == null)
                return false;
            if (PInteraction.widget(target.getWidget(),"Set as default") && waitForOptions()){
                NPCInteraction.handleConversation("Yes");
                return true;
            }
            return false;
        }

        //Uses the teleport and waits until you arrive at the destination.
        public boolean use(){
            if(this == getDefault()){
                PItem book = getBook();
                return book != null && RSItemHelper.click(book,"Teleport") && waitTillAtDestination(this);
            }
            if(this == REVENANT_CAVES) // bug where you can't activate it from the interface for whatever reason.
                return setAsDefault() && use();
            if(!isOpen() && !openBook())
                return false;
            RSInterface target = getInterface(this);
            return target != null && PInteraction.widget(target.getWidget(), "Activate") && waitTillAtDestination(this);
        }

    }

    public static boolean teleport(Teleports teleport){
        return teleport != null && teleport.getCount() > 0 && teleport.use();
    }

    public static int getCount(Teleports teleport){
        return teleport != null ? teleport.getCount() : 0;
    }

    public static boolean isDefault(Teleports teleport){
        return getDefault() == teleport;
    }

    public static boolean setAsDefault(Teleports teleport){
        return teleport != null && teleport.setAsDefault();
    }

    public static Teleports getDefault(){
        RSVarBit defaultTeleport = RSVarBit.get(DEFAULT_VARBIT);
        int value;
        if(defaultTeleport == null || (value = defaultTeleport.getValue()) == 0)
            return null;
        return Teleports.values()[value-1];
    }

    //Removes the default left click teleport option.
    public static boolean removeDefault(){
        PItem book = getBook();
        if (book != null && RSItemHelper.click(book,"Remove default") && waitForOptions()){
            NPCInteraction.handleConversation("Yes");
            return true;
        }
        return false;
    }

    //Caches the index and returns the RSInterface associated with the selected teleport.
    private static RSInterface getInterface(Teleports teleport){
        if(cache.containsKey(teleport.getName())){
            return new RSInterface(PWidgets.get(INTERFACE_MASTER,cache.get(teleport.getName())));
        }
        RSInterface master = new RSInterface(PWidgets.get(INTERFACE_MASTER, 0));
        if(master == null)
            return null;
        for(RSInterface child:master.getChildren()){
            String name = child.getText();
            if(name == null){
                continue;
            } else if(name.startsWith("<") && name.contains(teleport.getName())){
                cache.put(teleport.getName(), child.getIndex());
                return child;
            }
        }
        return null;
    }

    //Returns true if the Master scroll book interface is open.
    public static boolean isOpen(){
        return PWidgets.isSubstantiated(INTERFACE_MASTER);
    }

    //Opens the master scroll book interface.
    public static boolean openBook(){
        PItem book = getBook();
        return book != null && PInteraction.item(book,"Open") && waitForBookToOpen();
    }


    public static boolean hasBook(){
        return has();
    }

    public static boolean has(){
        return getBook() != null;
    }

    private static PItem getBook(){
        return PInventory.findItem(Filters.Items.nameEquals("Master scroll book"));
    }

    private static boolean waitForBookToOpen(){
        return WaitFor.condition(5000, () -> isOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean waitForOptions(){
        return WaitFor.condition(5000, () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    //Checks which scroll we are setting to default currently.
    private static String getDefaultTeleportText(){
        RSInterface master = new RSInterface(PWidgets.get(SELECT_OPTION_MASTER,SELECT_OPTION_CHILD));
        if(master == null)
            return null;
        RSInterface[] ifaces = master.getChildren();
        if(ifaces == null)
            return null;
        for(RSInterface iface:ifaces){
            String txt = iface.getText();
            if(txt == null || !txt.startsWith("Set"))
                continue;
            return txt;
        }
        return null;
    }

    private static boolean waitTillAtDestination(Teleports location){
        return WaitFor.condition(8000, () ->  location.getDestination().toWorldPoint().distanceTo(PPlayer.location()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }
}