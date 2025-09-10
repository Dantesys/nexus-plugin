package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.Material;

public enum MineracaoDif {
    FACIL(Material.COAL_ORE),
    MEDIO(Material.IRON_ORE),
    DIFICIL(Material.DEEPSLATE_REDSTONE_ORE),
    EXPERT(Material.DEEPSLATE_DIAMOND_ORE),
    INSANO(Material.DIAMOND_ORE);

    private final Material material;

    MineracaoDif(Material material){
        this.material=material;
    }
    public static Material getByDif(int dif){
        return switch(dif){
            case 2 -> MEDIO.material;
            case 3 -> DIFICIL.material;
            case 4 -> EXPERT.material;
            case 5 -> INSANO.material;
            default -> FACIL.material;
        };
    }
}
