package de.cheaterpaul.wallets.inventory;

import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.WalletItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class WalletContainer extends Container {

    protected final IInventory inventory;
    private final ItemStack walletStack;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public WalletContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, ItemStack.EMPTY);
    }

    public WalletContainer(int id, PlayerInventory playerInventory, ItemStack stack) {
        super(WalletsMod.wallet_container, id);
        this.walletStack = stack;
        this.inventory = new Inventory(7);
        this.addPlayerSlots(playerInventory);
        this.addSlots(inventory);
    }

    protected void addSlots(IInventory inventory) {
        this.addSlot(new CoinSlot(inventory, 0, 15,15, (value) -> true));
        this.addSlot(new CoinSlot(inventory, 1, 51,60, (value) -> value == CoinItem.CoinValue.ONE));
        this.addSlot(new CoinSlot(inventory, 2, 69,60, (value) -> value == CoinItem.CoinValue.FIVE));
        this.addSlot(new CoinSlot(inventory, 3, 87,60, (value) -> value == CoinItem.CoinValue.TWENTY));
        this.addSlot(new CoinSlot(inventory, 4, 105,60, (value) -> value == CoinItem.CoinValue.FIFTY));
        this.addSlot(new CoinSlot(inventory, 5, 123,60, (value) -> value == CoinItem.CoinValue.ONE_HUNDRED));
        this.addSlot(new CoinSlot(inventory, 6, 141,60, (value) -> value == CoinItem.CoinValue.FIVE_HUNDRED));
    }

    protected void addPlayerSlots(PlayerInventory playerInventory){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 153));
        }
    }

    public void insertCoin() {
        ItemStack stack = this.inventory.getItem(0);
        if (!stack.isEmpty()) {
            int amount = WalletItem.getCoinValue(this.walletStack) + ((CoinItem) stack.getItem()).getValue();
            WalletItem.setCoinValue(this.walletStack, amount);
            this.inventory.setItem(0, ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    public void takeCoin(CoinItem.CoinValue value) {
        int amount = value.getValue();
        if (WalletItem.getCoinValue(this.walletStack) >= amount) {
            WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) - amount);
            _takeCoin(value, 1);
        }
        broadcastChanges();
    }

    private void _takeCoin(CoinItem.CoinValue value, int amount) {
        if (amount <= 0) return;
        ItemStack stack = this.inventory.getItem(value.ordinal()+1);
        if (stack.isEmpty()) {
            this.inventory.setItem(value.ordinal()+1, new ItemStack(CoinItem.getCoin(value), amount));
        } else {
            stack.grow(amount);
        }
    }

    public void takeCoins(int value) {
        if (value <= 0) return;
        if (value > WalletItem.getCoinValue(this.walletStack)) {
            value = WalletItem.getCoinValue(this.walletStack);
        }
        for (CoinItem.CoinValue coinValue : CoinItem.CoinValue.values()) {
            int i = value & coinValue.getValue();
            value -= i * coinValue.getValue();
            _takeCoin(coinValue, i);
        }
        WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) - value);
        broadcastChanges();
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity p_75145_1_) {
        return true;
    }

    public static class CoinSlot extends Slot {

        private final Predicate<ItemStack> predicate;

        public CoinSlot(IInventory inventory, int slot, int xPos, int yPos, Predicate<CoinItem.CoinValue> predicate) {
            super(inventory, slot, xPos, yPos);
            this.predicate = itemStack -> {
                if (itemStack.getItem() instanceof CoinItem) {
                    return predicate.test(((CoinItem) itemStack.getItem()).getCoinValue());
                }
                return false;
            };
        }
    }
}
