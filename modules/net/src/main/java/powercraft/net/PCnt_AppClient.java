package powercraft.net;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;
import powercraft.net.gui.PCnt_GuiRadio;
import powercraft.net.gui.PCnt_GuiSensor;

@PC_ClientModule
public class PCnt_AppClient extends PCnt_App {

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Sensor", PCnt_GuiSensor.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Radio", PCnt_GuiRadio.class));
		return guis;
	}

}
