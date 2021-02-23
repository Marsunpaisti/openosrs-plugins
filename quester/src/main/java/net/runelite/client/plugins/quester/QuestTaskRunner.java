package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public class QuestTaskRunner {
    Task currentTask;
    List<Quest> quests;
    String currentQuestName;
    String currentTaskName;
    ReentrantLock overlayLock = new ReentrantLock();

    public QuestTaskRunner(Quest ...quests){
        this.quests = new ArrayList<Quest>();
        Collections.addAll(this.quests, quests);
    }

    public void loop(){
        if (quests.stream().allMatch(Quest::isCompleted)){
            log.info("All quests completed!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }
        if (quests.stream().allMatch(q -> q.isCompleted() || q.isFailed())){
            log.info("All quests completed or failed!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }

        if (currentTask == null || currentTask.isFailed() || currentTask.isCompleted()){
            currentTask = getNewTask();
        }

        if (currentTask != null){
            setCurrentTaskName(currentTask.name());
            currentTask.execute();
        } else {
            log.info("Error: All quests arent complete/failed, but currentTask is null!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }
    }


    public void setCurrentTaskName(String val){
        synchronized (overlayLock){
            this.currentTaskName = val;
        }
    }
    public void setCurrentQuestName(String val){
        synchronized (overlayLock){
            this.currentQuestName = val;
        }
    }

    public String getCurrentTaskName(){
        synchronized (overlayLock) {
            return this.currentTaskName;
        }
    }

    public String getCurrentQuestName(){
        synchronized (overlayLock) {
            return this.currentQuestName;
        }
    }

    public Task getNewTask(){
        List<Quest> sorted = quests
                .stream()
                .filter(q -> !q.isCompleted() && !q.isFailed())
                .sorted(Comparator.comparingInt(Quest::currentDistance))
                .collect(Collectors.toList());

        for (Quest q : sorted){
            Task qTask = q.getTask();
            if (qTask != null){
                setCurrentQuestName(q.getName());
                log.info("Started task: " + q.getName() + " > " + qTask.name());
                return q.getTask();
            }
        }
        return null;
    }
}
