package io.github.symmetricdevs.supersymmetry.common.world.gen;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPit extends WorldGenerator {

    protected static final BlockState AIR = Blocks.AIR.getDefaultState();

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        BlockState state = worldIn.getBlockState(position);
        int size = state.getBlock().getMetaFromState(state) + 1;
        worldIn.setBlockState(position, AIR, 2);
        BlockState biomeBlock = worldIn.getBiome(position).topBlock;

        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                if (x * x + z * z <= size * size * rand.nextFloat(0x.cp0f, 0x1.4p0f)) {
                    int top = worldIn.getHeight(position.getX() + x, position.getZ() + z);
                    if (top < 0x40) top = 0x40;
                    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(position.getX() + x, top,
                            position.getZ() + z);

                    for (int i = 0; i < 5; i++) {
                        worldIn.setBlockState(pos, AIR, 2);
                        pos.move(Direction.DOWN);
                    }

                    for (int i = 0; i < 0x20 && worldIn.getBlockState(pos) != MapGenLunarLavaTube.BASALT; i++) {
                        pos.move(Direction.DOWN);
                    }

                    int height = (int) (size * size * rand.nextFloat(0x.8p0f, 0x1.8p0f) / (x * x + z * z + size));
                    for (int i = 0; i < height; i++) {
                        pos.move(Direction.UP);
                        worldIn.setBlockState(pos, biomeBlock, 2);
                    }
                }
            }
        }
        return true;
    }
}
