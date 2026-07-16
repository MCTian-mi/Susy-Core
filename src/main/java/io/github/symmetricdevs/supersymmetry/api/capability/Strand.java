package io.github.symmetricdevs.supersymmetry.api.capability;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.GTCEuAPI;

import net.minecraft.nbt.CompoundTag;

/**
 * Modern port of the 1.12.2 {@code Strand} data object.
 */
public class Strand {

    public double thickness;
    public double width;
    public boolean isCut;
    public Material material;
    public int temperature;

    public Strand(double thickness, double width, boolean isCut, Material material, int temperature) {
        this.thickness = thickness;
        this.width = width;
        this.isCut = isCut;
        this.material = material;
        this.temperature = temperature;
    }

    public Strand(Strand strand) {
        this.thickness = strand.thickness;
        this.width = strand.width;
        this.isCut = strand.isCut;
        this.material = strand.material;
        this.temperature = strand.temperature;
    }

    public static CompoundTag serialize(CompoundTag nbt, Strand strand) {
        if (strand == null) {
            return nbt;
        }
        nbt.putDouble("Thickness", strand.thickness);
        nbt.putDouble("Width", strand.width);
        nbt.putBoolean("IsCut", strand.isCut);
        nbt.putString("Material", strand.material.getName());
        nbt.putInt("Temperature", strand.temperature);
        return nbt;
    }

    public static Strand deserialize(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return null;
        }
        Material material = GTCEuAPI.materialManager.getMaterial(nbt.getString("Material"));
        if (material == null) {
            material = GTMaterials.NULL;
        }
        return new Strand(
                nbt.getDouble("Thickness"),
                nbt.getDouble("Width"),
                nbt.getBoolean("IsCut"),
                material,
                nbt.getInt("Temperature"));
    }
}
