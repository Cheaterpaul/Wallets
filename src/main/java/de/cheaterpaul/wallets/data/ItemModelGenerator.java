package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Locale;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CoinItem.getAllCoins().forEach(coin -> {
            withExistingParent(coin.getRegistryName().toString(), modLoc("item/"+ coin.getCoinValue().toString().toLowerCase(Locale.ROOT)));
        });
        withExistingParent(WalletsMod.wallet_item.getRegistryName().toString(), modLoc("item/wallet_item"));
    }
}
