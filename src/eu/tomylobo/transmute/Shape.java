package eu.tomylobo.transmute;

import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.packetsystem.PacketSystem;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet40EntityMetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Shape {
	protected final Transmute transmute;
	protected final int entityId;
	protected final Entity entity;
	protected final DataWatcher datawatcher;

	protected Shape(Transmute transmute, Entity entity) {
		this.transmute = transmute;
		this.entity = entity;
		entityId = entity.entityId;
		datawatcher = new DataWatcher();
		datawatcher.addObject(31, "");
	}

	public void sendPacketToPlayersAround(Packet packet) {
		if (entity instanceof EntityPlayer)
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, packet, (EntityPlayer) entity);
		else
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, packet);
	}

	public void deleteEntity() {
		sendPacketToPlayersAround(new Packet29DestroyEntity(entity.entityId));
	}

	public void createOriginalEntity() {
		if (entity instanceof EntityPlayer)
			PlayerHelper.sendClientCommand((EntityPlayer) entity, 't', "");

		sendPacketToPlayersAround(transmute.ignorePacket(createOriginalSpawnPacket()));
	}

	private static final Method methodEntityTrackerEntry_b;
	static {
		try {
			methodEntityTrackerEntry_b = EntityTrackerEntry.class.getDeclaredMethod("b");
			methodEntityTrackerEntry_b.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private Packet createOriginalSpawnPacket() {
		EntityTrackerEntry ete = new EntityTrackerEntry(entity, 0, 0, false);

		try {
			return (Packet) methodEntityTrackerEntry_b.invoke(ete);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;

			throw new RuntimeException(cause);
		}
	}

	public byte getDataByte(int index) {
		try {
			return datawatcher.getWatchableObjectByte(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public int getDataInteger(int index) {
		try {
			return datawatcher.getWatchableObjectInt(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public String getDataString(int index) {
		try {
			return datawatcher.getWatchableObjectString(index);
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	public void setData(int index, Object value) {
		Packet40EntityMetadata p40 = createMetadataPacket(index, value);

		if (entity instanceof EntityPlayer) {
			sendYCData(index, value);
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, transmute.ignorePacket(p40), (EntityPlayer) entity);
		}
		else {
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, transmute.ignorePacket(p40));
		}
	}

	public void sendYCData(int index, Object value) {
		if (entity instanceof EntityPlayer)
			PlayerHelper.sendClientCommand((EntityPlayer) entity, 'd', index+"|"+value.getClass().getCanonicalName()+"|"+value);
	}

	protected Packet40EntityMetadata createMetadataPacket(int index, Object value) {
		if(value instanceof ItemStack) {
			try {
				// create entry
				datawatcher.addObject(index, new ItemStack(Block.endPortal, 2, 2));
				// mark dirty
				datawatcher.updateObject(index, new ItemStack(Block.endPortal, 1, 1));
			} catch (Exception e) {	}
		} else {
			try {
				// create entry
				datawatcher.addObject(index, value.getClass().getConstructor(String.class).newInstance("0"));
				// mark dirty
				datawatcher.updateObject(index, value.getClass().getConstructor(String.class).newInstance("1"));
			}
			catch (Exception e) { }
		}

		// put the actual data in
		datawatcher.updateObject(index, value);

		return new Packet40EntityMetadata(entityId, datawatcher, false);
	}

	public abstract void createTransmutedEntity();
	public abstract void createTransmutedEntity(EntityPlayer forPlayer);

	public abstract void runAction(EntityPlayer player, String action) throws CommandException;

	public static Shape getShape(Transmute transmute, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, entity, MyEntityTypes.typeNameToClass(mobType));
	}

	public static Shape getShape(Transmute transmute, Entity entity, Class<? extends net.minecraft.entity.Entity> mobType) throws EntityTypeNotFoundException {
		final int id = MyEntityTypes.classToId(mobType);
		if (EntityLiving.class.isAssignableFrom(mobType)) {
			return getShapeImpl(transmute, entity, id, MobShape.class);
		}

		/*
		 from: "    a\(.*\.class, "(.*)", (.*)\);"
		 to: case \2: // \1
		 */
		switch (id) {
		case 18: // ItemFrame
			return getShapeImpl(transmute, entity, id, ItemFrameShape.class);

		case 1: // Item
			return getShapeImpl(transmute, entity, id, ItemShape.class);

		case 2: // XPOrb
			return getShapeImpl(transmute, entity, id, ExperienceOrbShape.class);

		case 9: // Painting
			return getShapeImpl(transmute, entity, id, PaintingShape.class);

		case 10: // Arrow
		case 11: // Snowball
		case 12: // Fireball
		case 13: // SmallFireball
		case 14: // ThrownEnderpearl
		case 15: // EyeOfEnderSignal
		case 16: // ThrownPotion
		//case 17: // ThrownExpBottle
		//case 19: // WitherSkull
		case 20: // PrimedTnt
		case 21: // FallingSand
		case 40: // Minecart
		case 41: // Boat
		case 200: // EnderCrystal
		case 1000: // FishingHook
		case 1001: // Potion
		case 1002: // Egg
			return getShapeImpl(transmute, entity, id, VehicleShape.class);

		default:
			throw new RuntimeException("Invalid shape.");
		}
	}

	public static Shape getShape(Transmute transmute, Entity entity, int mobType) throws EntityTypeNotFoundException {
		return getShape(transmute, entity, MyEntityTypes.idToClass(mobType));
	}

	private static Shape getShapeImpl(Transmute transmute, Entity entity, int mobType, Class<? extends Shape> shapeClass) {
		try {
			return shapeClass.getConstructor(Transmute.class, Entity.class, int.class).newInstance(transmute, entity, mobType);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating shape.", e);
		}
	}


	public void reattachPassenger() {
		Entity passenger = entity.riddenByEntity;
		Entity vehicle = entity.ridingEntity;

		if (passenger != null)
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, new Packet39AttachEntity(passenger, entity));

		if (vehicle != null)
			PacketSystem.sendPacketToPlayersAround(Utils.getLocation(entity), 1024, new Packet39AttachEntity(entity, vehicle));
	}

	public abstract boolean onOutgoingPacket(EntityPlayer ply, int packetID, Packet packet);

	public abstract void tick();
}
