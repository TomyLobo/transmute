package eu.tomylobo.transmute;

import eu.tomylobo.commandsystem.CommandException;

public class EntityTypeNotFoundException extends CommandException {
	private static final long serialVersionUID = 1L;

	public EntityTypeNotFoundException() {
		super("Entity type not found.");
	}

	public EntityTypeNotFoundException(Throwable cause) {
		super("Entity type not found.", cause);
	}
}
