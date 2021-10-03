package de.cheaterpaul.wallets.inventory;

import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.ICoinContainer;
import de.cheaterpaul.wallets.items.WalletItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class WalletContainer extends Container {

    protected final IInventory inventory;
    private final ItemStack walletStack;
    private final IntReferenceHolder walletAmount;
    private final IntReferenceHolder walletPos;
    private final PlayerEntity player;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public WalletContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, ItemStack.EMPTY);
    }

    public WalletContainer(int id, PlayerInventory playerInventory, ItemStack stack) {
        super(WalletsMod.wallet_container, id);
        this.player = playerInventory.player;
        this.walletStack = stack;
        this.inventory = new Inventory(7);
        this.addSlots(inventory);
        this.addPlayerSlots(playerInventory);
        this.addDataSlot(this.walletAmount = IntReferenceHolder.standalone());
        this.addDataSlot(this.walletPos = IntReferenceHolder.standalone());
        this.walletAmount.set(WalletItem.getCoinValue(stack));
        if (!player.level.isClientSide) {
            this.walletPos.set(playerInventory.findSlotMatchingUnusedItem(stack));
        }
    }

    public int getWalletAmount() {
        return this.walletAmount.get();
    }

    protected void addSlots(IInventory inventory) {
        this.addSlot(new CoinSlot(inventory, 0, 15, 15, (stack) -> stack.getItem() instanceof CoinItem || stack.getItem() instanceof WalletItem));
        this.addSlot(new CoinSlot(inventory, 1, 51+20, 60, (value) -> false));
        this.addSlot(new CoinSlot(inventory, 2, 69+20, 60, (value) -> false));
        this.addSlot(new CoinSlot(inventory, 3, 87+20, 60, (value) -> false));
        this.addSlot(new CoinSlot(inventory, 4, 105+20, 60, (value) -> false));
        this.addSlot(new CoinSlot(inventory, 5, 123+20, 60, (value) -> false));
        this.addSlot(new CoinSlot(inventory, 6, 141+20, 60, (value) -> false));
    }

    protected void addPlayerSlots(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 6+8 + j * 18, 95 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new WalletSafeSlot(playerInventory, k, 6+8 + k * 18, 153));
        }
    }


    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull PlayerEntity playerEntity, int index) {
        int size = this.inventory.getContainerSize();
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < size) {
                if (!this.moveItemStackTo(slotStack, size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 27 + size) {
                if (!this.moveItemStackTo(slotStack, 0, size, false)) {
                    if (slotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (!this.moveItemStackTo(slotStack, 27 + size, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + size && index < 36 + size) {
                if (!this.moveItemStackTo(slotStack, 0, 27 + size, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }

    public void insertCoin() {
        ItemStack stack = this.inventory.getItem(0);
        if (!stack.isEmpty()) {
            int amount = WalletItem.getCoinValue(this.walletStack) + (((ICoinContainer) stack.getItem()).getCoins(stack));
            if ((((ICoinContainer) stack.getItem()).containsCoins())) {
                (((ICoinContainer) stack.getItem())).clear(stack);
            } else if (((ICoinContainer) stack.getItem()).isCoin()) {
                this.inventory.setItem(0, ItemStack.EMPTY);
            }
            WalletItem.setCoinValue(this.walletStack, amount);
            this.walletAmount.set(WalletItem.getCoinValue(this.walletStack));
            broadcastChanges();
        }
    }

    public void takeCoin(CoinItem.CoinValue value) {
        int amount = value.getValue();
        if (WalletItem.getCoinValue(this.walletStack) >= amount) {
            WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) - amount);
            _takeCoin(value, 1);
        }
        this.walletAmount.set(WalletItem.getCoinValue(this.walletStack));
        broadcastChanges();
    }

    private void _takeCoin(CoinItem.CoinValue value, int amount) {
        if (amount <= 0) return;
        ItemStack stack = this.inventory.getItem(value.ordinal() + 1);
        if (stack.isEmpty()) {
            this.inventory.setItem(value.ordinal() + 1, new ItemStack(CoinItem.getCoin(value), amount));
        } else {
            stack.grow(amount);
        }
    }

    public void takeCoins(int value) {
        if (value <= 0) return;
        if (value > WalletItem.getCoinValue(this.walletStack)) {
            value = WalletItem.getCoinValue(this.walletStack);
        }
        int remaining = value;
        for (int i = CoinItem.CoinValue.values().length - 1; i >= 0; i--) {
            CoinItem.CoinValue v = CoinItem.CoinValue.values()[i];
            int amount = remaining / v.getValue();
            remaining -= amount * v.getValue();
            _takeCoin(v, amount);
        }
        WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) - value);
        this.walletAmount.set(WalletItem.getCoinValue(this.walletStack));
        broadcastChanges();
    }

    @Override
    public void removed(@Nonnull PlayerEntity player) {
        super.removed(player);
        this.clearContainer(player, player.level, this.inventory);
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity p_75145_1_) {
        return true;
    }

    public class WalletSafeSlot extends Slot {
        public WalletSafeSlot(IInventory inventory, int slot, int xPos, int yPos) {
            super(inventory, slot, xPos, yPos);
        }

        @Override
        public boolean mayPickup(@Nonnull PlayerEntity player) {
            return walletPos.get() != this.getSlotIndex();
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (!player.level.isClientSide) {
//                player.closeContainer();
            }
        }
    }
    public static class CoinSlot extends Slot {


        private final Predicate<ItemStack> predicate;

        public CoinSlot(IInventory inventory, int slot, int xPos, int yPos, Predicate<ItemStack> predicate) {
            super(inventory, slot, xPos, yPos);
            this.predicate = predicate;
        }
        @Override
        public boolean mayPlace(@Nonnull ItemStack itemStack) {
            return predicate.test(itemStack);
        }

    }
}
