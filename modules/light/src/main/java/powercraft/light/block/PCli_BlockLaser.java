package powercraft.light.block;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.light.PCli_App;
import powercraft.light.model.PCli_ModelLaser;
import powercraft.light.packet.PCli_PacketLaser;
import powercraft.light.tile.PCli_TileEntityLaser;

@PC_BlockInfo(name = "Laser", tileEntity = PCli_TileEntityLaser.class, canPlacedRotated = true)
public class PCli_BlockLaser extends PC_Block implements PC_IItemInfo {
	public PCli_BlockLaser(int id) {
		super(Material.ground, "laser");
		setStepSound(Block.soundTypeMetal);
		setHardness(0.7F);
		setResistance(10.0F);
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

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		PCli_TileEntityLaser te = PC_Utils.getTE(world, i, j, k);

		if (te != null && te.getItemStack() != null) {
			ItemStack ihold = entityplayer.getCurrentEquippedItem();
			if (ihold == null || ihold.getItem() == Items.stick) {
				if (!PC_Utils.isCreative(entityplayer)) {
					PC_Utils.dropItemStack(world, i, j, k, te.getItemStack().toItemStack());
				}
				te.setItemStack(null);
			}
		}
		return false;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		if (!PC_Utils.isCreative(player)) {
			PCli_TileEntityLaser te = PC_Utils.getTE(world, x, y, z);

			if (te != null && te.getItemStack() != null) {
				PC_Utils.dropItemStack(world, x, y, z, te.getItemStack().toItemStack());
			}
		}
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
		PC_Utils.hugeUpdate(world, i, j, k);
		super.breakBlock(world, i, j, k, par5, par6);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
		PCli_TileEntityLaser te = PC_Utils.getTE(world, x, y, z);

		if (te != null) {
			te.setKiller(PC_BlockRegistry.isBlock(world, new PC_VecI(x, y - 1, z), "PCma_BlockRoaster"));
			boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
			te.setPowered(powered);
			if (!world.isRemote)
				PC_PacketHandler.sendToAll(new PCli_PacketLaser(new Object[] { x, y, z, powered }));
		}

	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PCli_ModelLaser modelLaser = new PCli_ModelLaser();
		modelLaser.laserParts[7].showModel = false;
		modelLaser.laserParts[0].showModel = false;
		modelLaser.laserParts[1].showModel = false;
		modelLaser.laserParts[2].showModel = false;
		modelLaser.laserParts[3].showModel = false;

		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCli_App.instance, "laser.png"));

		PC_Renderer.glRotatef(-90, 0, 1, 0);
		PC_Renderer.glScalef(f, -f, -f);
		modelLaser.renderLaser();
		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		PC_Renderer.glPopMatrix();
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		GL11.glPushMatrix();
		PCli_TileEntityLaser te = (PCli_TileEntityLaser) PC_Utils.getTE(world, x, y, z);
		te.clearAABBList();
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.0F, 0.0F, 0.0F), new PC_VecF(1.0F, 0.1875F, 1.0F),
				new PC_VecI(x, y, z));
		if (te != null) {
			if (te.getBlockMetadata() == 0 || te.getBlockMetadata() == 2) {
				PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.25F, 0.25F, 0.125F),
						new PC_VecF(0.75F, 0.75F, 0.875F), new PC_VecI(x, y, z));
			}
			if (te.getBlockMetadata() == 1 || te.getBlockMetadata() == 3) {
				PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.125F, 0.25F, 0.25F),
						new PC_VecF(0.875F, 0.75F, 0.75F), new PC_VecI(x, y, z));
			}
		}
		PC_Utils.setBlockBounds(this, 0, 0, 0, 1, 1, 1);
		GL11.glPopMatrix();
		return true;
	}

}
