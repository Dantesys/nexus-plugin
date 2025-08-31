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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.team.Team;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class BancoEvent implements Listener {

    private final ReliquiasNexus plugin;
    private final String MAIN_MENU_TITLE = "§lBanco Nexus";
    private final String SALDO_MENU_TITLE = "§lSeu Saldo";
    private final String EMPRESTIMO_MENU_TITLE = "§lEmpréstimos";
    private final String HISTORICO_MENU_TITLE = "§lHistórico de Moly";

    public BancoEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));

        ItemStack saldo = criarItemComID(Material.GOLD_INGOT, "§eSaldo", "saldo");
        ItemStack emprestimo = criarItemComID(Material.PAPER, "§bEmpréstimo", "emprestimo");
        ItemStack historico = criarItemComID(Material.BOOK, "§aHistórico", "historico");

        inv.setItem(11, saldo);
        inv.setItem(13, emprestimo);
        inv.setItem(15, historico);

        player.openInventory(inv);
    }

    private void abrirMenuSaldo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(SALDO_MENU_TITLE));

        double saldoPessoal = Economia.getSaldo(player);
        ItemStack saldoPessoalItem = criarItem(Material.GOLD_BLOCK, "§eSeu Saldo Pessoal",
                Collections.singletonList(Component.text("§7Você tem §6" + saldoPessoal + " moly§7.")));

        inv.setItem(12, saldoPessoalItem);

        String teamName = Team.getTeamName(player);
        if (teamName != null) {
            double saldoTeam = Economia.getSaldoTime(teamName);
            ItemStack saldoTeamItem = criarItem(Material.IRON_BLOCK, "§eSaldo do Time (" + teamName + ")",
                    Collections.singletonList(Component.text("§7O time tem §6" + saldoTeam + " moly§7.")));
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

        if (Economia.temEmprestimo(player)) {
            double divida = Economia.getEmprestimo(player);
            long vencimento = Economia.getVencimentoEmprestimo(player);
            long tempoRestante = (vencimento - Instant.now().getEpochSecond()) / 3600;

            ItemStack dividaItem = criarItem(Material.RED_WOOL, "§cSua Dívida",
                    Arrays.asList(
                            Component.text("§7Total: §c" + divida + " moly"),
                            Component.text("§7Tempo restante: §e" + tempoRestante + " horas")
                    ));
            ItemStack pagarItem = criarItemComID(Material.GREEN_WOOL, "§aPagar Dívida", "pagar_emprestimo");

            inv.setItem(12, dividaItem);
            inv.setItem(14, pagarItem);
        } else {
            ItemStack emprestimoItem = criarItemComID(Material.LIME_WOOL, "§aPedir Empréstimo (1000 moly)", "pedir_emprestimo");
            inv.setItem(13, emprestimoItem);
        }

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String inventoryTitle = event.getView().title().toString();
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (inventoryTitle.contains(MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                switch (itemId) {
                    case "saldo":
                        abrirMenuSaldo(player);
                        break;
                    case "emprestimo":
                        abrirMenuEmprestimo(player);
                        break;
                    case "historico":
                        abrirMenuHistorico(player);
                        break;
                }
            }
        } else if (inventoryTitle.contains(SALDO_MENU_TITLE) || inventoryTitle.contains(EMPRESTIMO_MENU_TITLE) || inventoryTitle.contains(HISTORICO_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                if ("back_button".equals(itemId)) {
                    abrirMenuPrincipal(player);
                    return;
                }

                // Lógica de empréstimo
                switch (itemId) {
                    case "pedir_emprestimo":
                        if (!Economia.temEmprestimo(player)) {
                            Economia.concederEmprestimo(player, 1000, 24); // Exemplo: 1000 moly, 24h
                            player.sendMessage(Component.text("✅ Você pegou um empréstimo de 1000 moly. Pague de volta em 24h!").color(NamedTextColor.GREEN));
                            abrirMenuEmprestimo(player);
                        } else {
                            player.sendMessage(Component.text("❌ Você já tem um empréstimo ativo.").color(NamedTextColor.RED));
                        }
                        break;
                    case "pagar_emprestimo":
                        double divida = Economia.getEmprestimo(player);
                        if (Economia.getSaldo(player) >= divida) {
                            Economia.removerSaldo(player, divida);
                            Economia.finalizarEmprestimo(player);
                            player.sendMessage(Component.text("✅ Você pagou sua dívida! Seu empréstimo foi finalizado.").color(NamedTextColor.GREEN));
                            abrirMenuEmprestimo(player);
                        } else {
                            player.sendMessage(Component.text("❌ Saldo insuficiente para pagar a dívida.").color(NamedTextColor.RED));
                        }
                        break;
                    case "pegar_historico":
                        List<String> historico = Economia.getHistorico(player);
                        ItemStack livro = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta bookMeta = (BookMeta) livro.getItemMeta();
                        bookMeta.setTitle("Histórico de Moly");
                        bookMeta.setAuthor(player.getName());
                        bookMeta.pages(historico.stream().map(Component::text).collect(Collectors.toList()));
                        livro.setItemMeta(bookMeta);
                        player.getInventory().addItem(livro);
                        player.sendMessage(Component.text("✅ Você recebeu seu histórico de transações.").color(NamedTextColor.GREEN));
                        break;
                }
            }
        }
    }
}