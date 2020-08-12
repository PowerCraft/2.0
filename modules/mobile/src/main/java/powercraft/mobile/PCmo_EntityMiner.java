package powercraft.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import powercraft.api.PC_Lang;
import powercraft.api.building.PC_BuildingManager;
import powercraft.api.entity.PC_FakePlayer;
import powercraft.api.interfaces.PC_INBT;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventoryWrapper;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.utils.PC_Serializer;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Logger;
import powercraft.mobile.PCmo_Command.ParseException;

public class PCmo_EntityMiner extends Entity implements PC_IInventoryWrapper {

	public static final int LTORCH = 3;
	public static final int LBRIDGE = 2;
	public static final int LLAVA = 4;
	public static final int LWATER = 6;
	public static final int LAIR = 7;
	public static final int LCOBBLE = 6;
	public static final int LCOMPRESS = 5;

	public static final String keepAllFuel = "keepAllFuel";
	public static final String torchesOnlyOnFloor = "torchesOnlyOnFloor";
	public static final String compressBlocks = "compressBlocks";
	public static final String miningEnabled = "miningEnabled";
	public static final String bridgeEnabled = "bridgeEnabled";
	public static final String lavaFillingEnabled = "lavaFillingEnabled";
	public static final String waterFillingEnabled = "waterFillingEnabled";
	public static final String torches = "torches";
	public static final String cobbleMake = "cobbleMake";
	public static final String airFillingEnabled = "airFillingEnabled";
	private boolean inGui = false;

	/** Fuel strength multiplier. It's also affected by level. */
	public static final double FUEL_STRENGTH = 0.9D;

	private static final Random rand = new Random();

	/** Fake player instance used for block mining */
	public EntityPlayer fakePlayer;

	/** Miner status */
	protected MinerStatus st = new MinerStatus();

	/** Cargo inventory with all items */
	protected MinerCargoInventory cargo = new MinerCargoInventory();

	public HashMap<String, Object> info = new HashMap<String, Object>();

	private String playerConectedID;
	private int playerTimeout = 0;

	/** cool-down timer for repeated key presses */
	private int[] keyPressTimer = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	/** number of ticks before the key is accepted again */
	private static final int CooldownTime = 8;

	private int tick = 0;
	private boolean moveEnabled = true;

	/**
	 * Create miner in world.
	 * 
	 * @param world the world
	 */
	public PCmo_EntityMiner(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(1.3F, 1.4F);
		yOffset = 0F;
		fakePlayer = new PC_FakePlayer(world);
		entityCollisionReduction = 1.0F;
		stepHeight = 0.6F;
		isImmuneToFire = true;
		setFlag(keepAllFuel, false);
		setFlag(torchesOnlyOnFloor, false);
		setFlag(compressBlocks, false);
		setFlag(miningEnabled, false);
		setFlag(bridgeEnabled, false);
		setFlag(lavaFillingEnabled, false);
		setFlag(waterFillingEnabled, false);
		setFlag(torches, false);
		setFlag(cobbleMake, false);
		setFlag(airFillingEnabled, false);
	}

	/**
	 * Create miner in world, at given position
	 * 
	 * @param world the world
	 * @param dx    pos X
	 * @param dy    pos Y
	 * @param dz    pos Z
	 */
	public PCmo_EntityMiner(World world, double dx, double dy, double dz) {
		this(world);
		setPosition(dx, dy + yOffset, dz);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = dx;
		prevPosY = dy;
		prevPosZ = dz;

		st.target.x = (int) dx;
		st.target.y = (int) dy;
		st.target.z = (int) dz;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	// this is used for hit timer and breaking animations.
	@Override
	protected void entityInit() {
		dataWatcher.addObject(17, new Integer(0));
		dataWatcher.addObject(18, new Integer(1));
		dataWatcher.addObject(19, new Float(0));
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
			return null;
		}
		return entity.boundingBox;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	// useless as miner can't be mounted
	@Override
	public double getMountedYOffset() {
		return 1D;
	}

	// returns true if in water.
	@Override
	public boolean handleWaterMovement() {
		return worldObj.isMaterialInBB(
				boundingBox.expand(-0.10000000149011612D, -0.40000000596046448D, -0.10000000149011612D),
				Material.water);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		// all but void and explosion is ignored.
		if (damagesource != DamageSource.outOfWorld && (worldObj.isRemote || isDead
				|| (damagesource.getSourceOfDamage() == null && damagesource.isExplosion()
						|| st.isExplosionResistent))) {
			return true;
		}
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() + i * 7);
		setBeenAttacked();
		if (getDamageTaken() > 40) {
			if (riddenByEntity != null) {
				riddenByEntity.mountEntity(this); // unmount
			}

			turnIntoBlocks();
		}
		return true;
	}

	@Override
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	// god knows what is this for
	@Override
	public void setPositionAndRotation2(double d, double d1, double d2, float f, float f1, int i) {
		motionX = 1.0;
	}

	@Override
	public void setVelocity(double d, double d1, double d2) {
		motionX = d;
		motionY = d1;
		motionZ = d2;
	}

	@Override
	public float getShadowSize() {
		return 1.0F;
	}

	public boolean canMakeCobble() {
		return st.level >= LCOBBLE && getFlag(cobbleMake) && cargo.hasItem(Items.lava_bucket)
				&& cargo.hasItem(Items.water_bucket);
	}

	public boolean getFlag(String key) {
		return (Boolean) getInfo(key);
	}

	/**
	 * Class containing internal miner data, often temporary and not important for
	 * the outer world.
	 * 
	 * @author MightyPork
	 */
	public class MinerStatus implements PC_INBT {

		/** Is program execution paused? */
		public boolean pausedWeasel = false;

		/** Is operation paused? */
		private boolean paused = false;

		/**
		 * Flag set if program is stopped and miner should not move until it receives
		 * keyboard command or gets new program.
		 */
		public boolean halted = false;

		/** List of miner commands waiting for execution */
		private String commandList = "";

		/** Command currently being processed */
		private int currentCommand = -1;

		/**
		 * The real command executed.<br>
		 * For example when executing "U" command and is already on half step,<br>
		 * "F" command is executed instead.
		 */
		private int realCommand = -1;

		/**
		 * Steps remaining to complete current command<br>
		 * Used for commands like "100", instead of inserting 100 times "F"
		 */
		private int stepCounter = 0;

		/** Command list saved when the program is paused. */
		private String commandListSaved = "";

		/** Flag: Is the programming GUI open? -> Ignore keyboard control */
		// no nbt
		public boolean programmingGuiOpen = false;

		/** Flag: the half step was already laid for this move (up) */
		private boolean upStepLaid = false;

		/** Flag: bridge building was already finished for thsi move */
		private boolean bridgeDone = false;

		/** Target position to check if command is finished */
		private PC_VecI target = new PC_VecI();

		/** Rotation in degrees remaining to complete current rotation command */
		private int rotationRemaining = 0;

		/**
		 * The miner's level.<br>
		 * Calculated from count of PowerCrystals.
		 */
		public int level = 1;

		/** Fuel consumed from items and waiting for usage */
		public int fuelBuffer = 0;
		/**
		 * Fuel allocated for current operation.<br>
		 * This fuel is already in the buffer, but won't be consumed until the operation
		 * is really finished.<br>
		 * that prevents fuel consumption when miner hits something it can't mine.
		 */
		private int fuelAllocated = 0;
		/**
		 * Fuel needed to execute current command - wonẗ move until some fuel is added
		 */
		private int fuelDeficit = 0;

		/**
		 * Mining progress counter.<br>
		 * <ul>
		 * <li>0,1,2,3 - blocks in front of miner
		 * <li>4,5 - blocks in front of and below miner - for "Down" command
		 * <li>6,7,8,9,10,11 - blocks mined during "Up" command
		 * </ul>
		 * <br>
		 * Values:
		 * <ul>
		 * <li>-1 - mining scheduled but not started yet
		 * <li>0 - mining finished
		 * <li>>0 - ticks remaining
		 * </ul>
		 */
		private int[] mineCounter = { -1, -1, -1, -1, /* under */0, 0, /* above */0, 0, /* top */0, 0, 0, 0 };

		/**
		 * Mining sound counter.<br>
		 * Sound is played when reaches zero, to prevent insane noise.
		 */
		private int miningTickCounter = 0;

		public boolean isExplosionResistent;

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setBoolean("Paused", paused);
			tag.setBoolean("PausedWeasel", pausedWeasel);
			tag.setBoolean("Halted", halted);
			tag.setString("CommandList", commandList);
			tag.setString("CommandListSaved", commandListSaved);
			tag.setInteger("Cmd", currentCommand);
			tag.setInteger("CmdReal", realCommand);
			tag.setInteger("Steps", stepCounter);
			tag.setBoolean("UpStepLaid", upStepLaid);
			tag.setBoolean("BridgeDone", bridgeDone);
			PC_Utils.saveToNBT(tag, "Target", target);
			tag.setInteger("RotationRemaining", rotationRemaining);
			tag.setInteger("Level", level);
			tag.setInteger("FuelBuffer", fuelBuffer);
			tag.setInteger("FuelAllocated", fuelAllocated);
			tag.setInteger("FuelDeficit", fuelDeficit);

			tag.setBoolean("isExplosionResistent", isExplosionResistent);

			for (int i = 0; i < mineCounter.length; i++) {
				tag.setInteger("mt" + i, mineCounter[i]);
			}

			return tag;
		}

		@Override
		public PC_INBT readFromNBT(NBTTagCompound tag) {
			paused = tag.getBoolean("Paused");
			pausedWeasel = tag.getBoolean("PausedWeasel");
			halted = tag.getBoolean("Halted");
			commandList = tag.getString("CommandList");
			commandListSaved = tag.getString("CommandListSaved");
			currentCommand = tag.getInteger("Cmd");
			realCommand = tag.getInteger("CmdReal");
			stepCounter = tag.getInteger("Steps");
			upStepLaid = tag.getBoolean("UpStepLaid");
			bridgeDone = tag.getBoolean("BridgeDone");
			PC_Utils.loadFromNBT(tag, "Target", target);
			rotationRemaining = tag.getInteger("RotationRemaining");
			level = tag.getInteger("Level");
			fuelBuffer = tag.getInteger("FuelBuffer");
			fuelAllocated = tag.getInteger("FuelAllocated");
			fuelDeficit = tag.getInteger("FuelDeficit");

			isExplosionResistent = tag.getBoolean("isExplosionResistent");

			for (int i = 0; i < mineCounter.length; i++) {
				mineCounter[i] = tag.getInteger("mt" + i);
			}
			return this;
		}

		/**
		 * Consume part of the allocated fuel
		 * 
		 * @param count fuel points to consume
		 */
		private void consumeAllocatedFuel(int count) {
			fuelAllocated -= count;
			fuelBuffer -= count;
			if (fuelBuffer < 0) {
				fuelBuffer = 0;
			}
			if (fuelAllocated < 0) {
				fuelAllocated = 0;
			}
		}

		/**
		 * If there is no fuel-consuming process, release allocated fuel in fuel buffer
		 * for other uses.
		 */
		private void releaseAllocatedFuelIfNoLongerNeeded() {
			if (!isMiningInProgress() && currentCommand == -1) {
				fuelAllocated = 0;
			}
		}

