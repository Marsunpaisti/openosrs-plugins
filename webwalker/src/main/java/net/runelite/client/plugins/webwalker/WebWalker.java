package net.runelite.client.plugins.webwalker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.OPRSExternalPluginManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PMenu;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;
import net.runelite.client.plugins.paistisuite.api.WebWalker.Teleports.Teleport;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WebWalkerDebugRenderer;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.SpiritTree;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.SpiritTreeManager;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.update.PluginInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"ConstantConditions", "UnnecessaryReturnStatement"})
@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "WebWalker",
        enabledByDefault = false,
        description = "Walks around with DaxWalker. Special thanks to Satoshi Oda, Manhattan, Illumine and Runemoro.",
        tags = {"npcs", "items", "paisti", "satoshi"}
)

@Slf4j
@Singleton
public class WebWalker extends PScript {

    public final static String CONFIG_GROUP = "WebWalker";

    @Inject
    private WebWalkerConfig config;
    RSTile targetLocation = null;
    int nextRunAt = PUtils.random(55, 95);
    private boolean allowTeleports;
    private boolean gnomeVillageComplete;
    private Point lastMenuOpenedPoint;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WebWalkerOverlay overlay;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private WebWalkerWorldmapOverlay worldmapOverlay;

    @Inject
    private ConfigManager configManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private WebWalkerDebugRenderer webWalkerDebugRenderer;

    @Inject
    private OPRSExternalPluginManager oprsExternalPluginManager;

    @Inject
    private Gson gson;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Provides
    WebWalkerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WebWalkerConfig.class);
    }

    @Override
    protected void startUp() {
        Teleport.TeleportType.TELEPORT_SPELL.setMoveCost(config.teleportSpellCost());
        Teleport.TeleportType.TELEPORT_SCROLL.setMoveCost(config.teleportScrollCost());
        Teleport.TeleportType.NONRECHARABLE_TELE.setMoveCost(config.nonrechargableTeleCost());
        Teleport.TeleportType.RECHARGABLE_TELE.setMoveCost(config.rechargableTeleCost());
        Teleport.TeleportType.UNLIMITED_TELE.setMoveCost(config.unlimitedTeleportCost());
    }

    @Subscribe
    private void onGameTick(GameTick event) {
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);

        if (WalkerEngine.getInstance().isNavigating() && event.getOption().contains("Walk here")) {
            PMenu.addEntry(event, client, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan) + " stop walking");
            return;
        }

        if (map != null) {
            if (map.getBounds().contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                PMenu.addEntry(event, client, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan) + " Autowalk");
                PMenu.addEntry(event, client, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan));
            }
        }
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        lastMenuOpenedPoint = client.getMouseCanvasPosition();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().contains("stop walking")) {
            log.info("Clicked Stop Walking");
            requestStop();
            return;
        }

        if (event.getMenuOption().contains("Autowalk")) {
            WorldPoint wp = calculateMapPoint(client.isMenuOpen() ? lastMenuOpenedPoint : client.getMouseCanvasPosition());
            allowTeleports = config.allowTeleports();
            targetLocation = new RSTile(wp);
            event.consume();
            try {
                super.start();
            } catch (Exception e) {
                log.error("Error trying to start WebWalker");
                e.printStackTrace();
            }
        }
    }

    private WorldPoint calculateMapPoint(Point point) {
        float zoom = client.getRenderOverview().getWorldMapZoom();
        RenderOverview renderOverview = client.getRenderOverview();
        final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
        final Point middle = webWalkerDebugRenderer.mapWorldPointToGraphicsPoint(mapPoint);

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }

    @Override
    protected void loop() {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        PlayerDetails details = PlayerDetails.generate();

        Point3D start = new Point3D(PPlayer.location());
        Point3D destination = new Point3D(targetLocation);
        DaxWalker.getInstance().allowTeleports = allowTeleports;

        List<PathResult> pathResults;
        boolean farmCapeToSpiritTree = false;

        boolean goingNearestBank = targetLocation.equals(new RSTile(0, 0, 0));

        if (goingNearestBank) {
            log.info("Start: " + start + ", Destination: Nearest Bank");
            List<BankPathRequestPair> pathRequestPairs = DaxWalker.getInstance().allowTeleports ? DaxWalker.getInstance().getBankPathTeleports() : new ArrayList<>();
            pathRequestPairs.add(0, new BankPathRequestPair(start, null));
            log.info("Total Request Count: " + pathRequestPairs.size());
            pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(PlayerDetails.generate(), pathRequestPairs));
        } else {
            log.info("Start: " + start + ", Destination: " + destination);
            List<PathRequestPair> pathRequestPairs = DaxWalker.getInstance().allowTeleports ? DaxWalker.getInstance().getPathTeleports(targetLocation) : new ArrayList<>();
            log.info("Teleport count: " + pathRequestPairs.size());
            pathRequestPairs.add(0, new PathRequestPair(start, destination));

            farmCapeToSpiritTree = SpiritTreeManager.getActiveSpiritTrees(client).getOrDefault(SpiritTree.Location.SPIRIT_TREE_GUILD, false)
                    && Teleport.FARMING_CAPE.getTeleportLimit().canCast() && Teleport.FARMING_CAPE.getRequirement().satisfies();
            if (farmCapeToSpiritTree) {
                pathRequestPairs.add(new PathRequestPair(new Point3D(Teleport.FARMING_CAPE.getLocation()), SpiritTree.Location.SPIRIT_TREE_GUILD.getPoint3D()));
            }

            boolean hasFarmedSpiritTree = false;
            for (SpiritTree.Location location : SpiritTree.Location.values()) {
                if (location.isFarming() && SpiritTreeManager.getActiveSpiritTrees(client).getOrDefault(location, false)) {
                    hasFarmedSpiritTree = true;
                    break;
                }
            }

            if (gnomeVillageComplete && hasFarmedSpiritTree && client.getWorldType().contains(WorldType.MEMBERS)) {
                for (SpiritTree.Location location : SpiritTree.Location.values()) {
                    if (SpiritTreeManager.getActiveSpiritTrees(client).getOrDefault(location, false)) {
                        pathRequestPairs.add(new PathRequestPair(location.getPoint3D(), destination));
                        pathRequestPairs.add(new PathRequestPair(new Point3D(PPlayer.location()), location.getPoint3D()));
                    }
                }
            }
            log.info("Total Request Count: " + pathRequestPairs.size());
            pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(details, pathRequestPairs));
        }

        //log.info("Total Paths: " + pathResults.size());

        List<PathResult> validPaths = DaxWalker.getInstance().validPaths(pathResults);

        if (validPaths.get(0) != null && validPaths.get(0).getPath() != null) {
            destination = validPaths.get(0).getLastPoint();
        }

