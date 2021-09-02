package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWidgets;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class NPCInteraction {
    public static String[] GENERAL_RESPONSES = {"Sorry, I'm a bit busy.", "OK then.", "Yes.", "Okay..."};

    private static final WidgetInfo[] ALL_WINDOWS = {
            WidgetInfo.DIALOG_OPTION_OPTION1,
            WidgetInfo.DIALOG_NPC_HEAD_MODEL,
            WidgetInfo.DIALOG_PLAYER,
            WidgetInfo.DIALOG_SPRITE,
            WidgetInfo.DIALOG_NOTIFICATION_CONTINUE
    };


    private static NPCInteraction instance;

    private NPCInteraction(){

    }

    private static NPCInteraction getInstance(){
        return instance != null ? instance : (instance = new NPCInteraction());
    }

    /**
     *
     * @param rsnpcFilter
     * @param talkOptions
     * @param replyAnswers
     * @return
     */
    public static boolean talkTo(Predicate<NPC> rsnpcFilter, String[] talkOptions, String[] replyAnswers) {
        if (!clickNpcAndWaitChat(rsnpcFilter, talkOptions)){
            return false;
        }
        handleConversation(replyAnswers);
        return true;
    }

    /**
     *
     * @param rsnpcFilter
     * @param options
     * @return
     */
    public static boolean clickNpcAndWaitChat(Predicate<NPC> rsnpcFilter, String... options) {
        return clickNpc(rsnpcFilter, options) && waitForConversationWindow();
    }

    public static boolean clickNpc(Predicate<NPC> rsnpcFilter, String... options) {
        NPC npc = new NPCQuery().filter(rsnpcFilter).result(PUtils.getClient()).nearestTo(PPlayer.get());
        if (npc == null) {
            log.info("Cannot find NPC.");
            return false;
        }

        return InteractionHelper.click(npc, options);
    }

    public static boolean waitForConversationWindow(){
        Player player = PPlayer.get();
        Actor rsCharacter = null;

        if (player != null){
            rsCharacter = player.getInteracting();
        }
        return WaitFor.condition(rsCharacter != null ? WaitFor.getMovementRandomSleep(rsCharacter.getWorldLocation()) : 10000, () -> {
            if (isConversationWindowUp()) {
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

    public static boolean isConversationWindowUp(){
        return Arrays.stream(ALL_WINDOWS).anyMatch(PWidgets::isSubstantiated);
    };

    public static void handleConversationRegex(String regex){
        log.info("Handling regex... " + regex);
        while (true){
            if (WaitFor.condition(PUtils.random(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                continue;
            }

            List<RSInterface> selectableOptions = getAllOptions(regex);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(100);
                continue;
            }

            WaitFor.milliseconds((int)PUtils.randomNormal(350, 2250, 350, 775));
            log.info("Replying with option: " + selectableOptions.get(0).getText());
            Keyboard.typeString(selectableOptions.get(0).getIndex() + "");
            waitForNextOption();
        }
    }

    public static void handleConversation(String... options){
        log.info("Handling... " + Arrays.asList(options));
        List<String> blackList = new ArrayList<>();
        int limit = 0;
        while (limit++ < 50){
            if (WaitFor.condition(PUtils.random(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                log.info("Conversation window not up.");
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                limit = 0;
                continue;
            }

            List<RSInterface> selectableOptions = getAllOptions(options);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(150);
                continue;
            }

            for (RSInterface selected : selectableOptions){
                if(blackList.contains(selected.getText())){
                    continue;
                }
                WaitFor.milliseconds((int)PUtils.randomNormal(350, 2250, 350, 775));
                log.info("Replying with option: " + selected.getText());
                blackList.add(selected.getText());
                Keyboard.typeString(selected.getIndex() + "");
                waitForNextOption();
                limit = 0;
                break;
            }
            WaitFor.milliseconds(20,40);
        }
        if(limit > 50){
            log.info("Reached conversation limit.");
        }
    }

    /**
     *
     * @return Click here to continue conversation interface
     */
    private static RSInterface getClickHereToContinue(){
        return PUtils.clientOnly(() -> {
            List<RSInterface> list = getSelectableOptions();
            if (list == null){
                return null;
            }
            Optional<RSInterface> optional = list.stream().filter(Objects::nonNull).filter(rsInterface -> rsInterface.getText().equals("Click here to continue")).findAny();
            return optional.orElse(null);
        }, "getClickHereToContinue");
    }

    /**
     * Presses space bar
     */
    private static void clickHereToContinue(){
        log.info("Clicking continue.");
        Keyboard.pressSpacebar();
        PUtils.sleepNormal(100, 600);
    }

    /**
     * Waits for chat conversation text change.
     */
    private static void waitForNextOption(){
        List<String> interfaces = getAllChatInterfaces().stream().map(RSInterface::getText).collect(Collectors.toList());
        WaitFor.condition(5000, () -> {
            if (!interfaces.equals(getAllChatInterfaces().stream().map(RSInterface::getText).collect(Collectors.toList()))){
                return WaitFor.Return.SUCCESS;
            }
            if (!isConversationWindowUp()){
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        });
    }

    /**
     *
     * @return List of all reply-able interfaces that has valid text.
     */
    private static List<RSInterface> getSelectableOptions(){
        return PUtils.clientOnly(() -> {
            List<RSInterface> allInterfaces = new ArrayList<RSInterface>();
            for (WidgetInfo window : ALL_WINDOWS) {
                var interfaces = InterfaceHelper.getAllChildren(window);
                if (interfaces == null) continue;

                for (RSInterface i : interfaces) {
                    allInterfaces.add(i);
                }
            }

            List<RSInterface> details = allInterfaces.stream()
                    .filter(Objects::nonNull)
                    .filter(rsInterfaceChild -> {
                        if (!rsInterfaceChild.getWidget().hasListener()) return false;
                        String text = rsInterfaceChild.getText();
                        return text != null && text.length() > 0;
                    })
                    .collect(Collectors.toList());
            if (details.size() > 0) {
                log.info("Conversation Options: [" + details.stream().filter(Objects::nonNull).map(RSInterface::getText).filter(Objects::nonNull).collect(
                        Collectors.joining(", ")) + "]");
            }
            return details;
        }, "getSelectableOptions");
    }

    /**
     *
     * @return List of all Chat interfaces
     */
    private static List<RSInterface> getAllChatInterfaces(){
        List<RSInterface> interfaces = new ArrayList<RSInterface>();
        for (WidgetInfo window : ALL_WINDOWS) {
            interfaces.addAll(InterfaceHelper.getAllChildren(window));
        }
        return interfaces;
    }

    /**
     *
     * @param regex
     * @return list of conversation clickable options that matches {@code regex}
     */
    private static List<RSInterface> getAllOptions(String regex){
        List<RSInterface> list = getSelectableOptions();
        return list != null ? list.stream().filter(rsInterface -> rsInterface.getText().matches(regex)).collect(
                Collectors.toList()) : null;
    }

    /**
     *
     * @param options
     * @return list of conversation clickable options that is contained in options.
     */
    private static List<RSInterface> getAllOptions(String... options){
        final List<String> optionList = Arrays.stream(options).map(String::toLowerCase).collect(Collectors.toList());
        List<RSInterface> list = getSelectableOptions();
        return list != null ? list.stream().filter(rsInterface -> optionList.contains(rsInterface.getText().trim().toLowerCase())).collect(
                Collectors.toList()) : null;
    }
}