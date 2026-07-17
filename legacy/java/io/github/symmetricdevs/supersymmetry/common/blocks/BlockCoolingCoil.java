package io.github.symmetricdevs.supersymmetry.common.blocks;

import static net.minecraft.world.level.material.Material.IRON;

import java.util.List;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.block.VariantActiveBlock;
import com.gregtechceu.gtceu.api.block.VariantItemBlock;
import com.gregtechceu.gtceu.api.unification.material.Material;
import io.github.symmetricdevs.supersymmetry.common.materials.SusyMaterials;

public class BlockCoolingCoil extends VariantActiveBlock<BlockCoolingCoil.CoolingCoilType> {

    public BlockCoolingCoil() {
        super(IRON);
        setTranslationKey("cooling_coil");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(CoolingCoilType.MANGANESE_IRON_ARSENIC_PHOSPHIDE));
    }

    @NotNull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack itemStack, @Nullable World worldIn, List<String> lines,
                               @NotNull ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, worldIn, lines, tooltipFlag);

        // noinspection rawtypes, unchecked
        VariantItemBlock itemBlock = (VariantItemBlock<CoolingCoilType, BlockCoolingCoil>) itemStack.getItem();
        BlockState stackState = itemBlock.getBlockState(itemStack);
        CoolingCoilType coolingCoilType = getState(stackState);

        lines.add(I18n.format("tile.cooling_coil.tooltip_temperature", coolingCoilType.coilTemperature));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull BlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull Mob.SpawnPlacementType type) {
        return false;
    }

    public enum CoolingCoilType implements IStringSerializable {

        MANGANESE_IRON_ARSENIC_PHOSPHIDE("manganese_iron_arsenic_phosphide", 160,
                SusyMaterials.ManganeseIronArsenicPhosphide),
        PRASEODYMIUM_NICKEL("praseodymium_nickel", 50, SusyMaterials.PraseodymiumNickel),
        GADOLINIUM_SILICON_GERMANIUM("gadolinium_silicon_germanium", 1, SusyMaterials.GadoliniumSiliconGermanium);

        public final String name;
        public final int coilTemperature;
        public final Material material;

        CoolingCoilType(String name, int coilTemperature, Material material) {
            this.name = name;
            this.coilTemperature = coilTemperature;
            this.material = material;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @NotNull
        @Override
        public String toString() {
            return getName();
        }
    }
}
