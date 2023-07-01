package de.cheaterpaul.wallets;

import de.cheaterpaul.wallets.client.core.ModScreens;
import de.cheaterpaul.wallets.config.Config;
import de.cheaterpaul.wallets.data.ItemModelGenerator;
import de.cheaterpaul.wallets.data.RecipeGenerator;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.CoinPouchItem;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(REFERENCE.MOD_ID)
public class WalletsMod
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, REFERENCE.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, REFERENCE.MOD_ID);

    public static final ModPacketDispatcher dispatcher = new ModPacketDispatcher();

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(REFERENCE.MOD_ID, WalletsMod::createCreativeTab);
    public static final RegistryObject<CoinItem> COIN_ONE = ITEMS.register("coin_one", () -> new CoinItem(CoinItem.CoinValue.ONE, new Item.Properties()));
    public static final RegistryObject<CoinItem> COIN_FIVE = ITEMS.register("coin_five", () -> new CoinItem(CoinItem.CoinValue.FIVE, new Item.Properties()));
    public static final RegistryObject<CoinItem> COIN_TWENTY = ITEMS.register("coin_twenty", () -> new CoinItem(CoinItem.CoinValue.TWENTY, new Item.Properties()));
    public static final RegistryObject<CoinItem> COIN_FIFTY = ITEMS.register("coin_fifty", () -> new CoinItem(CoinItem.CoinValue.FIFTY, new Item.Properties()));
    public static final RegistryObject<CoinItem> COIN_ONE_HUNDRED = ITEMS.register("coin_one_hundred", () -> new CoinItem(CoinItem.CoinValue.ONE_HUNDRED, new Item.Properties()));
    public static final RegistryObject<CoinItem> COIN_FIVE_HUNDRED = ITEMS.register("coin_five_hundred", () -> new CoinItem(CoinItem.CoinValue.FIVE_HUNDRED, new Item.Properties()));
    public static final RegistryObject<WalletItem> WALLET = ITEMS.register("wallet", () -> new WalletItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CoinPouchItem> COIN_POUCH = ITEMS.register("coin_pouch", () -> new CoinPouchItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MenuType<WalletContainer>> WALLET_CONTAINER = MENUS.register("wallet_container", () -> new MenuType<>(WalletContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public WalletsMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::gatherData);
        bus.addListener(this::doClientStuff);
        ITEMS.register(bus);
        MENUS.register(bus);
        CREATIVE_TABS.register(bus);

        Config.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        dispatcher.registerPackets();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeClient(), new ItemModelGenerator(gen.getPackOutput(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new RecipeGenerator(gen.getPackOutput()));
    }

    private static CreativeModeTab createCreativeTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(WALLET.get()))
                .title(Component.translatable("itemGroup." + REFERENCE.MOD_ID))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .displayItems((params, output) -> {
                    output.accept(COIN_ONE.get());
                    output.accept(COIN_FIVE.get());
                    output.accept(COIN_TWENTY.get());
                    output.accept(COIN_FIFTY.get());
                    output.accept(COIN_ONE_HUNDRED.get());
                    output.accept(COIN_FIVE_HUNDRED.get());
                    output.accept(WALLET.get());
                }).build();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ModScreens::registerScreens);
    }

}
