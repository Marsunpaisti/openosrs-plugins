package net.runelite.client.plugins.paistisuite;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public abstract class PScript extends Plugin {
    protected Runnable requestRunnerStop;
    private PScriptRunner scriptRunner;
    private Thread scriptThread;
    private boolean isRunning = false;

    public void start() throws Exception {
        if (scriptRunner != null) {
            log.error("Trying to start an already running script! Killing old runner");
            requestStop();
            PUtils.sendGameMessage("Script already running!");
            return;
        }

        try {
            scriptRunner = new PScriptRunner(this);
        } catch (Exception e) {
            log.error("Error: " + ExceptionUtils.getStackTrace(e));
            return;
        }
        scriptThread = new Thread(scriptRunner);
        scriptThread.start();
        isRunning = true;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public void requestStop() {
        log.info("Requested stop.");
        if (scriptRunner != null) scriptRunner.requestStop();
        scriptRunner = null;
        isRunning = false;
    }

    public boolean isStopRequested() {
        return this.scriptRunner == null || this.scriptRunner.isStopRequested();
    }

    @Override
    protected void shutDown() {
        log.info("Requested stop.");
        requestStop();
    }

    protected abstract void loop();

    protected abstract void onStart();

    protected abstract void onStop();
}
