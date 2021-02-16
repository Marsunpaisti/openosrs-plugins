package net.runelite.client.plugins.webwalker;
import com.google.inject.Provides;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WaitFor;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Filters;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSInterface;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.webwalker.Category;
import net.runelite.client.plugins.webwalker.WebWalkerConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.paistisuite.api.WebWalker.shared.InterfaceHelper.getAllChildren;

@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "WebWalker",
        enabledByDefault = false,
        description = "Walks around with dax walker",
        tags = {"npcs", "items"},
        type = PluginType.UTILITY
)

@Slf4j
@Singleton
public class WebWalker extends PScript {
    @Inject
    private WebWalkerConfig config;
    ArrayList<RSTile> path = null;
    RSTile targetLocation = null;
    int nextRunAt = PUtils.random(55, 95);

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WebWalkerOverlay overlay;

    @Inject
    private WebWalkerWorldmapOverlay worldmapOverlay;

    @Inject
    private ConfigManager configManager;

    @Provides
    WebWalkerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WebWalkerConfig.class);
    }

    @Override
    protected void startUp()
    {
    }

    @Subscribe
    private void onGameTick(GameTick event){
    }

    @Override
    protected void loop() {
        PUtils.sleepFlat(1500, 3000);
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;

        if (path == null){
            PlayerDetails details = PlayerDetails.generate();
            // Remove stuff from playerdetails so server wont try to use certain shortcuts or teleports.
            path = WebWalkerServerApi.getInstance().getPath(new Point3D(PPlayer.location()), new Point3D(targetLocation), details).toRSTilePath();
        }
        if (WalkerEngine.getInstance().walkPath(path, walkingCondition)) requestStop();
    }

    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        handleRun();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public void handleRun(){
        if (PWalking.isRunEnabled() && config.disableRun()) {
            PWalking.setRunEnabled(false);
            PUtils.sleepNormal(800, 3000, 400, 1200);
            return;
        }

        if (!PWalking.isRunEnabled() && PWalking.getRunEnergy() > nextRunAt && !config.disableRun()){
            nextRunAt = PUtils.random(55, 95);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(800, 3000, 400, 1200);
            return;
        }
    }

    @Override
    protected void onStart() {
        overlayManager.add(overlay);
        overlayManager.add(worldmapOverlay);
        PUtils.sendGameMessage("WebWalker started!");
        DaxWalker.setCredentials(new DaxCredentialsProvider() {
            @Override
            public DaxCredentials getDaxCredentials() {
                return new DaxCredentials("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY");
            }
        });
        path = null;
    }

    @Override
    protected void onStop() {
        overlayManager.remove(overlay);
        overlayManager.remove(worldmapOverlay);
        if (path != null) {
            path.clear();
            path = null;
        }
        PUtils.sendGameMessage("WebWalker stopped!");
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("WebWalker"))
        {
            return;
        }

        targetLocation = getConfigTargetLocation();
        if (targetLocation == null) {
            PUtils.sendGameMessage("Invalid target location!");
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

    private RSTile getConfigTargetLocation(){
        if (config.category().equals(Category.CUSTOM)){
            String[] coordStrings = config.customLocation()
                    .strip()
                    .replaceAll("[^\\d,]", "")
                    .split(",");

            if(coordStrings.length != 3) return null;
            return new RSTile(
                    Integer.parseInt(coordStrings[0]),
                    Integer.parseInt(coordStrings[1]),
                    Integer.parseInt(coordStrings[2]));
        }


        switch (config.category())
        {
            case BANKS:
                return new RSTile(config.catBanks().getWorldPoint());
            case BARCRAWL:
                return new RSTile(config.catBarcrawl().getWorldPoint());
            case CITIES:
                return new RSTile(config.catCities().getWorldPoint());
            case FARMING:
                return getFarmLocation();
            case GUILDS:
                return new RSTile(config.catGuilds().getWorldPoint());
            case SKILLING:
                return new RSTile(config.catSkilling().getWorldPoint());
            case SLAYER:
                return new RSTile(config.catSlayer().getWorldPoint());
            case MISC:
                return new RSTile(config.catMisc().getWorldPoint());
        }

        return null;
    }


    private RSTile getFarmLocation()
    {
        if (config.category().equals(Category.FARMING))
        {
            switch (config.catFarming())
            {
                case ALLOTMENTS:
                    return new RSTile(config.catFarmAllotments().getWorldPoint());
                case BUSHES:
                    return new RSTile(config.catFarmBushes().getWorldPoint());
                case FRUIT_TREES:
                    return new RSTile(config.catFarmFruitTrees().getWorldPoint());
                case HERBS:
                    return new RSTile(config.catFarmHerbs().getWorldPoint());
                case HOPS:
                    return new RSTile(config.catFarmHops().getWorldPoint());
                case TREES:
                    return new RSTile(config.catFarmTrees().getWorldPoint());
            }
        }
        return null;
    }
}
