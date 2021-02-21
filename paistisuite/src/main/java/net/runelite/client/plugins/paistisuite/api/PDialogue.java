package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PDialogue {
    private static final WidgetInfo[] DIALOG_WINDOWS = {
            WidgetInfo.DIALOG_OPTION_OPTION1,
            WidgetInfo.DIALOG_NPC,
            WidgetInfo.DIALOG_PLAYER,
            WidgetInfo.DIALOG_SPRITE,
            WidgetInfo.DIALOG_NOTIFICATION_CONTINUE
    };

    /**
     * Goes through NPC dialogue, picking replies in the given order
     * @return false if dialogue isnt open or if an option isnt found, true if all options were selected and dialogue is over
     */
    public static boolean handleDialogueInOrder(String ...choices){
        if (!isConversationWindowUp()) {
            log.info("No conversation window to handle!");
            return false;
        }

        int totalChoices = choices.length;
        int nextChoice = 0;
        while (isConversationWindowUp()){
            List<Widget> options = getDialogueOptions();
            if (options.size() == 0){
                PUtils.sleepNormal(200, 1000, 200, 400);
                continue;
            }
            if (options.size() == 1 && options.get(0).getText().contains("Click here to continue")){
                clickHereToContinue();
                continue;
            }
            if (options.size() > 0){
                if (nextChoice >= totalChoices) {
                    log.info("Handleconversation ran out of options and dialogue is still open!");
                    return false;
                }

                Widget choice = null;
                int choiceButton = 1;
                for (Widget o : options){
                    if (o.getText().matches(choices[nextChoice]) || o.getText().contains(choices[nextChoice])) {
                        choice = o;
                        break;
                    } else {
                        log.info(o.getText() + " doesnt contain " + choices[nextChoice]);
                    }
                    choiceButton++;
                }

                if (choice == null){
                    log.info("Unable to find correct choice for dialogue options: ");
                    options.forEach(o -> log.info(o.getText()));
                    log.info("Looking for choice: ");
                    log.info(choices[nextChoice]);
                    return false;
                } else {
                    log.info("Choosing option: " + choice.getText());
                    nextChoice++;
                    Keyboard.typeString(choiceButton + "");
                    PUtils.sleepNormal(200, 500);
                }
            }
            PUtils.sleepNormal(400, 1500);
        }

        if (nextChoice != choices.length) {
            log.info("Conversation closed without selecting all choices!");
            return false;
        }
        return true;
    }

    /**
     * Presses space bar
     */
    private static void clickHereToContinue(){
        log.info("Clicking continue.");
        Keyboard.pressSpacebar();
        PUtils.sleepNormal(200, 800);
    }


    /**
     *
     * @return List of all reply-able interfaces that has valid text.
     */
    public static List<Widget> getDialogueOptions(){
        return PUtils.clientOnly(() -> {
            List<Widget> allInterfaces = new ArrayList<Widget>();
            for (WidgetInfo window : DIALOG_WINDOWS) {
                var interfaces = getAllChildren(window);
                if (interfaces == null) continue;
                allInterfaces.addAll(interfaces);
            }

            List<Widget> selectables = allInterfaces.stream()
                    .filter(Objects::nonNull)
                    .filter(widget -> {
                        if (!widget.hasListener()) return false;
                        String text = widget.getText();
                        if (text == null || text.length() == 0) return false;
                        if (text.contains("Please wait...")) return false;
                        return true;
                    })
                    .collect(Collectors.toList());
            return selectables;
        }, "getDialogueOptions");
    }

    private static List<Widget> getAllChildren(WidgetInfo widgetInfo) {
        return PUtils.clientOnly(() -> {
            ArrayList<Widget> interfaces = new ArrayList<>();
            Queue<Widget> nestedSearch = new LinkedList<Widget>();
            Widget master = PUtils.getClient().getWidget(widgetInfo);
            if (master == null) return interfaces;
            nestedSearch.add(master);
            while (nestedSearch.size() > 0){
                master = nestedSearch.poll();
                if (master.getChildren() != null){
                    Arrays.stream(master.getChildren()).forEach(interfaces::add);
                    nestedSearch.addAll(Arrays.asList(master.getChildren()));
                }
                Arrays.stream(master.getDynamicChildren()).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getDynamicChildren()));
                Arrays.stream(master.getStaticChildren()).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getStaticChildren()));
                Arrays.stream(master.getNestedChildren()).forEach(interfaces::add);
                nestedSearch.addAll(Arrays.asList(master.getNestedChildren()));
            }

            return interfaces;
        }, "getAllChildren");
    }

    public static boolean isConversationWindowUp(){
        return Arrays.stream(DIALOG_WINDOWS).anyMatch(PWidgets::isSubstantiated);
    };
}
