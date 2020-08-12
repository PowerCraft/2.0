package powercraft.net.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Lang;
import powercraft.api.item.PC_Item;
import powercraft.api.utils.PC_Utils;
import powercraft.net.PCnt_RadioManager;

public class PCnt_ItemRadioRemote extends PC_Item {

	/**
	 * @param i ID
	 */
	public PCnt_ItemRadioRemote(int i) {
		super("remote");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!itemstack.hasTagCompound()) {
			onCreated(itemstack, world, entityplayer);
		}

		if (!world.isRemote && !itemstack.getTagCompound().getBoolean("inUse")) {
			PCnt_RadioManager.remoteOn(itemstack.getTagCompound().getString("channel"));
			itemstack.getTagCompound().setBoolean("inUse", true);
		}
		world.playSoundAtEntity(entityplayer, "random.click", (world.rand.nextFloat() + 0.7F) / 2.0F,
				1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);

		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 0x11940; // dunno why, this is taken from bow item
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		super.onPlayerStoppedUsing(itemstack, world, entityplayer, i);
		if (!world.isRemote) {
			PCnt_RadioManager.remoteOff(itemstack.getTagCompound().getString("channel"));
			itemstack.getTagCompound().setBoolean("inUse", false);
		}
		world.playSoundAtEntity(entityplayer, "random.click", (world.rand.nextFloat() + 0.7F) / 2.0F,
				1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("channel", "default");

		itemstack.setTagCompound(tag);
	}

	/**
	 * Set channel to this stack
	 * 
	 * @param itemstack stack
	 * @param channel   new channel
	 * @param output
	 */
	public static void setChannel(ItemStack itemstack, String channel, boolean output) {

		NBTTagCompound tag = itemstack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			itemstack.setTagCompound(tag);
		}

		tag.setString("channel", channel == null ? "default" : channel);
		if (output) {
			PC_Utils.chatMsg(PC_Lang.tr("pc.radioRemote.connected", new Object[] { channel }));
		}
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		if (itemstack.hasTagCompound()) {
			list.add(PC_Lang.tr("pc.radioRemote.desc",
					new Object[] { itemstack.getTagCompound().getString("channel") }));
		}
	}

}
