package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateWalletPacket {

    public final int walletAmount;
    public final int walletPos;

    public UpdateWalletPacket(int walletAmount, int walletPos) {
        this.walletAmount = walletAmount;
        this.walletPos = walletPos;
    }

    static void encode(UpdateWalletPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.walletAmount);
        buf.writeVarInt(msg.walletPos);
    }

    static UpdateWalletPacket decode(PacketBuffer buf) {
        return new UpdateWalletPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(final UpdateWalletPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        Container menu = Minecraft.getInstance().player.containerMenu;;
        if (menu instanceof WalletContainer) {
            ((WalletContainer) menu).update(msg);
        }
    }
}
