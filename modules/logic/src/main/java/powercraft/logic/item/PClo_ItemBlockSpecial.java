package powercraft.logic.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Lang;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.utils.PC_MathHelper;
import powercraft.logic.PClo_App;
import powercraft.logic.type.PClo_SpecialType;

public class PClo_ItemBlockSpecial extends PC_ItemBlock {

	public PClo_ItemBlockSpecial(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		for (int i = 0; i < PClo_SpecialType.TOTAL_SPECIAL_COUNT; i++) {
			arrayList.add(new ItemStack(this, 1, i));
		}

		return arrayList;
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return PClo_App.special.getIcon(1, 0);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + ".special" + itemstack.getItemDamage();
	}

	@Override
	public boolean isFull3D() {
		return false;
	}

	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b) {
		list.add(getDescriptionForSpecial(itemStack.getItemDamage()));
	}

	public static String getDescriptionForSpecial(int dmg) {
		return PC_Lang.tr("pc.special."
				+ PClo_SpecialType.names[PC_MathHelper.clamp_int(dmg, 0, PClo_SpecialType.TOTAL_SPECIAL_COUNT - 1)]
				+ ".desc");
	}

}
