package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MissoesManager {
    private JavaPlugin plugin;
    public MissoesManager(JavaPlugin plugin){
        this.plugin=plugin;
    }
    public void gerarNovaMissao(Player player){}
    public void cancelarMissao(Player player){}
}
