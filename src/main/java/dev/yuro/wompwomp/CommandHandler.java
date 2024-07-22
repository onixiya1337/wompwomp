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
import net.minecraftforge.registries.ForgeRegistries;

public class CommandHandler {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("wompwomp")
                        .then(Commands.literal("toggle")
                                .then(Commands.literal("brewingStand")
                                        .executes(this::toggleBrewingStand))
                                .then(Commands.literal("furnace")
                                        .executes(this::toggleFurnance))
                        )
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
//                                .then(Commands.literal("alchemyFuelItem")
//                                        .then(Commands.argument("item", StringArgumentType.greedyString())
//                                                .executes(ctx -> setItem(ctx, "alchemyFuelItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyIngredientItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyIngredientItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyAmplifierItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyAmplifierItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyPrepareItem")
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .executes(ctx -> setItem(ctx, "alchemyPrepareItem", StringArgumentType.getString(ctx, "item")))))
                                .then(Commands.literal("alchemyPotionEffect")
                                        .then(Commands.argument("effect", StringArgumentType.greedyString())
                                                .executes(ctx -> setPotionEffect(ctx, StringArgumentType.getString(ctx, "effect")))
                                                .suggests((ctx, builder) -> {
                                                    for (ResourceLocation resourceLocation : ForgeRegistries.POTIONS.getKeys()) {
                                                        builder.suggest(resourceLocation.toString());
                                                    }
                                                    return builder.buildFuture();
                                                })))
                        )
        );
    }

    private int toggleBrewingStand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("§8[§5AutoBrewing§8] " + (Config.brewingStandEnabled ? "§cDisabled" : "§aEnabled")), true);
        Config.BREWING_STAND_ENABLED.set(!Config.brewingStandEnabled);
        return 1;
    }

    private int toggleFurnance(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("§8[§5AutoFurnace§8] " + (Config.furnaceEnabled ? "§cDisabled" : "§aEnabled")), true);
        Config.FURNACE_ENABLED.set(!Config.furnaceEnabled);
        return 1;
    }

    private int setDelay(CommandContext<CommandSourceStack> context, int value) {
        Config.DELAY.set(value);
        Config.delay = value;
        context.getSource().sendSuccess(() -> Component.literal("Set delay to " + value), true);
        return 1;
    }

    private int setItem(CommandContext<CommandSourceStack> context, String configField, String itemName) {
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
            case "alchemyAmplifierItem":
                Config.ALCHEMY_AMPLIFIER_ITEM.set(itemName);
                Config.alchemyAmplifierItem = itemName;
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

    private int setPotionEffect(CommandContext<CommandSourceStack> context, String effect) throws CommandSyntaxException {
        if (!potionEffectExists(effect)) {
            context.getSource().sendFailure(Component.literal("Potion effect " + effect + " does not exist!"));
            return 0;
        }

        Config.ALCHEMY_POTION_EFFECT.set(effect);
        Config.alchemyPotionEffect = effect;
        context.getSource().sendSuccess(() -> Component.literal("Set alchemyPotionEffect to " + effect), true);
        return 1;
    }

    private boolean itemExists(String itemName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(itemName);
        return resourceLocation != null && ForgeRegistries.ITEMS.containsKey(resourceLocation);
    }

    private boolean potionEffectExists(String effectName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(effectName);
        return resourceLocation != null && ForgeRegistries.POTIONS.containsKey(resourceLocation);
    }
}