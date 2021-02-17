package net.runelite.client.plugins.aiofighter.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.aiofighter.AIOFighter;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class FightEnemiesState extends State {
    public FightEnemiesState(AIOFighter plugin){
        super(plugin);
    }
    public long targetAcquiredTimestamp = System.currentTimeMillis();
    public NPC lastTarget;

    @Override
    public String getName() {
        return "Fighting enemies";
    }

    @Override
    public void loop() {
        super.loop();

        // No combat and no target
        if (!inCombat() && !isInteracting() ){
            log.info("No combat - Trying to attack new target");
            PUtils.sleepNormal(500, 3500, 250, 800);
            attackNewTarget();
            return;
        }

        // No combat and trying to target timeout check
        if (!inCombat() && isInteracting() && System.currentTimeMillis() - targetAcquiredTimestamp >= 3000 && !PPlayer.isMoving()){
            log.info("Stuck trying to target enemy - Trying to attack new target");
            PUtils.sleepNormal(300, 1500, 250, 400);
            attackNewTarget();
            return;
        }

        // Current target is not suitable anymore
        if (getCurrentTarget() != null && !isCurrentTargetValid() && !getCurrentTarget().isDead()){
            log.info("Current target not valid - Trying to attack new target");
            PUtils.sleepNormal(300, 1500, 250, 400);
            attackNewTarget();
        }
    }

    public boolean attackNewTarget(){
        NPC target = getNewTarget();
        if (PInteraction.npc(target, "Attack")) {
            targetAcquiredTimestamp = System.currentTimeMillis();
            lastTarget = target;
            PUtils.sleepNormal(600, 1500);
            return true;
        }

        return false;
    }

    public boolean isCurrentTargetValid(){
        NPC interacting = (NPC)PPlayer.get().getInteracting();
        if (interacting != null){
            return plugin.validTargetFilter.test(interacting);
        }

        return false;
    }

    public NPC getNewTarget(){
        List<NPC> targets = plugin.getValidTargets();
        if (targets.size() < 1) return null;
        targets.sort(targetPrioritySorter);
        if (targets.size() >= 2 && PUtils.random(1,5) <= 1) {
            return targets.get(1);
        }
        return targets.get(0);
    }

    public double distanceTo(WorldPoint point){
        return PPlayer.getWorldLocation().distanceToHypotenuse((point));
    }
    public double distanceTo(NPC npc){
        return PPlayer.getWorldLocation().distanceToHypotenuse(npc.getWorldLocation());
    }

    public Comparator<NPC> targetPrioritySorter = (a, b) -> {
        boolean aTargetingUs = a.getInteracting() != null && a.getInteracting().equals(PPlayer.get());
        boolean bTargetingUs = b.getInteracting() != null && b.getInteracting().equals(PPlayer.get());
        if (aTargetingUs && !bTargetingUs) return -1;
        if (bTargetingUs && !aTargetingUs) return 1;
        return (int)Math.round(distanceTo(a)) - (int)Math.round(distanceTo(b));
    };

    public boolean inCombat(){
        NPC npc = (NPC)PPlayer.get().getInteracting();
        if (npc == null) return false;
        if (npc.getInteracting() != null && npc.getInteracting().equals(PPlayer.get())) return true;
        return false;
    }

    public NPC getCurrentTarget(){
        NPC npc = (NPC)PPlayer.get().getInteracting();
        return npc;
    }

    public boolean isInteracting(){
        NPC npc = (NPC)PPlayer.get().getInteracting();
        return npc != null;
    }

    @Override
    public boolean condition() {
        return getNewTarget() != null;
    }
}
