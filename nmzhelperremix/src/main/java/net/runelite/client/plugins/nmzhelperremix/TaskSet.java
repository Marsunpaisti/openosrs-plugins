package net.runelite.client.plugins.nmzhelperremix;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Client;

public class TaskSet
{
	public List<Task> taskList = new ArrayList<>();

	public TaskSet(Task... tasks)
	{
		taskList.addAll(Arrays.asList(tasks));
	}

	public void addAll(NMZHelperPlugin plugin, Client client, NMZHelperConfig config, List<Class<?>> taskClazzes)
	{
		for (Class<?> taskClass : taskClazzes)
		{
			try
			{
				Constructor ctor = taskClass.getDeclaredConstructor(NMZHelperPlugin.class, Client.class, NMZHelperConfig.class);
				ctor.setAccessible(true);
				taskList.add((Task)ctor.newInstance(plugin, client, config));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void clear()
	{
		taskList.clear();
	}

	/**
	 * Iterates through all the tasks in the set and returns
	 * the highest priority valid task.
	 *
	 * @return The first valid task from the task list or null if no valid task.
	 */
	public Task getValidTask()
	{
		for (Task task : this.taskList)
		{
			if (task.validate())
			{
				return task;
			}
		}
		return null;
	}
}
