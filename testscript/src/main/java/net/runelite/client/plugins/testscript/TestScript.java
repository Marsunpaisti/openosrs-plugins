package net.runelite.client.plugins.TestScript;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import org.pf4j.Extension;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "TestScript",
        enabledByDefault = false,
        description = "Testing stuff dont use.",
        tags = {"banking", "items", "paisti"}
)

@Slf4j
@Singleton
public class TestScript extends PScript {
    boolean fishActionFinished = false;
    final ReentrantLock fishingLock = new ReentrantLock();
    ExecutorService herbTarThread = Executors.newSingleThreadExecutor();

    @Override
    protected void startUp(){
        // Start the script runner thread on the plugin startup event
        try {
            super.start();
        } catch (Exception e){
            // This shouldnt ever happen lol
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Subscribe
    protected void onAnimationChanged(AnimationChanged event){
        if (!event.getActor().equals(PPlayer.get())) return;
        if (isFishActionFinished()) return;

        // If we are doing the fishing animation here, animation cancel with herbtar
        if (event.getActor().getAnimation() == 622){
            herbTarThread.submit(() -> {
                PUtils.sleepNormal(650, 800); // I have no idea how long this sleep has to be from the animation start
                PItem dropFish = PInventory.findItem(Filters.Items.nameContains("Salmon", "Trout"));
                PInteraction.item(dropFish, "Drop");
                PUtils.sleepNormal(100, 200);
                PItem herbTar = PInventory.findItem(Filters.Items.nameEquals("Swamp tar"));
                PItem herb = PInventory.findItem(Filters.Items.nameContains("Guam leaf"));
                if (!PInteraction.useItemOnItem(herbTar, herb)){
                    PUtils.sendGameMessage("Unable to make herbtar!");
                    return;
                }
                setFishActionFinished(true);
            });
        }
    }

    @Override
    protected void loop() {
        PUtils.sleepNormal(70, 130); // Just a small sleep so this doesnt run 9001 times per second
        // Condition stuff for checking rod and bait and whatever
        if (PInventory.isFull() || PUtils.getClient() == null || PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;

        NPC fishingSpot = PObjects.findNPC(
                Filters.NPCs.actionsContains("Lure")
                        .and(n -> n.getWorldLocation().distanceTo2D(PPlayer.getWorldLocation()) < 20)
        );

        // PInteraction internally null-checks so we dont need one before it
        if (!PInteraction.npc(fishingSpot, "Lure")){
            PUtils.sendGameMessage("Unable to find a fishing spot!");
            return;
        }
        // Set flag to false and wait for it to go true
        setFishActionFinished(false);

        // Wait in script thread until animation cancel logic or whatever tells us its done
        PUtils.waitCondition((int)PUtils.randomNormal(7000, 10000), this::isFishActionFinished);
        setFishActionFinished(true); // Set this to true if we somehow magically got here without using herbtar idk lol

        // Here herbtar animation should begin soon? Lets wait until the animation has begun and then fish again
        PUtils.waitCondition((int)PUtils.randomNormal(1000, 1500), () -> PPlayer.get().getAnimation() == 5249);
    }

    public boolean isFishActionFinished(){
        // Tbh I dont even know if this needs to be synchronized
        synchronized (fishingLock){
            return this.fishActionFinished;
        }
    }

    public void setFishActionFinished(boolean val){
        // Tbh I dont even know if this needs to be synchronized
        synchronized (fishingLock){
            this.fishActionFinished = val;
        }
    }

    @Override
    public void onStart(){
        PUtils.sendGameMessage("Fisher started!");
    }

    @Override
    public void onStop(){

    }
}
