package eu.tomylobo.commandsystem;

import eu.tomylobo.transmute.Transmute;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ICommand {
	@Retention(RetentionPolicy.RUNTIME) public @interface Names { String[] value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Help { String value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Usage { String value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Level { int value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Permission { String value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Cost { double value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface Disabled { }
	@Retention(RetentionPolicy.RUNTIME) public @interface BooleanFlags { String value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface StringFlags { String value(); }
	@Retention(RetentionPolicy.RUNTIME) public @interface NumericFlags { String value(); }

	public static enum FlagType {
		BOOLEAN, STRING, NUMERIC
	}

	private final Map<Character,FlagType> flagTypes = new HashMap<Character,FlagType>();

	protected final Set<Character> booleanFlags = new HashSet<Character>();
	protected final Map<Character,String> stringFlags = new HashMap<Character,String>();
	protected final Map<Character,Double> numericFlags = new HashMap<Character,Double>();

	protected ICommand() {
		if (this.getClass().getAnnotation(Disabled.class) != null)
			return;

		final Names namesAnnotation = this.getClass().getAnnotation(Names.class);
		if (namesAnnotation != null) {
			for (String name : namesAnnotation.value()) {
				CommandSystem.instance.registerCommand(name, this);
			}
		}

		parseFlagsAnnotations();
	}

	private void parseFlagsAnnotations() {
		final BooleanFlags booleanFlagsAnnotation = this.getClass().getAnnotation(BooleanFlags.class);
		if (booleanFlagsAnnotation != null) {
			parseFlagsAnnotation(booleanFlagsAnnotation.value(), FlagType.BOOLEAN);
		}

		final StringFlags stringFlagsAnnotation = this.getClass().getAnnotation(StringFlags.class);
		if (stringFlagsAnnotation != null) {
			parseFlagsAnnotation(stringFlagsAnnotation.value(), FlagType.STRING);
		}

		final NumericFlags numericFlagsAnnotation = this.getClass().getAnnotation(NumericFlags.class);
		if (numericFlagsAnnotation != null) {
			parseFlagsAnnotation(numericFlagsAnnotation.value(), FlagType.NUMERIC);
		}
	}

	private void parseFlagsAnnotation(final String flags, final FlagType flagType) {
		for (int i = 0; i < flags.length(); ++i) {
			flagTypes.put(flags.charAt(i), flagType);
		}
	}

	protected String parseFlags(String argStr) throws CommandException {
		if (argStr.trim().isEmpty()) {
			booleanFlags.clear();
			stringFlags.clear();
			numericFlags.clear();
			return argStr;
		}

		String[] args = argStr.split(" ");

		args = parseFlags(args);

		if (args.length == 0)
			return "";

		StringBuilder sb = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; ++i) {
			sb.append(' ');
			sb.append(args[i]);
		}

		return sb.toString();
	}

	protected String[] parseFlags(String[] args) throws CommandException {
		int nextArg = 0;

		parseFlagsAnnotations();
		booleanFlags.clear();
		stringFlags.clear();
		numericFlags.clear();

		while (nextArg < args.length) {
			// Fetch argument
			final String arg = args[nextArg++];

			// Empty argument? (multiple consecutive spaces)
			if (arg.isEmpty())
				continue;

			// No more flags?
			if (arg.charAt(0) != '-' || arg.length() == 1) {
				--nextArg;
				break;
			}

			// Handle flag parsing terminator --
			if (arg.equals("--"))
				break;

			if (!Character.isLetter(arg.charAt(1))) {
				--nextArg;
				break;
			}

			// Go through the flags
			for (int i = 1; i < arg.length(); ++i) {
				final char flagName = arg.charAt(i);

				final FlagType flagType = flagTypes.get(flagName);
				if (flagType == null)
					throw new CommandException("Invalid flag '"+flagName+"' specified.");

				switch (flagType) {
				case BOOLEAN:
					booleanFlags.add(flagName);
					break;

				case STRING:
					// Skip empty arguments...
					while (nextArg < args.length && args[nextArg].isEmpty())
						++nextArg;

					if (nextArg >= args.length)
						throw new CommandException("No value specified for "+flagName+" flag.");

					stringFlags.put(flagName, args[nextArg++]);
					break;

				case NUMERIC:
					// Skip empty arguments...
					while (nextArg < args.length && args[nextArg].isEmpty())
						++nextArg;

					if (nextArg >= args.length)
						throw new CommandException("No value specified for "+flagName+" flag.");

					numericFlags.put(flagName, Double.parseDouble(args[nextArg++]));
					break;
				}
			}
		}

		return Arrays.copyOfRange(args, nextArg, args.length);
	}

	public final int getMinLevel() {
		final Level levelAnnotation = this.getClass().getAnnotation(Level.class);
		if (levelAnnotation == null)
			throw new UnsupportedOperationException("You need either a GetMinLevel method or an @Level annotation.");

		return levelAnnotation.value();
	}

	public void Run(EntityPlayer ply, String[] args, String argStr) throws CommandException {

	}
	public void run(ICommandSender commandSender, String[] args, String argStr) throws CommandException {

		Run(asPlayer(commandSender), args, argStr);
	}

	public static EntityPlayer asPlayer(ICommandSender commandSender) throws CommandException {
		if (!(commandSender instanceof EntityPlayer))
			throw new CommandException("This command can only be run as a player.");

		return (EntityPlayer) commandSender;
	}

	public final String getHelp() {
		final Help helpAnnotation = this.getClass().getAnnotation(Help.class);
		if (helpAnnotation == null)
			return "";

		return helpAnnotation.value();
	}
	public final String getUsage() {
		final Usage usageAnnotation = this.getClass().getAnnotation(Usage.class);
		if (usageAnnotation == null)
			return "";

		return usageAnnotation.value();
	}

	public boolean canPlayerUseCommand(ICommandSender commandSender) {
		final Permission permissionAnnotation = this.getClass().getAnnotation(Permission.class);
		if (permissionAnnotation != null)
			return Utils.hasPermission(commandSender, permissionAnnotation.value());

		final int plylvl = PlayerHelper.getPlayerLevel(commandSender);
		final int reqlvl = getMinLevel();

		return plylvl >= reqlvl;
	}

	static void discover(FMLPreInitializationEvent event) {
		for (ASMData asm : event.getAsmData().getAll(ICommand.Names.class.getName())) {
			Transmute.logger.info("TLCommandSystem found command class "+asm.getClassName());

			try {
				Class.forName(asm.getClassName()).newInstance();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
