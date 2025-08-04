package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.dantesys.reliquiasNexus.util.NexusKeys.DONO;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class PerdeuEvent implements Listener {
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
    public void itemFrame(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity e = event.getRightClicked();
        if(e instanceof ItemFrame){
            ItemStack stack = player.getInventory().getItemInMainHand();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode colocar a reliquia numa moldura");
            }
        }
        if(e instanceof ArmorStand){
            ItemStack stack = player.getInventory().getItemInMainHand();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode colocar a reliquia num suporte de armadura");
            }
        }
    }
    @EventHandler
    public void vaso(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action a = event.getAction();
        if(a==Action.RIGHT_CLICK_BLOCK){
            Block b = event.getClickedBlock();
            if(b!=null && b == BlockType.DECORATED_POT){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    event.setCancelled(true);
                    player.sendMessage("§cVocê não pode colocar a reliquia num pote");
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
            if(inv!= InventoryType.CRAFTING && inv!=InventoryType.PLAYER && inv!=InventoryType.ENCHANTING && inv!=InventoryType.ANVIL && inv!=InventoryType.GRINDSTONE) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cVocê não pode arrastar uma relíquia para um container!");
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
    public void naoTrouxa(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        if(clickedItem != null){
            if (isBundle(cursorItem.getType()) && clickedItem.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)) {
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode mover uma relíquia para dentro de uma trouxa.");
            }
            if (isBundle(clickedItem.getType()) && cursorItem.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)) {
                event.setCancelled(true);
                player.sendMessage("§cVocê não pode guardar uma relíquia dentro de uma trouxa.");
            }
        }
    }
    private boolean isBundle(Material m){
        List<Material> mat = List.of(
                Material.BUNDLE,
                Material.BLACK_BUNDLE,
                Material.BLUE_BUNDLE,
                Material.BROWN_BUNDLE,
                Material.CYAN_BUNDLE,
                Material.GRAY_BUNDLE,
                Material.GREEN_BUNDLE,
                Material.LIGHT_BLUE_BUNDLE,
                Material.LIGHT_GRAY_BUNDLE,
                Material.LIME_BUNDLE,
                Material.MAGENTA_BUNDLE,
                Material.ORANGE_BUNDLE,
                Material.PINK_BUNDLE,
                Material.PURPLE_BUNDLE,
                Material.RED_BUNDLE,
                Material.WHITE_BUNDLE,
                Material.YELLOW_BUNDLE
        );
        return mat.contains(m);
    }
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryType inv = event.getInventory().getType();
        for (ItemStack item : event.getNewItems().values()) {
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (data.has(NEXUS.key, PersistentDataType.STRING) && inv!=InventoryType.PLAYER && inv!=InventoryType.ENCHANTING && inv!=InventoryType.ANVIL && inv!=InventoryType.GRINDSTONE && inv!= InventoryType.CRAFTING) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cVocê não pode arrastar uma relíquia para um container!");
            }
        }
    }
}
