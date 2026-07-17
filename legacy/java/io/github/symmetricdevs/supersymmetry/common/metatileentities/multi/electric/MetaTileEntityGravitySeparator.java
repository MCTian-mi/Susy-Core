package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static com.gregtechceu.gtceu.api.util.RelativeDirection.*;
import static io.github.symmetricdevs.supersymmetry.api.blocks.VariantHorizontalRotatableBlock.FACING;

import java.util.*;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.capability.GregtechDataCodes;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.items.materialitem.MetaPrefixItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.GTRecipeInput;
import com.gregtechceu.gtceu.api.unification.material.Material;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.CachedPatternRecipeMapMultiblock;
import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.particles.SusyParticleDust;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSeparatorRotor;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityGravitySeparator extends CachedPatternRecipeMapMultiblock {

    private static final int UPDATE_MATERIAL_COLOR = GregtechDataCodes.assignId();

    private static final String[][] ROTOR_PATTERN = { { "", "RRR", "", "" },
            { "", "", "", "", "RRR" } };
    private static final Vec3i PATTERN_OFFSET = new Vec3i(-1, 2, 1);

    private int[] particleColors;

    public MetaTileEntityGravitySeparator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.GRAVITY_SEPARATOR_RECIPES);
        this.recipeMapWorkable = new RecipeLogic(this) {

            @Override
            protected void setupRecipe(Recipe recipe) {
                super.setupRecipe(recipe);
                updateRenderInfo(recipe);
            }
        };
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        updateRenderInfo(recipeMapWorkable.getPreviousRecipe());
    }

    public void updateRenderInfo(Recipe recipe) {
        if (recipe == null) return;
        Stream<ItemStack> flattenedInputs = recipe.getInputs().stream().map(GTRecipeInput::getInputStacks)
                .map(Arrays::asList).flatMap(List::stream)
                .filter(stack -> MetaPrefixItem.tryGetMaterial(stack) != null);
        Stream<ItemStack> flattenedOutputs = recipe.getOutputs().stream();

        int[] materialColors = Stream.concat(flattenedInputs, flattenedOutputs)
                .map(stack -> Objects.requireNonNull(MetaPrefixItem.tryGetMaterial(stack)))
                .map(Material::getMaterialRGB).mapToInt(Integer::intValue).toArray();

        if (materialColors.length == 0) {
            this.writeCustomData(UPDATE_MATERIAL_COLOR, buf -> buf.writeVarIntArray(new int[] { 0xFFFFFF }));
            return;
        }
        this.writeCustomData(UPDATE_MATERIAL_COLOR, buf -> buf.writeVarIntArray(materialColors));
    }

    @Override
    protected String[][] getPattern() {
        return ROTOR_PATTERN;
    }

    @Override
    protected Vec3i getPatternOffset() {
        return PATTERN_OFFSET;
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityGravitySeparator(this.metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCYM
        TraceabilityPredicate casingPredicate = states(getCasingState()).setMinGlobalLimited(90);

        return FactoryBlockPattern.start(RIGHT, UP, FRONT)
                // front of R facing right side
                .aisle("C   C", "CC CC", "CFCFC", "CCSCC", " CCC ", " CCC ", "     ")
                .aisle("     ", " OOO ", "C###C", "RRRRR", "C###C", "C   C", "     ")
                .aisle("C   C", "C   C", "MCCCM", "C###C", "RRRRR", "C   C", "     ")
                .aisle("C   C", "C   C", "ECCCE", "RRRRR", "C###C", "C###C", "     ")
                .aisle("     ", "     ", "C   C", "CCCCC", "RRRRR", "C###C", "C###C")
                .aisle("     ", "C   C", "C   C", " CCC ", "C###C", "RRRRR", "J###J")
                .aisle("C   C", "CC CC", "CCCCC", " CCC ", " CCC ", "CCCCC", "CIIIC")
                /*
                 * Other orientation
                 * .aisle("C CC  C", "C CC CC", "CCCECCC", "CRCRC  ", " CRCRC ", " CCCCRC", "    CJC")
                 * .aisle("       ", "CO    C", "F#CC  C", "CR#RCCC", "C#R#R#C", "C  ##RC", "    ##I")
                 * .aisle("       ", " O    C", "C#CC  C", "CR#RCCC", "C#R#R#C", "C  ##RC", "    ##I")
                 * .aisle("       ", "CO    C", "F#CC  C", "CR#RCCC", "C#R#R#C", "C  ##RC", "    ##I")
                 * .aisle("C CC  C", "C CC CC", "CCCSCCC", "CRCRC  ", " CRCRC ", " CCCCRC", "    CJC")
                 */.where('S', selfPredicate()).where('R', rotorOrientation()).where('C', casingPredicate)
                .where('M', casingPredicate.or(autoAbilities(true, false)))
                .where('E', casingPredicate.or(autoAbilities(true, false, false, false, false, false, false)))
                .where('I', casingPredicate.or(autoAbilities(false, false, true, false, false, false, false)))
                .where('O', casingPredicate.or(autoAbilities(false, false, false, true, false, false, false)))
                .where('J', casingPredicate.or(autoAbilities(false, false, false, false, true, false, false)))
                .where('F', casingPredicate.or(autoAbilities(false, false, false, false, false, true, false)))
                .where('#', air()).where(' ', any()).build();
    }

    /*
     * can be reimplemented with states for R if rotation is not supposed to be specified
     * public BlockState[] getRotorStates() {
     * return new BlockState[] {
     * SuSyBlocks.SEPARATOR_ROTOR.getState(BlockSeparatorRotor.BlockSeparatorRotorType.STEEL).withProperty(FACING,
     * Direction.SOUTH),
     * SuSyBlocks.SEPARATOR_ROTOR.getState(BlockSeparatorRotor.BlockSeparatorRotorType.STEEL).withProperty(FACING,
     * Direction.NORTH),
     * SuSyBlocks.SEPARATOR_ROTOR.getState(BlockSeparatorRotor.BlockSeparatorRotorType.STEEL).withProperty(FACING,
     * Direction.EAST),
     * SuSyBlocks.SEPARATOR_ROTOR.getState(BlockSeparatorRotor.BlockSeparatorRotorType.STEEL).withProperty(FACING,
     * Direction.WEST)
     * };
     * }
     */

    // makes sure block at position is properly oriented rotor
    protected TraceabilityPredicate rotorOrientation() {
        // makes sure rotor's front faces the left side (relative to the player) of controller front
        return SuSyPredicates.horizontalOrientation(this, steelRotorState(), RelativeDirection.RIGHT, FACING);
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    protected BlockState steelRotorState() {
        return SuSyBlocks.SEPARATOR_ROTOR.getState(BlockSeparatorRotor.BlockSeparatorRotorType.STEEL);
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Nonnull
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    private static final float PARTICLE_SPEED = .05F;

    @Override
    public void update() {
        super.update();
        if (this.isActive() && getWorld().isRemote && this.particleColors != null)
            createParticles();
    }

    @SideOnly(Side.CLIENT)
    private void createParticles() {
        Random rand = getWorld().rand;
        if (cachedPattern == null || cachedPattern.length == 0)
            generateCachedPattern(getPattern(), getPatternOffset(), this.frontFacing, isFlipped());
        for (Vec3i offset : cachedPattern) {
            BlockPos pos = this.getPos().add(offset);

            // Lots of particles, not sure how performant this is
            Minecraft.getInstance().effectRenderer.addEffect(new SusyParticleDust(getWorld(),
                    pos.getX() + rand.nextDouble(),
                    pos.getY() + .5F / 16,
                    pos.getZ() + rand.nextDouble(),
                    PARTICLE_SPEED * this.getFrontFacing().getXOffset(), 0,
                    PARTICLE_SPEED * this.getFrontFacing().getZOffset(), 1,
                    3F, particleColors[Math.abs(rand.nextInt() % particleColors.length)]));
        }
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == UPDATE_MATERIAL_COLOR) {
            this.particleColors = buf.readVarIntArray();
        }
        super.receiveCustomData(dataId, buf);
    }
}
