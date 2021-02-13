package net.runelite.client.plugins.paistisuite.api.WebWalker.shared;

import net.runelite.api.Client;
import net.runelite.api.mixins.Inject;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;

import java.util.*;

public class InterfaceHelper {
    @Inject
    private static Client client;

    /**
     *
     * @param ids
     * @return never null
     */
    public static List<RSInterface> getAllInterfaces(int... ids){
        ArrayList<RSInterface> interfaces = new ArrayList<>();

        for (int id : ids) {
            Queue<RSInterface> queue = new LinkedList<>();
            RSInterface master = new RSInterface(client.getWidget(id, 0));

            if (master == null) {
                return interfaces;
            }

            queue.add(master);
            RSInterface[] components = master.getComponents();
            if (components != null) {
                Collections.addAll(queue, components);
            }

            while (!queue.isEmpty()) {
                RSInterface rsInterface = queue.poll();
                interfaces.add(rsInterface);
                RSInterface[] children = rsInterface.getChildren();
                if (children != null) {
                    Collections.addAll(queue, children);
                }
            }
        }

        return interfaces;
    }

    public static List<RSInterface> getAllInterfaces(RSInterface parent){
        ArrayList<RSInterface> interfaces = new ArrayList<>();
        Queue<RSInterface> queue = new LinkedList<>();

        if (parent == null){
            return interfaces;
        }

        queue.add(parent);
        while (!queue.isEmpty()){
            RSInterface rsInterface = queue.poll();
            interfaces.add(rsInterface);
            RSInterface[] children = rsInterface.getChildren();
            if (children != null) {
                Collections.addAll(queue, children);
            }
        }

        return interfaces;
    }

    public static boolean textEquals(RSInterface rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.equals(match);
    }

    public static boolean textContains(RSInterface rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.contains(match);
    }

    public static boolean textMatches(RSInterface rsInterface, String match){
        if (rsInterface == null){
            return false;
        }
        String text = rsInterface.getText();
        return text != null && text.matches(match);
    }

    public static List<String> getActions(RSInterface rsInterface){
        if (rsInterface == null){
            return Collections.emptyList();
        }
        String[] actions = rsInterface.getActions();
        if (actions == null){
            return Collections.emptyList();
        }
        return Arrays.asList(actions);
    }

}