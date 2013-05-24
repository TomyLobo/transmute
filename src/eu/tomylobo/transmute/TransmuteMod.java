package eu.tomylobo.transmute;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = "Transmute",
	name = "Transmute",
	version = "0.0.2",
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
		Transmute.logger.info("Transmute initializing...");

		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());

		configFile.save();
	}

	@Mod.PostInit
	public void postInit(FMLPostInitializationEvent event) {
		Materials.init();
	}

}
