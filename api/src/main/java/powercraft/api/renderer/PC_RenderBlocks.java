package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PC_RenderBlocks extends RenderBlocks {

	private int meta = 0;

	public PC_RenderBlocks(IBlockAccess blockAccess) {
		super(blockAccess);
	}

	public void setMeta(int m) {
		meta = m;
	}

	@Override
	public boolean renderStandardBlockWithAmbientOcclusion(Block par1Block, int par2, int par3, int par4, float par5,
			float par6, float par7) {
		this.enableAO = true;
		boolean flag = false;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		boolean flag1 = true;
		int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(983055);

		if (this.getBlockIcon(par1Block).getIconName().equals("grass_top")) {
			flag1 = false;
		} else if (this.hasOverrideBlockTexture()) {
			flag1 = false;
		}

		boolean flag2;
		boolean flag3;
		boolean flag4;
		boolean flag5;
		float f7;
		int i1;

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3 - 1, par4, 0)) {
			if (this.renderMinY <= 0.0D) {
				--par3;
			}

			this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
			this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
			this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
			this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
			this.aoLightValueScratchXYNN = this.blockAccess.getBlock(par2 - 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZNN = this.blockAccess.getBlock(par2, par3, par4 - 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZNP = this.blockAccess.getBlock(par2, par3, par4 + 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXYPN = this.blockAccess.getBlock(par2 + 1, par3, par4)
					.getAmbientOcclusionLightValue();
			flag3 = this.blockAccess.getBlock(par2 + 1, par3 - 1, par4).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 - 1, par3 - 1, par4).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2, par3 - 1, par4 + 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2, par3 - 1, par4 - 1).getCanBlockGrass();

			if (!flag4 && !flag2) {
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
			} else {
				this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(par2 - 1, par3, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3,
						par4 - 1);
			}

			if (!flag5 && !flag2) {
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
				this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
			} else {
				this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(par2 - 1, par3, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3,
						par4 + 1);
			}

			if (!flag4 && !flag3) {
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
			} else {
				this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(par2 + 1, par3, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3,
						par4 - 1);
			}

			if (!flag5 && !flag3) {
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
				this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
			} else {
				this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(par2 + 1, par3, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3,
						par4 + 1);
			}

			if (this.renderMinY <= 0.0D) {
				++par3;
			}

			i1 = l;

			if (this.renderMinY <= 0.0D || !this.blockAccess.getBlock(par2, par3 - 1, par4).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
			}

			f7 = this.blockAccess.getBlock(par2, par3 - 1, par4).getAmbientOcclusionLightValue();
			f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7)
					/ 4.0F;
			f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN)
					/ 4.0F;
			f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN)
					/ 4.0F;
			f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN)
					/ 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN,
					this.aoBrightnessYZNP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP,
					this.aoBrightnessXYPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN,
					this.aoBrightnessXYZPNN, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN,
					this.aoBrightnessYZNN, i1);

			if (flag1) {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5
						* 0.5F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6
						* 0.5F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7
						* 0.5F;
			} else {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
			}

			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			this.renderFaceYNeg(par1Block, (double) par2, (double) par3, (double) par4,
					this.getBlockIconFromSideAndMetadata(par1Block, 0, meta));
			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3 + 1, par4, 1)) {
			if (this.renderMaxY >= 1.0D) {
				++par3;
			}

			this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
			this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
			this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
			this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
			this.aoLightValueScratchXYNP = this.blockAccess.getBlock(par2 - 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXYPP = this.blockAccess.getBlock(par2 + 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZPN = this.blockAccess.getBlock(par2, par3, par4 - 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZPP = this.blockAccess.getBlock(par2, par3, par4 + 1)
					.getAmbientOcclusionLightValue();
			flag3 = this.blockAccess.getBlock(par2 + 1, par3 + 1, par4).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 - 1, par3 + 1, par4).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2, par3 + 1, par4 + 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2, par3 + 1, par4 - 1).getCanBlockGrass();

			if (!flag4 && !flag2) {
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
				this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
			} else {
				this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(par2 - 1, par3, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3,
						par4 - 1);
			}

			if (!flag4 && !flag3) {
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
				this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
			} else {
				this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(par2 + 1, par3, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3,
						par4 - 1);
			}

			if (!flag5 && !flag2) {
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
			} else {
				this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(par2 - 1, par3, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3,
						par4 + 1);
			}

			if (!flag5 && !flag3) {
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
			} else {
				this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(par2 + 1, par3, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3,
						par4 + 1);
			}

			if (this.renderMaxY >= 1.0D) {
				--par3;
			}

			i1 = l;

			if (this.renderMaxY >= 1.0D || !this.blockAccess.getBlock(par2, par3 + 1, par4).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
			}

			f7 = this.blockAccess.getBlock(par2, par3 + 1, par4).getAmbientOcclusionLightValue();
			f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7)
					/ 4.0F;
			f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP)
					/ 4.0F;
			f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN)
					/ 4.0F;
			f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN)
					/ 4.0F;
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP,
					this.aoBrightnessYZPP, i1);
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP,
					this.aoBrightnessXYPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP,
					this.aoBrightnessXYZPPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN,
					this.aoBrightnessYZPN, i1);
			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			this.renderFaceYPos(par1Block, (double) par2, (double) par3, (double) par4,
					this.getBlockIconFromSideAndMetadata(par1Block, 1, meta));
			flag = true;
		}

		IIcon icon;

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3, par4 - 1, 2)) {
			if (this.renderMinZ <= 0.0D) {
				--par4;
			}

			this.aoLightValueScratchXZNN = this.blockAccess.getBlock(par2 - 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZNN = this.blockAccess.getBlock(par2, par3 - 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZPN = this.blockAccess.getBlock(par2, par3 + 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZPN = this.blockAccess.getBlock(par2 + 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
			this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
			this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
			this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
			flag3 = this.blockAccess.getBlock(par2 + 1, par3, par4 - 1).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 - 1, par3, par4 - 1).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2, par3 + 1, par4 - 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2, par3 - 1, par4 - 1).getCanBlockGrass();

			if (!flag2 && !flag4) {
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
			} else {
				this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(par2 - 1, par3 - 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1,
						par4);
			}

			if (!flag2 && !flag5) {
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
			} else {
				this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(par2 - 1, par3 + 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1,
						par4);
			}

			if (!flag3 && !flag4) {
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
			} else {
				this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(par2 + 1, par3 - 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1,
						par4);
			}

			if (!flag3 && !flag5) {
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
			} else {
				this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(par2 + 1, par3 + 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1,
						par4);
			}

			if (this.renderMinZ <= 0.0D) {
				++par4;
			}

			i1 = l;

			if (this.renderMinZ <= 0.0D || !this.blockAccess.getBlock(par2, par3, par4 - 1).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
			}

			f7 = this.blockAccess.getBlock(par2, par3, par4 - 1).getAmbientOcclusionLightValue();
			f3 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN)
					/ 4.0F;
			f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN)
					/ 4.0F;
			f5 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN)
					/ 4.0F;
			f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7)
					/ 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN,
					this.aoBrightnessYZPN, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN,
					this.aoBrightnessXYZPPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN,
					this.aoBrightnessXZPN, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN,
					this.aoBrightnessYZNN, i1);

			if (flag1) {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5
						* 0.8F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6
						* 0.8F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7
						* 0.8F;
			} else {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
			}

			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIconFromSideAndMetadata(par1Block, 2, meta);
			this.renderFaceZNeg(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				this.colorRedTopLeft *= par5;
				this.colorRedBottomLeft *= par5;
				this.colorRedBottomRight *= par5;
				this.colorRedTopRight *= par5;
				this.colorGreenTopLeft *= par6;
				this.colorGreenBottomLeft *= par6;
				this.colorGreenBottomRight *= par6;
				this.colorGreenTopRight *= par6;
				this.colorBlueTopLeft *= par7;
				this.colorBlueBottomLeft *= par7;
				this.colorBlueBottomRight *= par7;
				this.colorBlueTopRight *= par7;
				this.renderFaceZNeg(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3, par4 + 1, 3)) {
			if (this.renderMaxZ >= 1.0D) {
				++par4;
			}

			this.aoLightValueScratchXZNP = this.blockAccess.getBlock(par2 - 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZPP = this.blockAccess.getBlock(par2 + 1, par3, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZNP = this.blockAccess.getBlock(par2, par3 - 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchYZPP = this.blockAccess.getBlock(par2, par3 + 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
			this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
			this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
			this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
			flag3 = this.blockAccess.getBlock(par2 + 1, par3, par4 + 1).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 - 1, par3, par4 + 1).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2, par3 + 1, par4 + 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2, par3 - 1, par4 + 1).getCanBlockGrass();

			if (!flag2 && !flag4) {
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
			} else {
				this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(par2 - 1, par3 - 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 - 1,
						par4);
			}

			if (!flag2 && !flag5) {
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
			} else {
				this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(par2 - 1, par3 + 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3 + 1,
						par4);
			}

			if (!flag3 && !flag4) {
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
			} else {
				this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(par2 + 1, par3 - 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 - 1,
						par4);
			}

			if (!flag3 && !flag5) {
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
			} else {
				this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(par2 + 1, par3 + 1, par4)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3 + 1,
						par4);
			}

			if (this.renderMaxZ >= 1.0D) {
				--par4;
			}

			i1 = l;

			if (this.renderMaxZ >= 1.0D || !this.blockAccess.getBlock(par2, par3, par4 + 1).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
			}

			f7 = this.blockAccess.getBlock(par2, par3, par4 + 1).getAmbientOcclusionLightValue();
			f3 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP)
					/ 4.0F;
			f6 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP)
					/ 4.0F;
			f5 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP)
					/ 4.0F;
			f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7)
					/ 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP,
					this.aoBrightnessYZPP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP,
					this.aoBrightnessXYZPPP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP,
					this.aoBrightnessXZPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP,
					this.aoBrightnessYZNP, i1);

			if (flag1) {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5
						* 0.8F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6
						* 0.8F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7
						* 0.8F;
			} else {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
			}

			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIconFromSideAndMetadata(par1Block, 3, meta);
			this.renderFaceZPos(par1Block, (double) par2, (double) par3, (double) par4,
					this.getBlockIconFromSideAndMetadata(par1Block, 3, meta));

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				this.colorRedTopLeft *= par5;
				this.colorRedBottomLeft *= par5;
				this.colorRedBottomRight *= par5;
				this.colorRedTopRight *= par5;
				this.colorGreenTopLeft *= par6;
				this.colorGreenBottomLeft *= par6;
				this.colorGreenBottomRight *= par6;
				this.colorGreenTopRight *= par6;
				this.colorBlueTopLeft *= par7;
				this.colorBlueBottomLeft *= par7;
				this.colorBlueBottomRight *= par7;
				this.colorBlueTopRight *= par7;
				this.renderFaceZPos(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2 - 1, par3, par4, 4)) {
			if (this.renderMinX <= 0.0D) {
				--par2;
			}

			this.aoLightValueScratchXYNN = this.blockAccess.getBlock(par2, par3 - 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZNN = this.blockAccess.getBlock(par2, par3, par4 - 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZNP = this.blockAccess.getBlock(par2, par3, par4 + 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXYNP = this.blockAccess.getBlock(par2, par3 + 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
			this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
			this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
			this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
			flag3 = this.blockAccess.getBlock(par2 - 1, par3 + 1, par4).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 - 1, par3 - 1, par4).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2 - 1, par3, par4 - 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2 - 1, par3, par4 + 1).getCanBlockGrass();

			if (!flag5 && !flag2) {
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
			} else {
				this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(par2, par3 - 1, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1,
						par4 - 1);
			}

			if (!flag4 && !flag2) {
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
			} else {
				this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(par2, par3 - 1, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1,
						par4 + 1);
			}

			if (!flag5 && !flag3) {
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
			} else {
				this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(par2, par3 + 1, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1,
						par4 - 1);
			}

			if (!flag4 && !flag3) {
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
			} else {
				this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(par2, par3 + 1, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1,
						par4 + 1);
			}

			if (this.renderMinX <= 0.0D) {
				++par2;
			}

			i1 = l;

			if (this.renderMinX <= 0.0D || !this.blockAccess.getBlock(par2 - 1, par3, par4).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4);
			}

			f7 = this.blockAccess.getBlock(par2 - 1, par3, par4).getAmbientOcclusionLightValue();
			f6 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP)
					/ 4.0F;
			f3 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP)
					/ 4.0F;
			f4 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP)
					/ 4.0F;
			f5 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7)
					/ 4.0F;
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP,
					this.aoBrightnessXZNP, i1);
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP,
					this.aoBrightnessXYZNPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN,
					this.aoBrightnessXYNP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN,
					this.aoBrightnessXZNN, i1);

			if (flag1) {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5
						* 0.6F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6
						* 0.6F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7
						* 0.6F;
			} else {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
			}

			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIconFromSideAndMetadata(par1Block, 4, meta);
			this.renderFaceXNeg(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				this.colorRedTopLeft *= par5;
				this.colorRedBottomLeft *= par5;
				this.colorRedBottomRight *= par5;
				this.colorRedTopRight *= par5;
				this.colorGreenTopLeft *= par6;
				this.colorGreenBottomLeft *= par6;
				this.colorGreenBottomRight *= par6;
				this.colorGreenTopRight *= par6;
				this.colorBlueTopLeft *= par7;
				this.colorBlueBottomLeft *= par7;
				this.colorBlueBottomRight *= par7;
				this.colorBlueTopRight *= par7;
				this.renderFaceXNeg(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2 + 1, par3, par4, 5)) {
			if (this.renderMaxX >= 1.0D) {
				++par2;
			}

			this.aoLightValueScratchXYPN = this.blockAccess.getBlock(par2, par3 - 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZPN = this.blockAccess.getBlock(par2, par3, par4 - 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXZPP = this.blockAccess.getBlock(par2, par3, par4 + 1)
					.getAmbientOcclusionLightValue();
			this.aoLightValueScratchXYPP = this.blockAccess.getBlock(par2, par3 + 1, par4)
					.getAmbientOcclusionLightValue();
			this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4);
			this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1);
			this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1);
			this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4);
			flag3 = this.blockAccess.getBlock(par2 + 1, par3 + 1, par4).getCanBlockGrass();
			flag2 = this.blockAccess.getBlock(par2 + 1, par3 - 1, par4).getCanBlockGrass();
			flag5 = this.blockAccess.getBlock(par2 + 1, par3, par4 + 1).getCanBlockGrass();
			flag4 = this.blockAccess.getBlock(par2 + 1, par3, par4 - 1).getCanBlockGrass();

			if (!flag2 && !flag4) {
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
			} else {
				this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(par2, par3 - 1, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1,
						par4 - 1);
			}

			if (!flag2 && !flag5) {
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
			} else {
				this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(par2, par3 - 1, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1,
						par4 + 1);
			}

			if (!flag3 && !flag4) {
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
			} else {
				this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(par2, par3 + 1, par4 - 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1,
						par4 - 1);
			}

			if (!flag3 && !flag5) {
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
			} else {
				this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(par2, par3 + 1, par4 + 1)
						.getAmbientOcclusionLightValue();
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1,
						par4 + 1);
			}

			if (this.renderMaxX >= 1.0D) {
				--par2;
			}

			i1 = l;

			if (this.renderMaxX >= 1.0D || !this.blockAccess.getBlock(par2 + 1, par3, par4).isOpaqueCube()) {
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4);
			}

			f7 = this.blockAccess.getBlock(par2 + 1, par3, par4).getAmbientOcclusionLightValue();
			f3 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP)
					/ 4.0F;
			f4 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7)
					/ 4.0F;
			f5 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP)
					/ 4.0F;
			f6 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP)
					/ 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP,
					this.aoBrightnessXZPP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP,
					this.aoBrightnessXYZPPP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN,
					this.aoBrightnessXYPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN,
					this.aoBrightnessXZPN, i1);

			if (flag1) {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5
						* 0.6F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6
						* 0.6F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7
						* 0.6F;
			} else {
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
			}

			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIconFromSideAndMetadata(par1Block, 5, meta);
			this.renderFaceXPos(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				this.colorRedTopLeft *= par5;
				this.colorRedBottomLeft *= par5;
				this.colorRedBottomRight *= par5;
				this.colorRedTopRight *= par5;
				this.colorGreenTopLeft *= par6;
				this.colorGreenBottomLeft *= par6;
				this.colorGreenBottomRight *= par6;
				this.colorGreenTopRight *= par6;
				this.colorBlueTopLeft *= par7;
				this.colorBlueBottomLeft *= par7;
				this.colorBlueBottomRight *= par7;
				this.colorBlueTopRight *= par7;
				this.renderFaceXPos(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		this.enableAO = false;
		return flag;
	}

	/**
	 * Renders the given texture to the bottom face of the block. Args: block, x, y,
	 * z, texture
	 */
	@Override
	public void renderFaceYNeg(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_,
			IIcon p_147768_8_) {
		Tessellator tessellator = Tessellator.instance;

		if (this.hasOverrideBlockTexture()) {
			p_147768_8_ = this.overrideBlockTexture;
		}

		double d3 = (double) p_147768_8_.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = (double) p_147768_8_.getInterpolatedU(this.renderMaxX * 16.0D);
		double d5 = (double) p_147768_8_.getInterpolatedV(this.renderMinZ * 16.0D);
		double d6 = (double) p_147768_8_.getInterpolatedV(this.renderMaxZ * 16.0D);

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = (double) p_147768_8_.getMinU();
			d4 = (double) p_147768_8_.getMaxU();
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d5 = (double) p_147768_8_.getMinV();
			d6 = (double) p_147768_8_.getMaxV();
		}

		double d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateBottom == 2) {
			d3 = (double) p_147768_8_.getInterpolatedU(this.renderMinZ * 16.0D);
			d5 = (double) p_147768_8_.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
			d4 = (double) p_147768_8_.getInterpolatedU(this.renderMaxZ * 16.0D);
			d6 = (double) p_147768_8_.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateBottom == 1) {
			d3 = (double) p_147768_8_.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = (double) p_147768_8_.getInterpolatedV(this.renderMinX * 16.0D);
			d4 = (double) p_147768_8_.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) p_147768_8_.getInterpolatedV(this.renderMaxX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateBottom == 3) {
			d3 = (double) p_147768_8_.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = (double) p_147768_8_.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = (double) p_147768_8_.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
			d6 = (double) p_147768_8_.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = p_147768_2_ + this.renderMinX;
		double d12 = p_147768_2_ + this.renderMaxX;
		double d13 = p_147768_4_ + this.renderMinY;
		double d14 = p_147768_6_ + this.renderMinZ;
		double d15 = p_147768_6_ + this.renderMaxZ;

		if (this.renderFromInside) {
			d11 = p_147768_2_ + this.renderMaxX;
			d12 = p_147768_2_ + this.renderMinX;
		}

		if (this.enableAO) {
			tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator.setBrightness(this.brightnessTopLeft);
			tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
			tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator.setBrightness(this.brightnessBottomLeft);
			tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
			tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight,
					this.colorBlueBottomRight);
			tessellator.setBrightness(this.brightnessBottomRight);
			tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
			tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator.setBrightness(this.brightnessTopRight);
			tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
		} else {
			tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
			tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
			tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
			tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
		}
	}

	@Override
	public boolean renderStandardBlockWithColorMultiplier(Block par1Block, int par2, int par3, int par4, float par5,
			float par6, float par7) {

		this.enableAO = false;
		Tessellator tessellator = Tessellator.instance;
		boolean flag = false;
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;
		float f7 = f4 * par5;
		float f8 = f4 * par6;
		float f9 = f4 * par7;
		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		if (par1Block != Blocks.grass) {
			f10 = f3 * par5;
			f11 = f5 * par5;
			f12 = f6 * par5;
			f13 = f3 * par6;
			f14 = f5 * par6;
			f15 = f6 * par6;
			f16 = f3 * par7;
			f17 = f5 * par7;
			f18 = f6 * par7;
		}

		int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3 - 1, par4, 0)) {
			tessellator.setBrightness(this.renderMinY > 0.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
			tessellator.setColorOpaque_F(f10, f13, f16);
			this.renderFaceYNeg(par1Block, (double) par2, (double) par3, (double) par4, par1Block.getIcon(0, meta));
			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3 + 1, par4, 1)) {
			tessellator.setBrightness(this.renderMaxY < 1.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
			tessellator.setColorOpaque_F(f7, f8, f9);
			this.renderFaceYPos(par1Block, (double) par2, (double) par3, (double) par4, par1Block.getIcon(1, meta));
			flag = true;
		}

		IIcon icon;

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3, par4 - 1, 2)) {
			tessellator.setBrightness(this.renderMinZ > 0.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
			tessellator.setColorOpaque_F(f11, f14, f17);
			icon = (IIcon) par1Block.getIcon(2, meta);
			this.renderFaceZNeg(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				tessellator.setColorOpaque_F(f11 * par5, f14 * par6, f17 * par7);
				this.renderFaceZNeg(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2, par3, par4 + 1, 3)) {
			tessellator.setBrightness(this.renderMaxZ < 1.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
			tessellator.setColorOpaque_F(f11, f14, f17);
			icon = par1Block.getIcon(3, meta);
			this.renderFaceZPos(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				tessellator.setColorOpaque_F(f11 * par5, f14 * par6, f17 * par7);
				this.renderFaceZPos(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2 - 1, par3, par4, 4)) {
			tessellator.setBrightness(this.renderMinX > 0.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
			tessellator.setColorOpaque_F(f12, f15, f18);
			icon = par1Block.getIcon(4, meta);
			this.renderFaceXNeg(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				tessellator.setColorOpaque_F(f12 * par5, f15 * par6, f18 * par7);
				this.renderFaceXNeg(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || shouldSideBeRendered(par1Block, par2 + 1, par3, par4, 5)) {
			tessellator.setBrightness(this.renderMaxX < 1.0D ? l
					: par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
			tessellator.setColorOpaque_F(f12, f15, f18);
			icon = this.getBlockIconFromSideAndMetadata(par1Block, 5, meta);
			this.renderFaceXPos(par1Block, (double) par2, (double) par3, (double) par4, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
				tessellator.setColorOpaque_F(f12 * par5, f15 * par6, f18 * par7);
				this.renderFaceXPos(par1Block, (double) par2, (double) par3, (double) par4,
						BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		return flag;
	}

	public boolean shouldSideBeRendered(Block block, int x, int y, int z, int dir) {
		return block.shouldSideBeRendered(blockAccess, x, y, z, dir);
	}

}
