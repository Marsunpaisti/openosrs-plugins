package net.runelite.client.plugins.quester;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PShopping;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.quester.CooksAssistant.CooksAssistantQuest;
import net.runelite.client.plugins.quester.RestlessGhost.RestlessGhostQuest;
import net.runelite.client.plugins.quester.SheepShearer.SheepShearerQuest;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;

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
    Instant startedTimestamp;
    Task previousTask;
    Task currentTask;
    @Inject
    private QuesterConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private QuesterOverlay overlay;
    @Getter
    private QuestTaskRunner questTaskRunner;
    private int nextRunAt = PUtils.random(25, 65);
    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN && PUtils.getClient().getGameState() != GameState.LOADING) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        handleRun();
        handleEating();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public boolean webWalkTo(WorldPoint loc){
        return DaxWalker.walkTo(new RSTile(loc), walkingCondition);
    }

    public void handleRun(){
        if (!PWalking.isRunEnabled() && (PWalking.getRunEnergy() > nextRunAt || inCombat())){
            nextRunAt = PUtils.random(25, 65);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(300, 1500, 500, 1200);
        }
    }

    public boolean inCombat(){
        return PObjects.findNPC(npc -> npc.getInteracting() != null && npc.getInteracting().equals(PPlayer.get())) != null;
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
        overlayManager.remove(overlay);
    }

    @Subscribe
    private void onGameTick(GameTick event){
    }

    @Override
    protected void loop() {
        PUtils.sleepNormal(40, 60);
        handleRun();
        handleEating();
        questTaskRunner.loop();
    }

    @Override
    protected void onStart() {
        startedTimestamp = Instant.now();
        overlayManager.add(overlay);
        DaxWalker.getInstance().allowTeleports = false;
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
        questTaskRunner = new QuestTaskRunner(
                new SheepShearerQuest(this),
                new CooksAssistantQuest(this),
                new RestlessGhostQuest(this)
        );
    }

    @Override
    protected void onStop() {
        overlayManager.remove(overlay);
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
