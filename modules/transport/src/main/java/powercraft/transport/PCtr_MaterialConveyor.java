package powercraft.transport;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class PCtr_MaterialConveyor extends Material {
	private static Material material = null;

	public static Material getMaterial() {
		if (material == null) {
			material = new PCtr_MaterialConveyor();
		}

		return material;
	}

	private PCtr_MaterialConveyor() {
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
