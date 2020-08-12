package powercraft.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.discovery.ModCandidate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import powercraft.api.entity.PC_EntityFanFX;
import powercraft.api.entity.PC_EntityLaserFX;
import powercraft.api.entity.PC_EntityLaserParticleFX;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_OverlayRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_ClientRenderer;
import powercraft.api.renderer.PC_IOverlayRenderer;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.mod_PowerCraft;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ModuleObject;

@PC_ClientModule
public class PC_APIClientModule extends PC_APIModule {

	private PC_ClientRenderer cr1, cr2;

	@Override
	protected void initVars() {
		PC_ClientUtils.create();
	}

	public static void loadAssetsFromDir(File dir) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("modid", mod_PowerCraft.MODID);
		map.put("name", dir.getName());
		map.put("version", "1");
		FMLModContainer container = new FMLModContainer(mod_PowerCraft.class.getName(),
				new ModCandidate(dir, dir, ContainerType.JAR), map);
		container.bindMetadata(MetadataCollection.from(null, ""));
		FMLClientHandler.instance().addModAsResource(container);
	}

	@Override
	protected void clientPreInit(List<PC_ModuleObject> modules) {
		PC_Logger.enterSection("Module Texture Init");
		for (File jar : PC_LauncherUtils.getPowerCraftModuleFile().listFiles())
			if (jar.getName().contains(".jar") && jar.getName() != ".DS_Store") {
				loadAssetsFromDir(jar);
			}
		for (PC_ModuleObject module : modules) {
			List<String> l = module.loadTextureFiles(new ArrayList<String>());
			if (l != null) {
				for (String file : l) {
					PC_TextureRegistry.registerTexture(PC_TextureRegistry.getPowerCraftImageDir()
							+ PC_TextureRegistry.getTextureName(module, file));
				}
			}
		}
		PC_TextureRegistry.registerTexture(PC_TextureRegistry.getGresImgDir() + "button.png");
		PC_TextureRegistry.registerTexture(PC_TextureRegistry.getGresImgDir() + "dialog.png");
		PC_TextureRegistry.registerTexture(PC_TextureRegistry.getGresImgDir() + "frame.png");
		PC_TextureRegistry.registerTexture(PC_TextureRegistry.getGresImgDir() + "scrollbar_handle.png");
		PC_TextureRegistry.registerTexture(PC_TextureRegistry.getGresImgDir() + "widgets.png");
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Language Init");
		Minecraft.getMinecraft();
		PC_Lang.load();
		PC_Logger.exitSection();
	}

	@Override
	protected ModuleFieldInit getModuleFieldInit(PC_ModuleObject module) {
		return new ClientModuleFieldInit(module);
	}

	@Override
	protected void clientInit(List<PC_ModuleObject> modules) {
		PC_ClientUtils.registerEnitiyFX(PC_EntityLaserParticleFX.class);
		PC_ClientUtils.registerEnitiyFX(PC_EntityLaserFX.class);
		PC_ClientUtils.registerEnitiyFX(PC_EntityFanFX.class);
		PC_ClientUtils.registerEnitiyFX("EntitySmokeFX", EntitySmokeFX.class);
		RenderingRegistry.registerBlockHandler(new PC_ClientRenderer(true));
		RenderingRegistry.registerBlockHandler(new PC_ClientRenderer(false));

		PC_Logger.enterSection("Register EntityRender");
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<Class<? extends Entity>, Render>> list = module
					.registerEntityRender(new ArrayList<PC_Struct2<Class<? extends Entity>, Render>>());
			if (list != null) {
				for (PC_Struct2<Class<? extends Entity>, Render> s : list) {
					RenderingRegistry.registerEntityRenderingHandler(s.a, s.b);
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Gui Init");
		for (PC_ModuleObject module : modules) {
			List<PC_Struct2<String, Class<PC_IGresClient>>> l = module
					.registerGuis(new ArrayList<PC_Struct2<String, Class<PC_IGresClient>>>());
			if (l != null) {
				for (PC_Struct2<String, Class<PC_IGresClient>> g : l) {
					PC_GresRegistry.registerGresGui(g.a, g.b);
				}
			}
		}
		PC_Logger.exitSection();
	}

	@Override
	protected void clientPostInit(List<PC_ModuleObject> modules) {
	}

	private static class ClientModuleFieldInit extends ModuleFieldInit {

		public ClientModuleFieldInit(PC_ModuleObject module) {
			super(module);
		}

		@Override
		protected void registerObject(Object object) {
			super.registerObject(object);
			if (object instanceof PC_IOverlayRenderer) {
				PC_OverlayRegistry.register((PC_IOverlayRenderer) object);
			}
		}

	}

}
