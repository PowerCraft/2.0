package powercraft.light.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCli_ModelMirror extends ModelBase {

	/** the main board */
	public ModelRenderer signBoard;
	/** connection to lower block */
	public ModelRenderer bottomSticks;
	/** connection to upper block */
	public ModelRenderer ceilingSticks;

	/** connection to X+ block */
	public ModelRenderer stickXplus;
	/** connection to X- block */
	public ModelRenderer stickXminus;
	/** connection to Z+ block */
	public ModelRenderer stickZplus;
	/** connection to Z- block */
	public ModelRenderer stickZminus;

	/**
	 * mirror model
	 */
	public PCli_ModelMirror() {
		signBoard = new ModelRenderer(this, 0, 0);
		signBoard.addBox(-8F, -5F, -1.01F, 16, 10, 2, 0.0F);

		bottomSticks = new ModelRenderer(this, 0, 13);
		bottomSticks.addBox(-5F, 5F, -1F, 2, 8, 2, 0.0F);
		bottomSticks.addBox(4F, 5F, -1F, 2, 8, 2, 0.0F);

		ceilingSticks = new ModelRenderer(this, 0, 13);
		ceilingSticks.addBox(-5F, -13F, -1F, 2, 8, 2, 0.0F);
		ceilingSticks.addBox(4F, -13F, -1F, 2, 8, 2, 0.0F);

		stickXplus = new ModelRenderer(this, 11, 13);
		stickXplus.addBox(-1F, 0F, -1F, 2, 12, 2, 0.0F);
		stickXplus.rotateAngleZ = (float) (-Math.PI * 0.5D);

		stickXminus = new ModelRenderer(this, 11, 13);
		stickXminus.addBox(-1F, 0F, -1F, 2, 12, 2, 0.0F);
		stickXminus.rotateAngleZ = (float) (Math.PI * 0.5D);

		stickZplus = new ModelRenderer(this, 11, 13);
		stickZplus.addBox(-1F, 0F, -1F, 2, 12, 2, 0.0F);
		stickZplus.rotateAngleX = (float) (-Math.PI * 0.5D);

		stickZminus = new ModelRenderer(this, 11, 13);
		stickZminus.addBox(-1F, 0F, -1F, 2, 12, 2, 0.0F);
		stickZminus.rotateAngleX = (float) (Math.PI * 0.5D);
	}

	/**
	 * Render mirror and ceiling/floor sticks
	 */
	public void renderMirrorNoSideSticks() {
		signBoard.render(0.0625F);
		bottomSticks.render(0.0625F);
		ceilingSticks.render(0.0625F);
	}

	/**
	 * render the side sticks
	 */
	public void renderMirrorSideSticks() {
		stickXplus.render(0.0625F);
		stickXminus.render(0.0625F);
		stickZplus.render(0.0625F);
		stickZminus.render(0.0625F);
	}

}
