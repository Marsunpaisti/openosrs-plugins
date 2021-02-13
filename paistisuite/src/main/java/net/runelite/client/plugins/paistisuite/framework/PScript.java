package net.runelite.client.plugins.paistisuite.framework;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public abstract class PScript {
    protected Runnable requestRunnerStop;
    public PScript() {
    }

    public void setStopRequester(Runnable stopRequester) {
        this.requestRunnerStop = stopRequester;
    }

    private void requestStop(){
        try {
            this.requestRunnerStop.run();
        }catch (Exception e){
            log.error("Error: " + ExceptionUtils.getStackTrace(e));
        }
    }

    protected abstract void loop();
    protected abstract void onStart();
    protected abstract void onStop();
}
