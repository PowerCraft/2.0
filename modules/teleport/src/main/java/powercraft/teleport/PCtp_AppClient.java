package powercraft.teleport;

import java.util.List;

import powercraft.api.gres.PC_IGresClient;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_RegisterGuis;

@PC_ClientModule
public class PCtp_AppClient extends PCtp_App {

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class<? extends PC_IGresClient>>> registerGuis(
			List<PC_Struct2<String, Class<? extends PC_IGresClient>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("Teleporter", PCtp_GuiTeleporter.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_IGresClient>>("PlayerTeleport",
				PCtp_GuiPlayerTeleport.class));
		return guis;
	}

}
