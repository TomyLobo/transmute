package eu.tomylobo.transmute;

import eu.tomylobo.packetsystem.PacketSystem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

public class ItemShape extends VehicleShape {
	static {
		//yOffsets[1] = 1.62;
	}

	protected final ItemStack itemStack = new ItemStack(Block.cactus, 1, 0);

	public ItemShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	protected Packet createItemMetadataPacket() {
		return createMetadataPacket(10, itemStack.copy());
	}

	protected void sendMetadataPacket() {
		sendPacketToPlayersAround(transmute.ignorePacket(createItemMetadataPacket()));
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();
		sendMetadataPacket();
	}

	@Override
	public void createTransmutedEntity(EntityPlayer forPlayer) {
		super.createTransmutedEntity(forPlayer);
		PacketSystem.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createItemMetadataPacket()));
	}

	public int getType() {
		return itemStack.itemID;
	}

	public void setType(int type) {
		itemStack.itemID = type;

		sendMetadataPacket();
	}

	public int getDataValue() {
		return itemStack.getItemDamage();
	}

	public void setData(int data) {
		itemStack.setItemDamage(data);

		sendMetadataPacket();
	}

	public int getCount() {
		return itemStack.stackSize;
	}

	public void setCount(int count) {
		itemStack.stackSize = count;

		sendMetadataPacket();
	}

	public void setType(int type, int data) {
		itemStack.itemID = type;
		itemStack.setItemDamage(data);

		sendMetadataPacket();
	}

	public void setType(int type, int data, int count) {
		itemStack.itemID = type;
		itemStack.setItemDamage(data);
		itemStack.stackSize = count;

		sendMetadataPacket();
	}
}
