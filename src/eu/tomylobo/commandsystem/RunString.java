package eu.tomylobo.commandsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;

public class RunString {
	private static final Pattern commandPattern = Pattern.compile("^([^ ]+).*$");
	private final List<String> commands;
	private final String cleanString;

	public RunString(List<String> commands) {
		this.commands = commands;

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String command : commands) {

			if (!first)
				sb.append("\u00a7c; \u00a79");
			first = false;

			sb.append(command);
		}

		cleanString = sb.toString();
	}

	public RunString(String commandString, Set<String> filter) throws CommandException {
		this(parseCommandString(commandString, filter));
	}

	private static List<String> parseCommandString(String commandString, Set<String> filter) throws CommandException {
		List<String> commands = new ArrayList<String>();

		for (String command : commandString.split(";")) {
			command = command.trim();
			if (command.charAt(0) == '/')
				command = command.substring(1);

			final Matcher commandMatcher = commandPattern.matcher(command);

			if (!commandMatcher.matches())
				continue;

			final String commandName = commandMatcher.group(1);
			if (filter.contains(commandName))
				throw new CommandException("Command \u00a79"+commandName+"\u00a7f cannot be bound.");

			commands.add(command);
		}
		return commands;
	}

	public String getCleanString() {
		return cleanString;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void run(ICommandSender commandSender) {
		final ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();

		for (String command : commands) {
			commandManager.executeCommand(commandSender, command);
		}
	}
}
