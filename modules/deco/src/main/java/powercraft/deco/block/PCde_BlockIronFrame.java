package powercraft.deco.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Utils;
import powercraft.deco.PCde_App;
import powercraft.deco.model.PCde_ModelIronFrame;
import powercraft.deco.tile.PCde_TileEntityIronFrame;
import powercraft.launcher.mod_PowerCraft;

@PC_BlockInfo(name = "IronFrame", tileEntity = PCde_TileEntityIronFrame.class)
public class PCde_BlockIronFrame extends PC_Block implements PC_IItemInfo {

	public PCde_BlockIronFrame(int id) {
		super(Material.iron, "ironframe");
		setHardness(1.5F);
		setResistance(50.0F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PCde_ModelIronFrame model = new PCde_ModelIronFrame();
		float f = 1.0F;

		Minecraft.getMinecraft().getTextureManager()
				.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, PC_TextureRegistry.getPowerCraftImageDir()
						+ PC_TextureRegistry.getTextureName(PCde_App.instance, "block_deco.png")));

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);

		model.setFrameParts(0, false);
		model.setFrameParts(1, false);
		model.setFrameParts(2, false);
		model.setFrameParts(3, false);
		model.setFrameParts(4, false);
		model.render();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();

		return true;

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		/*
		 * PCde_TileEntityIronFrame te = (PCde_TileEntityIronFrame)PC_Utils.getTE(world,
		 * x, y, z); PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new
		 * PC_VecF(0, 0, 0), new PC_VecF(0.2F, 1, 0.2F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.8F,
		 * 0, 0), new PC_VecF(1F, 1, 0.2F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0, 0,
		 * 0.8F), new PC_VecF(0.2F, 1, 1F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.8F,
		 * 0, 0.8F), new PC_VecF(1F, 1, 1F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * //Bottom PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new
		 * PC_VecF(0.2F, 0, 0F), new PC_VecF(0.8F, 0.2F, 0.2F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0F, 0,
		 * 0.2F), new PC_VecF(0.2F, 0.2F, 0.8F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.2F,
		 * 0, 0.8F), new PC_VecF(0.8F, 0.2F, 1F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.8F,
		 * 0, 0.2F), new PC_VecF(1F, 0.2F, 0.8F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * //top PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new
		 * PC_VecF(0.2F, 0.8F, 0F), new PC_VecF(0.8F, 1F, 0.2F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0F,
		 * 0.8F, 0.2F), new PC_VecF(0.2F, 1F, 0.8F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.2F,
		 * 0.8F, 0.8F), new PC_VecF(0.8F, 1F, 1F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * PC_Utils.setBlockBoundsAndCollision(Blocks.iron_block, te, new PC_VecF(0.8F,
		 * 0.8F, 0.2F), new PC_VecF(1F, 1F, 0.8F), new PC_VecI(x, y, z));
		 * PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 * 
		 * //PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(-5F, -8.5F, -5F),
		 * new PC_VecF(10F, 4F, 10F), new PC_VecI(x, y, z));
		 * //PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		 */
		return true;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list,
			Entity entity) {
		if (entity instanceof EntityEnderEye)
			return;
		if (entity instanceof EntityFireworkRocket)
			return;
		if (entity instanceof EntityItem)
			return;
		if (entity instanceof EntityXPOrb)
			return;
		if (PC_Utils.isEntityFX(entity))
			return;
		if (entity == null)
			return;
		setBlockBounds(0, 0, 0, 1, 1, 1);
		AxisAlignedBB axisalignedbb1 = super.getCollisionBoundingBoxFromPool(world, x, y, z);

		if (axisalignedbb1 != null && axisAlignedBB.intersectsWith(axisalignedbb1)) {
			list.add(axisalignedbb1);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}

}
