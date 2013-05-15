package eu.tomylobo.transmute;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Materials {
	private static final List<Map<String,Item>> aliases = new ArrayList<Map<String,Item>>();
	private static final Map<String,Short> dataValues = new HashMap<String,Short>();

	private static void addAlias(int priority, Item item, String... names) {
		for (int i = aliases.size(); i <= priority; ++i) {
			aliases.add(new HashMap<String, Item>());
		}

		Map<String, Item> aliases2 = aliases.get(priority);

		for (String name : names) {
			name = name.toLowerCase();
			aliases2.put(name, item);
			aliases2.put(name.replaceAll("_", "."), item);
			aliases2.put(name.replaceAll("\\.", "_"), item);
		}
	}

	private static void addAlias(Item item, String... names) {
		addAlias(0, item, names);
	}

	private static void addAlias(Block block, String... names) {
		addAlias(Item.itemsList[block.blockID], names);
	}

	static {
		addAlias(Item.shovelWood, "wood_spade", "wooden_spade");
		addAlias(Item.shovelStone, "stone_spade");
		addAlias(Item.shovelSteel, "iron_spade");
		addAlias(Item.shovelDiamond, "diamond_spade");
		addAlias(Item.shovelGold, "gold_spade", "golden_spade");
		addAlias(Item.axeWood, "wood_axe", "wooden_axe");
		addAlias(Item.axeStone, "golden_axe");
		addAlias(Item.axeSteel, "golden_axe");
		addAlias(Item.axeDiamond, "golden_axe");
		addAlias(Item.axeGold, "gold_axe", "golden_axe");
		// TODO: generic ^

		addAlias(Block.cobblestone, "cobblestone", "cobble_stone", "cobble");
		addAlias(Block.leaves, "leaf");
		addAlias(Block.music, "noteblock");
		addAlias(Block.stoneSingleSlab, "slab", "stoneslab");
		addAlias(Block.bookShelf, "shelf");
		addAlias(Block.cobblestoneMossy, "mossy_cobble");
		addAlias(Block.mobSpawner, "mobspawner");
		addAlias(Block.stairCompactPlanks, "wooden_stairs");
		addAlias(Block.stairCompactCobblestone, "cobble_stairs");
		addAlias(Block.torchRedstoneActive, "redstone_torch");
		addAlias(Block.redstoneRepeaterIdle, "repeater_block");
		addAlias(Item.gunpowder, "gunpowder");
		addAlias(Block.stoneButton, "stone_button");
		addAlias(Item.bucketEmpty, "bukkit");
		addAlias(Item.bucketWater, "water_bukkit");
		addAlias(Item.bucketLava, "lava_bukkit");
		addAlias(Item.bucketMilk, "milk_bukkit");
		addAlias(Item.dyePowder, "dye", "ink");
		addAlias(Item.redstoneRepeater, "repeater");
		addAlias(Block.pistonBase, "piston");
		addAlias(Block.pistonStickyBase, "sticky_piston", "piston_sticky");
		addAlias(Item.reed, "reed");
		addAlias(Item.porkRaw, "raw_pork", "pork");
		addAlias(Item.porkCooked, "cooked_pork");
		addAlias(Block.stairsStoneBrickSmooth, "stonebrick_stairs", "stone_brick_stairs");

		dataValues.put("43:SANDSTONE", (short) 1);
		dataValues.put("43:WOOD", (short) 2);
		dataValues.put("43:COBBLE", (short) 3);
		dataValues.put("43:COBBLESTONE", (short) 3);
		dataValues.put("43:BRICK", (short) 4);
		dataValues.put("43:STONEBRICK", (short) 5);

		dataValues.put("44:SANDSTONE", (short) 1);
		dataValues.put("44:WOOD", (short) 2);
		dataValues.put("44:COBBLE", (short) 3);
		dataValues.put("44:COBBLESTONE", (short) 3);
		dataValues.put("44:BRICK", (short) 4);
		dataValues.put("44:STONEBRICK", (short) 5);

		for (short i = 1; i <= 5; ++i) {
			dataValues.put("43:"+i, i);
			dataValues.put("44:"+i, i);
		}

		dataValues.put("5:REDWOOD", (short) 1);
		dataValues.put("5:DARK", (short) 1);
		dataValues.put("5:PINE", (short) 1);
		dataValues.put("5:SPRUCE", (short) 1);
		dataValues.put("5:BIRCH", (short) 2);
		dataValues.put("5:LIGHT", (short) 2);
		dataValues.put("5:JUNGLE", (short) 3);
		dataValues.put("5:TROPIC", (short) 3);

		dataValues.put("17:REDWOOD", (short) 1);
		dataValues.put("17:DARK", (short) 1);
		dataValues.put("17:PINE", (short) 1);
		dataValues.put("17:SPRUCE", (short) 1);
		dataValues.put("17:BIRCH", (short) 2);
		dataValues.put("17:LIGHT", (short) 2);
		dataValues.put("17:JUNGLE", (short) 3);
		dataValues.put("17:TROPIC", (short) 3);

		for (short i = 1; i <= 3; ++i) {
			dataValues.put("5:"+i, i);
			dataValues.put("17:"+i, i);
		}
	};

	public static Item matchMaterial(String materialName) {
		for (Map<String, Item> aliases2 : aliases) {
			Item material = aliases2.get(materialName.toLowerCase());
			if (material != null)
				return material;
		}

		return null;
	}

	public static Short getDataValue(final Item material, String dataName) {
		return dataValues.get(material.itemID+":"+dataName);
	}

	private static final String[] regexes = {
		"item.record",
		"tile\\.(block|ingot)([A-Z].*)",
		"(tile|item)\\.(axe|boots|chestplate|hatchet|helmet|hoe|leggings|pickaxe|shovel|spade|sword|bucket|potato|ore|stairs)([A-Z].*)",
		"tile\\.(.*)(Raw|Cooked)",
		"(tile|item)\\.([^A-Z]*)([A-Z][^A-Z]*)([A-Z].*)",
		"(tile|item)\\.([^A-Z]*)([A-Z].*)",
		"tile\\.(.*)",
		"item\\.(.*)",
	};

	static void init() {
		final Pattern[] patterns = new Pattern[regexes.length];
		for (int i = 0; i < regexes.length; ++i) {
			patterns[i] = Pattern.compile("^"+regexes[i]+"$");
		}

		final Item[] itemsList = Item.itemsList;
		for (int i = 0; i < itemsList.length; ++i) {
			final Item item = itemsList[i];
			if (item == null)
				continue;

			final String itemName = item.getItemName();
			if (itemName == null)
				continue;

			addAlias(item, itemName);

			for (int j = 0; j < patterns.length; ++j) {
				final Matcher matcher = patterns[j].matcher(itemName);
				if (!matcher.matches())
					continue;

				switch (j) {
				case 0:
					final ItemRecord itemRecord = (ItemRecord) item;
					addAlias(1, item, "record_"+itemRecord.recordName.replaceAll(" ", "_"));
					break;

				case 1:
					addAlias(1, item, matcher.group(2)+"_"+matcher.group(1));
					break;

				case 2:
					final String category = matcher.group(2);
					final String material = matcher.group(3);

					addAlias(1, item, material+"_"+category);

					if (material.equalsIgnoreCase("wood"))
						addAlias(1, item, "golden_"+category);

					if (material.equalsIgnoreCase("golden"))
						addAlias(1, item, "gold_"+category);

					if (material.equalsIgnoreCase("wood"))
						addAlias(1, item, "wooden_"+category);

					if (material.equalsIgnoreCase("wooden"))
						addAlias(1, item, "wood_"+category);

					break;

				case 3:
					final String state = matcher.group(2);
					addAlias(1, item, state+"_"+matcher.group(1));

					if (state.equalsIgnoreCase("Raw"))
						addAlias(2, item, matcher.group(1));

					break;

				case 4:
					addAlias(1, item, matcher.group(2)+"_"+matcher.group(3)+"_"+matcher.group(4));
					break;

				case 5:
					addAlias(1, item, matcher.group(2)+"_"+matcher.group(3));
					break;

				case 6:
					addAlias(1, item, matcher.group(1)+"_block");
					addAlias(2, item, matcher.group(1));
					break;

				case 7:
					addAlias(1, item, matcher.group(1));
					break;
				}

				break;
			}
		}
	}
}
