package io.github.symmetricdevs.supersymmetry.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.network.IClientExecutor;
import com.gregtechceu.gtceu.api.network.IPacket;

public class SPacketRemoveFluidState implements IPacket, IClientExecutor {

    private BlockPos blockPosition;

    public SPacketRemoveFluidState() {}

    public SPacketRemoveFluidState(BlockPos pos) {
        this.blockPosition = pos;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void executeClient(NetHandlerPlayClient netHandlerPlayClient) {
        World world = Minecraft.getInstance().world;
        world.setBlockToAir(blockPosition);
    }

    @Override
    public void encode(FriendlyByteBuf FriendlyByteBuf) {
        FriendlyByteBuf.writeBlockPos(blockPosition);
    }

    @Override
    public void decode(FriendlyByteBuf FriendlyByteBuf) {
        this.blockPosition = FriendlyByteBuf.readBlockPos();
    }
}
