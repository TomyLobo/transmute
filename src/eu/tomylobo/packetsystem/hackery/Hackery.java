package eu.tomylobo.packetsystem.hackery;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.management.ServerConfigurationManager;

public class Hackery {
	public static void init(MinecraftServer mc) {
		final ServerConfigurationManager oldConfigurationManager = mc.getConfigurationManager();
		final ServerConfigurationManager newConfigurationManager = wrapServerConfigurationManager(oldConfigurationManager);
		mc.setConfigurationManager(newConfigurationManager);
	}

	private static ServerConfigurationManager wrapServerConfigurationManager(ServerConfigurationManager oldConfigurationManager) {
		if (oldConfigurationManager == null)
			throw new RuntimeException("ServerConfigurationManager does not exist yet!");

		return new PacketListenerDedicatedPlayerList((DedicatedPlayerList) oldConfigurationManager);
	}
}
