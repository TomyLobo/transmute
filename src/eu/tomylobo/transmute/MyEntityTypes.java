package eu.tomylobo.transmute;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.boss.EntityWither;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class MyEntityTypes {
	@SuppressWarnings("unchecked")
	private static final Map<String, Class<? extends Entity>> typeNameToClass = new HashMap<String, Class<? extends Entity>>((Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping);
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, String> classToTypeName = new HashMap<Class<? extends Entity>, String>((Map<Class<? extends Entity>, String>) EntityList.classToStringMapping);
	@SuppressWarnings("unchecked")
	private static final Map<Integer, Class<? extends Entity>> idToClass = new HashMap<Integer, Class<? extends Entity>>((Map<Integer, Class<? extends Entity>>) EntityList.IDtoClassMapping);
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, Integer> classToId = new HashMap<Class<? extends Entity>, Integer>((Map<Class<? extends Entity>, Integer>) ObfuscationReflectionHelper.getPrivateValue(EntityList.class, null, "e", "classToIDMapping"));

	static {
		addAliases(EntityXPOrb.class, "XP");
		addAliases(EntityEnderPearl.class, "Enderpearl");
		addAliases(EntityEnderEye.class, "EnderSignal", "EyeOfEnder", "EnderEye");
		addAliases(EntityTNTPrimed.class, "Tnt");
		addAliases(EntityFallingSand.class, "FallingBlock");
		addAliases(EntityMagmaCube.class, "MagmaCube");
		addAliases(EntityDragon.class, "Dragon");
		addAliases(EntityOcelot.class, "Ocelot", "Cat");
		addAliases(EntityWither.class, "Wither");

		addType(EntityFishHook.class, 1000, "FishingHook", "Fish", "Hook", "FishingPole");
		addType(EntityPotion.class, 1001, "Potion", "ThrownPotion");
		addType(EntityEgg.class, 1002, "Egg", "ThrownEgg");
	}

	private static void addAliases(Class<? extends Entity> cls, String... aliases) {
		for (String alias : aliases) {
			typeNameToClass.put(alias, cls);
		}
	}

	private static void addType(Class<? extends Entity> cls, int mobType, String... aliases) {
		for (String alias : aliases) {
			typeNameToClass.put(alias, cls);
		}

		classToTypeName.put(cls, aliases[0]);
		idToClass.put(Integer.valueOf(mobType), cls);
		classToId.put(cls, Integer.valueOf(mobType));
	}

	public static final Class<? extends net.minecraft.entity.Entity> typeNameToClass(String mobType) throws EntityTypeNotFoundException {
		for (Entry<String, Class<? extends net.minecraft.entity.Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		throw new EntityTypeNotFoundException();
		//return typeNameToClass.get(mobType);
	}
	
	public static final int classToId(Class<? extends net.minecraft.entity.Entity> mobType) throws EntityTypeNotFoundException {
		final Integer id = classToId.get(mobType);
		if (id == null)
			throw new EntityTypeNotFoundException();

		return id;
	}

	public static final String classToTypeName(Class<? extends net.minecraft.entity.Entity> mobType) throws EntityTypeNotFoundException {
		final String typeName = classToTypeName.get(mobType);
		if (typeName == null)
			throw new EntityTypeNotFoundException();

		return typeName;
	}

	public static final Class<? extends net.minecraft.entity.Entity> idToClass(int id) throws EntityTypeNotFoundException {
		final Class<? extends net.minecraft.entity.Entity> mobType = idToClass.get(id);
		if (mobType == null)
			throw new EntityTypeNotFoundException();

		return mobType;
	}
}
