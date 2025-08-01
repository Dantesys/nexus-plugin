package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Map;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class VidaEvent implements Listener {
    Map<Integer, Double> level = Map.of(
            1, 25d,
            2, 50d,
            3, 75d,
            4, 100d
    );
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
        double recuperacaoNescessaria = level.get(levelAtual) * levelAtual;
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.giveExp(-10 * levelAtual);
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
            dataPlayer.set(VIDA.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    if(meta.hasEnchants()){
                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                    }
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus da Vida evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP ou ganhar mais "+(recuperacaoNescessaria-recuperacao)+" de vida para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        double recuperacaoNescessaria = level.get(levelAtual) * levelAtual;
        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
        return player.getTotalExperience() >= xpRequerido && recuperacao >= recuperacaoNescessaria;
    }
    @EventHandler
    public void recuperaVida(EntityRegainHealthEvent event){
        Entity e = event.getEntity();
        if(e instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(stack.getType() != Material.TOTEM_OF_UNDYING){
                stack = player.getInventory().getItemInOffHand();
            }
            if(stack.getType() != Material.TOTEM_OF_UNDYING)return;
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("vida")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                        int level = dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                        n.setLevel(level);
                        recuperacao+=event.getAmount();
                        dataPlayer.set(MISSAOVIDA.key,PersistentDataType.DOUBLE,recuperacao);
                        tentarEvoluir(player,n.getItem(level),level);
                    }
                }
            }else{
                stack = player.getInventory().getItemInOffHand();
                meta = stack.getItemMeta();
                data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)) {
                    String nome = data.get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank() && nome.equals("vida")) {
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if (n != null) {
                            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                            double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                            int level = dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1);
                            n.setLevel(level);
                            recuperacao += event.getAmount();
                            dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, recuperacao);
                            tentarEvoluir(player, n.getItem(level), level);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void reviver(EntityResurrectEvent e) {
        LivingEntity deadEntity = e.getEntity();
        if(deadEntity instanceof Player player){
            PlayerInventory pinv = player.getInventory();
            ItemStack item = pinv.getItemInMainHand();
            ItemStack item2 = pinv.getItemInOffHand();
            PersistentDataContainerView data = item.getPersistentDataContainer();
            PersistentDataContainerView data2 = item2.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING) || data2.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome==null || nome.isBlank() || !nome.equals("vida")){
                    nome = data2.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome==null || nome.isBlank()|| !nome.equals("vida")){
                        return;
                    }
                }
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n==null)return;
                if(dataPlayer.has(SPECIAL.key, PersistentDataType.INTEGER)){
                    int countDown = dataPlayer.getOrDefault(SPECIAL.key, PersistentDataType.INTEGER,0);
                    if(countDown>0){
                        e.setCancelled(true);
                        return;
                    }
                    int level = dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1);
                    int max = n.getMaxLevel();
                    int tempo = (max/level)*5;
                    player.getInventory().setItemInMainHand(item);
                    player.getInventory().setItemInOffHand(item2);
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class),
                            tempo,
                            () -> player.sendActionBar(Component.text("Habilidade do Nexus da Vida Ativado!")),
                            () -> {},
                            (t) -> {}
                    );
                    timer.scheduleTimer(20L);
                    dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
                }
            }
        }
    }
}
