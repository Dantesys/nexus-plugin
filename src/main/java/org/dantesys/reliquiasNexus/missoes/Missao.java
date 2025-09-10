package org.dantesys.reliquiasNexus.missoes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class Missao implements Listener {
    private final MissaoTipo tipo;
    private final MissaoDificuldade dificuldade;
    private int tempo;
    private int condicao;
    private boolean ativa;
    private boolean pausa;
    private Player player;
    private final JavaPlugin plugin;
    private Material material;
    private EntityType entity;
    private StructureType structure;
    private Biome biome;
    public Missao(MissaoTipo tipo,MissaoDificuldade dificuldade,int tempo,int condicao,JavaPlugin plugin){
        this.tipo=tipo;
        this.dificuldade=dificuldade;
        this.tempo=tempo;
        this.condicao=condicao;
        this.ativa=false;
        this.plugin=plugin;
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
    public void iniciar(Player player){
        this.player=player;
        ativa=true;
        runTempo();
    }
    public void reiniciar(){
        pausa=false;
        runTempo();
    }
    public void pausar(){
        pausa=true;
    }
    public int getDificuldade(){
        return this.dificuldade.dificuldade;
    }
    public void atualizaCondicao(){
        this.condicao=condicao-1;
    }
    private void runTempo(){
        new Temporizador(
                plugin,
                tempo,
                () -> {},
                () -> {
                    if(ativa && condicao<=0){
                        int xp = 10*dificuldade.dificuldade;
                        int money = 30*dificuldade.dificuldade;
                        String msg = ReliquiasNexus.getLang().getString("missao.recompensa","Você recebeu <xp> de xp e <money> de <nomeMoney>!");
                        msg = msg.replace("<xp>",xp+"");
                        msg = msg.replace("<money>",money+"");
                        msg = msg.replace("<nomeMoney>",ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","Moly"));
                        player.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
                        if(dificuldade.dificuldade>=4){
                            List<Nexus> reliquias = ItemsRegistro.getValidReliquia(ReliquiasNexus.getNexusConfig());
                            Random rng = new Random();
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
                },
                (t) -> {
                    player.getPersistentDataContainer().set(MISSAOTEMPO.key,PersistentDataType.INTEGER,t.getSegundosRestantes());
                    if(ativa && condicao>0){
                        long min = t.getSegundosRestantes() / 60;
                        long sec = t.getSegundosRestantes() % 60;
                        player.sendActionBar(Component.text("⏰"+ReliquiasNexus.getLang().getString("missao.tempoRestante","⏰ Tempo Restante: <tempo>").replace("<tempo>",min + ":" + (sec < 10 ? "0" : "") + sec)));
                    }else if(condicao<=0){
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
}