		/**
		 * Add fuel to fuel buffer for given co If there isn't enough fuel in the cargo
		 * inventory, add it to deficit counter.
		 * 
		 * @param cost fuel points needed
		 * @return true on success
		 */
		private boolean addFuelForCost(int cost) {

			if (fuelBuffer - fuelAllocated >= cost) {
				fuelAllocated += cost;
				return true;
			} else {
				for (int s = 0; s < cargo.getSizeInventory(); s++) {
					ItemStack stack = cargo.getStackInSlot(s);
					int bt = (int) (PC_RecipeRegistry.getFuelValue(stack) * FUEL_STRENGTH);
					if (bt > 0 && !(stack.getItem() == Items.lava_bucket && getFlag(cobbleMake) && level >= LCOBBLE)) {
						fuelBuffer += bt;
						if (stack.getItem().hasContainerItem()) {
							cargo.setInventorySlotContents(s, new ItemStack(stack.getItem().getContainerItem(), 1, 0));
						} else {
							cargo.decrStackSize(s, 1);
						}

						if ((fuelBuffer - fuelAllocated) >= cost) {
							fuelAllocated += cost;
							return true;
						}
					}
				}

				if ((fuelBuffer - fuelAllocated) >= cost) {
					fuelAllocated += cost;
					return true;
				}
			}

			if (fuelDeficit <= 0)
				fuelDeficit += cost - (fuelBuffer + fuelAllocated);
			return false;
		}

	}

	public static interface Agree {
		public boolean agree(ItemStack stack);
	}

	/**
	 * Cargo inventory and some inventory manipulation methods.
	 * 
	 * @author MightyPork
	 */
	public class MinerCargoInventory implements PC_IInventory {
		/**
		 * inventory
		 */
		private ItemStack[] inventoryContents;

		public MinerCargoInventory() {
			// super(PC_LangRegistry.tr("pc.miner.chestName"), false, 11 * 5);
			this.inventoryContents = new ItemStack[11 * 5];
		}

		public ItemStack getStackInSlot(int slot) {
			if (slot > this.getSizeInventory() || slot < 0)
				return null;
			return this.inventoryContents[slot];
		}

		/**
		 * Called when an the contents of an Inventory change, usually
		 */
		public void markDirty() {
		}

		@Override
		public void closeInventory() {
			updateLevel();
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack is) {
			this.inventoryContents[slot] = is;

			if (is != null && is.stackSize > this.getInventoryStackLimit()) {
				is.stackSize = this.getInventoryStackLimit();
			}
			this.markDirty();
		}

		/**
		 * Compress blocks in inventory to storage blocks.<br>
		 * <b>Time expensive, do only if needed!</b>
		 * 
		 * @param stack stack inserted
		 */
		private void compressInv(ItemStack stack) {
			if (st.level < LCOMPRESS) {
				return;
			}
			if (!getFlag(compressBlocks)) {
				return;
			}

			ItemStack out = null;
			int neededForOne = 0;

			do {
				if (stack.getItem() == Item.getItemFromBlock(Blocks.sand)) {
					out = new ItemStack(Blocks.sandstone);
					neededForOne = 4;
					break;
				}
				if (stack.getItem() == Items.snowball) {
					out = new ItemStack(Blocks.snow);
					neededForOne = 4;
					break;
				}
				if (stack.getItem() == Items.diamond) {
					out = new ItemStack(Blocks.diamond_block);
					neededForOne = 9;
					break;
				}
				if (stack.getItem() == Items.iron_ingot) {
					out = new ItemStack(Blocks.iron_block);
					neededForOne = 9;
					break;
				}
				if (stack.getItem() == Items.gold_ingot) {
					out = new ItemStack(Blocks.gold_block);
					neededForOne = 9;
					break;
				}
				if (stack.getItem() == Items.gold_nugget) {
					out = new ItemStack(Items.gold_ingot);
					neededForOne = 9;
					break;
				}
				if (stack.getItem() == Items.glowstone_dust) {
					out = new ItemStack(Blocks.glowstone);
					neededForOne = 4;
					break;
				}
				if (stack.getItem() == Items.dye && stack.getItemDamage() == 4) {
					out = new ItemStack(Blocks.lapis_block);
					neededForOne = 9;
					break;
				}
				if (stack.getItem() == Items.redstone) {
					Block redstonStorage = PC_BlockRegistry.getPCBlockByName("PCde_BlockRedstoneStorage");
					if (redstonStorage != Blocks.air) {
						out = new ItemStack(redstonStorage, 1, 0);
						neededForOne = 9;
						break;
					}
				}
				if (stack.getItem() == Items.clay_ball) {
					out = new ItemStack(Blocks.clay);
					neededForOne = 4;
					break;
				}
				if (stack.getItem() == Items.brick) {
					out = new ItemStack(Blocks.brick_block);
					neededForOne = 4;
					break;
				}

			} while (false);

			if (out == null || neededForOne == 0)
				return;

			int count = 0;

			for (int i = 0; i < getSizeInventory(); i++) {
				if (getStackInSlot(i) != null) {
					if (getStackInSlot(i).isItemEqual(stack)) {
						count += getStackInSlot(i).stackSize;
						setInventorySlotContents(i, null);
						continue;
					}
				}
			}

			while (count >= neededForOne) {
				if (PC_InventoryUtils.storeItemStackToInventoryFrom(this, out.copy())) {
					count -= neededForOne;
				} else {
					break;
				}
			}

			if (count > 0) {
				ItemStack remaining = stack.copy();
				remaining.stackSize = count;
				if (!PC_InventoryUtils.storeItemStackToInventoryFrom(this, remaining)) {
					entityDropItem(remaining, 1);
				}
			}

			return;
		}

		/**
		 * Get block from inventory good for building.
		 * 
		 * @return stack or null.
		 */
		public ItemStack getBlockForBuilding() {

			for (int pass = 0; pass <= 1; pass++) {
				for (int i = 0; i < getSizeInventory(); i++) {
					if (isBlockGoodForBuilding(getStackInSlot(i), pass)) {
						return decrStackSize(i, 1);
					}
				}
			}

			if (canMakeCobble()) {
				return new ItemStack(Blocks.cobblestone);
			}

			for (int pass = 2; pass <= 5; pass++) {
				for (int i = 0; i < getSizeInventory(); i++) {
					if (isBlockGoodForBuilding(getStackInSlot(i), pass)) {
						return decrStackSize(i, 1);
					}
				}
			}

			return null;
		}

		/**
		 * Check if block is good for building.
		 * 
		 * @param is   stack
		 * @param pass pass; 0 = cheap, 1 = better
		 * @return is good
		 */
		private boolean isBlockGoodForBuilding(ItemStack is, int pass) {
			if (is == null) {
				return false;
			}

			if (!(is.getItem() instanceof ItemBlock)) {
				return false;
			}

			Item id = is.getItem();

			if (id == Item.getItemFromBlock(Blocks.stone_slab) || id == Item.getItemFromBlock(Blocks.soul_sand))
				return false;

			if (PC_MSGRegistry.hasFlag(is, "NO_BUILD")) {
				return false;
			}

			if (pass >= 0) {
				if (id == Item.getItemFromBlock(Blocks.dirt) || id == Item.getItemFromBlock(Blocks.grass)
						|| id == Item.getItemFromBlock(Blocks.cobblestone)
						|| id == Item.getItemFromBlock(Blocks.netherrack))
					return true;
			}

			if (pass >= 1) {
				if (id == Item.getItemFromBlock(Blocks.planks) || id == Item.getItemFromBlock(Blocks.stone)
						|| id == Item.getItemFromBlock(Blocks.sandstone)
						|| id == Item.getItemFromBlock(Blocks.brick_block)
						|| id == Item.getItemFromBlock(Blocks.stonebrick)
						|| id == Item.getItemFromBlock(Blocks.nether_brick) // white stone??
						|| id == Item.getItemFromBlock(Blocks.wool) || id == Item.getItemFromBlock(Blocks.glass)
						|| id == Item.getItemFromBlock(Blocks.log))
					return true;
			}

			if (pass >= 2) {
				if (id == Item.getItemFromBlock(Blocks.iron_ore) || id == Item.getItemFromBlock(Blocks.clay))
					return true;
			}

			if (pass >= 3) {
				if (id == Item.getItemFromBlock(Blocks.sand) || id == Item.getItemFromBlock(Blocks.gravel))
					return false;
				if (Block.getBlockFromItem(id).isOpaqueCube() || Block.getBlockFromItem(id).renderAsNormalBlock())
					return true;
			}

			if (pass >= 4) {
				if (Block.getBlockFromItem(id).getMaterial().isSolid())
					return true;
			}
			return false;
		}

		/**
		 * @param Item   id
		 * @param damage damage
		 * @param count  count min size (must be in single stack!)
		 * @return stack consumed
		 */
		private ItemStack consumeItem(Item id, int damage, int count) {
			for (int i = 0; i < cargo.getSizeInventory(); i++) {
				ItemStack stack = cargo.getStackInSlot(i);
				if (stack != null && stack.getItem() == id && (damage == -1 || stack.getItemDamage() == damage)
						&& stack.stackSize >= count) {
					return cargo.decrStackSize(i, count);
				}
			}
			return null;
		}

		/**
		 * @param Item item
		 * @return inventory has some items of kind
		 */
		private boolean hasItem(Item item) {
			for (int i = 0; i < cargo.getSizeInventory(); i++) {
				if (cargo.getStackInSlot(i) != null && cargo.getStackInSlot(i).getItem() == item) {
					return true;
				}
			}
			return false;
		}

		/**
		 * group stacks.
		 */
		public void order() {
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			for (int i = 0; i < getSizeInventory(); i++) {
				stacks.add(getStackInSlot(i));
				setInventorySlotContents(i, null);
			}
			PC_InventoryUtils.groupStacks(stacks);
			for (ItemStack stack : stacks) {
				if (stack != null) {
					PC_InventoryUtils.storeItemStackToInventoryFrom(this, stack);
				}
			}
		}

		/**
		 * Deposit depositable blocks to nearby chest.
		 */
		public void depositToNearbyChest(boolean destroyInstead, Agree agr) {
			int y1 = (int) Math.floor(posY + 0.0002F);
			int x1 = (int) Math.round(posX);
			int z1 = (int) Math.round(posZ);

			for (int x = x1 - 2; x <= x1 + 1; x++) {
				for (int y = y1; y <= y1 + 1; y++) {
					for (int z = z1 - 2; z <= z1 + 1; z++) {
						IInventory chest = PC_InventoryUtils.getInventoryAt(worldObj, x, y, z);
						if (chest != null || destroyInstead) {
							// cycle through and deposit.
							for (int i = 0; i < cargo.getSizeInventory(); i++) {
								boolean stored = false;
								ItemStack stack = cargo.getStackInSlot(i);
								if (stack != null) {
									boolean yes = false;

									Item powerDust = PC_ItemRegistry.getPCItemByName("PCco_ItemPowerDust");

									if (agr == null) {

										yes = stack.getItem() != powerDust
												&& stack.getItem() != Item.getItemFromBlock(Blocks.torch)
												&& stack.getItem() != Items.bucket
												&& stack.getItem() != Items.lava_bucket
												&& stack.getItem() != Items.water_bucket && (!getFlag(keepAllFuel)
														|| PC_RecipeRegistry.getFuelValue(stack) == 0);

									} else {
										if (!getFlag(keepAllFuel) || (stack.getItem() != powerDust
												&& PC_RecipeRegistry.getFuelValue(stack) == 0)) {
											yes = agr.agree(stack);
										} else {
											yes = false;
										}
									}

									yes &= stack.getItem() != PC_ItemRegistry.getPCItemByName("PCws_ItemWeaselDisk");

									if (yes) {
										if (destroyInstead) {
											stored = true;
										} else {
											stored = PC_InventoryUtils.storeItemStackToInventoryFrom(chest, stack);
										}
									}

								}

								if (stored) {
									cargo.setInventorySlotContents(i, null);
								}
							}

							if (shouldMakeEffects()) {
								if (destroyInstead) {
									worldObj.playSoundAtEntity(PCmo_EntityMiner.this, "random.fizz", 0.2F,
											0.5F + rand.nextFloat() * 0.3F);
								} else {
									worldObj.playSoundAtEntity(PCmo_EntityMiner.this, "random.pop", 0.2F,
											0.5F + rand.nextFloat() * 0.3F);
								}
							}

							return;
						}
					}
				}
			}
		}

		/**
		 * Find block for halfstep
		 * 
		 * @return metadata
		 */
		private ItemStack getHalfStep() {
			for (int pass = 0; pass < 3; pass++) {
				for (int i = 0; i < cargo.getSizeInventory(); i++) {
					if (isItemGoodForHalfStep(cargo.getStackInSlot(i), pass)) {
						ItemStack returned = cargo.decrStackSize(i, 1);

						if (returned.getItem() == Item.getItemFromBlock(Blocks.stone_slab)) {
							return returned;
						}

						ItemStack step = makeHalfStep(returned);
						PC_InventoryUtils.storeItemStackToInventoryFrom(cargo, step.copy());
						return step;
					}
				}
			}

			if (getFlag(cobbleMake) && st.level >= LCOBBLE) {
				if (cargo.hasItem(Items.lava_bucket) && cargo.hasItem(Items.water_bucket)) {
					return new ItemStack(Blocks.stone_slab, 1, 3);
				}
			}

			return null;
		}

		private ItemStack makeHalfStep(ItemStack stack) {
			Block id = Block.getBlockFromItem(stack.getItem());
			int dmg = stack.getItemDamage();

			if (id == Blocks.stone) {
				return new ItemStack(Blocks.stone_slab, 1, 0);
			}

			if (id == Blocks.sandstone) {
				return new ItemStack(Blocks.stone_slab, 1, 1);
			}

			if (id == Blocks.planks) {
				return new ItemStack(Blocks.stone_slab, 1, 2);
			}

			if (id == Blocks.cobblestone) {
				return new ItemStack(Blocks.stone_slab, 1, 3);
			}

			if (id == Blocks.brick_block) {
				return new ItemStack(Blocks.stone_slab, 1, 4);
			}

			if (id == Blocks.stonebrick) {
				return new ItemStack(Blocks.stone_slab, 1, 5);
			}

			return new ItemStack(Blocks.stone_slab, 1, 0);
		}

		/**
		 * Check if stack can be crafted to halfstep.
		 * 
		 * @param is   stack
		 * @param pass pass; 0 = stone, 1 = planks+smoothstone, 2 =
		 *             sandstone+stonebrick+brick.
		 * @return is good
		 */
		private boolean isItemGoodForHalfStep(ItemStack is, int pass) {
			if (is == null) {
				return false;
			}
			Block id = Block.getBlockFromItem(is.getItem());

			if (pass == 0) {
				return id == Blocks.cobblestone || id == Blocks.stone_slab;
			}

			if (pass == 1) {
				return id == Blocks.planks || id == Blocks.stone;
			}

			if (pass == 2) {
				return id == Blocks.sandstone || id == Blocks.stonebrick || id == Blocks.brick_block;
			}

			return false;
		}

		@Override
		public boolean canPlayerInsertStackTo(int slot, ItemStack stack) {
			return true;
		}

		@Override
		public boolean canDispenseStackFrom(int slot) {
			ItemStack stack = getStackInSlot(slot);
			if (stack == null)
				return false;

			if (PC_RecipeRegistry.getFuelValue(stack) > 0) {
				if (stack.getItem() instanceof ItemBlock) {
					return !getFlag(keepAllFuel);
				} else {
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean canDropStackFrom(int slot) {
			return true;
		}

		@Override
		public int getSlotStackLimit(int slotIndex) {
			return getInventoryStackLimit();
		}

		@Override
		public boolean canPlayerTakeStack(int slotIndex, EntityPlayer entityPlayer) {
			return true;
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return null;
		}

		@Override
		public boolean canInsertItem(int i, ItemStack itemstack, int j) {
			return true;
		}

		@Override
		public boolean canExtractItem(int i, ItemStack itemstack, int j) {
			return true;
		}

		public ItemStack[] toItemStack() {
			ItemStack[] is = new ItemStack[this.getSizeInventory()];
			for (int i = 0; i < this.getSizeInventory(); i++) {
				is[i] = this.getStackInSlot(i);
			}
			return is;
		}

		@Override
		public void syncInventory(int side, EntityPlayer player, int slot) {
			if (side == 0) {// TODO:
				NBTTagCompound inv = new NBTTagCompound();
				PC_InventoryUtils.saveInventoryToNBT(inv, "cargo", this);
				PC_PacketHandler.sendToAll(new PCmo_PacketMinerClient(inv, new Object[] { 0, slot }));// slot has Entity
																										// ID
			}
		}

		@Override
		public int getSizeInventory() {
			return 11 * 5;
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			if (inventoryContents[i] != null) {
				if (inventoryContents[i].stackSize <= j) {
					ItemStack itemstack = inventoryContents[i];
					inventoryContents[i] = null;
					this.markDirty();
					return itemstack;
				}

				ItemStack itemstack1 = inventoryContents[i].splitStack(j);

				if (inventoryContents[i].stackSize == 0) {
					inventoryContents[i] = null;
				}

				this.markDirty();
				return itemstack1;
			} else {
				return null;
			}
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			if (slot >= 0 && slot < this.getSizeInventory())
				return this.getStackInSlot(slot);
			return null;
		}

		@Override
		public String getInventoryName() {
			return "pc.miner.chestName";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
			return true;
		}

		@Override
		public void openInventory() {
		}

		@Override
		public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
			return true;
		}
	}

	// STATUS VARIABLES and FLAGS

	/** Speed based on level. */
	private static final double[] MOTION_SPEED = { 0.04D, 0.05D, 0.06D, 0.07D, 0.08D, 0.09D, 0.11D, 0.12D };

	/**
	 * Should this item stack be destroyed?<br>
	 * (from console screen)
	 * 
	 * @param stack the stack collected
	 * @return destroy it
	 */
	private boolean shouldDestroyStack(ItemStack stack) {
		if (stack == null) {
			return true;
		}
		/*
		 * if (stack.getItem() == Item.getItemFromBlock(Blocks.cobblestone)) { return
		 * (DESTROY & COBBLE) != 0; } if (stack.getItem() ==
		 * Item.getItemFromBlock(Blocks.dirt)) { return (DESTROY & DIRT) != 0; } if
		 * (stack.getItem() == Item.getItemFromBlock(Blocks.gravel)) { return (DESTROY &
		 * GRAVEL) != 0; }
		 */
		return false;
	}

	/**
	 * Reset:
	 * <ul>
	 * <li>motion
	 * <li>commands list
	 * <li>current command
	 * <li>mine counter
	 * <li>align to blocks and get ready for next keyboard command.
	 * </ul>
	 * Typically called after "DELETE" key is pressed.
	 */
	public void resetEverything() {
		motionX = motionZ = 0;
		st.commandList = "";
		st.commandListSaved = "";
		st.currentCommand = -1;
		alignToBlocks();
		st.paused = false;
		st.halted = true;
		resetStatus();
	}

	/**
	 * Pause program execution when the GUI is opened for running Miner.
	 */
	public void pauseProgram() {
		if (st.paused) {
			return;
		}

		// pack last instruction into saved codebuffer
		st.commandListSaved = new String(st.commandList);
		String instruction = Character.toString(PCmo_Command.getCharFromInt(st.currentCommand));
		if (st.stepCounter > 0 && PCmo_Command.isCommandMove(st.currentCommand)) {
			instruction = (st.currentCommand == PCmo_Command.FORWARD ? "" : "-") + Integer.toString(st.stepCounter);
		} else if (instruction.equals("?")) {
			instruction = "";
		}

		st.commandListSaved = instruction + " " + st.commandListSaved;
		st.commandList = "";

		resetStatus();
		st.paused = true;
	}

	/**
	 * Resume program after GUI is closed.<br>
	 * If not paused, do nothing.
	 */
	public void resumeProgram() {
		if (!st.paused) {
			return;
		}

		resetStatus();
		st.commandList = new String(st.commandListSaved.trim());
		st.commandListSaved = "";

		st.paused = false;
	}

	/**
	 * Reset various status flags and counters, but keep fuel buffer.
	 */
	private void resetStatus() {
		st.currentCommand = -1;
		roundRotation();
		st.target.x = (int) posX;
		st.target.z = (int) posZ;
		resetMineCounter();
		st.stepCounter = 0;
		st.fuelDeficit = 0;
		st.fuelAllocated = 0;
		st.bridgeDone = false;
		st.upStepLaid = false;
	}

	/**
	 * @return is miner ready for keyboard command
	 */
	public boolean canReceiveKeyboardCommand() {
		if (playerConectedID == null/* || TODO!(st.paused || st.halted || brain.engine.isProgramFinished) */) {
			return false;
		}
		st.commandList = st.commandList.trim();
		return true;
	}

	/**
	 * Keyboard command sent to this miner
	 * 
	 * @param i command index
	 */
	public void receiveKeyboardCommand(int i) {
		Character chr = PCmo_Command.getCharFromInt(i);
		if (chr.equals('?')) {
			return;
		}
		st.commandList += chr.toString();
	}

	/**
	 * Append given code to the command list.<br>
	 * Used for "turn around" command, which sends RR.
	 * 
	 * @param code code to append
	 * @throws ParseException if code is invalid
	 */
	public void appendCode(String code) throws ParseException {
		st.commandList = st.commandList + " " + PCmo_Command.parseCode(code);
	}

	/**
	 * Put code to the commands list.
	 * 
	 * @param code the code to parse and start
	 * @throws ParseException if code is invalid
	 */
	public void setCode(String code) throws ParseException {
		st.commandList = PCmo_Command.parseCode(code);
	}

	/**
	 * Get next command from buffer or step count, prepare for execution and start
	 * it.
	 * 
	 * @return the command, or -1 if buffer is empty
	 */
	private int getNextCommand() {
		st.commandList = st.commandList.trim();

		if (st.commandList.length() > 0) {
			Character first = st.commandList.charAt(0);

			int cmd = PCmo_Command.getIntFromChar(first);
			if (cmd != -1) {
				st.commandList = st.commandList.substring(1);
				if (cmd == PCmo_Command.FORWARD || cmd == PCmo_Command.BACKWARD || cmd == PCmo_Command.UP) {
					st.stepCounter = 1;
				}
				return cmd;
			} else if (Character.isDigit(first) || first.equals('-')) {
				String numbuff = Character.toString(first);
				st.commandList = st.commandList.substring(1);

				while (st.commandList.length() > 0) {
					first = Character.valueOf(st.commandList.charAt(0));

					if (Character.isDigit(first)) {
						numbuff += first.toString();
					} else {
						break;
					}
					st.commandList = st.commandList.substring(1);
				}

				try {
					st.stepCounter = Integer.valueOf(numbuff);
					cmd = st.stepCounter > 0 ? PCmo_Command.FORWARD : PCmo_Command.BACKWARD;

					st.stepCounter = Math.abs(st.stepCounter);
					if (st.stepCounter == 0) {
						return -1;
					}

					return cmd;

				} catch (NumberFormatException nfe) {
					return -1;
				}
			} else {
				st.commandList = st.commandList.substring(1);
				return getNextCommand();
			}
		}
		return -1;
	}

	// === EXECUTION AND STATUS UTILS ===

	/**
	 * Check if miner has at the target coordinates, which indicates that MOVE
	 * command was finished.
	 * 
	 * @return true if miner is at target pos
	 */
	private boolean isMinerAtTargetPos() {
		if (st.currentCommand == PCmo_Command.FORWARD || st.currentCommand == PCmo_Command.UP) {
			if (rotationYaw == 0) {
				return posX <= st.target.x;
			}
			if (rotationYaw == 90) {
				return posZ <= st.target.z;
			}
			if (rotationYaw == 180) {
				return posX >= st.target.x;
			}
			if (rotationYaw == 270) {
				return posZ >= st.target.z;
			}
		} else if (st.currentCommand == PCmo_Command.BACKWARD) {
			if (rotationYaw == 0) {
				return posX >= st.target.x;
			}
			if (rotationYaw == 90) {
				return posZ >= st.target.z;
			}
			if (rotationYaw == 180) {
				return posX <= st.target.x;
			}
			if (rotationYaw == 270) {
				return posZ <= st.target.z;
			}
		}
		return true;
	}

	/**
	 * Get Miner's absolute distance to the target X coordinate
	 * 
	 * @return distance
	 */
	private double getTargetDistanceX() {
		return Math.abs(posX - st.target.x);
	}

	/**
	 * Get Miner's absolute distance to the target Z coordinate
	 * 
	 * @return distance
	 */
	private double getTargetDistanceZ() {
		return Math.abs(posZ - st.target.z);
	}

	/**
	 * Round rotation to 0, 90, 180 or 270 degrees.
	 */
	private void roundRotation() {
		rotationYaw = prevRotationYaw = getRotationRounded();
		st.rotationRemaining = 0;
	}

	public int getRotationRounded() {

		float a = rotationYaw;

		if (a < 0) {
			a = 360F - a;
		}
		if (a > 360F) {
			a = a - 360F;
		}

		if (a >= 315 || a < 45) {
			a = 0;
		}
		if (a >= 45 && a < 135) {
			a = 90;
		}
		if (a >= 135 && a < 215) {
			a = 180;
		}
		if (a >= 215 && a < 315) {
			a = 270;
		}

		return Math.round(a);
	}

	/**
	 * Move to rounded position (round X and Z)
	 */
	private void alignToBlocks() {
		setPosition(Math.round(posX), posY, Math.round(posZ));
	}

	/**
	 * Get ready for command's execution, and if possible, execute it right now.
	 */
	private void prepareForCommandExecution() {
		if (st.currentCommand > -1) {
			st.realCommand = st.currentCommand;
			prevPosX = posX = (int) Math.round(posX);
			prevPosX = posZ = (int) Math.round(posZ);
			int x = (int) Math.round(posX);
			int z = (int) Math.round(posZ);
			int y = (int) Math.floor(posY + 0.0002F);

			roundRotation();

			if (st.currentCommand == PCmo_Command.DEPOSIT) {
				cargo.depositToNearbyChest(false, null);
				st.currentCommand = -1;

			} else if (st.currentCommand == PCmo_Command.DISASSEMBLY) {
				turnIntoBlocks();
				return;
			} else if (st.currentCommand == PCmo_Command.BRIDGE_ENABLE) {

				setFlag(bridgeEnabled, true);
				st.currentCommand = -1;
			} else if (st.currentCommand == PCmo_Command.BRIDGE_DISABLE) {
				setFlag(bridgeEnabled, false);
				st.currentCommand = -1;

			} else if (st.currentCommand == PCmo_Command.MINING_ENABLE) {
				setFlag(miningEnabled, true);
				st.currentCommand = -1;
			} else if (st.currentCommand == PCmo_Command.MINING_DISABLE) {
				setFlag(miningEnabled, false);
				st.currentCommand = -1;

			} else if (st.currentCommand == PCmo_Command.LAVA_ENABLE) {
				setFlag(lavaFillingEnabled, true);
				st.currentCommand = -1;
			} else if (st.currentCommand == PCmo_Command.LAVA_DISABLE) {
				setFlag(lavaFillingEnabled, false);
				st.currentCommand = -1;

			} else if (st.currentCommand == PCmo_Command.WATER_ENABLE) {
				setFlag(waterFillingEnabled, true);
				st.currentCommand = -1;
			} else if (st.currentCommand == PCmo_Command.WATER_DISABLE) {
				setFlag(waterFillingEnabled, false);
				st.currentCommand = -1;

			} else if (st.currentCommand == PCmo_Command.DOWN) {
				if (!getFlag(miningEnabled)) {
					st.currentCommand = -1;
				} else {
					resetMineCounter();
					st.mineCounter[4] = -1;
					st.mineCounter[5] = -1;
				}

			} else if (st.currentCommand == PCmo_Command.UP) {
				if (!getFlag(miningEnabled)) {
					st.currentCommand = -1;
				} else {
					if (st.addFuelForCost(getStepCost())) {

						resetMineCounter();
						if (rotationYaw == 0) {
							st.target = new PC_VecI(x - 1, y, z);
						}
						if (rotationYaw == 90) {
							st.target = new PC_VecI(x, y, z - 1);
						}
						if (rotationYaw == 180) {
							st.target = new PC_VecI(x + 1, y, z);
						}
						if (rotationYaw == 270) {
							st.target = new PC_VecI(x, y, z + 1);
						}

						if (!isOnHalfStep()) {
							st.mineCounter[6] = -1;
							st.mineCounter[7] = -1;
							st.mineCounter[8] = -1;
							st.mineCounter[9] = -1;
							st.mineCounter[10] = -1;
							st.mineCounter[11] = -1;

							st.upStepLaid = false;
						} else {
							st.currentCommand = PCmo_Command.FORWARD;

							// lay stepblock.
							switch ((int) Math.floor(rotationYaw)) {
							case 0:
								layHalfStep(x - 2, y, z - 1, false);
								layHalfStep(x - 2, y, z, false);
								break;

							case 90:
								layHalfStep(x - 1, y, z - 2, false);
								layHalfStep(x, y, z - 2, false);
								break;

							case 180:
								layHalfStep(x + 1, y, z - 1, false);
								layHalfStep(x + 1, y, z, false);
								break;

							case 270:
								layHalfStep(x - 1, y, z + 1, false);
								layHalfStep(x, y, z + 1, false);
								break;
							}

						}

					}
				}

			} else if (st.currentCommand == PCmo_Command.FORWARD) {
				if (worldObj.isRemote || st.addFuelForCost(getStepCost())) {
					resetMineCounter();
					st.bridgeDone = false;
					if (rotationYaw == 0) {
						st.target.x = x - 1;
						st.target.z = z;
					}
					if (rotationYaw == 90) {
						st.target.z = z - 1;
						st.target.x = x;
					}
					if (rotationYaw == 180) {
						st.target.x = x + 1;
						st.target.z = z;
					}
					if (rotationYaw == 270) {
						st.target.z = z + 1;
						st.target.x = x;
					}
				}

			} else if (st.currentCommand == PCmo_Command.BACKWARD) {
				if (st.addFuelForCost(getStepCost())) {
					st.bridgeDone = false;
					if (rotationYaw == 0) {
						st.target.x = x + 1;
						st.target.z = z;
					}
					if (rotationYaw == 90) {
						st.target.z = z + 1;
						st.target.x = x;
					}
					if (rotationYaw == 180) {
						st.target.x = x - 1;
						st.target.z = z;
					}
					if (rotationYaw == 270) {
						st.target.z = z - 1;
						st.target.x = x;
					}
				}

			} else if (st.currentCommand == PCmo_Command.LEFT) {

				st.rotationRemaining = -90;

			} else if (st.currentCommand == PCmo_Command.RIGHT) {

				st.rotationRemaining = 90;

			} else if (st.currentCommand == PCmo_Command.NORTH) {

				if (rotationYaw == 0) {
					st.currentCommand = PCmo_Command.RIGHT;
					st.rotationRemaining = 90;
				}
				if (rotationYaw == 180) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = -90;
				}
				if (rotationYaw == 90) {
					st.currentCommand = -1;
				}
				if (rotationYaw == 270) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = rand.nextBoolean() ? 180 : -180;
				}

			} else if (st.currentCommand == PCmo_Command.SOUTH) {

				if (rotationYaw == 0) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = -90;
				}
				if (rotationYaw == 180) {
					st.currentCommand = PCmo_Command.RIGHT;
					st.rotationRemaining = 90;
				}
				if (rotationYaw == 90) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = rand.nextBoolean() ? 180 : -180;
				}
				if (rotationYaw == 270) {
					st.currentCommand = -1;
				}

			} else if (st.currentCommand == PCmo_Command.EAST) {

				if (rotationYaw == 0) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = rand.nextBoolean() ? 180 : -180;
				}
				if (rotationYaw == 180) {
					st.currentCommand = -1;
				}
				if (rotationYaw == 90) {
					st.currentCommand = PCmo_Command.RIGHT;
					st.rotationRemaining = 90;
				}
				if (rotationYaw == 270) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = -90;
				}

			} else if (st.currentCommand == PCmo_Command.WEST) {

				if (rotationYaw == 0) {
					st.currentCommand = -1;
				}
				if (rotationYaw == 180) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = rand.nextBoolean() ? 180 : -180;
				}
				if (rotationYaw == 90) {
					st.currentCommand = PCmo_Command.LEFT;
					st.rotationRemaining = -90;
				}
				if (rotationYaw == 270) {
					st.currentCommand = PCmo_Command.RIGHT;
					st.rotationRemaining = 90;
				}

			} else {
				st.currentCommand = -1;
			}
		}
	}

	public void setFlag(String key, boolean flag) {
		setInfo(key, flag);
	}

	/**
	 * Get price (in fuel points) for one step.<br>
	 * It's equal to half of current level.
	 * 
	 * @return step cost
	 */
	private int getStepCost() {
		return MathHelper.clamp_int(st.level / 2, 1, 4);
	}

	/**
	 * Look if there is any player who may appreciate the awesome effects and
	 * sounds.
	 * 
	 * @return should make effects
	 */
	private boolean shouldMakeEffects() {
		return worldObj.getClosestPlayerToEntity(this, 17D) != null && PC_SoundRegistry.isSoundEnabled();
	}

	/**
	 * Play the "ticking" sound made by miner's tracks.
	 */
	private void playMotionEffect() {
		if (!shouldMakeEffects()) {
			return;
		}
		worldObj.playSoundAtEntity(this, "random.click", 0.02F, 0.8F);
	}

	/**
	 * Spawn breaking particles for blockparticles
	 * 
	 * @param pos         position
	 * @param block_index index of the block in mining list
	 */
	private void playMiningEffect(PC_VecI pos, int block_index) {
		st.miningTickCounter++;

		if (!shouldMakeEffects()) {
			return;
		}
		Block block = PC_Utils.getBID(worldObj, pos);

		if (st.miningTickCounter % 8 == 0 && block != null) {
			PC_SoundRegistry.playSound(pos.x + 0.5F, pos.y + 0.5F, pos.z + 0.5F, block.stepSound.getBreakSound(),
					(block.stepSound.getVolume() + 1.0F) / 8F, block.stepSound.getPitch() * 0.5F);
		}

		if (block != null) {
			Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(pos.x, pos.y, pos.z,
					block_index < 4 ? getSideFromYaw() : (block_index < 6 ? 1 : 0));
		}
	}

	/**
	 * Convert "rotation yaw" angle to block side index.
	 * 
	 * @return block side for particles
	 */
	private int getSideFromYaw() {
		if (rotationYaw == 0) {
			return 5;
		}
		if (rotationYaw == 90) {
			return 3;
		}
		if (rotationYaw == 180) {
			return 4;
		}
		if (rotationYaw == 270) {
			return 2;
		}
		return 1;
	}

	/**
	 * Perform block harvesting, drop the item, remove block and play sound.
	 * 
	 * @param pos
	 */
	private void harvestBlock_do(PC_VecI pos) {
		if (pos == null)
			return;
		Block block = PC_Utils.getBID(worldObj, pos);
		int meta = PC_Utils.getMD(worldObj, pos);
		if (!shouldIgnoreBlockForHarvesting(pos, block)) {
			List<PC_Struct2<PC_VecI, ItemStack>> drops = PC_BuildingManager.harvest(worldObj, pos, 0);
			for (PC_Struct2<PC_VecI, ItemStack> drop : drops) {
				if (PC_SoundRegistry.isSoundEnabled()) {
					worldObj.playAuxSFX(2001, pos.x, pos.y, pos.z, (meta << 12));
				}
				PC_InventoryUtils.storeItemStackToInventoryFrom(cargo, drop.b);
			}
		}
	}

	/**
	 * Perform mining update of given block.
	 * 
	 * @param pos miner coordinate
	 * @param loc block position index.
	 */
	private void performMiningUpdate(PC_VecI pos, int loc) {
		Block block = PC_Utils.getBID(worldObj, pos);

		harvestBlock_do(pos);
		if (loc == 4 || loc == 5) {
			bridgeBuilding_do(pos.offset(0, -1, 0));
		}

		if (st.mineCounter[loc] <= 0) {

			if (shouldIgnoreBlockForHarvesting(pos, block)) {
				if (st.mineCounter[loc] < 0) {
					st.mineCounter[loc] = 0;
				}
				return;
			}

			if (block != null) {
				int cost = getBlockMiningCost(pos, block);
				if (block == Blocks.bedrock && st.level == 8 && pos.y == 0) {
					cost = -1; // What it should be?
				}

				if (cost > 0) {
					if (st.addFuelForCost(cost)) {
						st.mineCounter[loc] = cost;
					}
				}
			}

		}

		if (st.fuelDeficit == 0 && st.mineCounter[loc] > 0) {
			int step = st.level;
			if (st.mineCounter[loc] < step) {
				step = st.mineCounter[loc];
				st.mineCounter[loc] = 0;
			} else {
				st.mineCounter[loc] -= step;
			}

			st.consumeAllocatedFuel(step);

			if (st.mineCounter[loc] == 0) {
				harvestBlock_do(pos);
			}
		}

		if (st.mineCounter[loc] != 0 && block != null) {
			playMiningEffect(pos, loc);
		}

	}

	/**
	 * @return is mining in progress
	 */
	public boolean isMiningInProgress() {
		for (int counter : st.mineCounter) {
			if (counter != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return is mining finished
	 */
	public boolean isMiningDone() {
		return !isMiningInProgress();
	}

	/**
	 * Reset mining counters
	 */
	private void resetMineCounter() {
		for (int element : st.mineCounter) {
			st.fuelAllocated -= element;
			if (st.fuelAllocated <= 0) {
				st.fuelAllocated = 0;
				break;
			}
		}

		for (int i = 0; i < 4; i++) {
			st.mineCounter[i] = -1;
		}
		for (int i = 4; i < st.mineCounter.length; i++) {
			st.mineCounter[i] = 0;
		}
	}

	/**
	 * Check if block is unharvestable
	 * 
	 * @param pos
	 * @param id  block id
	 * @return is not harvested
	 */
	private boolean shouldIgnoreBlockForHarvesting(PC_VecI pos, Block block) {

		if (block == Blocks.air || block == null || block instanceof BlockTorch || block == Blocks.fire
				|| block == Blocks.portal || block == Blocks.end_portal || block instanceof BlockLiquid
				|| block == Blocks.redstone_wire || block == Blocks.stone_pressure_plate
				|| block == Blocks.wooden_pressure_plate || PC_MSGRegistry.hasFlag(worldObj, pos, "LIFT")
				|| PC_MSGRegistry.hasFlag(worldObj, pos, "BELT")) {
			return true;
		}

		boolean flag = true;

		// if (PC_CropHarvestingManager.isBlockRegisteredCrop()) {
		// flag = !PC_CropHarvestingManager.canHarvestBlock(id, pos.getMeta(worldObj));
		// }

		if (flag && block.getCollisionBoundingBoxFromPool(worldObj, pos.x, pos.y, pos.z) == null) {
			return true;
		}

		return false;

	}

	/**
	 * Check if miner is able to break given block.
	 * 
	 * @param pos
	 * @param block
	 * @return can break
	 */
	public boolean canHarvestBlockWithCurrentLevel(PC_VecI pos, Block block) {
		// exception - miner 8 can mine bedrock.
		if (PC_MSGRegistry.hasFlag(worldObj, pos, "HARVEST_STOP")
				|| PC_MSGRegistry.hasFlag(worldObj, pos, "NO_HARVEST")) {
			return false;
		}
		if (block == Blocks.bedrock) {
			return st.level == 8 && pos.y > 0;
		}

		switch (st.level) {
		case 1: // all but rocks and iron
			return block.getMaterial() != Material.rock && block.getMaterial() != Material.iron
					&& block != PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal");
		case 2: // everything but precious ores (cobble, coal, iron)
			return block != Blocks.obsidian && block != Blocks.gold_ore && block != Blocks.lapis_ore
					&& block != Blocks.lapis_block && block != Blocks.gold_block && block != Blocks.diamond_ore
					&& block != Blocks.diamond_block && block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore
					&& block != PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal");
		case 3: // all but diamonds + obsidian + power crystals
			return block != Blocks.obsidian && block != Blocks.diamond_ore && block != Blocks.diamond_block
					&& block != PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal");
		case 4: // all but obsidian
			return block != Blocks.obsidian;
		case 5:
		case 6:
		case 7:
		case 8:
			return true;
		}
		return false;
	}

	/**
	 * Get block mining price.
	 * 
	 * @param pos position
	 * @param id  block ID
	 * @return block's mining cost in fuel points.
	 */
	private int getBlockMiningCost(PC_VecI pos, Block block) {
		if (!canHarvestBlockWithCurrentLevel(pos, block)) {
			return -1;
		}
		if (shouldIgnoreBlockForHarvesting(pos, block)) {
			return 0;
		}

		// dirt, gravel, sand, non-rocks.
		if (block.getMaterial() != Material.rock && block.getMaterial() != Material.iron) {
			return 10;
		}
		if (block == Blocks.redstone_ore || block == Blocks.lapis_ore || block == Blocks.lit_redstone_ore
				|| block == Blocks.gold_ore) {
			return 100;// redstone,lapis,gold
		}
		if (block == Blocks.coal_ore || block == Blocks.iron_ore || block == Blocks.stonebrick
				|| block == Blocks.iron_block || block == Blocks.coal_block) {
			return 30;// coal,iron,stonebrick
		}
		if (block == Blocks.diamond_block || block == Blocks.diamond_ore) {
			return 150; // diamond
		}
		if (block == Blocks.obsidian) {
			return 600;
		}
		if (block == Blocks.bedrock) {
			return 2000;
		}
		if (block == PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal")) {
			return 100;
		}

		return 20;
	}

	/**
	 * Check if given location is empty.<br>
	 * Coord is X+ Z+ Y- corner of miner
	 * 
	 * @param pos
	 * @return is empty
	 */
	private boolean isLocationEmpty(PC_VecI pos) {
		boolean notempty = false;
		notempty |= !checkIfAir(pos.offset(0, 0, 0), true);
		notempty |= !checkIfAir(pos.offset(-1, 0, 0), true);
		notempty |= !checkIfAir(pos.offset(0, 0, -1), true);
		notempty |= !checkIfAir(pos.offset(-1, 0, -1), true);
		notempty |= !checkIfAir(pos.offset(0, 1, 0), false);
		notempty |= !checkIfAir(pos.offset(-1, 1, 0), false);
		notempty |= !checkIfAir(pos.offset(0, 1, -1), false);
		notempty |= !checkIfAir(pos.offset(-1, 1, -1), false);

		return !notempty;
	}

	/**
	 * Check if block at given position is air, laid half step or non-colliding
	 * block.
	 * 
	 * @param pos   position in world
	 * @param lower is it the lower block of miner's body
	 * @return is free to move
	 */
	private boolean checkIfAir(PC_VecI pos, boolean lower) {
		Block block = PC_Utils.getBID(worldObj, pos);

		if (lower && block == Blocks.stone_slab) {
			return true;
		}

		return block == null || block.getCollisionBoundingBoxFromPool(worldObj, pos.x, pos.y, pos.z) == null;
	}

	/**
	 * @return is miner standing on halfstep
	 */
	public boolean isOnHalfStep() {
		return (posY - Math.floor(posY + 0.0002)) >= 0.4D;
	}

	/**
	 * Place bridge blocks at target pos; target is X+ Z+.
	 * 
	 * @return false if it run out of material
	 */
	private boolean performBridgeBuilding() {
		if (!getFlag(bridgeEnabled)) {
			return true;
		}

		int ii = -1;
		int y = (int) Math.floor(posY - 0.9999F);
		if (isOnHalfStep()) {
			ii = 0;
		}
		if (!bridgeBuilding_do(st.target.setY((int) Math.round(posY - 0.2F)).offset(0, ii, 0))) {
			return false;
		}
		if (!bridgeBuilding_do(st.target.setY((int) Math.round(posY - 0.2F)).offset(-1, ii, 0))) {
			return false;
		}
		if (!bridgeBuilding_do(st.target.setY((int) Math.round(posY - 0.2F)).offset(0, ii, -1))) {
			return false;
		}
		if (!bridgeBuilding_do(st.target.setY((int) Math.round(posY - 0.2F)).offset(-1, ii, -1))) {
			return false;
		}
		return true;
	}

	/**
	 * Place bridge block at this exact position.
	 * 
	 * @param pos position
	 * @return success
	 */
	private boolean bridgeBuilding_do(PC_VecI pos) {
		if (checkIfAir(pos, false)) {
			if (st.level < LBRIDGE) {
				st.currentCommand = -1;
				return false;
			}
			ItemStack fill = cargo.getBlockForBuilding();
			if (fill == null) {
				return false;
			}
			Block block = Block.getBlockFromItem(fill.getItem());
			int meta = fill.getItemDamage();
			PC_Utils.setBID(worldObj, pos, block, meta);

			if (shouldMakeEffects()) {
				worldObj.playSoundEffect(pos.x + 0.5F, pos.y + 0.5F, pos.z + 0.5F, block.stepSound.soundName,
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
			}
		}

		return true;
	}

	/**
	 * Lay half step.<br>
	 * If already on step, check the block in front.<br>
	 * Smartly prevents falling into caves.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param step is already on half step.
	 */
	private void layHalfStep(int x, int y, int z, boolean step) {
		if (step) {
			if (worldObj.getBlock(x, y, z) == Blocks.air) {
				ItemStack halfstep = cargo.getHalfStep();
				if (halfstep != null) {
					PC_Utils.setBID(worldObj, x, y, z, Block.getBlockFromItem(halfstep.getItem()),
							((ItemBlock) halfstep.getItem()).getMetadata(halfstep.getItemDamage()));
				}

			}
		} else {

			// fix for in front of.
			Block block = worldObj.getBlock(x, y + (step ? -1 : 0), z);
			if (block == Blocks.air || block == Blocks.water || block == Blocks.lava || block == Blocks.flowing_lava
					|| block == Blocks.flowing_water
					|| block.getCollisionBoundingBoxFromPool(worldObj, x, y, z) == null) {
				ItemStack fill = cargo.getBlockForBuilding();
				if (fill == null) {
					return;
				}

				block = Block.getBlockFromItem(fill.getItem());
				int meta = fill.getItemDamage();
				PC_Utils.setBID(worldObj, x, y + (step ? -1 : 0), z, block, meta);
				if (shouldMakeEffects()) {
					worldObj.playSoundEffect(x + 0.5F, (float) y + (step ? -1 : 0) + 0.5F, z + 0.5F,
							block.stepSound.soundName, (block.stepSound.getVolume() + 1.0F) / 2.0F,
							block.stepSound.getPitch() * 0.8F);
				}
			}
		}
	}

	/**
	 * Fill nearby water with stones from inventory.
	 */
	private void fillWaterLavaAir() {

		if (!getFlag(waterFillingEnabled) && !getFlag(lavaFillingEnabled) && !getFlag(airFillingEnabled))
			return;

		int y1 = (int) Math.floor(posY + 0.0002F);
		int x1 = (int) Math.round(posX);
		int z1 = (int) Math.round(posZ);

		boolean replace = true;
		for (int x = x1 - 2; x <= x1 + 1; x++) {
			for (int y = y1 - 1; y <= y1 + 2; y++) {
				for (int z = z1 - 2; z <= z1 + 1; z++) {
					replace = !((y == y1 || y == y1 + 1) && (x == x1 || x == x1 - 1) && (z == z1 || z == z1 - 1));

					if (x == x1 - 2 && y == y1 - 1) {
						continue;
					}
					if (x == x1 - 2 && y == y1 + 2) {
						continue;
					}
					if (x == x1 + 1 && y == y1 - 1) {
						continue;
					}
					if (x == x1 + 1 && y == y1 + 2) {
						continue;
					}

					if (z == z1 - 2 && y == y1 - 1) {
						continue;
					}
					if (z == z1 - 2 && y == y1 + 2) {
						continue;
					}
					if (z == z1 + 1 && y == y1 - 1) {
						continue;
					}
					if (z == z1 + 1 && y == y1 + 2) {
						continue;
					}

					if (x == x1 - 2 && z == z1 - 2) {
						continue;
					}
					if (x == x1 - 2 && z == z1 + 1) {
						continue;
					}
					if (x == x1 + 1 && z == z1 - 2) {
						continue;
					}
					if (x == x1 + 1 && z == z1 + 1) {
						continue;
					}

					switch (Math.round(rotationYaw)) {
					case 180:
						if (x == x1 - 2 || x == x1 - 1) {
							replace = false;
						}
						break;
					case 270:
						if (z == z1 - 2 || z == z1 - 1) {
							replace = false;
						}
						break;
					case 0:
						if (x == x1 + 1 || x == x1) {
							replace = false;
						}
						break;
					case 90:
						if (z == z1 + 1 || z == z1) {
							replace = false;
						}
						break;
					}

					if (y == y1 + 2 && getFlag(airFillingEnabled) && isOnHalfStep()
							&& st.currentCommand == PCmo_Command.UP)
						replace = false;
					if (y == y1 - 1 && getFlag(airFillingEnabled))
						replace = getFlag(bridgeEnabled);

					Block block = worldObj.getBlock(x, y, z);
					if (((block == Blocks.water || block == Blocks.flowing_water) && getFlag(waterFillingEnabled)
							&& st.level >= LWATER)
							|| ((block == Blocks.flowing_lava || block == Blocks.lava) && getFlag(lavaFillingEnabled)
									&& st.level >= LLAVA)
							|| (block == Blocks.air && getFlag(airFillingEnabled) && st.level >= LAIR)) {

						if (block == Blocks.lava || block == Blocks.flowing_lava) {
							lavaFillBucket();
						}

						Block fillBlock = Blocks.air;
						int fillMeta = 0;
						if (replace) {
							ItemStack fill = cargo.getBlockForBuilding();
							if (fill != null) {
								fillBlock = Block.getBlockFromItem(fill.getItem());
								fillMeta = fill.getItemDamage();
							}
						}
						PC_Utils.setBID(worldObj, x, y, z, fillBlock, fillMeta);
						if (fillBlock != null) {
							if (shouldMakeEffects()) {
								worldObj.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, fillBlock.stepSound.soundName,
										(fillBlock.stepSound.getVolume() + 1.0F) / 2.0F,
										fillBlock.stepSound.getPitch() * 0.8F);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Try to unburry itself (if cobble was spawned at miner's position, sand fell
	 * on it or whatever.
	 * 
	 * @param targetPos do this for target position; current pos otherwise
	 */
	private void burriedFix(boolean targetPos) {

		int y1 = (int) Math.floor(posY + 0.0002F);

		if (isOnHalfStep()) {
			y1++;
		}

		int x1 = targetPos ? st.target.x : (int) Math.round(posX);
		int z1 = targetPos ? st.target.z : (int) Math.round(posZ);

		for (int x = x1 - 1; x <= x1; x++) {
			for (int y = y1; y <= y1 + 1; y++) {
				for (int z = z1 - 1; z <= z1; z++) {
					Block block = worldObj.getBlock(x, y, z);

					// get entry for new blocks.
					if (block != Blocks.air
							&& (block instanceof BlockSand || block == Blocks.cobblestone || block == Blocks.dirt)) {
						harvestBlock_do(new PC_VecI(x, y, z));
					}
				}
			}
		}

	}

	/**
	 * Place torches on ground and on walls, if enabled.
	 */
	private void performTorchPlacing() {
		if (st.level < 3) {
			return;
		}

		if (!getFlag(torches))
			return;

		int y = (int) Math.floor(posY + 0.0002F);
		int x = (int) Math.round(posX);
		int z = (int) Math.round(posZ);

		if (getBrightness(1.0F) > 0.2F) {
			return;
		}
		if (handleWaterMovement()) {
			return;
		}

		if (!cargo.hasItem(Item.getItemFromBlock(Blocks.torch))) {
			return;
		}

		int leftX = x, leftZ = z, rightX = x, rightZ = z;

		if (rotationYaw == 0) {
			rightZ = z - 1;
			leftZ = z;
		}
		if (rotationYaw == 90) {
			rightX = x;
			leftX = x - 1; /* rightZ=leftZ=z-1; */
		}
		if (rotationYaw == 180) {
			leftZ = z - 1;
			rightZ = z;
			rightX = leftX = x - 1;
		}
		if (rotationYaw == 270) {
			rightX = x - 1;
			leftX = x;
			leftZ = rightZ = z - 1;
		}

		Block torch = Blocks.torch;

		if (!getFlag(torchesOnlyOnFloor)) {
			if (worldObj.getBlock(rightX, y + 1, rightZ) == Blocks.air
					&& torch.canPlaceBlockAt(worldObj, rightX, y + 1, rightZ)) {
				PC_Utils.setBID(worldObj, rightX, y + 1, rightZ, torch, 0);
				cargo.consumeItem(Item.getItemFromBlock(Blocks.torch), -1, 1);
				return;
			}
			if (worldObj.getBlock(leftX, y + 1, leftZ) == Blocks.air
					&& torch.canPlaceBlockAt(worldObj, leftX, y + 1, leftZ)) {
				PC_Utils.setBID(worldObj, leftX, y + 1, leftZ, torch, 0);
				cargo.consumeItem(Item.getItemFromBlock(Blocks.torch), -1, 1);
				return;
			}
		}

		if (worldObj.getBlock(rightX, y, rightZ) == Blocks.air && torch.canPlaceBlockAt(worldObj, rightX, y, rightZ)) {
			PC_Utils.setBID(worldObj, rightX, y, rightZ, torch, 0);
			// set on floor if not building stairs.
			if (st.realCommand != PCmo_Command.UP) {
				Blocks.torch.onBlockPlacedBy(worldObj, rightX, y, rightZ, fakePlayer, new ItemStack(Blocks.torch));
			}
			cargo.consumeItem(Item.getItemFromBlock(Blocks.torch), -1, 1);
			return;
		}

		if (worldObj.getBlock(leftX, y, leftZ) == Blocks.air && torch.canPlaceBlockAt(worldObj, leftX, y, leftZ)) {
			PC_Utils.setBID(worldObj, leftX, y, leftZ, torch, 0);

			// set on floor if not building stairs.
			if (st.realCommand != PCmo_Command.UP) {
				Blocks.torch.onBlockPlacedBy(worldObj, leftX, y, leftZ, fakePlayer, new ItemStack(Blocks.torch));
			}
			cargo.consumeItem(Item.getItemFromBlock(Blocks.torch), -1, 1);
			return;
		}

		return;
	}

	/**
	 * fill bucket with lava
	 * 
	 * @return lava was removed (to bucket)
	 */
	private boolean lavaFillBucket() {
		for (int i = 0; i < cargo.getSizeInventory(); i++) {
			if (cargo.getStackInSlot(i) != null) {
				Item item = cargo.getStackInSlot(i).getItem();
				if (item == Items.bucket) {
					cargo.setInventorySlotContents(i, new ItemStack(Items.lava_bucket, 1, 0));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get coordinate of a block on given side.<br>
	 * Accepts: F,B,L,R,U,D; N,S,E,W; u,d; u and d are front-up and front-down, two
	 * blocks mined when doing UP or DOWN command.
	 * 
	 * @param side  side name
	 * @param index index - all 1-4, only u d 1-2
	 * @return coordinate of the block described.
	 */
	public PC_VecI getCoordOnSide(char side, int index) {

		// get x,y,z integers for position.
		int x = (int) Math.round(posX);
		int y = (int) Math.floor(posY + 0.02F);
		if (isOnHalfStep()) {
			y += 1;
		}
		int z = (int) Math.round(posZ);

		int yaw = getRotationRounded();

		// compass sides
		if (side == 'N') {
			yaw = 0;
			side = 'F';
		}
		if (side == 'S') {
			yaw = 0;
			side = 'B';
		}
		if (side == 'E') {
			yaw = 0;
			side = 'R';
		}
		if (side == 'W') {
			yaw = 0;
			side = 'L';
		}

		// derivates - left, right, back
		if (side == 'L') {
			yaw -= 90;
			side = 'F';
		}
		if (side == 'B') {
			yaw -= 180;
			side = 'F';
		}
		if (side == 'R') {
			yaw -= 270;
			side = 'F';
		}

		// normalize
		while (yaw < 0) {
			yaw += 360;
		}

		// ceil - upper up
		if (side == 'c') {
			switch (index) {
			case 1:
				return new PC_VecI(x, y + 2, z);
			case 2:
				return new PC_VecI(x - 1, y + 2, z);
			case 3:
				return new PC_VecI(x, y + 2, z - 1);
			case 4:
				return new PC_VecI(x - 1, y + 2, z - 1);
			}
		}
		if (side == 'U') {
			switch (index) {
			case 1:
				return new PC_VecI(x, y + 1, z);
			case 2:
				return new PC_VecI(x - 1, y + 1, z);
			case 3:
				return new PC_VecI(x, y + 1, z - 1);
			case 4:
				return new PC_VecI(x - 1, y + 1, z - 1);
			}
		}

		// DN - below miner
		if (side == 'D') {
			switch (index) {
			case 1:
				return new PC_VecI(x, y - 1, z);
			case 2:
				return new PC_VecI(x - 1, y - 1, z);
			case 3:
				return new PC_VecI(x, y - 1, z - 1);
			case 4:
				return new PC_VecI(x - 1, y - 1, z - 1);
			}
		}

		if (yaw == 180) {
			// F front
			if (side == 'F') {
				switch (index) {
				case 1:
					return new PC_VecI(x + 1, y + 1, z - 1);
				case 2:
					return new PC_VecI(x + 1, y + 1, z);
				case 3:
					return new PC_VecI(x + 1, y, z - 1);
				case 4:
					return new PC_VecI(x + 1, y, z);
				}
			}

			// d front down
			if (side == 'd') {
				switch (index) {
				case 1:
					return new PC_VecI(x + 1, y - 1, z - 1);
				case 2:
					return new PC_VecI(x + 1, y - 1, z);
				}
			}

			// u front up
			if (side == 'u') {
				switch (index) {
				case 1:
					return new PC_VecI(x + 1, y + 2, z - 1);
				case 2:
					return new PC_VecI(x + 1, y + 2, z);
				}
			}

			return null;
		}

		if (yaw == 270) {
			if (side == 'F') {
				switch (index) {
				case 1:
					return new PC_VecI(x, y + 1, z + 1);
				case 2:
					return new PC_VecI(x - 1, y + 1, z + 1);
				case 3:
					return new PC_VecI(x, y, z + 1);
				case 4:
					return new PC_VecI(x - 1, y, z + 1);
				}
			}

			if (side == 'd') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 1, y - 1, z + 1);
				case 2:
					return new PC_VecI(x, y - 1, z + 1);
				}
			}

			if (side == 'u') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 1, y + 2, z + 1);
				case 2:
					return new PC_VecI(x, y + 2, z + 1);
				}
			}

			return null;
		}

		if (yaw == 0) {
			if (side == 'F') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 2, y + 1, z);
				case 2:
					return new PC_VecI(x - 2, y + 1, z - 1);
				case 3:
					return new PC_VecI(x - 2, y, z);
				case 4:
					return new PC_VecI(x - 2, y, z - 1);
				}
			}

			if (side == 'd') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 2, y - 1, z - 1);
				case 2:
					return new PC_VecI(x - 2, y - 1, z);
				}
			}

			if (side == 'u') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 2, y + 2, z - 1);
				case 2:
					return new PC_VecI(x - 2, y + 2, z);
				}
			}

			return null;
		}

		if (yaw == 90) {
			if (side == 'F') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 1, y + 1, z - 2);
				case 2:
					return new PC_VecI(x, y + 1, z - 2);
				case 3:
					return new PC_VecI(x - 1, y, z - 2);
				case 4:
					return new PC_VecI(x, y, z - 2);
				}
			}

			if (side == 'd') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 1, y - 1, z - 2);
				case 2:
					return new PC_VecI(x, y - 1, z - 2);
				}
			}

			if (side == 'u') {
				switch (index) {
				case 1:
					return new PC_VecI(x - 1, y + 2, z - 2);
				case 2:
					return new PC_VecI(x, y + 2, z - 2);
				}
			}

			return null;
		}

		return null;
	}

	/**
	 * Send given command to all connected miners<br>
	 * Some miners may reject it.
	 * 
	 * @param cmd command index
	 * @return true if at least one miner accepted it
	 */
	public boolean sendCommandToMiners(int cmd) {
		boolean flag = false;

		if (canReceiveKeyboardCommand()) {
			flag = true;

			receiveKeyboardCommand(cmd);
			if (!worldObj.isRemote)
				PC_PacketHandler.sendToAll(new PCmo_PacketMinerClient(new NBTTagCompound(),
						new Object[] { 2, this.getEntityId(), "command", cmd }));

		}
		return flag;
	}

	/**
	 * Send command sequence to all connected miners
	 * 
	 * @param seq String with command characters
	 * @return true if at least one miner accepted it
	 */
	private boolean sendSequenceToMiners(String seq) {
		boolean flag = false;
		if (canReceiveKeyboardCommand()) {
			flag = true;
			try {
				appendCode(seq);
			} catch (Exception e) {
				PC_Logger.severe("Error in keyboard-sent command! This is a bug!");
				return false;
			}
		}
		return flag;
	}

	private void handleKeybordInput(EntityPlayer player) {
		if (player == null)
			return;
		if (player.openContainer != null)
			if (player.openContainer != player.inventoryContainer)
				return;

		for (int i = 0; i <= 8; i++) {
			if (keyPressTimer[i] > 0) {
				keyPressTimer[i]--;
			}
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mForward)) {
			if (keyPressTimer[0] == 0) {
				keyPressTimer[0] = CooldownTime;
				sendCommandToMiners(PCmo_Command.FORWARD);
			}
			return;
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mLeft)) {
			if (keyPressTimer[1] == 0) {
				keyPressTimer[1] = CooldownTime;
				sendCommandToMiners(PCmo_Command.LEFT);
			}
			return;
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mRight)) {
			if (keyPressTimer[2] == 0) {
				keyPressTimer[2] = CooldownTime;
				sendCommandToMiners(PCmo_Command.RIGHT);
			}
			return;
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mAround)) {
			if (keyPressTimer[3] == 0) {
				keyPressTimer[3] = CooldownTime;
				sendSequenceToMiners("RR");
			}
			return;
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mBackward)) {
			if (keyPressTimer[4] == 0) {
				keyPressTimer[4] = CooldownTime;
				sendCommandToMiners(PCmo_Command.BACKWARD);
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mDown)) {
			if (keyPressTimer[5] == 0) {
				keyPressTimer[5] = CooldownTime;
				sendCommandToMiners(PCmo_Command.DOWN);
			}
			return;
		}
		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mUp)) {
			if (keyPressTimer[6] == 0) {
				keyPressTimer[6] = CooldownTime;
				sendCommandToMiners(PCmo_Command.UP);
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mBridgeOn)) {
			if (!getFlag(bridgeEnabled) && sendCommandToMiners(PCmo_Command.BRIDGE_ENABLE)) {
				PC_Utils.chatMsg(PC_Lang.tr("pc.miner.bridgeOn"));
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mBridgeOff)) {
			if (getFlag(bridgeEnabled) && sendCommandToMiners(PCmo_Command.BRIDGE_DISABLE)) {
				PC_Utils.chatMsg(PC_Lang.tr("pc.miner.bridgeOff"));
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mDeposit)) {
			if (keyPressTimer[7] == 0) {
				keyPressTimer[7] = CooldownTime;
				sendCommandToMiners(PCmo_Command.DEPOSIT);
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mRun)) {
			if (keyPressTimer[8] == 0) {
				keyPressTimer[8] = CooldownTime;
				if (sendCommandToMiners(PCmo_Command.RUN_PROGRAM)) {
					PC_Utils.chatMsg(PC_Lang.tr("pc.miner.launchedAll"));
				}
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mToBlocks)) {
			sendCommandToMiners(PCmo_Command.DISASSEMBLY);
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mMiningOn)) {
			if (!getFlag(miningEnabled) && sendCommandToMiners(PCmo_Command.MINING_ENABLE)) {
				PC_Utils.chatMsg(PC_Lang.tr("pc.miner.miningOn"));
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mMiningOff)) {
			if (getFlag(miningEnabled) && sendCommandToMiners(PCmo_Command.MINING_DISABLE)) {
				PC_Utils.chatMsg(PC_Lang.tr("pc.miner.miningOff"));
			}
			return;
		}

		if (PC_KeyRegistry.isKeyPressed(player, PCmo_App.pk_mCancel)) {
			if (sendCommandToMiners(PCmo_Command.RESET)) {
				PC_Utils.chatMsg(PC_Lang.tr("pc.miner.operationsCancelled"));
			}
			return;
		}
	}

	// === UPDATE TICK ===

	@Override
	public void onUpdate() {
		super.onUpdate();

		handleKeybordInput(null);
		if (fakePlayer == null && worldObj != null)
			fakePlayer = new PC_FakePlayer(worldObj);
		if (!worldObj.isRemote) {
			Object o = getInfo("error");
			boolean error = false;
			if (o instanceof Boolean && (Boolean) o)
				error = true;
		}
		if (worldObj.isRemote && getInfo("error") != null && rand.nextInt(6) == 0) {
			worldObj.spawnParticle("largesmoke", posX, posY + 1F, posZ, 0, 0, 0);
		}

		// breaking animations.
		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0) {
			setDamageTaken(getDamageTaken() - 1);
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (playerConectedID != null && !worldObj.isRemote) {
			EntityPlayer e = PC_Utils.mcs().getConfigurationManager().func_152612_a(playerConectedID);
			if (e != null) {
				handleKeybordInput(e);
				playerTimeout = 0;
			} else {
				if (playerTimeout > 40) {
					playerConectedID = null;
				} else {
					playerTimeout++;
				}
			}
		}

		// EXECUTE CURRENT COMMAND

		boolean stop = false; // st.programmingGuiOpen;

		if (!stop) {
			if (st.fuelDeficit > 0) {
				if (st.addFuelForCost(st.fuelDeficit)) {
					st.fuelDeficit = 0;
					prepareForCommandExecution();
				}
			}

			st.releaseAllocatedFuelIfNoLongerNeeded();

			// normalize fuel deficit
			if (st.fuelDeficit < 0) {
				st.fuelDeficit = 0;
			}

			// if stopped and fuel deficit stays > 0
			if (st.currentCommand == -1 && st.fuelDeficit != 0) {
				st.fuelDeficit = 0;
			}

			// if there is enough fuel for current operation
			if (st.fuelDeficit == 0) {

				// execute rotation and check if target angle is reached.
				if (PCmo_Command.isCommandTurn(st.currentCommand)) {
					motionX = motionZ = 0;
					posX = st.target.x;
					posZ = st.target.z;

					if (Math.abs(st.rotationRemaining) < 3) {
						st.currentCommand = -1;
						posX = st.target.x;
						posZ = st.target.z;
						st.rotationRemaining = 0;
						roundRotation();
					} else {
						playMotionEffect();

						int step = MathHelper.clamp_int(st.level, 3, 7);
						step = MathHelper.clamp_int(step, 0, Math.abs(st.rotationRemaining));

						int incr = st.rotationRemaining > 0 ? step : -step;
						rotationYaw = rotationYaw + incr;
						if (rotationYaw < 0) {
							rotationYaw = prevRotationYaw = 360F + rotationYaw;
						}
						if (rotationYaw > 360F) {
							rotationYaw = prevRotationYaw = rotationYaw - 360F;
						}

						st.rotationRemaining -= incr;

						List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class,
								boundingBox.expand(-0.1, -0.5, -0.1).getOffsetBoundingBox(0.0D, 1D, 0.0D));
						for (Entity e : list) {
							if (e != this) {
								PC_VecF rel = new PC_VecF((float) e.posX, (float) (e.posY), (float) e.posZ)
										.sub((float) posX, (float) posY, (float) posZ);
								double rot = Math.atan2(rel.z, rel.x);
								double dis = rel.distanceTo(0.0f, rel.y, 0.0f);
								rot += Math.toRadians(incr);
								rel.x = (float) (Math.cos(rot) * dis);
								rel.z = (float) (Math.sin(rot) * dis);
								e.setLocationAndAngles(rel.x + posX, rel.y + posY - e.yOffset + 0.00001, rel.z + posZ,
										e.rotationYaw + incr, e.rotationPitch);
							}
						}

					}
					PC_Serializer s = new PC_Serializer();
					NBTTagCompound tag = st.writeToNBT(new NBTTagCompound());
					byte[] b = null;
					try {
						b = CompressedStreamTools.compress(tag);
					} catch (IOException e) {
						e.printStackTrace();
					}
					NBTTagCompound tag2 = new NBTTagCompound();
					writeEntityToNBT(tag2);
					byte[] b2 = null;
					try {
						b2 = CompressedStreamTools.compress(tag2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					PC_PacketHandler.sendToServer(new PCmo_PacketMinerServer(new Object[] { 3, getEntityId(), "set",
							posX, posY, posZ, motionX, motionY, motionZ, rotationYaw, b, b2 }));
				}

				if (st.currentCommand != -1) {
					burriedFix(false);
				}

				// check if movement destination is reached
				if (PCmo_Command.isCommandMove(st.currentCommand)
						|| (st.currentCommand == PCmo_Command.UP && isMiningDone())) {

					roundRotation();
					performTorchPlacing();

					// if target is reached
					if (isMinerAtTargetPos()) {

						// consume step cost from buffer
						st.consumeAllocatedFuel(getStepCost());

						// fill nearby liquids
						fillWaterLavaAir();

						// normalize position
						if (getTargetDistanceX() > 0.03125D) {
							posX = prevPosX = st.target.x;
						}

						if (getTargetDistanceZ() > 0.03125D) {
							posZ = prevPosZ = st.target.z;
						}

						// decrement step counter - used for commands like 100
						st.stepCounter--;
						if (st.stepCounter <= 0) {
							// out of code - will ask weasel next turn.
							st.currentCommand = -1;
							if (st.commandList.length() == 0) {
								// if no more commands, stop.
								motionX = 0;
								motionZ = 0;
							}
							// normalize step counter
							st.stepCounter = 0;
						} else {
							// prepare next target position.
							prepareForCommandExecution();
						}
					}
				}

				// perform movement and optional mining forwards
				// previous command may have set waitingForFuel to step cost. TODO: client part
				// move start
				if (PCmo_Command.isCommandMove(st.currentCommand) || st.currentCommand == PCmo_Command.DOWN
						|| st.currentCommand == PCmo_Command.UP) {
					// round rotation to world sides.
					roundRotation();

					boolean fw = (st.currentCommand == PCmo_Command.FORWARD);
					boolean dwn = (st.currentCommand == PCmo_Command.DOWN);
					boolean up = (st.currentCommand == PCmo_Command.UP);
					boolean back = (st.currentCommand == PCmo_Command.BACKWARD);

					// for checks
					int x = (int) Math.round(posX);
					int y = (int) Math.floor(posY + 0.0002F);
					if (isOnHalfStep()) {
						y += 1;
					}
					int z = (int) Math.round(posZ);

					boolean bridgeOk = true;
					if (!st.bridgeDone) {
						bridgeOk = performBridgeBuilding();
						if (!bridgeOk) {
							// bridge building failed!

						} else {
							st.bridgeDone = true;
						}
					}

					// if it cant move, stop.
					if (isMiningInProgress() || !bridgeOk) {
						motionX = motionZ = 0;
					}

					boolean miningDone = isMiningDone();
					double motionAdd = (MOTION_SPEED[st.level - 1] * ((fw || up) ? 1 : -1)) * 0.5D;
					boolean canMove = bridgeOk && !dwn && (!up || miningDone);
					if (up && !miningDone) {
						performMiningUpdate(getCoordOnSide('c', 1), 8);
						performMiningUpdate(getCoordOnSide('c', 2), 9);
						performMiningUpdate(getCoordOnSide('c', 3), 10);
						performMiningUpdate(getCoordOnSide('c', 4), 11);
					}

					if (!miningDone && (!back) && getFlag(miningEnabled)) {
						performMiningUpdate(getCoordOnSide('F', 1), 0);
						performMiningUpdate(getCoordOnSide('F', 2), 1);
						performMiningUpdate(getCoordOnSide('F', 3), 2);
						performMiningUpdate(getCoordOnSide('F', 4), 3);

						if (dwn) {
							performMiningUpdate(getCoordOnSide('d', 1), 4);
							performMiningUpdate(getCoordOnSide('d', 2), 5);
						}

						if (up) {
							performMiningUpdate(getCoordOnSide('u', 1), 6);
							performMiningUpdate(getCoordOnSide('u', 2), 7);
						}
					}

					if (rotationYaw == 180) {
						if (isLocationEmpty(st.target.setY(y)) && canMove) {
							motionX += motionAdd;
						}
						motionZ = 0;
					}

					if (rotationYaw == 270) {
						if (isLocationEmpty(st.target.setY(y)) && canMove) {
							motionZ += motionAdd;
						}
						motionX = 0;
					}

					if (rotationYaw == 0) {
						if (isLocationEmpty(st.target.setY(y)) && canMove) {
							motionX -= motionAdd;
						}
						motionZ = 0;
					}

					if (rotationYaw == 90) {
						if (isLocationEmpty(st.target.setY(y)) && canMove) {
							motionZ -= motionAdd;
						}
						motionX = 0;
					}

					if (dwn && !isMiningInProgress()) {
						st.currentCommand = -1;
					}

					if (up && isMiningDone() && !st.upStepLaid) {
						switch ((int) Math.floor(rotationYaw)) {
						case 0:
							layHalfStep(x - 2, y, z - 1, true);
							layHalfStep(x - 2, y, z, true);
							break;

						case 90:
							layHalfStep(x - 1, y, z - 2, true);
							layHalfStep(x, y, z - 2, true);
							break;

						case 180:
							layHalfStep(x + 1, y, z - 1, true);
							layHalfStep(x + 1, y, z, true);
							break;

						case 270:
							layHalfStep(x - 1, y, z + 1, true);
							layHalfStep(x, y, z + 1, true);
							break;
						}
						st.upStepLaid = true;
					}

					// stop if bumped into wall
					if ((!getFlag(miningEnabled) || !isMiningInProgress() || st.currentCommand == PCmo_Command.BACKWARD)
							&& !isLocationEmpty(st.target.setY(y))) {

						burriedFix(fw && getFlag(miningEnabled));

						if (!isLocationEmpty(st.target.setY(y))) {
							if (!getFlag(miningEnabled) || st.currentCommand == PCmo_Command.BACKWARD) {
								st.currentCommand = -1;
								resetMineCounter();
								st.consumeAllocatedFuel(getStepCost());
								st.target.x = (int) Math.round(posX);
								st.target.z = (int) Math.round(posZ);
								st.target.y = (int) Math.round(posY + 0.001F);

								st.stepCounter = 0;
							}
							motionX = motionZ = 0;
						}
					}
					PC_Serializer s = new PC_Serializer();
					NBTTagCompound tag = st.writeToNBT(new NBTTagCompound());
					byte[] b = null;
					try {
						b = CompressedStreamTools.compress(tag);
					} catch (IOException e) {
						e.printStackTrace();
					}
					NBTTagCompound tag2 = new NBTTagCompound();
					writeEntityToNBT(tag2);
					byte[] b2 = null;
					try {
						b2 = CompressedStreamTools.compress(tag2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					PC_PacketHandler.sendToServer(new PCmo_PacketMinerServer(new Object[] { 3, getEntityId(), "set",
							posX, posY, posZ, motionX, motionY, motionZ, rotationYaw, b, b2 }));
				} // TODO: Client part move end

			}

		}

		// FALL
		if (!onGround) {
			motionY -= 0.04D;
		}

		// speed limit.
		double d7 = MOTION_SPEED[st.level - 1];
		if (motionX < -d7) {
			motionX = -d7;
		}
		if (motionX > d7) {
			motionX = d7;
		}
		if (motionZ < -d7) {
			motionZ = -d7;
		}
		if (motionZ > d7) {
			motionZ = d7;
		}

		// GET NEW COMMAND FROM QUEUE
		if (!stop && st.currentCommand == -1) {

			int oldCmd = st.currentCommand;
			st.currentCommand = getNextCommand(); // gets command and removes it
													// from queue
			if (st.currentCommand != -1 && st.currentCommand != oldCmd) {
				alignToBlocks();
			}
			if (st.currentCommand == -1 && playerConectedID == null) {
				alignToBlocks();
			}

			roundRotation();
			prepareForCommandExecution();

			if (st.currentCommand != -1) {
				setSprinting(true);
			}
		}

		// slow down if no more commands are available (halt)
		if (st.currentCommand == -1 && st.commandList.length() == 0) {
			motionX = 0D;
			motionZ = 0D;
			setSprinting(false);
		}

		if (Math.abs(motionX) > 0.0001D || Math.abs(motionZ) > 0.0001D) {
			playMotionEffect();
		}

		if (!worldObj.isRemote) {

			// pick up items.
			List<EntityItem> list;

			list = worldObj.getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(1.5D, 0.5D, 1.5D));
			if (list != null && list.size() > 0) {
				for (int j1 = 0; j1 < list.size(); j1++) {
					EntityItem entity = list.get(j1);
					if (entity.delayBeforeCanPickup >= 6) {
						continue;
					}

					ItemStack itemStack = entity.getEntityItem().copy();

					Item item = itemStack.getItem();

					boolean xtal = item == Item
							.getItemFromBlock(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"));

					if (shouldDestroyStack(itemStack)) {
						entity.setDead();
						continue;
					}

					// if (xtal && PC_InventoryUtils.storeItemStackToInventoryFrom(xtals,
					// itemStack)) {//TODO:
					// entity.setDead();
					// } else if (PC_InventoryUtils.storeItemStackToInventoryFrom(cargo, itemStack))
					// {
					// entity.setDead();
					// }

					if (xtal) {
						updateLevel();
					}

					if (getFlag(compressBlocks)) {
						cargo.compressInv(itemStack);
					}
				}
			}

			// push items
			list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2D, 0.01D, 0.2D));
			if (list != null && list.size() > 0) {
				for (int j1 = 0; j1 < list.size(); j1++) {
					Entity entity = list.get(j1);
					if (PC_Utils.isEntityFX(entity) || entity instanceof EntityXPOrb) {
						continue;
					}
					if (entity.isDead) {
						continue;
					}

					if (entity instanceof EntityArrow) {
						// PC_InventoryUtils.storeItemStackToInventoryFrom(cargo, new
						// ItemStack(Items.arrow, 1, 0));
						entity.setDead();
						return;
					}

					// keep the same old velocity
					double motionX_prev = motionX;
					double motionY_prev = motionY;
					double motionZ_prev = motionZ;

					entity.applyEntityCollision(this);

					motionX = motionX_prev;
					motionY = motionY_prev;
					motionZ = motionZ_prev;
				}
			}

		}

		moveEntity(Math.min(motionX, getTargetDistanceX()), motionY, Math.min(motionZ, getTargetDistanceZ()));
		motionX *= 0.7D;
		motionZ *= 0.7D;

		if (!worldObj.isRemote) {
			if (tick % 20 == 0) {
				PC_Serializer s = new PC_Serializer();
				NBTTagCompound tag = st.writeToNBT(new NBTTagCompound());
				byte[] b = null;
				try {
					b = CompressedStreamTools.compress(tag);
				} catch (IOException e) {
					e.printStackTrace();
				}
				NBTTagCompound tag2 = new NBTTagCompound();
				writeEntityToNBT(tag2);
				byte[] b2 = null;
				try {
					b2 = CompressedStreamTools.compress(tag2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				PC_PacketHandler.sendToAll(new PCmo_PacketMinerClient(new NBTTagCompound(), new Object[] { 2,
						getEntityId(), "set", posX, posY, posZ, motionX, motionY, motionZ, rotationYaw, b, b2 }));
			}
		}
		tick++;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity.riddenByEntity == this || entity.ridingEntity == this) {
			return;
		}

		double d = entity.posX - posX;
		double d1 = entity.posZ - posZ;
		double d2 = MathHelper.abs_max(d, d1);
		if (d2 >= 0.001D) {
			d2 = MathHelper.sqrt_double(d2);
			d /= d2;
			d1 /= d2;
			double d3 = 1.0D / d2;
			if (d3 > 1.0D) {
				d3 = 1.0D;
			}
			d *= d3;
			d1 *= d3;
			d *= 0.05D;
			d1 *= 0.05D;
			d *= 1.0F - entityCollisionReduction;
			d1 *= 1.0F - entityCollisionReduction;
			isAirBorne = true;

			// this entity won't be moved!

			entity.addVelocity(d, 0.0D, d1);
		}
	}

	// NBT TAGs

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {

		if (playerConectedID != null)
			tag.setString("player", playerConectedID);

		tag.setBoolean(keepAllFuel, getFlag(keepAllFuel));
		tag.setBoolean(torchesOnlyOnFloor, getFlag(torchesOnlyOnFloor));
		tag.setBoolean(compressBlocks, getFlag(compressBlocks));
		tag.setBoolean(miningEnabled, getFlag(miningEnabled));
		tag.setBoolean(bridgeEnabled, getFlag(bridgeEnabled));
		tag.setBoolean(lavaFillingEnabled, getFlag(lavaFillingEnabled));
		tag.setBoolean(waterFillingEnabled, getFlag(waterFillingEnabled));
		tag.setBoolean(torches, getFlag(torches));
		tag.setBoolean(cobbleMake, getFlag(cobbleMake));
		tag.setBoolean(airFillingEnabled, getFlag(airFillingEnabled));
		PC_Utils.saveToNBT(tag, "Status", st);

		PC_InventoryUtils.saveInventoryToNBT(tag, "CargoInv", cargo);

		NBTTagCompound nbt = new NBTTagCompound();

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {

		if (tag.hasKey("player")) {
			playerConectedID = tag.getString("player");
		} else {
			playerConectedID = null;
		}
		EntityPlayer e = PC_Utils.mcs().getConfigurationManager().func_152612_a(playerConectedID);

		if (!this.inGui)
			PC_InventoryUtils.loadInventoryFromNBT(tag, "CargoInv", cargo);

		setFlag(keepAllFuel, tag.getBoolean(keepAllFuel));
		setFlag(torchesOnlyOnFloor, tag.getBoolean(torchesOnlyOnFloor));
		setFlag(compressBlocks, tag.getBoolean(compressBlocks));
		setFlag(miningEnabled, tag.getBoolean(miningEnabled));
		setFlag(bridgeEnabled, tag.getBoolean(bridgeEnabled));
		setFlag(lavaFillingEnabled, tag.getBoolean(lavaFillingEnabled));
		setFlag(waterFillingEnabled, tag.getBoolean(waterFillingEnabled));
		setFlag(torches, tag.getBoolean(torches));
		setFlag(cobbleMake, tag.getBoolean(cobbleMake));
		setFlag(airFillingEnabled, tag.getBoolean(airFillingEnabled));
		PC_Utils.loadFromNBT(tag, "Status", st);

		updateLevel();
	}

	// === PLAYER INTERACTION ===

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		if (riddenByEntity != null && (riddenByEntity instanceof EntityPlayer) && riddenByEntity != entityplayer) {
			return true;
		}

		// set for keyboard control or open gui.
		if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem()
				.getItem() == PC_ItemRegistry.getPCItemByName("PCco_ItemActivator")) {
			if (playerConectedID == null)
				playerConectedID = entityplayer.getDisplayName();
			else
				playerConectedID = null;
		} else {
			setInfo("isRunning", !st.paused);
			this.inGui = true;
			PC_GresRegistry.openGres("Miner", entityplayer, null, getEntityId());
			return true;
		}
		return true;
	}

	public boolean getInGui() {
		return this.inGui;
	}

	public void setInGui(boolean status) {
		this.inGui = status;
	}

	// === WATCHER ===

	@SuppressWarnings("javadoc")
	public void setDamageTaken(float f) {
		dataWatcher.updateObject(19, Float.valueOf(f));
	}

	@SuppressWarnings("javadoc")
	public float getDamageTaken() {
		return dataWatcher.getWatchableObjectFloat(19);
	}

	@SuppressWarnings("javadoc")
	public void setTimeSinceHit(int i) {
		dataWatcher.updateObject(17, Integer.valueOf(i));
	}

	@SuppressWarnings("javadoc")
	public int getTimeSinceHit() {
		return dataWatcher.getWatchableObjectInt(17);
	}

	@SuppressWarnings("javadoc")
	public void setForwardDirection(int i) {
		dataWatcher.updateObject(18, Integer.valueOf(i));
	}

	@SuppressWarnings("javadoc")
	public int getForwardDirection() {
		return dataWatcher.getWatchableObjectInt(18);
	}

	// SPAWNING AND DESPAWNING

	/**
	 * count crystals and update level; turn to blocks if there arent any.
	 */
	public void updateLevel() {

		int cnt = PCmo_MinerManager.countPowerCrystals(cargo);
		if (cnt == 0) {
			turnIntoBlocks();
			return;
		}

		if (!worldObj.isRemote) {
			int level = Math.min(cnt, 8);
			if (level != st.level) {
				st.level = level;
				PC_PacketHandler.sendToAll(new PCmo_PacketMinerClient(new NBTTagCompound(),
						new Object[] { 2, getEntityId(), "setLevel", st.level }));
			}
		}

		setFlag(bridgeEnabled, getFlag(bridgeEnabled) & (st.level >= LBRIDGE));
		setFlag(waterFillingEnabled, getFlag(waterFillingEnabled) & (st.level >= LWATER));
		setFlag(lavaFillingEnabled, getFlag(lavaFillingEnabled) & (st.level >= LLAVA));
		setFlag(airFillingEnabled, getFlag(airFillingEnabled) & (st.level >= LAIR));
		setFlag(cobbleMake, getFlag(cobbleMake) & (st.level >= LCOBBLE));
		setFlag(compressBlocks, getFlag(compressBlocks) & (st.level >= LCOMPRESS));
		setFlag(torches, getFlag(torches) & (st.level >= LTORCH));
	}

	/**
	 * Despawn the miner, recreate build structure at it's position; Called when
	 * miner is killed or "to blocks" key is pressed
	 */
	public void turnIntoBlocks() {
		if (worldObj.isRemote)
			return;
		int xh = (int) Math.round(posX);
		int y = (int) Math.floor(posY + 0.0001F);
		int zh = (int) Math.round(posZ);
		int yaw = (rotationYaw < 45 || rotationYaw > 315) ? 0
				: (rotationYaw < 135 ? 1 : (rotationYaw < 215 ? 2 : (rotationYaw < 315 ? 3 : 0)));

		int xl = xh - 1, zl = zh - 1;

		// building chests
		for (int x = xl; x <= xh; x++) {
			for (int z = zl; z <= zh; z++) {
				PC_Utils.setBID(worldObj, x, y, z, Blocks.iron_block, 0);
				if ((yaw == 0 && x == xh) || (yaw == 1 && z == zh) || (yaw == 2 && x == xl) || (yaw == 3 && z == zl)) {
					PC_Utils.setBID(worldObj, x, y + 1, z, Blocks.chest, 0);
				} else {
					PC_Utils.setBID(worldObj, x, y + 1, z, Blocks.iron_block, 0);
				}
			}
		}

		IInventory inv = null;

		for (int x = xl; x <= xh && inv == null; x++) {
			for (int k = zl; k <= zh && inv == null; k++) {
				inv = PC_InventoryUtils.getBlockInventoryAt(worldObj, new PC_VecI(x, y + 1, k));
			}
		}

		if (inv != null) {
			moveEnabled = false;
			PC_InventoryUtils.moveStacks(cargo, inv);
			PC_InventoryUtils.dropInventoryContents(cargo, worldObj,
					new PC_VecI((int) Math.round(posX), (int) Math.round(posY + 2.2F), (int) Math.round(posZ)));
		} else {
			PC_Logger.warning("Despawning miner - the chest blocks weren't found.");
		}

		setDead();

	}

	@Override
	public IInventory getInventory() {
		return cargo;
	}

	@Override
	public void moveEntity(double par1, double par3, double par5) {
		super.moveEntity(par1, par3, par5);
		List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class,
				boundingBox.expand(-0.1, -0.5, -0.1).getOffsetBoundingBox(0.0D, 1D, 0.0D));
		for (Entity e : list) {
			if (e != this) {
				e.moveEntity(par1, par3, par5);
			}
		}
	}

	public void setInfo(String key, Object obj) {
		info.put(key, obj);
		if (!worldObj.isRemote)
			PC_PacketHandler.sendToAll(new PCmo_PacketMinerClient(new NBTTagCompound(),
					new Object[] { 3, this.getEntityId(), "setInfo", key, obj }));
	}

	public Object getInfo(String key) {
		return info.get(key);
	}

	public void doInfoSet(String key, Object... obj) {
		setFlag((String) obj[0], (Boolean) obj[1]);
		if (worldObj.isRemote) {
			PC_PacketHandler
					.sendToServer(new PCmo_PacketMinerServer(new Object[] { 3, getEntityId(), "doInfoSet", key, obj }));
		} else {
			if (key.equalsIgnoreCase("set")) {
				setFlag((String) obj[0], (Boolean) obj[1]);
				setInfo((String) obj[0], obj[1]);
			}
		}
	}

	/**
	 * Check if block is good for building.
	 * 
	 * @param is   stack
	 * @param pass pass; 0 = cheap, 1 = better
	 * @return is good
	 */
	public static boolean isBlockGoodForBuilding(ItemStack is, int pass) {
		if (is == null) {
			return false;
		}

		if (!(is.getItem() instanceof ItemBlock)) {
			return false;
		}

		Item item = is.getItem();

		if (item == Item.getItemFromBlock(Blocks.stone_slab) || item == Item.getItemFromBlock(Blocks.soul_sand))
			return false;

		if (PC_MSGRegistry.hasFlag(is, "NO_BUILD")) {
			return false;
		}

		if (pass >= 0) {
			if (item == Item.getItemFromBlock(Blocks.dirt) || item == Item.getItemFromBlock(Blocks.grass)
					|| item == Item.getItemFromBlock(Blocks.cobblestone)
					|| item == Item.getItemFromBlock(Blocks.netherrack))
				return true;
		}

		if (pass >= 1) {
			if (item == Item.getItemFromBlock(Blocks.planks) || item == Item.getItemFromBlock(Blocks.stone)
					|| item == Item.getItemFromBlock(Blocks.sandstone)
					|| item == Item.getItemFromBlock(Blocks.brick_block)
					|| item == Item.getItemFromBlock(Blocks.stonebrick)
					|| item == Item.getItemFromBlock(Blocks.nether_brick)
					|| item == Item.getItemFromBlock(Blocks.end_stone) || item == Item.getItemFromBlock(Blocks.wool)
					|| item == Item.getItemFromBlock(Blocks.glass) || item == Item.getItemFromBlock(Blocks.log))
				return true;
		}

		if (pass >= 2) {
			if (item == Item.getItemFromBlock(Blocks.iron_ore) || item == Item.getItemFromBlock(Blocks.clay))
				return true;
		}

		if (pass >= 3) {
			if (item == Item.getItemFromBlock(Blocks.sand) || item == Item.getItemFromBlock(Blocks.gravel))
				return false;
			if (Block.getBlockFromItem(item).isOpaqueCube() || Block.getBlockFromItem(item).renderAsNormalBlock())
				return true;
		}

		if (pass >= 4) {
			if (Block.getBlockFromItem(item).getMaterial().isSolid())
				return true;
		}
		return false;
	}

	public boolean hasPlayer() {
		return playerConectedID != null;
	}

}
