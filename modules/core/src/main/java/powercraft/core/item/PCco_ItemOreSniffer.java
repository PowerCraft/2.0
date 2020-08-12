package powercraft.core.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Lang;
import powercraft.api.item.PC_Item;
import powercraft.api.registry.PC_GresRegistry;

public class PCco_ItemOreSniffer extends PC_Item {
	public PCco_ItemOreSniffer(int id) {
		super("oresniffer");
		setMaxStackSize(1);
		setMaxDamage(500);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l,
			float par8, float par9, float par10) {
		if (world.isRemote) {
			PC_GresRegistry.openGres("OreSnifferResultScreen", entityplayer, null, i, j, k, l);
		}

		itemstack.damageItem(1, entityplayer);
		return false;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b) {
		list.add(PC_Lang.tr("pc.sniffer.desc"));
	}

}
