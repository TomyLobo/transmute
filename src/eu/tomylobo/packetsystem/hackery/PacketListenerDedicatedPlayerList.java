package eu.tomylobo.packetsystem.hackery;

import eu.tomylobo.util.ReflectionUtils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.server.dedicated.DedicatedPlayerList;

public class PacketListenerDedicatedPlayerList extends DedicatedPlayerList {
	public PacketListenerDedicatedPlayerList(DedicatedPlayerList wrapped) {
		super(wrapped.getDedicatedServerInstance());

		ReflectionUtils.cloneFieldByField(DedicatedPlayerList.class, wrapped, this);
	}

	@Override
	public void initializeConnectionToPlayer(INetworkManager par1iNetworkManager, EntityPlayerMP par2EntityPlayerMP) {
		final PacketListenerNetworkManager networkManager;
		if (par1iNetworkManager instanceof PacketListenerNetworkManager)
			networkManager = (PacketListenerNetworkManager) par1iNetworkManager;
		else
			networkManager = new PacketListenerNetworkManager(par1iNetworkManager, par2EntityPlayerMP);

		super.initializeConnectionToPlayer(networkManager, par2EntityPlayerMP);
	}
}
