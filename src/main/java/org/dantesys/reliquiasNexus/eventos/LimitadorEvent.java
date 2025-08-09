package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;


import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class LimitadorEvent implements Listener {
    private final ReliquiasNexus plugin;
    public LimitadorEvent(ReliquiasNexus plugin){
        this.plugin=plugin;
    }
    private final Map<UUID, List<ItemStack>> reliquiasSalvas = new HashMap<>();
    public static void checkLimit(Player player){
        if(passou(player)){
            player.setHealth(0);
        }else{
            player.sendActionBar(Component.text("§cNão seja um colecionador"));
        }
    }
    private static boolean passou(Player player){
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(QTD.key, PersistentDataType.INTEGER)){
            int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
            int limite = ReliquiasNexus.getNexusConfig().getInt("limite");
            return qtd > limite;
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
                        ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
                        plugin.saveConfig();
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
        List<ItemStack> manterRelics = new ArrayList<>();
        for(ItemStack item: inv.getContents()){
            if(item!=null){
                ItemMeta meta = item.getItemMeta();
                if(meta!=null){
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if(data.has(NEXUS.key,PersistentDataType.STRING)){
                        Player assasino = player.getKiller();
                        boolean expurgo = ReliquiasNexus.getNexusConfig().getBoolean("expurgo");
                        if(assasino!=null && expurgo){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            data.set(DONO.key,PersistentDataType.STRING,assasino.getUniqueId().toString());
                            ReliquiasNexus.setConfigSave("nexus."+nome,assasino.getUniqueId().toString());
                            plugin.saveConfig();
                            assasino.getInventory().addItem(item);
                            int qa = assasino.getPersistentDataContainer().getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
                            qa++;
                            assasino.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,qa);
                            checkLimit(player);
                            event.getDrops().remove(item);
                        }
                        else{
                            manterRelics.add(item);
                            event.getDrops().remove(item);
                        }
                    }
                }
            }
        }
        if(player.getPersistentDataContainer().has(PROTECAO.key,PersistentDataType.STRING)){
            String peixe = player.getPersistentDataContainer().get(PROTECAO.key,PersistentDataType.STRING);
            if(peixe!=null && !peixe.isBlank()){
                event.deathMessage(Component.text("§cO Jogador "+player.getName()+" foi tranformado em um "+peixe+"!"));
                event.deathScreenMessageOverride(Component.text("§cVocê foi tranformado em um "+peixe+"!"));

            }
        }
        if(passou(player)){
            event.deathMessage(Component.text("§cO Jogador "+player.getName()+" foi eliminado por ter reliquias demais!"));
            event.deathScreenMessageOverride(Component.text("§cVocê foi eliminado por ter reliquias demais, se controla cara!"));
            player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,1);
            Random rd = new Random();
            int i = rd.nextInt(0, manterRelics.size()-1);
            ItemStack is = manterRelics.get(i);
            manterRelics.forEach(r -> {
                if(!r.equals(is)){
                    ItemMeta meta = r.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if(data.has(NEXUS.key,PersistentDataType.STRING)){
                        String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                        data.set(DONO.key,PersistentDataType.STRING,"");
                        ReliquiasNexus.setConfigSave("nexus."+nome,"");
                        plugin.saveConfig();
                    }
                }
            });
            manterRelics.removeIf(f -> !f.equals(is));
        }
        if (!manterRelics.isEmpty()) {
            reliquiasSalvas.put(player.getUniqueId(), manterRelics);
        }else{
            player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,0);
        }
        player.getPersistentDataContainer().set(PROTECAO.key,PersistentDataType.STRING,"");
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> reliquias = reliquiasSalvas.remove(player.getUniqueId());

        if (reliquias != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : reliquias) {
                    player.getInventory().addItem(item);
                }
            }, 1L);
        }
    }
}
