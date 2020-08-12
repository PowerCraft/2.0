package powercraft.machines.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_BeamTracer;
import powercraft.api.annotation.PC_BlockFlag;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.building.PC_BuildingManager;
import powercraft.api.interfaces.PC_IBeamHandler;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncTEClient;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

@PC_BlockFlag(flags = { PC_MSGRegistry.HARVEST_STOP, PC_MSGRegistry.NO_HARVEST })
@PC_BlockInfo(name = "Harvester", canPlacedRotated = true)
public class PCma_BlockHarvester extends PC_Block implements PC_IBeamHandler, PC_IItemInfo, PC_IPacketHandler {

	private static final int TXSIDE = 0, TXFRONT = 1, TXBACK = 2;
	/**
	 * Block which ends the harvesting. 98 = stone brick. Obsidian + bedrock stop
	 * too.
	 */
	public static final Block ENDBLOCK = Blocks.stonebrick;

	/** Stacks harvested during this flash (including stacks from animals etc.) */
	private ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

	public PCma_BlockHarvester(int id) {
		super(Material.ground, "side", "side", "harvester_back", "harvester_front", "side", "side");
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean isBlockSolid(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return true;
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		if (isIndirectlyPowered(world, i, j, k)) {
			harvestBlocks(world, i, j, k, l);

			l |= 8;
		}
		PC_Utils.setMD(world, i, j, k, l);
	}

	private boolean isIndirectlyPowered(World world, int i, int j, int k) {
		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			return true;
		}
		if (world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			return true;
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block l) {
		boolean flag = isIndirectlyPowered(world, i, j, k);
		if (flag) {
			world.scheduleBlockUpdate(i, j, k, this, tickRate(world));
			harvestBlocks(world, i, j, k, world.getBlockMetadata(i, j, k));
		}
	}

	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param deviceMeta
	 */
	private void harvestBlocks(World world, int x, int y, int z, int deviceMeta) {

		if (!world.isRemote)
			PC_PacketHandler.sendToAll(new PC_PacketSyncTEClient(new Object[] { 0, new PC_VecI(x, y, z), deviceMeta }));

		deviceMeta &= 0x7;

		PC_VecI cnt = new PC_VecI(x, y, z);
		PC_BeamTracer beamTracer = new PC_BeamTracer(world, this);

		beamTracer.setStartCoord(cnt);
		beamTracer.setStartMove(getRotation(deviceMeta).getOffset());
		beamTracer.setCanChangeColor(false);
		beamTracer.setDetectEntities(true);
		beamTracer.setTotalLengthLimit(8000);
		beamTracer.setMaxLengthAfterCrystal(2000);
		beamTracer.setStartLength(30);
		beamTracer.setData("crystalAdd", 100);
		beamTracer.setColor(new PC_Color(0.001f, 1.0f, 0.001f));

		if (world.getBlock(x, y - 1, z) == ENDBLOCK) {
			beamTracer.setStartLength(1);
			beamTracer.setMaxLengthAfterCrystal(1);
		}

		if (!world.isRemote)
			drops.clear();

		beamTracer.flash();

		if (drops != null && !world.isRemote) {
			PC_InventoryUtils.groupStacks(drops);

			for (ItemStack stack : drops) {
				dispenseItem(world, cnt, stack);
			}
		}
	}

	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block1, PC_VecI coord) {
		World world = beamTracer.getWorld();
		Block block = PC_Utils.getBID(world, coord);
		int meta = PC_Utils.getMD(world, coord);

		if (block == Blocks.obsidian || block == Blocks.bedrock || block == ENDBLOCK) {
			return true;
		}

		if (PC_MSGRegistry.hasFlag(world, coord, PC_MSGRegistry.HARVEST_STOP)) {
			return true;
		}

		// SKIP non-breaking
		if (block == Blocks.air || block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.redstone_lamp
				|| block == Blocks.redstone_lamp || block == Blocks.water || block == Blocks.flowing_water
				|| block == Blocks.lava || block == Blocks.flowing_lava || block == Blocks.sapling
				|| block == Blocks.pumpkin || block == Blocks.melon_stem || block == Blocks.cake || block == Blocks.fire
				|| block instanceof BlockTorch || block == Blocks.redstone_wire || block == Blocks.mob_spawner
				|| block == Blocks.lever || block == Blocks.wooden_button || block == Blocks.stone_button
				|| block instanceof BlockRedstoneRepeater || block == Blocks.piston_head || block == Blocks.piston
				|| block == Blocks.piston_extension || block == Blocks.sticky_piston || block instanceof BlockRail) {

			return false;
		}

		List<PC_Struct2<PC_VecI, ItemStack>> blockDrops = PC_BuildingManager.harvest(world, coord, 0);

		if (blockDrops != null) {

			if (!world.isRemote) {
				for (PC_Struct2<PC_VecI, ItemStack> blockDrop : blockDrops) {
					drops.add(blockDrop.b);
				}
			}

			return true;

		}

		return false;
	}

	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord) {

		World world = beamTracer.getWorld();

		if (entity == null)
			return false;

		if (entity instanceof EntityMinecart) {

			if (world.isRemote)
				return true;

			EntityMinecart cart = (EntityMinecart) entity;

			if (cart.isDead) {
				return false;
			}

			int l = PC_Utils.getMD(world, coord.x, coord.y, coord.z) & 7;

			int iPLUS1 = -Facing.offsetsXForSide[l];
			int kPLUS1 = -Facing.offsetsZForSide[l];

			PC_VecI startCoord = beamTracer.getStartCoord();

			cart.posX = startCoord.x + iPLUS1 * 1.5D;
			cart.posY = startCoord.y;
			cart.posZ = startCoord.z + kPLUS1 * 1.5D;
			if (cart instanceof EntityMinecartContainer) {
				EntityMinecartContainer container = (EntityMinecartContainer) cart;
				for (int i = 0; i < container.getSizeInventory(); i++) {
					addToDispenseList(world, container.getStackInSlot(i));
					container.setInventorySlotContents(i, null);
				}
			}
			addToDispenseList(world, new ItemStack(Items.minecart));
			if (cart instanceof EntityMinecartChest) {
				addToDispenseList(world, new ItemStack(Blocks.chest));
			} else if (cart instanceof EntityMinecartFurnace) {
				addToDispenseList(world, new ItemStack(Blocks.furnace));
			}
			cart.setDead();

		} else if (entity instanceof EntitySheep) {

			if (world.isRemote)
				return true;

			EntitySheep sheep = (EntitySheep) entity;

			if (sheep.isDead) {
				return false;
			}

			if (!sheep.getSheared()) {
				sheep.setSheared(true);
				addToDispenseList(world, new ItemStack(Blocks.wool, 1 + world.rand.nextInt(3), sheep.getFleeceColor()));
			}

		} else if (entity instanceof EntityMooshroom) {

			if (world.isRemote)
				return true;

			EntityMooshroom mooshroom = (EntityMooshroom) entity;

			if (mooshroom.isDead) {
				return false;
			}

			if (mooshroom.getGrowingAge() >= 0) {

				if (!world.isRemote) {
					EntityCow entitycow = new EntityCow(world);
					entitycow.setLocationAndAngles(mooshroom.posX, mooshroom.posY, mooshroom.posZ,
							mooshroom.rotationYaw, mooshroom.rotationPitch);
					entitycow.setHealth(mooshroom.getHealth());
					entitycow.renderYawOffset = mooshroom.renderYawOffset;
					world.spawnEntityInWorld(entitycow);
					addToDispenseList(world, new ItemStack(Blocks.red_mushroom, 1 + world.rand.nextInt(5), 0));
				}
				mooshroom.setDead();
				mooshroom.deathTime = 0;

				if (world.isRemote) {
					world.spawnParticle("largeexplode", mooshroom.posX, mooshroom.posY + (mooshroom.height / 2.0F),
							mooshroom.posZ, 0.0D, 0.0D, 0.0D);
				}

			}

		} else {
			return false;
		}

		return true;
	}

	private void addToDispenseList(World world, ItemStack stack) {
		if (!world.isRemote && stack != null && stack.getItem() != null && stack.stackSize != 0)
			drops.add(stack);
	}

	private void dispenseItem(World world, PC_VecI devPos, ItemStack itemstack) {

		if (itemstack == null || itemstack.stackSize <= 0) {
			return;
		}

		PC_VecI offset = getRotation(PC_Utils.getMD(world, devPos)).getOffset().mul(-1);

		double dx = devPos.x + offset.x * 1.0D + 0.5D;
		double dy = devPos.y + 0.5D;
		double dz = devPos.z + offset.z * 1.0D + 0.5D;

		EntityItem entityitem = new EntityItem(world, dx, dy - 0.29999999999999999D, dz, itemstack);
		double throwSpeed = world.rand.nextDouble() * 0.10000000000000001D + 0.20000000000000001D;

		Block b = PC_Utils.getBlock(world, devPos.offset(offset.x, 0, offset.z));
		String module = null;
		if (b instanceof PC_Block) {
			module = ((PC_Block) b).getModule().getModuleName();
		}

		if (module != null && module.equalsIgnoreCase("Transport")) {
			entityitem.motionX = 0;
			entityitem.motionY = 0;
			entityitem.motionZ = 0;
		} else {
			entityitem.motionX = offset.x * throwSpeed;
			entityitem.motionY = 0.20000000298023224D;
			entityitem.motionZ = offset.z * throwSpeed;
		}

		entityitem.delayBeforeCanPickup = 5;
		world.spawnEntityInWorld(entityitem);
		if (PC_SoundRegistry.isSoundEnabled()) {
			world.playAuxSFX(1000, devPos.x, devPos.y, devPos.z, 0);
		}

		world.playAuxSFX(2000, devPos.x, devPos.y, devPos.z, offset.x + 1 + (offset.z + 1) * 3);

	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		PC_VecI bc = (PC_VecI) o[1];
		if (player.worldObj.isRemote)
			harvestBlocks(player.worldObj, bc.x, bc.y, bc.z, (Integer) o[2]);
		return false;
	}

}
