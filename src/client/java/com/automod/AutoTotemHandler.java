package com.automod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotemHandler {

    private static int cooldown = 0;

    public static void tick(MinecraftClient client) {
        if (!AutoMod.autoTotemEnabled || client.player == null) return;
        if (cooldown > 0) { cooldown--; return; }

        ItemStack offhand = client.player.getOffHandStack();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) return;

        PlayerInventory inv = client.player.getInventory();
        int totemSlot = -1;

        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot < 0) return;

        boolean openedInv = false;
        if (AutoMod.totemOpenInv && client.currentScreen == null) {
            client.setScreen(new InventoryScreen(client.player));
            openedInv = true;
        }

        // Hotbar: inv 0-8 → screen 36-44 | Inventory: inv 9-35 → screen 9-35
        int screenSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
        int offhandScreenSlot = 45;

        var handler = client.player.currentScreenHandler;

        client.interactionManager.clickSlot(handler.syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(handler.syncId, offhandScreenSlot, 0, SlotActionType.PICKUP, client.player);

        if (!handler.getCursorStack().isEmpty()) {
            client.interactionManager.clickSlot(handler.syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
        }

        if (openedInv && client.currentScreen != null) {
            client.currentScreen.close();
        }

        cooldown = AutoMod.totemDelay;
    }
}
