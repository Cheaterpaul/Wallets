package de.cheaterpaul.wallets.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Config CONFIG;

    private static final ModConfigSpec configSpec;

    static {
        final Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Config::new);
        CONFIG = specPair.getKey();
        configSpec = specPair.getValue();
    }

    public final ModConfigSpec.BooleanValue disableCoinPouch;

    public Config(ModConfigSpec.Builder builder) {
        this.disableCoinPouch = builder.comment("disable create coin pouch button").define("disableCoinPouch", false);
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, configSpec);
    }
}
