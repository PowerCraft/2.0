package powercraft.logic.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.annotation.PC_Shining.OFF;
import powercraft.api.annotation.PC_Shining.ON;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Property;
import powercraft.logic.item.PClo_ItemBlockDelayer;
import powercraft.logic.tile.PClo_TileEntityDelayer;
import powercraft.logic.type.PClo_DelayerType;

@PC_Shining
@PC_BlockInfo(name = "Delayer", itemBlock = PClo_ItemBlockDelayer.class, tileEntity = PClo_TileEntityDelayer.class, canPlacedRotated = true)
public class PClo_BlockDelayer extends PC_Block {
	@ON
	public static PClo_BlockDelayer on;
	@OFF
	public static PClo_BlockDelayer off;

	public PClo_BlockDelayer(boolean on) {
		super(Material.ground, PClo_DelayerType.getTextures());
		setHardness(0.35F);
		setStepSound(Block.soundTypeWood);
		disableStats();
		setResistance(30.0F);
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);

		if (on)
			setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void initConfig(PC_Property config) {
		super.initConfig(config);
		on.setLightLevel(config.getInt("brightness", 7) * 0.0625F);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote)
			return;

		PClo_TileEntityDelayer te = getTE(world, x, y, z);
		boolean data = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0;

		switch (te.getType()) {
		case PClo_DelayerType.FIFO:
			boolean stop = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.RIGHT) > 0;
			boolean reset = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.LEFT) > 0;
			boolean[] stateBuffer = te.getStateBuffer();

			if (!stop && !reset) {
				boolean shouldState = stateBuffer[stateBuffer.length - 1];

				if (shouldState != isActive(world, x, y, z)) {
					PC_Utils.setBlockState(world, x, y, z, shouldState);
				}

				for (int i = stateBuffer.length - 1; i > 0; i--) {
					stateBuffer[i] = stateBuffer[i - 1];
				}

				stateBuffer[0] = data;
			}

			if (reset) {
				if (isActive(world, x, y, z)) {
					PC_Utils.setBlockState(world, x, y, z, false);
				}

				for (int i = 0; i < stateBuffer.length; i++) {
					stateBuffer[i] = false;
				}
			}
			break;
		case PClo_DelayerType.HOLD:

			boolean should = true;

			if (te.decRemainingTicks()) {
				should = false;
			}

			if (data) {
				te.resetRemainingTicks();
				should = true;
			}

			if (isActive(world, x, y, z) != should) {
				PC_Utils.setBlockState(world, x, y, z, should);
			}

		}
	}

	@Override
	public int tickRate(World par1World) {
		return 1;
	}

	@Override
	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return getProvidingStrongRedstonePowerValue(world, x, y, z, dir);
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		if (isActive(world, x, y, z) && dir == PC_Direction.FRONT) {
			return 15;
		}

		return 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PC_Renderer.glScalef(1.6F, 1.6F, 1.6F);
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
		PC_Renderer.glTranslatef(0, 0.15F, 0);
		PC_Renderer.renderInvBlockRotatedBox(this, metadata, 0, renderer);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		PC_Renderer.renderBlockRotatedBox(world, x, y, z, this, 0, renderer);
		/*
		 * PC_TileEntity te = this.getTE(world, x, y, z); te.clearAABBList(); TODO:Maybe
		 * in future //z+ if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y, z+1,
		 * 0)) { PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F, 0.75F,
		 * 0.6F, 0.25F, 1F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //z-
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y, z-1, 0)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F, 0F, 0.6F,
		 * 0.25F, 0.25F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //x+
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x-1, y, z, 0)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0F, 0F, 0.4F, 0.25F,
		 * 0.25F, 0.6F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //x-
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x+1, y, z, 0)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.75F, 0F, 0.4F, 1F,
		 * 0.25F, 0.6F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); }
		 */
		return true;
	}

	public static PClo_TileEntityDelayer getTE(IBlockAccess world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		;

		if (te instanceof PClo_TileEntityDelayer) {
			return (PClo_TileEntityDelayer) te;
		}

		return null;
	}

	public static int getType(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntityDelayer te = getTE(world, x, y, z);

		if (te != null) {
			return te.getType();
		}

		return 0;
	}

	public static boolean isActive(IBlockAccess world, int x, int y, int z) {
		return PC_Utils.getBID(world, x, y, z) == on;
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if (side == 1)
			return sideIcons[getType(iblockaccess, x, y, z) + 2
					+ (isActive(iblockaccess, x, y, z) ? 0 : PClo_DelayerType.TOTAL_DELAYER_COUNT)];

		if (side == 0)
			return sideIcons[0];

		return sideIcons[1];
	}

	@Override
	public IIcon getIcon(PC_Direction side, int meta) {
		if (side == PC_Direction.BOTTOM)
			return sideIcons[0];

		if (side == PC_Direction.TOP)
			return sideIcons[meta + 2];
		else
			return sideIcons[1];
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z) {
		if (this.getTE(iblockaccess, x, y, z) != null) {
			this.getTE(iblockaccess, x, y, z).clearAABBList();
			PC_Utils.setBlockBoundsAndCollision(this, this.getTE(iblockaccess, x, y, z), 0.25F, 0F, 0.25F, 0.75F, 0.5F,
					0.75F, x, y, z);
		} else
			setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack) {
		if (entityliving instanceof EntityPlayer)
			PC_GresRegistry.openGres("Delayer", (EntityPlayer) entityliving,
					PC_Utils.<PC_TileEntity>getTE(world, x, y, z));
		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		PC_Utils.hugeUpdate(world, x, y, z);
		onNeighborBlockChange(world, x, y, z, this);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		PC_GresRegistry.openGres("Delayer", player, PC_Utils.<PC_TileEntity>getTE(world, x, y, z));
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (!isActive(world, x, y, z))
			return;

		if (random.nextInt(3) != 0)
			return;

		double d = (x + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double d1 = (y + 0.2F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double d2 = (z + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		world.spawnParticle("reddust", d, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

}
