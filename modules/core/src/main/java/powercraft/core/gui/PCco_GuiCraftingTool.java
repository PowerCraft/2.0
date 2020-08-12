package powercraft.core.gui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_GresFrame;
import powercraft.api.gres.PC_GresImage;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresInventoryPlayer;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresScrollBar;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;
import powercraft.core.craftingtool.PCco_CraftingToolCrafter;
import powercraft.core.craftingtool.PCco_CraftingToolCraftingInventory;
import powercraft.core.craftingtool.PCco_CraftingToolInventory;

public class PCco_GuiCraftingTool extends PC_GresBaseWithInventory<PC_TileEntity> implements PC_IGresClient {

	private PCco_CraftingToolInventory ctinv;
	private PCco_CraftingToolCraftingInventory ctcinv;
	private PC_GresScrollBar scrollBar1;
	private PC_GresScrollBar scrollBar2;
	private PC_GresTextEdit search;
	private PC_GresInventory inv;
	private PC_GresWidget searchView;
	private PC_GresWidget recipeView;
	private PC_GresFrame crafting1;
	private PC_GresFrame crafting2;
	private PC_GresFrame crafting3;
	private String lastSearch;

	public PCco_GuiCraftingTool(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
	}

	@Override
	protected PC_Slot[] getAllSlots() {
		ctinv = new PCco_CraftingToolInventory(this, new PC_VecI(11, 5));
		ctcinv = new PCco_CraftingToolCraftingInventory(this);
		invSlots = new PC_Slot[ctinv.getSizeInventory() + 30];
		for (int i = 0; i < 30; i++) {
			invSlots[i] = new PC_Slot(ctcinv, i);
		}
		for (int i = 0; i < ctinv.getSizeInventory(); i++) {
			invSlots[i + 30] = new PC_Slot(ctinv, i);
		}
		return null;
	}

