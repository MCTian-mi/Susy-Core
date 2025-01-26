package supersymmetry.mixins.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import supersymmetry.datafix.util.BlockIDRemapper;

import java.io.File;

@Mixin(SaveFormatOld.class)
public class SaveFormatOldMixin {

    @Inject(method = "loadAndFix", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getCompoundTag(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;"))
    private static void lockUpOldRegistries(File file, DataFixer fixer, SaveHandler save, CallbackInfoReturnable<WorldInfo> cir, @Local NBTTagCompound exception) {
        BlockIDRemapper.onWorldLoad(save, exception);
    }
}
