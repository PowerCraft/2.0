package powercraft.light.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_BeamTracer.BeamHitResult;
import powercraft.api.PC_BeamTracer.BeamSettings;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.light.tile.PCli_TileEntityLaserSensor;

@PC_BlockInfo(name = "Laser Sensor", tileEntity = PCli_TileEntityLaserSensor.class)
public class PCli_BlockLaserSensor extends PC_Block implements PC_IItemInfo {

	private boolean renderSensor = false;

	public PCli_BlockLaserSensor(int id) {
		super(Material.ground, "lasersensor_down", "lasersensor_top", "lasersensor_side", "lasersensor_sensor");
		setStepSound(Block.soundTypeMetal);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setHardness(0.7F);
		setResistance(10.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
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
	public IIcon getIcon(PC_Direction par1, int par2) {
		if (renderSensor) {
			return sideIcons[3];
		}
		if (par1 == PC_Direction.BOTTOM) {
			return sideIcons[0];
		} else if (par1 == PC_Direction.TOP) {
			return sideIcons[1];
		}
		return sideIcons[2];
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public boolean renderInventoryBlock(int modelID, Object renderer) {
		float px = 1.0f / 16.0f;
		renderSensor = true;
		setBlockBounds(px * 4, px * 4, px * 4, px * 12, px * 12, px * 12);
		PC_Renderer.renderInvBox(renderer, this, 0);
		renderSensor = false;

		// cobble body
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, px * 2, 1.0F);
		PC_Renderer.renderInvBox(renderer, this, 0);
		setBlockBounds(px * 6, px * 2, px * 6, px * 10, px * 4, px * 10);
		PC_Renderer.renderInvBox(renderer, this, 0);
		// reset
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		float px = 1.0f / 16.0f;
		PCli_TileEntityLaserSensor te = (PCli_TileEntityLaserSensor) PC_Utils.getTE(world, x, y, z);
		renderSensor = true;
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(px * 4, px * 4, px * 4),
				new PC_VecF(px * 12, px * 12, px * 12), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		renderSensor = false;

		// cobble body
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.0F, 0.0F, 0.0F), new PC_VecF(1.0F, px * 2, 1.0F),
				new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(px * 6, px * 2, px * 6),
				new PC_VecF(px * 10, px * 4, px * 10), new PC_VecI(x, y, z));
		PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		// reset
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return true;
	}

	@Override
	public BeamHitResult onBlockHitByBeam(World world, int x, int y, int z, BeamSettings bs) {
		PCli_TileEntityLaserSensor te = PC_Utils.getTE(world, x, y, z);
		if (te != null && !world.isRemote) {
			te.hitByBeam();
		}
		return BeamHitResult.STOP;
	}

}
