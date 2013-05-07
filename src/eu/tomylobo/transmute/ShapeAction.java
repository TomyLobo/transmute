package eu.tomylobo.transmute;

import eu.tomylobo.commandsystem.CommandException;
import net.minecraft.entity.player.EntityPlayer;

public interface ShapeAction {
	abstract public void run(EntityShape shape, EntityPlayer player, String[] args, String argStr) throws CommandException;
}
