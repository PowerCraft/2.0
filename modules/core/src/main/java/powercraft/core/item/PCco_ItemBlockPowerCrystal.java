package powercraft.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_MathHelper;

public class PCco_ItemBlockPowerCrystal extends PC_ItemBlock {

	public PCco_ItemBlockPowerCrystal(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return super.getUnlocalizedName() + ".color" + Integer.toString(itemstack.getItemDamage());
	}

	@Override
	public int getColorFromItemStack(ItemStack itemStack, int pass) {
		return PC_Color.crystal_colors[PC_MathHelper.clamp_int(itemStack.getItemDamage(), 0, 7)];
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.rare;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		for (int i = 0; i < 8; i++) {
			arrayList.add(new ItemStack(this, 1, i));
		}
		return arrayList;
	}

}
