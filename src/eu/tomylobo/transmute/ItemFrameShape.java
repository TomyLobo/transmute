package eu.tomylobo.transmute;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;

public class ItemFrameShape extends ItemShape {
	protected byte orientation = 0;

	public ItemFrameShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = false;
	}

	@Override
	protected Packet createItemMetadataPacket() {
		try {
			datawatcher.addObject(3, (byte)1);
			datawatcher.updateObject(3, (byte)0);
		} catch (Exception e) { }

		datawatcher.updateObject(3, orientation);
		return createMetadataPacket(2, itemStack.copy());
	}

	public byte getOrientation() {
		return orientation;
	}

	public void setOrientation(byte orientation) {
		this.orientation = orientation;

		sendMetadataPacket();
	}
}
