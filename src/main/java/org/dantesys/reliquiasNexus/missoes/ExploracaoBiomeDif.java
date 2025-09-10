package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.block.Biome;
import org.bukkit.generator.structure.StructureType;

public enum ExploracaoBiomeDif {
    FACIL(Biome.PLAINS),
    MEDIO(Biome.DESERT),
    DIFICIL(Biome.BAMBOO_JUNGLE),
    EXPERT(Biome.MUSHROOM_FIELDS),
    INSANO(Biome.ICE_SPIKES);

    private final Biome bioma;

    ExploracaoBiomeDif(Biome bioma){
        this.bioma=bioma;
    }
    public static Biome getByDif(int dif){
        return switch(dif){
            case 2 -> MEDIO.bioma;
            case 3 -> DIFICIL.bioma;
            case 4 -> EXPERT.bioma;
            case 5 -> INSANO.bioma;
            default -> FACIL.bioma;
        };
    }
}
