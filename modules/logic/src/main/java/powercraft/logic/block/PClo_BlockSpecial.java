package powercraft.logic.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.annotation.PC_Shining.OFF;
import powercraft.api.annotation.PC_Shining.ON;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Property;
import powercraft.logic.item.PClo_ItemBlockSpecial;
import powercraft.logic.tile.PClo_TileEntityPulsar;
import powercraft.logic.tile.PClo_TileEntitySpecial;
import powercraft.logic.type.PClo_SpecialType;

@PC_Shining
@PC_BlockInfo(name = "Special", itemBlock = PClo_ItemBlockSpecial.class, tileEntity = PClo_TileEntitySpecial.class, canPlacedRotated = true)
public class PClo_BlockSpecial extends PC_Block {
	@ON
	public static PClo_BlockSpecial on;
	@OFF
	public static PClo_BlockSpecial off;

	public PClo_BlockSpecial(boolean on) {
		super(Material.ground, PClo_SpecialType.getTextures());
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
		onNeighborBlockChange(world, x, y, z, this);
	}

	@Override
	public void initConfig(PC_Property config) {
		super.initConfig(config);
		on.setLightLevel(config.getInt("brightness", 7) * 0.0625F);
	}

	private void spawnMobsFromSpawners(World world, int x, int y, int z) {
		spawnMobFromSpawner(world, x + 1, y, z);
		spawnMobFromSpawner(world, x - 1, y, z);
		spawnMobFromSpawner(world, x, y + 1, z);
		spawnMobFromSpawner(world, x, y, z + 1);
		spawnMobFromSpawner(world, x, y, z - 1);
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote)
			return;
		PClo_TileEntitySpecial te = getTE(world, x, y, z);
		boolean shouldState = false;
		boolean state = isActive(world, x, y, z);
		PC_Direction rot = getRotation(PC_Utils.getMD(world, x, y, z));
		int xAdd = rot.getOffset().x, zAdd = rot.getOffset().z;

		switch (te.getType()) {
		case PClo_SpecialType.DAY:
			shouldState = world.isDaytime();
			break;

		case PClo_SpecialType.NIGHT:
			shouldState = !world.isDaytime();
			break;

		case PClo_SpecialType.RAIN:
			shouldState = world.isRaining();
			break;

		case PClo_SpecialType.CHEST_EMPTY: {
			IInventory inv = PC_InventoryUtils.getInventoryAt(world, x - xAdd, y, z - zAdd);
			if (inv != null && inv != te) {
				shouldState = PC_InventoryUtils.getInventoryCountOf(inv, te.getStackInSlot(0)) == 0;
			}
			break;

		}
		case PClo_SpecialType.CHEST_FULL: {
			IInventory inv = PC_InventoryUtils.getInventoryAt(world, x - xAdd, y, z - zAdd);
			if (inv != null && inv != te) {
				shouldState = PC_InventoryUtils.getInventorySpaceFor(inv, te.getStackInSlot(0)) == 0;
			}
			break;

		}
		case PClo_SpecialType.SPECIAL: {
			shouldState = getRedstonePowerValueFromInput(world, x, y, z, PC_Direction.BACK) > 0;
			TileEntity tes = PC_Utils.getTE(world, x - xAdd, y, z - zAdd);

			if (tes instanceof PClo_TileEntityPulsar)
				((PClo_TileEntityPulsar) tes).setPaused(shouldState);

			if (shouldState == true && shouldState != state)
				spawnMobsFromSpawners(world, x, y, z);
			break;
		}
		}

