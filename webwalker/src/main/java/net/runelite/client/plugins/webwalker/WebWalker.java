package net.runelite.client.plugins.webwalker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.RenderOverview;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.PMenu;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.WebWalkerServerApi;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.WalkerEngine;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.SpiritTree;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.SpiritTreeManager;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.*;
import java.util.List;

@SuppressWarnings({"ConstantConditions", "UnnecessaryReturnStatement"})
@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "WebWalker",
        enabledByDefault = false,
        description = "Walks around with DaxWalker. Special thanks to Manhattan, Illumine and Runemoro.",
        tags = {"npcs", "items", "paisti"}
)

@Slf4j
@Singleton
public class WebWalker extends PScript {
    @Inject
    private WebWalkerConfig config;
    RSTile targetLocation = null;
    int nextRunAt = PUtils.random(55, 95);
    private boolean allowTeleports;
    private Point lastMenuOpenedPoint;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WebWalkerOverlay overlay;

    @Inject
    private WebWalkerWorldmapOverlay worldmapOverlay;

    @Inject
    private ConfigManager configManager;

    @Provides
    WebWalkerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WebWalkerConfig.class);
    }

    @Override
    protected void startUp() {
    }

    @Subscribe
    private void onGameTick(GameTick event) {
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final Widget map = PUtils.getClient().getWidget(WidgetInfo.WORLD_MAP_VIEW);

        if (WalkerEngine.getInstance().isNavigating() && event.getOption().contains("Walk here")) {
            PMenu.addEntry(event, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan) + " stop walking");
            return;
        }

        if (map != null) {
            if (map.getBounds().contains(PUtils.getClient().getMouseCanvasPosition().getX(), PUtils.getClient().getMouseCanvasPosition().getY())) {
                PMenu.addEntry(event, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan));
                PMenu.addEntry(event, ColorUtil.wrapWithColorTag("WebWalker", Color.cyan) + " Autowalk");
            }
        }
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        lastMenuOpenedPoint = PUtils.getClient().getMouseCanvasPosition();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {

        if (event.getMenuOption().contains("stop walking")) {
            requestStop();
            return;
        }

        if (event.getMenuOption().contains("Autowalk")) {
            WorldPoint wp = calculateMapPoint(PUtils.getClient().isMenuOpen() ? lastMenuOpenedPoint : PUtils.getClient().getMouseCanvasPosition());
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
        float zoom = PUtils.getClient().getRenderOverview().getWorldMapZoom();
        RenderOverview renderOverview = PUtils.getClient().getRenderOverview();
        final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
        final Point middle = mapWorldPointToGraphicsPoint(mapPoint);

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }


    private Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint) {
        RenderOverview ro = PUtils.getClient().getRenderOverview();

        if (!ro.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY())) {
            return null;
        }

        float pixelsPerTile = ro.getWorldMapZoom();

        Widget map = PUtils.getClient().getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();

            int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
            int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

            Point worldMapPosition = ro.getWorldMapPosition();

            //Offset in tiles from anchor sides
            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

            int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
            int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

            //Center on tile.
            yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
            xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

            yGraphDiff = worldMapRect.height - yGraphDiff;
            yGraphDiff += (int) worldMapRect.getY();
            xGraphDiff += (int) worldMapRect.getX();

            return new Point(xGraphDiff, yGraphDiff);
        }
        return null;
    }

    @Override
    protected void loop() {
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        PlayerDetails details = PlayerDetails.generate();

        Point3D start = new Point3D(PPlayer.location());
        Point3D destination = new Point3D(targetLocation);

        //log.info("Start: " + start + ", Destination: " + destination);

        // Im doing it manually to get the path to my local variables, you can just call DaxWalker.walkTo methods too
        DaxWalker.getInstance().allowTeleports = allowTeleports;
        List<PathRequestPair> pathRequestPairs = DaxWalker.getInstance().allowTeleports ? DaxWalker.getInstance().getPathTeleports(targetLocation) : new ArrayList<>();
        pathRequestPairs.add(new PathRequestPair(start, destination));

        for (SpiritTree.Location location : SpiritTree.Location.values()) {
            log.info(location.getName());
            if (SpiritTreeManager.getActiveSpiritTrees().getOrDefault(location, false)) {
                log.info("True");
                pathRequestPairs.add(new PathRequestPair(location.getPoint3D(), destination));
                pathRequestPairs.add(new PathRequestPair(new Point3D(PPlayer.location()), location.getPoint3D()));
            }
        }

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(details, pathRequestPairs));

        //log.info("Total Paths: " + pathResults.size());

        List<PathResult> validPaths = DaxWalker.getInstance().validPaths(pathResults);

        //log.info("Valid Paths: " + validPaths.size());

//        for (PathResult path : validPaths) {
//            log.info(path.toString());
//        }

        List<PathResult> curatedPaths = new ArrayList<>();
        List<PathResult> firstPath = new ArrayList<>();
        List<PathResult> secondPath = new ArrayList<>();

        for (PathResult path : validPaths) {
            boolean addedPath = false;
            for (SpiritTree.Location location : SpiritTree.Location.values()) {
                if (SpiritTreeManager.getActiveSpiritTrees().getOrDefault(location, false)) {
                    if (path.getFirstPoint().equals(start) && path.getLastPoint().equals(location.getPoint3D())) {
                        firstPath.add(path);
                        addedPath = true;
                        break;
                    }
                    if (path.getFirstPoint().equals(location.getPoint3D()) && path.getLastPoint().equals(destination)) {
                        secondPath.add(path);
                        addedPath = true;
                        break;
                    }
                }
            }
            if (!addedPath) {
                //log.info("Adding path1: " + path);
                curatedPaths.add(path);
            }
        }

        for (PathResult first : firstPath) {
            for (PathResult second : secondPath) {
                PathResult combinedPath = first.addPath(second);
                //log.info("Adding path2: " + combinedPath);
                curatedPaths.add(combinedPath);
            }
        }

        //log.info("Curated Paths: " + curatedPaths.size());

        PathResult pathResult = DaxWalker.getInstance().getBestPath(curatedPaths);
        log.info(pathResult.toString());
        if (pathResult == null) {
            log.warn("No valid path found");
            PUtils.sendGameMessage("No valid path found. Path status list: ");
            Set<PathStatus> statuses = new HashSet<PathStatus>();
            for (PathResult r : pathResults) {
                statuses.add(r.getPathStatus());
            }
            for (PathStatus r : statuses) {
                PUtils.sendGameMessage(r.toString());
            }
            requestStop();
            return;
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
        GameState gameState = PUtils.getClient().getGameState();
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
        overlayManager.add(overlay);
        overlayManager.add(worldmapOverlay);
        PUtils.sendGameMessage("WebWalker started!");
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
    }

    @Override
    protected void onStop() {
        overlayManager.remove(overlay);
        overlayManager.remove(worldmapOverlay);
        PUtils.sendGameMessage("WebWalker stopped!");
    }

    @Subscribe
    private synchronized void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equalsIgnoreCase("WebWalker")) return;
        if (event.getKey().equalsIgnoreCase("category")) {
            if (!event.getNewValue().equalsIgnoreCase("FARMING")) {
                configManager.setConfiguration("WebWalker", "catFarming", Farming.NONE);
            }
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
            Player player = PUtils.getClient().getLocalPlayer();
            if (player != null && PUtils.getClient().getGameState() == GameState.LOGGED_IN) {
                try {
                    super.start();
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        } else if (configButtonClicked.getKey().equals("stopButton")) {
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
