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
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.DaxCredentials;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.DaxCredentialsProvider;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.Point3D;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.RunescapeBank;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.webwalker.Category;
import net.runelite.client.plugins.webwalker.WebWalkerConfig;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

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
        try {
            super.start();
        } catch (Exception e){
            log.error(e.toString());
        }
    }

    @Override
    protected void loop() {
        PUtils.sleepFlat(1500, 3000);
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        if (path == null){
            path = WebWalkerServerApi.getInstance().getPath(new Point3D(PPlayer.location()), new Point3D(RunescapeBank.GRAND_EXCHANGE.getPosition()), null).toRSTilePath();
        } else {
            WalkerEngine.getInstance().walkPath(path, walkingCondition);
        }
    }

    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    @Override
    protected void onStart() {
        PUtils.sendGameMessage("Webwalker started");
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
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("WebWalker"))
        {
            return;
        }
        log.info("button {} pressed!", configButtonClicked.getKey());
        if (configButtonClicked.getKey().equals("startButton"))
        {
            Player player = PUtils.getClient().getLocalPlayer();
            if (player != null && PUtils.getClient().getGameState() == GameState.LOGGED_IN)
            {
            }
        }
    }
}
