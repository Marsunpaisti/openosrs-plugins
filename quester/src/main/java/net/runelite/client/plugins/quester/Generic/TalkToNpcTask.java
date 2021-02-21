package net.runelite.client.plugins.quester.Generic;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.paistisuite.api.*;
import net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.DaxWalker;
import net.runelite.client.plugins.paistisuite.api.WebWalker.wrappers.RSTile;
import net.runelite.client.plugins.quester.Task;

@Slf4j
public class TalkToNpcTask extends Task {
    String npcName;
    WorldPoint location;
    String talkAction;
    String[] choices;
    boolean isCompleted;
    boolean failed;
    boolean walkedToDestination = false;
    int talkAttempts = 0;

    public TalkToNpcTask(String npcName, WorldPoint location, String talkAction, String ...choices){
        super();
        this.npcName = npcName;
        this.location = location;
        this.talkAction = talkAction;
        this.choices = choices;
    }

    public TalkToNpcTask(String npcName, WorldPoint location, String ...choices){
        this(npcName, location, "Talk-to", choices);
    }

    @Override
    public String name() {
        return "Talk to " + this.npcName;
    }

    @Override
    public WorldPoint location() {
        return this.location;
    }

    @Override
    public boolean execute() {
        if (talkAttempts >= 3){
            log.info("Failed talk to npc task. Too many attempts to talk to npc.");
            this.failed = true;
            return false;
        }
        NPC npc = PObjects.findNPC(Filters.NPCs.nameContains(npcName));
        if (npc == null) {
            if (!walkedToDestination && DaxWalker.walkTo(new RSTile(this.location))){
                walkedToDestination = true;
                log.info("Walked to NPC");
                return true;
            } else {
                this.failed = true;
                log.info("Unable to walk to NPC!");
                return false;
            }
        } else {
            if (!PInteraction.npc(npc, talkAction)) {
                log.info("Unable to intaract with NPC!");
                this.failed = true;
                return false;
            } else {
                PUtils.waitCondition(PUtils.random(800, 1400), () -> PPlayer.isMoving());
                PUtils.waitCondition(PUtils.random(5000, 7000), () -> !PPlayer.isMoving());
                if (!PUtils.waitCondition(PUtils.random(1300, 1900), PDialogue::isConversationWindowUp)){
                    talkAttempts++;
                    log.info("Timed out while waiting for conversation window!");
                    return true;
                } else {
                    if (PDialogue.handleDialogueInOrder(choices)){
                        this.isCompleted = true;
                        return true;
                    } else {
                        log.info("Failed at handling talk to npc dialogue!");
                        this.failed = true;
                        return false;
                    }
                }
            }
        }
    };

    @Override
    public boolean condition() {
        return !isCompleted && !isFailed();
    }

    @Override
    public boolean isComplete() {
        return isCompleted;
    }

    @Override
    public boolean isFailed(){
        return this.failed;
    }
}
