package eu.tomylobo.transmute;

import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.packetsystem.PacketSystem;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.util.MathHelper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EntityShape extends Shape {
	protected static double[] yOffsets = new double[1024];
	protected static float[] yawOffsets = new float[1024];

	protected int mobType;
	private Map<String, ShapeAction> actions;

	protected float yawOffset = 0;
	protected double yOffset = 0;
	protected boolean dropping = false;

	public EntityShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity);

		this.mobType = mobType;
		actions = ShapeActions.get(mobType);

		yOffset = yOffsets[mobType];
		yawOffset = yawOffsets[mobType];

		try {
			Class<? extends Entity> entityClass = entity.getClass();
			int entityMobType = MyEntityTypes.classToId(entityClass);

			yOffset -= yOffsets[entityMobType];
			yawOffset -= yawOffsets[entityMobType];
		}
		catch (EntityTypeNotFoundException e) {
		}

		yOffset += 0.015625D;
	}

	@Override
	public void createTransmutedEntity() {
		sendPacketToPlayersAround(transmute.ignorePacket(createSpawnPacket()));

		// TODO: send datawatcher to players around

		if (entity instanceof EntityPlayer) {
			try {
				String typeName = MyEntityTypes.classToTypeName(MyEntityTypes.idToClass(mobType));
				PlayerHelper.sendClientCommand((EntityPlayer) entity, 't', typeName+"|"+yawOffset+"|"+yOffset);
			}
			catch (EntityTypeNotFoundException e) {
			}
		}
	}

	@Override
	public void createTransmutedEntity(EntityPlayer forPlayer) {
		PacketSystem.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createSpawnPacket()));
		// TODO: send datawatcher to player
	}

	protected abstract Packet createSpawnPacket();

	private static final Pattern commandPattern = Pattern.compile("^([^ ]+) (.+)?$");

	@Override
	public void runAction(EntityPlayer player, String action) throws CommandException {
		final Matcher matcher = commandPattern.matcher(action);

		final String actionName;
		final String argStr;
		final String[] args;
		if (matcher.matches()) {
			actionName = matcher.group(1);
			argStr = matcher.group(2);
			args = argStr.split(" +");
		}
		else {
			actionName = action.trim();
			argStr = "";
			args = new String[0];
		}

		runAction(player, actionName, args, argStr);
	}

	protected void runAction(EntityPlayer player, final String actionName, final String[] args, final String argStr) throws CommandException {
		if (actions == null)
			throw new CommandException("No actions defined for your current shape.");

		ShapeAction mobAction = actions.get(actionName);
		if (mobAction == null) {
			mobAction = actions.get("help");
			if (mobAction == null)
				throw new CommandException("No action named '"+actionName+"' defined for your current shape.");

			mobAction.run(this, player, new String[] { "" }, "");
			return;
		}

		mobAction.run(this, player, args, argStr);
	}

	@Override
	public boolean onOutgoingPacket(EntityPlayer ply, int packetID, Packet packet) {
		if (ply == entity)
			return true;

		switch (packetID) {
		case 18:
			return ((Packet18Animation) packet).animate == 2;

		case 22:
			return false; // will be overridden in MobShape

		//case 30:
		//case 31:
		case 32:
		case 33:
			Packet30Entity p30 = (Packet30Entity) packet;
			p30.yaw += (byte) ((int) (yawOffset * 256.0F / 360.0F));

			return true;

		case 34:
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			p34.yPosition = MathHelper.floor_double((entity.posY+yOffset) * 32.0D);
			p34.yaw = (byte) ((int) ((entity.rotationYaw+yawOffset) * 256.0F / 360.0F));
			//p34.c += (int)(yOffset * 32.0);
			//p34.e += (byte) ((int) (yawOffset * 256.0F / 360.0F));

			return true;

		default:
			return true;
		}
	}

	@Override
	public void tick() {
		if (!dropping)
			return;

		if (yOffset == 0) {
			if (Math.IEEEremainder(entity.posY, 1.0) < 0.00001) {
				final int blockX = Utils.locToBlock(entity.posX);
				final int blockY = Utils.locToBlock(entity.posY) - 1;
				final int blockZ = Utils.locToBlock(entity.posZ);

				final int id = entity.worldObj.getBlockId(blockX, blockY, blockZ);

				final Block block = Block.blocksList[id];
				if (!block.isCollidable())
					return;
			}
		}

		sendPacketToPlayersAround(new Packet34EntityTeleport(entity));
		sendPacketToPlayersAround(new Packet28EntityVelocity(entityId, entity.motionX, entity.motionY, entity.motionZ));
	}

	public double getYOffset() {
		return yOffset;
	}

	public void sendEntityStatus(byte status) {
		sendPacketToPlayersAround(new Packet38EntityStatus(entityId, status));
		sendYCData(ShapeYCData.ENTITY_STATUS, status);
	}

	@Override
	public String toString() {
		return String.format("EntityShape(%s)", this.entity);
	}
}
