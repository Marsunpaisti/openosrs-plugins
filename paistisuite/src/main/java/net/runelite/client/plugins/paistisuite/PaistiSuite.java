package net.runelite.client.plugins.paistisuite;

import com.google.inject.Injector;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.api.PGroundItems;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.DaxCredentials;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.DaxCredentialsProvider;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils.SpiritTreeManager;
import net.runelite.client.plugins.paistisuite.framework.ClientExecutor;
import net.runelite.client.plugins.paistisuite.framework.MenuInterceptor;
import net.runelite.client.plugins.paistisuite.ui.PaistiSuitePanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;

@Extension
@PluginDescriptor(
        name = "PaistiSuite",
        description = "Scripting framework by Paisti. Required by all other Paisti Plugins to work!",
        tags = {"npcs", "items"}
)

@Slf4j
@Singleton
public class PaistiSuite extends Plugin {
    public final static String CONFIG_GROUP = "PaistiSuite";

    @Inject
    private PaistiSuiteConfig config;
    @Inject
    public ClientExecutor clientExecutor;
    @Inject
    public Client client;
    @Inject
    public ChatMessageManager chatMessageManager;
    @Inject
    public NPCManager npcManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    protected Injector injector;
    @Getter
    @Inject
    private ConfigManager configManager;
    @Inject
    public WorldService worldService;
    @Inject
    private SpiritTreeManager spiritTreeManager;

    PaistiSuitePanel panel;
    private NavigationButton navButton;
    private static PaistiSuite instance;
    private static final ReentrantLock daxCredsLock = new ReentrantLock();
    private static DaxCredentialsProvider daxCredProvider;
    private static String daxKey;
    private static String daxSecret;

    public static PaistiSuite getInstance() {
        return instance;
    }

    @Provides
    PaistiSuiteConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PaistiSuiteConfig.class);
    }

    @Override
    protected void startUp() {

        final BufferedImage icon = ImageUtil.loadImageResource(PaistiSuite.class, "logo.png");

        if (injector != null) {
            panel = injector.getInstance(PaistiSuitePanel.class);
            navButton = NavigationButton.builder()
                    .tooltip("PaistiSuite")
                    .icon(icon)
                    .priority(25)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);
        }

        spiritTreeManager.loadSpiritTrees();

        client.setHideDisconnect(true);
        updateDaxCredProvider();
        instance = this;
        if (clientExecutor != null) clientExecutor.clearAllTasks();
    }

    public static DaxCredentialsProvider getDaxCredentialsProvider() {
        synchronized (daxCredsLock) {
            return new DaxCredentialsProvider() {
                @Override
                public DaxCredentials getDaxCredentials() {
                    return new DaxCredentials(daxKey, daxSecret);
                }
            };
        }
    }

    private void updateDaxCredProvider() {
        synchronized (daxCredsLock) {
            daxKey = config.daxApiKey();
            daxSecret = config.daxSecretKey();
        }
    }

    @Subscribe
    private void onItemSpawned(ItemSpawned event) {
        PGroundItems.onItemSpawned(event);
    }

    @Subscribe
    private void onItemDespawned(ItemDespawned event) {
        PGroundItems.onItemDespawned(event);
    }

    @Subscribe
    private void onItemQuantityChanged(ItemQuantityChanged event) {
        PGroundItems.onItemQuantityChanged(event);
    }

    @Subscribe
    private void onNpcLootReceived(final NpcLootReceived npcLootReceived) {
        PGroundItems.onNpcLootReceived(npcLootReceived);
    }

    @Subscribe
    private void onPlayerLootReceived(final PlayerLootReceived playerLootReceived) {
        PGroundItems.onPlayerLootReceived(playerLootReceived);
    }

    @Subscribe
    private void onGameTick(final GameTick event) {
        PGroundItems.onGameTick(event);
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event) {
        PGroundItems.onGameStateChanged(event);
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event) {
        int groupId = event.getGroupId();
        spiritTreeManager.checkTrees(groupId);
    }

    @Override
    protected void shutDown() {
        client.setHideDisconnect(false);
        if (clientExecutor != null) clientExecutor.clearAllTasks();
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    private void onClientTick(ClientTick t) {
        clientExecutor.runAllTasks();
    }

    @Subscribe
    private void onBeforeRender(BeforeRender bf) {
        //clientExecutor.runAllTasks();
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        MenuInterceptor.onMenuOptionClicked(event);
        PGroundItems.onMenuOptionClicked(event);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(PaistiSuite.CONFIG_GROUP)) return;
        updateDaxCredProvider();

        if (SpiritTreeManager.getActiveSpiritTrees(client).isEmpty() && event.getKey().equals(PaistiSuiteConfig.SPIRIT_TREES)) {
            spiritTreeManager.loadSpiritTrees();
        }
    }
}
