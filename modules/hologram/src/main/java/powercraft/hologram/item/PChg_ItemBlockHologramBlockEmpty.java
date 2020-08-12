package powercraft.hologram.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;

public class PChg_ItemBlockHologramBlockEmpty extends PC_ItemBlock {

	public PChg_ItemBlockHologramBlockEmpty(Block block) {
		super(block);
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return true;
	}

}
