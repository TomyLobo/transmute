package eu.tomylobo.transmute.commands;

import eu.tomylobo.commandsystem.ICommand.*;
import eu.tomylobo.commandsystem.PermissionDeniedException;
import eu.tomylobo.commandsystem.ToolBind;
import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.transmute.Materials;
import eu.tomylobo.transmute.Shape;
import eu.tomylobo.transmute.TransmuteMod;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

@Names({"shapeaction", "sac"})
@Help(
		"Gives your current shape a command.\n" +
		"Flags:\n" +
		"  -e to issue the command to an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -l to affect the last entity you transmuted\n" +
		"  -c to affect the closest transmuted entity\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("[<flags>][<command>]")
@Permission("transmute.shapeaction")
@BooleanFlags("elcx")
@StringFlags("i")
public class ShapeActionCommand extends TransmuteBaseCommand {
	@Override
	public void Run2(EntityPlayer ply, String[] args, String argStr, Entity target) throws CommandException {
		final String shapeAction = parseFlags(argStr);

		if (booleanFlags.contains('e')) {
			if (!Utils.hasPermission(ply,"transmute.shapeaction.others"))
				throw new PermissionDeniedException();

			final Item toolType;
			if (stringFlags.containsKey('i')) {
				final String materialName = stringFlags.get('i');
				toolType = Materials.matchMaterial(materialName);
			}
			else {
				toolType = ply.getHeldItem().getItem();
			}

			boolean left = booleanFlags.contains('x');

			ToolBind.add(ply, toolType, left, new ToolBind(shapeAction, ply) {
				@Override
				public boolean run(EntityInteractEvent event) throws CommandException {
					final EntityPlayer player = event.entityPlayer;
					if (!Utils.hasPermission(player, "transmute.shapeaction.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.entity;

					final Shape shape = TransmuteMod.instance.transmute.getShape(entity);
					if (shape == null)
						throw new CommandException("Your target is not currently transmuted.");

					shape.runAction(player, shapeAction);

					return true;
				}
			});

			PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+shapeAction+"\u00a7f to your tool (\u00a7e"+toolType.getItemName()+"\u00a7f). Right-click an entity to use.");
			return;
		}

		final Shape shape = TransmuteMod.instance.transmute.getShape(target);
		if (shape == null)
			throw new CommandException("Not currently transmuted.");

		shape.runAction(ply, shapeAction);
	}
}
