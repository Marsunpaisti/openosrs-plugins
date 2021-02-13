package net.runelite.client.plugins.paistisuite.framework;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.concurrent.*;

@Slf4j
@Singleton
public class ClientExecutor{
    @Inject
    private Client client;

    BlockingQueue<Runnable> scheduledTasks = new LinkedBlockingQueue<Runnable>();
    BlockingQueue<String> scheduledTasksNames = new LinkedBlockingQueue<String>();

    /**
     * Schedule a {@link Runnable} to be ran in the client threa
     * @param task {@link Runnable} to be ran
     */
    public void schedule(Runnable task, String name){
        if (name == null) name = "N/A";
        assert !client.isClientThread();
        scheduledTasks.add(task);
        scheduledTasksNames.add(name);
    }

    /**
     * Schedule a {@link Callable} to be ran in the client thread, returns a {@link Future} that will eventually have the return value of the callable
     * @param task {@link Callable} to be ran
     * @param <T> Return type of the given callable
     * @return Future that will eventually have the return value of the callable
     */
    public <T> Future<T> schedule(@NotNull Callable<T> task, String name){
        assert !client.isClientThread();
        ClientFuture<T> resultFuture = new ClientFuture<T>();
        schedule(() -> {
            try {
                if (resultFuture.isCancelled()) return;
                resultFuture.submitResult(task.call());
            } catch (Exception e) {
                resultFuture.submitException(e);
            }
        }, name);

        return resultFuture;
    }

    /**
     * Schedule a {@link Callable} to be ran in the client thread and waits for the result before continuing
     * @param task {@link Callable} to be ran
     * @param <T> Return type of the given callable
     * @return Result of the callable
     */
    public <T> T scheduleAndWait(@NotNull Callable<T> task, String name) throws InterruptedException, ExecutionException, CancellationException{
        assert !client.isClientThread();
        return schedule(task, name).get();
    }

    /**
     * Run all callables that are currently queued for the executor
     * Must be ran from client thread (ideally onClientTick)
     * @return True if at least one task was executed, false if queue was empty
     */
    public boolean runAllTasks(){
        assert client.isClientThread();
        int tasksRan = 0;
        HashMap<String, Integer> ranTasks = new HashMap<String, Integer>(scheduledTasks.size());
        while (scheduledTasks.size() > 0) {
            scheduledTasks.poll().run();
            String taskName = scheduledTasksNames.poll();
            tasksRan++;
            ranTasks.merge(taskName, 1, Integer::sum);
        }
        if (tasksRan > 0){
            StringBuilder message = new StringBuilder();
            message.append("Ran " + tasksRan + " tasks.");
            ranTasks.forEach((key, val) -> {
                message.append(" " + key + " x " + val.toString());
            });
            log.info(message.toString());
        }
        return tasksRan > 0;
    }

    public void clearAllTasks(){
        log.info("Cleared all scheduled tasks");
        scheduledTasks.clear();
    }

    /**
     * Runs the next callables from the queue
     * Must be ran from client thread (ideally onClientTick)
     * @return True if a task was executed, false if queue was empty
     */
    public boolean runOneTask(){
        assert client.isClientThread();
        if (scheduledTasks.size() > 0){
            scheduledTasks.poll().run();
            String taskName = scheduledTasksNames.poll();
            log.info("Ran task: " + taskName);
            return true;
        }
        return false;
    }
}
