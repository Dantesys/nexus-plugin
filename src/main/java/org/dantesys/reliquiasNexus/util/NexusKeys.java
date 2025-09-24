package org.dantesys.reliquiasNexus.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public enum NexusKeys {
    PROCURADO,
    MISSAOCOOLDOWN,
    TPACOOLDOWN,
    MISSAOTEMPO,
    LOJAPLAYER,
    COR,
    NEXUS,
    SLAVE,
    DRENO,
    RUGIDO,
    TOTEM,
    RENASCER,
    CHARGE,
    PROTECAO,
    SPECIAL,
    QTD,
    DONO,
    GUERREIRO,
    MISSAOGUERREIRO,
    CEIFADOR,
    MISSAOCEIFADOR,
    VIDA,
    MISSAOVIDA,
    MARES,
    MISSAOMARES,
    BARBARO,
    MISSAOBARBARO,
    FAZENDEIRO,
    MISSAOFAZENDEIRO,
    ESPIAO,
    MISSAOESPIAO,
    ARQUEIRO,
    MISSAOARQUEIRO,
    CACADOR,
    MISSAOCACADOR,
    TEMPESTADE,
    MISSAOTEMPESTADE,
    MINEIRO,
    MISSAOMINEIRO,
    FENIX,
    MISSAOFENIX,
    PROTETOR,
    MISSAOPROTETOR,
    HULK,
    MISSAOHULK,
    SCULK,
    MISSAOSCULK,
    PESCADOR,
    MISSAOPESCADOR,
    FLASH,
    MISSAOFLASH,
    MAGO,
    MISSAOMAGO,
    LADRAO,
    MISSAOLADRAO,
    DOMADOR,
    MISSAODOMADOR,
    COZINHEIRO,
    MISSAOCOZINHEIRO,
    CONSTRUTOR,
    MISSAOCONSTRUTOR,
    ABISSAL,
    MISSAOABISSAL,
    CRONOSOMBRA,
    MISSAOCRONOSOMBRA,
    ASSASSINO,
    MISSAOASSASSINO,
    FROSTIS,
    MISSAOFROSTIS,
    NECROMANTE,
    MISSAONECROMANTE,
    ALQUIMISTA,
    MISSAOALQUIMISTA,
    GOLEM,
    MISSAOGOLEM,
    DRAGAO,
    MISSAODRAGAO,
    LOJA_ITEM_KEY,
    SALDO,
    TEAM_NAME,
    TEAM_RANK,
    ENDER_CHEST_OWNED;

    public NamespacedKey key;

    public static void init(JavaPlugin plugin) {
        for (NexusKeys nexusKey : values()) {
            nexusKey.key = new NamespacedKey(plugin, nexusKey.name().toLowerCase());
        }
    }

    public static NamespacedKey getKey(String nome) {
        try {
            return valueOf(nome.toUpperCase()).key;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static List<NamespacedKey> getKeyLevel() {
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
        keys.add(COZINHEIRO.key);
        keys.add(CONSTRUTOR.key);
        keys.add(ABISSAL.key);
        keys.add(CRONOSOMBRA.key);
        keys.add(ASSASSINO.key);
        keys.add(FROSTIS.key);
        keys.add(NECROMANTE.key);
        keys.add(ALQUIMISTA.key);
        keys.add(GOLEM.key);
        keys.add(DRAGAO.key);
        return keys;
    }
}