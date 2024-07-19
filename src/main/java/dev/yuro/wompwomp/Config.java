package dev.yuro.wompwomp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static ForgeConfigSpec.IntValue DELAY = BUILDER
            .comment("Delay between furnace operations (in ticks)")
            .defineInRange("delay", 4, 1, 20);
    public static ForgeConfigSpec.BooleanValue CLOSE_INVENTORY = BUILDER
            .comment("Close inventory after operation")
            .define("closeInventory", true);
    public static ForgeConfigSpec.ConfigValue<String> FUEL_ITEM = BUILDER
            .comment("Fuel item to use (e.g., minecraft:coal)")
            .define("fuelItem", "minecraft:coal");
    public static ForgeConfigSpec.ConfigValue<String> SMELT_ITEM = BUILDER
            .comment("Item to smelt (e.g., minecraft:raw_copper)")
            .define("smeltItem", "minecraft:raw_copper");
    public static ForgeConfigSpec.ConfigValue<String> ALCHEMY_INGREDIENT_ITEM = BUILDER
            .comment("Item to use as ingredient (e.g., minecraft:blaze_powder)")
            .define("alchemyFuelItem", "minecraft:blaze_powder");
    public static ForgeConfigSpec.ConfigValue<String> ALCHEMY_FUEL_ITEM = BUILDER
            .comment("Item to use as ingredient (e.g., minecraft:blaze_powder)")
            .define("alchemyIngredientItem", "minecraft:blaze_powder");
    public static ForgeConfigSpec.ConfigValue<String> ALCHEMY_PREPARE_ITEM = BUILDER
            .comment("Item to use as ingredient (e.g., minecraft:nether_wart)")
            .define("alchemyBrewedPotionItem", "minecraft:nether_wart");
    public static ForgeConfigSpec.ConfigValue<String> ALCHEMY_POTION_EFFECT = BUILDER
            .comment("Potion effect to apply (e.g., minecraft:strength)")
            .define("alchemyPotionEffect", "minecraft:strength");
    public static int delay;
    public static boolean closeInventory;
    public static String furnanceFuelItem;
    public static String furnanceSmeltItem;
    public static String alchemyFuelItem;
    public static String alchemyIngredientItem;
    public static String alchemyPrepareItem;
    public static String alchemyPotionEffect;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        delay = DELAY.get();
        closeInventory = CLOSE_INVENTORY.get();

        furnanceFuelItem = FUEL_ITEM.get();
        furnanceSmeltItem = SMELT_ITEM.get();

        alchemyFuelItem = ALCHEMY_FUEL_ITEM.get();
        alchemyIngredientItem = ALCHEMY_INGREDIENT_ITEM.get();
        alchemyPrepareItem = ALCHEMY_PREPARE_ITEM.get();
        alchemyPotionEffect = ALCHEMY_POTION_EFFECT.get();
    }
}
