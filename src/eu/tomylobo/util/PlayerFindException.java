package eu.tomylobo.util;

import eu.tomylobo.commandsystem.CommandException;

public class PlayerFindException extends CommandException {
	private static final long serialVersionUID = 1L;

	public PlayerFindException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerFindException(String message) {
		super(message);
	}

	public PlayerFindException(Throwable cause) {
		super(cause);
	}

}
