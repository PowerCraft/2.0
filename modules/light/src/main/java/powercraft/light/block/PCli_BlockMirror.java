package powercraft.light.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_BeamTracer.BeamHitResult;
import powercraft.api.PC_BeamTracer.BeamSettings;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.light.tile.PCli_TileEntityMirror;

@PC_BlockInfo(name = "Mirror", tileEntity = PCli_TileEntityMirror.class)
public class PCli_BlockMirror extends PC_Block implements PC_IItemInfo {

	public PCli_BlockMirror(int id) {
		super(Material.glass, "mirror");
		float f = 0.4F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.1F, 0.5F - f, 0.5F + f, f1 - 0.1F, 0.5F + f);
		setHardness(1.0F);
		setResistance(4.0F);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int i) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase player, ItemStack itemStack) {
		int m = MathHelper.floor_double((((player.rotationYaw + 180F) * 16F) / 360F) + 0.5D) & 0xf;
		PC_Utils.setMD(world, i, j, k, m);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();
		if (ihold != null) {
			if (Block.getBlockFromItem(ihold.getItem()) == PC_BlockRegistry
					.getPCBlockByName("PCco_BlockPowerCrystal")) {

				PCli_TileEntityMirror teo = PC_Utils.getTE(world, i, j, k);
				if (teo != null) {
					teo.setMirrorColor(ihold.getItemDamage());
				}

				return true;
			}

			if (ihold.getItem() instanceof ItemBlock) {
				Block bhold = Block.getBlockFromItem(ihold.getItem());
				return false;
			}
		}

		int m = MathHelper.floor_double((((player.rotationYaw + 180F) * 16F) / 360F) + 0.5D) & 0xf;
		if (!world.isRemote)
			PC_Utils.setMD(world, i, j, k, m);

		return true;
	}

	/**
	 * Get mirror color
	 * 
	 * @param iblockaccess
	 * @param x
	 * @param y
	 * @param z
	 * @return the color index (crystal meta)
	 */
	public static int getMirrorColor(IBlockAccess iblockaccess, int x, int y, int z) {

		PCli_TileEntityMirror teo = PC_Utils.getTE(iblockaccess, x, y, z);

		if (teo == null) {
			return 0;
		}
		return teo.getMirrorColor();

	}

	@Override
	public int getRenderColor(int i) {
		return 0x999999;
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		return 0x999999;
	}

	/** angle rounded to 45 for vertical beam colliding with mirror */
	private static final int mirrorTo45[] = { 0, 0, 45, 90, 90, 90, 135, 180, 180, 180, 225, 270, 270, 270, 315, 0 };

	/**
	 * Get horizontal angle from movement vector
	 * 
	 * @param move movement vector
	 * @return angle
	 */
	private static float getAngleFromMove(PC_VecI move) {
		float beamAngle = 0;
		if (move.x == 0 && move.z == -1) {
			beamAngle = 0;
		}
		if (move.x == 1 && move.z == -1) {
			beamAngle = 45;
		}
		if (move.x == 1 && move.z == 0) {
			beamAngle = 90;
		}
		if (move.x == 1 && move.z == 1) {
			beamAngle = 135;
		}
		if (move.x == 0 && move.z == 1) {
			beamAngle = 180;
		}
		if (move.x == -1 && move.z == 1) {
			beamAngle = 225;
		}
		if (move.x == -1 && move.z == 0) {
			beamAngle = 270;
		}
		if (move.x == -1 && move.z == -1) {
			beamAngle = 315;
		}
		return beamAngle;
	}

	/** mirror angle for meta */
	private static final float mirrorAngle[] = new float[16];
	static {
		for (int a = 0; a < 8; a++) {
			mirrorAngle[a] = a * 22.5F;
			mirrorAngle[a + 8] = a * 22.5F;
		}
	}

	/**
	 * Get movement vector from angle
	 * 
	 * @param angle
	 * @return vector (coord)
	 */
	private static PC_VecI getMoveFromAngle(float angle) {
		int angleint = Math.round(angle);
		switch (angleint) {
		case 0:
			return new PC_VecI(0, 0, -1);
		case 45:
			return new PC_VecI(1, 0, -1);
		case 90:
			return new PC_VecI(1, 0, 0);
		case 135:
			return new PC_VecI(1, 0, 1);
		case 180:
			return new PC_VecI(0, 0, 1);
		case 225:
			return new PC_VecI(-1, 0, 1);
		case 270:
			return new PC_VecI(-1, 0, 0);
		case 315:
			return new PC_VecI(-1, 0, -1);
		}
		return null;
	}

	/**
	 * Get real difference of two angles
	 * 
	 * @param firstAngle
	 * @param secondAngle
	 * @return result
	 */
	private static float angleDiff(float firstAngle, float secondAngle) {

		float difference = secondAngle - firstAngle;

		while (difference < -180) {
			difference += 360;
		}

		while (difference > 180) {
			difference -= 360;
		}

		return difference;

	}

	/**
	 * Convert invalid angle to 0-360
	 * 
	 * @param angle to convert
	 * @return converted
	 */
	private static float fixAngle(float angle) {

		while (angle > 360) {
			angle -= 360;
		}

		while (angle < 0) {
			angle += 360;
		}

		return angle;

	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		float px = 0.0625F;
		setBlockBounds(0 * px, 6 * px, 7 * px, 15 * px, 15 * px, 9 * px);
		PC_Renderer.renderInvBox(renderer, this, 0);
		setBlockBounds(3 * px, 0 * px, 7 * px, 5 * px, 6 * px, 9 * px);
		PC_Renderer.renderInvBox(renderer, this, 0);
		setBlockBounds(10 * px, 0 * px, 7 * px, 12 * px, 6 * px, 9 * px);
		PC_Renderer.renderInvBox(renderer, this, 0);
		setBlockBounds(0, 0, 0, 1, 1, 1);
		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		float f = 0.4F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.1F, 0.5F - f, 0.5F + f, f1 - 0.1F, 0.5F + f);
		return true;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public BeamHitResult onBlockHitByBeam(World world, int x, int y, int z, BeamSettings settings) {
		PC_VecI move = settings.getMove();
		int mirrorColor = PCli_BlockMirror.getMirrorColor(world, x, y, z);
		PC_Color c = null;
		if (mirrorColor >= 0)
			c = PC_Color.fromHex(PC_Color.crystal_colors[mirrorColor]);
		if (c == null || settings.getColor().equals(c)) {
			// vertical beam
			if (move.x == 0 && move.z == 0) {

				int a = mirrorTo45[PC_Utils.getMD(world, x, y, z)];
				PC_VecI reflected = getMoveFromAngle(a).mul(-1);

				move.x = reflected.x;
				move.z = reflected.z;

			} else {
				float beamAngle = getAngleFromMove(move);
				float mAngle = mirrorAngle[PC_Utils.getMD(world, x, y, z)];

				float diff = angleDiff(beamAngle, mAngle);

				// the reflection
				float beamNew = beamAngle + diff * 2;

				beamNew = fixAngle(beamNew);

				PC_VecI reflected = getMoveFromAngle(beamNew).mul(-1);

				move.x = reflected.x;
				move.z = reflected.z;
			}
		}
		settings.setMove(move);
		return BeamHitResult.CONTINUE;
	}

}
