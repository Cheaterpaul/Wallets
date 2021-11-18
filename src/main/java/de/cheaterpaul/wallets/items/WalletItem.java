package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WalletItem extends Item implements ICoinContainer {

    public WalletItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui(((ServerPlayer) player), new SimpleMenuProvider((id, playerInv, ply) -> new WalletContainer(id, playerInv, stack), new TranslatableComponent("container.wallets.wallet")));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level p_77624_2_, List<Component> components, @Nonnull TooltipFlag tooltipFlag) {
        int value = getCoinValue(stack);
        components.add(new TranslatableComponent("text.wallets.wallet.stored", value).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public int getCoins(ItemStack stack) {
        return getCoinValue(stack);
    }

    @Override
    public boolean removedOnUsage() {
        return false;
    }

    @Override
    public boolean containsCoins() {
        return true;
    }

    @Override
    public void clear(ItemStack stack) {
        setCoinValue(stack, 0);
    }

    public static int getCoinValue(ItemStack stack) {
        return stack.getOrCreateTag().getInt("coin_value");
    }

    public static void setCoinValue(ItemStack stack, int newValue) {
        stack.getOrCreateTag().putInt("coin_value", newValue);
    }

}
