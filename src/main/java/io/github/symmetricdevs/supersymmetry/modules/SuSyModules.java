package io.github.symmetricdevs.supersymmetry.modules;

import com.gregtechceu.gtceu.api.modules.IModuleContainer;
import com.gregtechceu.gtceu.api.modules.ModuleContainer;
import io.github.symmetricdevs.supersymmetry.Supersymmetry;

@ModuleContainer
public class SuSyModules implements IModuleContainer {

    public static final String MODULE_CORE = "susy_core";
    public static final String MODULE_BDSAndM = "susy_bdsandm_integration";
    public static final String MODULE_BAUBLES = "susy_baubles_integration";
    public static final String MODULE_TOP = "susy_top_integration";
    public static final String MODULE_JEI = "susy_jei_integration";
    public static final String MODULE_PYROTECH = "susy_pyrotech_integration";
    public static final String MODULE_LITTLETILES = "susy_littletiles_integration";

    @Override
    public String getID() {
        return Supersymmetry.MODID;
    }
}
