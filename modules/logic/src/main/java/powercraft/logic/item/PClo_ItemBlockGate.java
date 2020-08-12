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
import powercraft.logic.type.PClo_GateType;

public class PClo_ItemBlockGate extends PC_ItemBlock {
	public PClo_ItemBlockGate(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		for (int i = 0; i < PClo_GateType.TOTAL_GATE_COUNT; i++) {
			arrayList.add(new ItemStack(this, 1, i));
		}

		return arrayList;
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return PClo_App.gate.getIcon(1, 0);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + ".gate" + itemstack.getItemDamage();
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
		list.add(getDescriptionForGate(itemStack.getItemDamage()));
	}

	public static String getDescriptionForGate(int dmg) {
		return PC_Lang.tr("pc.gate."
				+ PClo_GateType.names[PC_MathHelper.clamp_int(dmg, 0, PClo_GateType.TOTAL_GATE_COUNT - 1)] + ".desc");
	}

}
