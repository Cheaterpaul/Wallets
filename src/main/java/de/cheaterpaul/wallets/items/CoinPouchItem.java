package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class CoinPouchItem extends Item implements ICoinContainer {

    public CoinPouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean containsCoins() {
        return false;
    }

    @Override
    public Component getName( ItemStack stack) {
        return Component.translatable("item.wallets.coin_pouch", getCoins(stack));
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
