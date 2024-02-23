package de.cheaterpaul.wallets.server;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.network.InputEventPacket;
import de.cheaterpaul.wallets.network.TakeCoinPacket;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleInputEvent(InputEventPacket inputEventPacket, PlayPayloadContext playPayloadContext) {
        playPayloadContext.workHandler().execute(() -> {
            playPayloadContext.player().map(s -> s.containerMenu).filter(WalletContainer.class::isInstance).map(WalletContainer.class::cast).ifPresent(menu -> {
                switch (inputEventPacket.action()) {
                    case INSERT_COIN -> menu.insertCoin();
                    case TAKE_COIN -> menu.takeCoin(CoinItem.CoinValue.valueOf(inputEventPacket.param()));
                    case TAKE_COINS -> menu.takeCoins(Integer.parseInt(inputEventPacket.param()));
                    case CREATE_POUCH -> menu.createPouch(Integer.parseInt(inputEventPacket.param()));
                }
            });
        });
    }

    public void handleTakeCoin(TakeCoinPacket takeCoinPacket, PlayPayloadContext playPayloadContext) {
        playPayloadContext.workHandler().execute(() -> {
            playPayloadContext.player().map(s -> s.containerMenu).filter(WalletContainer.class::isInstance).map(WalletContainer.class::cast).ifPresent(menu -> {
                menu.takeCoin(takeCoinPacket.type(), takeCoinPacket.amount());
            });
        });
    }
}
