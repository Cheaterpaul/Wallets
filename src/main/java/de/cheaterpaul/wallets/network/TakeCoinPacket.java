package de.cheaterpaul.wallets.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public record TakeCoinPacket(CoinItem.CoinValue type, int amount) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MOD_ID, "take_coin");
    public static final Codec<TakeCoinPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    StringRepresentable.fromEnum(CoinItem.CoinValue::values).fieldOf("type").forGetter(TakeCoinPacket::type),
                    Codec.INT.fieldOf("amount").forGetter(TakeCoinPacket::amount)
            ).apply(inst, TakeCoinPacket::new)
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
