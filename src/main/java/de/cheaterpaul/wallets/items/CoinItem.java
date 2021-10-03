package de.cheaterpaul.wallets.items;

import net.minecraft.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CoinItem extends Item {

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

    public int getValue() {
        return this.coinValue.getValue();
    }

    public CoinValue getCoinValue() {
        return this.coinValue;
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
