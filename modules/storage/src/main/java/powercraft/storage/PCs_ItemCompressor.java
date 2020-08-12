package powercraft.storage;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import powercraft.api.item.PC_Item;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.utils.PC_Utils;

public class PCs_ItemCompressor extends PC_Item implements PC_IPacketHandler {

	public static final int NORMAL = 0, ENDERACCESS = 1;
	public static final String id2Name[] = { "normal", "enderaccess" };

	public PCs_ItemCompressor(int id) {
		super(id2Name[NORMAL] + "compressor", id2Name[ENDERACCESS] + "compressor");
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int l,
			float par8, float par9, float par10) {
		if (PC_Utils.getTE(world, x, y, z) != null && PC_Utils.getTE(world, x, y, z) instanceof TileEntityChest) {
			NBTTagCompound nbt = itemstack.getTagCompound();
			if (nbt == null)
				nbt = new NBTTagCompound();

			int[] pos = { x, y, z };
			nbt.setIntArray("posChest", pos);

			itemstack.setTagCompound(nbt);
			return true;
		} else {
			onItemRightClick(itemstack, world, player);
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				PC_GresRegistry.openGres("Compressor", player, null, new Object[] {});
				return itemstack;
			}
			NBTTagCompound nbt = itemstack.getTagCompound();
			int[] pos = { 0, 0, 0 };
			if (nbt != null)
				pos = nbt.getIntArray("posChest");
			if (PC_Utils.getTE(world, pos[0], pos[1], pos[2]) != null
					&& PC_Utils.getTE(world, pos[0], pos[1], pos[2]) instanceof TileEntityChest) {
				TileEntityChest te = (TileEntityChest) world.getTileEntity(pos[0], pos[1], pos[2]);

				te.func_145976_a(StatCollector.translateToLocal("item.PCs_ItemCompressor.normal.name") + ": ("
						+ StatCollector.translateToLocal("container.chest") + " " + pos[0] + ":" + pos[1] + ":" + pos[2]
						+ ")");
				player.displayGUIChest(te);
				te.func_145976_a(StatCollector.translateToLocal("container.chest"));
				return itemstack;
			}
			if (itemstack.getItemDamage() == ENDERACCESS) {
				InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
				player.displayGUIChest(inventoryenderchest);
				return itemstack;
			}
		}
		return itemstack;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this, 1, NORMAL));
		arrayList.add(new ItemStack(this, 1, ENDERACCESS));
		return arrayList;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + "." + id2Name[itemStack.getItemDamage()];
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		if (itemStack.hasTagCompound()) {
			if (itemStack.getTagCompound().hasKey("label"))
				list.add(itemStack.getTagCompound().getString("label"));
		}
	}

	public static String getName(ItemStack item) {
		if (item.hasTagCompound()) {
			if (item.getTagCompound().hasKey("label"))
				return item.getTagCompound().getString("label");
		}
		return "";
	}

	public static void setName(EntityPlayer player, String name) {
		NBTTagCompound nbtTag = getItem(player).getTagCompound();
		if (nbtTag == null) {
			nbtTag = new NBTTagCompound();
			getItem(player).setTagCompound(nbtTag);
		}
		if (name == null || name.equals(""))
			nbtTag.removeTag("label");
		else
			nbtTag.setString("label", name);
	}

	public static ItemStack getItem(EntityPlayer player) {
		return player.inventory.getCurrentItem();
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		String key = (String) o[0];
		if (key.equals("setName")) {
			setName(player, (String) o[1]);
		}
		return false;
	}

}
