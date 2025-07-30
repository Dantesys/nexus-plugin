package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.dantesys.reliquiasNexus.util.NexusKeys.DONO;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class PerdeuReliquia implements Listener {
    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item itemEntity)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
            ItemStack item = itemEntity.getItemStack();
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (data.has(NEXUS.key, PersistentDataType.STRING) && data.has(DONO.key,PersistentDataType.STRING)) {
                String uuidStr = data.get(DONO.key,PersistentDataType.STRING);
                if(uuidStr!=null && !uuidStr.isBlank()){
                    UUID uuid = UUID.fromString(uuidStr);
                    Player player = getServer().getPlayer(uuid);
                    if(player!=null){
                        player.getInventory().addItem(item);
                        itemEntity.remove();
                    }
                }
            }
        }
    }
    @EventHandler
    public void onItemDanoPorBloco(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof Item itemEntity)) return;
        if (event.getDamager()!=null && event.getDamager().getType() == Material.CACTUS) {
            ItemStack item = itemEntity.getItemStack();
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (data.has(NEXUS.key, PersistentDataType.STRING) && data.has(DONO.key,PersistentDataType.STRING)) {
                String uuidStr = data.get(DONO.key,PersistentDataType.STRING);
                if(uuidStr!=null && !uuidStr.isBlank()){
                    UUID uuid = UUID.fromString(uuidStr);
                    Player player = getServer().getPlayer(uuid);
                    if(player!=null){
                        player.getInventory().addItem(item);
                        itemEntity.remove();
                    }
                }
            }
        }
    }
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();
        PersistentDataContainerView data = item.getPersistentDataContainer();
        if (data.has(NEXUS.key, PersistentDataType.STRING) && data.has(DONO.key,PersistentDataType.STRING)) {
            String uuidStr = data.get(DONO.key,PersistentDataType.STRING);
            if(uuidStr!=null && !uuidStr.isBlank()){
                UUID uuid = UUID.fromString(uuidStr);
                Player player = getServer().getPlayer(uuid);
                if(player!=null){
                    player.getInventory().addItem(item);
                    event.getEntity().remove();
                }
            }
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PersistentDataContainerView data = item.getPersistentDataContainer();
        if (data.has(NEXUS.key, PersistentDataType.STRING)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode jogar fora uma relíquia, está doido?");
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack item = event.getCurrentItem();
        if(item!=null && !item.isEmpty() && item.getType()!=Material.AIR){
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (!data.has(NEXUS.key, PersistentDataType.STRING)) return;
            InventoryType inv = event.getInventory().getType();
            InventoryType clickedType = event.getClickedInventory() != null ? event.getClickedInventory().getType() : null;
            if (clickedType != InventoryType.PLAYER && clickedType!=InventoryType.ENCHANTING && clickedType!=InventoryType.ANVIL && clickedType!=InventoryType.GRINDSTONE) {
                if(inv!=InventoryType.PLAYER && inv!=InventoryType.ENCHANTING && inv!=InventoryType.ANVIL && inv!=InventoryType.GRINDSTONE) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cVocê não pode arrastar uma relíquia para um container!");
                }
            }
        }
    }
    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        Inventory i = event.getClickedInventory();
        if(i!=null){
            InventoryType inv = i.getType();
            ItemStack item = event.getCurrentItem();
            if(item!=null){
                PersistentDataContainerView data = item.getPersistentDataContainer();
                if (event.getClick().isShiftClick() && data.has(NEXUS.key, PersistentDataType.STRING) && inv!=InventoryType.PLAYER && inv!=InventoryType.ENCHANTING && inv!=InventoryType.ANVIL && inv!=InventoryType.GRINDSTONE) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cRelíquias não podem ser movidas com shift!");
                }
            }
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryType inv = event.getInventory().getType();
        for (ItemStack item : event.getNewItems().values()) {
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (data.has(NEXUS.key, PersistentDataType.STRING) && inv!=InventoryType.PLAYER && inv!=InventoryType.ENCHANTING && inv!=InventoryType.ANVIL && inv!=InventoryType.GRINDSTONE) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cVocê não pode arrastar uma relíquia para um container!");
            }
        }
    }
}
