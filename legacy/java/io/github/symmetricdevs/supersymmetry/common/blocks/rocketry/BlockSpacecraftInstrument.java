package io.github.symmetricdevs.supersymmetry.common.blocks.rocketry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.StringRepresentable;

import com.gregtechceu.gtceu.api.block.IStateHarvestLevel;
import com.gregtechceu.gtceu.api.block.VariantBlock;
import io.github.symmetricdevs.supersymmetry.api.rocketry.components.Instrument;
import io.github.symmetricdevs.supersymmetry.common.entities.EntityAbstractRocket;
import io.github.symmetricdevs.supersymmetry.common.rocketry.instruments.InstrumentLander;
import io.github.symmetricdevs.supersymmetry.common.rocketry.instruments.InstrumentRobotArm;

public class BlockSpacecraftInstrument extends VariantBlock<BlockSpacecraftInstrument.Type> {

    public BlockSpacecraftInstrument() {
        super(Material.IRON);
        setTranslationKey("spacecraft_instrument");
        setHardness(5f);
        setResistance(15f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(Type.FLIGHT_COMPUTER));
        setHarvestLevel("wrench", 4);
    }

    public enum Type implements IStringSerializable, IStateHarvestLevel {

        SENSOR_ARRAY("sensors", 4),
        COLLECTOR("collector", 4),
        CAMERA("position", 4),
        FLIGHT_COMPUTER("computer", 4),
        ENGINE("engine", 4),
        SOLAR_PANEL("solar_panel", 4),
        BATTERY("battery", 4),
        ARM("arm", 4, new InstrumentRobotArm()),
        LANDER("lander", 4, new InstrumentLander()); // will have variable purposes

        public String name;
        public int h;
        public Instrument instrument;

        Type(String name, int h) {
            this(name, h, null);
        }

        Type(String name, int h, Instrument instrument) {
            this.name = name;
            this.h = h;
            this.instrument = instrument;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getHarvestLevel(BlockState BlockState) {
            return h;
        }

        @Override
        public String getHarvestTool(BlockState state) {
            return "wrench";
        }

        public void act(int count, EntityAbstractRocket rocket) {
            if (instrument != null) instrument.act(count, rocket);
        }
    }
}
