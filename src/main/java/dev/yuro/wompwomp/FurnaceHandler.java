package dev.yuro.wompwomp;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FurnaceHandler {

    private static int delay = 4;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (event.phase == TickEvent.Phase.END) {
            if (player != null) {
                if (delay > 0) {
                    delay--;
                    return;
                }
                delay = Config.delay;
                AbstractContainerMenu container = player.containerMenu;
                if (container instanceof FurnaceMenu furnaceMenu) {
                    handleFurnace(furnaceMenu, player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        AbstractContainerMenu container = event.getContainer();
        if (container instanceof FurnaceMenu) {
            delay = Config.delay;
        }
    }

    private static void handleFurnace(FurnaceMenu furnaceMenu, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (furnaceMenu.getSlot(0).getItem().isEmpty()) {
            int smeltSlot = findItemInInventory(furnaceMenu, stringToItem(Config.furnanceSmeltItem));
            if (smeltSlot != -1) {
                assert minecraft.gameMode != null;
                minecraft.gameMode.handleInventoryMouseClick(furnaceMenu.containerId, smeltSlot, 0, ClickType.QUICK_MOVE, player);
                Main.LOGGER.info("Moved {} to the input slot", Config.furnanceSmeltItem);
                return;
            }
        }
        if (furnaceMenu.getSlot(1).getItem().isEmpty()) {
            int fuelSlot = findItemInInventory(furnaceMenu, stringToItem(Config.furnanceFuelItem));
            if (fuelSlot != -1) {
                assert minecraft.gameMode != null;
                minecraft.gameMode.handleInventoryMouseClick(furnaceMenu.containerId, fuelSlot, 1, ClickType.QUICK_MOVE, player);
                Main.LOGGER.info("Moved {} to the fuel slot", Config.furnanceFuelItem);
                return;
            }
        }
        ItemStack outputStack = furnaceMenu.getSlot(2).getItem();
        if (!outputStack.isEmpty()) {
            assert minecraft.gameMode != null;
            minecraft.gameMode.handleInventoryMouseClick(furnaceMenu.containerId, 2, 0, ClickType.QUICK_MOVE, player);
            Main.LOGGER.info("Moved smelted item to inventory");
            return;
        }
        if (Config.closeInventory)
            player.closeContainer();
    }

    private static int findItemInInventory(FurnaceMenu furnaceMenu, Item item) {
        for (int i = 3; i < 39; i++) {
            if (furnaceMenu.getSlot(i).getItem().getItem() == item) {
                Main.LOGGER.info("Item {} found in inventory at slot {}", item, i);
                return i;
            }
        }
        Main.LOGGER.info("Item {} not found in inventory", item);
        return -1;
    }

    private static Item stringToItem(String itemString) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemString));
    }
}