package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, REFERENCE.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Arrays.asList(WalletsMod.COIN_ONE,WalletsMod.COIN_FIVE,WalletsMod.COIN_TEN,WalletsMod.COIN_TWENTY,WalletsMod.COIN_FIFTY,WalletsMod.COIN_ONE_HUNDRED,WalletsMod.COIN_FIVE_HUNDRED).forEach(coin -> {
            withExistingParent(coin.getId().toString(), mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + coin.getId().getPath());
        });
        withExistingParent(WalletsMod.WALLET.getId().toString(),mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + WalletsMod.WALLET.getId().getPath());
        withExistingParent(WalletsMod.COIN_POUCH.getId().toString(),mcLoc("item/generated")).texture("layer0", REFERENCE.MOD_ID + ":item/" + WalletsMod.COIN_POUCH.getId().getPath());
    }
}
