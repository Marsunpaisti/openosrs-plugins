package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class Keyboard
{
    @Inject
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static Client client = PUtils.getClient();

    public static void typeString(String string)
    {
        if (string == null) {
            log.error("Null string given to typeKeys!");
            return;
        }
        executorService.submit(() ->
        {
            for (char c : string.toCharArray())
            {
                pressKey(c);
            }
        });
    }

    public static void pressSpacebar(){
        executorService.submit(() ->
        {
            pressKey(KeyEvent.VK_SPACE);
        });
    }

    public static void typeKeys(char ...keys) {
        if (keys == null) {
            log.error("Null keys given to typeKeys!");
            return;
        }
        executorService.submit(() ->
        {
            for (char c : keys)
            {
                log.info(c + " pressed!");
                pressKey(c);
            }
        });
    }

    private static void pressKey(char key)
    {
        keyEvent(401, key);
        keyEvent(402, key);
        keyEvent(400, key);
    }

    private static void pressKey(int key)
    {
        keyEvent(401, key);
        keyEvent(402, key);
        //keyEvent(400, key);
    }

    private static void keyEvent(int id, char key)
    {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, KeyEvent.VK_UNDEFINED, key
        );
        client.getCanvas().dispatchEvent(e);
    }

    private static void keyEvent(int id, int key)
    {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );
        client.getCanvas().dispatchEvent(e);
    }
}
