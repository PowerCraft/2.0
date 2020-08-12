package powercraft.light.tile;

import java.util.Random;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;

public class PCli_TileEntityLightningConductor extends PC_TileEntity implements PC_ITileEntityAABB {

	private int lightningCharge = 0;
	private int lightningChargeRequired = 0;

	private static final int FLASH_CHARGE_MIN = 8000;
	private static final int FLASH_CHARGE_MAX = 17000;

	private static Random rand = new Random();

	@Override
	public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
			float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			lightningChargeRequired = getLightningChargeRequired();
		}
	}

	@Override
	public void updateEntity() {
		int meta = PC_Utils.getMD(worldObj, getCoord());
		if (tileEntityInvalid || meta == 0) {
			return;
		}

		if (worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
			if (!worldObj.isRemote) {
				updateFlashCharge();

				if (lightningCharge >= lightningChargeRequired) {
					EntityLightningBolt bolt = null;

					for (int i = 0; i < 2; i++) {
						bolt = new EntityLightningBolt(worldObj, xCoord, yCoord + 1, zCoord);
						worldObj.addWeatherEffect(bolt);
					}

					lightningCharge = 0;
					lightningChargeRequired = getLightningChargeRequired();
				}
			} else {
				for (int i = 0; i < 2; i++) {
					PC_Utils.spawnParticle("PC_EntityLaserParticleFX", worldObj,
							new PC_VecF(getCoord()).offset(-0.1F + rand.nextFloat() * 1.2F,
									rand.nextFloat() * 0.8F - 1.0f, -0.1F + rand.nextFloat() * 1.2F),
							new PC_Color(0.6f, 0.6f, 1.0f), new PC_VecF(), 0);
				}

				for (int i = 0; i < 2; i++) {
					PC_Utils.spawnParticle("PC_EntityLaserParticleFX", worldObj,
							new PC_VecF(getCoord()).offset(0.1F + rand.nextFloat() * 0.8F,
									rand.nextFloat() * 0.8F - 0.2f, 0.1F + rand.nextFloat() * 0.8F),
							new PC_Color(0.6f, 0.6f, 1.0f), new PC_VecF(), 0);
				}

				for (int i = 0; i < 2; i++) {
					PC_Utils.spawnParticle("PC_EntityLaserParticleFX", worldObj,
							new PC_VecF(getCoord()).offset(0.2F + rand.nextFloat() * 0.6F,
									rand.nextFloat() * 0.9F - 0.4f, 0.2F + rand.nextFloat() * 0.6F),
							new PC_Color(0.6f, 0.6f, 1.0f), new PC_VecF(), 0);
				}
			}
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	private void updateFlashCharge() {
		int increment = rand.nextInt(4);

		if (worldObj.isThundering()) {
			increment = 2 + rand.nextInt(15);
		} else if (worldObj.isRaining()) {
			increment = 1 + rand.nextInt(10);
		} else if (worldObj.isBlockHighHumidity(xCoord, yCoord, zCoord)) {
			increment = rand.nextInt(2);
		}

		lightningCharge += increment;
	}

	private int getLightningChargeRequired() {
		return FLASH_CHARGE_MIN + rand.nextInt(FLASH_CHARGE_MAX - FLASH_CHARGE_MIN);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		lightningCharge = tag.getInteger("LightningCharge");
		lightningChargeRequired = tag.getInteger("LightningChargeRequired");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("LightningCharge", lightningCharge);
		tag.setInteger("LightningChargeRequired", lightningChargeRequired);
	}
}
