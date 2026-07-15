package io.github.symmetricdevs.supersymmetry.common.machine.storage;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.storage.DrumMachine;

/**
 * SuSy's plastic "can" — a {@link DrumMachine} whose material is a plastic
 * (polyethylene, polypropylene, PTFE, UHMWPE) rather than a metal. In 1.12.2
 * this was {@code MetaTileEntityPlasticCan}, which existed only to swap the drum
 * model/texture for {@code SusyTextures.PLASTIC_CAN}.
 * <p>
 * The behaviour is identical to a stock drum; the distinguishing feature is the
 * model. That is supplied by {@code SusyMachines.registerPlasticCan(...)} via a
 * SuSy {@code plastic_can} model (see Phase 6 for the real texture). This class
 * exists as a distinct type so the model factory and any can-specific behaviour
 * have a stable home.
 */
public class PlasticCanMachine extends DrumMachine {

    public PlasticCanMachine(IMachineBlockEntity holder, Material material, int capacity, Object... args) {
        super(holder, material, capacity, args);
    }
}
