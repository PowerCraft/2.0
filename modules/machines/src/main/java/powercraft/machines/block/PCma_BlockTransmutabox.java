package powercraft.machines.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import powercraft.machines.tile.PCma_TileEntityTransmutabox;

@PC_BlockInfo(name = "Transmutabox", tileEntity = PCma_TileEntityTransmutabox.class)
public class PCma_BlockTransmutabox extends PC_Block implements PC_IItemInfo {
	public PCma_BlockTransmutabox(int id) {
		super(Material.rock, "transmutabox");
		setHardness(1.5F);
		setResistance(50.0F);
		setStepSound(Block.soundTypeMetal);
		// setCreativeTab(CreativeTabs.tabDecorations);
	}

	public void receivePower(IBlockAccess world, int x, int y, int z, float power) {
		PCma_TileEntityTransmutabox te = PC_Utils.getTE(world, x, y, z);

		if (te != null) {
			te.addEnergy((int) power);
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		PC_GresRegistry.openGres("Transmutabox", player, PC_Utils.<PC_TileEntity>getTE(world, x, y, z));
		return true;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
		PC_Renderer.renderInvBox(renderer, this, metadata);
		setBlockBounds(0.0f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.8f, 0.0f, 0.0f, 1.0f, 0.2f, 0.2f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.8f, 0.8f, 0.0f, 1.0f, 1.0f, 0.2f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.8f, 0.8f, 0.8f, 1.0f, 1.0f, 1.0f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.0f, 0.8f, 0.8f, 0.2f, 1.0f, 1.0f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.0f, 0.0f, 0.8f, 0.2f, 0.2f, 1.0f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.0f, 0.8f, 0.0f, 0.2f, 1.0f, 0.2f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0.8f, 0.0f, 0.8f, 1.0f, 0.2f, 1.0f);
		PC_Renderer.renderInvBoxWithTexture(renderer, this, Blocks.iron_block.getBlockTextureFromSide(0));
		setBlockBounds(0, 0, 0, 1, 1, 1);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		PC_Renderer.tessellatorDraw();
		PC_Renderer.tessellatorStartDrawingQuads();
		setBlockBounds(0.1f, 0.1f, 0.1f, 0.9f, 0.9f, 0.9f);
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

		PC_Utils.setBlockBounds(Blocks.iron_block, 0.0f, 0.0f, 0.0f, 0.2f, 0.2f, 0.2f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.8f, 0.0f, 0.0f, 1.0f, 0.2f, 0.2f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.8f, 0.8f, 0.0f, 1.0f, 1.0f, 0.2f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.8f, 0.8f, 0.8f, 1.0f, 1.0f, 1.0f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.0f, 0.8f, 0.8f, 0.2f, 1.0f, 1.0f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.0f, 0.0f, 0.8f, 0.2f, 0.2f, 1.0f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.0f, 0.8f, 0.0f, 0.2f, 1.0f, 0.2f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.8f, 0.0f, 0.8f, 1.0f, 0.2f, 1.0f);
		PC_Renderer.renderStandardBlock(renderer, Blocks.iron_block, x, y, z);
		PC_Utils.setBlockBounds(Blocks.iron_block, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		PC_Renderer.tessellatorDraw();
		PC_Renderer.tessellatorStartDrawingQuads();
		return true;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

}
