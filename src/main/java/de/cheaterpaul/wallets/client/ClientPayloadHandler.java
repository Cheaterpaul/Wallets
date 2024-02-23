package de.cheaterpaul.wallets.client;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.network.UpdateWalletPacket;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {

    public static void handleUpdateWalletPacket(UpdateWalletPacket p, PlayPayloadContext l) {
        l.workHandler().execute(() -> {
            l.player().map(s -> s.containerMenu).filter(WalletContainer.class::isInstance).ifPresent(s -> ((WalletContainer) s).update(p));
        });
    }
}
