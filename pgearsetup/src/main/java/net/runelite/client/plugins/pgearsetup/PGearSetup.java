package net.runelite.client.plugins.pgearsetup;
import com.google.inject.Provides;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.paistisuite.PScript;
import net.runelite.client.plugins.paistisuite.PaistiSuite;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.types.Filters;
import net.runelite.client.plugins.paistisuite.api.types.PItem;
import net.runelite.client.plugins.pgearsetup.UI.PGearSetupPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Extension
@PluginDependency(PaistiSuite.class)
@PluginDescriptor(
        name = "PGearSetup",
        enabledByDefault = false,
        description = "Save and withdraw gear & inventory setups automatically.",
        tags = {"banking", "items", "paisti"}
)

@Slf4j
@Singleton
public class PGearSetup extends PScript {
    PGearSetupPanel panel;
    private NavigationButton navButton;
    @Inject
    private PGearSetupConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    public ItemManager itemManager;

    private GearSetupData targetGearSetup = null;
    public ArrayList<GearSetupData> gearSetups;

    @AllArgsConstructor
    private class WithdrawQuantity {
        @Getter
        @Setter
        int id, quantity;
        @Getter
        boolean isNoted;
    }

    @Provides
    PGearSetupConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PGearSetupConfig.class);
    }

    @Override
    protected void startUp(){
        loadSetups();

        panel = injector.getInstance(PGearSetupPanel.class);
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(this.getClass(), "nav.png");

        navButton = NavigationButton.builder()
                .tooltip("PGearSetup")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown(){
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    protected void onGameStateChanged(GameStateChanged e){
        if (e.getGameState() == GameState.LOGGED_IN){
            rebuildPanel();
        }
    }

    public void rebuildPanel(){
        SwingUtilities.invokeLater(() -> panel.reBuild());
    }

    public void reloadPanel(){
        clientToolbar.removeNavigation(navButton);
        panel = injector.getInstance(PGearSetupPanel.class);
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(this.getClass(), "nav.png");

        navButton = NavigationButton.builder()
                .tooltip("PGearSetup")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    public void saveSetups(){
        log.info("Saved gear setups");
        String serialized;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(gearSetups);
            oos.close();
            serialized = Base64.getEncoder().encodeToString(baos.toByteArray());
            configManager.setConfiguration("PGearSetup", "gearSetupsSerializedStore", serialized);
        } catch (Exception e) {
            log.error("Exception during serialization: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void loadSetups(){
        if (config.gearSetupsSerializedStore() == null){
            log.info("Defaulted gear setups");
            gearSetups = new ArrayList<>(Arrays.asList(
                    new GearSetupData("Setup 1", null, null),
                    new GearSetupData("Setup 2", null, null),
                    new GearSetupData("Setup 3", null, null),
                    new GearSetupData("Setup 4", null, null),
                    new GearSetupData("Setup 5", null, null),
                    new GearSetupData("Setup 6", null, null),
                    new GearSetupData("Setup 7", null, null),
                    new GearSetupData("Setup 8", null, null),
                    new GearSetupData("Setup 9", null, null)
            ));
        } else {
            try {
                String ser = config.gearSetupsSerializedStore();
                byte[] data = Base64.getDecoder().decode(ser);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                Object o = ois.readObject();
                ois.close();
                gearSetups = (ArrayList<GearSetupData>) o;
                log.info("Loaded gear setups");
            } catch (Exception e) {
                log.error("Exception during deserialization: " + e.getMessage());
                e.printStackTrace();
                log.info("Defaulted gear setups");
                gearSetups = new ArrayList<>(Arrays.asList(
                        new GearSetupData("Setup 1", null, null),
                        new GearSetupData("Setup 2", null, null),
                        new GearSetupData("Setup 3", null, null),
                        new GearSetupData("Setup 4", null, null),
                        new GearSetupData("Setup 5", null, null),
                        new GearSetupData("Setup 6", null, null),
                        new GearSetupData("Setup 7", null, null),
                        new GearSetupData("Setup 8", null, null),
                        new GearSetupData("Setup 9", null, null)
                ));
            }
        }
    }

    public void startEquipping(GearSetupData gearSetup){
        if (isRunning()) return;
        targetGearSetup = gearSetup;
        try {
            super.start();
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("PGearSetup"))
        {
            return;
        }

        if (configButtonClicked.getKey().equalsIgnoreCase("resetSetups")) {
            gearSetups = new ArrayList<>(Arrays.asList(
                    new GearSetupData("Setup 1", null, null),
                    new GearSetupData("Setup 2", null, null),
                    new GearSetupData("Setup 3", null, null),
                    new GearSetupData("Setup 4", null, null),
                    new GearSetupData("Setup 5", null, null),
                    new GearSetupData("Setup 6", null, null),
                    new GearSetupData("Setup 7", null, null),
                    new GearSetupData("Setup 8", null, null),
                    new GearSetupData("Setup 9", null, null)
            ));
            saveSetups();
            reloadPanel();
            PUtils.sendGameMessage("Reset all gear setups!");
        }
    }


    @Override
    public void loop(){
        if (!PBanking.openBank()) {
            PUtils.sendGameMessage("Unable to open bank!");
            requestStop();
            return;
        }

        if (!PUtils.waitCondition(PUtils.random(3000, 5000), () -> PBanking.isBankOpen())){
            PUtils.sendGameMessage("Unable to open bank!");
            requestStop();
            return;
        }
        PUtils.sleepNormal(200, 500);

        if (PBanking.depositInventory()){
            PUtils.sleepNormal(300, 600);
        } else {
            PUtils.sendGameMessage("Unable to deposit inventory!");
            requestStop();
            return;
        }

        if (PBanking.depositEquipment()){
            PUtils.sleepNormal(300, 600);
        } else {
            PUtils.sendGameMessage("Unable to deposit gear!");
            requestStop();
            return;
        }

        outer:
        for (GearSetupItemOptions eq : targetGearSetup.getEquipment().values()){
            if (eq.getOptions() == null) continue;
            for (GearSetupItem option : eq.getOptions()){
                boolean success = PBanking.withdrawItem("" + option.getId(), option.getQuantity());
                if (!success) success = PBanking.withdrawItem(PInventory.getItemDef(option.getId()).getName(), option.getQuantity());
                if (!success){
                    log.info("Unable to withdraw item id: " + option.getId());
                } else {
                    PUtils.sleepNormal(120, 400, 30, 160);
                    continue outer;
                }
            }
        }

        /*
        PBanking.closeBank();
        if (!PUtils.waitCondition(PUtils.random(1800, 2700), () -> !PBanking.isBankOpen())){
            PUtils.sendGameMessage("Unable to close bank!");
            requestStop();
            return;
        }*/

        PUtils.sleepNormal(300, 700);
        outer2:
        for (GearSetupItemOptions eq : targetGearSetup.getEquipment().values()){
            if (eq.getOptions() == null) continue;
            for (GearSetupItem option : eq.getOptions()){
                PItem eqItem = PInventory.findItem(Filters.Items.idEquals(option.getId()));
                if (eqItem == null) eqItem = PInventory.findItem(Filters.Items.nameEquals(PInventory.getItemDef(option.getId()).getName()));
                if (!PInteraction.item(eqItem, "Wear", "Wield")) {
                    log.info("Unable to equip item id: " + option.getId());
                } else {
                    PUtils.sleepNormal(120, 400, 30, 160);
                    continue outer2;
                }
            }
        }

        if (targetGearSetup.getInventory().stream().filter(i -> i.getOptions() != null && i.getOptions().size() != 0 && i.getOptions().get(0).getQuantity() != -1).count() == 0){
            PUtils.sendGameMessage("Gearing finished!");
            requestStop();
            return;
        }

        if (!PBanking.openBank()) {
            PUtils.sendGameMessage("Unable to open bank!");
            requestStop();
            return;
        }

        if (!PUtils.waitCondition(PUtils.random(3000, 5000), () -> PBanking.isBankOpen())){
            PUtils.sendGameMessage("Unable to open bank!");
            requestStop();
            return;
        }
        PUtils.sleepNormal(200, 500);

        // Group items by ID so they can be withdrawn at one time
        ArrayList<WithdrawQuantity> quantities = new ArrayList<>();
        outer3:
        for (GearSetupItemOptions withdrawItem : targetGearSetup.getInventory()){
            if (withdrawItem.getOptions() == null) continue;
            for (GearSetupItem option : withdrawItem.getOptions()) {
                WithdrawQuantity prev = quantities.stream().filter(q -> q.isNoted == option.isNoted() && q.id == (option.isNoted() ? option.getId() - 1 : option.getId())).findFirst().orElse(null);
                final Integer[] prevQuantity = {0};
                quantities.stream().filter(q -> q.getId() == (option.isNoted() ? option.getId() - 1 : option.getId())).forEach(q -> prevQuantity[0] += q.quantity);

                if (prev != null){
                    if (PBanking.findBankItem(Filters.Items.idEquals(option.isNoted() ? option.getId() - 1 : option.getId()).and(pItem -> pItem.getQuantity() >= prevQuantity[0] + option.getQuantity())) != null){
                        prev.setQuantity(prev.getQuantity() + option.getQuantity());
                        continue outer3;
                    }
                } else {
                    if (PBanking.findBankItem(Filters.Items.idEquals(option.isNoted() ? option.getId() - 1 : option.getId()).and(pItem -> pItem.getQuantity() >= option.getQuantity())) != null){
                        quantities.add(new WithdrawQuantity(option.isNoted() ? option.getId() - 1 : option.getId(), option.getQuantity(), option.isNoted()));
                        continue outer3;
                    }
                }
            }
        }

        for (WithdrawQuantity withdrawInfo : quantities){
            if (!PBanking.withdrawItem("" + withdrawInfo.getId(), withdrawInfo.getQuantity(), withdrawInfo.isNoted())){
                log.info("Unable to withdraw item id: " + withdrawInfo.getId());
                continue;
            }
            PUtils.sleepNormal(120, 400, 30, 160);
        }

        PUtils.sendGameMessage("Gear setup finished!");
        requestStop();
    }

    @Override
    public void onStart(){

    }
    @Override
    public void onStop(){

    }
}
