package me.kosinkadink.performantplants.util;

import me.kosinkadink.performantplants.interfaces.Droppable;
import me.kosinkadink.performantplants.plants.Drop;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DropHelper {

    public static void performDrops(Droppable droppable, Block block) {
        ArrayList<Drop> dropsList = droppable.getDrops();
        int dropLimit = droppable.getDropLimit();
        boolean limited = dropLimit >= 1;
        int dropCount = 0;
        for (Drop drop : dropsList) {
            // if there is a limit and have reached it, stop dropping
            if (limited && dropCount >= dropLimit) {
                break;
            }
            ItemStack dropStack = drop.generateDrop();
            if (dropStack.getAmount() != 0) {
                dropCount++;
                block.getWorld().dropItemNaturally(block.getLocation(), dropStack);
            }
        }
    }
}