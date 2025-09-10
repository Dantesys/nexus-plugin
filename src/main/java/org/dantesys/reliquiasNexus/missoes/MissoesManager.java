package org.dantesys.reliquiasNexus.missoes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class MissoesManager {
    private final JavaPlugin plugin;
    public MissoesManager(JavaPlugin plugin){
        this.plugin=plugin;
    }
    public List<Missao> gerarMissoes(Player player){
        Random rd = new Random();
        int level = player.getLevel();
        MissaoDificuldade missaoDificuldade = MissaoDificuldade.getByLevel(level);
        int dif = missaoDificuldade.dificuldade;
        Material coleta = ColetaDif.getByDif(rd.nextInt(1,dif));
        Missao missaoColeta = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,coleta,false,false);
        Material mineracao = MineracaoDif.getByDif(rd.nextInt(1,dif));
        Missao missaoMineracao = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,mineracao,true,false);
        Material lenhador = LenhadorDif.getByDif(rd.nextInt(1,dif));
        Missao missaoLenhador = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,lenhador,false,true);
        EntityType caca = CacaDif.getByDif(rd.nextInt(1,dif));
        Missao missaoCaca = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,caca);
        StructureType structure = ExploracaoStructureDif.getByDif(rd.nextInt(1,dif));
        Missao missaoStructure = new Missao(missaoDificuldade,720*level*2,1,plugin,structure);
        Biome biome = ExploracaoBiomeDif.getByDif(rd.nextInt(1,dif));
        Missao missaoBiome = new Missao(missaoDificuldade,720*level*2,1,plugin,biome);
        return List.of(missaoColeta,missaoMineracao,missaoLenhador,missaoCaca,missaoStructure,missaoBiome);
    }
    public void aceitarMissao(Player player,Missao missao){}
    public void cancelarMissao(Player player,Missao missao){}
}
