package net.runelite.client.plugins.quester;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.paistisuite.api.PUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QuestTaskRunner {
    Task currentTask;
    List<Quest> quests;
    String currentQuestName;
    String currentTaskName;

    public QuestTaskRunner(Quest ...quests){
        this.quests = new ArrayList<Quest>();
        Collections.addAll(this.quests, quests);
    }

    public void loop(){
        if (quests.stream().allMatch(Quest::isComplete)){
            log.info("All quests completed!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }
        if (quests.stream().allMatch(q -> q.isComplete() || q.isFailed())){
            log.info("All quests completed or failed!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }

        if (currentTask == null || currentTask.isFailed() || currentTask.isCompleted()){
            currentTask = getNewTask();
        }

        if (currentTask != null){
            currentTask.execute();
        } else {
            log.info("Error: All quests arent complete/failed, but currentTask is null!");
            PUtils.sleepNormal(2000, 4000);
            return;
        }
    }

    public Task getNewTask(){
        List<Quest> sorted = quests
                .stream()
                .filter(q -> !q.isComplete() && !q.isFailed())
                .sorted(Comparator.comparingInt(Quest::currentDistance))
                .collect(Collectors.toList());

        for (Quest q : sorted){
            Task qTask = q.getTask();
            if (qTask != null){
                currentQuestName = q.getName();
                currentTaskName = qTask.name();
                log.info("Started task: " + q.getName() + " > " + currentTaskName);
                return q.getTask();
            }
        }
        return null;
    }
}
