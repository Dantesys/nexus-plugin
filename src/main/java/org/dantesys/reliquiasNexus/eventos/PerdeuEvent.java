package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.List;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class PerdeuEvent implements Listener {
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
                String msg = ReliquiasNexus.getLang().getString("perdeu.frame");
                if(msg==null){
                    msg="Você não pode colocar a reliquia numa moldura.";
                }
                player.sendMessage("§c"+msg);
            }
        }
        if(e instanceof ArmorStand){
            ItemStack stack = player.getInventory().getItemInMainHand();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                event.setCancelled(true);
                String msg = ReliquiasNexus.getLang().getString("perdeu.armor");
                if(msg==null){
                    msg="Você não pode colocar a reliquia num suporte de armadura.";
                }
                player.sendMessage("§c"+msg);
            }
        }
    }
    @EventHandler
    public void vaso(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b!=null && b.getType() == Material.DECORATED_POT){
            ItemStack stack = player.getInventory().getItemInMainHand();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                event.setCancelled(true);
                String msg = ReliquiasNexus.getLang().getString("perdeu.pote");
                if(msg==null){
                    msg="Você não pode colocar a reliquia num pote.";
                }
                player.sendMessage("§c"+msg);
            }
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PersistentDataContainerView data = item.getPersistentDataContainer();
        if (data.has(NEXUS.key, PersistentDataType.STRING)) {
            event.setCancelled(true);
            String msg = ReliquiasNexus.getLang().getString("perdeu.drop");
            if(msg==null){
                msg="Você não pode jogar fora uma relíquia, está doido?";
            }
            event.getPlayer().sendMessage("§c"+msg);
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
                String msg = ReliquiasNexus.getLang().getString("perdeu.chest");
                if(msg==null){
                    msg="Você não pode colocar a relíquia para um container!";
                }
                event.getWhoClicked().sendMessage("§c"+msg);
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
                    String msg = ReliquiasNexus.getLang().getString("perdeu.shift");
                    if(msg==null){
                        msg="Relíquias não podem ser movidas com shift!";
                    }
                    event.getWhoClicked().sendMessage("§c"+msg);
                }
            }
        }
    }
    @EventHandler
    public void naoTrouxa(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        String msg = ReliquiasNexus.getLang().getString("perdeu.bundle");
        if(msg==null){
            msg="Você não pode guardar uma relíquia dentro de uma trouxa.";
        }
        if(clickedItem != null){
            if (isBundle(cursorItem.getType()) && clickedItem.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)) {
                event.setCancelled(true);
                player.sendMessage("§c"+msg);
            }
            if (isBundle(clickedItem.getType()) && cursorItem.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)) {
                event.setCancelled(true);
                player.sendMessage("§c"+msg);
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
                String msg = ReliquiasNexus.getLang().getString("perdeu.chest");
                if(msg==null){
                    msg="Você não pode colocar a relíquia para um container!";
                }
                event.getWhoClicked().sendMessage("§c"+msg);
            }
        }
    }
}
