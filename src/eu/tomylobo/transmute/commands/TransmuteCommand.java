package eu.tomylobo.transmute.commands;

import eu.tomylobo.commandsystem.ICommand.*;
import eu.tomylobo.commandsystem.PermissionDeniedException;
import eu.tomylobo.commandsystem.ToolBind;
import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.math.Location;
import eu.tomylobo.transmute.EntityShape;
import eu.tomylobo.transmute.Materials;
import eu.tomylobo.transmute.Shape;
import eu.tomylobo.transmute.TransmuteMod;
import eu.tomylobo.util.PlayerHelper;
import eu.tomylobo.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import java.util.List;

@Names("transmute")
@Help(
		"Disguises you or an entity as a mob.\n" +
		"Flags:\n" +
		"  -e to transmute an entity (binds to a tool)\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -l to transmute the last entity you transmuted\n" +
		"  -c to transmute the closest transmuted entity\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("[<flags>][<shape>]")
@Permission("transmute.transmute")
@BooleanFlags("elcx")
@StringFlags("i")
public class TransmuteCommand extends TransmuteBaseCommand {
	@Override
	public void Run2(EntityPlayer ply, String[] args, String argStr, Entity target) throws CommandException {
		if (args.length == 0) {
			if (!TransmuteMod.instance.transmute.isTransmuted(target))
				throw new CommandException("Not transmuted");

			TransmuteMod.instance.transmute.resetShape(ply, target);

			if (ply == target) {
				PlayerHelper.sendDirectedMessage(ply, "Transmuted you back into your original shape.");
			}
			else {
				PlayerHelper.sendDirectedMessage(ply, "Transmuted your last target back into its original shape.");
			}

			effect(target, null);
			return;
		}

		final String mobType = args[0];
		if (booleanFlags.contains('e')) {
			if (!Utils.hasPermission(ply, "transmute.transmute.others"))
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

			ToolBind.add(ply, toolType, left, new ToolBind(mobType, ply) {
				@Override
				public boolean run(EntityInteractEvent event) throws CommandException {
					final EntityPlayer player = event.entityPlayer;
					if (!Utils.hasPermission(player, "transmute.transmute.others"))
						throw new PermissionDeniedException();

					final Entity entity = event.target;

					final Shape shape;
					if (TransmuteMod.instance.transmute.isTransmuted(entity)) {
						shape = null;
						TransmuteMod.instance.transmute.resetShape(player, entity);

						PlayerHelper.sendDirectedMessage(player, "Transmuted your target back into its original shape.");
					}
					else {
						shape = TransmuteMod.instance.transmute.setShape(player, entity , mobType);

						PlayerHelper.sendDirectedMessage(player, "Transmuted your target into a "+mobType+".");
					}

					effect(entity, shape);
					return true;
				}
			});

			PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+mobType+"\u00a7f to your tool (\u00a7e"+toolType.getItemName()+"\u00a7f). Right-click an entity to use.");
			return;
		}

		final Shape shape = TransmuteMod.instance.transmute.setShape(ply, target, mobType);

		if (ply == target) {
			PlayerHelper.sendDirectedMessage(ply, "Transmuted you into "+mobType+".");
		}
		else {
			PlayerHelper.sendDirectedMessage(ply, "Transmuted your last target into "+mobType+".");
		}

		effect(target, shape);
	}

	private void effect(Entity target, Shape shape) {
		Location location;
		if (target instanceof EntityLiving) {
			location = Utils.getEyeLocation((EntityLiving) target);
		}
		else {
			location = Utils.getLocation(target);
			if (shape instanceof EntityShape)
				location = location.add(0, ((EntityShape) shape).getYOffset(), 0);
		}

		double radius = 64;
		radius *= radius;

		final List<EntityPlayer> players;
		if (target instanceof EntityPlayer)
			players = Utils.getObservingPlayers((EntityPlayer) target);
		else
			players = Utils.getObservingPlayers(target);

		for (EntityPlayer player : players) {
			if (Utils.getLocation(player).distanceSq(location) > radius)
				continue;

			// TODO
			/*player.playEffect(location, Effect.EXTINGUISH, 0);
			player.playEffect(location, Effect.SMOKE, 4);
			player.playEffect(location, Effect.SMOKE, 4);
			player.playEffect(location, Effect.SMOKE, 4);*/
		}
	}
}
