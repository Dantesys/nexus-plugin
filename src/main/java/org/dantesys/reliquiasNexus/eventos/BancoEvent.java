package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.Sound;
import org.bukkit.Particle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.economia.Banco;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.team.Team;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class BancoEvent implements Listener {

    private final ReliquiasNexus plugin;
    private final String MAIN_MENU_TITLE = "§lBanco Nexus";
    private final String SALDO_MENU_TITLE = "§lSeu Saldo";
    private final String EMPRESTIMO_MENU_TITLE = "§lEmpréstimos";
    private final String HISTORICO_MENU_TITLE = "§lHistórico de Moly";
    private final String CENTRAL_BANK_TITLE = "§lNexus Central Bank";

    public BancoEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));
        playBankAnimation(player, true);

        ItemStack bancoCentral = criarItemComID(Material.DIAMOND, CENTRAL_BANK_TITLE, "central_bank");
        ItemStack saldo = criarItemComID(Material.GOLD_INGOT, "§eSaldo Pessoal", "saldo");
        ItemStack emprestimo = criarItemComID(Material.PAPER, "§bEmpréstimos", "emprestimo");
        ItemStack historico = criarItemComID(Material.BOOK, "§aHistórico", "historico");

        inv.setItem(11, saldo);
        inv.setItem(13, emprestimo);
        inv.setItem(15, historico);
        inv.setItem(4, bancoCentral);

        player.openInventory(inv);
    }

    private void abrirMenuCentralBank(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(CENTRAL_BANK_TITLE));

        Banco centralBank = Banco.getNexusCentralBank();
        double saldoBank = centralBank.getSaldo();

        ItemStack saldoItem = criarItem(Material.GOLD_BLOCK, "§eSaldo do Banco Central",
                Collections.singletonList(Component.text("§7Total de moly: §6" + String.format("%,.2f", saldoBank) + " moly§7.")));

        inv.setItem(13, saldoItem);

        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(22, backArrow);

        player.openInventory(inv);
    }

    private void abrirMenuSaldo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(SALDO_MENU_TITLE));

        double saldoPessoal = Economia.getSaldo(player);
        ItemStack saldoPessoalItem = criarItem(Material.GOLD_BLOCK, "§eSeu Saldo Pessoal",
                Collections.singletonList(Component.text("§7Você tem §6" + String.format("%,.2f", saldoPessoal) + " moly§7.")));

        inv.setItem(12, saldoPessoalItem);

        String teamName = Team.getTeamName(player);
        if (teamName != null) {
            double saldoTeam = Economia.getSaldoTime(teamName);
            ItemStack saldoTeamItem = criarItem(Material.IRON_BLOCK, "§eSaldo do Time (" + teamName + ")",
                    Collections.singletonList(Component.text("§7O time tem §6" + String.format("%,.2f", saldoTeam) + " moly§7.")));
            inv.setItem(14, saldoTeamItem);
        }

        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(22, backArrow);

        player.openInventory(inv);
    }

    private void abrirMenuEmprestimo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(EMPRESTIMO_MENU_TITLE));

        UUID playerId = player.getUniqueId();

        if (Economia.temEmprestimo(playerId)) {
            double divida = Economia.getEmprestimo(playerId);

            ItemStack dividaItem = criarItem(Material.RED_WOOL, "§cSua Dívida",
                    Arrays.asList(
                            Component.text("§7Total: §c" + String.format("%,.2f", divida) + " moly"),
                            Component.text("§7Juros de 50% sobre o valor emprestado"),
                            Component.text(""),
                            Component.text("§eClique em Pagar para quitar sua dívida")
                    ));
            ItemStack pagarItem = criarItemComID(Material.GREEN_WOOL, "§a§lPAGAR DÍVIDA", "pagar_emprestimo");

            inv.setItem(11, dividaItem);
            inv.setItem(13, criarItemComID(Material.BARRIER, "§cNão é possível pedir novo empréstimo", "bloqueado"));
            inv.setItem(15, pagarItem);
        } else {
            ItemStack emprestimoItem = criarItemComID(Material.LIME_WOOL, "§a§lPEDIR EMPRÉSTIMO", "pedir_emprestimo");
            ItemStack infoItem = criarItem(Material.PAPER, "§eInformações do Empréstimo",
                    Arrays.asList(
                            Component.text("§7Valor: §a1000 moly"),
                            Component.text("§7Juros: §c50%"),
                            Component.text("§7Total a pagar: §61500 moly"),
                            Component.text(""),
                            Component.text("§eClique em Pedir Empréstimo para receber 1000 moly")
                    ));

            inv.setItem(11, infoItem);
            inv.setItem(13, emprestimoItem);
            inv.setItem(15, criarItemComID(Material.BARRIER, "§cNenhuma dívida para pagar", "nenhuma_divida"));
        }

        // Adicionar animação
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.7f, 1.0f);
        player.spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.05);

        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(22, backArrow);

        player.openInventory(inv);
    }

    private void abrirMenuHistorico(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(HISTORICO_MENU_TITLE));

        ItemStack pegarHistorico = criarItemComID(Material.PAPER, "§ePegar Histórico", "pegar_historico");

        inv.setItem(13, pegarHistorico);

        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§cVoltar",
                Collections.singletonList(Component.text("§7Clique para voltar ao menu principal.")),
                "back_button");
        inv.setItem(22, backArrow);

        player.openInventory(inv);
    }

    private ItemStack criarItem(Material material, String nome, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
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

    private void playClickAnimation(Player player, ItemStack clickedItem) {
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.5f, 1.5f);
        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.05);
    }

    private void playBankAnimation(Player player, boolean opening) {
        if (opening) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.7f, 1.2f);
            player.spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 0.7f, 1.0f);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String inventoryTitle = event.getView().getTitle();
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (inventoryTitle.contains(MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                switch (itemId) {
                    case "central_bank":
                        playClickAnimation(player, clickedItem);
                        abrirMenuCentralBank(player);
                        break;
                    case "saldo":
                        playClickAnimation(player, clickedItem);
                        abrirMenuSaldo(player);
                        break;
                    case "emprestimo":
                        playClickAnimation(player, clickedItem);
                        abrirMenuEmprestimo(player);
                        break;
                    case "historico":
                        playClickAnimation(player, clickedItem);
                        abrirMenuHistorico(player);
                        break;
                }
            }
        } else if (inventoryTitle.contains(SALDO_MENU_TITLE) || inventoryTitle.contains(EMPRESTIMO_MENU_TITLE) || inventoryTitle.contains(HISTORICO_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                if ("back_button".equals(itemId)) {
                    playClickAnimation(player, clickedItem);
                    abrirMenuPrincipal(player);
                    return;
                }

                // Lógica de empréstimo
                switch (itemId) {
                    case "pedir_emprestimo":
                        // Adicionar animação
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);

                        // Processar empréstimo
                        Economia.processarEmprestimo(player, 1000);
                        abrirMenuEmprestimo(player);
                        break;

                    case "pagar_emprestimo":
                        // Usar o novo método para pagar empréstimo
                        boolean sucesso = Economia.pagarEmprestimo(player);
                        if (sucesso) {
                            // Adicionar animação de sucesso
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            player.spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
                        } else {
                            // Adicionar animação de falha
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7f, 0.5f);
                            player.spawnParticle(Particle.SMOKE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.05);
                        }
                        abrirMenuEmprestimo(player);
                        break;

                    case "pegar_historico":
                        List<String> historico = Economia.getHistorico(player);
                        ItemStack livro = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta bookMeta = (BookMeta) livro.getItemMeta();
                        bookMeta.setTitle("Histórico de Moly");
                        bookMeta.setAuthor(player.getName());

                        // Converter lista de strings para componentes
                        List<Component> pages = historico.stream()
                                .map(Component::text)
                                .collect(Collectors.toList());

                        // Se não houver histórico, adicionar uma página padrão
                        if (pages.isEmpty()) {
                            pages.add(Component.text("Sem histórico de transações disponível."));
                        }

                        bookMeta.pages(pages);
                        livro.setItemMeta(bookMeta);

                        // Adicionar o livro ao inventário do jogador
                        if (player.getInventory().addItem(livro).isEmpty()) {
                            player.sendMessage(Component.text("✅ Você recebeu seu histórico de transações.").color(NamedTextColor.GREEN));
                        } else {
                            player.sendMessage(Component.text("❌ Seu inventário está cheio! Libere espaço para receber o histórico.").color(NamedTextColor.RED));
                        }
                        break;
                }
            }
        } else if (inventoryTitle.contains(CENTRAL_BANK_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                if ("back_button".equals(itemId)) {
                    playClickAnimation(player, clickedItem);
                    abrirMenuPrincipal(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("Banco")) {
            playBankAnimation((Player) event.getPlayer(), false);
        }
    }
}