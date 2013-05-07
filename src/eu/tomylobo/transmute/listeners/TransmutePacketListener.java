package eu.tomylobo.transmute.listeners;

import eu.tomylobo.packetsystem.PacketListener;
import eu.tomylobo.transmute.Shape;
import eu.tomylobo.transmute.Transmute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet40EntityMetadata;

import java.util.HashSet;
import java.util.Set;

public class TransmutePacketListener extends PacketListener {
	private final Transmute transmute;
	public final Set<Packet> ignoredPackets = new HashSet<Packet>();

	public TransmutePacketListener(Transmute transmute) {
		super();
		this.transmute = transmute;

		register(PacketDirection.OUTGOING, 17);
		register(PacketDirection.OUTGOING, 18);

		register(PacketDirection.OUTGOING, 20);
		register(PacketDirection.OUTGOING, 22);
		register(PacketDirection.OUTGOING, 23);
		register(PacketDirection.OUTGOING, 24);

		register(PacketDirection.OUTGOING, 32);
		register(PacketDirection.OUTGOING, 33);
		register(PacketDirection.OUTGOING, 34);
		register(PacketDirection.OUTGOING, 40);
	}

	@Override
	public boolean onOutgoingPacket(final EntityPlayer player, int packetId, final Packet packet) {
		if (ignoredPackets.contains(packet))
			return true;

		final int entityId;

		switch (packetId) {
		case 17:
			return !transmute.isTransmuted(((Packet17Sleep) packet).entityID);

		case 18:
			entityId = ((Packet18Animation) packet).entityId;
			break;

		case 20:
			return handleSpawn(player, ((Packet20NamedEntitySpawn) packet).entityId);

		case 22:
			entityId = ((Packet22Collect) packet).collectorEntityId;
			break;

		case 23:
			return handleSpawn(player, ((Packet23VehicleSpawn) packet).entityId);

		case 24:
			return handleSpawn(player, ((Packet24MobSpawn) packet).entityId);

		//case 30:
		//case 31:
		case 32:
		case 33:
			entityId = ((Packet30Entity) packet).entityId;
			break;

		case 34:
			entityId = ((Packet34EntityTeleport) packet).entityId;
			break;

		case 40:
			return !transmute.isTransmuted(((Packet40EntityMetadata) packet).entityId);

		default:
			return true;
		}

		final Shape shape = transmute.getShape(entityId);
		if (shape == null)
			return true;

		return shape.onOutgoingPacket(player, packetId, packet);
	}

	private boolean handleSpawn(final EntityPlayer ply, final int entityId) {
		final Shape shape = transmute.getShape(entityId);
		if (shape == null)
			return true;

		shape.createTransmutedEntity(ply);

		return false;
	}

	/*private Entity getEntityFromID(final Player ply, final int entityId) {
		return ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityId);
	}*/
}
