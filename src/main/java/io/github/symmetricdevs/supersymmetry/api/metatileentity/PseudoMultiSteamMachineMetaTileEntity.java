package io.github.symmetricdevs.supersymmetry.api.MetaMachine;

/**
 * Interface for steam-powered pseudo-multiblock machines.
 * Combines the pseudo-multiblock structure check with steam machine capabilities.
 */
public interface PseudoMultiSteamMachineMetaTileEntity extends PseudoMultiMachineMetaTileEntity {

    /**
     * @return true if this machine is weather or terrain resistant
     */
    default boolean isWeatherOrTerrainResistant() {
        return true;
    }
}
