package powercraft.api.gres;

import powercraft.api.tileentity.PC_ITileEntityWatcher;

public interface PC_IGresClient extends PC_ITileEntityWatcher {

	public abstract void initGui(PC_IGresGui gui);

	public void onGuiClosed(PC_IGresGui gui);

	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui);

	public void onKeyPressed(PC_IGresGui gui, char c, int i);

	public void updateTick(PC_IGresGui gui);

	public void updateScreen(PC_IGresGui gui);

	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3);

}
