package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LojaEvent implements Listener {

    private final ReliquiasNexus plugin;
    private final String MAIN_MENU_TITLE = "Loja";
    private final String OP_ITEMS_MENU_TITLE = "Itens OP";
    private final String NORMAL_ITEMS_MENU_TITLE = "Itens Normais";

    public LojaEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));

        // Adicionar bordas cinzas
        ItemStack borda = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, borda);
        }

        ItemStack opItems = criarItemComID(Material.DIAMOND_SWORD, "§bItens OP",
                Arrays.asList("§7Clique para ver itens OP"), "op_items");

        ItemStack normalItems = criarItemComID(Material.IRON_INGOT, "§aItens Normais",
                Arrays.asList("§7Clique para ver itens normais"), "normal_items");

        inv.setItem(11, opItems);
        inv.setItem(15, normalItems);

        player.openInventory(inv);
        playOpenSound(player);
    }

    private void abrirMenuOpItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(OP_ITEMS_MENU_TITLE));

        // Adicionar bordas
        ItemStack borda = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, borda);
        }

        // Itens OP exatos das imagens
        adicionarItemComPreco(inv, 10, Material.RESPAWN_ANCHOR, "Âncora de Respawn", 90000.0, 1);
        adicionarItemComPreco(inv, 11, Material.ELYTRA, "Elytra", 22000.0, 1);
        adicionarItemComPreco(inv, 12, Material.NETHERITE_SWORD, "Espada de Netherite", 50000.0, 1);
        adicionarItemComPreco(inv, 13, Material.NETHERITE_PICKAXE, "Picareta de Netherite", 45000.0, 1);
        adicionarItemComPreco(inv, 14, Material.TOTEM_OF_UNDYING, "Totem", 15000.0, 1);
        adicionarItemComPreco(inv, 15, Material.ENCHANTED_GOLDEN_APPLE, "Maçã Dourada Encantada", 100000.0, 1);
        adicionarItemComPreco(inv, 16, Material.BEACON, "Farol", 20000.0, 1);
        adicionarItemComPreco(inv, 19, Material.SHULKER_BOX, "Caixa de Shulker", 7500.0, 1);
        adicionarItemComPreco(inv, 20, Material.TRIDENT, "Tridente", 30000.0, 1);
        adicionarItemComPreco(inv, 21, Material.NETHER_STAR, "Estrela do Nether", 18000.0, 1);
        adicionarItemComPreco(inv, 22, Material.DIAMOND_BLOCK, "Bloco de Diamante", 5000.0, 1);
        adicionarItemComPreco(inv, 23, Material.NETHERITE_BLOCK, "Bloco de Netherita", 50000.0, 1);
        adicionarItemComPreco(inv, 24, Material.WITHER_SKELETON_SKULL, "Crânio de Wither", 25000.0, 1);
        adicionarItemComPreco(inv, 25, Material.FIREWORK_ROCKET, "Foguetes", 2000.0, 64);
        adicionarItemComPreco(inv, 28, Material.CONDUIT, "Nexo do Oceano", 15000.0, 1);
        adicionarItemComPreco(inv, 29, Material.DRAGON_BREATH, "Sopro do Dragão", 8000.0, 1);
        adicionarItemComPreco(inv, 30, Material.HEART_OF_THE_SEA, "Coração do Mar", 11000.0, 1);
        adicionarItemComPreco(inv, 31, Material.NETHERITE_HELMET, "Capacete de Netherita", 12000.0, 1);
        adicionarItemComPreco(inv, 32, Material.NETHERITE_CHESTPLATE, "Peitoral de Netherita", 15000.0, 1);
        adicionarItemComPreco(inv, 33, Material.NETHERITE_LEGGINGS, "Calças de Netherita", 14000.0, 1);
        adicionarItemComPreco(inv, 34, Material.NETHERITE_BOOTS, "Botas de Netherita", 11000.0, 1);

        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Arrays.asList("§7Clique para voltar ao menu principal."),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
        playOpenSound(player);
    }

    private void abrirMenuNormalItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(NORMAL_ITEMS_MENU_TITLE));

        // Adicionar bordas
        ItemStack borda = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, borda);
        }

        // Itens Normais exatos das imagens
        adicionarItemComPreco(inv, 10, Material.DIAMOND, "Diamante", 500.0, 1);
        adicionarItemComPreco(inv, 11, Material.DIAMOND_SWORD, "Espada de Diamante", 1000.0, 1);
        adicionarItemComPreco(inv, 12, Material.DIAMOND_PICKAXE, "Picareta de Diamante", 1500.0, 1);
        adicionarItemComPreco(inv, 13, Material.DIAMOND_AXE, "Machado de Diamante", 1300.0, 1);
        adicionarItemComPreco(inv, 14, Material.IRON_INGOT, "Barra de Ferro", 50.0, 1);
        adicionarItemComPreco(inv, 15, Material.GOLD_INGOT, "Barra de Ouro", 75.0, 1);
        adicionarItemComPreco(inv, 16, Material.OAK_LOG, "Toras de Carvalho", 10.0, 1);
        adicionarItemComPreco(inv, 19, Material.COBBLESTONE, "Pedregulho", 5.0, 1);
        adicionarItemComPreco(inv, 20, Material.DIAMOND_HELMET, "Capacete de Diamante", 1200.0, 1);
        adicionarItemComPreco(inv, 21, Material.DIAMOND_CHESTPLATE, "Peitoral de Diamante", 2000.0, 1);
        adicionarItemComPreco(inv, 22, Material.DIAMOND_LEGGINGS, "Calças de Diamante", 1800.0, 1);
        adicionarItemComPreco(inv, 23, Material.DIAMOND_BOOTS, "Botas de Diamante", 1100.0, 1);
        adicionarItemComPreco(inv, 24, Material.BEEF, "Bife", 25.0, 1);
        adicionarItemComPreco(inv, 25, Material.COOKED_BEEF, "Bife Cozido", 30.0, 1);
        adicionarItemComPreco(inv, 28, Material.GOLDEN_CARROT, "Cenoura Dourada", 40.0, 1);
        adicionarItemComPreco(inv, 29, Material.ENCHANTED_BOOK, "Livro Encantado", 500.0, 1);
        adicionarItemComPreco(inv, 30, Material.WATER_BUCKET, "Balde de Água", 15.0, 1);
        adicionarItemComPreco(inv, 31, Material.LAVA_BUCKET, "Balde de Lava", 25.0, 1);
        adicionarItemComPreco(inv, 32, Material.ARROW, "Flecha", 3.0, 1);
        adicionarItemComPreco(inv, 33, Material.EXPERIENCE_BOTTLE, "Frasco de Experiência", 75.0, 1);
        adicionarItemComPreco(inv, 34, Material.DIAMOND_HORSE_ARMOR, "Armadura de Diamante", 2000.0, 1);

        // Bloco de Ferro da imagem
        adicionarItemComPreco(inv, 35, Material.IRON_BLOCK, "Bloco de Ferro", 500.0, 1);

        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Arrays.asList("§7Clique para voltar ao menu principal."),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
        playOpenSound(player);
    }

    private ItemStack criarItemComID(Material material, String nome, List<String> lore, String id) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));

        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(Component.text(line));
        }
        meta.lore(componentLore);

        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack criarCabecaComID(String owner, String nome, List<String> lore, String id) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(owner);
        meta.displayName(Component.text(nome));

        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(Component.text(line));
        }
        meta.lore(componentLore);

        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        head.setItemMeta(meta);
        return head;
    }

    private void adicionarItemComPreco(Inventory inv, int slot, Material material, String nome, double preco, int quantidade) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Preço: §6" + preco + " moly"));
        lore.add(Component.text("§aClique para comprar!"));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING,
                nome.replaceAll("§.", "").replaceAll(" ", "_"));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE, preco);
        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    private void playOpenSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

        // Animação simples de abrir menu
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count < 3) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f + (count * 0.2f));
                    count++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    private void playBuySound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    private void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.8f);
    }

    // Método auxiliar para converter Component para String
    private String componentToString(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String inventoryTitle = componentToString(event.getView().title());
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (inventoryTitle.equals(MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                switch (itemId) {
                    case "op_items":
                        abrirMenuOpItems(player);
                        break;
                    case "normal_items":
                        abrirMenuNormalItems(player);
                        break;
                }
            }
        } else if (inventoryTitle.equals(OP_ITEMS_MENU_TITLE) || inventoryTitle.equals(NORMAL_ITEMS_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);

                if ("back_button".equals(itemId)) {
                    abrirMenuPrincipal(player);
                    return;
                }

                // Verificar se o item tem preço
                if (data.has(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE)) {
                    double preco = data.get(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE);
                    double saldo = Economia.getSaldo(player);

                    if (saldo >= preco) {
                        if (player.getInventory().firstEmpty() != -1) {
                            Economia.removerSaldo(player, preco, "Compra de item na loja");
                            player.getInventory().addItem(new ItemStack(clickedItem.getType(), clickedItem.getAmount()));

                            String nomeItem = meta.hasDisplayName() ? componentToString(meta.displayName()) : clickedItem.getType().toString();
                            player.sendMessage(Component.text("✅ Você comprou " + nomeItem + " por " + preco + " moly.").color(NamedTextColor.GREEN));
                            playBuySound(player);
                        } else {
                            player.sendMessage(Component.text("❌ Seu inventário está cheio!").color(NamedTextColor.RED));
                            playErrorSound(player);
                        }
                    } else {
                        player.sendMessage(Component.text("❌ Saldo insuficiente para comprar este item.").color(NamedTextColor.RED));
                        playErrorSound(player);
                    }
                }
            }
        }
    }
}