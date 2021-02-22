package net.runelite.client.plugins.quester;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.quester.RestlessGhost.RestlessGhostQuest;
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
        tags = {"npcs", "items", "paisti"}
)

@Slf4j
@Singleton
public class Quester extends PScript {
    Task previousTask;
    Task currentTask;
    @Inject
    private QuesterConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    private QuestTaskRunner questTaskRunner;
    private int nextRunAt = PUtils.random(25, 65);
    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        handleRun();
        handleEating();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public void handleRun(){
        if (!PWalking.isRunEnabled() && PWalking.getRunEnergy() > nextRunAt){
            nextRunAt = PUtils.random(25, 65);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(300, 1500, 500, 1200);
        }
    }

    private void handleEating(){
        return;
    }

    @Provides
    QuesterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(QuesterConfig.class);
    }

    @Override
    protected void startUp()
    {
    }

    @Override
    protected void shutDown(){
        requestStop();
    }

    @Subscribe
    private void onGameTick(GameTick event){
        QuestTextReader.getQuestText("Cook");
    }

    @Override
    protected void loop() {
        PUtils.sleepNormal(40, 70);
        questTaskRunner.loop();
    }

    @Override
    protected void onStart() {
        DaxWalker.getInstance().allowTeleports = false;
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
        questTaskRunner = new QuestTaskRunner(
                new RestlessGhostQuest(this)
        );
    }

    @Override
    protected void onStop() {
    }


    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("Quester"))
        {
            return;
        }

        if (configButtonClicked.getKey().equals("startButton"))
        {
            Player player = PPlayer.get();
            if (player != null && PUtils.getClient().getGameState() == GameState.LOGGED_IN)
            {
                try {
                    super.start();
                } catch (Exception e){
                    log.error(e.toString());
                }
            }
        } else if (configButtonClicked.getKey().equals("stopButton")){
            requestStop();
        }
    }

}
