package net.runelite.client.plugins.aiofighter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.queries.NPCQuery;
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
import net.runelite.client.plugins.aiofighter.states.FightEnemiesState;
import net.runelite.client.plugins.aiofighter.states.LootItemsState;
import net.runelite.client.plugins.aiofighter.states.State;
import net.runelite.client.plugins.aiofighter.states.WalkToFightAreaState;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.WalkingCondition;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.real_time_collision.CollisionDataCollector;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.Keyboard;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PGroundItem;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.update.PluginInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
        tags = {"combat", "magic", "fighter", "paisti"}
)

@Slf4j
@Singleton
public class AIOFighter extends PScript {
    int nextRunAt = PUtils.random(25, 65);
    int nextEatAt;
    public boolean enablePathfind;
    public Instant startedTimestamp;
    boolean usingSavedSafeSpot = false;
    boolean usingSavedFightTile = false;
    public int minEatHp;
    public int maxEatHp;
    public int searchRadius;
    public WorldPoint safeSpot;
    public WorldPoint searchRadiusCenter;
    public String[] enemiesToTarget;
    public String[] foodsToEat;
    public String[] lootNames;
    public int lootGEValue;
    public Predicate<NPC> validTargetFilter;
    public Predicate<PGroundItem> validLootFilter;
    public Predicate<PItem> validFoodFilter;
    public boolean stopWhenOutOfFood;
    public boolean eatFoodForLoot;
    public boolean safeSpotForCombat;
    public boolean safeSpotForLogout;
    private long lastAntiAfk = System.currentTimeMillis();
    private long antiAfkDelay = PUtils.randomNormal(120000, 270000);
    State currentState;
    List<State> states;
    public FightEnemiesState fightEnemiesState = new FightEnemiesState(this);
    public LootItemsState lootItemsState = new LootItemsState(this);
    public WalkToFightAreaState walkToFightAreaState = new WalkToFightAreaState(this);
    private String currentStateName;

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
    @Inject
    private OPRSExternalPluginManager oprsExternalPluginManager;
    @Inject
    private Gson gson;
    @Inject
    private ChatMessageManager chatMessageManager;


