package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.entity.EntityType;

public enum CacaDif {
    FACIL(EntityType.ZOMBIE),
    MEDIO(EntityType.SKELETON),
    DIFICIL(EntityType.BREEZE),
    EXPERT(EntityType.PIGLIN_BRUTE),
    INSANO(EntityType.WARDEN);

    private final EntityType entity;

    CacaDif(EntityType entity){
        this.entity=entity;
    }
    public static EntityType getByDif(int dif){
        return switch(dif){
            case 2 -> MEDIO.entity;
            case 3 -> DIFICIL.entity;
            case 4 -> EXPERT.entity;
            case 5 -> INSANO.entity;
            default -> FACIL.entity;
        };
    }
}
