package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.Material;

public enum LenhadorDif {
    FACIL(Material.OAK_LOG),
    MEDIO(Material.BIRCH_LOG),
    DIFICIL(Material.CHERRY_LOG),
    EXPERT(Material.PALE_OAK_LOG),
    INSANO(Material.MANGROVE_LOG);

    private final Material material;

    LenhadorDif(Material material){
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
