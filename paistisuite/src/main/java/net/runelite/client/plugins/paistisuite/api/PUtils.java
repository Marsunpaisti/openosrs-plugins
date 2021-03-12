package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.geometry.Cuboid;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.framework.ClientFuture;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class PUtils {
    /*
    private static PUtils instance;
    public static PUtils getInstance(){
        if (instance == null) instance = new PUtils();
        return instance;
    }*/

    private static final Polygon NOT_WILDERNESS_BLACK_KNIGHTS = new Polygon( // this is black knights castle
            new int[]{2994, 2995, 2996, 2996, 2994, 2994, 2997, 2998, 2998, 2999, 3000, 3001, 3002, 3003, 3004, 3005, 3005,
                    3005, 3019, 3020, 3022, 3023, 3024, 3025, 3026, 3026, 3027, 3027, 3028, 3028, 3029, 3029, 3030, 3030, 3031,
                    3031, 3032, 3033, 3034, 3035, 3036, 3037, 3037},
            new int[]{3525, 3526, 3527, 3529, 3529, 3534, 3534, 3535, 3536, 3537, 3538, 3539, 3540, 3541, 3542, 3543, 3544,
                    3545, 3545, 3546, 3546, 3545, 3544, 3543, 3543, 3542, 3541, 3540, 3539, 3537, 3536, 3535, 3534, 3533, 3532,
                    3531, 3530, 3529, 3528, 3527, 3526, 3526, 3525},
            43
    );
    private static final Cuboid MAIN_WILDERNESS_CUBOID = new Cuboid(2944, 3525, 0, 3391, 4351, 3);
    private static final Cuboid GOD_WARS_WILDERNESS_CUBOID = new Cuboid(3008, 10112, 0, 3071, 10175, 3);
    private static final Cuboid WILDERNESS_UNDERGROUND_CUBOID = new Cuboid(2944, 9920, 0, 3391, 10879, 3);

    /**
     * Gets the wilderness level based on a world point
     * Java reimplementation of clientscript 384 [proc,wilderness_level]
     *
     * @param point the point in the world to get the wilderness level for
     * @return the int representing the wilderness level
     */
    public static int getWildernessLevelFrom(WorldPoint point)
    {
        if (MAIN_WILDERNESS_CUBOID.contains(point))
        {
            if (NOT_WILDERNESS_BLACK_KNIGHTS.contains(point.getX(), point.getY()))
            {
                return 0;
            }

            return ((point.getY() - 3520) / 8) + 1; // calc(((coordz(coord) - (55 * 64)) / 8) + 1)
        }
        else if (GOD_WARS_WILDERNESS_CUBOID.contains(point))
        {
            return ((point.getY() - 9920) / 8) - 1; // calc(((coordz(coord) - (155 * 64)) / 8) - 1)
        }
        else if (WILDERNESS_UNDERGROUND_CUBOID.contains(point))
        {
            return ((point.getY() - 9920) / 8) + 1; // calc(((coordz(coord) - (155 * 64)) / 8) + 1)
        }
        return 0;
    }

    public static Client getClient() {
        if (PaistiSuite.getInstance() == null) return null;
        return PaistiSuite.getInstance().client;
    }

    public static Boolean logout(){
        log.info("Attempting to log out.");
        return PUtils.clientOnly(() -> {
            int param1 = (PWidgets.get(WidgetInfo.LOGOUT_BUTTON) != null) ? 11927560 : 4522007;
            PUtils.getClient().invokeMenuAction(
                    "",
                    "",
                    1,
                    MenuAction.CC_OP.getId(),
                    -1,
                    param1
            );
            return true;
        }, "logout");
    }

    private static double clamp(double val, int min, int max)
    {
        return Math.max(min, Math.min(max, val));
    }

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static long randomNormal(int min, int max, double deviation, double mean)
    {
        long result;
        int attempts = 0;
        do {
            if (attempts >= 10) {
                log.error("Had to fallback to clamping in randomNormal!");
                log.error("Min: " + min + " Max: " + max + " Dev: " + deviation + " mean: " + mean);
                return Math.round(clamp(ThreadLocalRandom.current().nextGaussian() * deviation + mean, min, max));
            }
            result = Math.round(ThreadLocalRandom.current().nextGaussian() * deviation + mean);
            attempts++;
        } while ( result < min || result > max);
        return result;
    }

    public static long randomNormal(int min, int max)
    {
        return randomNormal(min, max, ((double)max-(double)min)/6d, min + ((double)max-(double)min)/2d);
    }

    public static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            log.error("Interrupted sleep: " + e);
        }
    }

    public static void sleepFlat(int min, int max){
        sleep(PUtils.random(min, max));
    }

    public static void sleepNormal(int min, int max){
        sleep((int) randomNormal(min, max));
    }

    public static void sleepNormal(int min, int max, double deviation, double mean){
        sleep((int)PUtils.randomNormal(min, max, deviation, mean));
    }

    public static boolean waitCondition(int timeout, BooleanSupplier condition){
        long startTime = System.currentTimeMillis();
        while (!condition.getAsBoolean()){
            if (System.currentTimeMillis() - startTime > timeout) return false;
            sleepFlat(40, 70);
        }
        return true;
    }

    public static boolean isMembersWorld(){
        EnumSet<WorldType> types = PUtils.getClient().getWorldType();
        return types.contains(WorldType.MEMBERS);
    }

    public static boolean isClientThread(){
        return PUtils.getClient() != null && PUtils.getClient().isClientThread();
    }

    public static <T> T clientOnly(@NotNull Callable<T> task, String name){
        try {
            if (PUtils.isClientThread()){
                return task.call();
            }

            return PaistiSuite.getInstance().clientExecutor.scheduleAndWait(task, name);
        } catch (Exception e){
            log.error("Exception in " + name);
            e.printStackTrace();
            return null;
        }
    }

    public static void sendGameMessage(String message){
        log.info("Sent game msg: " + message);
        if (PaistiSuite.getInstance().client.isClientThread()){
            PaistiSuite.getInstance().chatMessageManager
                    .queue(QueuedMessage.builder()
                            .type(ChatMessageType.CONSOLE)
                            .runeLiteFormattedMessage(
                                    new ChatMessageBuilder()
                                            .append(ChatColorType.HIGHLIGHT)
                                            .append(message)
                                            .build())
                            .build());
        } else {
            PaistiSuite.getInstance().clientExecutor.schedule(() -> {
                PaistiSuite.getInstance().chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(
                                        new ChatMessageBuilder()
                                                .append(ChatColorType.HIGHLIGHT)
                                                .append(message)
                                                .build())
                                .build());
            }, "sendGameMessage");
        }
    }

    public static String[] parseCommaSeparated(String toParse){
        return Arrays.stream(toParse.strip().split(",")).map(String::strip).toArray(String[]::new);
    }

    public static String[] parseNewlineSeparated(String toParse){
        return Arrays.stream(toParse.strip().split("\n")).map(String::strip).toArray(String[]::new);
    }
}
