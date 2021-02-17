package net.runelite.client.plugins.aiofighter;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.NPCQuery;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.aiofighter.states.FightEnemiesState;
import net.runelite.client.plugins.aiofighter.states.State;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "AiO Fighter",
        enabledByDefault = false,
        description = "Fully configurable all-in-one fighter",
        tags = {"combat", "magic", "fighter", "paisti"},
        type = PluginType.PVM
)

@Slf4j
@Singleton
public class AIOFighter extends PScript {
    int nextRunAt = PUtils.random(25,65);
    public int searchRadius;
    public WorldPoint searchRadiusCenter;
    public String[] enemiesToTarget;
    State currentState;
    List<State> states = new ArrayList<State>();
    public FightEnemiesState fightEnemiesState = new FightEnemiesState(this);

    @Inject
    private AIOFighterConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AIOFighterOverlay overlay;
    @Inject
    private AIOFighterOverlayMinimap minimapoverlay;
    @Inject
    private ConfigManager configManager;


    @Provides
    AIOFighterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AIOFighterConfig.class);
    }

    @Override
    protected void startUp()
    {

    }

    @Override
    protected void onStart() {
        overlayManager.add(overlay);
        overlayManager.add(minimapoverlay);
        PUtils.sendGameMessage("AiO Fighter started!");
        enemiesToTarget = PUtils.parseCommaSeparated(config.enemyNames());
        searchRadius = config.searchRadius();
        searchRadiusCenter = PPlayer.location();
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
        states.add(this.fightEnemiesState);
    }

    @Override
    protected void onStop() {
        overlayManager.remove(overlay);
        overlayManager.remove(minimapoverlay);
        PUtils.sendGameMessage("AiO Fighter stopped!");
    }

    public State getValidState(){
        for (State s : states) {
            if (s.condition()) return s;
        }
        return null;
    }

    @Override
    protected void loop() {
        PUtils.sleepFlat(20, 50);
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        currentState = getValidState();
        if (currentState != null) currentState.loop();
    }

    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        handleRun();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public void handleRun(){
        if (!PWalking.isRunEnabled() && PWalking.getRunEnergy() > nextRunAt){
            nextRunAt = PUtils.random(25, 65);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(300, 1500, 500, 1200);
            return;
        }
    }

    public List<NPC> getValidTargets(){
        return new NPCQuery().result(PUtils.getClient())
                .list.stream().filter(validTargetFilter).collect(Collectors.toList());
    }

    public Predicate<NPC> validTargetFilter = (NPC n) -> {
        return n.getWorldLocation().distanceToHypotenuse(searchRadiusCenter) <= searchRadius
                && Filters.NPCs.nameEquals(enemiesToTarget).test(n)
                && (n.getInteracting() == null || n.getInteracting().equals(PPlayer.get()));
    };

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("AIOFighter"))
        {
            return;
        }

        if (configButtonClicked.getKey().equals("startButton"))
        {
            Player player = PUtils.getClient().getLocalPlayer();
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
