package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class LimitadorEvent implements Listener {
    FileConfiguration config = ReliquiasNexus.getPlugin(ReliquiasNexus.class).getConfig();
    private void checkLimit(Player player){
        if(passou(player)){
            kaboom(player);
        }else{
            player.sendActionBar(Component.text("§cNão seja um colecionador"));
        }
    }
    private void kaboom(Player player){
        Location loc = player.getLocation();
        World world = player.getWorld();
        world.createExplosion(loc,5f,false,false);
        player.setHealth(0);
    }
    private boolean passou(Player player){
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(QTD.key, PersistentDataType.INTEGER)){
            int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
            return qtd > 3;
        }
        return false;
    }
    @EventHandler
    public void pegouChao(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player player){
            ItemStack stack = event.getItem().getItemStack();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank()){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    PersistentDataContainer playerData = player.getPersistentDataContainer();
                    if(playerData.has(QTD.key,PersistentDataType.INTEGER) && n!=null){
                        int qtd = playerData.getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
                        qtd++;
                        int level=1;
                        NamespacedKey key = NexusKeys.getKey(nome);
                        if(key!=null && playerData.has(key,PersistentDataType.INTEGER)){
                            level=playerData.getOrDefault(key,PersistentDataType.INTEGER,1);
                        }
                        stack = n.getItem(level);
                        meta = stack.getItemMeta();
                        data = meta.getPersistentDataContainer();
                        data.set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                        playerData.set(QTD.key,PersistentDataType.INTEGER,qtd);
                        config.set("nexus."+nome,player.getUniqueId().toString());
                        checkLimit(player);
                    }
                }
            }
        }
    }
    @EventHandler
    public void limitesCara(PlayerDeathEvent event){
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        for(ItemStack item: inv.getContents()){
            if(item!=null){
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING) && data.has(DONO.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    String uuidStr = data.get(DONO.key,PersistentDataType.STRING);
                    if(uuidStr!=null && !uuidStr.isBlank()){
                        UUID uuid = UUID.fromString(uuidStr);
                        if(player.getUniqueId()==uuid){
                            data.set(DONO.key,PersistentDataType.STRING,"");
                            config.set("nexus."+nome,"");
                        }
                    }
                }
            }
        }
        if(passou(player)){
            event.deathMessage(Component.text("§cO Jogador "+player.getName()+" foi eliminado por ter reliquias demais!"));
            event.deathScreenMessageOverride(Component.text("§cVocê foi eliminado por ter reliquias demais, se controla cara!"));
        }
        player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,0);
    }
}
