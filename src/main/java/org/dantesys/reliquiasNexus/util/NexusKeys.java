package org.dantesys.reliquiasNexus.util;

import org.bukkit.NamespacedKey;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.ArrayList;
import java.util.List;

public enum NexusKeys {
    NEXUS(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"nexus")),
    PROTECAO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"protecao")),
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
    MISSAOFAZENDEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_fazendeiro")),
    ESPIAO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"espiao")),
    MISSAOESPIAO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_espiao")),
    ARQUEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"arqueiro")),
    MISSAOARQUEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_arqueiro")),
    CACADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"cacador")),
    MISSAOCACADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_cacador")),
    TEMPESTADE(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"tempestade")),
    MISSAOTEMPESTADE(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_tempestade")),
    MINEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"mineiro")),
    MISSAOMINEIRO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_mineiro")),
    FENIX(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"fenix")),
    MISSAOFENIX(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_fenix")),
    PROTETOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"protetor")),
    MISSAOPROTETOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_protetor")),
    HULK(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"hulk")),
    MISSAOHULK(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_hulk")),
    SCULK(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"sculk")),
    MISSAOSCULK(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_sculk")),
    PESCADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"pescador")),
    MISSAOPESCADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_pescador")),
    FLASH(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"flash")),
    MISSAOFLASH(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_flash")),
    MAGO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"mago")),
    MISSAOMAGO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_mago")),
    LADRAO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"ladrao")),
    MISSAOLADRAO(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_ladrao")),
    DOMADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"domador")),
    MISSAODOMADOR(new NamespacedKey(ReliquiasNexus.getPlugin(ReliquiasNexus.class),"missao_domador"));
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
            case "espiao" -> ESPIAO.key;
            case "arqueiro" -> ARQUEIRO.key;
            case "cacador" -> CACADOR.key;
            case "tempestade" -> TEMPESTADE.key;
            case "mineiro" -> MINEIRO.key;
            case "fenix" -> FENIX.key;
            case "protetor" -> PROTETOR.key;
            case "hulk" -> HULK.key;
            case "sculk" -> SCULK.key;
            case "pescador" -> PESCADOR.key;
            case "flash" -> FLASH.key;
            case "mago" -> MAGO.key;
            case "ladrao" -> LADRAO.key;
            case "domador" -> DOMADOR.key;
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
        keys.add(ESPIAO.key);
        keys.add(ARQUEIRO.key);
        keys.add(CACADOR.key);
        keys.add(TEMPESTADE.key);
        keys.add(MINEIRO.key);
        keys.add(FENIX.key);
        keys.add(PROTETOR.key);
        keys.add(HULK.key);
        keys.add(SCULK.key);
        keys.add(PESCADOR.key);
        keys.add(FLASH.key);
        keys.add(MAGO.key);
        keys.add(LADRAO.key);
        keys.add(DOMADOR.key);
        return keys;
    }
}