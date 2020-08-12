package powercraft.core.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresProgressBar;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.inventory.PC_SlotNoPickup;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_MathHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCco_GuiOreSnifferResultScreen implements PC_IGresClient {

	private PC_GresProgressBar slider;
	private PC_GresInventory inv;
	private PC_VecI vector;
	private PC_VecI[][] startpos;
	private World world;
	private PC_VecI start;

	private static final int range = 16;

	private void rotateRight() {
		PC_VecI swap = startpos[0][0];
		startpos[0][0] = startpos[0][1];
		startpos[0][1] = startpos[0][2];
		startpos[0][2] = startpos[1][2];
		startpos[1][2] = startpos[2][2];
		startpos[2][2] = startpos[2][1];
		startpos[2][1] = startpos[2][0];
		startpos[2][0] = startpos[1][0];
		startpos[1][0] = swap;
	}

	public PCco_GuiOreSnifferResultScreen(EntityPlayer player, PC_TileEntity te, Object[] o) {
		// super(player, te, o);
		int[] offsetX = { 0, 0, 0, 0, 1, -1 };
		int[] offsetZ = { 0, 0, 1, -1, 0, 0 };
		int[] offsetY = { 1, -1, 0, 0, 0, 0 };
		this.startpos = new PC_VecI[3][3];
		this.world = player.worldObj;
		this.start = new PC_VecI((Integer) o[0], (Integer) o[1], (Integer) o[2]);
		this.vector = new PC_VecI(offsetX[(Integer) o[3]], offsetY[(Integer) o[3]], offsetZ[(Integer) o[3]]);

		int l = PC_MathHelper.floor_double(((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		if (vector.equals(new PC_VecI(0, -1, 0))) {

			this.startpos[0][0] = start.offset(-1, 0, -1);
			this.startpos[1][0] = start.offset(0, 0, -1);
			this.startpos[2][0] = start.offset(1, 0, -1);
			this.startpos[0][1] = start.offset(-1, 0, 0);
			this.startpos[1][1] = start;
			this.startpos[2][1] = start.offset(1, 0, 0);
			this.startpos[0][2] = start.offset(-1, 0, 1);
			this.startpos[1][2] = start.offset(0, 0, 1);
			this.startpos[2][2] = start.offset(1, 0, 1);

			l = 3 - l;
			l += 3;
			for (int i = 0; i < l; i++) {
				rotateRight();
				rotateRight();
			}

		} else if (vector.equals(new PC_VecI(1, 0, 0))) {
			this.startpos[0][0] = start.offset(0, 1, -1);
			this.startpos[1][0] = start.offset(0, 1, 0);
			this.startpos[2][0] = start.offset(0, 1, 1);
			this.startpos[0][1] = start.offset(0, 0, -1);
			this.startpos[1][1] = start;
			this.startpos[2][1] = start.offset(0, 0, 1);
			this.startpos[0][2] = start.offset(0, -1, -1);
			this.startpos[1][2] = start.offset(0, -1, 0);
			this.startpos[2][2] = start.offset(0, -1, 1);
		} else if (vector.equals(new PC_VecI(0, 0, -1))) {
			this.startpos[0][0] = start.offset(-1, 1, 0);
			this.startpos[1][0] = start.offset(0, 1, 0);
			this.startpos[2][0] = start.offset(1, 1, 0);
			this.startpos[0][1] = start.offset(-1, 0, 0);
			this.startpos[1][1] = start;
			this.startpos[2][1] = start.offset(1, 0, 0);
			this.startpos[0][2] = start.offset(-1, -1, 0);
			this.startpos[1][2] = start.offset(0, -1, 0);
			this.startpos[2][2] = start.offset(1, -1, 0);

		} else if (vector.equals(new PC_VecI(0, 1, 0))) {
			this.startpos[0][2] = start.offset(-1, 0, -1);
			this.startpos[1][2] = start.offset(0, 0, -1);
			this.startpos[2][2] = start.offset(1, 0, -1);
			this.startpos[0][1] = start.offset(-1, 0, 0);
			this.startpos[1][1] = start;
			this.startpos[2][1] = start.offset(1, 0, 0);
			this.startpos[0][0] = start.offset(-1, 0, 1);
			this.startpos[1][0] = start.offset(0, 0, 1);
			this.startpos[2][0] = start.offset(1, 0, 1);

			l += 2;
			for (int i = 0; i < l; i++) {
				rotateRight();
				rotateRight();
			}

		} else if (vector.equals(new PC_VecI(-1, 0, 0))) {
			this.startpos[2][0] = start.offset(0, 1, -1);
			this.startpos[1][0] = start.offset(0, 1, 0);
			this.startpos[0][0] = start.offset(0, 1, 1);
			this.startpos[2][1] = start.offset(0, 0, -1);
			this.startpos[1][1] = start;
			this.startpos[0][1] = start.offset(0, 0, 1);
			this.startpos[2][2] = start.offset(0, -1, -1);
			this.startpos[1][2] = start.offset(0, -1, 0);
			this.startpos[0][2] = start.offset(0, -1, 1);
		} else if (vector.equals(new PC_VecI(0, 0, 1))) {
			this.startpos[2][0] = start.offset(-1, 1, 0);
			this.startpos[1][0] = start.offset(0, 1, 0);
			this.startpos[0][0] = start.offset(1, 1, 0);
			this.startpos[2][1] = start.offset(-1, 0, 0);
			this.startpos[1][1] = start;
			this.startpos[0][1] = start.offset(1, 0, 0);
			this.startpos[2][2] = start.offset(-1, -1, 0);
			this.startpos[1][2] = start.offset(0, -1, 0);
			this.startpos[0][2] = start.offset(1, -1, 0);
		}
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("item.PCco_ItemOreSniffer.name");
		w.padding.setTo(10, 10, 0);
		w.gapUnderTitle = 15;

		w.setAlignH(PC_GresAlign.CENTER);
		w.setAlignV(PC_GresAlign.CENTER);

		PC_GresWidget vg;

		vg = new PC_GresLayoutV();
		vg.setAlignH(PC_GresAlign.LEFT);

		vg.add(new PC_GresLabel("pc.sniffer.distance"));

		vg.add(slider = new PC_GresProgressBar(0x9900ff, 150));

		slider.configureLabel("", "" + (range - 1), range - 1);
		slider.setLabelOffset(1);
		slider.setFraction(0);
		slider.setEditable(true);
		w.add(vg);

		w.add(inv = new PC_GresInventory(3, 3));
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				inv.setSlot(x, y, new PC_SlotNoPickup());
			}
		}
		gui.add(w);

		loadBlocksForDistance(0);
	}

	private void loadBlocksForDistance(int distance) {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				ItemStack stack = null;

				PC_VecI pos = startpos[x][y].offset(vector.copy().mul(distance));

				Block block = PC_Utils.getBID(world, pos);
				int meta = PC_Utils.getMD(world, pos);

				if (block != Blocks.air && block != null) {
					if (block == Blocks.redstone_wire) {
						block = Block.getBlockFromItem(Items.redstone);
					}
					stack = new ItemStack(block, 1, meta);
				}

				((PC_SlotNoPickup) inv.getSlot(x, y)).setBackgroundStack(stack);
			}
		}
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == slider) {
			int distance = slider.getNumber() - 1;
			loadBlocksForDistance(distance);
		}
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			gui.close();
		}
	}

	@Override
	public void updateTick(PC_IGresGui gui) {
	}

	@Override
	public void updateScreen(PC_IGresGui gui) {
	}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

	@Override
	public void keyChange(String key, Object value) {
	}

}
