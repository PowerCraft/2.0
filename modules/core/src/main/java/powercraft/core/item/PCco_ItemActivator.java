package powercraft.core.item;

import org.lwjgl.input.Keyboard;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import powercraft.api.item.PC_Item;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_MSGRegistry.MSGIterator;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCco_ItemActivator extends PC_Item {

	int Shift = Keyboard.KEY_LSHIFT;

	public PCco_ItemActivator(int id) {
		super("activator");
		setMaxDamage(100);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l,
			float par8, float par9, float par10) {
		if (world.isRemote)
			return false;
		if (world.getBlock(x, y, z) == Blocks.mob_spawner && entityplayer.isSneaking()) {
			world.setBlock(x, y, z, Blocks.air);
			PC_Utils.dropItemStack(world, x, y, z,
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockBlockSaver")));
			return true;
		} else if (world.getBlock(x, y, z) == Blocks.mob_spawner) {
			PC_GresRegistry.openGres("SpawnerEditor", entityplayer, null, new Object[] { x, y, z });
		}

		Boolean ok = (Boolean) PC_MSGRegistry.callAllMSG(new MSGIterator() {
			@Override
			public Object onRet(Object o) {
				if (o instanceof Boolean && (Boolean) o) {
					return true;
				}
				return null;
			}
		}, PC_MSGRegistry.MSG_ON_ACTIVATOR_USED_ON_BLOCK, itemstack, entityplayer, world, new PC_VecI(x, y, z));

		if (ok != null && ok) {
			return true;
		}

		if (PC_RecipeRegistry.searchRecipe3DAndDo(entityplayer, world, new PC_VecI(x, y, z)))
			return true;

		int dir = ((PC_MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;

		for (int i = 0; i < 3; i++) {

			PC_VecI pos = new PC_VecI(x - Direction.offsetX[dir], y + i, z - Direction.offsetZ[dir]);
			if (i == 2) {
				// try direct up.
				pos = new PC_VecI(x, y + 1, z);
			}

			if (PC_Utils.getBID(world, pos) == Blocks.chest
					&& PC_Utils.getBID(world, pos.copy().add(0, -1, 0)) == Blocks.iron_block) {
				break;
			}

			ItemStack stackchest = PC_Utils.extractAndRemoveTileEntity(world, pos);
			if (stackchest != null) {
				PC_Utils.dropItemStack(world, pos, stackchest);
				return true;
			}
		}

		return false;
	}
}