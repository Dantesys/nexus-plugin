package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.Color;
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

import java.util.*;
import java.util.stream.Collectors;

public class BancoEvent implements Listener {

    private final ReliquiasNexus plugin;
    private final String MAIN_MENU_TITLE = "§l§dN§5e§9x§bu§as §6B§ca§cn§6c§eo";
    private final String SALDO_MENU_TITLE = "§l§eSeu Saldo";
    private final String EMPRESTIMO_MENU_TITLE = "§l§bEmpréstimos";
    private final String HISTORICO_MENU_TITLE = "§l§aHistórico de Moly";

    private final Map<UUID, Integer> animationTasks = new HashMap<>();
    private final Map<UUID, Integer> particleTasks = new HashMap<>();
    private final Map<UUID, Integer> musicTasks = new HashMap<>();
    private final Map<UUID, List<ItemStack>> animatedItems = new HashMap<>();

    // Materiais para animação de ouro
    private final Material[] goldMaterials = {
            Material.GOLD_NUGGET,
            Material.GOLD_INGOT,
            Material.GOLD_BLOCK,
            Material.GOLD_INGOT,
            Material.GOLD_NUGGET
    };

    // Cores para partículas giratórias
    private final Color[] particleColors = {
            Color.fromRGB(255, 0, 0),    // Vermelho
            Color.fromRGB(255, 165, 0),  // Laranja
            Color.fromRGB(255, 255, 0),  // Amarelo
            Color.fromRGB(0, 255, 0),    // Verde
            Color.fromRGB(0, 0, 255),    // Azul
            Color.fromRGB(75, 0, 130),   // Índigo
            Color.fromRGB(238, 130, 238) // Violeta
    };

