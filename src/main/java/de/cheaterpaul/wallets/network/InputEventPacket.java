package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class InputEventPacket {
    public static final String INSERT_COIN = "ic";
    public static final String TAKE_COIN = "tc";
    public static final String TAKE_COINS = "tcs";

    private static final String SPLIT = "&";

    private String action;
    private String param;

    public InputEventPacket() {
        this("","");
    }
    public InputEventPacket(String action) {
        this(action, "");
    }

    public InputEventPacket(String action, String param) {
        this.action = action;
        this.param = param;
    }

    static void encode(InputEventPacket msg, PacketBuffer buf) {
        buf.writeUtf(msg.action + SPLIT + msg.param);
    }

    static InputEventPacket decode(PacketBuffer buf) {
        String[] s = buf.readUtf(50).split(SPLIT);
        InputEventPacket msg = new InputEventPacket();
        msg.action = s[0];
        if (s.length > 1) {
            msg.param = s[1];
        } else {
            msg.param = "";
        }
        return msg;
    }

    public static void handle(final InputEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ServerPlayerEntity player = ctx.getSender();
        Container menu = player.containerMenu;
        if (menu instanceof WalletContainer) {
            switch (msg.action) {
                case INSERT_COIN:
                    ((WalletContainer) menu).insertCoin();
                    break;
                case TAKE_COIN:
                    ((WalletContainer) menu).takeCoin(CoinItem.CoinValue.valueOf(msg.param));
                    break;
                case TAKE_COINS:
                    ((WalletContainer) menu).takeCoins(Integer.parseInt(msg.param));
            }
        }
    }

}
