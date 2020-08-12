package powercraft.api.utils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercraft.api.PC_APIModule;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_INBT;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.reflect.PC_ReflectionField;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_RegistryServer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.launcher.PC_LauncherUtils;

public class PC_Utils {

	protected static PC_Utils instance;

	private static Random rand = new Random();

	public static final int BLOCK_NOTIFY = 1, BLOCK_UPDATE = 2, BLOCK_ONLY_SERVERSIDE = 4;

	public static ArrayList<String> blockMAABBList = new ArrayList<String>();

	public static void addBlockMAABBToList(String name) {
		if (!blockMAABBList.contains(name))
			blockMAABBList.add(name);
	}

	public static ArrayList<String> getBlockMAABBList() {
		return blockMAABBList;
	}

	protected PC_Utils() {

	}

	public static boolean create() {
		if (instance == null) {
			instance = new PC_Utils();
			PC_RegistryServer.create();
			return true;
		}

		return false;
	}

	protected boolean iIsClient() {
		return false;
	}

	protected GameType iGetGameTypeFor(EntityPlayer player) {
		return ((EntityPlayerMP) player).theItemInWorldManager.getGameType();
	}

	protected World iGetWorldForDimension(int dimension) {
		return mcs().worldServerForDimension(dimension);
	}

	protected boolean iIsEntityFX(Entity entity) {
		return false;
	}

	protected EntityPlayer iGetPlayer() {
		return null;
	}

	protected void iSpawnParticle(String name, Object[] o) {

	}

	protected File iGetMCDirectory() {
		return mcs().getFile("");
	}

	protected void iChatMsg(String tr) {
	}

	public static Block getBID(IBlockAccess blockAccess, int x, int y, int z) {
		return blockAccess.getBlock(x, y, z);
	}

	public static Block getBID(IBlockAccess blockAccess, PC_VecI pos) {
		return getBID(blockAccess, pos.x, pos.y, pos.z);
	}

	public static boolean setBID(World world, int x, int y, int z, Block block, int meta, int flag) {
		return world.setBlock(x, y, z, block, meta, flag);
	}

	public static boolean setBID(World world, PC_VecI pos, Block block, int meta, int flag) {
		return setBID(world, pos.x, pos.y, pos.z, block, meta, flag);
	}

