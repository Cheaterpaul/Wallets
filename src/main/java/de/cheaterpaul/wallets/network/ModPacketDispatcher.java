package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketDispatcher {
    protected final SimpleChannel dispatcher;
    private byte packetId = 0;

    private static final String PROTOCOL_VERSION = Integer.toString(1);


    public ModPacketDispatcher() {
        this.dispatcher = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(REFERENCE.MOD_ID, "main")).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();
    }

    protected int nextID() {
        return packetId++;
    }

    public void registerPackets() {
        dispatcher.registerMessage(nextID(), InputEventPacket.class, InputEventPacket::encode, InputEventPacket::decode, InputEventPacket::handle);
        dispatcher.registerMessage(nextID(), TakeCoinPacket.class, TakeCoinPacket::encode, TakeCoinPacket::decode, TakeCoinPacket::handle);
        dispatcher.registerMessage(nextID(), UpdateWalletPacket.class, UpdateWalletPacket::encode, UpdateWalletPacket::decode, UpdateWalletPacket::handle);
    }

    public void sentToServer(InputEventPacket packet){
        this.dispatcher.sendToServer(packet);
    }
    public void sentToServer(TakeCoinPacket packet){
        this.dispatcher.sendToServer(packet);
    }
    public void sentToPlayer(UpdateWalletPacket packet, ServerPlayer player){
        this.dispatcher.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
