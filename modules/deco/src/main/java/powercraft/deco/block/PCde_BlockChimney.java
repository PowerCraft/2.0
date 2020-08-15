package powercraft.deco.block;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.deco.item.PCde_ItemBlockChimney;
import powercraft.deco.tile.PCde_TileEntityChimney;

@PC_BlockInfo(name = "Chimney", itemBlock = PCde_ItemBlockChimney.class, tileEntity = PCde_TileEntityChimney.class)
public class PCde_BlockChimney extends PC_Block {

	public PCde_BlockChimney(int id) {
		super(Material.rock);
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
	public IIcon getIcon(PC_Direction par1, int par2) {
		if (par2 == 0)
			return Blocks.cobblestone.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 1)
			return Blocks.brick_block.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 2)
			return Blocks.stonebrick.getIcon(par1.getMCDir(), 0);

		if (par2 == 3)
			return Blocks.stonebrick.getIcon(par1.getMCDir(), 2);
		if (par2 == 4)
			return Blocks.stonebrick.getIcon(par1.getMCDir(), 3);

		if (par2 == 5)
			return Blocks.sandstone.getIcon(par1.getMCDir(), 0);
		if (par2 == 6)
			return Blocks.sandstone.getIcon(par1.getMCDir(), 1);
		if (par2 == 7)
			return Blocks.sandstone.getIcon(par1.getMCDir(), 2);

		if (par2 == 8)
			return Blocks.nether_brick.getIcon(par1.getMCDir(), 1);

		if (par2 == 9)
			return Blocks.quartz_block.getIcon(par1.getMCDir(), 0);
		if (par2 == 10)
			return Blocks.quartz_block.getIcon(par1.getMCDir(), 1);
		if (par2 == 11)
			return Blocks.quartz_block.getIcon(par1.getMCDir(), 2);

		if (par2 == 12)
			return Blocks.mossy_cobblestone.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 13)
			return Blocks.stonebrick.getIcon(par1.getMCDir(), 1);

		if (par2 == 14)
			return Blocks.hardened_clay.getBlockTextureFromSide(par1.getMCDir());
		if (par2 == 15)
			return Blocks.iron_block.getBlockTextureFromSide(par1.getMCDir());

		return null;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		final float px = 0.0625F;
		float w = px * 3;

		setBlockBounds(0, 0, 0, 1, px, w);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0, 1-px, 0, 1, 1, w);
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(1 - w, 0, w, 1, px, 1-w); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		setBlockBounds(1 - w, 1-px, w, 1, 1, 1-w); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(0, 0, 1 - w, 1, px, 1);
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		setBlockBounds(0, 1-px, 1 - w, 1, 1, 1);
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		
		setBlockBounds(0, 0, w, w, px, 1-w); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		setBlockBounds(0, 1-px, w, w, 1, 1-w); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		
		setBlockBounds(0 + px, 0+px, 0, 1 - px, 1-px, w);
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(1 - w, 0+px, 0 + px, 1, 1-px, 1 - px); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(0 + px, 0+px, 1 - w, 1 - px, 1-px, 1);
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(0, 0+px, 0 + px, w, 1-px, 1 - px); 
		PC_Renderer.renderInvBox(renderer, this, metadata); 
		
		setBlockBounds(0, 0, 0, 1, 1, 1);

		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {

		final float px = 0.0625F;
		float w = px * 3;
		
		setBlockBounds(0, 0, 0, 1, px, w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(0, 1-px, 0, 1, 1, w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(1 - w, 0, w, 1, px, 1-w); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		setBlockBounds(1 - w, 1-px, w, 1, 1, 1-w); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(0, 0, 1 - w, 1, px, 1);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		setBlockBounds(0, 1-px, 1 - w, 1, 1, 1);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		
		setBlockBounds(0, 0, w, w, px, 1-w); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		setBlockBounds(0, 1-px, w, w, 1, 1-w); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		
		setBlockBounds(0 + px, 0+px, 0, 1 - px, 1-px, w);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(1 - w, 0+px, 0 + px, 1, 1-px, 1 - px); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(0 + px, 0+px, 1 - w, 1 - px, 1-px, 1);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(0, 0+px, 0 + px, w, 1-px, 1 - px); 
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z); 
		
		setBlockBounds(0, 0, 0, 1, 1, 1);

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
