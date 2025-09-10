package org.dantesys.reliquiasNexus.missoes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class Missao{
    private final MissaoTipo tipo;
    private final MissaoDificuldade dificuldade;
    private int tempo;
    private final int condicao;
    private int feito;
    private boolean ativa;
    private boolean pausa;
    private Player player;
    private final JavaPlugin plugin;
    private Material material;
    private EntityType entity;
    private StructureType structure;
    private Biome biome;
    private Scoreboard menu;
    public Missao(MissaoTipo tipo,MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin){
        this.tipo=tipo;
        this.dificuldade=dificuldade;
        this.tempo=tempo;
        this.condicao=condicao;
        this.ativa=false;
        this.plugin=plugin;
        this.feito=0;
    }
    //COLETA, MINERACAO OU LENHADOR
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,Material material,boolean eMineracao,boolean eLenhador){
        this.material=material;
        this(eMineracao?MissaoTipo.MINERACAO:eLenhador?MissaoTipo.LENHADOR:MissaoTipo.COLETA,dificuldade,tempo,condicao,plugin);
    }
    //CAÇA
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,EntityType entity){
        this.entity=entity;
        this(MissaoTipo.CACA,dificuldade,tempo,condicao,plugin);

    }
    //EXPLORAÇÃO
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,StructureType structure){
        this.structure=structure;
        this(MissaoTipo.EXPLORACAO,dificuldade,tempo,condicao,plugin);

    }
    public Missao(MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin,Biome biome){
        this.biome=biome;
        this(MissaoTipo.EXPLORACAO,dificuldade,tempo,condicao,plugin);

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
        ativa=true;
        menu = Bukkit.getScoreboardManager().getNewScoreboard();
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
        ativa=false;
        pausa=false;
        menu = Bukkit.getScoreboardManager().getMainScoreboard();
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
    private void runTempo(){
        new Temporizador(
                plugin,
                tempo,
                () -> {},
                () -> {
                    if(ativa && condicao<=feito){
                        int xp = 10*dificuldade.dificuldade;
                        int money = 30*dificuldade.dificuldade;
                        Random rng = new Random();
                        String msg = ReliquiasNexus.getLang().getString("missao.recompensa","Você recebeu <xp> de xp e <money> de <nomeMoney>!");
                        msg = msg.replace("<xp>",xp+"");
                        msg = msg.replace("<money>",money+"");
                        msg = msg.replace("<nomeMoney>",ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","Moly"));
                        player.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
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
                            NamespacedKey key = NexusKeys.getKey(nome);
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
                    menu = Bukkit.getScoreboardManager().getMainScoreboard();
                },
                (t) -> {
                    player.getPersistentDataContainer().set(MISSAOTEMPO.key,PersistentDataType.INTEGER,t.getSegundosRestantes());
                    if(ativa && condicao>feito){
                        long min = t.getSegundosRestantes() / 60;
                        long sec = t.getSegundosRestantes() % 60;
                        Objective objetivo = menu.registerNewObjective(ReliquiasNexus.getLang().getString("missao.menu", "Missão"), tipo.nome, Component.text(ReliquiasNexus.getLang().getString("missao.menu", "Missão")).decorate(TextDecoration.BOLD));
                        objetivo.setDisplaySlot(DisplaySlot.SIDEBAR);
                        Score tempo = objetivo.getScore(ChatColor.YELLOW+"⏰"+ReliquiasNexus.getLang().getString("missao.tempoRestante","Tempo Restante: <tempo>").replace("<tempo>",min + ":" + (sec < 10 ? "0" : "") + sec));
                        tempo.setScore(4);
                        Score missao = objetivo.getScore(ChatColor.YELLOW+ReliquiasNexus.getLang().getString("missao.menu", "Missão")+": "+tipo.nome);
                        missao.setScore(3);
                        Score status = objetivo.getScore(ChatColor.GREEN+ReliquiasNexus.getLang().getString("missao.progresso", "Progresso")+": "+feito+"/"+condicao);
                        status.setScore(2);
                        Score barra = objetivo.getScore(ChatColor.AQUA+getBarra());
                        barra.setScore(1);
                        player.setScoreboard(menu);
                    }else if(ativa){
                        player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.concluida","Missão Concluída com sucesso!")).color(NamedTextColor.GREEN));
                        player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                        t.stop();
                    }else if(pausa){
                        tempo=t.getSegundosRestantes();
                        t.stop();
                    }else{
                        player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.cancelada","Missão Cancelada!")).color(NamedTextColor.RED));
                        player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                        t.stop();
                    }
                }
        ).scheduleTimer(20L);
    }
    private String getBarra(){
        int tamanho=20;
        int preenchido = ((feito/condicao) *tamanho);
        StringBuilder barra = new StringBuilder();
        for(int i=0;i<tamanho;i++){
            barra.append(i<preenchido?ChatColor.GREEN:ChatColor.GRAY).append("|");
        }
        return barra.toString();
    }
}