	@Override
	public void keyChange(String key, Object value) {

	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("pc.gui.craftingTool.title");
		searchView = new PC_GresLayoutV();
		searchView.add(search = new PC_GresTextEdit("", 20));
		PC_GresWidget lh = new PC_GresLayoutH();
		lh.setAlignV(PC_GresAlign.STRETCH);
		inv = new PC_GresInventory(11, 5);
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 11; x++) {
				inv.setSlot(x, y, invSlots[y * 11 + x + 30]);
			}
		}
		lh.add(inv);
		lh.add(scrollBar1 = new PC_GresScrollBar((ctinv.getNumRows() + 6) * 16));
		searchView.add(lh);
		searchView.add(new PC_GresInventoryPlayer(true));
		w.add(searchView);

		recipeView = new PC_GresLayoutV();
		lh = new PC_GresLayoutH();
		lh.setAlignV(PC_GresAlign.STRETCH);
		PC_GresLayoutV lv = new PC_GresLayoutV();
		crafting1 = new PC_GresFrame();
		PC_GresInventory recipeInv = new PC_GresInventory(3, 3);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				recipeInv.setSlot(x, y, invSlots[x + y * 3 + 1]);
			}
		}
		crafting1.add(recipeInv);
		crafting1.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 44, 66, 12, 11));
		crafting1.add(new PC_GresInventory(invSlots[0]));
		lv.add(crafting1);
		crafting2 = new PC_GresFrame();
		recipeInv = new PC_GresInventory(3, 3);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				recipeInv.setSlot(x, y, invSlots[x + y * 3 + 11]);
			}
		}
		crafting2.add(recipeInv);
		crafting2.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 44, 66, 12, 11));
		crafting2.add(new PC_GresInventory(invSlots[10]));
		lv.add(crafting2);
		crafting3 = new PC_GresFrame();
		recipeInv = new PC_GresInventory(3, 3);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				recipeInv.setSlot(x, y, invSlots[x + y * 3 + 21]);
			}
		}
		crafting3.add(recipeInv);
		crafting3.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 44, 66, 12, 11));
		crafting3.add(new PC_GresInventory(invSlots[20]));
		lv.add(crafting3);
		lh.add(lv);
		lh.add(scrollBar2 = new PC_GresScrollBar((ctinv.getNumRows() + 6) * 16));
		recipeView.add(lh);
		recipeView.setVisible(false);
		w.add(recipeView);
		recipeView.setVisible(false);
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == scrollBar1) {
			ctinv.setScroll(scrollBar1.getScroll() / 16);
		} else if (widget == scrollBar2) {
			if (crafting1.getSize().y > 0) {
				ctcinv.setScroll((int) (scrollBar2.getScroll() / crafting1.getSize().y + 0.5));
			}
		} else if (widget == search) {
			if (lastSearch != null) {
				if (lastSearch.equals(search.getText()))
					return;
			}
			ctinv.setSearchString(search.getText());
			scrollBar1.setMaxScollSize((ctinv.getNumRows() + 6) * 16);
			ctinv.setScroll(scrollBar1.getScroll() / 16);
		} else if (widget == inv) {
			Slot slot = inv.slotOver;
			if (slot != null && inv.isMouseDown) {
				craft(slot.getStack());
			}
		}
	}

	private void craft(ItemStack p) {
		if (p != null) {
			ItemStack is = p.copy();
			ItemStack[] pi = PCco_CraftingToolCrafter.getPlayerInventory(this.thePlayer);
			is.stackSize = PCco_CraftingToolCrafter.craft(is, pi, new ArrayList<ItemStack>(), 0, this.thePlayer);
			if (is.stackSize > 0) {
				ItemStack isp = this.thePlayer.inventory.getItemStack();
				if (isp == null) {
					this.thePlayer.inventory.setItemStack(is);
				} else {
					if (!isp.isItemEqual(is)) {
						return;
					}
					if (isp.stackSize + is.stackSize > is.getMaxStackSize()) {
						return;
					}
					isp.stackSize += is.stackSize;
					this.thePlayer.inventory.setItemStack(isp);
				}
				PCco_CraftingToolCrafter.setPlayerInventory(pi, this.thePlayer);
				// PC_PacketHandler.sendToServer(new PC_PacketSyncInv(null, 0, is, 0));
				this.ctinv.updateAvailability();
			}
		}
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			if (searchView.isVisible()) {
				PC_PacketHandler.sendToServer(new PC_PacketSyncPlayerInv(thePlayer.inventory));
				gui.close();
			} else {
				recipeView.setVisible(false);
				searchView.setVisible(true);
				searchView.getParent().calcChildPositions();
			}
		} else if (i == Keyboard.KEY_R) {
			PC_VecI mp = gui.getMousePos();
			Slot slot = gui.getSlotAt(mp.x, mp.y);
			if (slot != null) {
				ItemStack is = slot.getStack();
				if (is == null && slot instanceof PC_Slot) {
					is = ((PC_Slot) slot).getBackgroundStack();
				}
				if (is != null) {
					ctcinv.setProduct(is.copy());
					searchView.setVisible(false);
					recipeView.setVisible(true);
					crafting1.setVisible(false);
					crafting2.setVisible(false);
					crafting3.setVisible(false);
					scrollBar2.setMaxScollSize(0);
					recipeView.getParent().calcChildPositions();
				}
			}
		}
	}

	@Override
	public void updateTick(PC_IGresGui gui) {
	}

	@Override
	public void updateScreen(PC_IGresGui gui) {
		ctcinv.nextTick();
	}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

	public synchronized void updateSrcoll() {
		scrollBar1.setMaxScollSize(ctinv.getNumRows() * 16);
		ctinv.setScroll((int) (scrollBar1.getScroll() / 16.0 + 0.5));
	}

	public void updateCraftings() {
		int recipes = ctcinv.getNumRecipes();
		if (recipes > 0) {
			crafting1.setVisible(true);
		}
		if (recipes > 1) {
			crafting2.setVisible(true);
		}
		if (recipes > 2) {
			crafting3.setVisible(true);
		}
		scrollBar2.setMaxScollSize(crafting1.getSize().y * (recipes + 1));
		ctcinv.setScroll((int) (scrollBar2.getScroll() / crafting1.getSize().y + 0.5));
		recipeView.calcChildPositions();
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		ItemStack is = super.slotClick(par1, par2, par3, par4EntityPlayer);
		ctinv.updateAvailability();
		return is;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
