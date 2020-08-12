package powercraft.machines.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.tile.PCma_TileEntityXPBank;

@PC_BlockInfo(name = "XP Bank", tileEntity = PCma_TileEntityXPBank.class)
public class PCma_BlockXPBank extends PC_Block implements PC_IItemInfo {
	public PCma_BlockXPBank(int id) {
		super(Material.ground, "xpbank");
		setStepSound(Block.soundTypeMetal);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setHardness(6.0F);
		setResistance(100.0F);
		setLightLevel(0.5F);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	private float calculateHeightMultiplier(int xp) {
		return Math.min(xp / 500F, 1F);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.9F, 0.8F);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {

		PC_GresRegistry.openGres("XPBank", entityplayer, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void onBlockHarvested(World world, int i, int j, int k, int par5, EntityPlayer player) {
		try {
			((PCma_TileEntityXPBank) world.getTileEntity(i, j, k)).withdrawXP(player);
		} catch (NullPointerException npe) {
		}
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.0F, 0.3F, 0.0F, 1.0F, 0.8F, 1.0F);
		PC_Renderer.renderInvBox(renderer, Blocks.obsidian, 0);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.0F, 0.0F, 0.0F, 0.2F, 0.3F, 0.2F);
		PC_Renderer.renderInvBox(renderer, Blocks.obsidian, 0);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.8F, 0.0F, 0.0F, 1.0F, 0.3F, 0.2F);
		PC_Renderer.renderInvBox(renderer, Blocks.obsidian, 0);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.0F, 0.0F, 0.8F, 0.2F, 0.3F, 1.0F);
		PC_Renderer.renderInvBox(renderer, Blocks.obsidian, 0);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.8F, 0.0F, 0.8F, 1.0F, 0.3F, 1.0F);
		PC_Renderer.renderInvBox(renderer, Blocks.obsidian, 0);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		PCma_TileEntityXPBank te = (PCma_TileEntityXPBank) world.getTileEntity(x, y, z);

		te.clearAABBList();

		int xp = te.getXP();
		PC_Utils.setBlockBoundsAndCollision(this, te,
				new PC_VecF(0.15F, 0.29F - 0.2F * calculateHeightMultiplier(xp), 0.15F),
				new PC_VecF(0.85F, 0.71F + 0.2F * calculateHeightMultiplier(xp), 0.85F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(Blocks.obsidian, te, new PC_VecF(0.0F, 0.3F, 0.0F),
				new PC_VecF(1.0F, 0.7F, 1.0F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, Blocks.obsidian, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(Blocks.obsidian, te, new PC_VecF(0.0F, 0.0F, 0.0F),
				new PC_VecF(0.15F, 0.3F, 0.15F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, Blocks.obsidian, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(Blocks.obsidian, te, new PC_VecF(0.85F, 0.0F, 0.0F),
				new PC_VecF(1.0F, 0.3F, 0.15F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, Blocks.obsidian, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(Blocks.obsidian, te, new PC_VecF(0.0F, 0.0F, 0.85F),
				new PC_VecF(0.15F, 0.3F, 1.0F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, Blocks.obsidian, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(Blocks.obsidian, te, new PC_VecF(0.85F, 0.0F, 0.85F),
				new PC_VecF(1.0F, 0.3F, 1.0F), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, Blocks.obsidian, x, y, z);
		PC_Utils.setBlockBounds(Blocks.obsidian, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return true;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

}
