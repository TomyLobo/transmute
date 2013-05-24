package eu.tomylobo.transmute;

import eu.tomylobo.math.Location;
import eu.tomylobo.scheduler.ScheduledTask;
import eu.tomylobo.scheduler.Scheduler;
import eu.tomylobo.scheduler.WorldScheduler;
import eu.tomylobo.transmute.listeners.TransmutePacketListener;
import eu.tomylobo.transmute.listeners.TransmutePlayerListener;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

public class Transmute implements Runnable {
	public static final Logger logger = Logger.getLogger("Minecraft");

	private static final long CREATION_DELAY = 10;

	private final TransmutePacketListener transmutePacketListener;
	@SuppressWarnings("unused")
	private final TransmutePlayerListener transmutePlayerListener;
	private final Map<Integer, Shape> transmuted = new HashMap<Integer,Shape>();
	private final Map<EntityPlayer, Entity> lastEntities = new HashMap<EntityPlayer, Entity>();

	public Transmute() {
		transmutePacketListener = new TransmutePacketListener(this);
		transmutePlayerListener = new TransmutePlayerListener(this);

		final WorldScheduler worldScheduler = Scheduler.instance.getWorldScheduler(0); // TODO: Running on the main world scheduler is ok... for now

		new ScheduledTask(worldScheduler) {
			@Override
			public void run() {
				// clean up ignored packets
				long minTimestamp = System.currentTimeMillis() - 1000;

				for (Iterator<Packet> iterator = transmutePacketListener.ignoredPackets.iterator(); iterator.hasNext(); ) {
					final Packet packet = iterator.next();

					if (packet.creationTimeMillis < minTimestamp)
						iterator.remove();
				}

				// clean up transmuted entities
				for (Iterator<Map.Entry<Integer, Shape>> iterator = transmuted.entrySet().iterator(); iterator.hasNext(); ) {
					final Map.Entry<Integer, Shape> entry = iterator.next();
					final Shape shape = entry.getValue();

					if (shape.entity.isDead)
						iterator.remove();
				}
			}
		}.scheduleSyncRepeating(0, 200);

		worldScheduler.scheduleSyncRepeatingTask(this, 0, 1);

		PlayerHelper.registerMap(lastEntities);
	}

	public boolean isTransmuted(int entityId) {
		return transmuted.containsKey(entityId);
	}

	public boolean isTransmuted(Entity entity) {
		return transmuted.containsKey(entity.entityId);
	}

	public Shape getShape(int entityId) {
		return transmuted.get(entityId);
	}

	public Shape getShape(Entity entity) {
		return transmuted.get(entity.entityId);
	}

	public Shape setShape(EntityPlayer player, Entity entity, final Shape shape) {
		if (shape.entity != entity)
			throw new IllegalArgumentException("Assigned a shape to the wrong entity!");

		transmuted.put(entity.entityId, shape);
		shape.deleteEntity();
		new ScheduledTask(entity.worldObj) { public void run() {
			shape.createTransmutedEntity();
			shape.reattachPassenger();
		}}.scheduleSyncDelayed(CREATION_DELAY);

		lastEntities.put(player, entity);

		return shape;
	}

	public Shape setShape(EntityPlayer player, Entity entity, int mobType) throws EntityTypeNotFoundException {
		return setShape(player, entity, Shape.getShape(this, entity, mobType));
	}

	public Shape setShape(EntityPlayer player, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return setShape(player, entity, Shape.getShape(this, entity, mobType));
	}

	public Shape resetShape(EntityPlayer player, Entity entity) {
		final Shape shape = removeShape(entity);
		if (shape != null) {
			new ScheduledTask(entity.worldObj) { public void run() {
				shape.createOriginalEntity();
				shape.reattachPassenger();
			}}.scheduleSyncDelayed(CREATION_DELAY);
		}

		lastEntities.put(player, entity);

		return shape;
	}

	public Shape removeShape(Entity entity) {
		Shape shape = transmuted.remove(entity.entityId);
		if (shape != null)
			shape.deleteEntity();

		return shape;
	}

	Packet ignorePacket(Packet packet) {
		if(packet == null)
			throw new NullPointerException();
		transmutePacketListener.ignoredPackets.add(packet);
		return packet;
	}

	public Entity getLastTransmutedEntity(EntityPlayer player) {
		return lastEntities.get(player);
	}

	public Entity getClosestTransmutedEntity(EntityPlayer player) {
		final Location playerLocation = Utils.getLocation(player);

		double minDistanceSq = 20*20;
		Entity closest = null;

		for (Iterator<Map.Entry<Integer, Shape>> iterator = transmuted.entrySet().iterator(); iterator.hasNext(); ) {
			final Map.Entry<Integer, Shape> entry = iterator.next();
			final Shape shape = entry.getValue();

			final Entity entity = shape.entity;
			if (entity.isDead) {
				iterator.remove();
				continue;
			}

			final double distanceSq = playerLocation.distanceSq(Utils.getLocation(entity));
			if (distanceSq >= minDistanceSq)
				continue;

			minDistanceSq = distanceSq;
			closest = entity;
		}

		return closest;
	}

	@Override
	public void run() {
		for (Shape shape : transmuted.values()) {
			shape.tick();
		}
	}
}
