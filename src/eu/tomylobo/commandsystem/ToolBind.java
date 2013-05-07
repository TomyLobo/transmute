package eu.tomylobo.commandsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import eu.tomylobo.util.PlayerHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public abstract class ToolBind {
	public final String playerName;
	public final String name;

	public ToolBind(String name, EntityPlayer ply) {
		this.name = name;
		this.playerName = ply == null ? null : ply.getEntityName();
	}

	public boolean run(PlayerInteractEvent event) throws CommandException { return false; };
	public boolean run(EntityInteractEvent event) throws CommandException { return false; };

	private static Map<String, ToolBind> toolMappings = new HashMap<String, ToolBind>();

	public static void add(EntityPlayer ply, Item toolType, boolean left, ToolBind toolBind) {
		String key = makeKey(ply.getEntityName(),toolType.getItemName(), left);

		toolMappings.put(key, toolBind);
	}

	public static void addGlobal(Item toolType, ToolBind toolBind) {
		String key = toolType.getItemName();

		toolMappings.put(key, toolBind);
	}

	/**
	 * Removes a tool mapping for the given player/tool pair.
	 *
	 * @param ply the player the mapping is associated with.
	 * @param toolType the tool the mapping is associated with.
	 * @return true if there was a previous mapping
	 */
	public static boolean remove(EntityPlayer ply, Item toolType, boolean left) {
		String key = makeKey(ply.getEntityName(),toolType.getItemName(), left);

		return toolMappings.remove(key) != null;
	}

	public static void removeGlobal(Item toolType) {
		String key = toolType.getItemName();

		toolMappings.remove(key);
	}

	public static Map<String, ToolBind> list(String playerName) {
		Map<String, ToolBind> ret = new HashMap<String, ToolBind>();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (!playerName.equals(toolBind.playerName))
				continue;

			String toolName = entry.getKey();
			toolName = toolName.substring(toolName.indexOf(' ')+1);

			ret.put(toolName, toolBind);
		}
		return ret;
	}

	public static Map<String, ToolBind> listGlobal() {
		Map<String, ToolBind> ret = new HashMap<String, ToolBind>();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (toolBind.playerName != null)
				continue;

			ret.put(entry.getKey(), toolBind);
		}
		return ret;
	}

	public static void updateToolMappings(EntityPlayer player) {
		String playerName = player.getEntityName();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (playerName.equals(toolBind.playerName)) {
				String toolName = entry.getKey();
				toolName = toolName.substring(toolName.indexOf(' ')+1);
				PlayerHelper.sendDirectedMessage(player, "Restored bind \u00a7e"+toolName+"\u00a7f => \u00a79"+toolBind.name);
			}
		}
	}

	public static ToolBind get(String playerName, Item itemMaterial, boolean left) {
		final String itemName = itemMaterial.getItemName();
		final ToolBind toolBind = toolMappings.get(makeKey(playerName, itemName, left));
		if (toolBind != null)
			return toolBind;

		return toolMappings.get(itemName);
	}

	private static String makeKey(String playerName, final String itemName, boolean left) {
		return playerName+" "+itemName+left;
	}
}
