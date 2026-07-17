package io.github.symmetricdevs.supersymmetry.mixins.gregtech;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.IMachine;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import io.github.symmetricdevs.supersymmetry.api.SusyLog;

/**
 * Migration mixin: intercepts NBT load on MetaMachineBlockEntity to
 * migrate old "gregtech:" machine IDs to "susy:" IDs.
 * <p>
 * In 1.12.2 this targeted MetaTileEntityHolder; the modern equivalent
 * is MetaMachineBlockEntity.
 */
@Mixin(MetaMachineBlockEntity.class)
public abstract class MetaTileEntityHolderMixin {

    @Inject(method = "loadMachineData",
            remap = true,
            at = @At("HEAD"),
            cancellable = true)
    private void checkSuSy(CompoundTag compound, CallbackInfo ci) {
        if (!compound.contains("metaTileEntityId")) return;

        String metaTileEntityIdRaw = compound.getString("metaTileEntityId");
        if (metaTileEntityIdRaw == null || metaTileEntityIdRaw.isEmpty()) return;

        if (metaTileEntityIdRaw.startsWith("gregtech:")) {
            String susyName = metaTileEntityIdRaw.replace("gregtech:", "supersymmetry:");
            ResourceLocation susyId = ResourceLocation.tryParse(susyName);
            if (susyId != null && GTRegistries.MACHINES.containsKey(susyId)) {
                compound.putString("metaTileEntityId", susyName);
                SusyLog.logger.debug("Successfully migrated SuSy MetaTileEntity with ID {}", susyName);
            }
        }
    }
}
