package powercraft.transport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventoryWrapper;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.block.PCtr_BlockBeltBase;
import powercraft.transport.tile.PCtr_TileEntityEjectionBelt;

public class PCtr_BeltHelper {
	public static final float HEIGHT = 0.0625F;

	public static final float HEIGHT_SELECTED = HEIGHT;

	public static final float HEIGHT_COLLISION = HEIGHT;

	public static final double MAX_HORIZONTAL_SPEED = 0.5F;

	public static final double HORIZONTAL_BOOST = 0.14D;

	public static final double BORDERS = 0.35D;

	public static final double BORDER_BOOST = 0.063D;

	public static final float STORAGE_BORDER = 0.5F;

	public static final float STORAGE_BORDER_LONG = 0.8F;

	public static final float STORAGE_BORDER_V = 0.6F;

	public static int getRotation(int meta) {
		switch (meta) {
		case 0:
		case 6:
			return 0;

		case 1:
		case 7:
			return 1;

		case 8:
		case 14:
			return 2;

		case 9:
		case 15:
			return 3;
		}

		return meta;
	}

	public static boolean isEntityIgnored(Entity entity) {
		if (entity == null) {
			return true;
		}

		if (!entity.isEntityAlive()) {
			return true;
		}

		if (PC_Utils.isEntityFX(entity)) {
			return true;
		}

		if (entity.ridingEntity != null) {
			return true;
		}

		if (entity instanceof EntityPlayer) {
			if (((EntityPlayer) entity).isSneaking()) {
				return true;
			}

			if (((EntityPlayer) entity).inventory.armorItemInSlot(0) != null) {
				if (((EntityPlayer) entity).inventory.armorItemInSlot(0).getItem() == PCtr_App.slimeboots) {
					return true;
				}
			}
		}

		return false;
	}

