package eu.tomylobo.scheduler;

import java.util.EnumSet;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eu.tomylobo.transmute.Transmute;

@Mod(
	modid = "TLScheduler",
	name = "TLScheduler",
	version = "0.0.1"
)
public class Scheduler implements ITickHandler {
	@Mod.Instance("TLScheduler")
	public static Scheduler instance;

	@Mod.Init
	public void init(FMLInitializationEvent event) {
		Transmute.logger.info("TLScheduler initializing...");
		MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.registerTickHandler(this, Side.SERVER);
	}

	
	// ITickHandler
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		/*final World world = (World) tickData[0];

		getWorldScheduler(world).tick();*/

		getWorldScheduler(0).tick();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) { }

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "TLScheduler";
	}


	// API
	//Map<Integer,WorldScheduler> worldSchedulers = new HashMap<Integer, WorldScheduler>();
	WorldScheduler worldScheduler = new WorldScheduler();

	public WorldScheduler getWorldScheduler(World world) {
		return getWorldScheduler(world.getWorldInfo().getDimension());
	}

	public WorldScheduler getWorldScheduler(int dimension) {
		/*WorldScheduler worldScheduler = worldSchedulers.get(dimension);
		if (worldScheduler == null)
			worldSchedulers.put(dimension, worldScheduler = new WorldScheduler());*/

		return worldScheduler;
	}
}
