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
    MISSAOCEIFADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_ceifador")),
    VIDA(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"vida")),
    MISSAOVIDA(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_vida")),
    MARES(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"mares")),
    MISSAOMARES(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_mares")),
    BARBARO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"barbaro")),
    MISSAOBARBARO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_barbaro")),
    FAZENDEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"fazendeiro")),
    MISSAOFAZENDEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_FAZENDEIRO"));
    public final NamespacedKey key;
    NexusKeys(NamespacedKey nexus) {
        key = nexus;
    }

    public static NamespacedKey getKey(String nome) {
        return switch (nome){
            case "guerreiro" -> GUERREIRO.key;
            case "ceifador" -> CEIFADOR.key;
            case "vida" -> VIDA.key;
            case "mares" -> MARES.key;
            case "barbaro" -> BARBARO.key;
            case "fazendeiro" -> FAZENDEIRO.key;
            default -> null;
        };
    }
    public static List<NamespacedKey> getKeyLevel(){
        List<NamespacedKey> keys = new ArrayList<>();
        keys.add(GUERREIRO.key);
        keys.add(CEIFADOR.key);
        keys.add(VIDA.key);
        keys.add(MARES.key);
        keys.add(BARBARO.key);
        keys.add(FAZENDEIRO.key);
        return keys;
    }
}