	public static void packItems(World world, PC_VecI pos) {
		if (world.isRemote)
			return;

		List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1));

		if (items.size() < 5) {
			return;
		}

		for (EntityItem item1 : items) {
			if (item1 == null || item1.isDead || item1.getEntityItem() == null) {
				continue;
			}

			if (item1.getEntityItem().stackSize < 1) {
				item1.setDead();
				continue;
			}

			if (item1.getEntityItem().isItemStackDamageable()) {
				continue;
			}

			if (item1.getEntityItem().isItemEnchanted()) {
				continue;
			}

			if (!item1.getEntityItem().isStackable()) {
				continue;
			}

			ItemStack stackTarget = item1.getEntityItem();

			if (stackTarget.stackSize == stackTarget.getMaxStackSize()) {
				continue;
			}

			for (EntityItem item2 : items) {
				if (item2.isDead) {
					break;
				}

				ItemStack stackAdded = item2.getEntityItem();

				if (item2 == item1) {
					continue;
				}

				if (stackTarget.isItemEqual(stackAdded)) {
					if (stackTarget.stackSize < stackTarget.getMaxStackSize()) {
						int sizeRemain = stackTarget.getMaxStackSize() - stackTarget.stackSize;

						if (sizeRemain >= stackAdded.stackSize) {
							stackTarget.stackSize += stackAdded.stackSize;
							item2.setDead();
						} else {
							stackTarget.stackSize = stackTarget.getMaxStackSize();
							stackAdded.stackSize -= sizeRemain;
							break;
						}
					}
				}
			}
		}
	}

	public static void doSpecialItemAction(World world, PC_VecI beltPos, EntityItem entity) {
		if (entity == null || entity.getEntityItem() == null) {
			return;
		}

		boolean flag = false;
		flag |= entity.getEntityItem().getItem() == Items.water_bucket;
		flag |= entity.getEntityItem().getItem() == Items.lava_bucket;
		flag |= entity.getEntityItem().getItem() == Items.glass_bottle;

		if (!flag) {
			return;
		}

		do {
			if (doSpecialItemAction_do(world, beltPos.offset(0, 0, 1), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(0, 0, -1), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(1, 0, 0), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(-1, 0, 0), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(0, -1, 1), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(0, -1, -1), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(1, -1, 0), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(-1, -1, 0), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(0, 1, 0), entity)) {
				break;
			}

			if (doSpecialItemAction_do(world, beltPos.offset(0, -1, 0), entity)) {
				break;
			}
		} while (false);
	}

	private static boolean doSpecialItemAction_do(World world, PC_VecI pos, EntityItem entity) {
		if (entity.getEntityItem().getItem() == Items.water_bucket) {
			if (PC_Utils.getBID(world, pos) == Blocks.cauldron && PC_Utils.getMD(world, pos) < 3) {
				PC_Utils.setMD(world, pos, 3);
				entity.setEntityItemStack(new ItemStack(Items.bucket));
				return true;
			}
		}

		if (entity.getEntityItem().getItem() == Items.bucket) {
			if (PC_Utils.getBID(world, pos) == Blocks.water
					|| PC_Utils.getBID(world, pos) == Blocks.flowing_water && PC_Utils.getMD(world, pos) == 0) {
				PC_Utils.setBID(world, pos, Blocks.air, 0);
				entity.setEntityItemStack(new ItemStack(Items.water_bucket));
				return true;
			}
		}

		if (entity.getEntityItem().getItem() == Items.glass_bottle) {
			if (PC_Utils.getBID(world, pos) == Blocks.cauldron && PC_Utils.getBID(world, pos) != Blocks.air) {
				int meta = PC_Utils.getMD(world, pos);
				PC_Utils.setMD(world, pos, meta - 1);
				EntityItem entity2 = new EntityItem(world, entity.posX, entity.posY, entity.posZ,
						new ItemStack(Items.potionitem, 1, 0));
				entity2.motionX = entity.motionX;
				entity2.motionY = entity.motionY;
				entity2.motionZ = entity.motionZ;
				entity2.delayBeforeCanPickup = 7;
				world.spawnEntityInWorld(entity2);
				entity.getEntityItem().stackSize--;

				if (entity.getEntityItem().stackSize <= 0) {
					entity.getEntityItem().stackSize = 0;
					entity.setDead();
				}

				return true;
			}
		}

		return false;
	}

	public static boolean storeNearby(World world, PC_VecI pos, EntityItem entity, boolean ignoreStorageBorder) {
		if (storeItemIntoMinecart(world, pos, entity)) {
			return true;
		}

		if (!ignoreStorageBorder && entity.posY > pos.y + 1 - STORAGE_BORDER_V) {
			return false;
		}

		PC_Block block = PC_Utils.getBlock(world, pos);
		PC_Direction rot = block.getRotation(PC_Utils.getMD(world, pos));

		if (isBeyondStorageBorder(world, rot, pos, entity, STORAGE_BORDER) || ignoreStorageBorder) {

			if (storeEntityItemAt(world, pos.offset(rot.getOffset()), entity, rot)) {
				return true;
			}

			if (rot != PC_Direction.BACK && rot != PC_Direction.FRONT
					&& storeEntityItemAt(world, pos.offset(0, 0, -1), entity, PC_Direction.BACK)) {
				return true;
			}

			if (rot != PC_Direction.LEFT && rot != PC_Direction.RIGHT
					&& storeEntityItemAt(world, pos.offset(1, 0, 0), entity, PC_Direction.RIGHT)) {
				return true;
			}

			if (rot != PC_Direction.FRONT && rot != PC_Direction.BACK
					&& storeEntityItemAt(world, pos.offset(0, 0, 1), entity, PC_Direction.FRONT)) {
				return true;
			}

			if (rot != PC_Direction.RIGHT && rot != PC_Direction.LEFT
					&& storeEntityItemAt(world, pos.offset(-1, 0, 0), entity, PC_Direction.LEFT)) {
				return true;
			}

			if (storeEntityItemAt(world, pos.offset(0, 1, 0), entity, PC_Direction.TOP)) {
				return true;
			}
		}

		return false;
	}

	public static boolean storeItemIntoMinecart(World world, PC_VecI beltPos, EntityItem entity) {
		List<EntityMinecart> hitList = world.getEntitiesWithinAABB(EntityMinecart.class,
				AxisAlignedBB
						.getBoundingBox(beltPos.x, beltPos.y, beltPos.z, beltPos.x + 1, beltPos.y + 1, beltPos.z + 1)
						.expand(1.0D, 1.0D, 1.0D));

		if (hitList.size() > 0) {
			for (EntityMinecart cart : hitList) {
				if (cart instanceof EntityMinecartContainer) {

					IInventory inventory = (EntityMinecartContainer) cart;

					if (entity != null && entity.isEntityAlive()) {
						ItemStack stackToStore = entity.getEntityItem();

						if (stackToStore != null
								&& PC_InventoryUtils.storeItemStackToInventoryFrom(inventory, stackToStore)) {
							soundEffectChest(world, beltPos);

							if (stackToStore.stackSize <= 0) {
								entity.setDead();
								stackToStore.stackSize = 0;
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean storeEntityItemAt(World world, PC_VecI inventoryPos, EntityItem entity, PC_Direction side) {
		if (world.isRemote)
			return false;
		IInventory inventory = PC_InventoryUtils.getInventoryAt(world, inventoryPos.x, inventoryPos.y, inventoryPos.z);

		if (inventory != null && entity != null && entity.isEntityAlive()) {
			ItemStack stackToStore = entity.getEntityItem();

			if (stackToStore != null
					&& PC_InventoryUtils.storeItemStackToInventoryFrom(inventory, stackToStore, side)) {
				soundEffectChest(world, inventoryPos);

				if (stackToStore.stackSize <= 0) {
					entity.setDead();
					stackToStore.stackSize = 0;
					return true;
				}
			}
		}

		return false;
	}

	public static void soundEffectChest(World world, PC_VecI pos) {
		PC_SoundRegistry.playSound(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D, "random.pop",
				(world.rand.nextFloat() + 0.7F) / 5.0F, 0.5F + world.rand.nextFloat() * 0.3F);
	}

	public static boolean isBlocked(World world, PC_VecI blockPos) {
		boolean isWall = !world.isAirBlock(blockPos.x, blockPos.y, blockPos.z) && !isTransporterAt(world, blockPos);

		if (isWall) {
			Block block = PC_Utils.getBID(world, blockPos);

			if (block != null) {
				if (!block.getMaterial().blocksMovement()) {
					isWall = false;
				}
			}
		}

		return isWall;
	}

	public static boolean isConveyorAt(World world, PC_VecI pos) {
		Block block = PC_Utils.getBID(world, pos);

		if (block != Blocks.air) {
			if (block instanceof PCtr_BlockBeltBase) {
				return true;
			}
		}

		return false;
	}

	public static boolean isTransporterAt(World world, PC_VecI pos) {
		Block block = PC_Utils.getBID(world, pos);

		if (block != Blocks.air) {
			if (block instanceof PCtr_BlockBeltBase) {
				return true;
			}
		}

		return false;
	}

	public static int getDir(PC_Direction dir) {
		switch (dir.getMCDir()) {
		case 0:
			return 2;
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 0;
		case 4:
			return 5;
		case 5:
			return 4;
		}
		return -1;
	}

	public static boolean isBeyondStorageBorder(World world, PC_Direction rotation, PC_VecI beltPos, Entity entity,
			float border) {

		if (rotation == PC_Direction.BACK) {
			if (entity.posZ > beltPos.z + 1 - border) {
				return false;
			}
		} else if (rotation == PC_Direction.LEFT) {
			if (entity.posX < beltPos.x + border) {
				return false;
			}
		} else if (rotation == PC_Direction.FRONT) {
			if (entity.posZ < beltPos.z + border) {
				return false;
			}
		} else if (rotation == PC_Direction.RIGHT) {
			if (entity.posX > beltPos.x + 1 - border) {
				return false;
			}
		} else if (rotation == PC_Direction.TOP) {
			if (entity.posY > beltPos.y + 1 - border) {
				return false;
			}
		} else if (rotation == PC_Direction.BOTTOM) {
			if (entity.posY < beltPos.y + border) {
				return false;
			}
		}

		return true;
	}

	public static void entityPreventDespawning(World world, PC_VecI pos, boolean preventPickup, Entity entity) {
		if (entity instanceof EntityItem) {
			if (preventPickup) {
				((EntityItem) entity).delayBeforeCanPickup = 7;
			}

			if (((EntityItem) entity).age >= 5000) {
				if (world
						.getEntitiesWithinAABBExcludingEntity(null,
								AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1))
						.size() < 40) {
					((EntityItem) entity).age = 4000;
				}
			}
		}

		if (entity instanceof EntityXPOrb) {
			if (((EntityXPOrb) entity).xpOrbAge >= 5000) {
				if (world
						.getEntitiesWithinAABBExcludingEntity(null,
								AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1))
						.size() < 40) {
					((EntityXPOrb) entity).xpOrbAge = 4000;
				}
			}
		}
	}

	public static void moveEntityOnBelt(World world, PC_VecI pos, Entity entity, boolean bordersEnabled,
			boolean motionEnabled, PC_Direction direction, double max_horizontal_speed, double horizontal_boost) {
		int jumpModifier = (entity instanceof EntityItem || entity instanceof EntityXPOrb) ? 2 : 3;
		if (motionEnabled && world.rand.nextInt(35) == 0) {
			List list = world.getEntitiesWithinAABBExcludingEntity(entity,
					AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1));
		}

		int moveDirection = direction.getMCSide();

		if (moveDirection < 4 && (entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
			if (entity instanceof EntityItem) {
				if (entity.motionY > 0.2F) {
					entity.motionY /= 3F;
				}
			}
			if (entity.motionY > 0.2) {
				entity.motionY -= 0.1;
			}
		}

		if (moveDirection >= 4) {
			if (entity.onGround) {
				entity.moveEntity(0, 0.01D, 0);
			}
		}

		if (entity.stepHeight <= 0.15F) {
			entity.stepHeight = 0.25F;
		}

		float motionX, motionY, motionZ;
		motionZ = PC_MathHelper.clamp_float((float) entity.motionZ, (float) -max_horizontal_speed,
				(float) max_horizontal_speed);
		motionY = PC_MathHelper.clamp_float((float) entity.motionY, (float) -max_horizontal_speed,
				(float) max_horizontal_speed);
		motionX = PC_MathHelper.clamp_float((float) entity.motionX, (float) -max_horizontal_speed,
				(float) max_horizontal_speed);

		double BBOOST = (entity instanceof EntityPlayer) ? BORDER_BOOST / 4.0D : BORDER_BOOST;

		switch (moveDirection) {
		case 0:
			if (motionZ >= -max_horizontal_speed && motionEnabled) {
				entity.addVelocity(0, 0, -horizontal_boost);
			}

			if (bordersEnabled) {
				if (entity.posX > pos.x + (1D - BORDERS)) {
					entity.addVelocity(-BORDER_BOOST, 0, 0);
				}

				if (entity.posX < pos.x + BORDERS) {
					entity.addVelocity(BORDER_BOOST, 0, 0);
				}
			}

			break;

		case 2:
			if (motionX <= max_horizontal_speed && motionEnabled) {
				entity.addVelocity(horizontal_boost, 0, 0);
			}

			if (bordersEnabled) {
				if (entity.posZ > pos.z + BORDERS) {
					entity.addVelocity(0, 0, -BORDER_BOOST);
				}

				if (entity.posZ < pos.z + (1D - BORDERS)) {
					entity.addVelocity(0, 0, BORDER_BOOST);
				}
			}

			break;

		case 3:
			if (motionZ <= max_horizontal_speed && motionEnabled) {
				entity.addVelocity(0, 0, horizontal_boost);
			}

			if (bordersEnabled) {
				if (entity.posX > pos.x + (1D - BORDERS)) {
					entity.addVelocity(-BORDER_BOOST, 0, 0);
				}

				if (entity.posX < pos.x + BORDERS) {
					entity.addVelocity(BORDER_BOOST, 0, 0);
				}
			}

			break;

		case 1:
			if (motionX >= -max_horizontal_speed && motionEnabled) {
				entity.addVelocity(-horizontal_boost, 0, 0);
			}

			if (bordersEnabled) {
				if (entity.posZ > pos.z + BORDERS) {
					entity.addVelocity(0, 0, -BORDER_BOOST);
				}

				if (entity.posZ < pos.z + (1D - BORDERS)) {
					entity.addVelocity(0, 0, BORDER_BOOST);
				}
			}

			break;

		case 5:

			if (Math.abs(entity.motionY) > 0.4D) {
				entity.motionY *= 0.3D;
			}

			entity.fallDistance = 0;

			if (entity.motionY < (motionEnabled ? 0.2D : 0.3D)) {
				entity.motionY = (motionEnabled ? 0.2D : 0.3D);
			}

			if (bordersEnabled) {
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

				entity.motionZ = PC_MathHelper.clamp_float((float) entity.motionZ, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));
				entity.motionX = PC_MathHelper.clamp_float((float) entity.motionX, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));

			}

			break;

		case 4:

			if (Math.abs(entity.motionY) > 0.4D) {
				entity.motionY *= 0.3D;
			}

			entity.fallDistance = 0;

			if (bordersEnabled) {
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

				entity.motionZ = PC_MathHelper.clamp_float((float) entity.motionZ, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));
				entity.motionX = PC_MathHelper.clamp_float((float) entity.motionX, (float) -(BORDER_BOOST * 1.5D),
						(float) (BORDER_BOOST * 1.5D));

			}
		}

		if (entity.riddenByEntity != null) {
			entity.updateRiderPosition();
		}
	}

	public static ItemStack getItemStackForEntity(Entity entity) {
		ItemStack itemstack = null;

		if (entity instanceof EntityItem) {
			itemstack = ((EntityItem) entity).getEntityItem().copy();
		} else {

			if (entity instanceof EntityPig) {
				itemstack = new ItemStack(Items.porkchop, 1, 0);
			}

			if (entity instanceof EntitySheep) {
				itemstack = new ItemStack(Blocks.wool, 1, 0);
			}

			if (entity instanceof EntityCow) {
				itemstack = new ItemStack(Items.beef, 1, 0);
			}

			if (entity instanceof EntityCreeper) {
				itemstack = new ItemStack(Items.gunpowder, 1, 0);
			}

			if (entity instanceof EntityZombie) {
				itemstack = new ItemStack(Items.rotten_flesh, 1, 0);
			}

			if (entity instanceof EntitySkeleton) {
				itemstack = new ItemStack(Items.bone, 1, 0);
			}

			if (entity instanceof EntitySlime) {
				itemstack = new ItemStack(Items.slime_ball, 1, 0);
			}

			if (entity instanceof EntityEnderman) {
				itemstack = new ItemStack(Items.ender_pearl, 1, 0);
			}

			if (entity instanceof EntitySnowman) {
				itemstack = new ItemStack(Items.snowball, 1, 0);
			}

			if (entity instanceof EntityChicken) {
				itemstack = new ItemStack(Items.chicken, 1, 0);
			}

			if (entity instanceof EntityXPOrb) {
				itemstack = new ItemStack(Items.diamond, 1, 0);
			}

			if (entity instanceof EntitySpider) {
				itemstack = new ItemStack(Items.string, 1, 0);
			}

			if (entity instanceof EntityOcelot) {
				itemstack = new ItemStack(Items.fish, 1, 0);
			}

			if (entity instanceof EntityMooshroom) {
				itemstack = new ItemStack(Blocks.red_mushroom_block, 1, 0);
			}

			if (entity instanceof EntityWolf) {
				itemstack = new ItemStack(Items.cookie, 1, 0);
			}

			if (entity instanceof EntityBlaze) {
				itemstack = new ItemStack(Items.blaze_powder, 1, 0);
			}

			if (entity instanceof EntityMagmaCube) {
				itemstack = new ItemStack(Items.magma_cream, 1, 0);
			}

			if (entity instanceof EntityPigZombie) {
				itemstack = new ItemStack(Items.gold_nugget, 1, 0);
			}

			if (entity instanceof EntityIronGolem) {
				itemstack = new ItemStack(Items.iron_ingot, 1, 0);
			}
		}
		return itemstack;
	}

	public static boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		ItemStack stack = entityplayer.getCurrentEquippedItem();

		if (stack == null) {
			return false;
		}

		Item equip_item = stack.getItem();

		if (equip_item instanceof ItemMinecart) {
			System.out.println("1");
			if (!world.isRemote) {
				world.spawnEntityInWorld(EntityMinecart.createMinecart(world, i + 0.5F, j + 0.5F, k + 0.5F,
						((ItemMinecart) equip_item).minecartType));
			}

			if (!entityplayer.capabilities.isCreativeMode) {
				entityplayer.inventory.decrStackSize(entityplayer.inventory.currentItem, 1);
			}

			return true;
		}

		return false;
	}

	public static boolean isActive(int meta) {
		return (meta & 8) != 0;
	}

	public static boolean storeAllSides(World world, PC_VecI pos, EntityItem entity) {
		if (storeItemIntoMinecart(world, pos, entity)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(0, 0, -1), entity, PC_Direction.BACK)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(0, 0, 1), entity, PC_Direction.FRONT)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(-1, 0, 0), entity, PC_Direction.LEFT)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(1, 0, 0), entity, PC_Direction.RIGHT)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(0, 1, 0), entity, PC_Direction.TOP)) {
			return true;
		}

		if (storeEntityItemAt(world, pos.offset(0, -1, 0), entity, PC_Direction.BOTTOM)) {
			return true;
		}

		return false;
	}

	public static boolean dispenseFromInventoryAt(World world, PC_VecI inventoryPos, PC_VecI beltPos) {
		IInventory inventory = PC_InventoryUtils.getInventoryAt(world, inventoryPos.x, inventoryPos.y, inventoryPos.z);

		if (inventory == null) {
			return false;
		}

		return dispenseItemOntoBelt(world, inventoryPos, inventory, beltPos);
	}

	public static void tryToDispenseItem(World world, PC_VecI beltPos) {
		int rot = getRotation(PC_Utils.getMD(world, beltPos));

		if (rot == 2 && dispenseFromInventoryAt(world, beltPos.offset(0, 0, -1), beltPos)) {
			return;
		}

		if (rot == 3 && dispenseFromInventoryAt(world, beltPos.offset(1, 0, 0), beltPos)) {
			return;
		}

		if (rot == 0 && dispenseFromInventoryAt(world, beltPos.offset(0, 0, 1), beltPos)) {
			return;
		}

		if (rot == 1 && dispenseFromInventoryAt(world, beltPos.offset(-1, 0, 0), beltPos)) {
			return;
		}

		if (rot != 2 && dispenseFromInventoryAt(world, beltPos.offset(0, 0, -1), beltPos)) {
			return;
		}

		if (rot != 3 && dispenseFromInventoryAt(world, beltPos.offset(1, 0, 0), beltPos)) {
			return;
		}

		if (rot != 0 && dispenseFromInventoryAt(world, beltPos.offset(0, 0, 1), beltPos)) {
			return;
		}

		if (rot != 1 && dispenseFromInventoryAt(world, beltPos.offset(-1, 0, 0), beltPos)) {
			return;
		}
	}

	public static boolean dispenseItemOntoBelt(World world, PC_VecI invPos, IInventory inventory, PC_VecI beltPos) {
		ItemStack[] stacks = dispenseStuffFromInventory(world, beltPos, inventory);

		if (stacks != null) {
			stacks = PC_InventoryUtils.groupStacks(stacks);

			for (ItemStack stack : stacks) {
				createEntityItemOnBelt(world, invPos, beltPos, stack);
			}

			return true;
		}

		return false;
	}

	public static boolean dispenseStackFromNearbyMinecart(World world, PC_VecI beltPos) {
		List<Entity> hitList = world.getEntitiesWithinAABB(IInventory.class,
				AxisAlignedBB
						.getBoundingBox(beltPos.x, beltPos.y, beltPos.z, beltPos.x + 1, beltPos.y + 1, beltPos.z + 1)
						.expand(0.6D, 0.6D, 0.6D));

		if (hitList.size() > 0) {
			for (Entity entityWithInventory : hitList) {
				if (dispenseItemOntoBelt(
						world, new PC_VecI((int) (entityWithInventory.posX + 0.5),
								(int) (entityWithInventory.posY + 0.5), (int) (entityWithInventory.posZ + 0.5)),
						(IInventory) entityWithInventory, beltPos)) {
					return true;
				}
			}
		}

		List<Entity> hitList2 = world.getEntitiesWithinAABB(PC_IInventoryWrapper.class,
				AxisAlignedBB
						.getBoundingBox(beltPos.x, beltPos.y, beltPos.z, beltPos.x + 1, beltPos.y + 1, beltPos.z + 1)
						.expand(0.6D, 0.6D, 0.6D));

		if (hitList2.size() > 0) {
			for (Entity entityWithInventory : hitList2) {
				if (((PC_IInventoryWrapper) entityWithInventory).getInventory() != null) {
					if (dispenseItemOntoBelt(world,
							new PC_VecI((int) (entityWithInventory.posX + 0.5), (int) (entityWithInventory.posY + 0.5),
									(int) (entityWithInventory.posZ + 0.5)),
							((PC_IInventoryWrapper) entityWithInventory).getInventory(), beltPos)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	static boolean isSpecialContainer(IInventory inventory) {
		boolean flag = false;
		flag |= inventory instanceof TileEntityFurnace;
		flag |= inventory instanceof TileEntityBrewingStand;
		return flag;
	}

	static ItemStack[] dispenseFromSpecialContainer(IInventory inventory, PCtr_TileEntityEjectionBelt teb) {

		boolean modeStacks = teb.getActionType() == 0;
		boolean modeItems = teb.getActionType() == 1;
		boolean modeAll = teb.getActionType() == 2;
		boolean random = teb.getItemSelectMode() == 2;
		boolean first = teb.getItemSelectMode() == 0;
		boolean last = teb.getItemSelectMode() == 1;
		int numStacks = teb.getNumStacksEjected();
		int numItems = teb.getNumItemsEjected();

		if (inventory instanceof TileEntityFurnace) {
			ItemStack stack = inventory.getStackInSlot(2);

			if (stack != null && stack.stackSize > 0) {
				if (modeItems) {
					stack = inventory.decrStackSize(2, numItems);
				} else {
					inventory.setInventorySlotContents(2, null);
				}
				return new ItemStack[] { stack };
			}

			return null;
		} else if (inventory instanceof TileEntityBrewingStand) {
			if (((TileEntityBrewingStand) inventory).getBrewTime() != 0) {
				return null;
			}

			List<ItemStack> l = new ArrayList<ItemStack>();
			int[] rand = null;
			if (random) {
				rand = new int[4];
				for (int i = 0; i < 4; i++) {
					rand[i] = -1;
				}
				for (int i = 0; i < 4; i++) {
					while (true) {
						int index = teb.rand.nextInt(4);
						if (rand[index] == -1) {
							rand[index] = i;
							break;
						}
					}
				}
			}

			for (int i = last ? 3 : 0; (last ? i > 0 : i < 4)
					&& (modeStacks ? numStacks - 1 >= l.size() : true); i = (last ? i - 1 : i + 1)) {
				int index = i;

				if (random) {
					index = rand[index];
				}

				ItemStack stack = inventory.getStackInSlot(index);

				if ((index < 3 && (stack != null && stack.stackSize > 0 && stack.getItem() == Items.potionitem
						&& stack.getItemDamage() != 0)) || (index == 3 && (stack != null))) {
					inventory.setInventorySlotContents(index, null);
					l.add(stack);
					if (modeItems) {
						break;
					}
				}
			}

			return l.toArray(new ItemStack[0]);
		}

		return null;
	}

	public static ItemStack[] dispenseStuffFromInventory(World world, PC_VecI beltPos, IInventory inventory) {
		PCtr_TileEntityEjectionBelt teb = PC_Utils.getTE(world, beltPos);
		if (isSpecialContainer(inventory)) {
			return dispenseFromSpecialContainer(inventory, teb);
		}

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		boolean modeStacks = teb.getActionType() == 0;
		boolean modeItems = teb.getActionType() == 1;
		boolean modeAll = teb.getActionType() == 2;

		if (modeAll) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (inventory instanceof PC_IInventory) {
					if (!((PC_IInventory) inventory).canDispenseStackFrom(i)) {
						continue;
					}
				}

				ItemStack inSlot = inventory.getStackInSlot(i);

				if (inSlot != null) {
					stacks.add(inSlot);
					inventory.setInventorySlotContents(i, null);
				}
			}

			return PC_InventoryUtils.stacksToArray(stacks);
		}

		boolean random = teb.getItemSelectMode() == 2;
		boolean first = teb.getItemSelectMode() == 0;
		boolean last = teb.getItemSelectMode() == 1;
		int numStacks = teb.getNumStacksEjected();
		int numItems = teb.getNumItemsEjected();

		if (modeStacks && numStacks == 0)
			return new ItemStack[] {};

		if (modeItems && numItems == 0)
			return new ItemStack[] {};

		int i = 0;

		if (first) {
			i = 0;
		}

		if (last) {
			i = inventory.getSizeInventory() - 1;
		}

		if (random) {
			i = teb.rand.nextInt(inventory.getSizeInventory());
		}

		int randomTries = inventory.getSizeInventory() * 2;

		while (true) {
			boolean accessDenied = false;

			if (inventory instanceof PC_IInventory) {
				if (!((PC_IInventory) inventory).canDispenseStackFrom(i)) {
					accessDenied = true;
				}
			}

			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.stackSize > 0 && !accessDenied) {
				if (modeStacks) {
					if (numStacks > 0) {
						inventory.setInventorySlotContents(i, null);
						stacks.add(stack);
						numStacks--;

						if (numStacks <= 0) {
							break;
						}
					}
				} else if (modeItems) {
					if (numItems > 0) {
						stack = inventory.decrStackSize(i, numItems);
						numItems -= stack.stackSize;
						stacks.add(stack);

						if (numItems <= 0) {
							break;
						}
					}
				}
			}

			if (first) {
				i++;

				if (i >= inventory.getSizeInventory()) {
					break;
				}
			} else if (last) {
				i--;

				if (i < 0) {
					break;
				}
			} else if (random) {
				i = teb.rand.nextInt(inventory.getSizeInventory());
				randomTries--;

				if (randomTries == 0) {
					break;
				}
			}
		}

		return PC_InventoryUtils.stacksToArray(stacks);
	}

	public static void createEntityItemOnBelt(World world, PC_VecI invPos, PC_VecI beltPos, ItemStack stack) {
		EntityItem item = new EntityItem(world, beltPos.x + 0.5D, beltPos.y + 0.3D, beltPos.z + 0.5D, stack);
		item.motionX = 0.0D;
		item.motionY = 0.0D;
		item.motionZ = 0.0D;
		PC_VecI vector = beltPos.copy().sub(invPos);
		item.posX += 0.43D * vector.x;
		item.posZ += 0.43D * vector.z;
		item.delayBeforeCanPickup = 7;
		world.spawnEntityInWorld(item);
	}
}
