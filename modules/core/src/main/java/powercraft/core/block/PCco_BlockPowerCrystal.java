package powercraft.core.block;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_BeamTracer.BeamHitResult;
import powercraft.api.PC_BeamTracer.BeamSettings;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Config;
import powercraft.api.annotation.PC_OreInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.core.item.PCco_ItemBlockPowerCrystal;

@PC_BlockInfo(name = "Power Crystal", itemBlock = PCco_ItemBlockPowerCrystal.class)
@PC_OreInfo(oreName = "PowerCrystal", genOresInChunk = 3, genChances = 30, genOresDepositMaxCount = 4, genOresMaxY = 15, genOresMinY = 5)
public class PCco_BlockPowerCrystal extends PC_Block {

	@PC_Config
	public static boolean makeSound;

	public PCco_BlockPowerCrystal(int id) {
		super(Material.glass, "powercrystal");
		setHardness(0.5F);
		setResistance(0.5F);
		setStepSound(Block.soundTypeGlass);
		setCreativeTab(CreativeTabs.tabMaterials);
		setBlockTextureName("glass");
		setLightLevel(0);
	}

	@Override
	public int getBlockColor() {
		return PC_Color.crystal_colors[2];
	}

	@Override
	public int getRenderColor(int i) {
		return PC_Color.crystal_colors[PC_MathHelper.clamp_int(i, 0, 7)];
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		return PC_Color.crystal_colors[PC_MathHelper.clamp_int(iblockaccess.getBlockMetadata(i, j, k), 0, 7)];
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return true;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		Block block = world.getBlock(i, j - 1, k);

		if (makeSound && PC_SoundRegistry.isSoundEnabled()) {
			EntityPlayer player = world.getClosestPlayer(i + 0.5D, j + 0.5D, k + 0.5D, 12);

			if (player != null) {
				if (block == Blocks.stone)// || id_under == 7 || id_under == blockID ??????
				{
					int distance = (int) Math.round(player.getDistanceSq(i + 0.5D, j + 0.5D, k + 0.5D) / 10);

					if (distance == 0) {
						distance = 1;
					}

					if (random.nextInt(distance) == 0) {
						PC_SoundRegistry.playSound(i + 0.5D, j + 0.5D, k + 0.5D, "random.orb", 0.15F,
								0.5F * ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.8F));
					}
				}
			}
		}

		int meta = world.getBlockMetadata(i, j, k);

		if (meta == 8) {
			PC_Utils.setMD(world, i, j, k, 0);
			meta = 0;
		}

		double r = PC_Color.red(getRenderColor(meta));
		double g = PC_Color.green(getRenderColor(meta));
		double b = PC_Color.blue(getRenderColor(meta));
		r = (r > 0D ? r : 0.001D);
		g = (g > 0D ? g : 0.001D);
		b = (b > 0D ? b : 0.001D);
		float y = j + random.nextFloat();
		float x = i + random.nextFloat();
		float z = k + random.nextFloat();
		world.spawnParticle("reddust", x, y, z, r, g, b);
	}

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		Random posRand = new Random(metadata);
		for (int q = 3 + posRand.nextInt(3); q > 0; q--) {
			float x, y, z, a, b, c;
			x = 0.0F + posRand.nextFloat() * 0.6F;
			y = 0.0F + posRand.nextFloat() * 0.6F;
			z = 0.0F + posRand.nextFloat() * 0.6F;
			a = 0.2F + Math.max(posRand.nextFloat() * (0.7F - x), 0.3F);
			b = 0.2F + Math.max(posRand.nextFloat() * (0.7F - y), 0.3F);
			c = 0.2F + Math.max(posRand.nextFloat() * (0.7F - z), 0.3F);
			setBlockBounds(x, y, z, x + a, y + b, z + c);
			PC_Renderer.glPushMatrix();
			PC_Renderer.glEnable(GL11.GL_BLEND);
			PC_Renderer.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			PC_Renderer.renderInvBox(renderer, this, metadata);
			PC_Renderer.glDisable(GL11.GL_BLEND);
			PC_Renderer.glPopMatrix();
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);

		return true;

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		Random posRand = new Random(x + x * y * z + z + world.getBlockMetadata(x, y, z));
		for (int q = 3 + posRand.nextInt(2); q > 0; q--) {
			float i, j, k, a, b, c;
			i = posRand.nextFloat() * 0.6F;
			j = (q == 2 ? 0.001F : posRand.nextFloat() * 0.6F);
			k = posRand.nextFloat() * 0.6F;
			a = i + 0.3F + posRand.nextFloat() * (0.7F - i);
			b = j + 0.3F + posRand.nextFloat() * (0.7F - j);
			c = k + 0.3F + posRand.nextFloat() * (0.7F - k);
			setBlockBounds(i, j, k, a, b, c);
			PC_Renderer.glPushMatrix();
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
			PC_Renderer.glPopMatrix();
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);

		return true;

	}

	@Override
	public int getGenOresSpawnMetadata(Random random, World world, int chunkX, int chunkZ) {
		return random.nextInt(8);
	}

	@Override
	public BeamHitResult onBlockHitByBeam(World world, int x, int y, int z, BeamSettings settings) {
		settings.setColor(PC_Color
				.fromHex(PC_Color.crystal_colors[PC_MathHelper.clamp_int(PC_Utils.getMD(world, x, y, z), 0, 7)]));
		return BeamHitResult.CONTINUE;
	}

}
