package powercraft.transport;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_LoadTextureFiles;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;
import powercraft.transport.gui.PCtr_GuiEjectionBelt;
import powercraft.transport.gui.PCtr_GuiSeparationBelt;
import powercraft.transport.gui.PCtr_GuiSplitter;

@PC_ClientModule
public class PCtr_AppClient extends PCtr_App {

	@PC_LoadTextureFiles
	public List<String> loadTextureFiles(List<String> textures) {
		textures.add("slimeboots.png");
		return textures;
	}

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("SeperationBelt",
				PCtr_GuiSeparationBelt.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("EjectionBelt", PCtr_GuiEjectionBelt.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Splitter", PCtr_GuiSplitter.class));
		return guis;
	}

}
