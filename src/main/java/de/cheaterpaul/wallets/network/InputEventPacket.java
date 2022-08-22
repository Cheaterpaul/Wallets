package de.cheaterpaul.wallets.network;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InputEventPacket {
    public static final String INSERT_COIN = "ic";
    public static final String TAKE_COIN = "tc";
    public static final String TAKE_COINS = "tcs";
    public static final String CREATE_POUCH = "cp";
    public static final String UPDATE_WALLET = "uw";
    public static final String UPDATE_WALLET_TAKE = "uwt";

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

    static void encode(InputEventPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.action + SPLIT + msg.param);
    }

    static InputEventPacket decode(FriendlyByteBuf buf) {
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
        ServerPlayer player = ctx.getSender();
        ctx.enqueueWork(() -> {
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof WalletContainer) {
                switch (msg.action) {
                    case INSERT_COIN -> ((WalletContainer) menu).insertCoin();
                    case TAKE_COIN -> ((WalletContainer) menu).takeCoin(CoinItem.CoinValue.valueOf(msg.param));
                    case TAKE_COINS -> ((WalletContainer) menu).takeCoins(Integer.parseInt(msg.param));
                    case CREATE_POUCH -> ((WalletContainer) menu).createPouch(Integer.parseInt(msg.param));
                }
            }
        });
        ctx.setPacketHandled(true);
    }

}
