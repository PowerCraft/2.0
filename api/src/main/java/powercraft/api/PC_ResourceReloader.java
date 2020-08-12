package powercraft.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import powercraft.api.utils.PC_Utils;

public class PC_ResourceReloader {

	protected long timeStamp;

	@SubscribeEvent
	public void resourceReload(TextureStitchEvent e) {
		PC_Lang.load();
	}

}
