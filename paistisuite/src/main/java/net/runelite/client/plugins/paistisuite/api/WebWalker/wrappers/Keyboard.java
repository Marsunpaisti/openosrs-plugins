package net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers;

import net.runelite.api.Client;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;

/*
    CREDITS TO Illumine for making his KeyboardUtils
*/

public class Keyboard
{
    @Inject
    private static ExecutorService executorService;

    private static Client client = PUtils.getClient();

    public static void typeString(String string)
    {
        executorService.submit(() ->
        {
            for (char c : string.toCharArray())
            {
                pressKey(c);
            }
        });
    }

    public static void typeKeys(char ...keys) {
        executorService.submit(() ->
        {
            for (char c : keys)
            {
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