//        log.info("Valid Paths: " + validPaths.size());
//        for (PathResult path : validPaths) {
//            log.info(path.toString());
//        }

        List<PathResult> curatedPaths = new ArrayList<>();
        List<PathResult> firstPath = new ArrayList<>();
        List<PathResult> secondPath = new ArrayList<>();

        for (PathResult path : validPaths) {
            if (farmCapeToSpiritTree) {
                if (path.getFirstPoint().equals(new Point3D(Teleport.FARMING_CAPE.getLocation())) && path.getLastPoint().equals(SpiritTree.Location.SPIRIT_TREE_GUILD.getPoint3D())) {
                    firstPath.add(path);
                    continue;
                }
            }

            SpiritTree.Location entrySpirit = SpiritTree.Location.getSpiritTree(path.getLastPoint());
            if (path.getFirstPoint().equals(start) && entrySpirit != null && SpiritTreeManager.getActiveSpiritTrees(client).getOrDefault(entrySpirit, false)) {
                firstPath.add(path);
                continue;
            }

            SpiritTree.Location exitSpirit = SpiritTree.Location.getSpiritTree(path.getFirstPoint());
            if (path.getLastPoint().equals(destination) && exitSpirit != null && SpiritTreeManager.getActiveSpiritTrees(client).getOrDefault(exitSpirit, false)) {
                secondPath.add(path);
                continue;
            }

            curatedPaths.add(path);
        }

        for (PathResult first : firstPath) {
            for (PathResult second : secondPath) {
                PathResult combinedPath = first.addPath(second);
                curatedPaths.add(combinedPath);
            }
        }

        if (curatedPaths.size() == 0) {
            log.warn("No valid path found");
            PUtils.sendGameMessage("No valid path found.");
            requestStop();
            return;
        }

