package io.github.symmetricdevs.supersymmetry.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import io.github.symmetricdevs.supersymmetry.api.pattern.SuSyPredicates;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.jetbrains.annotations.NotNull;

/**
 * Eccentric Roll Crusher controller. Its pure pattern predicate records the component roll
 * positions and common metal-sheet variant; after formation this controller applies the legacy
 * gearbox-dependent roll facing and makes the item-import part look like that selected sheet.
 */
public class EccentricRollCrusherMachine extends WorkableElectricMultiblockMachine {

    private static final String SHEET_VARIANT_KEY = "EccentricRollCrusherMetalSheet";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            EccentricRollCrusherMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private boolean applyingRollOrientation;

    /** Registry name of the uniform sheet predicate's resolved block, saved and sent to clients. */
    @Persisted
    @DescSynced
    @UpdateListener(methodName = "onSelectedSheetChanged")
    private String selectedSheetId = "";

    public EccentricRollCrusherMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        if (applyingRollOrientation) {
            return;
        }

        super.onStructureFormed();
        captureSelectedSheet();
        applyRollOrientation();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        setSelectedSheetId("");
    }

    /**
     * Appearance consumed by the multiblock builder for the formed item-import part. A real
     * controller uses its saved server selection; XEI's formed dummy has no saved state, so it
     * reads the preview-local pattern selection. A malformed nonempty saved ID remains steel.
     */
    public BlockState getSelectedSheetAppearance() {
        if (selectedSheetId.isEmpty()) {
            Block previewSheet = SuSyPredicates.getChosenVariant(this, SHEET_VARIANT_KEY);
            return isAllowedSheet(previewSheet)
                    ? previewSheet.defaultBlockState()
                    : GTBlocks.CASING_STEEL_SOLID.get().defaultBlockState();
        }

        ResourceLocation id = ResourceLocation.tryParse(selectedSheetId);
        if (id == null) {
            return GTBlocks.CASING_STEEL_SOLID.get().defaultBlockState();
        }

        Block sheet = BuiltInRegistries.BLOCK.get(id);
        return isAllowedSheet(sheet) ? sheet.defaultBlockState() : GTBlocks.CASING_STEEL_SOLID.get().defaultBlockState();
    }

    private void captureSelectedSheet() {
        var level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        Block selected = SuSyPredicates.getChosenVariant(this, SHEET_VARIANT_KEY);
        ResourceLocation id = selected != null && isAllowedSheet(selected) ? BuiltInRegistries.BLOCK.getKey(selected) : null;
        setSelectedSheetId(id == null ? "" : id.toString());
    }

    private void setSelectedSheetId(String id) {
        if (selectedSheetId.equals(id)) {
            return;
        }
        selectedSheetId = id;
        markDirty("selectedSheetId");
    }

    private static boolean isAllowedSheet(Block block) {
        return GTBlocks.METAL_SHEETS.values().stream().anyMatch(entry -> entry.get() == block) ||
                GTBlocks.LARGE_METAL_SHEETS.values().stream().anyMatch(entry -> entry.get() == block);
    }

    /** Client-side: the controller sync changes a part appearance, so rerender all attached parts. */
    @SuppressWarnings("unused")
    private void onSelectedSheetChanged(String newValue, String oldValue) {
        var level = getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }
        for (IMultiPart part : getParts()) {
            part.self().scheduleRenderUpdate();
        }
    }

    private void applyRollOrientation() {
        var level = getLevel();
        if (level == null || level.isClientSide || !isFormed()) {
            return;
        }

        Direction front = getFrontFacing();
        Direction left = front.getCounterClockWise();
        Direction rollFacing = level.getBlockState(getPos().relative(left)).is(GTBlocks.CASING_STEEL_GEARBOX.get())
                ? front
                : front.getOpposite();

        applyingRollOrientation = true;
        try {
            for (long packedPos : SuSyPredicates.getEccentricRollPositions(this)) {
                BlockPos pos = BlockPos.of(packedPos);
                var state = level.getBlockState(pos);
                if (state.hasProperty(BlockStateProperties.FACING)
                        && state.getValue(BlockStateProperties.FACING) != rollFacing) {
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.FACING, rollFacing));
                }
            }
        } finally {
            applyingRollOrientation = false;
        }
    }
}
