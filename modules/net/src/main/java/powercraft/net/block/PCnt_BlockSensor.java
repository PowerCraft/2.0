package powercraft.net.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.net.PCnt_App;
import powercraft.net.item.PCnt_ItemBlockSensor;
import powercraft.net.model.PCnt_ModelSensor;
import powercraft.net.tile.PCnt_TileEntitySensor;

/**
 * Entity Proximity Sensor
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
@PC_BlockInfo(name = "Sensor", itemBlock = PCnt_ItemBlockSensor.class, tileEntity = PCnt_TileEntitySensor.class)
public class PCnt_BlockSensor extends PC_Block {

	/**
	 * proximity sensor
	 * 
	 * @param id block ID
	 */
	public PCnt_BlockSensor(int id) {
		super(Material.ground, "radio_red", "sensor_item", "sensor_mob", "sensor_player");
		setHardness(0.35F);
		setResistance(30.0F);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public int damageDropped(int i) {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7,
			float par8, float par9) {

		PC_GresRegistry.openGres("Sensor", player, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		// PC_Utils.openGres(player, new PClo_GuiSensor((PClo_TileEntitySensor) new
		// PC_CoordI(i, j, k).getTileEntity(world)));
		return true;
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		printRange(world, new PC_VecI(i, j, k));
	}

	/**
	 * SHow current range (distance) using chat.
	 * 
	 * @param world the world
	 * @param pos   device position.
	 */
	public static void printRange(World world, PC_VecI pos) {
		PCnt_TileEntitySensor ent = PC_Utils.getTE(world, pos);
		ent.printRange();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		if (!world.getBlock(i, j - 1, k).getMaterial().isSolid()) {
			return false;
		} else {
			return super.canPlaceBlockAt(world, i, j, k);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (!PC_Utils.<PCnt_TileEntitySensor>getTE(world, i, j, k).isActive()) {
			return;
		}

		double ii = i + 0.2D + random.nextDouble() * 0.6;
		double jj = j + 0.5D + random.nextDouble() * 0.4;
		double kk = k + 0.2D + random.nextDouble() * 0.6;

		world.spawnParticle("reddust", ii, jj, kk, 0, 0, 0);
	}

	PCnt_ModelSensor model = new PCnt_ModelSensor();

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {

		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.glTranslatef(0, -0.5F, 0);

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCnt_App.instance, "block_sensor.png"));

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);
		model.setType(metadata, false);
		model.render();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		PCnt_TileEntitySensor te = (PCnt_TileEntitySensor) PC_Utils.getTE(world, x, y, z);
		te.clearAABBList();
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0F, 0F, 0F), new PC_VecF(1F, 0.255F, 1F),
				new PC_VecI(x, y, z));
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.375F, 0.2F, 0.375F),
				new PC_VecF(1F - 0.375F, 0.7F, 1F - 0.375F), new PC_VecI(x, y, z));
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.3125F, 0.565F, 0.3125F),
				new PC_VecF(1F - 0.3125F, 0.925F, 1F - 0.3125F), new PC_VecI(x, y, z));
		setBlockBounds(0, 0, 0, 1, 1, 1);
		return true;
	}

	/*
	 * @Override public boolean removeBlockByPlayer(World world, EntityPlayer
	 * player, int x, int y, int z) { int type =
	 * PC_Utils.<PCnt_TileEntitySensor>getTE(world, x, y, z).getGroup(); boolean
	 * remove = super.removeBlockByPlayer(world, player, x, y, z);
	 * 
	 * if (remove && !PC_Utils.isCreative(player)) { dropBlockAsItem_do(world, x, y,
	 * z, new ItemStack(PCnt_App.sensor, 1, type)); } TODO: return remove; }
	 */

}
