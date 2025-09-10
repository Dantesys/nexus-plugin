package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.Material;

public enum ColetaDif {
    FACIL(Material.WHEAT_SEEDS),
    MEDIO(Material.POTATO),
    DIFICIL(Material.BROWN_MUSHROOM),
    EXPERT(Material.RED_MUSHROOM),
    INSANO(Material.TORCHFLOWER_SEEDS);

    private final Material material;

    ColetaDif(Material material){
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
