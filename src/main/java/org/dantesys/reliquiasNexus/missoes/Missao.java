package org.dantesys.reliquiasNexus.missoes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;

import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class Missao{
    private final MissaoTipo tipo;
    private final MissaoDificuldade dificuldade;
    private int tempo;
    private final int condicao;
    private int feito;
    private boolean pausa;
    private Player player;
    private final JavaPlugin plugin;
    private Material material;
    private EntityType entity;
    private StructureType structure;
    private Biome biome;
    private boolean cancel;
    public Missao(MissaoTipo tipo,MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin){
        this.tipo=tipo;
        this.dificuldade=dificuldade;
        this.tempo=tempo;
        this.condicao=Math.max(condicao, 1);
        this.plugin=plugin;
        this.feito=0;
    }
    //COLETA, MINERACAO OU LENHADOR
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,Material material,boolean eMineracao,boolean eLenhador){
        this(eMineracao?MissaoTipo.MINERACAO:eLenhador?MissaoTipo.LENHADOR:MissaoTipo.COLETA,dificuldade,tempo,condicao,plugin);
        this.material=material;
    }
    //CAÇA
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,EntityType entity){
        this(MissaoTipo.CACA,dificuldade,tempo,condicao,plugin);
        this.entity=entity;
    }
    //EXPLORAÇÃO
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,StructureType structure){
        this(MissaoTipo.EXPLORACAO,dificuldade,tempo,condicao,plugin);
        this.structure=structure;
    }
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,Biome biome){
        this(MissaoTipo.EXPLORACAO,dificuldade,tempo,condicao,plugin);
        this.biome=biome;
    }
    public String getTipo(){
        return tipo.nome;
    }
    public Object get(String opcao){
        return switch (opcao){
            case "entity" -> entity;
            case "structure" -> structure;
            case "biome" -> biome;
            default -> material;
        };
    }
    public void iniciar(Player player){
        this.player=player;
        runTempo();
    }
    public void reiniciar(){
        pausa=false;
        runTempo();
    }
    public void pausar(){
        pausa=true;
    }
    public void cancelar(){
        pausa=false;
        cancel=true;
    }
    public int getDificuldade(){
        return this.dificuldade.dificuldade;
    }
    public void atualizaCondicao(){
        this.feito=feito+1;
    }
    public void atualizaCondicao(int i){
        this.feito=feito+i;
    }
    private void entregaRecompensa(){
        int xp = 10*dificuldade.dificuldade;
        int money = 30*dificuldade.dificuldade;
        Random rng = new Random();
        String msg = ReliquiasNexus.getLang().getString("missao.recompensa","Você recebeu <xp> de xp e <money> de <nomeMoney>!");
        msg = msg.replace("<xp>",xp+"");
        msg = msg.replace("<money>",money+"");
        msg = msg.replace("<nomeMoney>",ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","Moly"));
        player.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
        player.giveExp(xp);
        double real = player.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0d);
        player.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,real+money);
        if(dificuldade.dificuldade>4 || (dificuldade.dificuldade==4 && rng.nextInt(100)>=90)){
            List<Nexus> reliquias = ItemsRegistro.getValidReliquia(ReliquiasNexus.getNexusConfig());
            int escolhido = rng.nextInt(reliquias.size());
            Nexus n = reliquias.get(escolhido);
            String nome = n.getNome();
            ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
            plugin.saveConfig();
            PersistentDataContainer container = player.getPersistentDataContainer();
            container.set(QTD.key, PersistentDataType.INTEGER,1);
            int level =1;
            NamespacedKey key = getKey(nome);
            if(key!=null && container.has(key,PersistentDataType.INTEGER)){
                level=container.getOrDefault(key,PersistentDataType.INTEGER,1);
            }else if(key!=null){
                container.set(key,PersistentDataType.INTEGER,1);
            }
            ItemStack stack = n.getItem(level);
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);
            player.giveExpLevels(dificuldade.dificuldade>4?-100:-74);
            String r=ReliquiasNexus.getLang().getString("joinquit.relic");
            if(r==null){
                r="Você recebeu a relíquia do <relic>";
            }
            r=r.replace("<relic>",nome);
            player.sendMessage(Component.text("§2"+r));
        }
    }
    private void runTempo(){
        MissaoScoreboard missaoSB = new MissaoScoreboard(player);
        missaoSB.updateMissao(tipo.nome);
        missaoSB.updateMeta(getMeta());
        missaoSB.updateProgresso(feito,condicao);
        new BukkitRunnable() {
            int tempoRestante = tempo;
            @Override
            public void run() {
                // Tick normal
                player.getPersistentDataContainer().set(MISSAOTEMPO.key, PersistentDataType.INTEGER, tempoRestante);
                long displayTime = Math.max(0, tempoRestante);
                missaoSB.updateTempo(displayTime / 60 + ":" + (displayTime % 60 < 10 ? "0" : "") + displayTime % 60);
                missaoSB.updateProgresso(feito, condicao);
                if (cancel) {
                    player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.cancelada","Missão Cancelada!")).color(NamedTextColor.RED));
                    player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                    // Limpa o scoreboard do jogador
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard vazio = manager.getNewScoreboard();
                    player.setScoreboard(vazio);
                    cancel();
                    return;
                }
                if (pausa) {
                    tempo = tempoRestante;
                    cancel();
                    return;
                }
                if (feito >= condicao) {
                    player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.concluida","Missão Concluída com sucesso!")).color(NamedTextColor.GREEN));
                    player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                    entregaRecompensa(); // seu bloco de recompensa
                    // Limpa o scoreboard do jogador
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard vazio = manager.getNewScoreboard();
                    player.setScoreboard(vazio);
                    cancel();
                    return;
                }
                tempoRestante--;
                if (tempoRestante<0) {
                    player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.cancelada","Missão Cancelada!")).color(NamedTextColor.RED));
                    player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                    // Limpa o scoreboard do jogador
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard vazio = manager.getNewScoreboard();
                    player.setScoreboard(vazio);
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    private String getMeta(){
        return switch (tipo){
            case COLETA,MINERACAO,LENHADOR -> material.name();
            case CACA -> entity.name();
            case EXPLORACAO -> structure!=null?structure.toString():biome.translationKey();
        };
    }
}