//        if (curatedPaths.get(0) != null) {
//            log.info("Walk Path0: " + curatedPaths.get(0).getCost() + ", " + curatedPaths.get(0));
//        }
//        for (PathResult pathResult : curatedPaths) {
//            if (start.equals(pathResult.getPath().get(0))) {
//                log.info("Walk Path: " + curatedPaths.get(0).getCost() + ", " + pathResult);
//                continue;
//            }
//            Teleport teleport = DaxWalker.getMap().get(new RSTile(pathResult.getPath().get(0)));
//            if (teleport == null) {
//                log.info("Unknown Teleport Path: " + pathResult.getCost() + ", " + pathResult);
//                continue;
//            }
//            log.info(teleport.name() + " Path: " + (teleport.getMoveCost() + pathResult.getCost()) + ", " + pathResult);
//        }
//        log.info("Curated Paths: " + curatedPaths.size());

        PathResult pathResult = DaxWalker.getInstance().getBestPath(start, curatedPaths);

        if (pathResult == null) {
            log.warn("No valid path found");
            PUtils.sendGameMessage("No valid path found. Path status list: ");
            Set<PathStatus> statuses = new HashSet<>();
            for (PathResult r : pathResults) {
                statuses.add(r.getPathStatus());
            }
            for (PathStatus r : statuses) {
                PUtils.sendGameMessage(r.toString());
            }
            if (statuses.contains(PathStatus.RATE_LIMIT_EXCEEDED)) {
                PUtils.sendGameMessage("Consider buying your own Web Walking API key at https://admin.dax.cloud/webwalker");
            }
            requestStop();
            return;
        }
        log.info("Movecost: " + DaxWalker.getInstance().getPathMoveCost(start, pathResult) + ", Path: " + pathResult);

        if (pathResult.getPath() != null) {
            RunescapeBank destBank = RunescapeBank.getBank(new RSTile(pathResult.getLastPoint()));
            if (destBank != null) {
                PUtils.sendGameMessage("Going to Bank: " + destBank.name());
                log.info("Going to Bank: " + destBank.name());
            }
        }

        ArrayList<RSTile> path = pathResult.toRSTilePath();
        if (WalkerEngine.getInstance().walkPath(path, walkingCondition)) {
            log.info("Path successfully finished!");
        } else {
            log.info("Failed at walking path");
        }
        requestStop();
        return;
    }

    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        GameState gameState = client.getGameState();
        if (gameState != GameState.LOGGED_IN && gameState != GameState.LOADING) {
            return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        }
        handleRun();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public void handleRun() {
        if (PWalking.isRunEnabled() && config.disableRun()) {
            PWalking.setRunEnabled(false);
            PUtils.sleepNormal(800, 3000, 400, 1200);
            return;
        }

        if (!PWalking.isRunEnabled() && PWalking.getRunEnergy() > nextRunAt && !config.disableRun()) {
            nextRunAt = PUtils.random(55, 95);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(800, 3000, 400, 1200);
            return;
        }
    }

    @Override
    protected void onStart() {
        PluginWrapper wrappedPaistiPlugin = oprsExternalPluginManager.getExternalPluginManager().getPlugin("paistisuite-plugin");
        PluginWrapper wrappedWebwalkerPlugin = oprsExternalPluginManager.getExternalPluginManager().getPlugin("webwalker-plugin");
        if (wrappedPaistiPlugin == null) {
            sendGameMessage("WebWalker - Missing PaistiSuite Plugin");
            return;
        }
        if (!isPluginEnabled("paistisuite")) {
            sendGameMessage("WebWalker - PaistiSuite Plugin needs to be Enabled");
            return;
        }
        try {
            HttpUrl pluginJson = HttpUrl.parse("https://raw.githubusercontent.com/rokaHakor/openosrs-plugins/master/plugins.json");
            Request request = new Request.Builder().url(pluginJson).build();
            Response response = RuneLiteAPI.CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();
                JsonReader reader = RuneLiteAPI.GSON.newJsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                final List<PluginInfo> plugins = gson.fromJson(reader, new TypeToken<List<PluginInfo>>() {
                }.getType());
                for (PluginInfo pluginInfo : plugins) {
                    if (pluginInfo.id.equals("paistisuite-plugin")) {
                        String paistiGithubVersion = pluginInfo.releases.get(0).version;
                        if (!wrappedPaistiPlugin.getDescriptor().getVersion().equals(paistiGithubVersion)) {
                            sendGameMessage("WebWalker - PaistiSuite version out of date, should be " + paistiGithubVersion);
                        }
                    } else if (pluginInfo.id.equals("webwalker-plugin")) {
                        String webwalkerGithubVersion = pluginInfo.releases.get(0).version;
                        if (!wrappedWebwalkerPlugin.getDescriptor().getVersion().equals(webwalkerGithubVersion)) {
                            sendGameMessage("WebWalker - WebWalker version out of date, should be " + webwalkerGithubVersion);
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            log.error("Version check error: ", e);
        }
        gnomeVillageComplete = false;
        clientThread.invoke(() -> {
            // Checking if Tree Gnome Village is complete
            client.runScript(ScriptID.QUEST_STATUS_GET, 438);
            gnomeVillageComplete = client.getIntStack()[0] == 2;
        });
        overlayManager.add(overlay);
        overlayManager.add(worldmapOverlay);
        PUtils.sendGameMessage("WebWalker started!");
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
        configManager.setConfiguration(WebWalker.CONFIG_GROUP, WebWalkerConfig.WALKING, true);
    }

    public boolean isPluginEnabled(String pluginName) {
        final String value = configManager.getConfiguration(RuneLiteConfig.GROUP_NAME, pluginName.toLowerCase());
        return Boolean.parseBoolean(value);
    }

    public void sendGameMessage(String message) {
        log.info(message);
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(message)
                .build();

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(chatMessage)
                .build());
    }

    @Override
    protected void onStop() {
        overlayManager.remove(overlay);
        overlayManager.remove(worldmapOverlay);
        PUtils.sendGameMessage("WebWalker stopped!");
        configManager.setConfiguration(WebWalker.CONFIG_GROUP, WebWalkerConfig.WALKING, false);
    }

    @Subscribe
    private synchronized void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equalsIgnoreCase("WebWalker")) return;
        if (event.getKey().equalsIgnoreCase("category")) {
            if (!event.getNewValue().equalsIgnoreCase("FARMING")) {
                configManager.setConfiguration("WebWalker", "catFarming", Farming.NONE);
            }
        }
        if (event.getKey().equals("teleportSpellCost")) {
            Teleport.TeleportType.TELEPORT_SPELL.setMoveCost(config.teleportSpellCost());
        }
        if (event.getKey().equals("teleportScrollCost")) {
            Teleport.TeleportType.TELEPORT_SCROLL.setMoveCost(config.teleportScrollCost());
        }
        if (event.getKey().equals("nonrechargableTeleCost")) {
            Teleport.TeleportType.NONRECHARABLE_TELE.setMoveCost(config.nonrechargableTeleCost());
        }
        if (event.getKey().equals("rechargableTeleCost")) {
            Teleport.TeleportType.RECHARGABLE_TELE.setMoveCost(config.rechargableTeleCost());
        }
        if (event.getKey().equals("unlimitedTeleportCost")) {
            Teleport.TeleportType.UNLIMITED_TELE.setMoveCost(config.unlimitedTeleportCost());
        }
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked) {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("WebWalker")) {
            return;
        }

        allowTeleports = config.allowTeleports();
        targetLocation = getConfigTargetLocation();
        if (targetLocation == null) {
            PUtils.sendGameMessage("Invalid target location!");
            return;
        }

        if (configButtonClicked.getKey().equals("startButton")) {
            Player player = client.getLocalPlayer();
            if (player != null && client.getGameState() == GameState.LOGGED_IN) {
                try {
                    super.start();
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        } else if (configButtonClicked.getKey().equals("stopButton")) {
            log.info("Clicked Stop Button");
            requestStop();
            return;
        }
    }

    private RSTile getConfigTargetLocation() {
        if (config.category().equals(Category.CUSTOM)) {
            String[] coordStrings = config.customLocation()
                    .strip()
                    .replaceAll("[^\\d,]", "")
                    .split(",");

            if (coordStrings.length != 3) return null;
            return new RSTile(
                    Integer.parseInt(coordStrings[0]),
                    Integer.parseInt(coordStrings[1]),
                    Integer.parseInt(coordStrings[2]));
        }


        switch (config.category()) {
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
            case NONE:
                return new RSTile(0, 0, 0);
        }

        return null;
    }


    private RSTile getFarmLocation() {
        if (config.category().equals(Category.FARMING)) {
            switch (config.catFarming()) {
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
