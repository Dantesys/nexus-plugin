package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MorteEvent implements Listener {

    private final ReliquiasNexus plugin;
    private static final Map<UUID, Long> deathTimers = new HashMap<>();
    private static final Map<UUID, List<ItemStack>> playerItemsAtDeath = new HashMap<>();
    private static final Map<UUID, Location> deathLocations = new HashMap<>();

    public MorteEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {

            // Verifica se o expurgo está ativo para desativar o sistema de renascimento
            if (ReliquiasNexus.getNexusConfig().getBoolean("expurgo")) {
                return;
            }

            deathTimers.put(player.getUniqueId(), System.currentTimeMillis());
            deathLocations.put(player.getUniqueId(), player.getLocation());

            // Separa os itens normais das relíquias
            List<ItemStack> normalItemsToDrop = new ArrayList<>();
            List<ItemStack> relicsToKeep = new ArrayList<>();
            for (ItemStack item : event.getDrops()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if (data.has(NexusKeys.NEXUS.key, PersistentDataType.STRING)) {
                        relicsToKeep.add(item.clone());
                    } else {
                        normalItemsToDrop.add(item.clone());
                    }
                } else {
                    normalItemsToDrop.add(item.clone());
                }
            }

            // Guarda os itens normais para uma possível restauração
            playerItemsAtDeath.put(player.getUniqueId(), normalItemsToDrop);
            event.getDrops().clear(); // Impede que os itens caiam no chão imediatamente

            // Devolve as relíquias ao jogador
            PlayerInventory inv = player.getInventory();
            for (ItemStack relic : relicsToKeep) {
                inv.addItem(relic);
            }
            relicsToKeep.clear(); // Limpa a lista de relíquias salvas

            player.setGameMode(GameMode.SPECTATOR);

            player.sendMessage(Component.text("§cVocê morreu!").color(NamedTextColor.RED));
            player.sendMessage(Component.text("§eEspere 1 minuto e 30 segundos para renascer. Seus itens (exceto relíquias) serão dropados no local da morte."));

            if (Economia.getSaldo(player) >= 250) {
                player.sendMessage(Component.text("§aOu pague 250 moly para renascer imediatamente com seus itens no inventário.")
                        .append(Component.text(" §b[PAGAR]").clickEvent(ClickEvent.runCommand("/nexus pagar_renascer"))).color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("§cVocê não tem moly suficiente para pagar o renascimento."));
            }

            new BukkitRunnable() {
                private final UUID playerUUID = player.getUniqueId();

                @Override
                public void run() {
                    if (player.isOnline() && player.getGameMode() == GameMode.SPECTATOR && deathTimers.containsKey(playerUUID)) {
                        long timeElapsed = (System.currentTimeMillis() - deathTimers.get(playerUUID)) / 1000;
                        long remainingTime = 90 - timeElapsed; // 1 minuto e 30 segundos = 90 segundos

                        if (remainingTime <= 0) {
                            // Tempo acabou, revive o jogador e dropa os itens
                            player.setGameMode(GameMode.SURVIVAL);

                            // Teleporta o jogador 5 blocos de distância
                            Location deathLoc = deathLocations.get(playerUUID);
                            if (deathLoc != null) {
                                double newX = deathLoc.getX() + (ThreadLocalRandom.current().nextDouble() * 10 - 5);
                                double newZ = deathLoc.getZ() + (ThreadLocalRandom.current().nextDouble() * 10 - 5);
                                double newY = player.getWorld().getHighestBlockYAt((int) newX, (int) newZ) + 1;
                                player.teleport(new Location(player.getWorld(), newX, newY, newZ));
                            } else {
                                player.teleport(player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : player.getWorld().getSpawnLocation());
                            }

                            player.sendMessage(Component.text("§aO tempo acabou! Você renasceu sem seus itens.").color(NamedTextColor.GREEN));

                            // Deixa os itens caírem no chão
                            List<ItemStack> items = playerItemsAtDeath.get(playerUUID);
                            if(items != null) {
                                for(ItemStack item : items) {
                                    player.getWorld().dropItemNaturally(deathLocations.get(playerUUID), item);
                                }
                                playerItemsAtDeath.remove(playerUUID);
                            }

                            deathTimers.remove(playerUUID);
                            deathLocations.remove(playerUUID);
                            this.cancel();
                        } else {
                            long minutes = remainingTime / 60;
                            long seconds = remainingTime % 60;
                            String timeString = String.format("%02d:%02d", minutes, seconds);
                            player.sendActionBar(Component.text("§eTempo restante para renascer: " + timeString));
                        }
                    } else {
                        // Limpa o timer se o jogador desconectar ou renascer por outros meios
                        deathTimers.remove(playerUUID);
                        playerItemsAtDeath.remove(playerUUID);
                        deathLocations.remove(playerUUID);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // A lógica de renascimento é tratada no comando
        // e no BukkitRunnable.
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR && deathTimers.containsKey(player.getUniqueId())) {
            // Impede movimento no modo espectador
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ() || event.getFrom().getY() != event.getTo().getY()) {
                event.setCancelled(true);
            }
        }
    }

    public static void handlePaidRespawn(Player player) {
        if (deathTimers.containsKey(player.getUniqueId()) && player.getGameMode() == GameMode.SPECTATOR) {
            if (Economia.getSaldo(player) >= 250) {
                Economia.removerSaldo(player, 250, "Renascimento instantâneo");

                // Restaura os itens normais no inventário do jogador
                List<ItemStack> itemsToRestore = playerItemsAtDeath.get(player.getUniqueId());
                if (itemsToRestore != null) {
                    PlayerInventory inv = player.getInventory();
                    for (ItemStack item : itemsToRestore) {
                        inv.addItem(item);
                    }
                    playerItemsAtDeath.remove(player.getUniqueId());
                }

                player.setGameMode(GameMode.SURVIVAL);
                // Renasce no local de spawn padrão ou na cama
                player.teleport(player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : player.getWorld().getSpawnLocation());

                player.sendMessage(Component.text("§aPagamento efetuado! Você renasceu com seus itens.").color(NamedTextColor.GREEN));

                deathTimers.remove(player.getUniqueId());
                deathLocations.remove(player.getUniqueId());
            } else {
                player.sendMessage(Component.text("§cVocê não tem moly suficiente para pagar o renascimento.").color(NamedTextColor.RED));
            }
        }
    }
}