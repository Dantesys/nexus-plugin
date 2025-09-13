package org.dantesys.reliquiasNexus.missoes;

import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.jetbrains.annotations.Nullable;

public enum MissaoTipo {
    COLETA(ReliquiasNexus.getLang().getString("missao.tipos.coleta","Coleta")),
    CACA(ReliquiasNexus.getLang().getString("missao.tipos.caca","Caça")),
    MINERACAO(ReliquiasNexus.getLang().getString("missao.tipos.mineracao","Mineração")),
    LENHADOR(ReliquiasNexus.getLang().getString("missao.tipos.lenhador","Lenhador")),
    EXPLORACAO(ReliquiasNexus.getLang().getString("missao.tipos.exploracao","Exploração"));

    public final String nome;

    MissaoTipo(String nome){
        this.nome=nome;
    }

    public static MissaoTipo get(@Nullable String string) {
        for(MissaoTipo tipo: values()){
            if(tipo.nome.equals(string)){
                return tipo;
            }
        }
        return COLETA;
    }
}