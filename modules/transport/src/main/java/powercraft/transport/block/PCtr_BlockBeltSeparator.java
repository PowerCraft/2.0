package powercraft.transport.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.tile.PCtr_TileEntitySeparationBelt;

@PC_BlockInfo(name = "separation belt", tileEntity = PCtr_TileEntitySeparationBelt.class, canPlacedRotated = true)
public class PCtr_BlockBeltSeparator extends PCtr_BlockBeltBase {
	public PCtr_BlockBeltSeparator(int id) {
		super("belt_separator");
		setBlockName("PCtr_BlockBeltSeparator");
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int p_149681_5_, EntityPlayer p_149681_6_) {
		PCtr_TileEntitySeparationBelt te = (PCtr_TileEntitySeparationBelt) world.getTileEntity(x, y, z);
		for (int a = 0; a < te.getSizeInventory(); a++) {
			if (te.getStackInSlot(a) != null) {
				EntityItem ei = new EntityItem(world, x, y, z, te.getStackInSlot(a));
				world.spawnEntityInWorld(ei);
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		PCtr_TileEntitySeparationBelt tes = (PCtr_TileEntitySeparationBelt) world.getTileEntity(i, j, k);
		PC_Direction redir = tes.calculateItemDirection(entity);
		PC_Direction rotation = getRotation(world.getBlockMetadata(i, j, k));
		redir = rotation.rotate(redir);

		PC_VecI pos_leading_to = pos.offset(redir.getOffset());

		if (entity instanceof EntityItem
				&& PCtr_BeltHelper.storeEntityItemAt(world, pos_leading_to, (EntityItem) entity, redir)) {
			return;
		}

		boolean leadsToNowhere = PCtr_BeltHelper.isBlocked(world, pos_leading_to);

		if (!leadsToNowhere) {
			PCtr_BeltHelper.entityPreventDespawning(world, pos, true, entity);
		}

		leadsToNowhere = leadsToNowhere && PCtr_BeltHelper.isBeyondStorageBorder(world, redir, pos, entity,
				PCtr_BeltHelper.STORAGE_BORDER_LONG);
		PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, !leadsToNowhere, redir,
				PCtr_BeltHelper.MAX_HORIZONTAL_SPEED, PCtr_BeltHelper.HORIZONTAL_BOOST);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		if (PCtr_BeltHelper.blockActivated(world, i, j, k, entityplayer)) {
			return true;
		} else {

			PC_GresRegistry.openGres("SeperationBelt", entityplayer, PC_Utils.getTE(world, i, j, k));
			return true;
		}
	}

}
