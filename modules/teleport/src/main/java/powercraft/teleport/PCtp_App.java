package powercraft.teleport;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitDataHandlers;
import powercraft.launcher.loader.PC_Module.PC_InitPackets;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;

@PC_Module(name = "Teleport", version = "@Version@")
public class PCtp_App {

	public static PCtp_TeleporterManager teleporterManager = new PCtp_TeleporterManager();

	@PC_FieldObject(clazz = PCtp_BlockTeleporter.class)
	public static PC_Block teleporter;
	@PC_FieldObject(clazz = PCtp_ItemTeleporterCore.class)
	public static PC_Item teleporterCore;

	@PC_InitRecipes
	public void initRecipes() {
		if (PC_GlobalVariables.mediumRecipes) {
			GameRegistry.addRecipe(new ItemStack(teleporter, 1), new Object[] { "PPP", "PVP", "SSS", 'V',
					teleporterCore, 'P', Blocks.glass_pane, 'S', Items.iron_ingot });
			GameRegistry.addRecipe(new ItemStack(teleporterCore, 1),
					new Object[] { "EOE", "OVO", "EOE", 'V',
							new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 3), 'O',
							Blocks.obsidian, 'E', Items.ender_pearl });
		} else if (PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addRecipe(new ItemStack(teleporter, 1), new Object[] { " P ", "PVP", "SSS", 'V',
					new ItemStack(Items.dye, 1, 5), 'P', Blocks.glass_pane, 'S', Items.iron_ingot });
		}
	}

	@PC_InitPackets
	public List<Class> initPackets(List<Class> packets) {
		packets.add(PCtp_PacketTeleporterSync.class);
		packets.add(PCtp_PacketTeleport.class);
		return packets;
	}

	@PC_InitDataHandlers
	public List<PC_Struct2<String, PC_IDataHandler>> initDataHandlers(
			List<PC_Struct2<String, PC_IDataHandler>> dataHandlers) {
		dataHandlers.add(new PC_Struct2<String, PC_IDataHandler>("Teleporter", teleporterManager));
		return dataHandlers;
	}

}
