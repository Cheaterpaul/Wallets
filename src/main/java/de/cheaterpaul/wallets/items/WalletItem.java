package de.cheaterpaul.wallets.items;

import de.cheaterpaul.wallets.inventory.WalletContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WalletItem extends Item {

    public WalletItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World level, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), new SimpleNamedContainerProvider((id, playerInv, ply) -> new WalletContainer(id, playerInv, stack), new TranslationTextComponent("container.wallets.wallet")));
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> components, @Nonnull ITooltipFlag tooltipFlag) {
        int value = getCoinValue(stack);
        components.add(new TranslationTextComponent("text.wallets.wallet.stored", value));
    }

    public static int getCoinValue(ItemStack stack) {
        return stack.getOrCreateTag().getInt("coin_value");
    }

    public static void setCoinValue(ItemStack stack, int newValue) {
        stack.getOrCreateTag().putInt("coin_value", newValue);
    }

}
