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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Optional;

public class BrewingStandHandler {

    private int delay = 4;
    private boolean carryingItem = false;
    private int ingredientAmount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().player != null) {
                if (!Config.brewingStandEnabled)
                    return;
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
            carryingItem = false;
            ingredientAmount = 0;
        }
    }

    private void handleBrewingStand(BrewingStandMenu brewingStandMenu, Player player) {
        assert Minecraft.getInstance().gameMode != null;

        int freeSlot = getFreeSlot(brewingStandMenu);
        int freeSlotsInBrewer = 0;
        if (freeSlot != -1) {
            for (int i = 0; i <= 2; i++) {
                if (!brewingStandMenu.getSlot(i).getItem().isEmpty() && isPotion(brewingStandMenu.getSlot(i).getItem(), Config.alchemyPotionEffect)){
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, i, 0, ClickType.QUICK_MOVE, player);
                    Main.LOGGER.info("Moved potion to slot {}", freeSlot);
                    return;
                }
                if (brewingStandMenu.getSlot(i).getItem().isEmpty())
                    freeSlotsInBrewer++;
            }
        }

        int potionType = 0;
        ItemStack potion = brewingStandMenu.getSlot(0).getItem();
        ArrayList<Slot> awkwardPotionSlots = findPotionInInventory(brewingStandMenu, "minecraft:awkward");
        ArrayList<Slot> waterPotionSlots = findPotionInInventory(brewingStandMenu, "minecraft:water");
        if (!potion.isEmpty()) {
            if (isPotion(potion, "minecraft:water")) {
                potionType = 1;
            } else if (isPotion(potion, "minecraft:awkward")) {
                potionType = 2;
            }
        } else {
            if (waterPotionSlots.size() >= 3) {
                potionType = 1;
            } else if (awkwardPotionSlots.size() >= 3) {
                potionType = 2;
            }
        }

        Main.LOGGER.info("Potion type: {}", potionType);

        for (int i = 0; i <= 2; i++) {
            if (brewingStandMenu.getSlot(i).getItem().isEmpty()) {
                Main.LOGGER.info("Found empty slot at {}", i);
                if (potionType == 1) {
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, waterPotionSlots.getFirst().index, 0, ClickType.QUICK_MOVE, player);
                    Main.LOGGER.info("Moved water potion to slot {}", i);
                } else if (potionType == 2) {
                    if (awkwardPotionSlots.size() >= 3 - i) {
                        Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, awkwardPotionSlots.getFirst().index, 0, ClickType.QUICK_MOVE, player);
                        Main.LOGGER.info("Moved awkward potion to slot {}", i);
                    }
                }
                return;
            }
        }

        if (freeSlotsInBrewer != 0) {
            Main.LOGGER.info("Free slots in brewer: {}", freeSlotsInBrewer);
            return;
        }

        if (isPotion(potion, "minecraft:water")) {
            int prepareSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyPrepareItem));
            ingredientAmount = prepareSlot != -1 ? brewingStandMenu.getSlot(prepareSlot).getItem().getCount() : 0;
            if (brewingStandMenu.getSlot(3).getItem().isEmpty()) {
                if (!carryingItem && prepareSlot != -1) {
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, prepareSlot, 0, ClickType.PICKUP, player);
                    carryingItem = true;
                    Main.LOGGER.info("Grabbed {} from the inventory", Config.alchemyPrepareItem);
                    return;
                } else if (carryingItem) {
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, 3, 1, ClickType.PICKUP, player);
                    carryingItem = ingredientAmount > 1;
                    Main.LOGGER.info("Moved {} to the prepare slot", Config.alchemyPrepareItem);
                    return;
                }
            } else if (carryingItem && ingredientAmount > 1) {
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, prepareSlot, 0, ClickType.PICKUP, player);
                carryingItem = false;
                Main.LOGGER.info("Moved {} to the prepare slot", Config.alchemyPrepareItem);
                return;
            }
        }

        if (isPotion(potion, "minecraft:awkward")) {
            int ingredientSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyIngredientItem));
            ingredientAmount = ingredientSlot != -1 ? brewingStandMenu.getSlot(ingredientSlot).getItem().getCount() : 0;
            if (brewingStandMenu.getSlot(3).getItem().isEmpty()) {
                if (!carryingItem && ingredientSlot != -1) {
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, ingredientSlot, 0, ClickType.PICKUP, player);
                    carryingItem = true;
                    Main.LOGGER.info("Grabbed {} from the inventory", Config.alchemyIngredientItem);
                    return;
                } else if (carryingItem) {
                    Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, 3, 1, ClickType.PICKUP, player);
                    carryingItem = ingredientAmount > 1;
                    Main.LOGGER.info("Moved {} to the ingredient slot", Config.alchemyIngredientItem);
                    return;
                }
            } else if (carryingItem && ingredientAmount > 1) {
                Minecraft.getInstance().gameMode.handleInventoryMouseClick(brewingStandMenu.containerId, ingredientSlot, 0, ClickType.PICKUP, player);
                carryingItem = false;
                Main.LOGGER.info("Moved {} to the ingredient slot", Config.alchemyIngredientItem);
                return;
            }
        }

        if (brewingStandMenu.getSlot(4).getItem().isEmpty()) {
            int fuelSlot = findItemInInventory(brewingStandMenu, stringToItem(Config.alchemyFuelItem));
            if (fuelSlot != -1) {
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

    private ArrayList<Slot> findPotionInInventory(BrewingStandMenu brewingStandMenu, String potionId) {
        ArrayList<Slot> potionSlots = new ArrayList<>();
        for (int i = 5; i < 41; i++) {
            ItemStack itemStack = brewingStandMenu.getSlot(i).getItem();
            if (isPotion(itemStack, potionId)) {
                Main.LOGGER.info("Potion {} found in inventory at slot {}", potionId, i);
                potionSlots.add(brewingStandMenu.getSlot(i));
            }
        }
        return potionSlots;
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
