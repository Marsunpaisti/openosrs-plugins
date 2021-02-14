package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.paistisuite.api.PInteraction;

import java.util.Arrays;

public class RSInterface {
    Widget widget;

    public RSInterface(Widget w) {
        this.widget = w;
    }

    public String getText() {
        return this.widget.getText();
    }

    public int getIndex() {
        return this.widget.getIndex();
    }

    public int getTextureID(){
        return this.widget.getSpriteId();
    }

    public RSInterface[] getChildren(){
        return Arrays.stream(widget.getChildren()).map(RSInterface::new).toArray(RSInterface[]::new);
    }

    public RSInterface[] getComponents(){
        return Arrays.stream(widget.getChildren()).map(RSInterface::new).toArray(RSInterface[]::new);
    }

    public String[] getActions() {
        return this.widget.getActions();
    }

    public boolean click(String ...options){
        return PInteraction.widget(this.widget, options);
    }

    public Widget getWidget(){
        return this.widget;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RSInterface)) return false;

        return ((RSInterface) o).widget.equals(this.widget);
    }
}
