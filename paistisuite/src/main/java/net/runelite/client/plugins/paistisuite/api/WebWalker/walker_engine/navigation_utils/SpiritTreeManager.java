package net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.navigation_utils;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.PaistiSuiteConfig;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Singleton
public class SpiritTreeManager {

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    @Inject
    private Gson gson;

    @Getter
    private static final Map<String, Map<SpiritTree.Location, Boolean>> spiritTreeData = new HashMap<>();

    public static Map<SpiritTree.Location, Boolean> getActiveSpiritTrees(Client client) {
        return spiritTreeData.getOrDefault(Hashing.sha256()
                .hashString(client.getUsername(), StandardCharsets.UTF_8)
                .toString(), new HashMap<>());
    }

    public void checkTrees(int groupId) {
        if (groupId == 187) {
            log.info("Checking");
            clientThread.invokeLater(() -> {
                Widget options = client.getWidget(187, 3);
                if (options == null) {
                    log.info("Options Null");
                    return;
                }
                Widget[] children = options.getChildren();
                if (children == null) {
                    log.info("Children null");
                    return;
                }
                for (Widget child : children) {
                    String text = child.getText();
                    if (text != null) {
                        for (SpiritTree.Location location : SpiritTree.Location.values()) {
                            if (text.contains(location.getName())) {
                                String userNameHash = Hashing.sha256()
                                        .hashString(client.getUsername(), StandardCharsets.UTF_8)
                                        .toString();
                                Map<SpiritTree.Location, Boolean> currentSpiritTrees = spiritTreeData.getOrDefault(userNameHash, new HashMap<>());
                                currentSpiritTrees.put(location, !text.contains("5f5f5f"));
                                spiritTreeData.put(userNameHash, currentSpiritTrees);
                                break;
                            }
                        }
                    }
                }
                log.info("Saved Trees");
                saveSpiritTrees();
            });
        }
    }

    public void loadSpiritTrees() {
        final String spiritTreeJSON = configManager.getConfiguration(PaistiSuite.CONFIG_GROUP, PaistiSuiteConfig.SPIRIT_TREES);

        if (!Strings.isNullOrEmpty(spiritTreeJSON)) {
            final Map<String, Map<SpiritTree.Location, Boolean>> trees = gson.fromJson(spiritTreeJSON, new TypeToken<HashMap<String, HashMap<SpiritTree.Location, Boolean>>>() {
            }.getType());

            spiritTreeData.clear();
            spiritTreeData.putAll(trees);
        }
    }

    public void saveSpiritTrees() {
        final String json = gson.toJson(spiritTreeData);
        configManager.setConfiguration(PaistiSuite.CONFIG_GROUP, PaistiSuiteConfig.SPIRIT_TREES, json);
    }
}
