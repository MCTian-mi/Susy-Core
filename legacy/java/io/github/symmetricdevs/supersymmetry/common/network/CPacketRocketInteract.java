package io.github.symmetricdevs.supersymmetry.common.network;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.gregtechceu.gtceu.api.network.IPacket;
import com.gregtechceu.gtceu.api.network.IServerExecutor;

/**
 * A variation on CPacketUseEntity for rockets which skips the distance check.
 */
public class CPacketRocketInteract implements IPacket, IServerExecutor {

    private int entityId;
    private Vec3 hitVec;
    private InteractionHand hand;

    public CPacketRocketInteract() {}

    @SideOnly(Side.CLIENT)
    public CPacketRocketInteract(Entity entityIn, InteractionHand handIn, Vec3 hitVecIn) {
        this.entityId = entityIn.getEntityId();
        this.hand = handIn;
        this.hitVec = hitVecIn;
    }

    @Override
    public void executeServer(NetHandlerPlayServer handler) {
        Entity entity = handler.player.getEntityWorld().getEntityByID(this.entityId);
        if (entity == null) {
            return;
        }
        if (handler.player.getDistanceSq(entity) < 1600) { // Much more reasonable :trollface:
            if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(handler.player, entity, hitVec, hand) != null)
                return;
            entity.applyPlayerInteraction(handler.player, hitVec, hand);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);

        buf.writeFloat((float) this.hitVec.x);
        buf.writeFloat((float) this.hitVec.y);
        buf.writeFloat((float) this.hitVec.z);

        buf.writeEnumValue(this.hand);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();

        this.hitVec = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());

        this.hand = buf.readEnumValue(InteractionHand.class);
    }

    @Nullable
    public Entity getEntityFromWorld(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }
}
