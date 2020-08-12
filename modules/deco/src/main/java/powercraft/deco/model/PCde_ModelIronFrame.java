package powercraft.deco.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCde_ModelIronFrame extends ModelBase {

	private ModelRenderer ironFrame[];

	/**
	 * Radio block model.
	 */
	public PCde_ModelIronFrame() {

		textureWidth = 128;
		textureHeight = 64;

		ironFrame = new ModelRenderer[8];

		// frame
		// top bottom
		ironFrame[0] = new ModelRenderer(this, 0, 0);
		ironFrame[0].addBox(-8F, -8F, -8F, 16, 3, 16, 0.0F);
		ironFrame[0].addBox(-8F, 5F, -8F, 16, 3, 16, 0.0F);

		ironFrame[1] = new ModelRenderer(this, 0, 0);
		ironFrame[1].addBox(-8F, -8F, -8F, 16, 3, 16, 0.0F);
		ironFrame[1].addBox(-8F, 5F, -8F, 16, 3, 16, 0.0F);
		ironFrame[1].rotateAngleX = (float) (Math.PI / 2);

		ironFrame[2] = new ModelRenderer(this, 0, 0);
		ironFrame[2].addBox(-8F, -8F, -8F, 16, 3, 16, 0.0F);
		ironFrame[2].addBox(-8F, 5F, -8F, 16, 3, 16, 0.0F);
		ironFrame[2].rotateAngleZ = (float) (Math.PI / 2);

		// fillings
		ironFrame[3] = new ModelRenderer(this, 64, 39);
		ironFrame[3].addBox(-5F, -8.5F, -5F, 10, 4, 10, 0.0F);

		ironFrame[4] = new ModelRenderer(this, 64, 39);
		ironFrame[4].addBox(-5F, -8.5F, -5F, 10, 4, 10, 0.0F);
		ironFrame[4].rotateAngleX = (float) (Math.PI / 2);

		ironFrame[5] = new ModelRenderer(this, 64, 39);
		ironFrame[5].addBox(-5F, -8.5F, -5F, 10, 4, 10, 0.0F);
		ironFrame[5].rotateAngleZ = -(float) (Math.PI / 2);

		ironFrame[6] = new ModelRenderer(this, 64, 39);
		ironFrame[6].addBox(-5F, -8.5F, -5F, 10, 4, 10, 0.0F);
		ironFrame[6].rotateAngleX = -(float) (Math.PI / 2);

		ironFrame[7] = new ModelRenderer(this, 64, 39);
		ironFrame[7].addBox(-5F, -8.5F, -5F, 10, 4, 10, 0.0F);
		ironFrame[7].rotateAngleZ = (float) (Math.PI / 2);

	}

	/**
	 * Set iron frame fillings
	 * 
	 * @param side 0 = top, 1,2,3,4 = sides
	 * @param show visible
	 */
	public void setFrameParts(int side, boolean show) {
		ironFrame[side + 3].showModel = show;
	}

	/**
	 * Do render.
	 * 
	 */
	public void render() {
		// parts[1].render(0.0625F);

		for (ModelRenderer part : ironFrame) {
			if (part == null) {
				break;
			}

			part.render(0.0625F); // length of one size and position unit
		}
	}

}
