package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

@Slf4j
public class PWidgets {
    public static Boolean isValid(int id) {
        return PUtils.clientOnly(() ->  PUtils.getClient().getWidget(id, 0) != null, "widgetIsValid");
    }

    public static Boolean isValid(int id, int child) {
        return PUtils.clientOnly(() ->  PUtils.getClient().getWidget(id, child) != null, "widgetIsValid");
    }

    public static Boolean isValid(WidgetInfo widgetInfo) {
        return PUtils.clientOnly(() ->  PUtils.getClient().getWidget(widgetInfo) != null, "widgetIsValid");
    }

    public static Boolean isSubstantiated(int id) {
        return PUtils.clientOnly(() -> isValid(id, 0) && !get(id, 0).isHidden(), "widgetIsSubstantiated");
    }

    public static Boolean isSubstantiated(int id, int child) {
        return PUtils.clientOnly(() -> isValid(id, child) && !get(id, child).isHidden(), "widgetIsSubstantiated");
    }

    public static Boolean isSubstantiated(WidgetInfo widgetInfo) {
        return PUtils.clientOnly(() -> isValid(widgetInfo) && !get(widgetInfo).isHidden(), "widgetIsSubstantiated");
    }

    public static Widget get(int id, int child){
        return PUtils.clientOnly(() ->  PUtils.getClient().getWidget(id, child), "getWidget");
    }

    public static Widget get(WidgetInfo widgetInfo){
        return PUtils.clientOnly(() ->  PUtils.getClient().getWidget(widgetInfo), "getWidget");
    }
}
