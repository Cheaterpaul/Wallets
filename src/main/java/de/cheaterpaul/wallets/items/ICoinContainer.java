package de.cheaterpaul.wallets.items;

import net.minecraft.item.ItemStack;

public interface ICoinContainer {

    boolean containsCoins();

    default void clear(ItemStack stack) {
    }

    boolean isCoin();

    int getCoins(ItemStack stack);
}
