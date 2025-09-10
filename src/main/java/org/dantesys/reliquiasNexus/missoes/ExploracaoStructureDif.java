package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.generator.structure.StructureType;

public enum ExploracaoStructureDif {
    FACIL(StructureType.MINESHAFT),
    MEDIO(StructureType.OCEAN_MONUMENT),
    DIFICIL(StructureType.FORTRESS),
    EXPERT(StructureType.STRONGHOLD),
    INSANO(StructureType.END_CITY);

    private final StructureType structure;

    ExploracaoStructureDif(StructureType structure){
        this.structure=structure;
    }
    public static StructureType getByDif(int dif){
        return switch(dif){
            case 2 -> MEDIO.structure;
            case 3 -> DIFICIL.structure;
            case 4 -> EXPERT.structure;
            case 5 -> INSANO.structure;
            default -> FACIL.structure;
        };
    }
}
