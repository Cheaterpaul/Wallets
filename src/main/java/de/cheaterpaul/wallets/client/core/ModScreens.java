package de.cheaterpaul.wallets.client.core;

import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.client.gui.WalletScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreens {

    public static void registerScreens() {
        MenuScreens.register(WalletsMod.wallet_container, WalletScreen::new);
    }
}
