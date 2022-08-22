package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TakeCoinPacket {

    private final CoinItem.CoinValue type;
    private final int amount;

    public TakeCoinPacket(CoinItem.CoinValue type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    static void encode(TakeCoinPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.type.ordinal());
        buf.writeInt(msg.amount);
    }

    static TakeCoinPacket decode(FriendlyByteBuf buf) {
        return new TakeCoinPacket(CoinItem.CoinValue.values()[buf.readInt()], buf.readInt());
    }

    public static void handle(final TakeCoinPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof WalletContainer) {
                ((WalletContainer) menu).takeCoin(msg.type, msg.amount);
            }
        });
        ctx.setPacketHandled(true);
    }
}
