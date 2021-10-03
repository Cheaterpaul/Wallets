package de.cheaterpaul.wallets.items;

import net.minecraft.item.Item;

public class CoinItem extends Item {

    protected final CoinValue coinValue;

    public CoinItem(CoinValue coinValue, Properties properties) {
        super(properties);
        this.coinValue = coinValue;
    }

    public int getValue() {
        return this.coinValue.getValue();
    }

    public CoinValue getCoinValue() {
        return this.coinValue;
    }

    enum CoinValue {
        ONE(1), TWO(2), FIVE(5), TEN(10), TWENTY(20), FIFTY(50), ONE_HUNDRED(100), TWO_HUNDRED(200), FIVE_HUNDRED(500);

        private final int value;

        CoinValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
