package eu.tomylobo.commandsystem;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import eu.tomylobo.commandsystem.commands.BindCommand;
import eu.tomylobo.transmute.Transmute;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.StringUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod(
	modid = "TLCommandSystem",
	name = "TomyLobo's command system",
	version = "0.0.1"
)
public class CommandSystem {
	@Mod.Instance("TLCommandSystem")
	public static CommandSystem instance;

	Map<String,ICommand> commands = new HashMap<String, ICommand>();

	/**
	 * Stores the FMLServerStartingEvent in order to use it to register further commands.
	 */
	private FMLServerStartingEvent serverStartingEvent;

	@Mod.Init
	public void init(FMLInitializationEvent event) {
		Transmute.logger.info("TLCommandSystem initializing...");
		MinecraftForge.EVENT_BUS.register(this);
	}

	// Commands
	public ICommand getCommand(String name) {
		return commands.get(name);
	}

	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);

		if (serverStartingEvent == null)
			return;

		registerCommandWithMinecraft(name, command);
	}

	@Mod.ServerStarting
	public void onServerStart(FMLServerStartingEvent event) {
		this.serverStartingEvent = event;

		new BindCommand();

		for (Map.Entry<String, ICommand> entry : commands.entrySet()) {
			registerCommandWithMinecraft(entry.getKey(), entry.getValue());
		}
	}

	private void registerCommandWithMinecraft(final String name, final ICommand command) {
		this.serverStartingEvent.registerServerCommand(new CommandBase() {
			@Override
			public String getCommandName() {
				return name;
			}

			@Override
			public String getCommandUsage(ICommandSender par1iCommandSender) {
				return "/" + name + " " + command.getUsage();
			}

			@Override
			public void processCommand(ICommandSender commandSender, String[] args) {
				final String argStr = StringUtils.join(args, " ");

				try {
					command.run(commandSender, args, argStr);
				}
				catch (CommandException e) {
					PlayerHelper.sendDirectedMessage(commandSender, e.getMessage());
				}
				catch (Exception e) {
					e.printStackTrace();
					PlayerHelper.sendDirectedMessage(commandSender, e.getMessage(), '4');
				}
			}
		});
	}

	// ToolBinds
	@ForgeSubscribe
	public void onPlayerInteractEntity(EntityInteractEvent event) {
		final ToolBind toolBind = getToolBindForEvent(event);
		if (toolBind == null)
			return;

		final EntityPlayer player = event.entityPlayer;

		event.setCanceled(true);

		try {
			toolBind.run(event); // TODO: evaluate return value
		}
		catch (CommandException e) {
			player.sendChatToPlayer(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			PlayerHelper.sendDirectedMessage(player, e.getMessage(), '4');
		}
	}

	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event) {
		final ToolBind toolBind = getToolBindForEvent(event);
		if (toolBind == null)
			return;

		final EntityPlayer player = event.entityPlayer;

		event.setCanceled(true);

		try {
			toolBind.run(event); // TODO: evaluate return value
		}
		catch (CommandException e) {
			player.sendChatToPlayer(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			PlayerHelper.sendDirectedMessage(player, e.getMessage(), '4');
		}
	}

	private ToolBind getToolBindForEvent(PlayerEvent event) {
		final EntityPlayer player = event.entityPlayer;

		final ItemStack heldItem = player.getHeldItem();
		if (heldItem == null)
			return null;

		final ToolBind toolBind = ToolBind.get(player.getEntityName(), heldItem.getItem(), false);
		if (toolBind == null)
			return null;

		return toolBind;
	}
}