	public static boolean setBID(World world, int x, int y, int z, Block block, int meta) {
		return setBID(world, x, y, z, block, meta, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setBID(World world, PC_VecI pos, Block blockID, int meta) {
		return setBID(world, pos.x, pos.y, pos.z, blockID, meta);
	}

	public static boolean setBID(World world, int x, int y, int z, Block block) {
		return setBID(world, x, y, z, block, 0);
	}

	public static boolean setBID(World world, PC_VecI pos, Block blockID) {
		return setBID(world, pos.x, pos.y, pos.z, blockID);
	}

	public static int getMD(IBlockAccess blockAccess, int x, int y, int z) {
		return blockAccess.getBlockMetadata(x, y, z);
	}

	public static int getMD(IBlockAccess blockAccess, PC_VecI pos) {
		return getMD(blockAccess, pos.x, pos.y, pos.z);
	}

	public static boolean setMD(World world, int x, int y, int z, int meta, int flag) {
		return world.setBlockMetadataWithNotify(x, y, z, meta, flag);
	}

	public static boolean setMD(World world, PC_VecI pos, int meta, int flag) {
		return setMD(world, pos.x, pos.y, pos.z, meta, flag);
	}

	public static boolean setMD(World world, int x, int y, int z, int meta) {
		return world.setBlockMetadataWithNotify(x, y, z, meta, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setMD(World world, PC_VecI pos, int meta) {
		return setMD(world, pos.x, pos.y, pos.z, meta);
	}

	// Need to remove
	public static <T extends TileEntity> T getTE(World blockAccess, int x, int y, int z) {
		return (T) blockAccess.getTileEntity(x, y, z);
	}

	// Need to remove
	public static <T extends TileEntity> T getTE(World blockAccess, PC_VecI pos) {
		return getTE(blockAccess, pos.x, pos.y, pos.z);
	}

	public static <T extends TileEntity> T getTE(IBlockAccess blockAccess, int x, int y, int z) {
		return (T) blockAccess.getTileEntity(x, y, z);
	}

	public static <T extends TileEntity> T getTE(IBlockAccess blockAccess, PC_VecI pos) {
		return getTE(blockAccess, pos.x, pos.y, pos.z);
	}

	public static void setTE(World world, int x, int y, int z, TileEntity te) {
		world.setTileEntity(x, y, z, te);
	}

	public static void setTE(World world, PC_VecI pos, TileEntity te) {
		setTE(world, pos.x, pos.y, pos.z, te);
	}

	public static <T extends Block> T getBlock(IBlockAccess blockAccess, int x, int y, int z) {
		return (T) getBID(blockAccess, x, y, z);
	}

	public static <T extends Block> T getBlock(IBlockAccess blockAccess, PC_VecI pos) {
		return getBlock(blockAccess, pos.x, pos.y, pos.z);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block, int meta, int flag) {
		return setBID(world, x, y, z, block, meta, flag);
	}

	public static boolean setBlock(World world, PC_VecI pos, Block block, int meta, int flag) {
		return setBlock(world, pos.x, pos.y, pos.z, block, meta, flag);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block, int meta) {
		return setBlock(world, x, y, z, block, meta, BLOCK_NOTIFY | BLOCK_UPDATE);
	}

	public static boolean setBlock(World world, PC_VecI pos, Block block, int meta) {
		return setBlock(world, pos.x, pos.y, pos.z, block, meta);
	}

	public static boolean setBlock(World world, int x, int y, int z, Block block) {
		return setBlock(world, x, y, z, block, 0);
	}

	public static boolean setBlock(World world, PC_VecI pos, Block block) {
		return setBlock(world, pos.x, pos.y, pos.z, block);
	}

	public static boolean isBlockReplaceable(World world, int x, int y, int z) {
		Block block = getBlock(world, x, y, z);
		if (block == null || block == Blocks.air)
			return true;
		if (block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlockReplaceable(World world, PC_VecI pos) {
		return isBlockReplaceable(world, pos.x, pos.y, pos.z);
	}

	public static int getBlockRedstonePowereValue(World world, int x, int y, int z) {
		return world.getStrongestIndirectPower(x, y, z);
	}

	public static int getBlockRedstonePowereValue(World world, PC_VecI pos) {
		return getBlockRedstonePowereValue(world, pos.x, pos.y, pos.z);
	}

	public static void markBlockForUpdate(World world, int x, int y, int z) {
		world.markBlockForUpdate(x, y, z);
	}

	public static void markBlockForUpdate(World world, PC_VecI pos) {
		markBlockForUpdate(world, pos.x, pos.y, pos.z);
	}

	public static void setBlockBounds(Block block, double x, double y, double z, double width, double height,
			double depht) {
		block.setBlockBounds((float) x, (float) y, (float) z, (float) width, (float) height, (float) depht);
	}

	public static void setBlockBoundsAndCollision(Block block, PC_TileEntity te, PC_VecF start, PC_VecF end,
			PC_VecI cords) {
		setBlockBoundsAndCollision(block, te, start, end, cords, true);
	}

	public static void setBlockBoundsAndCollision(Block block, PC_TileEntity te, float x, float y, float z, float a,
			float b, float c, int i, int j, int k) {
		setBlockBoundsAndCollision(block, te, new PC_VecF(x, y, z), new PC_VecF(a, b, c), new PC_VecI(i, j, k), true);
	}

	public static void setBlockBoundsAndCollision(Block block, PC_TileEntity te, float x, float y, float z, float a,
			float b, float c, int i, int j, int k, boolean isCollided) {
		setBlockBoundsAndCollision(block, te, new PC_VecF(x, y, z), new PC_VecF(a, b, c), new PC_VecI(i, j, k),
				isCollided);
	}

	public static void setBlockBoundsAndCollision(Block block, PC_TileEntity te, PC_VecF start, PC_VecF end,
			PC_VecI cords, boolean isCollided) {
		te.addAABB(AxisAlignedBB.getBoundingBox((double) cords.x + start.x, (double) cords.y + start.y,
				(double) cords.z + start.z, (double) cords.x + end.x, (double) cords.y + end.y,
				(double) cords.z + end.z), isCollided);
		block.setBlockBounds((float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y,
				(float) end.z);
	}

	public static void setBlockState(World world, int x, int y, int z, boolean on) {
		Block b = getBlock(world, x, y, z);

		if (b instanceof PC_Block) {
			int meta = getMD(world, x, y, z);
			PC_TileEntity te = getTE(world, x, y, z);
			Class c = b.getClass();

			if (c.isAnnotationPresent(PC_Shining.class)) {
				Block bon = (Block) PC_ReflectHelper.getFieldsWithAnnotation(c, c, PC_Shining.ON.class).get(0);
				Block boff = (Block) PC_ReflectHelper.getFieldsWithAnnotation(c, c, PC_Shining.OFF.class).get(0);

				if ((b == bon && !on) || (b == boff && on)) {
					if (on) {
						b = bon;
					} else {
						b = boff;
					}

					if (!world.isRemote) {
						PC_GlobalVariables.tileEntity.add(0, te);
					}

					setBID(world, x, y, z, b, meta, BLOCK_UPDATE);

					if (!world.isRemote) {
						PC_GlobalVariables.tileEntity.remove(0);
					}

					world.notifyBlocksOfNeighborChange(x, y, z, b);

					if (te != null) {
						// PC_PacketHandler.sendTileEntity(te); //TODO: check it
					}

				}
			}
		}
	}

	public static void setBlockState(World world, PC_VecI pos, boolean on) {
		setBlockState(world, pos.x, pos.y, pos.z, on);
	}

	public static void hugeUpdate(World world, int x, int y, int z) {
		Block blockID = getBID(world, x, y, z);
		notifyBlockOfNeighborChange(world, x - 2, y, z, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y, z, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y, z, blockID);
		notifyBlockOfNeighborChange(world, x + 2, y, z, blockID);
		notifyBlockOfNeighborChange(world, x, y - 2, z, blockID);
		notifyBlockOfNeighborChange(world, x, y - 1, z, blockID);
		notifyBlockOfNeighborChange(world, x, y + 1, z, blockID);
		notifyBlockOfNeighborChange(world, x, y + 2, z, blockID);
		notifyBlockOfNeighborChange(world, x, y, z - 2, blockID);
		notifyBlockOfNeighborChange(world, x, y, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x, y, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x, y, z + 2, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y + 1, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y + 1, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y + 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y + 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y + 1, z, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y + 1, z, blockID);
		notifyBlockOfNeighborChange(world, x, y + 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x, y + 1, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y - 1, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y - 1, z - 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y - 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y - 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x + 1, y - 1, z, blockID);
		notifyBlockOfNeighborChange(world, x - 1, y - 1, z, blockID);
		notifyBlockOfNeighborChange(world, x, y - 1, z + 1, blockID);
		notifyBlockOfNeighborChange(world, x, y - 1, z - 1, blockID);
	}

	public static void hugeUpdate(World world, PC_VecI pos) {
		hugeUpdate(world, pos.x, pos.y, pos.z);
	}

	public static void notifyNeighbour(World world, int x, int y, int z) {
		world.notifyBlocksOfNeighborChange(x, y, z, getBID(world, x, y, z));
	}

	public static void notifyNeighbour(World world, PC_VecI pos) {
		notifyNeighbour(world, pos.x, pos.y, pos.z);
	}

	public static void notifyBlockOfNeighborChange(World world, int x, int y, int z, Block blockID) {
		Block block = getBlock(world, x, y, z);
		if (block != null) {
			block.onNeighborBlockChange(world, x, y, z, blockID);
		}
	}

	public static void notifyBlockOfNeighborChange(World world, PC_VecI pos, Block blockID) {
		notifyBlockOfNeighborChange(world, pos.x, pos.y, pos.z, blockID);
	}

	public static boolean isPlayerOPOrOwner(EntityPlayer player) {
		String[] users = mcs().getConfigurationManager().getAllUsernames();
		for (String name : users)
			if (name == player.getDisplayName().trim().toLowerCase())
				return true;
		return mcs().getServerOwner() == player.getDisplayName();
	}

	public static GameType getGameTypeFor(EntityPlayer player) {
		return instance.iGetGameTypeFor(player);
	}

	public static boolean isCreative(EntityPlayer player) {
		return getGameTypeFor(player).isCreative();
	}

	public static EntityPlayer getPlayer() {
		return instance.iGetPlayer();
	}

	public static boolean anyPlayerInNear(World world, int x, int y, int z, double dist) {
		return world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, dist) != null;
	}

	public static boolean playerInNear(World world, int x, int y, int z, double dist) {
		EntityPlayer player = getPlayer();
		if (player == null)
			return false;
		x -= player.posX;
		y -= player.posY;
		z -= player.posZ;
		return x * x + y * y + z * z <= dist * dist;
	}

	public static void dropItemStack(World world, int x, int y, int z, ItemStack itemstack) {
		if (itemstack != null && !world.isRemote) {
			float f = rand.nextFloat() * 0.8F + 0.1F;
			float f1 = rand.nextFloat() * 0.8F + 0.1F;
			float f2 = rand.nextFloat() * 0.8F + 0.1F;

			while (itemstack.stackSize > 0) {
				int j = rand.nextInt(21) + 10;

				if (j > itemstack.stackSize) {
					j = itemstack.stackSize;
				}

				itemstack.stackSize -= j;

				EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2,
						new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

				if (itemstack.hasTagCompound()) {
					entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
				}

				float f3 = 0.05F;
				entityitem.motionX = (float) rand.nextGaussian() * f3;
				entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * f3;
				entityitem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityitem);
			}
		}
	}

	public static void dropItemStack(World world, PC_VecI pos, ItemStack itemstack) {
		if (itemstack.getItem() != null)
			dropItemStack(world, pos.x, pos.y, pos.z, itemstack);
	}

	public static void spawnMobs(World world, int x, int y, int z, String type) {
		byte count = 5;
		boolean spawnParticles = playerInNear(world, x, y, z, 16);

		for (int q = 0; q < count; q++) {
			EntityLiving entityliving = (EntityLiving) EntityList.createEntityByName(type, world);

			if (entityliving == null) {
				return;
			}

			int c = world.getEntitiesWithinAABB(entityliving.getClass(),
					AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(8D, 4D, 8D)).size();

			if (c >= 6) {
				if (spawnParticles) {
					double d = world.rand.nextGaussian() * 0.02D;
					double d1 = world.rand.nextGaussian() * 0.02D;
					double d2 = world.rand.nextGaussian() * 0.02D;
					world.spawnParticle("smoke", x + 0.5D, y + 0.4D, z + 0.5D, d, d1, d2);
				}

				return;
			}

			double d3 = x + (world.rand.nextDouble() - world.rand.nextDouble()) * 3D;
			double d4 = (y + world.rand.nextInt(3)) - 1;
			double d5 = z + (world.rand.nextDouble() - world.rand.nextDouble()) * 3D;
			entityliving.setLocationAndAngles(d3, d4, d5, world.rand.nextFloat() * 360F, 0.0F);

			if (world.checkNoEntityCollision(entityliving.boundingBox)
					&& world.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).size() == 0) {
				world.spawnEntityInWorld(entityliving);

				if (spawnParticles) {
					world.playAuxSFX(2004, x, y, z, 0);
					entityliving.spawnExplosionParticle();
				}

				return;
			}
		}
	}

	public static boolean isEntityFX(Entity entity) {
		return instance.iIsEntityFX(entity);
	}

	public static void spawnEntityInWorld(World world, Entity entity, boolean clientToo) {
		if (world.isRemote) {
			if (!clientToo)
				return;
		} else {
			if (isEntityFX(entity))
				return;
		}
		world.spawnEntityInWorld(entity);
	}

	public static void spawnParticle(String name, Object... o) {
		instance.iSpawnParticle(name, o);
	}

	public static int getWorldDimension(World worldObj) {
		return worldObj.provider.dimensionId;
	}

	public static World getWorldForDimension(int dimension) {
		return instance.iGetWorldForDimension(dimension);
	}

	public static MinecraftServer mcs() {
		return MinecraftServer.getServer();
	}

	public static CreativeTabs getCreativeTab(CreativeTabs _default) {
		return PC_APIModule.creativeTab;
	}

	public static boolean isClient() {
		return instance.iIsClient();
	}

	public static boolean isServer() {
		return !instance.iIsClient();
	}

	public static ItemStack getContainerItemStack(ItemStack itemStack) {
		return itemStack.getItem().getContainerItem(itemStack);
	}

	public static File getMCDirectory() {
		return instance.iGetMCDirectory();
	}

	public static File getPowerCraftFile() {
		return PC_LauncherUtils.getPowerCraftFile();
	}

	public static <T extends PC_INBT<T>> T loadFromNBT(NBTTagCompound nbttagcompound, String string, T nbt) {
		NBTTagCompound nbttag = nbttagcompound.getCompoundTag(string);
		return nbt.readFromNBT(nbttag);
	}

	public static void saveToNBT(NBTTagCompound nbttagcompound, String string, PC_INBT nbt) {
		NBTTagCompound nbttag = new NBTTagCompound();
		nbt.writeToNBT(nbttag);
		if (nbttag != null)
			nbttagcompound.setTag(string, nbttag);
	}

	public static List<Integer> parseIntList(String list) {
		if (list == null) {
			return null;
		}

		String[] parts = list.split(",");
		ArrayList<Integer> intList = new ArrayList<Integer>();

		for (String part : parts) {
			try {
				intList.add(Integer.parseInt(part.trim()));
			} catch (NumberFormatException e) {
			}
		}

		return intList;
	}

	public static void saveToNBT(NBTTagCompound nbtTag, String key, Object value) {
		if (value == null) {
			return;
		} else if (value.getClass().isArray()) {
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			int size = Array.getLength(value);
			nbtTag2.setInteger("count", size);
			nbtTag2.setString("type", value.getClass().getName());
			for (int i = 0; i < size; i++) {
				saveToNBT(nbtTag2, "value[" + i + "]", Array.get(value, i));
			}
			nbtTag.setTag(key, nbtTag2);
		} else if (value instanceof PC_INBT) {
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			nbtTag2.setString("type", value.getClass().getName());
			saveToNBT(nbtTag2, "value", (PC_INBT) value);
			nbtTag.setTag(key, nbtTag2);
		} else if (value instanceof List) {
			List l = (List) value;
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			int size = l.size();
			nbtTag2.setInteger("count", size);
			nbtTag2.setString("type", l.getClass().getName());
			for (int i = 0; i < size; i++) {
				saveToNBT(nbtTag2, "value[" + i + "]", l.get(i));
			}
			nbtTag.setTag(key, nbtTag2);
		} else if (value instanceof Map) {
			Map<?, ?> m = (Map) value;
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			int size = m.size();
			nbtTag2.setInteger("count", size);
			nbtTag2.setString("type", m.getClass().getName());
			int i = 0;
			for (Entry e : m.entrySet()) {
				saveToNBT(nbtTag2, "key[" + i + "]", e.getKey());
				saveToNBT(nbtTag2, "value[" + i + "]", e.getValue());
				i++;
			}
			nbtTag.setTag(key, nbtTag2);
		} else if (value instanceof Byte) {
			nbtTag.setByte(key, (Byte) value);
		} else if (value instanceof Short) {
			nbtTag.setShort(key, (Short) value);
		} else if (value instanceof Integer) {
			nbtTag.setInteger(key, (Integer) value);
		} else if (value instanceof Long) {
			nbtTag.setLong(key, (Long) value);
		} else if (value instanceof Float) {
			nbtTag.setFloat(key, (Float) value);
		} else if (value instanceof Double) {
			nbtTag.setDouble(key, (Double) value);
		} else if (value instanceof Boolean) {
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			nbtTag2.setString("type", Boolean.class.getName());
			nbtTag2.setBoolean("value", (Boolean) value);
			nbtTag.setTag(key, nbtTag2);
		} else if (value instanceof String) {
			nbtTag.setString(key, (String) value);
		} else if (value instanceof ItemStack) {
			NBTTagCompound nbtTag2 = new NBTTagCompound();
			nbtTag2.setString("type", ItemStack.class.getName());
			((ItemStack) value).writeToNBT(nbtTag2);
			nbtTag.setTag(key, nbtTag2);
		}
	}

	public static Object loadFromNBT(NBTTagCompound nbtTag, String key) {
		Object value = nbtTag.getTag(key);
		if (value instanceof NBTTagCompound) {
			NBTTagCompound nbtTag2 = nbtTag.getCompoundTag(key);
			try {
				Class c = Class.forName(nbtTag2.getString("type"));
				if (c.isArray()) {
					int size = nbtTag2.getInteger("count");
					Object a = Array.newInstance(c.getComponentType(), size);
					for (int i = 0; i < size; i++) {
						Array.set(a, i, loadFromNBT(nbtTag2, "value[" + i + "]"));
					}
					return a;
				} else if (c == ItemStack.class) {
					return ItemStack.loadItemStackFromNBT(nbtTag2);
				} else if (c == Boolean.class) {
					return nbtTag2.getBoolean("value");
				} else {
					try {
						Object o = c.newInstance();
						if (o instanceof PC_INBT) {
							o = loadFromNBT(nbtTag2, "value", (PC_INBT) o);
						} else if (o instanceof List) {
							int size = nbtTag2.getInteger("count");
							for (int i = 0; i < size; i++) {
								((List) o).add(loadFromNBT(nbtTag2, "value[" + i + "]"));
							}
						} else if (o instanceof Map) {
							int size = nbtTag2.getInteger("count");
							for (int i = 0; i < size; i++) {
								((Map) o).put(loadFromNBT(nbtTag2, "key[" + i + "]"),
										loadFromNBT(nbtTag2, "value[" + i + "]"));
							}
						}
						return o;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (value instanceof NBTTagByte) {
			return ((NBTTagByte) value).func_150290_f();
		} else if (value instanceof NBTTagShort) {
			return ((NBTTagShort) value).func_150289_e();
		} else if (value instanceof NBTTagInt) {
			return ((NBTTagInt) value).func_150287_d();
		} else if (value instanceof NBTTagLong) {
			return ((NBTTagLong) value).func_150291_c();
		} else if (value instanceof NBTTagFloat) {
			return ((NBTTagFloat) value).func_150288_h();
		} else if (value instanceof NBTTagDouble) {
			return ((NBTTagDouble) value).func_150286_g();
		} else if (value instanceof NBTTagString) {
			return ((NBTTagString) value).func_150285_a_();
		}
		return null;
	}

	public static double ticksToSecs(int ticks) {
		return ticks * 0.05D;
	}

	public static int ticksToSecsInt(int ticks) {
		return Math.round(ticks * 0.05F);
	}

	public static int secsToTicks(double secs) {
		return (int) (secs * 20);
	}

	public static void chatMsg(String tr) {
		instance.iChatMsg(tr);
	}

	public static ItemStack extractAndRemoveTileEntity(World world, PC_VecI pos) {
		if (PC_MSGRegistry.hasFlag(world, pos, PC_MSGRegistry.NO_HARVEST)) {
			return null;
		}

		TileEntity te = PC_Utils.getTE(world, pos);

		if (te == null) {
			return null;
		}

		ItemStack stack = new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockBlockSaver"));
		NBTTagCompound blocktag = new NBTTagCompound();
		te.writeToNBT(blocktag);
		Block dmgB = PC_Utils.getBID(world, pos);
		Item dmg = Item.getItemFromBlock(dmgB);
		blocktag.setInteger("BlockMeta", PC_Utils.getMD(world, pos));
		stack.setTagCompound(blocktag);

		if (te instanceof IInventory) {
			IInventory ic = (IInventory) te;
			for (int i = 0; i < ic.getSizeInventory(); i++) {
				ic.setInventorySlotContents(i, null);
			}
		}

		te.invalidate();
		setBID(world, pos, Blocks.air, 0);
		return stack;
	}

	public static TileEntity createTileEntity(Block block, World world, int metadata) {
		return block.createTileEntity(world, metadata);
	}

	// ----------
	public static final int INDEX_ShapedOreRecipe_width = 4;
	public static final PC_ReflectionField<ShapedOreRecipe, Integer> ShapedOreRecipe_width = new PC_ReflectionField<ShapedOreRecipe, Integer>(
			ShapedOreRecipe.class, INDEX_ShapedOreRecipe_width, int.class);

	@SuppressWarnings("unchecked")
	public static List<IRecipe> getRecipesForProduct(ItemStack prod) {
		List<IRecipe> recipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
		List<IRecipe> ret = new ArrayList<IRecipe>();

		for (IRecipe recipe : recipes) {
			ItemStack out = recipe.getRecipeOutput();
			if (PC_InventoryUtils.itemStacksEqual(out, prod)) {
				ret.add(recipe);
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemStack>[][] getExpectedInput(IRecipe recipe, int width, int hight) {
		List<ItemStack>[][] list;
		int w = width;
		int h = hight;
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes sr = (ShapedRecipes) recipe;
			int sizeX = sr.recipeWidth;
			int sizeY = sr.recipeHeight;
			ItemStack[] stacks = sr.recipeItems;
			if (w == -1)
				w = sizeX;
			if (h == -1)
				h = sizeY;
			if (sizeX > w || sizeY > h)
				return null;
			list = new List[w][h];
			int i = 0;
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					if (i < stacks.length) {
						if (stacks[i] != null) {
							list[x][y] = new ArrayList<ItemStack>();
							list[x][y].add(stacks[i]);
						}
					}
					i++;
				}
			}
		} else if (recipe instanceof ShapelessRecipes) {
			List<ItemStack> stacks = ((ShapelessRecipes) recipe).recipeItems;
			if (w == -1)
				w = stacks.size();
			if (h == -1)
				h = 1;
			if (h * w < stacks.size())
				return null;
			list = new List[w][h];
			int i = 0;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (i < stacks.size()) {
						list[x][y] = new ArrayList<ItemStack>();
						list[x][y].add(stacks.get(i));
					}
					i++;
				}
			}
		} else if (recipe instanceof ShapedOreRecipe) {
			ShapedOreRecipe sor = (ShapedOreRecipe) recipe;
			int sizeX = ShapedOreRecipe_width.getValue(sor);
			Object[] stacks = sor.getInput();
			int sizeY = stacks.length / sizeX;
			if (w == -1)
				w = sizeX;
			if (h == -1)
				h = sizeY;
			if (sizeX > w || sizeY > h)
				return null;
			list = new List[w][h];
			int i = 0;
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					if (i < stacks.length) {
						list[x][y] = getItemStacksForOreItem(stacks[i]);
					}
					i++;
				}
			}
		} else if (recipe instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe sor = (ShapelessOreRecipe) recipe;
			List<Object> stacks = sor.getInput();
			if (w == -1)
				w = stacks.size();
			if (h == -1)
				h = 1;
			if (h * w < stacks.size())
				return null;
			list = new List[w][h];
			int i = 0;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (i < stacks.size()) {
						list[x][y] = getItemStacksForOreItem(stacks.get(i));
					}
					i++;
				}
			}
		} else {
			return null;
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemStack> getItemStacksForOreItem(Object oreItem) {
		if (oreItem instanceof ItemStack) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			list.add((ItemStack) oreItem);
			return list;
		} else if (oreItem instanceof List) {
			return new ArrayList<ItemStack>((List<ItemStack>) oreItem);
		}
		return null;
	}

	public static boolean isVecInsideYZBounds(Vec3 vec, AxisAlignedBB bb) {
		return vec == null ? false
				: vec.yCoord >= bb.minY && vec.yCoord <= bb.maxY && vec.zCoord >= bb.minZ && vec.zCoord <= bb.maxZ;
	}

	public static boolean isVecInsideXZBounds(Vec3 vec, AxisAlignedBB bb) {
		return vec == null ? false
				: vec.xCoord >= bb.minX && vec.xCoord <= bb.maxX && vec.zCoord >= bb.minZ && vec.zCoord <= bb.maxZ;
	}

	public static boolean isVecInsideXYBounds(Vec3 vec, AxisAlignedBB bb) {
		return vec == null ? false
				: vec.xCoord >= bb.minX && vec.xCoord <= bb.maxX && vec.yCoord >= bb.minY && vec.yCoord <= bb.maxY;
	}

	public static boolean isBlockInBB(World world, Block block, AxisAlignedBB aabb) {
		return isBlockInBB(world, block, aabb, 0);
	}

	public static boolean isBlockInBB(World world, Block block, AxisAlignedBB aabb, int maxBlocks) {
		int counter = 0;
		for (int x = (int) aabb.minX; x < aabb.maxX; x++) {
			for (int y = (int) aabb.minY; y < aabb.maxY; y++) {
				for (int z = (int) aabb.minZ; z < aabb.maxZ; z++) {
					if (getBID(world, x, y, z) == block) {
						if (maxBlocks > 0 && counter < maxBlocks)
							counter++;
						else
							return true;
					}
				}
			}
		}
		return false;
	}
}