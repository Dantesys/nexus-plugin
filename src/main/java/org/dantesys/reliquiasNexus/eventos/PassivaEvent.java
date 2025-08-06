package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class PassivaEvent implements Listener {
    @EventHandler
    public void tick(ServerTickEndEvent event){
        int tick = event.getTickNumber();
        if(tick%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                PersistentDataContainer conteiner = player.getPersistentDataContainer();
                if(conteiner.has(SPECIAL.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
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
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i <= 8; i++) {
                ItemStack stack = inv.getItem(i);
                if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && nome.equals("mago")) {
                        ItemMeta meta = stack.getItemMeta();
                        NamespacedKey key = Material.WRITTEN_BOOK.getKey();
                        switch (i){
                            case 0 -> key = Material.FIRE_CHARGE.getKey();
                            case 1 -> key = Material.WIND_CHARGE.getKey();
                            case 2 -> key = Material.SNOWBALL.getKey();
                            case 3 -> key = Material.EGG.getKey();
                            case 4 -> key = Material.SPECTRAL_ARROW.getKey();
                            case 5 -> key = Material.SCULK_SHRIEKER.getKey();
                            case 6 -> key = Material.ENDER_EYE.getKey();
                            case 7 -> key = Material.BREEZE_ROD.getKey();
                            case 8 -> key = Material.BEACON.getKey();
                        }
                        meta.setItemModel(key);
                        stack.setItemMeta(meta);
                        break;
                    }
                }
            }
        });
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
