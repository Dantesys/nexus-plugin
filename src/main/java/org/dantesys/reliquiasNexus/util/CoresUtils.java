package org.dantesys.reliquiasNexus.util;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class CoresUtils {
    /**
     * Retorna o DyeColor correspondente ao ItemStack de corante.
     * Se o item não for corante ou for null, retorna null.
     */
    public static DyeColor getDyeColorFromItem(ItemStack item) {
        if (item == null) return null;

        switch (item.getType()) {
            case WHITE_DYE: return DyeColor.WHITE;
            case ORANGE_DYE: return DyeColor.ORANGE;
            case MAGENTA_DYE: return DyeColor.MAGENTA;
            case LIGHT_BLUE_DYE: return DyeColor.LIGHT_BLUE;
            case YELLOW_DYE: return DyeColor.YELLOW;
            case LIME_DYE: return DyeColor.LIME;
            case PINK_DYE: return DyeColor.PINK;
            case GRAY_DYE: return DyeColor.GRAY;
            case LIGHT_GRAY_DYE: return DyeColor.LIGHT_GRAY;
            case CYAN_DYE: return DyeColor.CYAN;
            case PURPLE_DYE: return DyeColor.PURPLE;
            case BLUE_DYE: return DyeColor.BLUE;
            case BROWN_DYE: return DyeColor.BROWN;
            case GREEN_DYE: return DyeColor.GREEN;
            case RED_DYE: return DyeColor.RED;
            case BLACK_DYE: return DyeColor.BLACK;
            default: return null; // não é corante
        }
    }
}
