package powercraft.api.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.mod_PowerCraft;
import powercraft.launcher.loader.PC_ModuleObject;

public abstract class PC_ItemArmor extends ItemArmor implements PC_IItemInfo {
	public static final int HEAD = 0, TORSO = 1, LEGS = 2, FEET = 3;

	private PC_ModuleObject module;
	private boolean canSetTextureFile = true;
	private String armorTexture;
	private PC_ItemInfo replaced;
	private PC_ItemInfo thisItem;
	protected IIcon[] icons;
	private String[] textureNames;

	protected PC_ItemArmor(ArmorMaterial material, int type, String textureName, String... textureNames) {
		super(material, 2, type);
		thisItem = new PC_ItemInfo();
		this.textureNames = new String[1 + textureNames.length];
		icons = new IIcon[1 + textureNames.length];
		this.textureNames[0] = textureName;
		for (int i = 0; i < textureNames.length; i++) {
			this.textureNames[i + 1] = textureNames[i];
		}
	}

	public PC_ModuleObject getModule() {
		return module;
	}

	public void setModule(PC_ModuleObject module) {
		this.module = module;
	}

	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public boolean showInCraftingTool() {
		return true;
	}

	public void setArmorTextureFile(String armorTexture) {
		this.armorTexture = armorTexture;
	}

	@Override
	public Item setCreativeTab(CreativeTabs _default) {
		return super.setCreativeTab(PC_Utils.getCreativeTab(_default));
	}

	public IIcon getIconFromDamage(int par1) {
		if (par1 >= icons.length) {
			par1 = icons.length - 1;
		}
		return icons[par1];
	}

	public int getBurnTime(ItemStack fuel) {
		return 0;
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		for (int i = 0; i < textureNames.length; i++) {
			icons[i] = par1IconRegister.registerIcon(
					mod_PowerCraft.MODID + ":" + PC_TextureRegistry.getTextureName(module, textureNames[i]));
		}
	}

	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
		return mod_PowerCraft.MODID + ":" + PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(module, armorTexture);
	}
}
