package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercraft.api.PC_BeamTracer.BeamHitResult;
import powercraft.api.PC_BeamTracer.BeamSettings;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Config;
import powercraft.api.annotation.PC_OreInfo;
import powercraft.api.item.PC_ItemInfo;
import powercraft.api.reflect.PC_FieldWithAnnotation;
import powercraft.api.reflect.PC_IFieldAnnotationIterator;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_RegistryClient;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Property;
import powercraft.launcher.mod_PowerCraft;
import powercraft.launcher.loader.PC_ModuleObject;

public class PC_Block extends BlockContainer {

	private PC_BlockInfo blockInfo;
	private PC_ModuleObject module;
	private PC_ItemInfo replaced;
	private PC_ItemInfo thisBlock;
	private String[] sideTextures;
	protected IIcon[] sideIcons;
	protected Object[] sideIconsO;
	private int[] oreGens;

	public PC_Block(Material material) {
		super(material);
		blockInfo = getClass().getAnnotation(PC_BlockInfo.class);
		disableStats();
	}

	public PC_Block(Material material, String texture) {
		this(material);
		sideTextures = new String[] { texture };
	}

	public PC_Block(Material material, String... textures) {
		this(material);
		sideTextures = textures;
	}

	public boolean showInCraftingTool() {
		return true;
	}

	public String getName() {
		return blockInfo == null ? null : blockInfo.name();
	}

	public boolean canPlacedRotated() {
		return blockInfo.canPlacedRotated();
	}

