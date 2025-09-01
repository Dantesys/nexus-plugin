package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LojaEvent implements Listener {

    private final ReliquiasNexus plugin;
    private final String MAIN_MENU_TITLE = "§lNexus Shop";
    private final String OP_ITEMS_MENU_TITLE = "§lItens OP";
    private final String NORMAL_ITEMS_MENU_TITLE = "§lItens Normais";

    public LojaEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));

        ItemStack opItems = criarItemComID(Material.DIAMOND_SWORD, "§b§lItens OP", "op_items");
        ItemStack normalItems = criarItemComID(Material.IRON_INGOT, "§a§lItens Normais", "normal_items");

        inv.setItem(12, opItems);
        inv.setItem(14, normalItems);

        player.openInventory(inv);
    }

    private void abrirMenuOpItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(OP_ITEMS_MENU_TITLE));

        // Item do Baú do Fim (Ender Chest)
        ItemStack enderChestItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderChestMeta = enderChestItem.getItemMeta();
        enderChestMeta.displayName(Component.text("§bBaú do Fim"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Permite o uso do comando §e/ec§7."));
        lore.add(Component.text("§7Abre seu inventário do fim de qualquer lugar."));
        lore.add(Component.text(""));
        lore.add(Component.text("§aPreço: §65.000 Moly"));
        lore.add(Component.text("§aCompra única por jogador."));
        enderChestMeta.lore(lore);
        enderChestItem.setItemMeta(enderChestMeta);

        PersistentDataContainer metaPDC = enderChestItem.getItemMeta().getPersistentDataContainer();
        metaPDC.set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, "ender_chest");
        enderChestItem.setItemMeta(enderChestMeta);

        inv.setItem(22, enderChestItem);

        // Outros itens OP (20+ itens de exemplo)
        adicionarItemComPreco(inv, 0, Material.NETHERITE_SWORD, "§cEspada de Netherite", 50000.0, 1);
        adicionarItemComPreco(inv, 1, Material.NETHERITE_PICKAXE, "§cPicareta de Netherite", 45000.0, 1);
        adicionarItemComPreco(inv, 2, Material.TOTEM_OF_UNDYING, "§eTotem ", 15000.0, 1);
        adicionarItemComPreco(inv, 3, Material.ENCHANTED_GOLDEN_APPLE, "§6Maçã Dourada Encantada", 100000.0, 1);
        adicionarItemComPreco(inv, 4, Material.BEACON, "§bFarol", 20000.0, 1);
        adicionarItemComPreco(inv, 5, Material.SHULKER_BOX, "§aCaixa de Shulker", 7500.0, 1);
        adicionarItemComPreco(inv, 6, Material.TRIDENT, "§bTridente da Tempestade", 30000.0, 1);
        adicionarItemComPreco(inv, 7, Material.NETHER_STAR, "§5Estrela do Nether", 18000.0, 1);
        adicionarItemComPreco(inv, 8, Material.DIAMOND_BLOCK, "§bBloco de Diamante", 5000.0, 1);
        adicionarItemComPreco(inv, 9, Material.NETHERITE_BLOCK, "§cBloco de Netherita", 50000.0, 1);
        adicionarItemComPreco(inv, 10, Material.WITHER_SKELETON_SKULL, "§0Crânio de Wither", 25000.0, 1);
        adicionarItemComPreco(inv, 11, Material.ELYTRA, "§fElytra", 22000.0, 1);
        adicionarItemComPreco(inv, 12, Material.FIREWORK_ROCKET, "§fFoguetes de Voo", 2000.0, 64);
        adicionarItemComPreco(inv, 13, Material.COOKED_BEEF, "§cCozido de Bife", 500.0, 64);
        adicionarItemComPreco(inv, 14, Material.CONDUIT, "§bNexo do Oceano", 15000.0, 1);
        adicionarItemComPreco(inv, 15, Material.DRAGON_BREATH, "§5Sopro do Dragão", 8000.0, 1);
        adicionarItemComPreco(inv, 16, Material.HEART_OF_THE_SEA, "§bCoração do Mar", 11000.0, 1);
        adicionarItemComPreco(inv, 17, Material.RESPAWN_ANCHOR, "§dÂncora de Respawn", 9000.0, 1);
        adicionarItemComPreco(inv, 18, Material.NETHERITE_HELMET, "§cCapacete de Netherita", 12000.0, 1);
        adicionarItemComPreco(inv, 19, Material.NETHERITE_CHESTPLATE, "§cPeitoral de Netherita", 15000.0, 1);
        adicionarItemComPreco(inv, 20, Material.NETHERITE_LEGGINGS, "§cCaleças de Netherita", 14000.0, 1);
        adicionarItemComPreco(inv, 21, Material.NETHERITE_BOOTS, "§cBota de Netherita", 11000.0, 1);


        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
    }

    private void abrirMenuNormalItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(NORMAL_ITEMS_MENU_TITLE));

        // Itens Normais (20+ itens de exemplo)
        adicionarItemComPreco(inv, 0, Material.DIAMOND, "§bDiamante", 500.0, 1);
        adicionarItemComPreco(inv, 1, Material.DIAMOND_SWORD, "§bEspada de Diamante", 1000.0, 1);
        adicionarItemComPreco(inv, 2, Material.DIAMOND_PICKAXE, "§bPicareta de Diamante", 1500.0, 1);
        adicionarItemComPreco(inv, 3, Material.DIAMOND_AXE, "§bMachado de Diamante", 1300.0, 1);
        adicionarItemComPreco(inv, 4, Material.IRON_INGOT, "§fBarra de Ferro", 50.0, 1);
        adicionarItemComPreco(inv, 5, Material.GOLD_INGOT, "§6Barra de Ouro", 75.0, 1);
        adicionarItemComPreco(inv, 6, Material.OAK_LOG, "§eToras de Carvalho", 10.0, 1);
        adicionarItemComPreco(inv, 7, Material.COBBLESTONE, "§7Pedregulho", 5.0, 1);
        adicionarItemComPreco(inv, 8, Material.DIAMOND_HELMET, "§bCapacete de Diamante", 1200.0, 1);
        adicionarItemComPreco(inv, 9, Material.DIAMOND_CHESTPLATE, "§bPeitoral de Diamante", 2000.0, 1);
        adicionarItemComPreco(inv, 10, Material.DIAMOND_LEGGINGS, "§bCaleças de Diamante", 1800.0, 1);
        adicionarItemComPreco(inv, 11, Material.DIAMOND_BOOTS, "§bBotas de Diamante", 1100.0, 1);
        adicionarItemComPreco(inv, 12, Material.BEEF, "§cBife", 25.0, 1);
        adicionarItemComPreco(inv, 13, Material.COOKED_BEEF, "§cCozido de Bife", 30.0, 1);
        adicionarItemComPreco(inv, 14, Material.GOLDEN_CARROT, "§6Cenoura Dourada", 40.0, 1);
        adicionarItemComPreco(inv, 15, Material.ENCHANTED_BOOK, "§aLivro Encantado", 500.0, 1);
        adicionarItemComPreco(inv, 16, Material.WATER_BUCKET, "§bBalde de Água", 15.0, 1);
        adicionarItemComPreco(inv, 17, Material.LAVA_BUCKET, "§cBalde de Lava", 25.0, 1);
        adicionarItemComPreco(inv, 18, Material.ARROW, "§fFlecha", 3.0, 1);
        adicionarItemComPreco(inv, 19, Material.EXPERIENCE_BOTTLE, "§dFrasco de Experiência", 75.0, 1);
        adicionarItemComPreco(inv, 20, Material.DIAMOND_HORSE_ARMOR, "§bArmadura de Diamante para Cavalo", 2000.0, 1);
        adicionarItemComPreco(inv, 21, Material.LEAD, "§aCorda", 20.0, 1);
        adicionarItemComPreco(inv, 22, Material.SADDLE, "§aSela", 150.0, 1);
        adicionarItemComPreco(inv, 23, Material.NAME_TAG, "§eEtiqueta de Nome", 100.0, 1);
        adicionarItemComPreco(inv, 24, Material.OBSIDIAN, "§0Obsidiana", 30.0, 1);
        adicionarItemComPreco(inv, 25, Material.END_STONE, "§fPedra do Fim", 10.0, 1);
        adicionarItemComPreco(inv, 26, Material.NETHERRACK, "§4Netherrack", 8.0, 1);
        adicionarItemComPreco(inv, 27, Material.COAL, "§8Carvão", 2.0, 1);
        adicionarItemComPreco(inv, 28, Material.COPPER_INGOT, "§6Barra de Cobre", 25.0, 1);
        adicionarItemComPreco(inv, 29, Material.LAPIS_LAZULI, "§bLápis-lazúli", 15.0, 1);
        adicionarItemComPreco(inv, 30, Material.EMERALD, "§aEsmeralda", 250.0, 1);
        adicionarItemComPreco(inv, 31, Material.REDSTONE, "§cRedstone", 12.0, 1);
        adicionarItemComPreco(inv, 32, Material.QUARTZ, "§fQuartzo do Nether", 18.0, 1);
        adicionarItemComPreco(inv, 33, Material.GLOWSTONE_DUST, "§ePó de Glowstone", 20.0, 1);
        adicionarItemComPreco(inv, 34, Material.BLAZE_ROD, "§6Vara de Blaze", 45.0, 1);
        adicionarItemComPreco(inv, 35, Material.GHAST_TEAR, "§fLágrima de Ghast", 80.0, 1);
        adicionarItemComPreco(inv, 36, Material.PHANTOM_MEMBRANE, "§cMembrana de Phantom", 90.0, 1);
        adicionarItemComPreco(inv, 37, Material.SHULKER_SHELL, "§dCasco de Shulker", 1200.0, 1);
        adicionarItemComPreco(inv, 38, Material.WITHER_ROSE, "§0Rosa de Wither", 600.0, 1);
        adicionarItemComPreco(inv, 39, Material.VILLAGER_SPAWN_EGG, "§aOvo de Villager", 300.0, 1);
        adicionarItemComPreco(inv, 40, Material.ZOMBIE_SPAWN_EGG, "§2Ovo de Zumbi", 200.0, 1);
        adicionarItemComPreco(inv, 41, Material.SKELETON_SPAWN_EGG, "§7Ovo de Esqueleto", 200.0, 1);
        adicionarItemComPreco(inv, 42, Material.SPIDER_SPAWN_EGG, "§8Ovo de Aranha", 150.0, 1);
        adicionarItemComPreco(inv, 43, Material.CREEPER_SPAWN_EGG, "§aOvo de Creeper", 250.0, 1);
        adicionarItemComPreco(inv, 44, Material.IRON_BLOCK, "§fBloco de Ferro", 500.0, 1);
        adicionarItemComPreco(inv, 45, Material.GOLD_BLOCK, "§6Bloco de Ouro", 750.0, 1);
        adicionarItemComPreco(inv, 46, Material.EMERALD_BLOCK, "§aBloco de Esmeralda", 2500.0, 1);

        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
    }

    private void adicionarItemComPreco(Inventory inv, int slot, Material material, String nome, double preco, int quantidade) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));
        meta.lore(Arrays.asList(
                Component.text("§7Preço: §6" + preco + " moly"),
                Component.text("§aClique para comprar!")
        ));
        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, nome.replaceAll("§.",""));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE, preco);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private ItemStack criarItemComID(Material material, String nome, String id) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));
        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack criarCabecaComID(String owner, String nome, List<Component> lore, String id) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(owner);
        meta.displayName(Component.text(nome));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        head.setItemMeta(meta);
        return head;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String inventoryTitle = PlainComponentSerializer.plain().serialize(event.getView().title());
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (inventoryTitle.contains(MAIN_MENU_TITLE.replaceAll("§.", ""))) {
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
        } else if (inventoryTitle.contains(OP_ITEMS_MENU_TITLE.replaceAll("§.", "")) || inventoryTitle.contains(NORMAL_ITEMS_MENU_TITLE.replaceAll("§.", ""))) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                if ("back_button".equals(itemId)) {
                    abrirMenuPrincipal(player);
                    return;
                }

                // Lógica para comprar Baú do Fim
                if (itemId.equals("ender_chest")) {
                    if (Economia.getSaldo(player) >= 5000) {
                        PersistentDataContainer playerData = player.getPersistentDataContainer();
                        if (playerData.has(NexusKeys.ENDER_CHEST_OWNED.key, PersistentDataType.BOOLEAN)) {
                            player.sendMessage(Component.text("❌ Você já comprou o Baú do Fim. Esta é uma compra única.").color(NamedTextColor.RED));
                            return;
                        }

                        Economia.removerSaldo(player, 5000, "Compra de Baú do Fim");
                        playerData.set(NexusKeys.ENDER_CHEST_OWNED.key, PersistentDataType.BOOLEAN, true);
                        player.sendMessage(Component.text("✅ Você comprou o Baú do Fim por 5.000 moly. Use o comando /ec para abri-lo.").color(NamedTextColor.GREEN));
                    } else {
                        player.sendMessage(Component.text("❌ Saldo insuficiente para comprar este item.").color(NamedTextColor.RED));
                    }
                    return;
                }

                // Lógica de compra para outros itens
                if (data.has(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE)) {
                    double preco = data.get(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE);
                    double saldo = Economia.getSaldo(player);

                    if (saldo >= preco) {
                        if (player.getInventory().firstEmpty() != -1) {
                            Economia.removerSaldo(player, preco);
                            player.getInventory().addItem(new ItemStack(clickedItem.getType(), clickedItem.getAmount()));

                            String nomeItem = PlainComponentSerializer.plain().serialize(Objects.requireNonNull(meta.displayName()));
                            player.sendMessage(Component.text("✅ Você comprou " + nomeItem + " por " + preco + " moly.").color(NamedTextColor.GREEN));
                        } else {
                            player.sendMessage(Component.text("❌ Seu inventário está cheio!").color(NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("❌ Saldo insuficiente para comprar este item.").color(NamedTextColor.RED));
                    }
                }
            }
        }
    }
}