		if (state != shouldState)
			PC_Utils.setBlockState(world, x, y, z, shouldState);
	}

	private boolean isOutputActive(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return getProvidingStrongRedstonePowerValue(world, x, y, z, dir);
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		if (!isActive(world, x, y, z))
			return 0;

		switch (getType(world, x, y, z)) {
		case PClo_SpecialType.DAY:
		case PClo_SpecialType.NIGHT:
		case PClo_SpecialType.RAIN:
			return 15;

		case PClo_SpecialType.CHEST_EMPTY:
		case PClo_SpecialType.CHEST_FULL:
			if (dir == PC_Direction.FRONT) {
				return 15;
			}

			break;
		}

		return 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		if (getType(world, x, y, z) == PClo_SpecialType.CHEST_EMPTY
				|| getType(world, x, y, z) == PClo_SpecialType.CHEST_FULL) {
			if ((side == PC_Direction.RIGHT.getMCSide()
					&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.LEFT)
					|| (side == PC_Direction.LEFT.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.RIGHT)
					|| (side == PC_Direction.FRONT.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.FRONT)
					|| (side == PC_Direction.BACK.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.BACK))
				return true;
			else
				return false;
		}
		if (getType(world, x, y, z) == PClo_SpecialType.SPECIAL) {
			if ((side == PC_Direction.RIGHT.getMCSide()
					&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.RIGHT)
					|| (side == PC_Direction.LEFT.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.LEFT)
					|| (side == PC_Direction.FRONT.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.BACK)
					|| (side == PC_Direction.BACK.getMCSide()
							&& this.getRotation(PC_Utils.getMD(world, x, y, z)) == PC_Direction.FRONT))
				return true;
			else
				return false;
		}
		if (side == 0)
			return true;
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
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		PC_Renderer.renderBlockRotatedBox(world, x, y, z, this, 0, renderer);
		/*
		 * PClo_TileEntitySpecial te = this.getTE(world, x, y, z);//TODO: Maybe in
		 * future te.clearAABBList(); //z+
		 * if(PClo_TileEntityWire.checkLogical(te.getWorld(), x, y, z+1, 0)) {
		 * PC_Utils.setBlockBoundsAndCollision(PClo_App.wire, te, 0.4F, 0F, 0.75F, 0.6F,
		 * 0.25F, 1F, x, y, z); PC_Renderer.renderBlockRotatedBox(world, x, y, z,
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

	public static PClo_TileEntitySpecial getTE(IBlockAccess world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);

		if (te instanceof PClo_TileEntitySpecial) {
			return (PClo_TileEntitySpecial) te;
		}

		return null;
	}

	public static int getType(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntitySpecial te = getTE(world, x, y, z);

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
			return sideIcons[getType(iblockaccess, x, y, z) + 4
					+ (isActive(iblockaccess, x, y, z) ? 0 : PClo_SpecialType.TOTAL_SPECIAL_COUNT)];
		if (side == 0)
			return sideIcons[0];
		if (getType(iblockaccess, x, y, z) == PClo_SpecialType.CHEST_EMPTY
				|| getType(iblockaccess, x, y, z) == PClo_SpecialType.CHEST_FULL
				|| getType(iblockaccess, x, y, z) == PClo_SpecialType.SPECIAL) {
			if ((side == PC_Direction.RIGHT.getMCDir()
					&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.FRONT)
					|| (side == PC_Direction.LEFT.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.FRONT)
					|| (side == PC_Direction.FRONT.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.LEFT)
					|| (side == PC_Direction.BACK.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.LEFT)) {
				if (getType(iblockaccess, x, y, z) == PClo_SpecialType.SPECIAL)
					return sideIcons[3];
				else
					return sideIcons[2];
			}
			if ((side == PC_Direction.RIGHT.getMCDir()
					&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.BACK)
					|| (side == PC_Direction.LEFT.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.BACK)
					|| (side == PC_Direction.FRONT.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.RIGHT)
					|| (side == PC_Direction.BACK.getMCDir()
							&& this.getRotation(PC_Utils.getMD(iblockaccess, x, y, z)) == PC_Direction.RIGHT)) {
				if (getType(iblockaccess, x, y, z) == PClo_SpecialType.SPECIAL)
					return sideIcons[2];
				else
					return sideIcons[3];
			}
		}
		return sideIcons[1];
	}

	@Override
	public IIcon getIcon(PC_Direction side, int meta) {
		if (side == PC_Direction.BOTTOM)
			return sideIcons[0];
		if (side == PC_Direction.TOP)
			return sideIcons[meta + 4];
		else {
			if (meta == PClo_SpecialType.CHEST_EMPTY || meta == PClo_SpecialType.CHEST_FULL
					|| meta == PClo_SpecialType.SPECIAL) {
				if ((side == PC_Direction.RIGHT && this.getRotation(meta) == PC_Direction.FRONT)
						|| (side == PC_Direction.LEFT && this.getRotation(meta) == PC_Direction.FRONT)
						|| (side == PC_Direction.FRONT && this.getRotation(meta) == PC_Direction.LEFT)
						|| (side == PC_Direction.BACK && this.getRotation(meta) == PC_Direction.LEFT)) {
					if (meta == PClo_SpecialType.SPECIAL)
						return sideIcons[3];
					else
						return sideIcons[3];
				}
				if ((side == PC_Direction.RIGHT && this.getRotation(meta) == PC_Direction.BACK)
						|| (side == PC_Direction.LEFT && this.getRotation(meta) == PC_Direction.BACK)
						|| (side == PC_Direction.FRONT && this.getRotation(meta) == PC_Direction.RIGHT)
						|| (side == PC_Direction.BACK && this.getRotation(meta) == PC_Direction.RIGHT)) {
					if (meta == PClo_SpecialType.SPECIAL)
						return sideIcons[2];
					else
						return sideIcons[3];
				}
			}
			return sideIcons[1];
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PC_Renderer.glScalef(1.6F, 1.6F, 1.6F);
		if (metadata == PClo_SpecialType.CHEST_EMPTY || metadata == PClo_SpecialType.CHEST_FULL) {
			PC_Renderer.glTranslatef(0, 0, -0.25F);
			setBlockBounds(0.25F, 0F, 0.5F, 0.75F, 0.5F, 1.0F);
		} else if (metadata == PClo_SpecialType.SPECIAL) {
			PC_Renderer.glTranslatef(0, 0, 0.25F);
			setBlockBounds(0.25F, 0F, 0.0F, 0.75F, 0.5F, 0.5F);
		} else {
			setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);
		}
		PC_Renderer.glTranslatef(0, 0.15F, 0);
		PC_Renderer.renderInvBlockRotatedBox(this, metadata, 0, renderer);
		return true;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		PClo_TileEntitySpecial te = getTE(world, x, y, z);
		PC_Direction rot = getRotation(PC_Utils.getMD(world, x, y, z));

		float xS = 0.25F;
		float xE = 0.75F;
		float zS = 0.25F;
		float zE = 0.75F;
		if (te != null) {
			te.clearAABBList();
			if (rot == PC_Direction.RIGHT) {
				xS = 0.5F;
				xE = 1.0F;
				if (te.getType() == PClo_SpecialType.SPECIAL) {
					xS = 0.0F;
					xE = 0.5F;
				}
			}
			if (rot == PC_Direction.LEFT) {
				xS = 0.0F;
				xE = 0.5F;
				if (te.getType() == PClo_SpecialType.SPECIAL) {
					xS = 0.5F;
					xE = 1.0F;
				}
			}
			if (rot == PC_Direction.BACK) {
				zS = 0.5F;
				zE = 1.0F;
				if (te.getType() == PClo_SpecialType.SPECIAL) {
					zS = 0.0F;
					zE = 0.5F;
				}
			}
			if (rot == PC_Direction.FRONT) {
				zS = 0.0F;
				zE = 0.5F;
				if (te.getType() == PClo_SpecialType.SPECIAL) {
					zS = 0.5F;
					zE = 1.0F;
				}
			}

			if (te.getType() == PClo_SpecialType.CHEST_EMPTY || te.getType() == PClo_SpecialType.CHEST_FULL
					|| te.getType() == PClo_SpecialType.SPECIAL)
				PC_Utils.setBlockBoundsAndCollision(this, te, xS, 0F, zS, xE, 0.5F, zE, x, y, z);
			else
				PC_Utils.setBlockBoundsAndCollision(this, te, 0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F, x, y, z);
		} else {
			setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.5F, 0.75F);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		int type = getType(world, i, j, k);
		if (type != PClo_SpecialType.CHEST_EMPTY && type != PClo_SpecialType.CHEST_FULL) {
			return false;
		}

		PC_GresRegistry.openGres("Special", entityplayer, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		return true;
	}

	public static void spawnMobFromSpawner(World world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner tems = (TileEntityMobSpawner) te;

			if (te != null) {
				PC_Utils.spawnMobs(world, x, y, z, tems.func_145881_a().getEntityNameToSpawn());
			}
		}
	}

	public static void preventSpawnerSpawning(World world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner tems = (TileEntityMobSpawner) te;
			tems.func_145881_a().spawnDelay = 20;
		}
	}
}
