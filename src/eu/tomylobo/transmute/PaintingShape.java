package eu.tomylobo.transmute;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet34EntityTeleport;

public class PaintingShape extends EntityShape {
	static {
		yOffsets[9] = 1.62;
		yawOffsets[9] = 180;
	}

	private String paintingName = "Kebab";

	public PaintingShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();

		sendYCData(ShapeYCData.PAINTING_NAME, paintingName);
	}

	@Override
	protected Packet createSpawnPacket() {
		final Packet25EntityPainting p25 = new Packet25EntityPainting();

		p25.entityId = entityId;

		p25.xPosition = (int) entity.posX;
		p25.yPosition = (int)(entity.posY + yOffset);
		p25.zPosition = (int) entity.posZ;
		p25.direction = 0;
		p25.title = paintingName;

		return p25;
	}

	public void setPaintingName(String paintingName) {
		this.paintingName = paintingName;

		deleteEntity();
		createTransmutedEntity();
	}

	public String getPaintingName() {
		return paintingName;
	}

	@Override
	public boolean onOutgoingPacket(EntityPlayer ply, int packetID, Packet packet) {
		if (!super.onOutgoingPacket(ply, packetID, packet))
			return false;

		switch (packetID) {
		//case 30:
		//case 31:
		case 32:
		case 33:
			return false;

		case 34:
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			p34.yaw = (byte) -p34.yaw;
			return true;
		}

		return true;
	}
}
