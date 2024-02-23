package de.cheaterpaul.wallets.network;

import com.mojang.serialization.Codec;
import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.client.ClientPayloadHandler;
import de.cheaterpaul.wallets.server.ServerPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ModPacketDispatcher {
    private static final String PROTOCOL_VERSION = Integer.toString(1);


    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlerEvent event) {
        registerPackets(event.registrar(REFERENCE.MOD_ID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(IPayloadRegistrar registrar) {
        registrar.play(InputEventPacket.ID, jsonReader(InputEventPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleInputEvent));
        registrar.play(TakeCoinPacket.ID, jsonReader(TakeCoinPacket.CODEC), handler -> handler.server(ServerPayloadHandler.getInstance()::handleTakeCoin));
        registrar.play(UpdateWalletPacket.ID, jsonReader(UpdateWalletPacket.CODEC), handler -> handler.client((p,l) -> ClientPayloadHandler.handleUpdateWalletPacket(p,l)));
    }

    protected static <T> FriendlyByteBuf.Reader<T> jsonReader(Codec<T> codec) {
        return buf -> buf.readJsonWithCodec(codec);
    }

}
