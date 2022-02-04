package de.cheaterpaul.wallets.inventory;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.CoinPouchItem;
import de.cheaterpaul.wallets.items.ICoinContainer;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.UpdateWalletPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class WalletContainer extends Container {

    protected final IInventory inventory;
    private final ItemStack walletStack;
    private int walletAmount;
    private int walletPos;
    private final PlayerEntity player;
    private final Map<CoinItem.CoinValue, TakeOnlySlot> coinSlots;
    private ICoinChangeListener changeListener;

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
        this.coinSlots = new HashMap<>();
        this.addSlots(inventory);
        this.addPlayerSlots(playerInventory);
        this.walletAmount = WalletItem.getCoinValue(stack);
        if (!player.level.isClientSide) {
            this.walletPos = playerInventory.findSlotMatchingUnusedItem(stack);
        }
    }

    public void listen(ICoinChangeListener listener) {
        this.changeListener = listener;
    }

    public int getWalletAmount() {
        return this.walletAmount;
    }

    protected void addSlots(IInventory inventory) {
        this.addSlot(new CoinSlot(inventory, 0, 15, 15, (stack) -> stack.getItem() instanceof ICoinContainer));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.ONE, inventory, 1, 51+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_one")));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.FIVE, inventory, 2, 69+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_five")));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.TWENTY, inventory, 3, 87+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_twenty")));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.FIFTY, inventory, 4, 105+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_fifty")));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.ONE_HUNDRED, inventory, 5, 123+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_one_hundred")));
        this.addSlot(new TakeOnlySlot(CoinItem.CoinValue.FIVE_HUNDRED, inventory, 6, 141+20, 60,new ResourceLocation(REFERENCE.MOD_ID,"item/coin_five_hundred")));
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
        if (player.level.isClientSide) return;
        ItemStack stack = this.inventory.getItem(0);
        if (!stack.isEmpty()) {
            int coin_value = (((ICoinContainer) stack.getItem()).getCoins(stack));
            if (getWalletAmount() + coin_value > 999999999) return; // limit wallet amount to ensure right screen rendering
            if ((((ICoinContainer) stack.getItem()).containsCoins())) {
                (((ICoinContainer) stack.getItem())).clear(stack);
            } else if (((ICoinContainer) stack.getItem()).removedOnUsage()) {
                this.inventory.setItem(0, ItemStack.EMPTY);
            }
            this.addWalletCoins(coin_value);
        }
    }

    public void updateClient() {
        if (!(this.player instanceof ServerPlayerEntity)) return;
        WalletsMod.dispatcher.sentToPlayer(new UpdateWalletPacket(this.walletAmount, this.walletPos), ((ServerPlayerEntity) this.player));
    }

    public void takeCoin(CoinItem.CoinValue value) {
        takeCoin(value,1);
    }

    public void takeCoin(CoinItem.CoinValue type, int amount) {
        assert amount <= 64;
        int coinValue = type.getValue();
        int coins = Math.min(WalletItem.getCoinValue(this.walletStack) / coinValue, amount);
        ItemStack slot = this.coinSlots.get(type).getItem();
        coins = Math.min(coins, slot.getMaxStackSize() - slot.getCount());
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
        this.player.inventory.placeItemBackInInventory(this.player.level, stack);
    }

    private void addWalletCoins(int amount) {
        WalletItem.setCoinValue(this.walletStack, WalletItem.getCoinValue(this.walletStack) + amount);
        this.walletAmount = WalletItem.getCoinValue(this.walletStack);
        this.updateClient();
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

    public void update(UpdateWalletPacket msg) {
        this.walletAmount = msg.walletAmount;
        this.walletPos = msg.walletPos;
        if (this.changeListener != null) {
            this.changeListener.coinsChanged();
        }
    }

    public class WalletSafeSlot extends Slot {
        public WalletSafeSlot(IInventory inventory, int slot, int xPos, int yPos) {
            super(inventory, slot, xPos, yPos);
        }

        @Override
        public boolean mayPickup(@Nonnull PlayerEntity player) {
            return walletPos != this.getSlotIndex();
        }

        @Override
        public void setChanged() {
            super.setChanged();
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
    public class TakeOnlySlot extends Slot {

        private ResourceLocation texture;

        public TakeOnlySlot(CoinItem.CoinValue coin, IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, ResourceLocation texture) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
            this.texture = texture;
            coinSlots.put(coin, this);
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
