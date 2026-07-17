package io.github.symmetricdevs.supersymmetry.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantActiveBlock;
import com.gregtechceu.gtceu.client.utils.BloomEffectUtil;


public class BlockElectrodeAssembly extends VariantActiveBlock<BlockElectrodeAssembly.ElectrodeAssemblyType> {

    public BlockElectrodeAssembly() {
        super(net.minecraft.world.level.material.Material.IRON);
        setTranslationKey("electrode_assembly");
        setHardness(3.0f);
        setResistance(3.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 1);
        setDefaultState(getState(ElectrodeAssemblyType.CARBON));
    }

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canRenderInLayer(@Nonnull BlockState state, @Nonnull BlockRenderLayer layer) {
        ElectrodeAssemblyType type = getState(state);
        if (type == ElectrodeAssemblyType.CARBON) {
            if (layer == BlockRenderLayer.SOLID) return true;
        } else if (layer == BlockRenderLayer.CUTOUT) return true;

        if (isBloomEnabled(type)) return layer == BloomEffectUtil.getEffectiveBloomLayer();
        return layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    protected boolean isBloomEnabled(ElectrodeAssemblyType value) {
        if (ConfigHolder.client.coilsActiveEmissiveTextures && value == ElectrodeAssemblyType.CARBON) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum ElectrodeAssemblyType implements IStringSerializable, IStateHarvestLevel {

        CARBON("carbon", 1);

        private final String name;
        private final int harvestLevel;

        ElectrodeAssemblyType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        public int getHarvestLevel(BlockState state) {
            return this.harvestLevel;
        }

        public String getHarvestTool(BlockState state) {
            return "wrench";
        }
    }
}
