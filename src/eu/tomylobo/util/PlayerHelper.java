package eu.tomylobo.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerHelper {
	public static void sendClientCommand(EntityPlayer ply, char command, String... args) { }

	// TOOD clean up on player leave
	public static Set<Map<EntityPlayer,?>> registeredMaps = new HashSet<Map<EntityPlayer,?>>();
	public static Set<Set<EntityPlayer>> registeredSets = new HashSet<Set<EntityPlayer>>();
	public static void registerMap(Map<EntityPlayer,?> map) {
		registeredMaps.add(map);
	}
	public static void registerSet(Set<EntityPlayer> set) {
		registeredSets.add(set);
	}

	public static int getPlayerLevel(ICommandSender commandSender) {
		return 0;
	}

	public static void sendDirectedMessage(ICommandSender commandSender, String msg, char colorCode) {
		commandSender.sendChatToPlayer("\u00a7"+colorCode+"[Transmute]\u00a7f " + msg);
	}

	public static void sendDirectedMessage(ICommandSender commandSender, String msg) {
		sendDirectedMessage(commandSender, msg, '5');
	}
}