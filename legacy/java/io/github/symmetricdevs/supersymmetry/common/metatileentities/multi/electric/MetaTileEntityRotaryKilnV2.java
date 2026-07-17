package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.electric;

import static io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates.hiddenGearTooth;
import static io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyPredicates.hiddenStates;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;
import com.gregtechceu.gtceu.common.block.CasingBlock.MetalCasingType;
import com.gregtechceu.gtceu.common.blocks.BlockTurbineCasing;
import com.gregtechceu.gtceu.common.data.GTMachines;
import io.github.symmetricdevs.supersymmetry.api.capability.SuSyDataCodes;
import io.github.symmetricdevs.supersymmetry.api.metatileentity.IAnimatableMTE;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;
import io.github.symmetricdevs.supersymmetry.common.blocks.BlockGrinderCasing;
import io.github.symmetricdevs.supersymmetry.common.blocks.SuSyBlocks;

public class MetaTileEntityRotaryKilnV2 extends WorkableElectricMultiblockMachine implements IAnimatableMTE {

    @SideOnly(Side.CLIENT)
    private BlockPos lightPos;
    @SideOnly(Side.CLIENT)
    private Vec3i transformation;
    @SideOnly(Side.CLIENT)
    private AABB renderBounding;

    @Nullable
    private Collection<BlockPos> hiddenBlocks;

    public MetaTileEntityRotaryKilnV2(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.ROTARY_KILN);
    }

    protected static BlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    private static BlockState getShellCasingState() {
        return SuSyBlocks.GRINDER_CASING.getState(BlockGrinderCasing.Type.WEAR_RESISTANT_LINED_MILL_SHELL);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity BlockEntity) {
        return new MetaTileEntityRotaryKilnV2(this.metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        // Different characters use common constraints. Copied from GCyM
        TraceabilityPredicate casingPredicate = states(getCasingState()).setMinGlobalLimited(6);
        TraceabilityPredicate maintenance = abilities(MultiblockAbility.MAINTENANCE_HATCH).setMinGlobalLimited(1)
                .setMaxGlobalLimited(1);

        return FactoryBlockPattern.start()
                .aisle("F         F", "LD   B   DR", "LDCCCBCCCDR", "GD   B   DG")
                .aisle("     F     ", "LDCCCBCCCDR", "L#########R", "LDCCCBCCCDR")
                .aisle("F         F", "LD   B   DR", "LDCCCSCCCDR", "GD   B   DG")
                .where('S', selfPredicate())
                .where('B', hiddenStates(getCasingState()))
                .where('C', hiddenStates(getShellCasingState()))
                .where('D', hiddenGearTooth(
                        RelativeDirection.LEFT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), false)
                                .getAxis()))
                .where('F', frames(Materials.Steel))
                .where('G',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('L', casingPredicate
                        .or(autoAbilities(false, false, true, false, false, true, false))
                        .or(autoAbilities(true, false, false, false, false, false, false)).setMinGlobalLimited(0)
                        .or(maintenance))
                .where('R', casingPredicate
                        .or(autoAbilities(false, false, false, true, true, false, false))
                        .or(autoAbilities(true, false, false, false, false, false, false)).setMinGlobalLimited(0)
                        .or(maintenance))
                .where(' ', any())
                .where('#', air())
                .build();
    }

    @Override
    @Nullable
    public Collection<BlockPos> getHiddenBlocks() {
        return hiddenBlocks;
    }

    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    protected ICubeRenderer getFrontOverlay() {
        return SusyTextures.ROTARY_KILN_OVERLAY;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.hiddenBlocks = context.getOrDefault("Hidden", new ArrayList<>());
        World world = getWorld();

        // This will only be called on a server side world
        // so actually no need to check !world.isRemote
        if (world != null && !world.isRemote) {
            disableBlockRendering(true);
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(SuSyDataCodes.RESET_RENDER_FIELDS, buf -> {
                /* Do nothing */
            });
            disableBlockRendering(false);
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        World world = getWorld();
        if (world != null && !world.isRemote) {
            disableBlockRendering(isStructureFormed()); // This is a bit ugly tho...
        }
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == SuSyDataCodes.RESET_RENDER_FIELDS) {
            this.lightPos = null;
            this.renderBounding = null;
            this.transformation = null;
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        if (this.renderBounding == null) {
            Direction front = getFrontFacing();
            Direction upwards = getUpwardsFacing();
            boolean flipped = isFlipped();
            Direction left = RelativeDirection.LEFT.getRelativeFacing(front, upwards, flipped);
            Direction up = RelativeDirection.UP.getRelativeFacing(front, upwards, flipped);
            BlockPos pos = getPos();

            var v1 = pos.offset(left.getOpposite(), 7).offset(up.getOpposite(), 3);
            var v2 = pos.offset(left, 7).offset(up, 4).offset(front.getOpposite(), 4);
            this.renderBounding = new AABB(v1, v2);
        }
        return renderBounding;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3i getTransformation() {
        if (this.transformation == null) {
            Direction front = getFrontFacing();
            Direction upwards = getUpwardsFacing();
            boolean flipped = isFlipped();
            Direction back = front.getOpposite();
            Direction up = RelativeDirection.UP.getRelativeFacing(front, upwards, flipped);

            int xOff = back.getXOffset() - up.getXOffset();
            int yOff = back.getYOffset() - up.getYOffset();
            int zOff = back.getZOffset() - up.getZOffset();

            this.transformation = new Vec3i(xOff, yOff, zOff);
        }
        return transformation;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockPos getLightPos() {
        if (this.lightPos == null) {
            Direction front = getFrontFacing();
            Direction upwards = getUpwardsFacing();
            boolean flipped = isFlipped();
            Direction back = front.getOpposite();
            Direction up = RelativeDirection.UP.getRelativeFacing(front, upwards, flipped);

            this.lightPos = getPos().offset(up, 2).offset(back, 1);
        }
        return lightPos;
    }

    @SideOnly(Side.CLIENT)
    private <T extends MetaTileEntity> void renderGeoModel(T mte, double x, double y, double z, float partialTicks) {
        // GeckoLib 4 rendering will be added here
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (isStructureFormed()) {
            renderGeoModel(this, x, y, z, partialTicks);
        }
    }
}
