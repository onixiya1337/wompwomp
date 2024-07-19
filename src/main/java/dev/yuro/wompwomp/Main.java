package dev.yuro.wompwomp;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "wompwomp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "wompwomp.toml");
    }
}
