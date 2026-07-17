package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.primitive;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;

import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.ItemHandlerList;
import com.gregtechceu.gtceu.api.capability.PrimitiveRecipeLogic;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.ParallelLogicType;
import com.gregtechceu.gtceu.api.machine.multiblock.RecipeMapPrimitiveMultiblockController;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityCupolaFurnace extends RecipeMapPrimitiveMultiblockController {

    public int size;

    public MetaTileEntityCupolaFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.CUPOLA_FURNACE);
        this.recipeMapWorkable = new CupolaFurnaceLogic(this, SuSyRecipeMaps.CUPOLA_FURNACE);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCupolaFurnace(this.metaTileEntityId);
    }

    public static TraceabilityPredicate isIndicatorPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
            if (air().test(blockWorldState)) {
                blockWorldState.getMatchContext().increment("height", 1);
                return true;
            } else
                return false;
        });
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, BACK, UP)
                .aisle("FAF", "AOA", "FAF")
                .aisle("CCC", "CAC", "CSC")
                .aisle("CCC", "CAC", "CCC")
                .aisle("CCC", "CHC", "CCC").setRepeatable(1, 8)
                .where('C',
                        states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS))
                                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(4)))
                .where('A', air())
                .where('H', isIndicatorPredicate())
                .where('S', selfPredicate())
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                .where('F', frames(Materials.Steel))
                .build();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.initializeAbilities();
        this.size = context.getOrDefault("height", 1);
    }

    @Override
    protected void initializeAbilities() {
        this.importItems = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.PRIMITIVE_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.CUPOLA_FURNACE_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    protected ModularUI createUI(Player Player) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    public void update() {
        super.update();
    }

    public class CupolaFurnaceLogic extends PrimitiveRecipeLogic {

        public CupolaFurnaceLogic(RecipeMapPrimitiveMultiblockController BlockEntity,
                                  GTRecipeType<?> GTRecipeType) {
            super(BlockEntity, GTRecipeType);
        }

        public int getParallelLimit() {
            return ((MetaTileEntityCupolaFurnace) this.getMetaTileEntity()).size;
        }

        protected long getMaxParallelVoltage() {
            return 2147432767L;
        }

        public @NotNull ParallelLogicType getParallelLogicType() {
            return ParallelLogicType.MULTIPLY;
        }
    }
}