	public void initConfig(PC_Property config) {
		PC_OreInfo oreInfo = getClass().getAnnotation(PC_OreInfo.class);
		if (oreInfo != null) {
			oreGens = new int[4];
			oreGens[0] = config.getInt("spawn.in_chunk", oreInfo.genOresInChunk(),
					"Number of deposits in each 16x16 chunk.");
			oreGens[1] = config.getInt("spawn.deposit_max_size", oreInfo.genOresDepositMaxCount(),
					"Highest Ore count in one deposit");
			oreGens[2] = config.getInt("spawn.max_y", oreInfo.genOresMaxY(), "Max Y coordinate of ore deposits.");
			oreGens[3] = config.getInt("spawn.min_y", oreInfo.genOresMinY(), "Min Y coordinate of ore deposits.");
		}
		PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_Config.class,
				new InitConfigFieldAnnotationIterator(config));
	}

	public TileEntity newTileEntity(World world) {
		if (blockInfo == null || blockInfo.tileEntity() == null
				|| blockInfo.tileEntity() == PC_BlockInfo.PC_FakeTileEntity.class) {
			return null;
		} else {
			return PC_ReflectHelper.create(blockInfo.tileEntity());
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int a) {
		if (PC_GlobalVariables.tileEntity.size() > 0 && !world.isRemote) {
			TileEntity tileEntity = PC_GlobalVariables.tileEntity.get(0);
			if (tileEntity.isInvalid())
				tileEntity.validate();
			return tileEntity;
		}
		return newTileEntity(world);
	}

	public void setModule(PC_ModuleObject module) {
		this.module = module;
	}

	public PC_ModuleObject getModule() {
		return module;
	}

	public void setItemBlock(ItemBlock itemBlock) {
		thisBlock.item = itemBlock;
	}

	public ItemBlock getItemBlock() {
		return (ItemBlock) thisBlock.item;
	}

	@Override
	public Block setCreativeTab(CreativeTabs _default) {
		return super.setCreativeTab(PC_Utils.getCreativeTab(_default));
	}

	@Override
	public int getRenderType() {
		return PC_Renderer.getRendererID(true);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		if (PC_GlobalVariables.tileEntity.size() == 0 || world.isRemote) {
			super.breakBlock(world, x, y, z, block, metadata);
		}
	}

	public int makeBlockMetadata(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z,
			int side, float xHit, float yHit, float zHit, int metadata) {
		if (blockInfo.canPlacedRotated()) {
			int rotation = PC_MathHelper.floor_double(((entityPlayer.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			if (PC_KeyRegistry.isPlacingReversed(entityPlayer)) {
				rotation = (rotation + 2) % 4;
			}
			metadata &= ~3;
			metadata |= rotation;
		}
		return metadata;
	}

	public PC_Direction getRotation(int metadata) {
		if (blockInfo.canPlacedRotated()) {
			metadata &= 3;
			if (metadata == 0) {
				return PC_Direction.FRONT;
			} else if (metadata == 1) {
				return PC_Direction.RIGHT;
			} else if (metadata == 2) {
				return PC_Direction.BACK;
			} else if (metadata == 3) {
				return PC_Direction.LEFT;
			}
		}
		return PC_Direction.FRONT;
	}

	public PC_Direction getRotation2(int metadata) {
		if (blockInfo.canPlacedRotated()) {
			metadata &= 3;
			if (metadata == 0) {
				return PC_Direction.FRONT;
			} else if (metadata == 1) {
				return PC_Direction.LEFT;
			} else if (metadata == 2) {
				return PC_Direction.BACK;
			} else if (metadata == 3) {
				return PC_Direction.RIGHT;
			}
		}
		return PC_Direction.FRONT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int dir, int metadata) {
		PC_Direction pcDir = PC_Direction.getFromMCDir(dir);
		pcDir = pcDir.rotate(getRotation(metadata));
		return (IIcon) getIcon(pcDir, metadata);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction dir, int metadata) {
		if (sideIcons != null) {
			int index = dir.getMCDir();
			if (index >= sideIcons.length)
				index = sideIcons.length - 1;
			return (IIcon) sideIcons[index];
		}
		return null;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof PC_TileEntity) {
			((PC_TileEntity) te).onNeighborBlockChange(block);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int dir, float xHit,
			float yHit, float zHit) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof PC_TileEntity) {
			return ((PC_TileEntity) te).openGui(entityPlayer);
		}
		return false;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int dir) {
		PC_Direction pcDir = PC_Direction.getFromMCDir(dir);
		pcDir = pcDir.rotate(getRotation(PC_Utils.getMD(world, x, y, z))).mirror();
		return getProvidingWeakRedstonePowerValue(world, x, y, z, pcDir);
	}

	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof PC_TileEntity) {
			return ((PC_TileEntity) te).getProvidingWeakRedstonePowerValue(dir);
		}
		return getProvidingStrongRedstonePowerValue(world, x, y, z, dir);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int dir) {
		PC_Direction pcDir = PC_Direction.getFromMCDir(dir);
		pcDir = pcDir.rotate(getRotation(PC_Utils.getMD(world, x, y, z))).mirror();
		return getProvidingStrongRedstonePowerValue(world, x, y, z, pcDir);
	}

	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof PC_TileEntity) {
			return ((PC_TileEntity) te).getProvidingStrongRedstonePowerValue(dir);
		}
		return 0;
	}

	public int getRedstonePowerValueFromInput(World world, int x, int y, int z, PC_Direction dir) {
		dir = dir.rotateRev(getRotation(PC_Utils.getMD(world, x, y, z)));
		PC_VecI offset = dir.getOffset();
		int value = world.getIndirectPowerLevelTo(x + offset.x, y + offset.y, z + offset.z, dir.getMCDir());
		if (canProvidePower() && value == 0
				&& PC_Utils.getBID(world, x + offset.x, y + offset.y, z + offset.z) == Blocks.redstone_wire) {
			return PC_Utils.getMD(world, x + offset.x, y + offset.y, z + offset.z);
		}
		return value;
	}

	public int getRedstonePowereValue(World world, int x, int y, int z) {
		return world.getStrongestIndirectPower(x, y, z);
	}

	public int getRedstonePowerValueFromInputEx(World world, int x, int y, int z, PC_Direction dir) {
		dir = dir.rotateRev(getRotation(PC_Utils.getMD(world, x, y, z)));
		PC_VecI offset = dir.getOffset();
		int powerLevel = world.getIndirectPowerLevelTo(x + offset.x, y + offset.y, z + offset.z, dir.getMCDir());
		if (powerLevel == 0
				&& PC_Utils.getBID(world, x + offset.x, y + offset.y, z + offset.z) == Blocks.redstone_wire) {
			powerLevel = PC_Utils.getMD(world, x + offset.x, y + offset.y, z + offset.z);
		}
		return powerLevel;
	}

	public int getRedstonePowereValueEx(World world, int x, int y, int z) {
		int max = 0;
		for (int i = 0; i < 6; i++) {
			int value = getRedstonePowerValueFromInputEx(world, x, y, z, PC_Direction.getFromMCDir(i));
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	@Override
	public Block setLightOpacity(int lightOpacity) {
		thisBlock.lightOpacity = lightOpacity;
		return super.setLightOpacity(lightOpacity);
	}

	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return isFlammable(world, x, y, z, metadata);
	}

	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int md) {
		return getFlammability(world, x, y, z, md) > 0;
	}

	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return getFlammability(world, x, y, z, metadata);
	}

	public int getFlammability(IBlockAccess world, int x, int y, int z, int md) {
		return 0;
	}

	public boolean isBlockReplaceable(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		if (sideTextures != null) {
			sideIcons = new IIcon[sideTextures.length];
			for (int i = 0; i < sideTextures.length; i++) {
				if (sideTextures[i] != null) {
					sideIcons[i] = iconRegister.registerIcon(
							mod_PowerCraft.MODID + ":" + PC_TextureRegistry.getTextureName(module, sideTextures[i]));
				}
			}
		}

		PC_TextureRegistry.onIconLoading(this, iconRegister);

	}

	public void onIconLoading() {

	}

	public boolean isOre() {
		return oreGens != null;
	}

	public int getGenOresSpawnsInChunk(Random random, World world, int chunkX, int chunkZ) {
		return oreGens[0];
	}

	public int getGenOreblocksOnSpawnPoint(Random random, World world, int chunkX, int chunkZ) {
		return random.nextInt(oreGens[1] + 1);
	}

	public PC_VecI getGenOresSpawnPoint(Random random, World world, int chunkX, int chunkZ) {
		return new PC_VecI(random.nextInt(16), oreGens[3] + random.nextInt(oreGens[2] - oreGens[3] + 1),
				random.nextInt(16));
	}

	public int getGenOresSpawnMetadata(Random random, World world, int chunkX, int chunkZ) {
		return 0;
	}

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		return false;
	}

	public boolean renderInventoryBlock(int metadata, Object renderer) {
		return false;
	}

	public PC_VecI moveBlockTryToPlaceOnSide(World world, int x, int y, int z, PC_Direction side, float xHit,
			float yHit, float zHit, Block block, ItemStack itemStack, EntityPlayer entityPlayer) {
		return null;
	}

	public PC_VecI moveBlockTryToPlaceAt(World world, int x, int y, int z, PC_Direction dir, float xHit, float yHit,
			float zHit, ItemStack itemStack, EntityPlayer entityPlayer) {
		return null;
	}

	public BeamHitResult onBlockHitByBeam(World world, int x, int y, int z, BeamSettings settings) {
		return BeamHitResult.FALLBACK;
	}

	public boolean canStructureConnectTo(IBlockAccess world, int x, int y, int z, ItemStack tube, PC_Direction dir) {
		return false;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);
		if (te instanceof PC_TileEntity) {
			return ((PC_TileEntity) te).getPickMetadata();
		}
		return super.getDamageValue(world, x, y, z);
	}

	private class InitConfigFieldAnnotationIterator implements PC_IFieldAnnotationIterator<PC_Config> {

		private PC_Property config;

		public InitConfigFieldAnnotationIterator(PC_Property config) {
			this.config = config;
		}

		@Override
		public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_Config> fieldWithAnnotation) {
			Class<?> c = fieldWithAnnotation.getFieldClass();
			String name = fieldWithAnnotation.getAnnotation().name();
			if (name.equals("")) {
				name = fieldWithAnnotation.getFieldName();
			}
			String[] comment = fieldWithAnnotation.getAnnotation().comment();
			if (c == String.class) {
				String data = (String) fieldWithAnnotation.getValue();
				data = config.getString(name, data, comment);
				fieldWithAnnotation.setValue(data);
			} else if (c == Integer.class || c == int.class) {
				int data = (Integer) fieldWithAnnotation.getValue();
				data = config.getInt(name, data, comment);
				fieldWithAnnotation.setValue(data);
			} else if (c == Float.class || c == float.class) {
				float data = (Float) fieldWithAnnotation.getValue();
				data = config.getFloat(name, data, comment);
				fieldWithAnnotation.setValue(data);
			} else if (c == Boolean.class || c == boolean.class) {
				boolean data = (Boolean) fieldWithAnnotation.getValue();
				data = config.getBoolean(name, data, comment);
				fieldWithAnnotation.setValue(data);
			}
			return false;
		}

	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		byte b0 = -1;
		Vec3 vec38 = null;

		PC_TileEntity te = PC_Utils.getTE(world, new PC_VecI(x, y, z));
		if (te == null || !(te instanceof PC_ITileEntityAABB)) {
			Block block = world.getBlock(x, y, z);
			return PC_ClientUtils.collisionRayTrace(world, x, y, z, start, end, block);
		}
		ArrayList<AxisAlignedBB> aabbs = te.getAABBList();

		start = start.addVector((double) (-x), (double) (-y), (double) (-z));
		end = end.addVector((double) (-x), (double) (-y), (double) (-z));

		for (AxisAlignedBB aabb : aabbs) {
			Vec3 vec32 = start.getIntermediateWithXValue(end, aabb.minX - x);
			Vec3 vec33 = start.getIntermediateWithXValue(end, aabb.maxX - x);
			Vec3 vec34 = start.getIntermediateWithYValue(end, aabb.minY - y);
			Vec3 vec35 = start.getIntermediateWithYValue(end, aabb.maxY - y);
			Vec3 vec36 = start.getIntermediateWithZValue(end, aabb.minZ - z);
			Vec3 vec37 = start.getIntermediateWithZValue(end, aabb.maxZ - z);

			if (!PC_Utils.isVecInsideYZBounds(vec32, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec32 = null;

			if (!PC_Utils.isVecInsideYZBounds(vec33, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec33 = null;

			if (!PC_Utils.isVecInsideXZBounds(vec34, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec34 = null;

			if (!PC_Utils.isVecInsideXZBounds(vec35, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec35 = null;

			if (!PC_Utils.isVecInsideXYBounds(vec36, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec36 = null;

			if (!PC_Utils.isVecInsideXYBounds(vec37, aabb.getOffsetBoundingBox(-x, -y, -z)))
				vec37 = null;

			if (vec32 != null && (vec38 == null || start.squareDistanceTo(vec32) < start.squareDistanceTo(vec38)))
				vec38 = vec32;

			if (vec33 != null && (vec38 == null || start.squareDistanceTo(vec33) < start.squareDistanceTo(vec38)))
				vec38 = vec33;

			if (vec34 != null && (vec38 == null || start.squareDistanceTo(vec34) < start.squareDistanceTo(vec38)))
				vec38 = vec34;

			if (vec35 != null && (vec38 == null || start.squareDistanceTo(vec35) < start.squareDistanceTo(vec38)))
				vec38 = vec35;

			if (vec36 != null && (vec38 == null || start.squareDistanceTo(vec36) < start.squareDistanceTo(vec38)))
				vec38 = vec36;

			if (vec37 != null && (vec38 == null || start.squareDistanceTo(vec37) < start.squareDistanceTo(vec38)))
				vec38 = vec37;

			if (vec38 == vec32)
				b0 = 4;

			if (vec38 == vec33)
				b0 = 5;

			if (vec38 == vec34)
				b0 = 0;

			if (vec38 == vec35)
				b0 = 1;

			if (vec38 == vec36)
				b0 = 2;

			if (vec38 == vec37)
				b0 = 3;
		}

		if (vec38 == null)
			return null;
		else
			return new MovingObjectPosition(x, y, z, b0, vec38.addVector((double) x, (double) y, (double) z));
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB entityBox, List collidingBoxes,
			@Nullable Entity entityIn) {
		PC_TileEntity te = PC_Utils.getTE(world, new PC_VecI(x, y, z));
		AxisAlignedBB aabbDefault = getCollisionBoundingBoxFromPool(world, x, y, z);
		if (te == null || !(te instanceof PC_ITileEntityAABB) || entityIn instanceof EntityItem) {
			if (aabbDefault != null && entityBox.intersectsWith(aabbDefault))
				collidingBoxes.add(aabbDefault);
			return;
		}
		ArrayList<AxisAlignedBB> aabbs = te.getAABBList();
		for (AxisAlignedBB aabb : aabbs) {
			if (aabb != null && entityBox.intersectsWith(aabb) && te.getCollisions().get(aabb)) {
				collidingBoxes.add(aabb);
			}

		}
	}
}
