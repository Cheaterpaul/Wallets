package de.cheaterpaul.wallets;

import de.cheaterpaul.wallets.client.core.ModScreens;
import de.cheaterpaul.wallets.data.ItemModelGenerator;
import de.cheaterpaul.wallets.inventory.WalletContainer;
import de.cheaterpaul.wallets.items.CoinItem;
import de.cheaterpaul.wallets.items.WalletItem;
import de.cheaterpaul.wallets.network.ModPacketDispatcher;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@Mod(REFERENCE.MOD_ID)
public class WalletsMod
{
    public WalletsMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(new ItemModelGenerator(gen, event.getExistingFileHelper()));
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ModScreens::registerScreens);
    }

    private void setup(final FMLCommonSetupEvent event) {
        dispatcher.registerPackets();
    }

    public static final ModPacketDispatcher dispatcher = new ModPacketDispatcher();

    public static final ItemGroup ITEM_GROUP = new ItemGroup("wallets") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(wallet_item);
        }
    };
    @ObjectHolder(REFERENCE.MOD_ID+":coin_one")
    public static final CoinItem coin_one = getNull();
    @ObjectHolder(REFERENCE.MOD_ID+":wallet")
    public static final WalletItem wallet_item = getNull();
    @ObjectHolder(REFERENCE.MOD_ID+":wallet")
    public static final ContainerType<WalletContainer> wallet_container = getNull();

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();
            registry.register(new CoinItem(CoinItem.CoinValue.ONE, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_one"));
            registry.register(new CoinItem(CoinItem.CoinValue.FIVE, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_five"));
            registry.register(new CoinItem(CoinItem.CoinValue.TWENTY, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_twenty"));
            registry.register(new CoinItem(CoinItem.CoinValue.FIFTY, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_fifty"));
            registry.register(new CoinItem(CoinItem.CoinValue.ONE_HUNDRED, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_one_hundred"));
            registry.register(new CoinItem(CoinItem.CoinValue.FIVE_HUNDRED, new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "coin_five_hundred"));
            registry.register(new WalletItem(new Item.Properties().tab(ITEM_GROUP)).setRegistryName(REFERENCE.MOD_ID, "wallet"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
            registry.register(new ContainerType<>(WalletContainer::new).setRegistryName(REFERENCE.MOD_ID, "wallet"));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static <T> T getNull() {
        return null;
    }
}
