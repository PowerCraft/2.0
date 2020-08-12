package powercraft.net.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Lang;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.net.PCnt_App;
import powercraft.net.PCnt_RadioManager;
import powercraft.net.item.PCnt_ItemBlockRadio;
import powercraft.net.item.PCnt_ItemRadioRemote;
import powercraft.net.model.PCnt_ModelRadio;
import powercraft.net.tile.PCnt_TileEntityRadio;

@PC_BlockInfo(name = "Redstone Radio", itemBlock = PCnt_ItemBlockRadio.class, tileEntity = PCnt_TileEntityRadio.class)
public class PCnt_BlockRadio extends PC_Block {

	public PCnt_BlockRadio(int id) {
		super(Material.ground, "radiobase", "radiobase_side", "radio_transmitter", "radio_receiver", "radio_red");
		setHardness(0.35F);
		setResistance(30.0F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public IIcon getIcon(PC_Direction i, int j) {
		if (i == PC_Direction.BOTTOM || i == PC_Direction.TOP)
			return sideIcons[0];
		return sideIcons[1];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		ItemStack ihold = entityplayer.getCurrentEquippedItem();

		PCnt_TileEntityRadio ter = PC_Utils.getTE(world, x, y, z);
		if (ter == null)
			return false;

		if (ihold != null) {
			if (ihold.getItem() == PC_ItemRegistry.getPCItemByName("PCco_ItemActivator")) {

				if (ter.isTransmitter()) {

					if (ihold.hasTagCompound()) {
						ihold.getTagCompound().setString("RadioChannel", ter.getChannel());
					} else {
						NBTTagCompound tag = new NBTTagCompound();
						tag.setString("RadioChannel", ter.getChannel());
						ihold.setTagCompound(tag);
					}

					PC_Utils.chatMsg(PC_Lang.tr("pc.radio.activatorGetChannel", new Object[] { ter.getChannel() }));

				} else {
					if (ihold.hasTagCompound()) {
						String chnl = ihold.getTagCompound().getString("RadioChannel");
						if (!chnl.equals("")) {

							ter.setChannel(chnl);

							PC_VecI pos = ter.getCoord();

							ter.setActive(PCnt_RadioManager.getChannelState(chnl));
							if (ter.isActive()) {
								PC_Utils.setMD(world, pos, 1);
							}

							world.scheduleBlockUpdate(pos.x, pos.y, pos.z, this, 1);

							PC_Utils.chatMsg(PC_Lang.tr("pc.radio.activatorSetChannel", new Object[] { chnl }));
							world.playSoundEffect(x, y, z, "note.snare", (world.rand.nextFloat() + 0.7F) / 2.0F, 0.5F);
						}
					}
				}
				return true;
			}
		}

		ItemStack holditem = entityplayer.getCurrentEquippedItem();
		if (holditem != null) {
			if (holditem.getItem() == PCnt_App.portableTx) {
				String channel = PCnt_RadioManager.default_radio_channel;

				channel = PC_Utils.<PCnt_TileEntityRadio>getTE(world, x, y, z).getChannel();

				PCnt_ItemRadioRemote.setChannel(holditem, channel, world.isRemote);
				world.playSoundAtEntity(entityplayer, "note.snare", (world.rand.nextFloat() + 0.7F) / 2.0F,
						1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
				return true;
			}
		}
		if (!world.isRemote)
			PC_GresRegistry.openGres("Radio", entityplayer, ter);

		/*
		 * int rtype = ter.isTransmitter() ? PClo_GuiRadio.TRANSMITTER :
		 * PClo_GuiRadio.RECEIVER; String channel = ter.getChannel();
		 * 
		 * PC_Utils.openGres(entityplayer, new PClo_GuiRadio(entityplayer.dimension,
		 * ter.getCoord(), channel, rtype));
		 */

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		PCnt_TileEntityRadio te = PC_Utils.getTE(world, x, y, z);

		if (te.isTransmitter() && te.isActive() && !world.isRemote) {
			PCnt_RadioManager.transmitterOff(te.getChannel());
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {

		PCnt_TileEntityRadio ter = PC_Utils.getTE(world, i, j, k);

		if (ter.isTransmitter()) {

			ter.setTransmitterState(isGettingPower(world, i, j, k));

		}

		world.markBlockForUpdate(i, j, k);
	}

	private boolean isGettingPower(World world, int i, int j, int k) {
		return ((PC_Utils.getBlockRedstonePowereValue(world, i, j, k) > 0
				|| PC_Utils.getBlockRedstonePowereValue(world, i, j - 1, k) > 0
				|| world.isBlockIndirectlyGettingPowered(i, j, k)
				|| world.isBlockIndirectlyGettingPowered(i, j - 1, k)));
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		PCnt_TileEntityRadio ter = PC_Utils.getTE(world, i, j, k);
		if (ter.isTransmitter())
			ter.setTransmitterState(isGettingPower(world, i, j, k));

		world.scheduleBlockUpdate(i, j, k, block, 1);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {

		PCnt_TileEntityRadio te = PC_Utils.getTE(world, i, j, k);
		if (!te.isActive())
			return;

		boolean tiny = te.isRenderMicro();

		double x = (i + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double y = (j + (tiny ? 0.2F : 0.9F)) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;
		double z = (k + 0.5F) + (random.nextFloat() - 0.5F) * 0.20000000000000001D;

		world.spawnParticle("reddust", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public int getMobilityFlag() {
		return 2;
	}

	PCnt_ModelRadio model = new PCnt_ModelRadio();

	@Override
	public boolean renderInventoryBlock(int metadata, Object renderer) {
		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.glTranslatef(0, -0.5F, 0);

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCnt_App.instance, "block_radio.png"));

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);
		boolean tx = false;
		if (metadata == 1)
			tx = true;
		model.setType(tx, false);
		model.render();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();

		return true;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		PCnt_TileEntityRadio te = (PCnt_TileEntityRadio) PC_Utils.getTE(world, x, y, z);

		te.clearAABBList();

		if (!te.isRenderMicro()) {
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0, 0, 0), new PC_VecF(1, 0.250F, 1),
					new PC_VecI(x, y, z));
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.40625F, 0, 0.40625F),
					new PC_VecF(0.59375F, 0.4375F, 0.59375F), new PC_VecI(x, y, z));
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.4375F, 0.4375F, 0.4375F),
					new PC_VecF(0.5625F, 0.625F, 0.5625F), new PC_VecI(x, y, z));
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.46875F, 0.625F, 0.46875F),
					new PC_VecF(0.53125F, 0.75F, 0.53125F), new PC_VecI(x, y, z));
		} else {
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.34375F, 0, 0.34375F),
					new PC_VecF(0.65625F, 0.0625F, 0.65625F), new PC_VecI(x, y, z));
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.46875F, 0.0625F, 0.46875F),
					new PC_VecF(0.53125F, 0.125F, 0.53125F), new PC_VecI(x, y, z));
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.4375F, 0.13F, 0.4375F),
					new PC_VecF(0.5625F, 0.25F, 0.5625F), new PC_VecI(x, y, z));
		}
		setBlockBounds(0, 0, 0, 1, 1, 1);

		return true;
	}
}
