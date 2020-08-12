package powercraft.machines.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockFlag;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.entity.PC_FakePlayer;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.tile.PCma_TileEntityReplacer;

@PC_BlockFlag(flags = { PC_MSGRegistry.HARVEST_STOP, PC_MSGRegistry.NO_HARVEST })
@PC_BlockInfo(name = "Replacer", tileEntity = PCma_TileEntityReplacer.class)
public class PCma_BlockReplacer extends PC_Block implements PC_IItemInfo {
	private static final int TXTOP = 1, TXSIDE = 0; // These are the sides and their IDs in the texture file

	public PCma_BlockReplacer(int id) {
		super(Material.ground, "replacer_side", "replacer_top", "replacer_side");
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		PCma_TileEntityReplacer tileentity = (PCma_TileEntityReplacer) world.getTileEntity(i, j, k);

		if (tileentity != null) {
			tileentity.setAidEnabled(!tileentity.isAidEnabled());
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		ItemStack ihold = entityplayer.getCurrentEquippedItem(); // This is the Item which the player has currently
		// equipped

		PCma_TileEntityReplacer tileentity = PC_Utils.<PCma_TileEntityReplacer>getTE(world, i, j, k); // This brings us
		// the
		// TileEntity of
		// the current
		// Block

		if (ihold != null) {
			if (ihold.getItem() == PC_ItemRegistry.getPCItemByName("PCco_ItemActivator")) {
				int l = MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3;

				if (PC_KeyRegistry.isPlacingReversed(entityplayer)) {
					l = (((l + 4) % 4) + 2) % 4;
				}

				if (entityplayer.isSneaking()) {
					l = PC_KeyRegistry.isPlacingReversed(entityplayer) ? 5 : 4;
				}

				if (tileentity != null) {
					PC_VecI coordOffset = tileentity.getCoordOffset();
					switch (l) {
					case 0:
						coordOffset.z++;
						break;

					case 2:
						coordOffset.z--;
						break;

					case 3:
						coordOffset.x++;
						break;

					case 1:
						coordOffset.x--;
						break;

					case 4:
						coordOffset.y++;
						break;

					case 5:
						coordOffset.y--;
						break;
					}

					coordOffset.x = MathHelper.clamp_int(coordOffset.x, -16, 16);
					coordOffset.y = MathHelper.clamp_int(coordOffset.y, -16, 16);
					coordOffset.z = MathHelper.clamp_int(coordOffset.z, -16, 16);
					tileentity.setCoordOffset(coordOffset);
				}

				return true;
			}
		}

		if (world.isRemote) // this says whether we're on server or not if isRemote==true then we're on
		// client
		{
			return true; // on the server there can't be a gui that's why we interrupt it before the gui
			// get's opened
		}

		PC_GresRegistry.openGres("Replacer", entityplayer, tileentity); // This opens the Gui
		return true; // If we return true, then this click is "counted" else it's like we've ignored
		// it
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block l) {
		world.scheduleBlockUpdate(i, j, k, this, tickRate(world));
		PCma_TileEntityReplacer ter = (PCma_TileEntityReplacer) world.getTileEntity(i, j, k);

		if (ter != null && !world.isRemote) {
			boolean powered = isIndirectlyPowered(world, i, j, k);

			if (!powered) {// I DON'T KNOW HOW IT WORK! BUT IT WORK! DON'T TOUCH IT!!
				if (!replacer_canPlaceBlockAt(ter.getWorldObj(), ter.getStackInSlot(0), ter.getCoord())) {
					replacer_placeBlockAt(ter.getWorldObj(), ter.extraMeta, ter.getStackInSlot(0),
							ter.getCoordOffset());
					ter.setInventorySlotContents(0, null);
					ter.extraMeta = -1;
					ter.syncInventory(1, Minecraft.getMinecraft().thePlayer, 0);
				}
			} else {
				if (!replacer_canHarvestBlockAt(ter.getWorldObj(), ter.getCoord())) {
					PC_Struct2<ItemStack, Integer> harvested = replacer_harvestBlockAt(ter.getWorldObj(),
							ter.getCoordOffset());
					ter.setInventorySlotContents(0, harvested.a);
					ter.extraMeta = harvested.b;
					ter.syncInventory(1, Minecraft.getMinecraft().thePlayer, 0);
				}
			} // END
		}
	}

	private boolean replacer_canHarvestBlockAt(World world, PC_VecI pos) {
		Block block = PC_Utils.getBlock(world, pos);

		if (block == Blocks.air || block == null) {
			return true;
		}

		/**
		 * TODO if (!PC_MSGRegistry.hasFlag(world, pos, PC_Utils.NO_HARVEST)) { return
		 * false; }
		 */

		if (block == Blocks.bedrock && pos.y == 0) {
			return false;
		}

		return true;
	}

	private boolean replacer_canPlaceBlockAt(World world, ItemStack itemstack, PC_VecI pos) {
		if (itemstack == null) {
			return true;
		}

		Item item = itemstack.getItem();

		if (item == Item.getItemFromBlock(Blocks.trapped_chest)) {
			return PC_Utils.getTE(world, pos) == null;
		}

		if (item instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(item);

			if (block == null) {
				return false;
			}

			/**
			 * TODO if (PC_MSGRegistry.hasFlag(itemstack, PC_Utils.NO_BUILD)) { return
			 * false; }
			 */

			if (block.hasTileEntity()) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean replacer_placeBlockAt(World world, int meta, ItemStack itemstack, PC_VecI pos) {
		if (itemstack == null) {
			PC_Utils.setBID(world, pos, Blocks.air, 0);
			return true;
		}

		if (itemstack.getItem() == Item.getItemFromBlock(Blocks.trapped_chest)) {
			PC_Utils.setBID(world, pos, Blocks.air, 0);
			world.removeTileEntity(pos.x, pos.y, pos.z);

			if (!Item.getItemFromBlock(Blocks.trapped_chest).onItemUse(itemstack, new PC_FakePlayer(world), world,
					pos.x, pos.y + 1, pos.z, 0, 0.0f, 0.0f, 0.0f)) {
				return false;
			}

			itemstack.stackSize--;

			if (meta != -1) {
				PC_Utils.setMD(world, pos, meta);
			}

			return true;
		}

		if (!replacer_canPlaceBlockAt(world, itemstack, pos)) {
			return false;
		}

		ItemBlock iblock = (ItemBlock) itemstack.getItem();

		if (iblock == Item.getItemFromBlock(Blocks.water)) {
			iblock = (ItemBlock) Item.getItemFromBlock(Blocks.flowing_water);
		}

		if (iblock == Item.getItemFromBlock(Blocks.lava)) {
			iblock = (ItemBlock) Item.getItemFromBlock(Blocks.flowing_water);
		}

		if (PC_Utils.setBID(world, pos, Block.getBlockFromItem(iblock),
				iblock.getMetadata(itemstack.getItemDamage()))) {
			if (PC_Utils.getBID(world, pos) == Block.getBlockFromItem(iblock)) {
				world.notifyBlockChange(pos.x, pos.y, pos.z, Block.getBlockFromItem(iblock));
			}

			if (meta != -1 && !iblock.getHasSubtypes()) {
				PC_Utils.setMD(world, pos, meta);
			}

			itemstack.stackSize--;
		}

		return true;
	}

	private PC_Struct2<ItemStack, Integer> replacer_harvestBlockAt(World world, PC_VecI pos) {
		ItemStack loot = null;
		int meta = PC_Utils.getMD(world, pos);

		if (!replacer_canHarvestBlockAt(world, pos)) {
			return null;
		}

		if (PC_Utils.getTE(world, pos) != null) {
			/**
			 * TODO return new PC_Struct2<ItemStack,
			 * Integer>(PC_Utils.extractAndRemoveChest(world, pos), meta);
			 */
		}

		Block block = PC_Utils.getBID(world, pos);

		if (block == null) {
			return null;
		}

		/**
		 * TODO if (PC_Block.canSilkHarvest(block)) { loot =
		 * PC_Block.createStackedBlock(block, PC_Utils.getMD(world, pos)); } else
		 */
		{
			Block dropBlock = block;
			int dropMeta = block.damageDropped(PC_Utils.getMD(world, pos));
			int dropQuant = block.quantityDropped(world.rand);

			if (dropBlock == Blocks.air) {
				dropBlock = PC_Utils.getBID(world, pos);
			}

			if (dropQuant <= 0) {
				dropQuant = 1;
			}

			loot = new ItemStack(dropBlock, dropQuant, dropMeta);
		}

		return new PC_Struct2<ItemStack, Integer>(loot, meta);
	}

	private void swapBlocks(PCma_TileEntityReplacer te) {
		PC_VecI pos = te.getCoord().offset(te.getCoordOffset());

		if (pos.equals(te.getCoord())) {
			return;
		}

		if (!replacer_canHarvestBlockAt(te.getWorldObj(), pos)) {
			return;
		}

		if (!replacer_canPlaceBlockAt(te.getWorldObj(), te.getStackInSlot(0), pos)
				&& !replacer_canHarvestBlockAt(te.getWorldObj(), pos)) {
			return;
		}

		PC_Struct2<ItemStack, Integer> harvested = replacer_harvestBlockAt(te.getWorldObj(), pos);

		if (!replacer_placeBlockAt(te.getWorldObj(), te.extraMeta, te.getStackInSlot(0), pos)) {
			if (harvested != null)
				replacer_placeBlockAt(te.getWorldObj(), harvested.b, harvested.a, pos);
			te.setInventorySlotContents(0, null);
			te.extraMeta = -1;
			te.syncInventory(1, Minecraft.getMinecraft().thePlayer, 0);
			return;
		}

		if (harvested == null) {
			te.setInventorySlotContents(0, null);
			te.extraMeta = -1;
		} else {
			te.setInventorySlotContents(0, harvested.a);
			te.extraMeta = harvested.b;
		}
		te.syncInventory(1, Minecraft.getMinecraft().thePlayer, 0);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		PCma_TileEntityReplacer ter = (PCma_TileEntityReplacer) world.getTileEntity(i, j, k);

		if (ter != null && !world.isRemote) {
			boolean powered = isIndirectlyPowered(world, i, j, k);

			if (powered != ter.isState()) {
				swapBlocks(ter);
				ter.setState(powered);
			}
		}
	}

	private boolean isIndirectlyPowered(World world, int i, int j, int k) {
		/**
		 * TODO if (PC_Utils.isPoweredDirectly(world, i, j, k)) { return true; }
		 */

		if (world.isBlockIndirectlyGettingPowered(i, j, k)) {
			return true;
		}

		/**
		 * TODO if (PC_Utils.isPoweredDirectly(world, i, j-1, k)) { return true; }
		 */

		if (world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			return true;
		}

		return false;
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

}
