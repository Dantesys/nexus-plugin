package org.dantesys.reliquiasNexus.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import static org.bukkit.Material.*;

public class BlocoUtils {
    public static Material getBlocoColorido(Material tipoBase, DyeColor cor) {
        // Se cor for nula, assume branco
        if((tipoBase == GLASS || tipoBase == GLASS_PANE) && cor==null){
            return tipoBase;
        }else{
            if(tipoBase == GLASS){
                tipoBase = WHITE_STAINED_GLASS;
            }
            if(tipoBase == GLASS_PANE){
                tipoBase = WHITE_STAINED_GLASS_PANE;
            }
        }
        if (cor == null) {
            cor = DyeColor.WHITE;
        }

        switch (tipoBase) {
            case WHITE_CONCRETE:
                return Material.valueOf(cor.name() + "_CONCRETE");
            case TERRACOTTA:
                return Material.valueOf(cor.name() + "_TERRACOTTA");
            case WHITE_WOOL:
                return Material.valueOf(cor.name() + "_WOOL");
            case WHITE_STAINED_GLASS:
                return Material.valueOf(cor.name() + "_STAINED_GLASS");
            case WHITE_STAINED_GLASS_PANE:
                return Material.valueOf(cor.name() + "_STAINED_GLASS_PANE");
            case GREEN_TERRACOTTA:
                return Material.valueOf(cor.name() + "_TERRACOTTA");
            default:
                return tipoBase; // blocos não coloridos permanecem
        }
    }
}
