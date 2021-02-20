package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class RSInterface {
    Widget widget;

    public RSInterface(Widget w) {
        this.widget = w;
    }

    public String getText() {
        return PUtils.clientOnly(() -> {
            if (this.widget == null) return null;
            return this.widget.getText();
        }, "RSInterface.getText");
    }

    public Integer getIndex() {
        return PUtils.clientOnly(() -> {
            if (this.widget == null) return null;
            return this.widget.getIndex();
        }, "RSInterface.getIndex");
    }

    public Integer getTextureID(){
        return PUtils.clientOnly(() -> {
            if (this.widget == null) return null;
            return this.widget.getSpriteId();
        }, "RSInterface.getTextureID");
    }

    public RSInterface[] getChildren() {
        return PUtils.clientOnly(() -> {
            if (widget == null) return null;
            if (widget.getChildren() == null) return null;
            return Arrays.stream(widget.getChildren()).filter(Objects::nonNull).map(RSInterface::new).toArray(RSInterface[]::new);
        }, "RSInterface.getChildren");
    }

    public String[] getActions() {
        return PUtils.clientOnly(() -> {
            if (widget == null) return null;
            return widget.getActions();
        }, "RSInterface.getActions");
    }

    public boolean interact(String ...options){
        return PInteraction.widget(this.widget, options);
    }
    public boolean interact(){
        return PInteraction.clickWidget(this.widget);
    }

    public boolean click(){
        return PInteraction.clickWidget(this.widget);
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
