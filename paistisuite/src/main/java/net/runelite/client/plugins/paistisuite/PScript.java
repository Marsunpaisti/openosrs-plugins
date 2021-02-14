package net.runelite.client.plugins.paistisuite;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public abstract class PScript extends Plugin {
    protected Runnable requestRunnerStop;
    private PScriptRunner scriptRunner;
    private Thread scriptThread;
    private boolean stopRequested = false;

    public void start() throws Exception {
        if (scriptRunner != null) log.error("Trying to start an already running script!");
        stopRequested = false;

        try {
            scriptRunner = new PScriptRunner(this);
        } catch (Exception e){
            log.error("Error: " + ExceptionUtils.getStackTrace(e));
            return;
        }
        scriptThread = new Thread(scriptRunner);
        scriptThread.start();
    }

    protected void requestStop(){
        stopRequested = true;
        if (scriptRunner != null) scriptRunner.requestStop();
        scriptRunner = null;
    }

    public boolean isStopRequested(){
        return this.stopRequested;
    }

    @Override
    protected void shutDown() {
        requestStop();
    }

    protected abstract void loop();
    protected abstract void onStart();
    protected abstract void onStop();
}
