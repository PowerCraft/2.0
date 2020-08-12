package powercraft.logic;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_AddSplashes;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;
import powercraft.logic.gui.PClo_GuiDelayer;
import powercraft.logic.gui.PClo_GuiPulsar;
import powercraft.logic.gui.PClo_GuiSpecial;

@PC_ClientModule
public class PClo_AppClient extends PClo_App {

	@PC_AddSplashes
	public List<String> addSplashes(List<String> list) {
		list.add("Adjustable clock pulse!");
		return list;
	}

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Delayer", PClo_GuiDelayer.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Pulsar", PClo_GuiPulsar.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Special", PClo_GuiSpecial.class));
		return guis;
	}

}
