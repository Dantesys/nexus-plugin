package org.dantesys.reliquiasNexus.util;

import org.bukkit.NamespacedKey;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.ArrayList;
import java.util.List;

public enum NexusKeys {
    NEXUS(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"nexus")),
    SPECIAL(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"special")),
    QTD(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"qtd_nexus")),
    DONO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"dono")),
    GUERREIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"guerreiro")),
    MISSAOGUERREIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_guerreiro")),
    CEIFADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"ceifador")),
    MISSAOCEIFADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_ceifador"));
    public final NamespacedKey key;
    NexusKeys(NamespacedKey nexus) {
        key = nexus;
    }

    public static NamespacedKey getKey(String nome) {
        return switch (nome){
            case "guerreiro" -> GUERREIRO.key;
            case "ceifador" -> CEIFADOR.key;
            default -> null;
        };
    }
    public static List<NamespacedKey> getKeyLevel(){
        List<NamespacedKey> keys = new ArrayList<>();
        keys.add(GUERREIRO.key);
        keys.add(CEIFADOR.key);
        return keys;
    }
}
