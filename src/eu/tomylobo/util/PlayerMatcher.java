package eu.tomylobo.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.FMLCommonHandler;

public class PlayerMatcher {
	private static EntityPlayer literalMatch(String name) throws PlayerNotFoundException {
		final ICommandSender commandSender = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayer onlinePlayer = PlayerSelector.matchOnePlayer(commandSender, name);
		if (onlinePlayer != null)
			return onlinePlayer;

		//return new OfflinePlayer(plugin.getServer(), name);
		throw new PlayerNotFoundException();
	}

	private static final Pattern quotePattern = Pattern.compile("^\"(.*)\"$");
	public static EntityPlayer matchPlayerSingle(String subString, boolean implicitlyLiteral) throws PlayerNotFoundException, MultiplePlayersFoundException {
		if (implicitlyLiteral)
			return literalMatch(subString);

		Matcher matcher = quotePattern.matcher(subString);

		if (matcher.matches())
			return literalMatch(matcher.group(1));

		final ICommandSender commandSender = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayerMP[] players = PlayerSelector.matchPlayers(commandSender, subString);

		int c = players.length;
		if (c < 1)
			throw new PlayerNotFoundException();

		if (c > 1)
			throw new MultiplePlayersFoundException(Arrays.asList(players));

		return players[0];
	}

	public static EntityPlayer matchPlayerSingle(String subString) throws PlayerNotFoundException, MultiplePlayersFoundException {
		return matchPlayerSingle(subString, false);
	}
}