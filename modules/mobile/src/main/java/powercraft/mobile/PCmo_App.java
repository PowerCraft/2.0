package powercraft.mobile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.recipes.PC_3DRecipe;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitEntities;
import powercraft.launcher.loader.PC_Module.PC_InitPackets;
import powercraft.launcher.loader.PC_Module.PC_InitProperties;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_Module.PC_RegisterContainers;
import powercraft.launcher.loader.PC_ModuleObject;

@PC_Module(name = "Mobile", version = "@Version@")
public class PCmo_App {

	public static PCmo_MinerManager minerManager = new PCmo_MinerManager();

	public static final String pk_mForward = "move_forward";
	public static final String pk_mBackward = "move_backward";
	public static final String pk_mLeft = "turn_left";
	public static final String pk_mRight = "turn_right";
	public static final String pk_mAround = "turn_around";
	public static final String pk_mUp = "mine_up";
	public static final String pk_mDown = "mine_down";
	public static final String pk_mBridgeOn = "set_bridge_on";
	public static final String pk_mBridgeOff = "set_bridge_off";
	public static final String pk_mRun = "run_program";
	public static final String pk_mDeposit = "store_to_chest";
	public static final String pk_mToBlocks = "deactivate";
	public static final String pk_mMiningOn = "set_mining_on";
	public static final String pk_mMiningOff = "set_mining_off";
	public static final String pk_mCancel = "reset";

	@PC_Instance
	public static PC_ModuleObject instance;

	@PC_InitProperties
	public void initProperties(PC_Property config) {
		PC_KeyRegistry.watchForKey(config, pk_mForward, 0x48); // KEY_NUMPAD8
		PC_KeyRegistry.watchForKey(config, pk_mBackward, 0x50); // KEY_NUMPAD2
		PC_KeyRegistry.watchForKey(config, pk_mLeft, 0x4b); // KEY_NUMPAD4
		PC_KeyRegistry.watchForKey(config, pk_mRight, 0x4d); // KEY_NUMPAD6
		PC_KeyRegistry.watchForKey(config, pk_mAround, 0x4c); // KEY_NUMPAD5
		PC_KeyRegistry.watchForKey(config, pk_mDown, 0x4a); // KEY_SUBTRACT
		PC_KeyRegistry.watchForKey(config, pk_mUp, 0x4e); // KEY_ADD

		PC_KeyRegistry.watchForKey(config, pk_mBridgeOn, 0x18); // KEY_O
		PC_KeyRegistry.watchForKey(config, pk_mBridgeOff, 0x19); // KEY_P
		PC_KeyRegistry.watchForKey(config, pk_mRun, 0x9c); // KEY_NUMPADENTER

		PC_KeyRegistry.watchForKey(config, pk_mDeposit, 0x53); // KEY_DECIMAL
		PC_KeyRegistry.watchForKey(config, pk_mToBlocks, 0x4f); // KEY_NUMPAD1
		PC_KeyRegistry.watchForKey(config, pk_mMiningOn, 0x47); // KEY_NUMPAD7
		PC_KeyRegistry.watchForKey(config, pk_mMiningOff, 0x49); // KEY_NUMPAD9

		PC_KeyRegistry.watchForKey(config, pk_mCancel, 0xd3); // KEY_DELETE
	}

	@PC_InitEntities
	public List<PC_Struct2<Class<? extends Entity>, Integer>> initEntities(
			List<PC_Struct2<Class<? extends Entity>, Integer>> entities) {
		entities.add(new PC_Struct2<Class<? extends Entity>, Integer>(PCmo_EntityMiner.class, 200));
		return entities;
	}

	@PC_InitRecipes
	public List<PC_IRecipe> initRecipes(List<PC_IRecipe> recipes) {

		recipes.add(new PC_3DRecipe(minerManager, new String[] { "ss", "ss" }, new String[] { "ss", "cc" }, 's',
				Blocks.iron_block, 'c', Blocks.chest));

		recipes.add(new PC_3DRecipe(minerManager, new String[] { "oooo", "oooo", "oooo", "oooo" },
				new String[] { "oooo", "o  o", "o  o", "oooo" }, new String[] { "oooo", "o  o", "o  o", "oooo" },
				new String[] { "oooo", "oooo", "oooo", "oooo" }, 'o', Blocks.obsidian));

		return recipes;
	}

	@PC_InitPackets
	public List<Class> initPackets(List<Class> packets) {
		packets.add(PCmo_PacketMinerClient.class);
		packets.add(PCmo_PacketMinerServer.class);
		return packets;
	}

	@PC_RegisterContainers
	public List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> registerContainers(
			List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>("Miner", PCmo_ContainerMiner.class));
		return guis;
	}
}
