package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.rocket;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.capability.ItemHandlerList;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockSuSyMultiblockCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityLander;

public class MetaTileEntityLandingPad extends MultiblockControllerMachine {

    private AABB landingAreaBB;
    protected IItemHandlerModifiable outputInventory;

    public MetaTileEntityLandingPad(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityLandingPad(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        setStructureAABB();
    }

    protected void initializeAbilities() {
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
    }

    @Override
    protected void updateFormedValid() {
        EntityLander lander = getLander();
        if (lander != null && !lander.isEmpty()) {
            GTTransferUtils.moveInventoryItems(lander.getInventory(), this.outputInventory);
            if (this.isBlockRedstonePowered()) {
                lander.setLaunched(true);
            }
        }
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    protected static BlockState getPadState() {
        return SuSyBlocks.MULTIBLOCK_CASING.getState(BlockSuSyMultiblockCasing.CasingType.HEAVY_DUTY_PAD);
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    public TraceabilityPredicate getAbilityPredicate() {
        TraceabilityPredicate predicate = super.autoAbilities(true, false);
        predicate.or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2)
                .setPreviewCount(1));
        predicate.or(abilities(MultiblockAbility.EXPORT_ITEMS).setPreviewCount(1));
        predicate.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1));
        return predicate;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive(), true);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("     CCCCC     ", "      CCC      ", "      CCC      ")
                .aisle("   CCPPPPPCC   ", "     PPPPP     ", "     AAAAA     ")
                .aisle("  CPPPPPPPPPC  ", "   PPPPPPPPP   ", "   AAAAAAAAA   ")
                .aisle(" CPPPPPPPPPPPC ", "  PPPPPPPPPPP  ", "  AAAAAAAAAAA  ")
                .aisle(" CPPPPPPPPPPPC ", "  PPPPPPPPPPP  ", "  AAAAAAAAAAA  ")
                .aisle("CPPPPPPPPPPPPPC", " PPPPPPPPPPPPP ", " AAAAAAAAAAAAA ")
                .aisle("CPPPPPPPPPPPPPC", "CPPPPPPPPPPPPPC", "CAAAAAAAAAAAAAC")
                .aisle("CPPPPPPPPPPPPPC", "CPPPPPPPPPPPPPC", "CAAAAAAAAAAAAAC")
                .aisle("CPPPPPPPPPPPPPC", "CPPPPPPPPPPPPPC", "CAAAAAAAAAAAAAC")
                .aisle("CPPPPPPPPPPPPPC", " PPPPPPPPPPPPP ", " AAAAAAAAAAAAA ")
                .aisle(" CPPPPPPPPPPPC ", "  PPPPPPPPPPP  ", "  AAAAAAAAAAA  ")
                .aisle(" CPPPPPPPPPPPC ", "  PPPPPPPPPPP  ", "  AAAAAAAAAAA  ")
                .aisle("  CPPPPPPPPPC  ", "   PPPPPPPPP   ", "   AAAAAAAAA   ")
                .aisle("   CCPPPPPCC   ", "     PPPPP     ", "     AAAAA     ")
                .aisle("     CCSCC     ", "      CCC      ", "      CCC      ")
                .where(' ', any())
                .where('A', air())
                .where('S', selfPredicate())
                .where('C', states(getCasingState()).setMinGlobalLimited(6).or(getAbilityPredicate()))
                .where('P', states(getPadState()))
                .build();
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return true;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    public EntityLander getLander() {
        for (EntityLander entity : this.getWorld().getEntitiesWithinAABB(EntityLander.class,
                this.landingAreaBB)) {
            if (entity.onGround) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    public void setStructureAABB() {
        Direction facing = this.getFrontFacing();
        BlockPos controllerPos = this.getPos();

        BlockPos padCenter = controllerPos.offset(facing.getOpposite(), 7);

        Direction right = facing.rotateY();
        Direction left = facing.rotateYCCW();

        // Only accept the Y layer directly on top of the landing pad
        BlockPos corner1 = padCenter.offset(left, 7).offset(facing, 6).offset(Direction.UP, 1);
        BlockPos corner2 = padCenter.offset(right, 7).offset(facing.getOpposite(), 6).offset(Direction.UP, 2);

        // Create the bounding box
        this.landingAreaBB = new AABB(corner1, corner2);
    }
}
