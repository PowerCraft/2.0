package powercraft.api.registry;

import java.util.TreeMap;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_WorldGenerator;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_OreInfo;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.block.PC_Block;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_ModuleObject;

public final class PC_BlockRegistry {

	protected static TreeMap<String, PC_Block> blocks = new TreeMap<String, PC_Block>();

	public static <T extends PC_Block> T register(PC_ModuleObject module, Class<T> blockClass,
			Class<? extends PC_ItemBlock> itemBlockClass, Class<? extends TileEntity> tileEntityClass) {
		PC_Property config = module.getConfig().getProperty(blockClass.getSimpleName(), null, null);
		try {

			if (!config.getBoolean("enabled", true)) {
				return null;
			}

			PC_Block block;
			PC_Block blockOff;
			PC_Block blockOn;

			if (blockClass.isAnnotationPresent(PC_Shining.class)) {
				if (itemBlockClass == null) {
					itemBlockClass = PC_ItemBlock.class;
				}
				// off
				blockOff = blockClass.getConstructor(boolean.class).newInstance(false);
				PC_ReflectHelper.setFieldsWithAnnotationTo(blockClass, blockClass, PC_Shining.OFF.class, blockOff);
				blockOff.setBlockName(blockClass.getSimpleName());
				blockOff.setModule(module);
				blocks.put(blockClass.getSimpleName() + ".Off", blockOff);
				GameRegistry.registerBlock(blockOff, itemBlockClass, blockClass.getSimpleName() + ".Off");
				// on
				blockOn = blockClass.getConstructor(boolean.class).newInstance(true);
				PC_ReflectHelper.setFieldsWithAnnotationTo(blockClass, blockClass, PC_Shining.ON.class, blockOn);
				blockOn.setBlockName(blockClass.getSimpleName());
				blockOn.setModule(module);
				blocks.put(blockClass.getSimpleName() + ".On", blockOn);
				GameRegistry.registerBlock(blockOn, itemBlockClass, blockClass.getSimpleName() + ".On");

				if (tileEntityClass != null) {
					if (PC_ITileEntityRenderer.class.isAssignableFrom(tileEntityClass))
						PC_RegistryServer.getInstance().tileEntitySpecialRenderer(tileEntityClass);
					else
						GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
				}

				return (T) blockOn;
			} else {
				block = blockClass.getConstructor(int.class).newInstance(1);// TODO: change to void constructor
				blocks.put(blockClass.getSimpleName(), block);
				block.setBlockName(blockClass.getSimpleName());
				block.setModule(module);
				block.initConfig(config);

				if (block.getClass().isAnnotationPresent(PC_OreInfo.class)) {
					PC_WorldGenerator.register(block.getClass().getAnnotation(PC_OreInfo.class), block);
				}

				if (itemBlockClass == null) {
					itemBlockClass = PC_ItemBlock.class;
				}

				GameRegistry.registerBlock(block, itemBlockClass, blockClass.getSimpleName());

				if (tileEntityClass != null) {
					if (PC_ITileEntityRenderer.class.isAssignableFrom(tileEntityClass))
						PC_RegistryServer.getInstance().tileEntitySpecialRenderer(tileEntityClass);
					else
						GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
				}

				return (T) block;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends PC_Block> T register(PC_ModuleObject module, Class<T> blockClass) {

		Class<? extends PC_ItemBlock> itemBlockClass = null;
		Class<? extends TileEntity> tileEntityClass = null;

		PC_BlockInfo blockInfo = PC_ReflectHelper.getAnnotation(blockClass, PC_BlockInfo.class);

		if (blockInfo != null) {

			if (blockInfo.itemBlock() != PC_BlockInfo.PC_FakeItemBlock.class) {
				itemBlockClass = blockInfo.itemBlock();
			}

			if (blockInfo.tileEntity() != PC_BlockInfo.PC_FakeTileEntity.class) {
				tileEntityClass = blockInfo.tileEntity();
			}

		}

		return register(module, blockClass, itemBlockClass, tileEntityClass);
	}

	public static PC_Block getPCBlockByName(String name) {
		if (blocks.containsKey(name)) {
			return blocks.get(name);
		}
		return null;
	}

	public static TreeMap<String, PC_Block> getPCBlocks() {
		return new TreeMap<String, PC_Block>(blocks);
	}

	public static boolean isBlock(IBlockAccess world, PC_VecI pos, String... names) {
		Block block = PC_Utils.getBlock(world, pos);
		if (block instanceof PC_Block) {
			for (String name : names)
				if (block == getPCBlockByName(name))
					return true;
		}
		return false;
	}

}
