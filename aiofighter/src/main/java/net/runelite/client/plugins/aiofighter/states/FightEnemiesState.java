package net.runelite.client.plugins.aiofighter.states;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.aiofighter.AIOFighter;
import net.runelite.client.plugins.paistisuite.api.PInteraction;
import net.runelite.client.plugins.paistisuite.api.PPlayer;
import net.runelite.client.plugins.paistisuite.api.PUtils;
import net.runelite.client.plugins.paistisuite.api.PWalking;
import net.runelite.client.plugins.paistisuite.api.WebWalker.walker_engine.local_pathfinding.Reachable;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class FightEnemiesState extends State {
    public FightEnemiesState(AIOFighter plugin){
        super(plugin);
    }
    public long targetClickedTimestamp = System.currentTimeMillis();
    public NPC lastTarget;

    @Override
    public String getName() {
        return "Fight enemies";
    }

    @Override
    public void loop() {
        super.loop();

        // In combat and outside safespot
        if (inCombat() && plugin.safeSpotForCombat && PPlayer.location().distanceTo(plugin.safeSpot) > 0) {
            PWalking.sceneWalk(plugin.safeSpot);
            PUtils.waitCondition(PUtils.random(700, 1300), () -> PPlayer.isMoving());
            PUtils.waitCondition(PUtils.random(4000, 6000), () -> !PPlayer.isMoving());
            PUtils.sleepNormal(100, 400);
            // Attack target again after moving to safespot
            if (!attackLastTarget()) attackNewTarget();
            return;
        }

        // No combat and no target
        if (!inCombat() && !isInteracting() ){
            log.info("No combat - Trying to attack new target");
            PUtils.sleepNormal(500, 3500, 250, 800);
            if (plugin.isStopRequested()) return;
            attackNewTarget();

            // Run to safespot after attack animation starts to play
            if (plugin.safeSpotForCombat && PPlayer.location().distanceTo(plugin.safeSpot) > 0) {
                PUtils.waitCondition(PUtils.random(2000, 3000), () -> PPlayer.get().getAnimation() != -1);
                if (PPlayer.location().distanceTo(plugin.safeSpot) == 0) return;
                PUtils.sleepNormal(100, 800, 150, 200);
                PWalking.sceneWalk(plugin.safeSpot);
                PUtils.waitCondition(PUtils.random(700, 1000), () -> PPlayer.isMoving());
                PUtils.waitCondition(PUtils.random(3000, 4500), () -> !PPlayer.isMoving());
                PUtils.sleepNormal(100, 800, 150, 200);
                // Attack target again after moving to safespot
                if (!attackLastTarget()) attackNewTarget();
            }
            return;
        }

        // No combat and trying to target timeout check
        if (!inCombat() && isInteracting()){
            if (System.currentTimeMillis() - targetClickedTimestamp >= 3000 && !PPlayer.isMoving()){
                log.info("Stuck trying to target enemy - Trying to attack new target");
                PUtils.sleepNormal(300, 1500, 250, 400);
                if (plugin.isStopRequested()) return;
                attackNewTarget();
            }

            return;
        }

        // Current target is not suitable anymore
        if (getCurrentTarget() != null && !isCurrentTargetValid() && !getCurrentTarget().isDead()){
            log.info("Current target not valid - Trying to attack new target");
            PUtils.sleepNormal(300, 1500, 250, 400);
            if (plugin.isStopRequested()) return;
            attackNewTarget();
        }
    }

    public boolean attackNewTarget(){
        NPC target = getNewTarget();
        if (PInteraction.npc(target, "Attack")) {
            targetClickedTimestamp = System.currentTimeMillis();
            lastTarget = target;
            return PUtils.waitCondition(PUtils.random(700, 1300), this::isInteracting);
        }

        return false;
    }

    public boolean attackLastTarget(){
        if (lastTarget == null || !plugin.validTargetFilter.test(lastTarget)) return false;
        if (PInteraction.npc(lastTarget, "Attack")) {
            targetClickedTimestamp = System.currentTimeMillis();
            return PUtils.waitCondition(PUtils.random(700, 1300), this::isInteracting);
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
        if (targets.size() >= 2 && PUtils.random(1,5) <= 1 && !(targets.get(0).getInteracting() != null && targets.get(0).getInteracting().equals(PPlayer.get()))) {
            return targets.get(1);
        }
        return targets.get(0);
    }

    public int pathFindDistanceTo(WorldPoint p){
        Reachable r = new Reachable();
        return r.getDistance(new RSTile(p));
    }

    private double distanceTo(NPC n){
        return PPlayer.distanceTo(n);
    }

    public Comparator<NPC> targetPrioritySorter = (a, b) -> {
        boolean aTargetingUs = a.getInteracting() != null && a.getInteracting().equals(PPlayer.get());
        boolean bTargetingUs = b.getInteracting() != null && b.getInteracting().equals(PPlayer.get());
        if (aTargetingUs && !bTargetingUs) return -1;
        if (bTargetingUs && !aTargetingUs) return 1;
        if (plugin.enablePathfind) {
            return pathFindDistanceTo(a.getWorldLocation()) - pathFindDistanceTo(b.getWorldLocation());
        } else {
            return (int)Math.round(distanceTo(a)) - (int)Math.round(distanceTo(b));
        }
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
