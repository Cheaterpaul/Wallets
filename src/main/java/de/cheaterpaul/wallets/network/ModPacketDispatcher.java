package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
    }

    public void sentToServer(InputEventPacket packet){
        this.dispatcher.sendToServer(packet);
    }
}
