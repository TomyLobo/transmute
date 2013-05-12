package eu.tomylobo.transmute.commands;

import eu.tomylobo.commandsystem.ICommand;
import eu.tomylobo.commandsystem.ICommand.*;
import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.scheduler.Scheduler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

@Names("trtest")
@Permission("transmute.test")
public class TestCommand extends ICommand {
	@Override
	public void Run(EntityPlayer ply, String[] args, String argStr) throws CommandException {
		if (argStr.equals("sched")) {
			final EntityPlayer ply2 = ply;
			ply2.sendChatToPlayer("start");
			Scheduler.instance.getWorldScheduler(ply.worldObj).scheduleSyncDelayedTask(new Runnable() { public void run() {
				ply2.sendChatToPlayer("start+3s");
			}}, 20*3);
		}

		if (args[0].equals("pl")) {
			final EntityPlayerMP ply2 = (EntityPlayerMP) ply;
			final MinecraftServer minecraftServer = ply2.mcServer;
			final ServerConfigurationManager serverConfigurationManager = MinecraftServer.getServerConfigurationManager(minecraftServer);
			final NetServerHandler netServerHandler = ply2.playerNetServerHandler;
			final INetworkManager networkManager = netServerHandler.netManager;

			ply.sendChatToPlayer(String.format("minecraftServer.serverConfigManager = %s", serverConfigurationManager));
			ply.sendChatToPlayer(String.format("ply2.playerNetServerHandler = %s", netServerHandler));
			ply.sendChatToPlayer(String.format("netServerHandler.netManager = %s", networkManager));
		}
	}
}
