package de.cheaterpaul.wallets.inventory;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.CoinPouchItem;
import de.cheaterpaul.wallets.items.ICoinContainer;
import de.cheaterpaul.wallets.items.WalletItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class WalletContainer extends AbstractContainerMenu {

    protected final Container inventory;
    private final ItemStack walletStack;
    private final DataSlot walletAmount;
    private final DataSlot walletPos;
    private final Player player;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public WalletContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ItemStack.EMPTY);
    }

    public WalletContainer(int id, Inventory playerInventory, ItemStack stack) {
        super(WalletsMod.wallet_container, id);
        this.player = playerInventory.player;
        this.walletStack = stack;
        this.inventory = new SimpleContainer(7);
        this.addSlots(inventory);
        this.addPlayerSlots(playerInventory);
        this.addDataSlot(this.walletAmount = DataSlot.standalone());
        this.addDataSlot(this.walletPos = DataSlot.standalone());
        this.walletAmount.set(WalletItem.getCoinValue(stack));
        if (!player.level.isClientSide) {
            this.walletPos.set(playerInventory.findSlotMatchingUnusedItem(stack));
        }
    }

    public int getWalletAmount() {
        return this.walletAmount.get();
    }

    protected void addSlots(Container inventory) {
        this.addSlot(new CoinSlot(inventory, 0, 15, 15, (stack) -> stack.getItem() instanceof ICoinContainer));
        this.addSlot(new TakeOnlySlot(inventory, 1, 51+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_one")));
        this.addSlot(new TakeOnlySlot(inventory, 2, 69+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_five")));
        this.addSlot(new TakeOnlySlot(inventory, 3, 87+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_twenty")));
        this.addSlot(new TakeOnlySlot(inventory, 4, 105+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_fifty")));
        this.addSlot(new TakeOnlySlot(inventory, 5, 123+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_one_hundred")));
        this.addSlot(new TakeOnlySlot(inventory, 6, 141+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_five_hundred")));
    }

    protected void addPlayerSlots(Inventory playerInventory) {
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
    public ItemStack quickMoveStack(@Nonnull Player playerEntity, int index) {
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
        if (player.level.isClientSide) return;
        ItemStack stack = this.inventory.getItem(0);
        if (!stack.isEmpty()) {
            int coin_value = (((ICoinContainer) stack.getItem()).getCoins(stack));
            if ((((ICoinContainer) stack.getItem()).containsCoins())) {
                (((ICoinContainer) stack.getItem())).clear(stack);
            } else if (((ICoinContainer) stack.getItem()).removedOnUsage()) {
                this.inventory.setItem(0, ItemStack.EMPTY);
            }
            addWalletCoins(coin_value);
        }
    }

    public void takeCoin(CoinItem.CoinValue value) {
        takeCoin(value,1);
    }

    public void takeCoin(CoinItem.CoinValue type, int amount) {
        assert amount <= 64;
        int coinValue = type.getValue();
        int coins = Math.min(WalletItem.getCoinValue(this.walletStack) / coinValue, amount);
        _takeCoin(type, coins);
        addWalletCoins(-coins * coinValue);
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
        addWalletCoins(-value);
    }

    public void createPouch(int value) {
        if (value > WalletItem.getCoinValue(this.walletStack)) {
            value = WalletItem.getCoinValue(this.walletStack);
        }
        if (value <= 0) return;
        ItemStack stack = CoinPouchItem.createPouch(value);
        addWalletCoins(-value);
        this.player.getInventory().placeItemBackInInventory(stack);
    }

    private void addWalletCoins(int amount) {
        WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) + amount);
        this.walletAmount.set(WalletItem.getCoinValue(this.walletStack));
        broadcastChanges();
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.clearContainer(player, this.inventory);
    }

    @Override
    public boolean stillValid(@Nonnull Player p_75145_1_) {
        return true;
    }

    public class WalletSafeSlot extends Slot {
        public WalletSafeSlot(Container inventory, int slot, int xPos, int yPos) {
            super(inventory, slot, xPos, yPos);
        }

        @Override
        public boolean mayPickup(@Nonnull Player player) {
            return walletPos.get() != this.getSlotIndex();
        }

        @Override
        public void setChanged() {
            super.setChanged();
        }
    }
    public static class CoinSlot extends Slot {


        private final Predicate<ItemStack> predicate;

        public CoinSlot(Container inventory, int slot, int xPos, int yPos, Predicate<ItemStack> predicate) {
            super(inventory, slot, xPos, yPos);
            this.predicate = predicate;
        }
        @Override
        public boolean mayPlace(@Nonnull ItemStack itemStack) {
            return predicate.test(itemStack);
        }

    }
    public static class TakeOnlySlot extends Slot {

        private ResourceLocation texture;

        public TakeOnlySlot(Container p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, ResourceLocation texture) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
            this.texture = texture;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }
}
