package de.cheaterpaul.wallets.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinItem extends Item implements ICoinContainer {

    private static final Map<CoinValue, CoinItem> coinMap = new HashMap<>(CoinValue.values().length, 1);

    public static CoinItem getCoin(CoinValue value) {
        return coinMap.get(value);
    }

    public static Collection<CoinItem> getAllCoins() {
        return coinMap.values();
    }


    protected final CoinValue coinValue;

    public CoinItem(CoinValue coinValue, Properties properties) {
        super(properties);
        this.coinValue = coinValue;
        coinMap.put(coinValue, this);
    }

    @Override
    public void appendHoverText(ItemStack stack,  Level level,  List<Component> list,  TooltipFlag tooltipFlag) {
        int sum = ((ICoinContainer) stack.getItem()).getCoins(stack);
        list.add(Component.translatable("text.wallets.sum_amount", sum).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public int getCoins(ItemStack stack) {
        return stack.getCount() * this.coinValue.getValue();
    }

    @Override
    public boolean removedOnUsage() {
        return true;
    }

    @Override
    public boolean containsCoins() {
        return false;
    }

    public enum CoinValue {
        ONE(1), FIVE(5), TWENTY(20), FIFTY(50), ONE_HUNDRED(100), FIVE_HUNDRED(500);

        private final int value;

        CoinValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
