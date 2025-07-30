package org.dantesys.reliquiasNexus.util;

import org.bukkit.NamespacedKey;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public enum NexusKeys {
    NEXUS(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"nexus")),
    SPECIAL(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"special")),
    QTD(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"qtd_nexus")),
    DONO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"dono")),
    GUERREIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"guerreiro")),
    MISSAOGUERREIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_guerreiro"));
    public final NamespacedKey key;
    NexusKeys(NamespacedKey nexus) {
        key = nexus;
    }

    public static NamespacedKey getKey(String nome) {
        return switch (nome){
            case "guerreiro" -> GUERREIRO.key;
            default -> null;
        };
    }
}
