package powercraft.deco;

import java.util.List;

import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_LoadTextureFiles;

@PC_ClientModule
public class PCde_AppClient extends PCde_App {

	@PC_LoadTextureFiles
	public List<String> loadTextureFiles(List<String> textures) {
		textures.add("block_deco.png");
		return textures;
	}

}
