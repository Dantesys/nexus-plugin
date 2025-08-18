package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class PassivaEvent implements Listener {
    @EventHandler
    public void reviver(EntityResurrectEvent e) {
        LivingEntity deadEntity = e.getEntity();
        if(deadEntity instanceof Player player){
            player.getPersistentDataContainer().set(PROTECAO.key,PersistentDataType.STRING,"");
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
                if(dataPlayer.has(TOTEM.key, PersistentDataType.INTEGER)){
                    int countDown = dataPlayer.getOrDefault(TOTEM.key, PersistentDataType.INTEGER,0);
                    if(countDown>0){
                        e.setCancelled(true);
                        return;
                    }
                    int tempo = 120;
                    player.getInventory().setItemInMainHand(item);
                    player.getInventory().setItemInOffHand(item2);
                    dataPlayer.set(TOTEM.key,PersistentDataType.INTEGER,tempo);
                }
            }
        }
    }
    @EventHandler
    public void tick(ServerTickEndEvent event){
        int tick = event.getTickNumber();
        if(tick%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                PersistentDataContainer conteiner = player.getPersistentDataContainer();
                LimitadorEvent.checkLimit(player);
                if(conteiner.has(SPECIAL.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("Special "+tempo+"s"));
                    }
                }else{
                    conteiner.set(SPECIAL.key, PersistentDataType.INTEGER,0);
                }
                if(conteiner.has(DRENO.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(DRENO.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("♡ "+tempo+"s"));
                    }
                }else{
                    conteiner.set(DRENO.key, PersistentDataType.INTEGER,0);
                }
                if(conteiner.has(TOTEM.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(TOTEM.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("♡ "+tempo+"s"));
                    }
                }else{
                    conteiner.set(TOTEM.key, PersistentDataType.INTEGER,0);
                }
                PlayerInventory pinv = player.getInventory();
                pinv.forEach(stack -> {
                    if(stack!=null){
                        PersistentDataContainerView data = stack.getPersistentDataContainer();
                        aplicaEfeito(data,player);
                    }
                });
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
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            Nexus nexus = ItemsRegistro.getFromNome(nome!=null?nome:"");
            if(nexus!=null && nome!=null){
                switch(nome){
                    case "guerreiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
                        Guerreiro.getPassivabyLevel(level,player);
                    }
                    case "ceifador" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                        Ceifador.getPassivabyLevel(level,player);
                    }
                    case "vida" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                        Vida.getPassivabyLevel(level,player);
                    }
                    case "mares" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
                        Mares.getPassivabyLevel(level,player);
                    }
                    case "barbaro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
                        Barbaro.getPassivabyLevel(level,player);
                    }
                    case "fazendeiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                        Fazendeiro.getPassivabyLevel(level,player);
                    }
                    case "espiao" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,1);
                        Espiao.getPassivabyLevel(level,player);
                    }
                    case "arqueiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
                        Arqueiro.getPassivabyLevel(level,player);
                    }
                    case "cacador" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
                        Cacador.getPassivabyLevel(level,player);
                    }
                }
            }
        }
    }
}
