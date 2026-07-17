package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric.strand;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;

import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.unification.FluidUnifier;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.unification.material.properties.PropertyKey;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.capability.Strand;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.unification.material.info.SuSyMaterialFlags;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public abstract class MetaTileEntityStrandMold extends MetaTileEntityStrandShaper {

    private static final FluidStack COOLANT = Materials.Water.getFluid(50);
    private static final FluidStack HOT_COOLANT = Materials.Steam.getFluid(50 * 960);

    public MetaTileEntityStrandMold(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    protected abstract int getRequiredMetal();

    protected abstract double getOutputThickness();

    protected abstract double getOutputWidth();

    @Override
    protected boolean consumeInputsAndSetupRecipe() {
        FluidStack stack = getFirstMaterialFluid();
        if (stack == null || stack.amount < getRequiredMetal()) return false;
        stack = stack.copy();
        stack.amount = getRequiredMetal();
        this.inputFluidInventory.drain(stack, true);
        this.inputFluidInventory.drain(COOLANT, true);
        this.maxProgress = 40;
        return true;
    }

    @Override
    protected Strand resultingStrand() {
        FluidStack stack = getFirstMaterialFluid();
        if (stack == null || stack.amount < getRequiredMetal()) {
            return null;
        }
        Material mat = FluidUnifier.getMaterialFromFluid(stack.getFluid());
        if (mat == null || !mat.hasProperty(PropertyKey.INGOT) || !mat.hasFlag(SuSyMaterialFlags.CONTINUOUSLY_CAST)) {
            return null;
        }
        if (mat.getFluid(GCYMFluidStorageKeys.MOLTEN) != null) {
            if (stack.getFluid() != mat.getFluid(GCYMFluidStorageKeys.MOLTEN)) {
                return null;
            }
        } else if (mat.getFluid(FluidStorageKeys.LIQUID) != null) {
            if (stack.getFluid() != mat.getFluid(FluidStorageKeys.LIQUID)) {
                return null;
            }
        }
        FluidStack fluidStack = this.inputFluidInventory.drain(COOLANT, false);
        if (fluidStack == null || fluidStack.amount < 50) {
            return null;
        }
        return new Strand(getOutputThickness(), getOutputWidth(), false, mat, stack.getFluid().getTemperature());
    }

    @Override
    public void output() {
        super.output();
        this.outputFluidInventory.fill(HOT_COOLANT, true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart part) {
        if (part instanceof IMultiblockAbilityPart<?>abilityPart) {
            MultiblockAbility<?> ability = abilityPart.getAbility();
            if (ability == MultiblockAbility.IMPORT_FLUIDS || ability == MultiblockAbility.EXPORT_FLUIDS ||
                    ability == SuSyMultiblockAbilities.STRAND_EXPORT) {
                return SusyTextures.CONDUCTIVE_COPPER_PIPE;
            }
        }
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.FLUID_SOLIDIFIER_OVERLAY;
    }

    protected BlockState getPipeCasingState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.COPPER_PIPE);
    }

    protected BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("susy.multiblock.strand_mold.tooltip"));
    }
}
