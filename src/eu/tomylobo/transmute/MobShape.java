package eu.tomylobo.transmute;


import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet24MobSpawn;

public class MobShape extends EntityShape {
	static {
		yawOffsets[63] = 180;
	}

	public MobShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		switch (mobType) {
		case 56: // Ghast
		case 63: // EnderDragon
		case 94: // Squid
			dropping = false;
			break;

		default:
			dropping = true;
		}
	}

	@Override
	protected Packet createSpawnPacket() {
		final Packet24MobSpawn p24 = new Packet24MobSpawn();

		p24.entityId = entityId;
		p24.type = (byte) mobType;
		p24.xPosition = MathHelper.floor_double(entity.posX * 32.0D);
		p24.yPosition = MathHelper.floor_double((entity.posY+yOffset) * 32.0D);
		p24.zPosition = MathHelper.floor_double(entity.posZ * 32.0D);
		p24.yaw = (byte) ((int) ((entity.rotationYaw+yawOffset) * 256.0F / 360.0F));
		p24.pitch = (byte) ((int) (entity.rotationPitch * 256.0F / 360.0F));
		p24.headYaw = p24.yaw;
		//p24.i = 
		//p24.j = 
		//p24.k = 
		ObfuscationReflectionHelper.setPrivateValue(Packet24MobSpawn.class, p24, datawatcher, "s", "metaData");
		return p24;
	}

	@Override
	public boolean onOutgoingPacket(EntityPlayer ply, int packetID, Packet packet) {
		return packetID == 22 || super.onOutgoingPacket(ply, packetID, packet);
	}
}
