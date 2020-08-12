package powercraft.transport.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_App;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.PCtr_MaterialElevator;
import powercraft.transport.item.PCtr_ItemBlockElevator;

@PC_BlockInfo(name = "Elevator", itemBlock = PCtr_ItemBlockElevator.class)
public class PCtr_BlockElevator extends PC_Block {
	private static final double BORDERS = 0.25D;
	private static final double BORDER_BOOST = 0.062D;

	public PCtr_BlockElevator(int id) {
		super(PCtr_MaterialElevator.getMaterial(), "elevator_up", "elevator_down");
		setHardness(0.5F);
		setResistance(8.0F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName("PCtr_BlockElevator");
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return true;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int meta) {
		return meta == 0 ? sideIcons[0] : sideIcons[1];
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.packItems(world, pos);

			if (PCtr_BeltHelper.storeItemIntoMinecart(world, pos, (EntityItem) entity)
					|| PCtr_BeltHelper.storeAllSides(world, pos, (EntityItem) entity)) {
				entity.setDead();
				return;
			}
		}

		boolean down = (PC_Utils.getMD(world, pos) == 1);
		PCtr_BeltHelper.entityPreventDespawning(world, pos, true, entity);
		boolean halted = world.isBlockIndirectlyGettingPowered(i, j, k);
		double BBOOST = (entity instanceof EntityPlayer) ? BORDER_BOOST / 4.0D : BORDER_BOOST;
		Block block = world.getBlock(i, j + (down ? -1 : 1), k);
		Block blockThis = world.getBlock(i, j, k);

		if (Math.abs(entity.motionY) > 0.4D) {
			entity.motionY *= 0.3D;
		}

		entity.fallDistance = 0;

		if (block != blockThis || halted) {
			if (entity instanceof EntityLiving) {
				PC_Direction side = null;

				if ((PCtr_BeltHelper.isConveyorAt(world, pos.offset(1, 0, 0)) && world.isAirBlock(i + 1, j + 1, k))) {
					side = PC_Direction.LEFT;
				} else if ((PCtr_BeltHelper.isConveyorAt(world, pos.offset(-1, 0, 0))
						&& world.isAirBlock(i - 1, j + 1, k))) {
					side = PC_Direction.RIGHT;
				} else if ((PCtr_BeltHelper.isConveyorAt(world, pos.offset(0, 0, 1))
						&& world.isAirBlock(i, j + 1, k + 1))) {
					side = PC_Direction.FRONT;
				} else if ((PCtr_BeltHelper.isConveyorAt(world, pos.offset(0, 0, -1))
						&& world.isAirBlock(i, j + 1, k - 1))) {
					side = PC_Direction.BACK;
				} else if ((world.isAirBlock(i + 1, j, k) && !world.isAirBlock(i + 1, j - 1, k))) {
					side = PC_Direction.LEFT;
				} else if ((world.isAirBlock(i - 1, j, k) && !world.isAirBlock(i - 1, j - 1, k))) {
					side = PC_Direction.RIGHT;
				} else if ((world.isAirBlock(i, j, k + 1) && !world.isAirBlock(i, j - 1, k + 1))) {
					side = PC_Direction.FRONT;
				} else if ((world.isAirBlock(i, j, k - 1) && !world.isAirBlock(i, j - 1, k - 1))) {
					side = PC_Direction.BACK;
				}

				if (side != null) {
					PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, true, side,
							PCtr_BeltHelper.MAX_HORIZONTAL_SPEED, PCtr_BeltHelper.HORIZONTAL_BOOST);
				}
			} else {
				if ((down && entity.posY < j + 0.6D) || (!down && entity.posY > j + 0.1D)) {
					if (PCtr_BeltHelper.isConveyorAt(world, pos.offset(1, 0, 0))) {
						PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, true, PC_Direction.LEFT,
								PCtr_BeltHelper.MAX_HORIZONTAL_SPEED,
								PCtr_BeltHelper.HORIZONTAL_BOOST * (down ? 1.2D : 1));
					} else if (PCtr_BeltHelper.isConveyorAt(world, pos.offset(-1, 0, 0))) {
						PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, true, PC_Direction.RIGHT,
								PCtr_BeltHelper.MAX_HORIZONTAL_SPEED,
								PCtr_BeltHelper.HORIZONTAL_BOOST * (down ? 1.2D : 1));
					} else if (PCtr_BeltHelper.isConveyorAt(world, pos.offset(0, 0, 1))) {
						PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, true, PC_Direction.FRONT,
								PCtr_BeltHelper.MAX_HORIZONTAL_SPEED,
								PCtr_BeltHelper.HORIZONTAL_BOOST * (down ? 1.2D : 1));
					} else if (PCtr_BeltHelper.isConveyorAt(world, pos.offset(0, 0, -1))) {
						PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, true, PC_Direction.BACK,
								PCtr_BeltHelper.MAX_HORIZONTAL_SPEED,
								PCtr_BeltHelper.HORIZONTAL_BOOST * (down ? 1.2D : 1));
					}
				}
			}
		} else {
			if (!down) {
				if (entity.motionY < ((block != blockThis || halted) ? 0.2D : 0.3D)) {
					entity.motionY = ((block != blockThis || halted) ? 0.2D : 0.3D);

					if (entity.onGround) {
						entity.moveEntity(0, 0.01D, 0);
					}
				}
			}

			if (entity.posX > pos.x + (1D - BORDERS)) {
				entity.motionX -= BBOOST;
			}

			if (entity.posX < pos.x + BORDERS) {
				entity.motionX += BBOOST;
			}

			if (entity.posZ > pos.z + BORDERS) {
				entity.motionZ -= BBOOST;
			}

			if (entity.posZ < pos.z + (1D - BORDERS)) {
				entity.motionZ += BBOOST;
			}

			if (!(block != blockThis || halted)) {
				entity.motionZ = PC_MathHelper.clamp_float((float) entity.motionZ, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));
				entity.motionX = PC_MathHelper.clamp_float((float) entity.motionX, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		boolean down = world.getBlockMetadata(i, j, k) == 1;
		boolean bottom = world.getBlock(i, j - 1, k) != world.getBlock(i, j, k);

		if (down && bottom) {
			return PCtr_App.conveyorBelt.getCollisionBoundingBoxFromPool(world, i, j, k);
		}

		return null;
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
	public int colorMultiplier(IBlockAccess world, int i, int j, int k) {
		return getRenderColor(world.getBlockMetadata(i, j, k));
	}

}
