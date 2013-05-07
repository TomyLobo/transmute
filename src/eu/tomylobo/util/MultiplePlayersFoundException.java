package eu.tomylobo.util;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class MultiplePlayersFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;
	private List<? extends EntityPlayer> players;

	public MultiplePlayersFoundException(List<? extends EntityPlayer> players) {
		super("Sorry, multiple players found!");
		this.players = players;
	}

	public List<? extends EntityPlayer> getPlayers() {
		return players;
	}
}
