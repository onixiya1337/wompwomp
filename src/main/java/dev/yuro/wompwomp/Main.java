package dev.yuro.wompwomp;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "simpletogglesprint";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final BrewingStandHandler brewingStandHandler = new BrewingStandHandler();
    public static final FurnaceHandler furnaceHandler = new FurnaceHandler();
    public static final CommandHandler commandHandler = new CommandHandler();

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(brewingStandHandler);
        MinecraftForge.EVENT_BUS.register(furnaceHandler);
        MinecraftForge.EVENT_BUS.register(commandHandler);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "wompwomp.toml");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
