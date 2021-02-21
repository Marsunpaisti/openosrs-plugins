package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.quester.RestlessGhost.RestlessGhostTask;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;

@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "Quester",
        enabledByDefault = false,
        description = "Completes quests",
        tags = {"npcs", "items", "paisti"},
        type = PluginType.UTILITY
)

@Slf4j
@Singleton
public class Quester extends PScript {
    Task previousTask;

    @Inject
    private OverlayManager overlayManager;
    private TaskContainer questContainer = new TaskContainer(
            new RestlessGhostTask()
    );

    @Override
    protected void startUp()
    {
        try {
            super.start();
        } catch (Exception e){
            log.error("Error starting tester: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void shutDown(){
        requestStop();
    }

    @Subscribe
    private void onGameTick(GameTick event){
    }

    @Override
    protected void loop() {
        PUtils.sleepNormal(40, 70);
        Task currentTask = questContainer.getTask();
        if (currentTask != previousTask){
            if (currentTask == null){
                log.info("Current task: " + "NO TASK");
            } else {
                log.info("Current task: " + currentTask.name());
            }
        }
        previousTask = currentTask;

        if (currentTask != null && !currentTask.isComplete()){
            currentTask.execute();
        }
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }
}
