package eu.tomylobo.packetsystem.hackery;

import java.net.SocketAddress;

import eu.tomylobo.packetsystem.PacketSystem;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public class PacketListenerNetworkManager implements INetworkManager {
	private final INetworkManager wrapped;
	private final EntityPlayerMP player;

	public PacketListenerNetworkManager(INetworkManager wrapped, EntityPlayerMP player) {
		this.wrapped = wrapped;
		this.player = player;
	}

	@Override
	public void addToSendQueue(Packet var1) {
		var1 = PacketSystem.handleOutgoingPacket(player, var1);
		if (var1 == null)
			return;

		wrapped.addToSendQueue(var1);
	}

	// pass the rest on to the wrapped NM
	@Override
	public void wakeThreads() {
		wrapped.wakeThreads();
	}

	@Override
	public void setNetHandler(NetHandler handler) {
		wrapped.setNetHandler(handler);
	}

	@Override
	public void serverShutdown() {
		wrapped.serverShutdown();
	}

	@Override
	public void processReadPackets() {
		wrapped.processReadPackets();
	}

	@Override
	public int packetSize() {
		return wrapped.packetSize();
	}

	@Override
	public void networkShutdown(String var1, Object... var2) {
		wrapped.networkShutdown(var1, var2);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return wrapped.getSocketAddress();
	}

	@Override
	public void closeConnections() {
		wrapped.closeConnections();
	}
}