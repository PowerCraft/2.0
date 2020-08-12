package powercraft.logic.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
import powercraft.logic.item.PClo_ItemBlockRepeater;
import powercraft.logic.tile.PClo_TileEntityRepeater;
import powercraft.logic.type.PClo_RepeaterType;

@PC_Shining
@PC_BlockInfo(name = "Repeater", itemBlock = PClo_ItemBlockRepeater.class, tileEntity = PClo_TileEntityRepeater.class, canPlacedRotated = true)
public class PClo_BlockRepeater extends PC_Block {
	@ON
	public static PClo_BlockRepeater on;
	@OFF
	public static PClo_BlockRepeater off;

	public PClo_BlockRepeater(boolean on) {
		super(Material.ground, PClo_RepeaterType.getTextures());
		setHardness(0.35F);
		setStepSound(Block.soundTypeWood);
		disableStats();
		setResistance(30.0F);
		setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);

		if (on) {
			setCreativeTab(CreativeTabs.tabRedstone);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itmeStack) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		PC_Utils.hugeUpdate(world, x, y, z);
	}

	@Override
	public void initConfig(PC_Property config) {
		super.initConfig(config);
		on.setLightLevel(config.getInt("brightness", 7) * 0.0625F);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		PClo_TileEntityRepeater te = getTE(world, x, y, z);

		if (te.getType() == PClo_RepeaterType.CROSSING) {
			int[] inp = { getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.LEFT) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.RIGHT) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.FRONT) > 0 ? 1 : 0 };
			int variant = te.getInp();
			int shouldState = 0;

			switch (variant) {
			case 0:
				shouldState = inp[0] | inp[1] << 1;
				break;

			case 1:
				shouldState = inp[1] | inp[2] << 1;
				break;

			case 2:
				shouldState = inp[0] | inp[3] << 1;
				break;

			case 3:
				shouldState = inp[2] | inp[3] << 1;
				break;
			}

			if (te.getState() != shouldState) {
				te.setState(shouldState);
			}
			if ((shouldState != 0) != isActive(world, x, y, z)) {
				PC_Utils.setBlockState(world, x, y, z, shouldState != 0);
			}
		} else {
			boolean shouldState = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0;

			if (isActive(world, x, y, z) != shouldState) {
				PC_Utils.setBlockState(world, x, y, z, shouldState);
			}
		}
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote)
			return;

		PClo_TileEntityRepeater te = getTE(world, x, y, z);

		if (te.getType() == PClo_RepeaterType.CROSSING) {
			int[] inp = { getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.LEFT) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.RIGHT) > 0 ? 1 : 0,
					getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.FRONT) > 0 ? 1 : 0 };
			int variant = te.getInp();
			int shouldState = 0;

			switch (variant) {
			case 0:
				shouldState = inp[0] | inp[1] << 1;
				break;

			case 1:
				shouldState = inp[1] | inp[2] << 1;
				break;

			case 2:
				shouldState = inp[0] | inp[3] << 1;
				break;

			case 3:
				shouldState = inp[2] | inp[3] << 1;
				break;
			}

			if (te.getState() != shouldState) {
				te.setState(shouldState);
			}
			if ((shouldState != 0) != isActive(world, x, y, z)) {
				PC_Utils.setBlockState(world, x, y, z, shouldState != 0);
			}
		} else {
			boolean shouldState = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0;

			if (isActive(world, x, y, z) != shouldState) {
				PC_Utils.setBlockState(world, x, y, z, shouldState);
			}
		}

		/*
		 * if (te.getType() == PClo_RepeaterType.REPEATER_STRAIGHT_I || te.getType() ==
		 * PClo_RepeaterType.REPEATER_CORNER_I){ updateTick(world, x, y, z, new
		 * Random()); }else{ boolean shouldState = getRedstonePowerValueFromInput(world,
		 * x, y, z, PC_Direction.BACK)>0; if (isActive(world, x, y, z) != shouldState ||
		 * te.getType() == PClo_RepeaterType.CROSSING){ world.scheduleBlockUpdate(x, y,
		 * z, block, tickRate(world)); } TODO: work without it?? }
		 */
	}

	@Override
	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return getProvidingStrongRedstonePowerValue(world, x, y, z, dir);
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		PClo_TileEntityRepeater te = getTE(world, x, y, z);
		int type = te.getType();
		boolean L = false, R = false, F = false, B = false;
		int variant = te.getInp();

		if (type == PClo_RepeaterType.CROSSING) {
			int state = te.getState();
			boolean power1 = (state & 1) != 0;
			boolean power2 = (state & 2) != 0;

			switch (variant) {
			case 0:
				if (dir == PC_Direction.RIGHT) {
					return power1 ? 15 : 0;
				}

				if (dir == PC_Direction.FRONT) {
					return power2 ? 15 : 0;
				}
				break;
			case 1:
				if (dir == PC_Direction.FRONT) {
					return power1 ? 15 : 0;
				}

				if (dir == PC_Direction.LEFT) {
					return power2 ? 15 : 0;
				}
				break;

			case 2:
				if (dir == PC_Direction.RIGHT) {
					return power1 ? 15 : 0;
				}

				if (dir == PC_Direction.BACK) {
					return power2 ? 15 : 0;
				}
				break;
			case 3:
				if (dir == PC_Direction.LEFT) {
					return power1 ? 15 : 0;
				}

				if (dir == PC_Direction.BACK) {
					return power2 ? 15 : 0;
				}
				break;
			}
			return 0;
		}
		boolean power = isActive(world, x, y, z);

		if (!power)
			return 0;

		if (type == PClo_RepeaterType.SPLITTER_I) {
			L = variant != 3;
			R = variant != 1;
			F = variant != 2;
			B = false;
		} else if (type == PClo_RepeaterType.REPEATER_STRAIGHT_I || type == PClo_RepeaterType.REPEATER_STRAIGHT) {
			F = true;
		} else if (type == PClo_RepeaterType.REPEATER_CORNER_I || type == PClo_RepeaterType.REPEATER_CORNER) {
			L = variant == 0;
			R = variant == 1;
		}

		if (dir == PC_Direction.LEFT)
			return L ? 15 : 0;
		if (dir == PC_Direction.RIGHT)
			return R ? 15 : 0;
		if (dir == PC_Direction.FRONT)
			return F ? 15 : 0;
		if (dir == PC_Direction.BOTTOM)
			return B ? 15 : 0;

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
		// TODO:Maybe in future
		/*
		 * PC_TileEntity te = this.getTE(world, x, y, z); te.clearAABBList(); int sidez1
		 * = PClo_App.special.getRotation2(PC_Utils.getMD(world, x, y,
		 * z+1)).getMCSide(); int sidez2 =
		 * PClo_App.special.getRotation2(PC_Utils.getMD(world, x, y, z-1)).getMCSide();
		 * int sidex1 = PClo_App.special.getRotation2(PC_Utils.getMD(world, x-1, y,
		 * z)).getMCSide(); int sidex2 =
		 * PClo_App.special.getRotation2(PC_Utils.getMD(world, x+1, y, z)).getMCSide();
		 * 
		 * //z+ if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y, z+1, sidez1)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F, 0.75F, 0.6F,
		 * 0.25F, 1F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //z-
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y, z-1, sidez2)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F, 0F, 0.6F,
		 * 0.25F, 0.25F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //x+
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x-1, y, z, sidex1)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0F, 0F, 0.4F, 0.25F,
		 * 0.25F, 0.6F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); } //x-
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x+1, y, z, sidex2)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.75F, 0F, 0.4F, 1F,
		 * 0.25F, 0.6F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
		 * PClo_App.wire, 0, renderer); }
		 */
		return true;
	}

	public static PClo_TileEntityRepeater getTE(IBlockAccess world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);

		if (te instanceof PClo_TileEntityRepeater) {
			return (PClo_TileEntityRepeater) te;
		}

		return null;
	}

	public static int getType(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntityRepeater te = getTE(world, x, y, z);

		if (te != null) {
			return te.getType();
		}

		return 0;
	}

	public static int getInp(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntityRepeater te = getTE(world, x, y, z);

		if (te != null) {
			return te.getInp();
		}

		return 0;
	}

	public static boolean isActive(IBlockAccess world, int x, int y, int z) {
		return PC_Utils.getBID(world, x, y, z) == on;
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if (side == 1)
			return sideIcons[PClo_RepeaterType.getTextureIndex(getType(iblockaccess, x, y, z),
					isActive(iblockaccess, x, y, z)) + getInp(iblockaccess, x, y, z)];

		if (side == 0)
			return sideIcons[0];

		return sideIcons[1];
	}

	@Override
	public IIcon getIcon(PC_Direction side, int meta) {
		if (side == PC_Direction.BOTTOM)
			return sideIcons[0];

		if (side == PC_Direction.TOP)
			return sideIcons[PClo_RepeaterType.getTextureIndex(meta, true)];
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();

		if (ihold != null) {
			if (ihold.getItem() == Items.stick) {
				if (!world.isRemote) {
					getTE(world, x, y, z).change();
				}

				return true;
			}
		}

		return false;
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
