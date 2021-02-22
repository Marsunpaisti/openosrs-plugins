package net.runelite.client.plugins.quester;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class QuestTextReader {
    @Value
    public static class QuestTextPart {
        String text;
        boolean completed;

        @Override
        public String toString(){
            return (completed ? "COMPLETETD: " : "NOT COMPLETED: ") + text;
        }
    }

    public static List<QuestTextPart> getQuestText(String questName){
        if (!openQuestText(questName)) return null;
        Widget parent = PWidgets.get(WidgetInfo.DIARY_QUEST_WIDGET_TEXT);
        Widget[] children = parent.getStaticChildren();
        List<QuestTextPart> questTexts = new ArrayList<QuestTextPart>();
        if (children == null) {
            log.info("Null children!");
            return null;
        }
        for (Widget c : children){
            if (c.getText() == null || c.getText().length() == 0) continue;
            boolean completed = c.getText().contains("<str>");
            String stripped = c.getText().strip().replaceAll("<(.*?)>", "");
            questTexts.add(new QuestTextPart(stripped, completed));
        }

        questTexts.stream().forEach(t -> log.info("" + t));

        Widget xButton = PWidgets.get(119, 180);
        PInteraction.widget(xButton, "Close");
        return questTexts;
    }

    private static boolean openQuestText(String questName){
        Widget questNames = PWidgets.get(WidgetInfo.QUESTLIST_FREE_CONTAINER);
        Widget questNames2 = PWidgets.get(WidgetInfo.QUESTLIST_MEMBERS_CONTAINER);

        if (PWidgets.get(WidgetInfo.DIARY_QUEST_WIDGET_TITLE) != null){
            if (PWidgets.get(WidgetInfo.DIARY_QUEST_WIDGET_TITLE).getText().contains(questName)) {
                return true;
            }
        }

        if (questNames != null && questNames2 != null){
            Widget[] children = questNames.getChildren();
            Widget[] children2 = questNames2.getChildren();
            if (children != null && children2 != null) {
                List<Widget> allChildren = new ArrayList<Widget>();
                Collections.addAll(allChildren, children);
                Collections.addAll(allChildren, children2);
                for (Widget child : allChildren){
                    if (child.getText().contains(questName)){
                        log.info("Found quest name widget");
                        if (PInteraction.widget(child, "Read Journal:")){
                            return PUtils.waitCondition(1900, () -> {
                                if (PWidgets.get(WidgetInfo.DIARY_QUEST_WIDGET_TITLE) != null){
                                    if (PWidgets.get(WidgetInfo.DIARY_QUEST_WIDGET_TITLE).getText().contains(questName)) {
                                        return true;
                                    }
                                }
                                return false;
                            });
                        }
                    }
                }
            }
        }

        return false;
    }

    public enum QuestStage {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public static QuestStage getQuestStage(String questName){
        Widget questNames = PWidgets.get(WidgetInfo.QUESTLIST_FREE_CONTAINER);
        Widget questNames2 = PWidgets.get(WidgetInfo.QUESTLIST_MEMBERS_CONTAINER);

        if (questNames != null && questNames2 != null){
            Widget[] children = questNames.getChildren();
            Widget[] children2 = questNames2.getChildren();
            if (children != null && children2 != null) {
                List<Widget> allChildren = new ArrayList<Widget>();
                Collections.addAll(allChildren, children);
                Collections.addAll(allChildren, children2);
                for (Widget child : allChildren){
                    if (child.getText().contains(questName)){
                        if (child.getTextColor() == 16711680) return QuestStage.NOT_STARTED;
                        if (child.getTextColor() == 16776960) return QuestStage.IN_PROGRESS;;
                        if (child.getTextColor() == 65280) return QuestStage.COMPLETED;
                    }
                }
            }
        }

        return null;
    }
}
