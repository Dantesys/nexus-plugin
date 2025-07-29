package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class LimitadorEvent implements Listener {
    private void checkLimit(Player player){
        if(passou(player)){
            kaboom(player);
        }
    }
    private void kaboom(Player player){
        Location loc = player.getLocation();
        World world = player.getWorld();
        world.strikeLightning(loc);
        world.createExplosion(loc,4f,false,false);
        player.setHealth(-100);
    }
    private boolean passou(Player player){
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(QTD.key, PersistentDataType.INTEGER)){
            int qtd = container.get(QTD.key, PersistentDataType.INTEGER);
            if(qtd>3){
                return true;
            }
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
                PersistentDataContainer playerData = player.getPersistentDataContainer();
                if(playerData.has(QTD.key,PersistentDataType.INTEGER)){
                    int qtd = playerData.get(QTD.key,PersistentDataType.INTEGER);
                    qtd++;
                    playerData.set(QTD.key,PersistentDataType.INTEGER,qtd);
                    data.set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                    stack.setItemMeta(meta);
                    checkLimit(player);
                }
            }
        }
    }
    @EventHandler
    public void limitesCara(PlayerDeathEvent event){
        Player player = event.getPlayer();
        player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,0);
        if(passou(player)){
            event.deathMessage(Component.text("§c[△] O Jogador "+player.getName()+" foi eliminado por ter reliquias demais!"));
            event.deathScreenMessageOverride(Component.text("§c[△] Você foi eliminado por ter reliquias demais!"));
        }
    }
}
