package eu.tomylobo.transmute.listeners;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import eu.tomylobo.transmute.Shape;
import eu.tomylobo.transmute.Transmute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

public class TransmutePlayerListener implements IConnectionHandler {
	final Transmute transmute;

	public TransmutePlayerListener(Transmute transmute) {
		this.transmute = transmute;
		NetworkRegistry.instance().registerConnectionHandler(this);
	}

	Map<INetworkManager, EntityPlayer> managerMap = new HashMap<INetworkManager, EntityPlayer>();

	/**
	 * Called when a player logs into the server
	 *  SERVER SIDE
	 *
	 * @param player
	 * @param netHandler
	 * @param manager
	 */
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		final EntityPlayer notchPlayer = (EntityPlayer) player;

		managerMap.put(manager, notchPlayer);

		Shape shape = transmute.getShape(notchPlayer);

		if (shape == null)
			return;

		Transmute.logger.warning("Rejoined with shape - this shouldn't happen.");
		//shape.rejoin();
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) { return null; }

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		// maybe...
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		final EntityPlayer player = managerMap.remove(manager);
		if (player == null)
			return; // server query (?)

		transmute.removeShape(player);
	}

	// client-only stuff
	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { }

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) { }
}
