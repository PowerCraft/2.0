package powercraft.api.registry;

import net.minecraft.util.IIcon;
import powercraft.api.block.PC_Block;
import powercraft.launcher.loader.PC_ModuleObject;

public final class PC_TextureRegistry {

	public static void registerTexture(String texture) {
		if (texture != null)
			PC_RegistryClient.getInstance().registerTexture(texture);
	}

	public static String getPowerCraftImageDir() {
		return "textures/";
	}

	public static String getGresImgDir() {
		return getPowerCraftImageDir() + "Api/gres/";
	}

	public static String getTextureName(PC_ModuleObject module, String texuteName) {
		return module.getModuleName() + "/" + texuteName;
	}

	public static void onIconLoading(PC_Block block, Object iconRegister) {
		PC_RegistryServer.getInstance().onIconLoading(block, iconRegister);
	}

	public static IIcon registerIcon(PC_ModuleObject module, String texture) {
		return PC_RegistryServer.getInstance().registerIcon(getTextureName(module, texture));
	}

}
