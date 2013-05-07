package eu.tomylobo.util;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public class Utils {
	public static Location getLocation(Entity entity) {
		return new Location(entity.worldObj, new Vector(entity.posX, entity.posY, entity.posZ), entity.rotationYaw, entity.rotationPitch);
	}

	public static Location getEyeLocation(EntityLiving entity) {
		return new Location(entity.worldObj, new Vector(entity.posX, entity.posY+entity.getEyeHeight(), entity.posZ), entity.rotationYaw, entity.rotationPitch);
	}

	public static int locToBlock(double posX) {
		return MathHelper.floor_double(posX);
	}

	public static boolean hasPermission(ICommandSender commandSender, String value) {
		// TODO
		return true;
	}

	@SuppressWarnings("unchecked")
	public static List<EntityPlayer> getObservingPlayers(EntityPlayer target) {
		return target.worldObj.playerEntities;
	}

	@SuppressWarnings("unchecked")
	public static List<EntityPlayer> getObservingPlayers(Entity target) {
		return target.worldObj.playerEntities;
	}
}