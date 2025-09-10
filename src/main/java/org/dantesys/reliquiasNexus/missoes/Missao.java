package org.dantesys.reliquiasNexus.missoes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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

import static org.dantesys.reliquiasNexus.util.NexusKeys.DONO;
import static org.dantesys.reliquiasNexus.util.NexusKeys.QTD;

public class Missao {
    private final String tipo;
    private final int dificuldade;
    private final int tempo;
    private int condicao;
    private boolean ativa;
    private Player player;
    private final JavaPlugin plugin;
    public Missao(String tipo,int dificuldade,int tempo,int condicao,JavaPlugin plugin){
        this.tipo=tipo;
        this.dificuldade=dificuldade;
        this.tempo=tempo;
        this.condicao=condicao;
        this.ativa=false;
        this.plugin=plugin;
    }
    public String getTipo(){
        return tipo;
    }
    public void iniciar(Player player){
        this.player=player;
        ativa=true;
        runTempo();
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
                        int xp = 10*dificuldade;
                        int money = 30*dificuldade;
                        String msg = ReliquiasNexus.getLang().getString("missao.recompensa","Você recebeu <xp> de xp e <money> de <nomeMoney>!");
                        msg = msg.replace("<xp>",xp+"");
                        msg = msg.replace("<money>",money+"");
                        msg = msg.replace("<nomeMoney>",ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","Moly"));
                        player.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
                        if(dificuldade>=9){
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
                    if(ativa && condicao>0){
                        player.sendActionBar(Component.text("⏰"+ReliquiasNexus.getLang().getString("missao.tempoRestante","Falta <segundo> segundos para acabar o tempo!").replace("<segundo>",t.getSegundosRestantes()+"")));
                    }else if(condicao<=0){
                        player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.concluida","Missão Concluída com sucesso!")).color(NamedTextColor.GREEN));
                        t.stop();
                    }else{
                        player.sendMessage(Component.text(ReliquiasNexus.getLang().getString("missao.cancelada","Missão Cancelada!")).color(NamedTextColor.RED));
                        t.stop();
                    }
                }
        ).scheduleTimer(20L);
    }
}
