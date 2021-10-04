package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import de.cheaterpaul.wallets.items.CoinItem;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, REFERENCE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        CoinItem.getAllCoins().forEach(coin -> {
            withExistingParent(coin.getRegistryName().toString(), mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + coin.getRegistryName().getPath());
        });
        withExistingParent(WalletsMod.wallet_item.getRegistryName().toString(),mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + WalletsMod.wallet_item.getRegistryName().getPath());
        withExistingParent(WalletsMod.coin_pouch.getRegistryName().toString(),mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + WalletsMod.coin_pouch.getRegistryName().getPath());
    }
}
