package net.runelite.client.plugins.paistisuite.scripts.chopper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.framework.PScript;

@Slf4j
public class Chopper extends PScript {
    @Override
    protected void loop() {
        PUtils.sleepNormal(3000, 5000);
        PUtils.sendGameMessage("Test");
        if (PUtils.getClient().getGameState() != GameState.LOGGED_IN) return;
        if (PPlayer.get().getAnimation() != -1 || PPlayer.isMoving()) return;

        var tree = PObjects.getAllObjectsWithDefs()
                .stream()
                .filter(pair -> pair.getSecond().getName().equalsIgnoreCase("Tree"))
                .filter(pair -> pair.getFirst().getWorldLocation().distanceTo(PPlayer.getWorldLocation()) < 5)
                .sorted((a, b) -> a.getFirst().getWorldLocation().distanceTo(PPlayer.getWorldLocation()) - b.getFirst().getWorldLocation().distanceTo(PPlayer.getWorldLocation()))
                .findFirst();

        var axe = PInventory.getAllItemsWithDefs()
                .stream()
                .filter(pair -> pair.getSecond().getName().equalsIgnoreCase("Bronze axe"))
                .findFirst();


        if (tree.isPresent() && axe.isPresent()){
            PUtils.sendGameMessage("Trying to chop tree");
            PInteraction.useItemOnGameObject(axe.get().getFirst(), tree.get().getFirst());
        }

    }


    @Override
    protected void onStart() {
        log.info("TestScript started");
    }

    @Override
    protected void onStop() {
        log.info("TestScript stopped");
    }
}
