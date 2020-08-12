package powercraft.machines;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_LoadTextureFiles;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;
import powercraft.machines.gui.PCma_GuiAutomaticWorkbench;
import powercraft.machines.gui.PCma_GuiBlockBuilder;
import powercraft.machines.gui.PCma_GuiReplacer;
import powercraft.machines.gui.PCma_GuiRoaster;
import powercraft.machines.gui.PCma_GuiTransmutabox;
import powercraft.machines.gui.PCma_GuiXPBank;

@PC_ClientModule
public class PCma_AppClient extends PCma_App {

	@PC_LoadTextureFiles
	public List<String> loadTextureFiles(List<String> textures) {
		textures.add("fisher.png");
		return textures;
	}

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("AutomaticWorkbench",
				PCma_GuiAutomaticWorkbench.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("BlockBuilder", PCma_GuiBlockBuilder.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Replacer", PCma_GuiReplacer.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Roaster", PCma_GuiRoaster.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Transmutabox", PCma_GuiTransmutabox.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("XPBank", PCma_GuiXPBank.class));
		return guis;
	}

}
