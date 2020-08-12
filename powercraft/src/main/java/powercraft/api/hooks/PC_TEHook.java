package powercraft.api.hooks;

import java.util.ArrayList;

import net.minecraft.util.AxisAlignedBB;
import powercraft.api.hooklib.asjasm.HookField;

public class PC_TEHook {

	@HookField(targetClassName = "net.minecraft.tileentity.TileEntity")
	private ArrayList<AxisAlignedBB> aabbs = new ArrayList<AxisAlignedBB>();

}
