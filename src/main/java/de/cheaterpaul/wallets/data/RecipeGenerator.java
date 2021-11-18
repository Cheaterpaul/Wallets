package de.cheaterpaul.wallets.data;

import de.cheaterpaul.wallets.REFERENCE;
import de.cheaterpaul.wallets.WalletsMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(WalletsMod.wallet_item).pattern(" S ").pattern("LLL").define('S', Tags.Items.STRING).define('L', Tags.Items.LEATHER).unlockedBy("has_leather", has( Tags.Items.LEATHER)).unlockedBy("has_string", has(Tags.Items.STRING)).save(consumer, new ResourceLocation(REFERENCE.MOD_ID, "wallet"));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Wallets Recipes";
    }
}
