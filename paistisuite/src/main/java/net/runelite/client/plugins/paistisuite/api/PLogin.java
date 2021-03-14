package net.runelite.client.plugins.paistisuite.api;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;

import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PLogin {
    private final static ExecutorService loginThread = Executors.newSingleThreadExecutor();

    private boolean handlePostLoginScreen() {
        Widget login = PWidgets.get(378, 87);
        if (login == null || !login.getText().equalsIgnoreCase("CLICK HERE TO PLAY")) return false;

        if (login.getBounds() != null && login.getBounds().getX() != -1 && login.getBounds().getY() != -1) {
            loginThread.submit(() -> {
                PMouse.clickShape(login.getBounds());
            });
            return true;
        }

        return false;
    }

    public boolean login(String user, String pass){
        if (PUtils.getClient().getGameState() != GameState.LOGIN_SCREEN) return false;
        if (user == null || pass == null || user.length() == 0 || pass.length() == 0) return false;

        Keyboard.typeKeysInt(KeyEvent.VK_ENTER);
        PUtils.sleepNormal(300, 600);
        PUtils.getClient().setUsername(user);
        PUtils.getClient().setPassword(pass);
        PUtils.sleepNormal(300, 600);
        Keyboard.typeKeysInt(KeyEvent.VK_ENTER);
        PUtils.sleepNormal(100, 300);
        Keyboard.typeKeysInt(KeyEvent.VK_ENTER);
        PUtils.sleepNormal(700, 1200);
        if (!PUtils.waitCondition(3000, () -> {
            Widget postLogin = PWidgets.get(378, 87);
            return postLogin != null && postLogin.getText().equalsIgnoreCase("CLICK HERE TO PLAY");
        })){
            return false;
        }

        return handlePostLoginScreen();
    }

}
