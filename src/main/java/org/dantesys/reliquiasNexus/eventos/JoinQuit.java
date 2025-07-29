package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
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

public class JoinQuit implements Listener {
    FileConfiguration config = ReliquiasNexus.getPlugin(ReliquiasNexus.class).getConfig();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int qtd = 0;
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(QTD.key,PersistentDataType.INTEGER)){
            qtd=container.get(QTD.key, PersistentDataType.INTEGER);
        }
        container.set(SPECIAL.key,PersistentDataType.INTEGER,0);
        player.sendActionBar(Component.text("Special OK"));
        if(qtd==0){
            List<Nexus> reliquias = ItemsRegistro.getValidReliquia(config);
            Random rng = new Random();
            int escolhido = rng.nextInt(reliquias.size());
            String nome = reliquias.get(escolhido).getNome();
            config.set("nexus."+nome,player.getUniqueId());
            container.set(QTD.key,PersistentDataType.INTEGER,1);
            container.set(NexusKeys.getKey(nome),PersistentDataType.INTEGER,1);
            ItemStack stack = reliquias.get(escolhido).getItem();
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);
            event.joinMessage(Component.text("§l§0[☐] §r§2 Bem-vindo ao jogo, Jogador "+player.getName()));
        }else{
            event.joinMessage(Component.text("§l§0[☐] §r§2 Bem-vindo devolta, Jogador "+player.getName()));
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(Component.text("§l§0[☐] §r§4O Jogador "+player.getName()+" saiu do jogo!"));
    }
}