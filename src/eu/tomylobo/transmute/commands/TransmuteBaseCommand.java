package eu.tomylobo.transmute.commands;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import eu.tomylobo.commandsystem.CommandException;
import eu.tomylobo.commandsystem.ICommand;
import eu.tomylobo.transmute.TransmuteMod;

public abstract class TransmuteBaseCommand extends ICommand {
	@Override
	public final void Run(EntityPlayer ply, String[] args, String argStr) throws CommandException {
		args = parseFlags(args);

		final Entity target;
		if (booleanFlags.contains('l')) {
			target = TransmuteMod.instance.transmute.getLastTransmutedEntity(ply);
		}
		else if (booleanFlags.contains('c')) {
			target = TransmuteMod.instance.transmute.getClosestTransmutedEntity(ply);
		}
		else {
			target = ply;
		}

		Run2(ply, args, argStr, target);
	}

	public abstract void Run2(EntityPlayer ply, String[] args, String argStr, Entity target) throws CommandException;
}