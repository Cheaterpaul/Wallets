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

public record InputEventPacket(Action action, String param) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MOD_ID, "input_event");
    public static final Codec<InputEventPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    StringRepresentable.fromEnum(Action::values).fieldOf("action").forGetter(InputEventPacket::action),
                    Codec.STRING.fieldOf("param").forGetter(InputEventPacket::param)
            ).apply(inst, InputEventPacket::new
            ));


    public enum Action implements StringRepresentable {
        INSERT_COIN("insert_coin"),
        TAKE_COIN("take_coin"),
        TAKE_COINS("take_coins"),
        CREATE_POUCH("create_pouch"),
        UPDATE_WALLET("update_wallet"),
        UPDATE_WALLET_TAKE("update_wallet_take");

        private final String serializedName;

        Action(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.serializedName;
        }
    }

    public InputEventPacket(Action action) {
        this(action, "");
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
