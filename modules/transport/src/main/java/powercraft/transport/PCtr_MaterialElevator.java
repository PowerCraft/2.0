package powercraft.transport;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class PCtr_MaterialElevator extends Material {
	private static Material material = null;

	public static Material getMaterial() {
		if (material == null) {
			material = new PCtr_MaterialElevator();
		}

		return material;
	}

	private PCtr_MaterialElevator() {
		super(MapColor.airColor);
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public boolean getCanBlockGrass() {
		return false;
	}

	@Override
	public boolean blocksMovement() {
		return true;
	}
}
