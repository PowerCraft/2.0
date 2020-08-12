package powercraft.api.gres;

import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.utils.PC_VecI;

public interface PC_IGresGui {

	/**
	 * Add a widget
	 * 
	 * @param widget widget to add
	 * @return the child layout
	 */
	public abstract PC_GresWidget add(PC_GresWidget widget);

	/**
	 * @param b does pause game?
	 */
	public abstract void setPausesGame(boolean b);

	/**
	 * Close the gui and set in-game focus.
	 */
	public abstract void close();

	/**
	 * Set focused element.
	 * 
	 * @param widget
	 */
	public void setFocus(PC_GresWidget widget);

	/**
	 * Set background gradient colors - 0xAARRGGBB.
	 * 
	 * @param top
	 * @param bottom
	 */
	public void setBackground(int top, int bottom);

	public PC_VecI getSize();

	public PC_GresBaseWithInventory getContainer();

	public void registerAction(PC_GresWidget widget);

	public TileEntity getTE();

	public Slot getSlotAt(int x, int y);

	public PC_VecI getMousePos();

}
