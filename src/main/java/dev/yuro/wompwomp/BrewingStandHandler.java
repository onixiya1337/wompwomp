package dev.yuro.wompwomp;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class BrewingStandHandler {

    private int delay = 4;
    private boolean putItem = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().player != null) {
                if (delay > 0) {
                    delay--;
                    return;
                }
                delay = Config.delay;
                AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
                if (container instanceof BrewingStandMenu brewingStandMenu) {
                    handleBrewingStand(brewingStandMenu, Minecraft.getInstance().player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        AbstractContainerMenu container = event.getContainer();
        if (container instanceof BrewingStandMenu) {
            delay = Config.delay;
        }
    }

    private void handleBrewingStand(BrewingStandMenu brewingStandMenu, Player player) {
        for (int i = 0; i <= 2; i++) {
            ItemStack potion = brewingStandMenu.getSlot(i).getItem();
            if (potion.isEmpty()) {
                int awkwardPotionSlot = findPotionInInventory(brewingStandMenu, "minecraft:awkward");
                if (awkwardPotionSlot != -1) {
                    assert Minecraft.getInstance().gameMode != null;
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, awkwardPotionSlot, i, ClickType.QUICK_MOVE, player);
                    Main.LOGGER.info("Moved awkward potion to slot {}", i);
                    return;
                }
                int potionSlot = findPotionInInventory(brewingStandMenu, "minecraft:water");
                if (potionSlot != -1) {
                    assert Minecraft.getInstance().gameMode != null;
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, potionSlot, 1, ClickType.QUICK_MOVE, player);
                    Main.LOGGER.info("Moved water bottle to slot {}", i);
                    return;
                }
            } else if (isPotion(potion, "minecraft:water") && brewingStandMenu.getSlot(3).getItem().isEmpty()) {
                int ingredientSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyPrepareItem));
                if (ingredientSlot != -1) {
                    assert Minecraft.getInstance().gameMode != null;
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, ingredientSlot, 1, ClickType.QUICK_MOVE, player);
                    Main.LOGGER.info("Moved {} to the prepare slot", Config.alchemyPrepareItem);
                    return;
                }
            } else if (isPotion(potion, "minecraft:awkward") && brewingStandMenu.getSlot(3).getItem().isEmpty()) {
                int ingredientSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyIngredientItem));
                if (ingredientSlot != -1) {
                    assert Minecraft.getInstance().gameMode != null;
                    if (putItem) {
                        Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, ingredientSlot, 0, ClickType.PICKUP, player);
                        putItem = false;
                        Main.LOGGER.info("Grabbed {} from the inventory", Config.alchemyIngredientItem);
                        return;
                    } else {
                        Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, 3, 0, ClickType.SWAP, player);
                        putItem = true;
                        Main.LOGGER.info("Moved {} to the ingredient slot", Config.alchemyIngredientItem);
                        return;
                    }
                }
            } else if (isPotion(potion, Config.alchemyPotionEffect) && getFreeSlot(brewingStandMenu) != -1) {
                assert Minecraft.getInstance().gameMode != null;
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, i, 0, ClickType.QUICK_MOVE, player);
                Main.LOGGER.info("Moved potion {} to the inventory", Config.alchemyPotionEffect);
                return;
            }
        }
        if (brewingStandMenu.getSlot(4).getItem().isEmpty()) {
            int fuelSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyFuelItem));
            if (fuelSlot != -1) {
                assert Minecraft.getInstance().gameMode != null;
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, fuelSlot, 1, ClickType.QUICK_MOVE, player);
                Main.LOGGER.info("Moved {} to the fuel slot", Config.alchemyFuelItem);
                return;
            }
        }
        if (Config.closeInventory)
            player.closeContainer();
    }

    private int findItemInInventory(BrewingStandMenu brewingStandMenu, Item item) {
        for (int i = 5; i < 41; i++) {
            if (brewingStandMenu.getSlot(i).getItem().getItem() == item) {
                Main.LOGGER.info("Item {} found in inventory at slot {}", item, i);
                return i;
            }
        }
        Main.LOGGER.info("Item {} not found in inventory", item);
        return -1;
    }

    private int findPotionInInventory(BrewingStandMenu brewingStandMenu, String potionId) {
        for (int i = 5; i < 41; i++) {
            ItemStack itemStack = brewingStandMenu.getSlot(i).getItem();
            if (isPotion(itemStack, potionId)) {
                Main.LOGGER.info("Potion {} found in inventory at slot {}", potionId, i);
                return i;
            }
        }
        return -1;
    }

    private boolean isPotion(ItemStack itemStack, String potionId) {
        if (itemStack.getItem() == Items.POTION) {
            Optional<Holder<Potion>> potionOptional = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
            if (potionOptional.isPresent()) {
                Potion potion = potionOptional.get().value();
                ResourceLocation potionKey = BuiltInRegistries.POTION.getKey(potion);
                return potionKey != null && potionKey.toString().equals(potionId);
            }
        }
        return false;
    }

    private Item stringToItem(String itemString) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemString));
    }

    private int getFreeSlot(BrewingStandMenu brewingStandMenu) {
        for (int i = 5; i < 41; i++) {
            if (brewingStandMenu.getSlot(i).getItem().isEmpty()) {
                Main.LOGGER.info("Found free slot at {}", i);
                return i;
            }
        }
        return -1;
    }
}
