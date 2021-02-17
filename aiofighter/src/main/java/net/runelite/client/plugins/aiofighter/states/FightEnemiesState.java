package net.runelite.client.plugins.aiofighter.states;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.NPCQuery;
import net.runelite.client.plugins.aiofighter.AIOFighter;
import net.runelite.client.plugins.paistisuite.api.Filters;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FightEnemiesState extends State {
    public FightEnemiesState(AIOFighter plugin){
        super(plugin);
    }

    @Override
    public String getName() {
        return "Fighting enemies";
    }

    @Override
    public void loop() {
        super.loop();
    }

    public NPC getNewTarget(){
        List<NPC> targets = plugin.getValidTargets();
        if (targets.size() == 1) return null;
        targets.sort(distanceSorter);
        if (targets.size() >= 2 && PUtils.random(1,5) <= 1) {
            return targets.get(1);
        }
        return targets.get(0);
    }

    public double distanceTo(WorldPoint point){
        return PPlayer.getWorldLocation().distanceToHypotenuse((point));
    }

    public Comparator<NPC> distanceSorter = Comparator.comparingInt(a -> (int) (Math.round(distanceTo(a.getWorldLocation()))));

    public boolean inCombat(){
        NPC npc = (NPC)PPlayer.get().getInteracting();
        if (npc == null) return false;
        if (npc.getInteracting().equals(PPlayer.get())) return true;
        return false;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
