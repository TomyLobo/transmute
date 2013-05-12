package eu.tomylobo.transmute;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import eu.tomylobo.transmute.commands.ShapeActionCommand;
import eu.tomylobo.transmute.commands.TestCommand;
import eu.tomylobo.transmute.commands.TransmuteCommand;

@Mod(
	modid = "Transmute",
	name = "Transmute",
	version = "0.0.1",
	dependencies = "required-after:TLPacketSystem;required-after:TLCommandSystem;required-after:TLScheduler"
)
public class TransmuteMod {
	public static class Config {
	}

	@Mod.Instance("Transmute")
	public static TransmuteMod instance;

	public Transmute transmute = new Transmute();

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {
		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());

		configFile.save();
	}
	@Mod.Init
	public void init(FMLInitializationEvent event) {
		Transmute.logger.info("Transmute initializing...");

		new TransmuteCommand();
		new ShapeActionCommand();
		new TestCommand();
	}

	@Mod.PostInit
	public void postInit(FMLPostInitializationEvent event) {
		Materials.init();
	}

}
