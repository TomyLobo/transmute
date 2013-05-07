package eu.tomylobo.packetsystem;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import eu.tomylobo.math.Location;
import eu.tomylobo.packetsystem.hackery.Hackery;
import eu.tomylobo.transmute.Transmute;
import eu.tomylobo.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;

@Mod(
	modid = "TLPacketSystem",
	name = "TomyLobo's packet system",
	version = "0.0.1"
)
public class PacketSystem {
	@Mod.ServerAboutToStart
	public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		Transmute.logger.info("TLPacketSystem trying to poke hooks...");
		Hackery.init(event.getServer());
		Transmute.logger.info("Success!");
	}

	private static final ArrayListMultimap<Integer,PacketListener> outgoing = ArrayListMultimap.create();
	private static final ArrayListMultimap<Integer,PacketListener> incoming = ArrayListMultimap.create();

	private static final int NPACKETS = 256;
	private static final PacketListener[][] outgoingBaked = new PacketListener[NPACKETS][];
	private static final PacketListener[][] incomingBaked = new PacketListener[NPACKETS][];

	public static final Packet handleOutgoingPacket(final EntityPlayerMP player, final Packet packet) {
		final int packetId = packet.getPacketId();

		for (final PacketListener handler : outgoingBaked[packetId]) {
			try {
				if (!handler.onOutgoingPacket(player, packetId, packet))
					return null;
			}
			catch (Throwable e) {
				e.printStackTrace();
				continue;
			}
		}
		return packet;
	}

	static void register(PacketListener packetListener, PacketListener.PacketDirection direction, int packetId) {
		switch (direction) {
		case OUTGOING:
			outgoing.put(packetId, packetListener);
			bake(outgoing, outgoingBaked);
			break;

		case INCOMING:
			incoming.put(packetId, packetListener); // TODO: Incoming PacketHandler
			bake(incoming, incomingBaked);
			break;
		}
	}

	private static void bake(ArrayListMultimap<Integer, PacketListener> multiMap, PacketListener[][] ret) {
		for (int i = 0; i < NPACKETS; ++i) {
			final List<PacketListener> handlers = multiMap.get(i);

			ret[i] = handlers.toArray(new PacketListener[handlers.size()]);
		}
	}

	public static void sendPacketToPlayer(EntityPlayer player, Packet packet) {
		PacketDispatcher.sendPacketToPlayer(packet, (cpw.mods.fml.common.network.Player) player);
	}

	public static void sendPacketToPlayersAround(Location location, double distance, Packet packet) {
		sendPacketToPlayersAround(location, distance, packet, null);
	}

	public static void sendPacketToPlayersAround(Location location, double distance, Packet packet, EntityPlayer except) {
		final double distanceSq = distance * distance;
		for (Object obj : location.getWorld().playerEntities) {
			final EntityPlayer player = (EntityPlayer) obj;
			if (player == except)
				continue;

			final Location playerLocation = Utils.getLocation(player);
			if (location.distanceSq(playerLocation) > distanceSq)
				continue;

			sendPacketToPlayer(player, packet);
		}
	}
}
