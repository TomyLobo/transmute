package eu.tomylobo.scheduler;

import net.minecraft.world.World;

public abstract class ScheduledTask implements Runnable {
	private int taskId = -1;
	private final WorldScheduler worldScheduler;

	public ScheduledTask(WorldScheduler worldScheduler) {
		this.worldScheduler = worldScheduler;
	}

	public ScheduledTask(World world) {
		this(Scheduler.instance.getWorldScheduler(world));
	}

	public ScheduledTask(int dimension) {
		this(Scheduler.instance.getWorldScheduler(dimension));
	}

	public void scheduleSyncDelayed(long delay) {
		taskId = worldScheduler.scheduleSyncDelayedTask(this, delay);
	}


	public void scheduleSyncDelayed() {
		taskId = worldScheduler.scheduleSyncDelayedTask(this);
	}


	public void scheduleSyncRepeating(long delay, long period) {
		taskId = worldScheduler.scheduleSyncRepeatingTask(this, delay, period);
	}

	public void cancel() {
		worldScheduler.cancelTask(taskId);
		taskId = -1;
	}
}
