package de.cheaterpaul.wallets.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Config CONFIG;

    private static final ForgeConfigSpec configSpec;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG = specPair.getKey();
        configSpec = specPair.getValue();
    }

    public final ForgeConfigSpec.BooleanValue disableCoinPouch;

    public Config(ForgeConfigSpec.Builder builder) {
        this.disableCoinPouch = builder.comment("disable create coin pouch button").define("disableCoinPouch", false);
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, configSpec);
    }
}
