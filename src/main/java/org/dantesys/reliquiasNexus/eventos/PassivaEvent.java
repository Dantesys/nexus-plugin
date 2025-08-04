package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;

import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class PassivaEvent implements Listener {
    @EventHandler
    public void tick(ServerTickEndEvent event){
        int tick = event.getTickNumber();
        if(tick%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                PersistentDataContainer conteiner = player.getPersistentDataContainer();
                if(conteiner.has(SPECIAL.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo<=0){
                        player.sendActionBar(Component.text("Special: OK"));
                    }else{
                        tempo--;
                        conteiner.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("Special em "+tempo+"s"));
                    }
                }else{
                    conteiner.set(SPECIAL.key, PersistentDataType.INTEGER,0);
                }
                PlayerInventory pinv = player.getInventory();
                PersistentDataContainerView data = pinv.getItemInMainHand().getPersistentDataContainer();
                aplicaEfeito(data,player);
                data = pinv.getItemInOffHand().getPersistentDataContainer();
                aplicaEfeito(data,player);
                ItemStack stack = pinv.getHelmet();
                if(stack!=null){
                    data = pinv.getItemInOffHand().getPersistentDataContainer();
                    aplicaEfeito(data,player);
                }
                stack = pinv.getChestplate();
                if(stack!=null){
                    data = pinv.getItemInOffHand().getPersistentDataContainer();
                    aplicaEfeito(data,player);
                }
                stack = pinv.getLeggings();
                if(stack!=null){
                    data = pinv.getItemInOffHand().getPersistentDataContainer();
                    aplicaEfeito(data,player);
                }
                stack = pinv.getBoots();
                if(stack!=null){
                    data = pinv.getItemInOffHand().getPersistentDataContainer();
                    aplicaEfeito(data,player);
                }
            });
        }
    }
    private void aplicaEfeito(PersistentDataContainerView data, Player player){
        if(data.has(NEXUS.key)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            Nexus nexus = ItemsRegistro.getFromNome(nome!=null?nome:"");
            if(nexus!=null){
                player.addPotionEffects(nexus.getEfeitos());
            }
        }
    }
}
