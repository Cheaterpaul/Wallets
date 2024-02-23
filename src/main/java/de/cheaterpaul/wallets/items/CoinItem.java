package de.cheaterpaul.wallets.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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

    public enum CoinValue implements StringRepresentable {
        ONE(1, "one", "text.wallets.coin.one"),
        FIVE(5, "five", "text.wallets.coin.five"),
        TEN(10, "ten", "text.wallets.coin.ten"),
        TWENTY(20, "twenty", "text.wallets.coin.twenty"),
        FIFTY(50, "fifty", "text.wallets.coin.fifty"),
        ONE_HUNDRED(100, "one_hundred", "text.wallets.coin.one_hundred"),
        FIVE_HUNDRED(500, "five_hundred", "text.wallets.coin.five_hundred");

        private final int value;
        private final String serializedName;
        private final String translation;

        CoinValue(int value, String serializedName, String translation) {
            this.value = value;
            this.serializedName = serializedName;
            this.translation = translation;
        }

        public int getValue() {
            return value;
        }

        public String getTranslation() {
            return translation;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.serializedName;
        }
    }

}
