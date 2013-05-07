package eu.tomylobo.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class WorldScheduler {
	private static int lastTaskId = 0;

	public class TaskDescriptor implements Comparable<TaskDescriptor> {
		public final Runnable runnable;
		public final long period;

		public long scheduledAt = Long.MAX_VALUE;

		public final int taskId = ++lastTaskId;

		public TaskDescriptor(Runnable runnable, long period) {
			this.runnable = runnable;
			this.period = period;
		}

		public void run() {
			runnable.run();

			if (period < 0)
				return;

			schedule(this, period);
		}

		@Override
		public int compareTo(TaskDescriptor other) {
			long thisVal = this.scheduledAt;
			long anotherVal = other.scheduledAt;

			if (thisVal > anotherVal)
				return 1;

			if (thisVal < anotherVal)
				return -1;

			return 0;
		}

		@Override
		public String toString() {
			return String.format("TaskDescriptor(%d, %s)", scheduledAt, runnable.getClass().getName());
		}
	}

	private long now = 0;

	public final PriorityQueue<TaskDescriptor> queue = new PriorityQueue<TaskDescriptor>(); // TEMP public!
	public final Map<Integer, TaskDescriptor> scheduledTasks = new HashMap<Integer, TaskDescriptor>(); // TEMP public!

	private int schedule(TaskDescriptor taskDescriptor, long delay) {
		taskDescriptor.scheduledAt = now + Math.max(1, delay);

		queue.add(taskDescriptor);
		scheduledTasks.put(taskDescriptor.taskId, taskDescriptor);

		return taskDescriptor.taskId;
	}


	// API
	public int scheduleSyncDelayedTask(Runnable runnable) {
		return scheduleSyncDelayedTask(runnable, 1);
	}

	public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
		return scheduleSyncRepeatingTask(runnable, delay, -1);
	}

	public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
		return schedule(new TaskDescriptor(runnable, period), delay);
	}

	public void cancelTask(int taskId) {
		final TaskDescriptor taskDescriptor = scheduledTasks.remove(taskId);
		if (taskDescriptor == null)
			return;

		queue.remove(taskDescriptor);
	}

	public void tick() {
		++now;
		while (!queue.isEmpty() && queue.peek().scheduledAt <= now) {
			final TaskDescriptor taskDescriptor = queue.poll();
			scheduledTasks.remove(taskDescriptor.taskId);

			taskDescriptor.run();
		}
	}
}
