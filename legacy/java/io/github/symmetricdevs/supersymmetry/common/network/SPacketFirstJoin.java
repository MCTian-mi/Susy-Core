package io.github.symmetricdevs.supersymmetry.common.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.network.IClientExecutor;
import com.gregtechceu.gtceu.api.network.IPacket;
import io.github.symmetricdevs.supersymmetry.client.ClientProxy;

public class SPacketFirstJoin implements IPacket, IClientExecutor {

    public SPacketFirstJoin() {}

    @Override
    @SideOnly(Side.CLIENT)
    public void executeClient(NetHandlerPlayClient netHandlerPlayClient) {
        ClientProxy.titleRenderTimer = 0;
    }

    @Override
    public void encode(FriendlyByteBuf FriendlyByteBuf) {}

    @Override
    public void decode(FriendlyByteBuf FriendlyByteBuf) {}
}
