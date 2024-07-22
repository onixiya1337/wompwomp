package dev.yuro.wompwomp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue DELAY = BUILDER
            .comment("Delay between furnace operations (in ticks)")
            .defineInRange("delay", 4, 1, 20);
    public static final ForgeConfigSpec.BooleanValue CLOSE_INVENTORY = BUILDER
            .comment("Close inventory after operation")
            .define("closeInventory", true);
    public static final ForgeConfigSpec.ConfigValue<String> FUEL_ITEM = BUILDER
            .comment("Fuel item to use (e.g., minecraft:coal)")
            .define("fuelItem", "minecraft:coal");
    public static final ForgeConfigSpec.ConfigValue<String> SMELT_ITEM = BUILDER
            .comment("Item to smelt (e.g., minecraft:raw_copper)")
            .define("smeltItem", "minecraft:raw_copper");
    public static final ForgeConfigSpec.ConfigValue<String> ALCHEMY_INGREDIENT_ITEM = BUILDER
            .comment("Item to use as ingredient (e.g., minecraft:blaze_powder)")
            .define("alchemyFuelItem", "minecraft:blaze_powder");
    public static final ForgeConfigSpec.ConfigValue<String> ALCHEMY_FUEL_ITEM = BUILDER
            .comment("Item to use as fuel (e.g., minecraft:blaze_powder)")
            .define("alchemyIngredientItem", "minecraft:blaze_powder");
    public static final ForgeConfigSpec.ConfigValue<String> ALCHEMY_AMPLIFIER_ITEM = BUILDER
            .comment("Item to use as amplifier (e.g., minecraft:glowstone_powder)")
            .define("alchemyAmplifierItem", "minecraft:glowstone_powder");
    public static final ForgeConfigSpec.ConfigValue<String> ALCHEMY_PREPARE_ITEM = BUILDER
            .comment("Item to use as prepare ingredient (e.g., minecraft:nether_wart)")
            .define("alchemyBrewedPotionItem", "minecraft:nether_wart");
    public static final ForgeConfigSpec.ConfigValue<String> ALCHEMY_POTION_EFFECT = BUILDER
            .comment("Potion effect to apply (e.g., minecraft:strong_strength)")
            .define("alchemyPotionEffect", "minecraft:strong_strength");
    public static final ForgeConfigSpec.BooleanValue BREWING_STAND_ENABLED = BUILDER
            .comment("Enable brewing stand automation")
            .define("brewingStandEnabled", true);
    public static final ForgeConfigSpec.BooleanValue FURNACE_ENABLED = BUILDER
            .comment("Enable furnace automation")
            .define("furnaceEnabled", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int delay;
    public static boolean closeInventory;
    public static String furnanceFuelItem;
    public static String furnanceSmeltItem;
    public static String alchemyFuelItem;
    public static String alchemyIngredientItem;
    public static String alchemyAmplifierItem;
    public static String alchemyPrepareItem;
    public static String alchemyPotionEffect;
    public static boolean brewingStandEnabled;
    public static boolean furnaceEnabled;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        delay = DELAY.get();
        closeInventory = CLOSE_INVENTORY.get();

        furnanceFuelItem = FUEL_ITEM.get();
        furnanceSmeltItem = SMELT_ITEM.get();

        alchemyFuelItem = ALCHEMY_FUEL_ITEM.get();
        alchemyIngredientItem = ALCHEMY_INGREDIENT_ITEM.get();
        alchemyAmplifierItem = ALCHEMY_AMPLIFIER_ITEM.get();
        alchemyPrepareItem = ALCHEMY_PREPARE_ITEM.get();
        alchemyPotionEffect = ALCHEMY_POTION_EFFECT.get();

        brewingStandEnabled = BREWING_STAND_ENABLED.get();
        furnaceEnabled = FURNACE_ENABLED.get();
    }
}
