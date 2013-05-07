package eu.tomylobo.transmute;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet26EntityExpOrb;
import net.minecraft.util.MathHelper;

public class ExperienceOrbShape extends EntityShape {
	static {
		yOffsets[2] = 1.62;
	}

	public ExperienceOrbShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	@Override
	protected Packet createSpawnPacket() {
		final Packet26EntityExpOrb p26 = new Packet26EntityExpOrb();

		p26.entityId = entityId;

		p26.posX = MathHelper.floor_double(entity.posX * 32.0D);
		p26.posY = MathHelper.floor_double((entity.posY+yOffset) * 32.0D);
		p26.posZ = MathHelper.floor_double(entity.posZ * 32.0D);

		p26.xpValue = 1;

		return p26;
	}
}
