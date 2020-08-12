package powercraft.api.registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockFlag;
import powercraft.api.interfaces.PC_IMSG;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public final class PC_MSGRegistry {

	private static List<PC_IMSG> msgObjects = new ArrayList<PC_IMSG>();

	public static final int MSG_DEFAULT_NAME = 1, MSG_BLOCK_FLAGS = 2, MSG_ITEM_FLAGS = 3,
			MSG_RENDER_INVENTORY_BLOCK = 4, MSG_RENDER_WORLD_BLOCK = 5, MSG_LOAD_FROM_CONFIG = 10,
			MSG_ON_HIT_BY_BEAM_TRACER = 11, MSG_BURN_TIME = 12, MSG_RECIVE_POWER = 13, MSG_CAN_RECIVE_POWER = 14,
			MSG_ON_ACTIVATOR_USED_ON_BLOCK = 15, MSG_STR_MSG = 17, MSG_RENDER_ITEM_HORIZONTAL = 18, MSG_ROTATION = 19,
			MSG_RATING = 20, MSG_CONDUCTIVITY = 21, MSG_LOAD_WORLD = 23, MSG_GET_PROVIDET_GLOBAL_FUNCTIONS = 24,
			MSG_RENDER_OVERLAY = 25, MSG_OPEN_GUI_OR_PLACE_BLOCK = 26, MSG_DOES_SMOKE = 27;

	public static final String HARVEST_STOP = "HARVEST_STOP", NO_HARVEST = "NO_HARVEST",
			DECO_FRAME_ATTACHED = "DECO_FRAME_ATTACHED";

	public static List<PC_IMSG> getMSGObjects() {
		return new ArrayList<PC_IMSG>(msgObjects);
	}

	public static void registerMSGObject(PC_IMSG obj) {
		msgObjects.add(obj);
	}

	public static Object callBlockMSG(IBlockAccess world, PC_VecI pos, int msg, Object... o) {
		return callBlockMSG(world, pos.x, pos.y, pos.z, msg, o);
	}

	public static Object callBlockMSG(IBlockAccess world, int x, int y, int z, int msg, Object... o) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		if (block instanceof PC_IMSG) {
			return ((PC_IMSG) block).msg(msg, o);
		}
		return null;
	}

	public static List<Object> callAllMSG(int msg, Object... o) {
		List<Object> l = new ArrayList<Object>();
		List<PC_IMSG> objs = getMSGObjects();
		for (PC_IMSG obj : objs) {
			Object ret = obj.msg(msg, o);
			if (ret != null) {
				l.add(ret);
			}
		}
		return l;
	}

	public static interface MSGIterator {
		public Object onRet(Object o);
	}

	public static Object callAllMSG(MSGIterator iterator, int msg, Object... o) {
		List<PC_IMSG> objs = getMSGObjects();
		for (PC_IMSG obj : objs) {
			Object ret = obj.msg(msg, o);
			ret = iterator.onRet(ret);
			if (ret != null)
				return ret;

		}
		return null;
	}

	public static boolean hasFlag(World world, PC_VecI pos, String flag) {
		Block b = PC_Utils.getBlock(world, pos);
		if (b == null)
			return false;
		if (b.getClass().isAnnotationPresent(PC_BlockFlag.class)) {
			String[] list = b.getClass().getAnnotation(PC_BlockFlag.class).flags();
			for (String key : list) {
				if (key.equals(flag)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasFlag(ItemStack is, String flag) {
		Item i = is.getItem();
		if (i instanceof ItemBlock) {
			Block b = Block.getBlockFromItem(i);
			if (b instanceof PC_IMSG) {
				List<String> list = (List<String>) ((PC_IMSG) b).msg(MSG_ITEM_FLAGS, is, new ArrayList<String>());
				if (list != null) {
					return list.contains(flag);
				}
			}
		}
		if (i instanceof PC_IMSG) {
			List<String> list = (List<String>) ((PC_IMSG) i).msg(MSG_ITEM_FLAGS, is, new ArrayList<String>());
			if (list != null) {
				return list.contains(flag);
			}
		}

		return false;
	}

}
