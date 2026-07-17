package io.github.symmetricdevs.supersymmetry.common.metatileentities.multi.primitive;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.ItemHandlerList;
import com.gregtechceu.gtceu.api.gui.ModularUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.IMultiblockPart;
import com.gregtechceu.gtceu.api.machine.multiblock.RecipeMapPrimitiveMultiblockController;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.client.particle.VanillaParticleEffects;
import com.gregtechceu.gtceu.client.renderer.CubeRendererState;
import com.gregtechceu.gtceu.client.renderer.GTCEuBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.cclop.ColourOperation;
import com.gregtechceu.gtceu.client.renderer.cclop.LightMapOperation;
import com.gregtechceu.gtceu.client.renderer.texture.Textures;

import io.github.symmetricdevs.supersymmetry.api.MetaMachine.multiblock.SuSyMultiblockAbilities;
import io.github.symmetricdevs.supersymmetry.api.recipes.SuSyRecipeMaps;
import io.github.symmetricdevs.supersymmetry.client.renderer.textures.SusyTextures;

public class MetaTileEntityPrimitiveSmelter extends RecipeMapPrimitiveMultiblockController {

    private static final TraceabilityPredicate SNOW_PREDICATE = new TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));

    public MetaTileEntityPrimitiveSmelter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, SuSyRecipeMaps.PRIMITIVE_SMELTER);
    }

    @Override
    protected void initializeAbilities() {
        this.importItems = new ItemHandlerList(getAbilities(SuSyMultiblockAbilities.PRIMITIVE_IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(SuSyMultiblockAbilities.PRIMITIVE_EXPORT_ITEMS));
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.initializeAbilities();
    }

    public static BlockState getCasingState() {
        return ModuleCore.Blocks.MASONRY_BRICK.getDefaultState();
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("BBB", "BBB", "SBS")
                .aisle("BBB", "B#B", "B B")
                .aisle("BBB", "BCB", "SBS")
                .where('B', states(getCasingState()).setMinGlobalLimited(14)
                        .or(abilities(SuSyMultiblockAbilities.PRIMITIVE_IMPORT_ITEMS).setPreviewCount(1))
                        .or(abilities(SuSyMultiblockAbilities.PRIMITIVE_EXPORT_ITEMS).setPreviewCount(1)))
                .where('C', selfPredicate())
                .where('S', states(ModuleCore.Blocks.MASONRY_BRICK_SLAB.getDefaultState()))
                .where('#', air().or(SNOW_PREDICATE))
                .where(' ', air())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return SusyTextures.MASONRY_BRICK;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityPrimitiveSmelter(this.metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return SusyTextures.PRIMITIVE_SMELTER_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
        if (recipeMapWorkable.isActive() && isStructureFormed()) {
            Direction back = getFrontFacing().getOpposite();
            Matrix4 offset = translation.copy().translate(back.getXOffset(), 0.2, back.getZOffset());
            CubeRendererState op = Textures.RENDER_STATE.get();
            Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world));
            SusyTextures.SLAG_HOT.renderSided(Direction.UP, renderState, offset,
                    ArrayUtils.addAll(pipeline, new LightMapOperation(240, 240), new ColourOperation(0xFFFFFFFF)));
            Textures.RENDER_STATE.set(op);
        }
    }

    @Override
    public void update() {
        super.update();

        if (this.isActive() && !getWorld().isRemote) {
            damageEntitiesAndBreakSnow();
        }
    }

    private void damageEntitiesAndBreakSnow() {
        BlockPos middlePos = this.getPos();
        middlePos = middlePos.offset(getFrontFacing().getOpposite());
        this.getWorld().getEntitiesWithinAABB(LivingEntity.class, new AABB(middlePos))
                .forEach(entity -> entity.attackEntityFrom(DamageSource.LAVA, 3.0f));

        if (getOffsetTimer() % 10 == 0) {
            BlockState state = getWorld().getBlockState(middlePos);
            GTUtility.tryBreakSnow(getWorld(), middlePos, state, true);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            VanillaParticleEffects.defaultFrontEffect(this, 0.3F, EnumParticleTypes.SMOKE_LARGE,
                    EnumParticleTypes.FLAME);

            BlockPos pos = getPos();

            Direction facing = getFrontFacing().getOpposite();
            float x = (float) facing.getXOffset() + (float) pos.getX() + 0.5F;
            float y = (float) facing.getYOffset() + (float) pos.getY() + 1.2F +
                    (float) (GTValues.RNG.nextDouble() * 2.0 / 16.0);;
            float z = (float) facing.getZOffset() + (float) pos.getZ() + 0.5F;

            for (int i = 0; i < 4; i++) {
                double offsetX = (GTValues.RNG.nextDouble() * 2.0 - 1.0) * 0.3;
                double offsetY = (GTValues.RNG.nextDouble() * 2.0 - 1.0) * 0.3;
                double offsetZ = (GTValues.RNG.nextDouble() * 2.0 - 1.0) * 0.3;
                getWorld().spawnParticle(EnumParticleTypes.FLAME, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);

                if (GTValues.RNG.nextDouble() < 0.05) {
                    getWorld().spawnParticle(EnumParticleTypes.LAVA, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0,
                            0.0);
                }
            }

            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                getWorld().playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
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
    public void clearMachineInventory(NonNullList<ItemStack> itemBuffer) {
        // Don't do anything since exportItems/importItems are proxies
        // This is called when the machine block is broken
        // The other way of fixing this could be writing a custom recipe workable,
        // which redirects getInputInventory() and getOutputInventory()
        // just like what RecipeLogic did.
    }

    @Override
    public String getHarvestTool() {
        return "pickaxe";
    }
}
