package eu.tomylobo.packetsystem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

public class PacketListener {

	public enum PacketDirection {
		OUTGOING, INCOMING
	}

	public void register(PacketDirection direction, int packetId) {
		PacketSystem.register(this, direction, packetId);
	}

	public boolean onOutgoingPacket(EntityPlayer player, int packetId, Packet packet) {
		return false;
	}
}
