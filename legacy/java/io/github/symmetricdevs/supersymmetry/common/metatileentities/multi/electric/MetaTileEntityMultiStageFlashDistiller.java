package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;

import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.client.utils.TooltipHelper;
import com.gregtechceu.gtceu.common.blocks.BlockBoilerCasing;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;

public class MetaTileEntityMultiStageFlashDistiller extends WorkableElectricMultiblockMachine {

    public MetaTileEntityMultiStageFlashDistiller(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.MULTI_STAGE_FLASH_DISTILLATION);
        this.recipeMapWorkable = new RecipeLogic(this, true);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityMultiStageFlashDistiller(this.metaTileEntityId);
    }

    protected BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCyM
        TraceabilityPredicate casingPredicate = states(getCasingState()).setMinGlobalLimited(70);
        TraceabilityPredicate maintenanceEnergy = super.autoAbilities(true, true, false, false, false, false, false);

        return FactoryBlockPattern.start()
                .aisle(" EEEB", " BEEB", " BEEB", " EEEB", "  BBB")
                .aisle(" AAA ", " B#B ", " B#BB", " AAA ", "  B  ")
                .aisle("CAAAB", "CAAAB", "CAAAB", "CAAAC", "CCBCC")
                .aisle(" DDD ", " DDD ", " DDD ", " DDD ", "  B  ")
                .aisle(" DDD ", " D#D ", " D#D ", " DDD ", "  B  ")
                .aisle("CDDDC", "CDDDC", "CDDDC", "CDDDC", "CCBCC")
                .aisle(" AAA ", " BAB ", " BAB ", " AAA ", "  B  ")
                .aisle(" AAA ", " B#B ", " B#B ", " AAA ", "  B  ")
                .aisle("CAAAC", "CAAAC", "CAAAC", "CAAAC", "CCBCC")
                .aisle(" DDD ", " DDD ", " DDD ", " DDD ", "  B  ")
                .aisle(" DDD ", " D#D ", " D#D ", " DDD ", "  B  ")
                .aisle("CDDDC", "CDDDC", "CDDDC", "CDDDC", "CCBCC")
                .aisle(" AAAB", " BABB", " BABB", " AAAB", "  BBB")
                .aisle(" AAAC", " B#BC", " B#BC", " AAAC", "  BCC")
                .aisle(" FFF ", " FSF ", " FFF ", " FFF ", "  B  ")
                .where('S', selfPredicate())
                .where('B', states(MetaBlocks.BOILER_CASING.getState((BlockBoilerCasing.BoilerCasingType.STEEL_PIPE))))
                .where('C', frames(Materials.Steel))
                .where('A', casingPredicate
                        .or(maintenanceEnergy))
                .where('D', states(MetaBlocks.METAL_CASING.getState(MetalCasingType.STAINLESS_CLEAN))
                        .or(maintenanceEnergy))
                .where('E', casingPredicate
                        .or(maintenanceEnergy)
                        .or(autoAbilities(false, false, true, false, false, true, false)))
                .where('F', casingPredicate
                        .or(maintenanceEnergy)
                        .or(autoAbilities(false, false, true, false, true, false, false)))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart part) {
        if (part instanceof IMultiblockAbilityPart<?>abilityPart) {
            var ability = abilityPart.getAbility();
            if (ability == MultiblockAbility.MAINTENANCE_HATCH || ability == MultiblockAbility.INPUT_ENERGY) {
                return Textures.CLEAN_STAINLESS_STEEL_CASING;
            }
        }
        return Textures.SOLID_STEEL_CASING;
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gregtech.machine.perfect_oc", new Object[0]));
    }

    @Nonnull
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }
}
