package powercraft.logic.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Property;
import powercraft.logic.item.PClo_ItemBlockFlipFlop;
import powercraft.logic.tile.PClo_TileEntityFlipFlop;
import powercraft.logic.type.PClo_FlipFlopType;

@PC_Shining
@PC_BlockInfo(name = "FlipFlop", itemBlock = PClo_ItemBlockFlipFlop.class, tileEntity = PClo_TileEntityFlipFlop.class, canPlacedRotated = true)
public class PClo_BlockFlipFlop extends PC_Block {
	private static Random rand = new Random();

	@ON
	public static PClo_BlockFlipFlop on;
	@OFF
	public static PClo_BlockFlipFlop off;

	public PClo_BlockFlipFlop(boolean on) {
		super(Material.ground, PClo_FlipFlopType.getTextures());
		setHardness(0.35F);
		setStepSound(Block.soundTypeWood);
		disableStats();
		setResistance(30.0F);
		setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);

		if (on)
			setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void initConfig(PC_Property config) {
		super.initConfig(config);
		on.setLightLevel(config.getInt("brightness", 7) * 0.0625F);
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		PClo_TileEntityFlipFlop te = getTE(world, x, y, z);
		boolean state = isActive(world, x, y, z);
		boolean i1 = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.RIGHT) > 0;
		boolean i2 = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0;
		boolean i3 = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.LEFT) > 0;
		boolean shouldState = state;

		switch (te.getType()) {
		case PClo_FlipFlopType.D:

			if (i3) {
				shouldState = false;
			}

			if (i1) {
				shouldState = i2;
			}

			break;

		case PClo_FlipFlopType.RS:

			if (i1) {
				shouldState = false;
			}

			if (i3) {
				shouldState = true;
			}

			break;

		case PClo_FlipFlopType.T:

			if (i2) {
				if (!te.getClock()) {
					te.setClock(true);
					shouldState = !state;
				}
			} else {
				if (te.getClock()) {
					te.setClock(false);
				}
			}

			if (i1 || i3) {
				shouldState = false;
			}

			break;

		case PClo_FlipFlopType.RANDOM:

			if (i2) {
				if (!te.getClock()) {
					te.setClock(true);
					shouldState = rand.nextBoolean();
				}
			} else {
				if (te.getClock()) {
					te.setClock(false);
				}
			}
		}

		if (state != shouldState) {
			PC_Utils.setBlockState(world, x, y, z, shouldState);
		}
	}

	@Override
	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return getProvidingStrongRedstonePowerValue(world, x, y, z, dir);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itmeStack) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		PC_Utils.hugeUpdate(world, x, y, z);
		onNeighborBlockChange(world, x, y, z, this);
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		if (!isActive(world, x, y, z)) {
			return 0;
		}

		if (PC_Direction.FRONT == dir) {
			return 15;
		}

		if (getType(world, x, y, z) == PClo_FlipFlopType.RS) {
			if (PC_Direction.BACK == dir) {
				return 15;
			}
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
	public int getRenderType() {
		return PC_Renderer.getRendererID(true);
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PC_Renderer.glScalef(1.6F, 1.6F, 1.6F);
		setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);
		PC_Renderer.glTranslatef(0, 0.15F, 0);
		PC_Renderer.renderInvBlockRotatedBox(this, metadata, 0, renderer);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		PC_Renderer.renderBlockRotatedBox(world, x, y, z, this, 0, renderer);
		/*
		 * PC_TileEntity te = this.getTE(world, x, y, z); te.clearAABBList(); TODO:
		 * Maybe in future //z+ if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y,
		 * z+1, 0)) { PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F,
		 * 0.75F, 0.6F, 0.25F, 1F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x,
		 * y, z, PClo_App.wire, 0, renderer); } //z-
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

	public static PClo_TileEntityFlipFlop getTE(IBlockAccess world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		;

		if (te instanceof PClo_TileEntityFlipFlop) {
			return (PClo_TileEntityFlipFlop) te;
		}

		return null;
	}

	public static int getType(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntityFlipFlop te = getTE(world, x, y, z);

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
		if (side == 1) {
			return sideIcons[getType(iblockaccess, x, y, z) + 2
					+ (isActive(iblockaccess, x, y, z) ? 0 : PClo_FlipFlopType.TOTAL_FLIPFLOP_COUNT)];
		}

		if (side == 0) {
			return sideIcons[0];
		}

		return sideIcons[1];
	}

	@Override
	public IIcon getIcon(PC_Direction side, int meta) {
		if (side == PC_Direction.BOTTOM) {
			return sideIcons[0];
		}

		if (side == PC_Direction.TOP) {
			return sideIcons[meta + 2];
		} else {
			return sideIcons[1];
		}
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
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (!isActive(world, x, y, z)) {
			return;
		}

		if (random.nextInt(3) != 0) {
			return;
		}

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
