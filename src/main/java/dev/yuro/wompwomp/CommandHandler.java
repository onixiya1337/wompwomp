package dev.yuro.wompwomp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("wompwomp")
                        .then(Commands.literal("config")
                                .then(Commands.literal("delay")
                                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 20))
                                                .executes(ctx -> setDelay(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                                .then(Commands.literal("fuelItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "fuelItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("smeltItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "smeltItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyFuelItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyFuelItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyIngredientItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyIngredientItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyPrepareItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyPrepareItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyPotionEffect")
                                        .then(Commands.argument("effect", StringArgumentType.greedyString())
                                                .executes(ctx -> setPotionEffect(ctx, StringArgumentType.getString(ctx, "effect")))))
                        )
        );
    }

    private static int setDelay(CommandContext<CommandSourceStack> context, int value) {
        Config.DELAY.set(value);
        Config.delay = value;
        context.getSource().sendSuccess(() -> Component.literal("Set delay to " + value), true);
        return 1;
    }

    private static int setItem(CommandContext<CommandSourceStack> context, String configField, String itemName) {
        if (!itemExists(itemName)) {
            context.getSource().sendFailure(Component.literal("Item " + itemName + " does not exist!"));
            return 0;
        }

        switch (configField) {
            case "fuelItem":
                Config.FUEL_ITEM.set(itemName);
                Config.furnanceFuelItem = itemName;
                break;
            case "smeltItem":
                Config.SMELT_ITEM.set(itemName);
                Config.furnanceSmeltItem = itemName;
                break;
            case "alchemyFuelItem":
                Config.ALCHEMY_FUEL_ITEM.set(itemName);
                Config.alchemyFuelItem = itemName;
                break;
            case "alchemyIngredientItem":
                Config.ALCHEMY_INGREDIENT_ITEM.set(itemName);
                Config.alchemyIngredientItem = itemName;
                break;
            case "alchemyPrepareItem":
                Config.ALCHEMY_PREPARE_ITEM.set(itemName);
                Config.alchemyPrepareItem = itemName;
                break;
            default:
                context.getSource().sendFailure(Component.literal("Invalid config field!"));
                return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Set " + configField + " to " + itemName), true);
        return 1;
    }

    private static int setPotionEffect(CommandContext<CommandSourceStack> context, String effect) throws CommandSyntaxException {
        if (!potionEffectExists(effect)) {
            context.getSource().sendFailure(Component.literal("Potion effect " + effect + " does not exist!"));
            return 0;
        }

        Config.ALCHEMY_POTION_EFFECT.set(effect);
        Config.alchemyPotionEffect = effect;
        context.getSource().sendSuccess(() -> Component.literal("Set alchemyPotionEffect to " + effect), true);
        return 1;
    }

    private static boolean itemExists(String itemName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(itemName);
        return resourceLocation != null && ForgeRegistries.ITEMS.containsKey(resourceLocation);
    }

    private static boolean potionEffectExists(String effectName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(effectName);
        return resourceLocation != null && ForgeRegistries.POTIONS.containsKey(resourceLocation);
    }
}