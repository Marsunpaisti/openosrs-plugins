package net.runelite.client.plugins.webwalker;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.webwalker.Category;
import net.runelite.client.plugins.webwalker.WebWalkerConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void loop() {
        PUtils.sleepFlat(1500, 3000);
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;

        if (path == null){
            PlayerDetails details = PlayerDetails.generate();
            // Remove stuff from playerdetails so server wont try to use certain shortcuts or teleports.
            details.equipment = new ArrayList<IntPair>();
            details.setting.forEach(pair -> log.info("Setting " + pair.getKey() + ": " + pair.getValue()));
            details.varbit.forEach(pair -> log.info("Varbit " + pair.getKey() + ": " + pair.getValue()));
            details.firemaking = 1;
            details.woodcutting = 1;
            details.hunter = 1;
            details.construction = 1;
            details.magic = 1;
            details.mining = 1;

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
