package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TakeCoinPacket {

    private final CoinItem.CoinValue type;
    private final int amount;

    public TakeCoinPacket(CoinItem.CoinValue type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    static void encode(TakeCoinPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.type.ordinal());
        buf.writeInt(msg.amount);
    }

    static TakeCoinPacket decode(PacketBuffer buf) {
        return new TakeCoinPacket(CoinItem.CoinValue.values()[buf.readInt()], buf.readInt());
    }

    public static void handle(final TakeCoinPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Container menu = player.containerMenu;
        if (menu instanceof WalletContainer) {
            ((WalletContainer) menu).takeCoin(msg.type, msg.amount);
        }
    }
}
