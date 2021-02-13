package net.runelite.client.plugins.paistisuite.api;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.plugins.paistisuite.PaistiSuite;

import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Singleton
public class PUtils {
    /*
    private static PUtils instance;
    public static PUtils getInstance(){
        if (instance == null) instance = new PUtils();
        return instance;
    }*/

    public static Client getClient() {
        return PaistiSuite.getInstance().client;
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
        return (long) clamp(Math.round(ThreadLocalRandom.current().nextGaussian() * deviation + mean), min, max);
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
        sleepNormal(min, max, (max-min)/6d, (max-min)/2d);
    }

    public static void sleepNormal(int min, int max, double deviation, double mean){
        sleep((int)PUtils.randomNormal(min, max, deviation, mean));
    }


    public static void sendGameMessage(String message){
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
}
