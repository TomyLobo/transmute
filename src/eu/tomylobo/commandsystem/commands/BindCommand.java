package eu.tomylobo.commandsystem.commands;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.commandsystem.ICommand;
import eu.tomylobo.commandsystem.RunString;
import eu.tomylobo.commandsystem.ToolBind;
import eu.tomylobo.commandsystem.ICommand.*;
import eu.tomylobo.transmute.Materials;
import eu.tomylobo.util.PlayerHelper;

@Names("bind")
@Help(
		"Binds a command to your current tool. The leading slash\n" +
		"is optional. Unbind by typing '/bind' without arguments.\n" +
		"Flags:\n" +
		"  -l lists your current binds.\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("-l|[-i <item name or id>][<command>[;<command>[;<command> ...]]]")
@Permission("yiffbukkit.bind")
@BooleanFlags("lx")
@StringFlags("i")
public class BindCommand extends ICommand {
	private static final Set<String> filter = new HashSet<String>();

	static {
		filter.add("/pm");
		filter.add("/msg");
		filter.add("/emote");
		filter.add("/say");
		filter.add("/me");
		filter.add("/emote");
		filter.add("/throw");
		filter.add("/bind");
	}

	@Override
	public void Run(EntityPlayer ply, String[] args, String argStr) throws CommandException {
		argStr = parseFlags(argStr).trim();

		if (booleanFlags.contains('l')) {
			String playerName = ply.getEntityName();
			for (Entry<String, ToolBind> entry : ToolBind.list(playerName).entrySet()) {
				ToolBind toolBind = entry.getValue();
				String toolName = entry.getKey();

				PlayerHelper.sendDirectedMessage(ply, "\u00a7e"+toolName+"\u00a7f => \u00a79"+toolBind.name);
			}
			return;
		}

		Item toolType;
		if (stringFlags.containsKey('i')) {
			final String materialName = stringFlags.get('i');

			toolType = Materials.matchMaterial(materialName);
			if (toolType == null)
				throw new CommandException("Material "+materialName+" not found");
		}
		else if (!argStr.isEmpty() && argStr.charAt(0) == '-') {
			throw new CommandException("Invalid flag specified");
		}
		else {
			toolType = ply.getHeldItem().getItem();
		}

		boolean left = booleanFlags.contains('x');

		if (argStr.isEmpty()) {
			unbind(ply, toolType, left);
			return;
		}

		final RunString parsedCommands = new RunString(argStr, filter);

		final ToolBind toolBind = new ToolBind(parsedCommands.getCleanString(), ply) {
			@Override
			public boolean run(PlayerInteractEvent event) {
				parsedCommands.run(event.entityPlayer);

				return true;
			}
		};

		ToolBind.add(ply, toolType, left, toolBind);

		PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+parsedCommands.getCleanString()+"\u00a7f to your tool (\u00a7e"+toolType.getItemName()+"\u00a7f). Right-click to use.");
	}

	public static void unbind(EntityPlayer ply, Item toolType, boolean left) {
		if (ToolBind.remove(ply, toolType, left)) {
			PlayerHelper.sendDirectedMessage(ply, "Unbound your tool (\u00a7e"+toolType.getItemName()+"\u00a7f).");
		}
		else {
			PlayerHelper.sendDirectedMessage(ply, "Your tool (\u00a7e"+toolType.getItemName()+"\u00a7f) was not bound.");
		}
	}
}
