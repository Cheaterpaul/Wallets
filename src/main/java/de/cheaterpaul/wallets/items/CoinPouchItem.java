package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    public ITextComponent getName(@Nonnull ItemStack stack) {
        return new TranslationTextComponent("item.wallets.coin_pouch", getCoins(stack));
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
        ItemStack pouch = new ItemStack(WalletsMod.coin_pouch);
        pouch.getOrCreateTag().putInt("ContainedCoins", amount);
        return pouch;
    }
}
