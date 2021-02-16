package net.runelite.client.plugins.paistisuite.api.WebWalker.shared;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;

import java.util.*;

@Slf4j
public class InterfaceHelper {
    private static Client client = PUtils.getClient();

    public static List<RSInterface> getAllChildren(WidgetInfo widgetInfo) {
        return PUtils.clientOnly(() -> {
            ArrayList<RSInterface> interfaces = new ArrayList<>();
            Queue<Widget> nestedSearch = new LinkedList<Widget>();
            Widget master = PUtils.getClient().getWidget(widgetInfo);
            if (master == null) return interfaces;
            nestedSearch.add(master);
            while (nestedSearch.size() > 0){
                master = nestedSearch.poll();
                if (master.getChildren() != null){
                    Arrays.stream(master.getChildren()).map(RSInterface::new).forEach(interfaces::add);
                    nestedSearch.addAll(Arrays.asList(master.getChildren()));
                }
                Arrays.stream(master.getDynamicChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getDynamicChildren()));
                Arrays.stream(master.getStaticChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getStaticChildren()));
                Arrays.stream(master.getNestedChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getNestedChildren()));
            }

            return interfaces;
        }, "getAllChildren");
    }

    public static List<RSInterface> getAllChildren(int id) {
        return getAllChildren(id, 0);
    }

    public static List<RSInterface> getAllChildren(int id, int child) {
        return PUtils.clientOnly(() -> {
            ArrayList<RSInterface> interfaces = new ArrayList<>();
            Queue<Widget> nestedSearch = new LinkedList<Widget>();
            Widget master = PWidgets.get(id, child);
            if (master == null) return interfaces;
            nestedSearch.add(master);
            while (nestedSearch.size() > 0){
                master = nestedSearch.poll();
                if (master.getChildren() != null){
                    Arrays.stream(master.getChildren()).map(RSInterface::new).forEach(interfaces::add);
                    nestedSearch.addAll(Arrays.asList(master.getChildren()));
                }
                Arrays.stream(master.getDynamicChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getDynamicChildren()));
                Arrays.stream(master.getStaticChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getStaticChildren()));
                Arrays.stream(master.getNestedChildren()).map(RSInterface::new).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getNestedChildren()));
            }
            return interfaces;
        }, "getAllChildren");
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