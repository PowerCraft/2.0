package powercraft.deco.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCde_ModelStairs extends ModelBase {
	private ModelRenderer ironLedgeStairs[];

	/**
	 * Radio block model.
	 */
	public PCde_ModelStairs() {

		textureWidth = 128;
		textureHeight = 64;

		ironLedgeStairs = new ModelRenderer[4];

		ironLedgeStairs[0] = new ModelRenderer(this, 64, 17);
		ironLedgeStairs[0].addBox(-8F, -9F, -8F, 8, 1, 16, 0.0F);
		ironLedgeStairs[0].addBox(0F, -1F, -8F, 8, 1, 16, 0.0F);

		ironLedgeStairs[1] = new ModelRenderer(this, 35, 37);
		ironLedgeStairs[1].addBox(-8F, -20F, -8F, 1, 11, 8, 0.0F);
		ironLedgeStairs[1].addBox(-8F, -12F, 0F, 1, 11, 8, 0.0F);
		ironLedgeStairs[1].rotateAngleY = (float) (Math.PI * 0.5F);

		ironLedgeStairs[2] = new ModelRenderer(this, 35, 37);
		ironLedgeStairs[2].addBox(7F, -12F, 0F, 1, 11, 8, 0.0F);
		ironLedgeStairs[2].addBox(7F, -20F, -8F, 1, 11, 8, 0.0F);
		ironLedgeStairs[2].rotateAngleY = (float) (Math.PI * 0.5F);

		ironLedgeStairs[3] = new ModelRenderer(this, 64, 34);
		ironLedgeStairs[3].addBox(-11.5F, -1F, -2F, 23, 1, 4, 0.0F);
		ironLedgeStairs[3].setRotationPoint(0, 0, 0);
		ironLedgeStairs[3].rotateAngleZ = (float) (Math.PI * 0.25F);
	}

	/**
	 * Set which fences are shown for stairs.
	 * 
	 * @param a 1st
	 * @param b 2nd
	 */
	public void setStairsFences(boolean a, boolean b) {
		ironLedgeStairs[1].showModel = a;
		ironLedgeStairs[2].showModel = b;
	}

	/**
	 * Do render.
	 * 
	 */
	public void render() {
		// parts[1].render(0.0625F);

		for (ModelRenderer part : ironLedgeStairs) {
			if (part == null) {
				break;
			}

			part.render(0.0625F); // length of one size and position unit
		}
	}
}
