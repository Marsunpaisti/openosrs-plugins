package net.runelite.client.plugins.paistisuite.framework;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PScriptRunner implements Runnable {
    private PScript script;

    public PScriptRunner(Class<? extends PScript> scriptClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.script = scriptClass.getDeclaredConstructor().newInstance();
        this.script.setStopRequester(this::requestStop); }

    private boolean stopRequested = false;
    public void requestStop (){
        synchronized (this){
            this.stopRequested = true;
        }
    };

    public void run() {
        log.info("Script runner started");
        script.onStart();
        while (true) {
            synchronized (this) {
                if (stopRequested){
                    script.onStop();
                    log.info("Script runner stopped");
                    return;
                }
            }
            script.loop();
        }
    }
}
