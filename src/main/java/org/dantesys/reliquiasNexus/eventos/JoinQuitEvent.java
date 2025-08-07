package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class JoinQuitEvent implements Listener {
    FileConfiguration config = ReliquiasNexus.getPlugin(ReliquiasNexus.class).getConfig();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
        boolean novato = container.getOrDefault(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,true);
        container.set(SPECIAL.key,PersistentDataType.INTEGER,qtd);
        player.sendActionBar(Component.text("Special OK"));
        if(qtd==0 && novato){
            container.set(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,false);
            List<Nexus> reliquias = ItemsRegistro.getValidReliquia(config);
            Random rng = new Random();
            int escolhido = rng.nextInt(reliquias.size());
            Nexus n = reliquias.get(escolhido);
            String nome = n.getNome();
            config.set("nexus."+nome,player.getUniqueId().toString());
            ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();
            container.set(QTD.key,PersistentDataType.INTEGER,1);
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
            event.joinMessage(Component.text("§2 Bem-vindo ao jogo, Jogador "+player.getName()));
            player.sendMessage(Component.text("§2 Você recebeu a reliquia "+nome));
        }else{
            event.joinMessage(Component.text("§2 Bem-vindo devolta, Jogador "+player.getName()));
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ReliquiasNexus.saiu(player);
        event.quitMessage(Component.text("§4O Jogador "+player.getName()+" saiu do jogo!"));
    }
}