    @Provides
    AIOFighterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AIOFighterConfig.class);
    }

    @Override
    protected void startUp() {
        if (PUtils.getClient() != null && PUtils.getClient().getGameState() == GameState.LOGGED_IN) {
            readConfig();
        }
        overlayManager.add(overlay);
        overlayManager.add(minimapoverlay);
    }

    @Subscribe
    protected void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            readConfig();
        }
    }

    @Override
    protected synchronized void onStart() {
        PluginWrapper wrappedPaistiPlugin = oprsExternalPluginManager.getExternalPluginManager().getPlugin("paistisuite-plugin");
        PluginWrapper wrappedAIOFighterPlugin = oprsExternalPluginManager.getExternalPluginManager().getPlugin("aiofighter-plugin");
        if (wrappedPaistiPlugin == null) {
            sendGameMessage("AIOFighter - Missing PaistiSuite Plugin");
            return;
        }
        if (!isPluginEnabled("paistisuite")) {
            sendGameMessage("AIOFighter - PaistiSuite Plugin needs to be Enabled");
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
                            sendGameMessage("AIOFighter - PaistiSuite version out of date, should be " + paistiGithubVersion);
                        }
                    } else if (pluginInfo.id.equals("aiofighter-plugin")) {
                        String aiofighterGithubVersion = pluginInfo.releases.get(0).version;
                        if (!wrappedAIOFighterPlugin.getDescriptor().getVersion().equals(aiofighterGithubVersion)) {
                            sendGameMessage("AIOFighter - AIOFighter version out of date, should be " + aiofighterGithubVersion);
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            log.error("Version check error: ", e);
        }
        PUtils.sendGameMessage("AiO Fighter started!");
        startedTimestamp = Instant.now();
        readConfig();
        if (usingSavedSafeSpot) {
            PUtils.sendGameMessage("Loaded safespot from last config.");
        }
        if (usingSavedFightTile) {
            PUtils.sendGameMessage("Loaded fight area from last config.");
        }
        DaxWalker.setCredentials(PaistiSuite.getDaxCredentialsProvider());
        DaxWalker.getInstance().allowTeleports = false;
        states = new ArrayList<State>();
        states.add(this.lootItemsState);
        states.add(this.fightEnemiesState);
        states.add(this.walkToFightAreaState);
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

    private synchronized void readConfig() {
        searchRadius = config.searchRadius();
        stopWhenOutOfFood = config.stopWhenOutOfFood();
        eatFoodForLoot = config.eatForLoot();
        enemiesToTarget = PUtils.parseCommaSeparated(config.enemyNames());
        foodsToEat = PUtils.parseCommaSeparated(config.foodNames());
        lootNames = PUtils.parseCommaSeparated(config.lootNames());
        maxEatHp = Math.min(PSkills.getActualLevel(Skill.HITPOINTS), config.maxEatHP());
        minEatHp = Math.min(config.minEatHP(), maxEatHp);
        nextEatAt = (int) PUtils.randomNormal(minEatHp, maxEatHp);
        validTargetFilter = createValidTargetFilter();
        validLootFilter = createValidLootFilter();
        validFoodFilter = createValidFoodFilter();
        lootGEValue = config.lootGEValue();
        safeSpotForCombat = config.enableSafeSpot();
        safeSpotForLogout = config.exitInSafeSpot();
        enablePathfind = config.enablePathfind();
        if (safeSpot == null) {
            usingSavedSafeSpot = true;
            safeSpot = config.storedSafeSpotTile();
        }
        if (searchRadiusCenter == null) {
            usingSavedFightTile = true;
            searchRadiusCenter = config.storedFightTile();
        }

        log.info("Targeting enemies: " + String.join(", ", enemiesToTarget));
        log.info("Food names: " + String.join(", ", foodsToEat));
        log.info("Loot names: " + String.join(", ", lootNames));
        log.info("Loot over value: " + (lootGEValue <= 0 ? "disabled" : lootGEValue));
        log.info("Min eat: " + minEatHp + " max eat: " + maxEatHp + " next eat: " + nextEatAt);
    }

    @Override
    protected synchronized void onStop() {
        PUtils.sendGameMessage("AiO Fighter stopped!");
        searchRadiusCenter = null;
        safeSpot = null;
    }

    @Override
    protected void shutDown() {
        requestStop();
        overlayManager.remove(overlay);
        overlayManager.remove(minimapoverlay);
    }

    public State getValidState() {
        for (State s : states) {
            if (s.condition()) return s;
        }
        return null;
    }

    @Override
    protected void loop() {
        PUtils.sleepFlat(50, 150);
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        if (handleStopConditions()) return;
        handleEating();
        if (isStopRequested()) return;
        handleRun();
        if (isStopRequested()) return;
        handleAntiAfk();

        State prevState = currentState;
        currentState = getValidState();
        if (currentState != null) {
            if (prevState != currentState) {
                log.info("Entered state: " + currentState.getName());
            }
            setCurrentStateName(currentState.chainedName());
            currentState.loop();
        } else {
            setCurrentStateName("Looking for state...");
        }
    }

    private void handleAntiAfk() {
        if (System.currentTimeMillis() - lastAntiAfk >= antiAfkDelay) {
            lastAntiAfk = System.currentTimeMillis();
            antiAfkDelay = PUtils.randomNormal(240000, 295000);
            Keyboard.typeKeysInt(KeyEvent.VK_SPACE);
            PUtils.sleepNormal(100, 200);
            Keyboard.typeKeysInt(KeyEvent.VK_BACK_SPACE);
            PUtils.sleepNormal(100, 200);
        }
    }

    private boolean handleStopConditions() {
        if (stopWhenOutOfFood && PInventory.findItem(validFoodFilter) == null) {
            setCurrentStateName("Stop Condition");
            if (safeSpotForLogout && PPlayer.location().distanceTo(safeSpot) != 0) {
                if (!PWalking.sceneWalk(safeSpot)) {
                    DaxWalker.getInstance().allowTeleports = false;
                    DaxWalker.walkTo(new RSTile(safeSpot), walkingCondition);
                }
                return true;
            }
            if (!fightEnemiesState.inCombat()) {
                PUtils.logout();
                if (PUtils.waitCondition(1500, () -> PUtils.getClient().getGameState() != GameState.LOGGED_IN)) {
                    requestStop();
                } else {
                    PUtils.sleepNormal(700, 2500);
                }
                return true;
            } else if (fightEnemiesState.inCombat()) {
                return true;
            }
        }
        return false;
    }

    private boolean handleEating() {
        if (PSkills.getCurrentLevel(Skill.HITPOINTS) <= nextEatAt) {
            nextEatAt = (int) PUtils.randomNormal(minEatHp, maxEatHp);
            log.info("Next eat at " + nextEatAt);
            NPC targetBeforeEating = null;
            if (currentState == fightEnemiesState
                    && fightEnemiesState.inCombat()
                    && validTargetFilter.test((NPC) PPlayer.get().getInteracting())
            ) {
                targetBeforeEating = (NPC) PPlayer.get().getInteracting();
            }

            boolean success = eatFood();
            if (success && targetBeforeEating != null && PUtils.random(1, 5) <= 4) {
                log.info("Re-targeting current enemy after eating");
                if (PInteraction.npc(targetBeforeEating, "Attack")) {
                    PUtils.sleepNormal(100, 700);
                }
            }
        }
        return false;
    }

    public boolean eatFood() {
        PItem food = PInventory.findItem(validFoodFilter);
        if (food != null) log.info("Eating " + food.getName());
        if (PInteraction.item(food, "Eat")) {
            log.info("Ate food");
            PUtils.sleepNormal(300, 1000);
            return true;
        }
        log.info("Failed to eat food!");
        return false;
    }

    public WalkingCondition walkingCondition = () -> {
        if (isStopRequested()) return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN && PUtils.getClient().getGameState() != GameState.LOADING)
            return WalkingCondition.State.EXIT_OUT_WALKER_FAIL;
        handleRun();
        handleEating();
        return WalkingCondition.State.CONTINUE_WALKER;
    };

    public void handleRun() {
        if (!PWalking.isRunEnabled() && PWalking.getRunEnergy() > nextRunAt) {
            nextRunAt = PUtils.random(25, 65);
            PWalking.setRunEnabled(true);
            PUtils.sleepNormal(300, 1500, 500, 1200);
        }
    }

    public List<NPC> getValidTargets() {
        return PUtils.clientOnly(() -> {
            CollisionDataCollector.generateRealTimeCollision();
            return new NPCQuery().result(PUtils.getClient())
                    .list.stream().filter(validTargetFilter).collect(Collectors.toList());
        }, "getValidTargets");
    }

    private synchronized Predicate<NPC> createValidTargetFilter() {
        Predicate<NPC> filter = (NPC n) -> {
            return n.getWorldLocation().distanceToHypotenuse(searchRadiusCenter) <= searchRadius
                    && Filters.NPCs.nameOrIdEquals(enemiesToTarget).test(n)
                    && (n.getInteracting() == null || n.getInteracting().equals(PPlayer.get()))
                    && !n.isDead();
        };

        if (config.enablePathfind()) filter = filter.and(this::isReachable);
        return filter;
    }

    private synchronized Predicate<PItem> createValidFoodFilter() {
        return Filters.Items.nameOrIdEquals(foodsToEat);
    }

    private synchronized Predicate<PGroundItem> createValidLootFilter() {
        Predicate<PGroundItem> filter = Filters.GroundItems.nameContainsOrIdEquals(lootNames);
        if (lootGEValue > 0) filter = filter.or(Filters.GroundItems.SlotPriceAtLeast(lootGEValue));
        filter = filter.and(item -> item.getLocation().distanceToHypotenuse(searchRadiusCenter) <= (searchRadius + 2));
        if (config.lootOwnKills()) filter = filter.and(item -> item.getLootType() == PGroundItem.LootType.PVM);
        if (config.enablePathfind()) filter = filter.and(item -> isReachable(item.getLocation()));
        return filter;
    }

    public Boolean isReachable(WorldPoint p) {
        Reachable r = new Reachable();
        return r.canReach(new RSTile(p));
    }

    public Boolean isReachable(NPC n) {
        Reachable r = new Reachable();
        return r.canReach(new RSTile(n.getWorldLocation()));
    }

    @Subscribe
    private synchronized void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equalsIgnoreCase("AIOFighter")) return;
        readConfig();
    }

    @Subscribe
    private synchronized void onConfigButtonPressed(ConfigButtonClicked configButtonClicked) {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("AIOFighter")) return;
        if (PPlayer.get() == null && PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;

        if (configButtonClicked.getKey().equals("startButton")) {
            Player player = PPlayer.get();
            try {
                super.start();
            } catch (Exception e) {
                log.error(e.toString());
                e.printStackTrace();
            }
        } else if (configButtonClicked.getKey().equals("stopButton")) {
            requestStop();
        } else if (configButtonClicked.getKey().equals("setFightAreaButton")) {
            PUtils.sendGameMessage("Fight area set to your position!");
            configManager.setConfiguration("AIOFighter", "storedFightTile", PPlayer.location());
            searchRadiusCenter = PPlayer.location();
            usingSavedFightTile = false;
        } else if (configButtonClicked.getKey().equals("setSafeSpotButton")) {
            PUtils.sendGameMessage("Safe spot set to your position!");
            configManager.setConfiguration("AIOFighter", "storedSafeSpotTile", PPlayer.location());
            safeSpot = PPlayer.location();
            usingSavedSafeSpot = false;
        }
    }

    public synchronized String getCurrentStateName() {
        return currentStateName;
    }

    public synchronized void setCurrentStateName(String currentStateName) {
        this.currentStateName = currentStateName;
    }
}
