package de.cheaterpaul.wallets.items;


import net.minecraft.world.item.ItemStack;

public interface ICoinContainer {

    boolean containsCoins();

    default void clear(ItemStack stack) {
    }

    boolean removedOnUsage();

    int getCoins(ItemStack stack);
}