    public BancoEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));
        playBankAnimation(player, true);

        // Itens principais
        ItemStack saldo = criarItemComID(Material.GOLD_INGOT, "§e§lSaldo Pessoal", "saldo");
        ItemStack emprestimo = criarItemComID(Material.PAPER, "§b§lEmpréstimos", "emprestimo");
        ItemStack historico = criarItemComID(Material.BOOK, "§a§lHistórico", "historico");

        // Item central com efeito especial
        ItemStack bankItem = criarItemAnimado(Material.GOLD_NUGGET, "§6§lBanco Nexus", "info", goldMaterials);

        inv.setItem(11, saldo);
        inv.setItem(13, bankItem);
        inv.setItem(15, emprestimo);
        inv.setItem(22, historico);

        // Flecha de voltar no canto inferior esquerdo
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c§lVoltar",
                Arrays.asList(Component.text("§7Clique para voltar")),
                "back_button");
        inv.setItem(18, backArrow);

        player.openInventory(inv);
        startInventoryAnimation(player, inv);
        startParticleAnimation(player);
        startMusic(player);
    }

    private void abrirMenuSaldo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(SALDO_MENU_TITLE));

        double saldoPessoal = Economia.getSaldo(player);
        ItemStack saldoPessoalItem = criarItem(Material.GOLD_BLOCK, "§e§lSeu Saldo Pessoal",
                Arrays.asList(
                        Component.text("§7Você tem: §6" + String.format("%,.2f", saldoPessoal) + " moly§7."),
                        Component.text("§7Gerencie suas finanças")
                ));

        inv.setItem(12, saldoPessoalItem);

        String teamName = Team.getTeamName(player);
        if (teamName != null) {
            double saldoTeam = Economia.getSaldoTime(teamName);
            ItemStack saldoTeamItem = criarItem(Material.IRON_BLOCK, "§e§lSaldo do Time",
                    Arrays.asList(
                            Component.text("§7Time: §a" + teamName),
                            Component.text("§7Saldo: §6" + String.format("%,.2f", saldoTeam) + " moly§7.")
                    ));
            inv.setItem(14, saldoTeamItem);
        }

        // Flecha de voltar no canto inferior esquerdo
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c§lVoltar",
                Arrays.asList(Component.text("§7Clique para voltar ao menu principal")),
                "back_button");
        inv.setItem(18, backArrow);

        player.openInventory(inv);
        playMenuSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f);
    }

    private void abrirMenuEmprestimo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(EMPRESTIMO_MENU_TITLE));

        UUID playerId = player.getUniqueId();

        if (Economia.temEmprestimo(playerId)) {
            double divida = Economia.getEmprestimo(playerId);

            ItemStack dividaItem = criarItem(Material.RED_WOOL, "§c§lSua Dívida",
                    Arrays.asList(
                            Component.text("§7Total: §c" + String.format("%,.2f", divida) + " moly"),
                            Component.text("§7Juros: §c50%"),
                            Component.text(""),
                            Component.text("§eClique em Pagar para quitar sua dívida")
                    ));

            ItemStack pagarItem = criarItemComID(Material.GREEN_WOOL, "§a§lPAGAR DÍVIDA", "pagar_emprestimo");

            inv.setItem(11, dividaItem);
            inv.setItem(13, criarItemComID(Material.BARRIER, "§c§lNOVO EMPRÉSTIMO", "bloqueado"));
            inv.setItem(15, pagarItem);
        } else {
            ItemStack emprestimoItem = criarItemComID(Material.LIME_WOOL, "§a§lPEDIR EMPRÉSTIMO", "pedir_emprestimo");
            ItemStack infoItem = criarItem(Material.PAPER, "§e§lInformações",
                    Arrays.asList(
                            Component.text("§7Empréstimo rápido e fácil"),
                            Component.text("§7Taxa de juros: §c50%"),
                            Component.text("§7Pagamento único"),
                            Component.text("§7Sem complicações")
                    ));

            inv.setItem(11, infoItem);
            inv.setItem(13, emprestimoItem);
            inv.setItem(15, criarItemComID(Material.BARRIER, "§c§lSEM DÍVIDA", "nenhuma_divida"));
        }

        // Flecha de voltar no canto inferior esquerdo
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c§lVoltar",
                Arrays.asList(Component.text("§7Clique para voltar ao menu principal")),
                "back_button");
        inv.setItem(18, backArrow);

        player.openInventory(inv);
        playMenuSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 0.8f);
    }

    private void abrirMenuHistorico(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(HISTORICO_MENU_TITLE));

        ItemStack pegarHistorico = criarItemComID(Material.WRITTEN_BOOK, "§e§lPegar Histórico", "pegar_historico");
        ItemStack infoItem = criarItem(Material.PAPER, "§6§lInformações",
                Arrays.asList(
                        Component.text("§7Seu histórico contém"),
                        Component.text("§7as últimas 20 transações"),
                        Component.text("§7realizadas no banco")
                ));

        inv.setItem(12, infoItem);
        inv.setItem(14, pegarHistorico);

        // Flecha de voltar no canto inferior esquerdo
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c§lVoltar",
                Arrays.asList(Component.text("§7Clique para voltar ao menu principal")),
                "back_button");
        inv.setItem(18, backArrow);

        player.openInventory(inv);
        playMenuSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.9f);
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

    private ItemStack criarItemAnimado(Material material, String nome, String id, Material[] materials) {
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

    private void startInventoryAnimation(Player player, Inventory inv) {
        UUID playerId = player.getUniqueId();

        // Cancelar animação anterior se existir
        if (animationTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(animationTasks.get(playerId));
        }

        // Itens para animar (slots principais)
        int[] animatedSlots = {11, 13, 15, 22};
        List<ItemStack> itemsToAnimate = new ArrayList<>();

        for (int slot : animatedSlots) {
            ItemStack item = inv.getItem(slot);
            if (item != null) {
                itemsToAnimate.add(item);
            }
        }

        animatedItems.put(playerId, itemsToAnimate);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!player.isOnline() || !player.getOpenInventory().getTopInventory().equals(inv)) {
                stopInventoryAnimation(player);
                return;
            }

            for (int i = 0; i < animatedSlots.length; i++) {
                if (i < itemsToAnimate.size()) {
                    ItemStack originalItem = itemsToAnimate.get(i);
                    Material[] materialsToUse = (originalItem.getType() == Material.GOLD_NUGGET ||
                            originalItem.getType() == Material.GOLD_INGOT ||
                            originalItem.getType() == Material.GOLD_BLOCK) ?
                            goldMaterials : new Material[]{originalItem.getType()};

                    Material nextMaterial = materialsToUse[(int) (System.currentTimeMillis() / 500 % materialsToUse.length)];

                    ItemStack animatedItem = originalItem.clone();
                    animatedItem.setType(nextMaterial);
                    inv.setItem(animatedSlots[i], animatedItem);
                }
            }
        }, 0L, 5L); // Animação a cada 5 ticks (0.25 segundos)

        animationTasks.put(playerId, taskId);
    }

    private void startParticleAnimation(Player player) {
        UUID playerId = player.getUniqueId();

        // Cancelar animação anterior se existir
        if (particleTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(particleTasks.get(playerId));
        }

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!player.isOnline()) {
                stopParticleAnimation(player);
                return;
            }

            // Partículas giratórias ao redor do jogador
            double time = System.currentTimeMillis() / 1000.0;
            int particlesPerCircle = 12;

            for (int i = 0; i < particlesPerCircle; i++) {
                double angle = 2 * Math.PI * i / particlesPerCircle + time;
                double x = Math.cos(angle) * 1.5;
                double z = Math.sin(angle) * 1.5;

                Color color = particleColors[(i + (int)(time * 2)) % particleColors.length];

                player.getWorld().spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(x, 2.0, z),
                        1,
                        new Particle.DustOptions(color, 1.5f)
                );
            }
        }, 0L, 2L); // Animação a cada 2 ticks (0.1 segundos)

        particleTasks.put(playerId, taskId);
    }

    private void startMusic(Player player) {
        UUID playerId = player.getUniqueId();

        // Cancelar música anterior se existir
        if (musicTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(musicTasks.get(playerId));
        }

        // Tocar música PIGSTEP
        player.stopSound(Sound.MUSIC_DISC_PIGSTEP);
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 0.8f, 1.0f);

        // Task para manter a música tocando
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!player.isOnline() || !player.getOpenInventory().getTitle().contains("Banco")) {
                stopMusic(player);
                return;
            }
            // Manter a música tocando
            player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 0.8f, 1.0f);
        }, 0L, 200L); // Verificar a cada 10 segundos

        musicTasks.put(playerId, taskId);
    }

    private void stopInventoryAnimation(Player player) {
        UUID playerId = player.getUniqueId();
        if (animationTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(animationTasks.get(playerId));
            animationTasks.remove(playerId);
            animatedItems.remove(playerId);
        }
    }

    private void stopParticleAnimation(Player player) {
        UUID playerId = player.getUniqueId();
        if (particleTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(particleTasks.get(playerId));
            particleTasks.remove(playerId);
        }
    }

    private void stopMusic(Player player) {
        UUID playerId = player.getUniqueId();
        if (musicTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(musicTasks.get(playerId));
            musicTasks.remove(playerId);
        }
        player.stopSound(Sound.MUSIC_DISC_PIGSTEP);
    }

    private void playClickAnimation(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.8f);
        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 15, 0.3, 0.3, 0.3, 0.1);
    }

    private void playBankAnimation(Player player, boolean opening) {
        if (opening) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 1.2f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
        }
    }

    private void playMenuSound(Player player, Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, 0.6f, pitch);
        player.spawnParticle(Particle.HEART, player.getLocation(), 5, 0.2, 0.2, 0.2, 0.05);
    }

    private void playSuccessAnimation(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f, 1.2f);
        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.2);
    }

    private void playErrorAnimation(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7f, 0.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 0.8f);
        player.spawnParticle(Particle.SMOKE, player.getLocation(), 15, 0.3, 0.3, 0.3, 0.1);
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

        if (inventoryTitle.contains(MAIN_MENU_TITLE) ||
                inventoryTitle.contains(SALDO_MENU_TITLE) ||
                inventoryTitle.contains(EMPRESTIMO_MENU_TITLE) ||
                inventoryTitle.contains(HISTORICO_MENU_TITLE)) {

            event.setCancelled(true);

            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);

                if ("back_button".equals(itemId)) {
                    playClickAnimation(player);
                    abrirMenuPrincipal(player);
                    return;
                }

                playClickAnimation(player);

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
                    case "pedir_emprestimo":
                        Economia.processarEmprestimo(player, 1000);
                        playSuccessAnimation(player);
                        abrirMenuEmprestimo(player);
                        break;
                    case "pagar_emprestimo":
                        boolean sucesso = Economia.pagarEmprestimo(player);
                        if (sucesso) {
                            playSuccessAnimation(player);
                        } else {
                            playErrorAnimation(player);
                        }
                        abrirMenuEmprestimo(player);
                        break;
                    case "pegar_historico":
                        List<String> historico = Economia.getHistorico(player);
                        ItemStack livro = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta bookMeta = (BookMeta) livro.getItemMeta();
                        bookMeta.setTitle("Histórico de Moly");
                        bookMeta.setAuthor(player.getName());

                        List<Component> pages = historico.stream()
                                .map(Component::text)
                                .collect(Collectors.toList());

                        if (pages.isEmpty()) {
                            pages.add(Component.text("Sem histórico de transações disponível."));
                        }

                        bookMeta.pages(pages);
                        livro.setItemMeta(bookMeta);

                        if (player.getInventory().addItem(livro).isEmpty()) {
                            player.sendMessage(Component.text("✅ Você recebeu seu histórico de transações.").color(NamedTextColor.GREEN));
                            playSuccessAnimation(player);
                        } else {
                            player.sendMessage(Component.text("❌ Seu inventário está cheio!").color(NamedTextColor.RED));
                            playErrorAnimation(player);
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Banco") || title.contains("Saldo") || title.contains("Empréstimo") || title.contains("Histórico")) {
            Player player = (Player) event.getPlayer();
            stopInventoryAnimation(player);
            stopParticleAnimation(player);
            stopMusic(player);
            playBankAnimation(player, false);
        }
    }
}