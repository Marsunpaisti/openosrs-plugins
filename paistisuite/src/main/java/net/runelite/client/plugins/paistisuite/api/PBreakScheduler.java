package net.runelite.client.plugins.paistisuite.api;

import java.time.Duration;
import java.time.Instant;

public class PBreakScheduler {
    private Instant lastBreakStarted;
    private Instant lastBreakEnded;
    private Instant schedulerCreated;
    private int minIntervalMinutes;
    private int maxIntervalMinutes;
    private int minDurationSeconds;
    private int maxDurationSeconds;
    private int currentBreakDuration;
    private int nextBreakIntervalSeconds;

    public PBreakScheduler(int minIntervalMinutes, int maxIntervalMinutes, int minDurationSeconds, int maxDurationSeconds){
        schedulerCreated = Instant.now();
        lastBreakStarted = null;
        lastBreakEnded = Instant.now();
        this.minIntervalMinutes = minIntervalMinutes;
        this.maxIntervalMinutes = maxIntervalMinutes;
        this.minDurationSeconds = minDurationSeconds;
        this.maxDurationSeconds = maxDurationSeconds;
        nextBreakIntervalSeconds = (int)PUtils.randomNormal(minIntervalMinutes*60, maxIntervalMinutes*60);
    }

    public boolean shouldTakeBreak(){
        return Duration.between(lastBreakEnded, Instant.now()).getSeconds() >= nextBreakIntervalSeconds;
    }

    public int getCurrentBreakDuration(){
        return currentBreakDuration;
    }

    public int getTimeUntiNextBreak() {
        return (int) Duration.between(Instant.now(), lastBreakEnded.plusSeconds(nextBreakIntervalSeconds)).getSeconds();
    }

    public void startBreak(){
        lastBreakStarted = Instant.now();
        currentBreakDuration = (int)PUtils.randomNormal(minDurationSeconds, maxDurationSeconds);
        nextBreakIntervalSeconds = (int)PUtils.randomNormal(minIntervalMinutes*60, maxIntervalMinutes*60);
    }

    public void endBreak(){
        lastBreakEnded = Instant.now();
    }

    public boolean shouldEndBreak(){
        return Duration.between(lastBreakStarted, Instant.now()).getSeconds() >= currentBreakDuration;
    }
}
