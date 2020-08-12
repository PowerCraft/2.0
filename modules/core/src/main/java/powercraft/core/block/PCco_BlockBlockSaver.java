package powercraft.core.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.utils.PC_VecI;
import powercraft.core.item.PCco_ItemBlockBlockSaver;

@PC_BlockInfo(name = "Block Saver", itemBlock = PCco_ItemBlockBlockSaver.class)
public class PCco_BlockBlockSaver extends PC_Block implements PC_IItemInfo {

	public PCco_BlockBlockSaver(int id) {
		super(Material.wood);
	}

	public int getRenderType() {
		return 22;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		return arrayList;
	}

	@Override
	public boolean showInCraftingTool() {
		return false;
	}

}
