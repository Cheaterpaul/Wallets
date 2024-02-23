package de.cheaterpaul.wallets.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record UpdateWalletPacket(int walletAmount, int walletPos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MOD_ID, "update_wallet");
    public static final Codec<UpdateWalletPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf("walletAmount").forGetter(UpdateWalletPacket::walletAmount),
                    Codec.INT.fieldOf("walletPos").forGetter(UpdateWalletPacket::walletPos)
            ).apply(inst, UpdateWalletPacket::new)
    );

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
