package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CoinPouchItem extends Item implements ICoinContainer {

    public CoinPouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean containsCoins() {
        return false;
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return new TranslatableComponent("item.wallets.coin_pouch", getCoins(stack));
    }

    @Override
    public boolean removedOnUsage() {
        return true;
    }

    @Override
    public int getCoins(ItemStack stack) {
        return stack.getOrCreateTag().getInt("ContainedCoins");
    }

    public static ItemStack createPouch(int amount) {
        ItemStack pouch = new ItemStack(WalletsMod.COIN_POUCH.get());
        pouch.getOrCreateTag().putInt("ContainedCoins", amount);
        return pouch;
    }
}
