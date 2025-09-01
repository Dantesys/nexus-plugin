package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class MorteEvent implements Listener {

    private final ReliquiasNexus plugin;
    private static final Map<UUID, Long> deathTimers = new HashMap<>();
    private static final Map<UUID, Boolean> hasPaid = new HashMap<>();

    public MorteEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {

            deathTimers.put(player.getUniqueId(), System.currentTimeMillis());
            hasPaid.put(player.getUniqueId(), false);

            player.setGameMode(GameMode.SPECTATOR);

            player.sendMessage(Component.text("§cVocê morreu!").color(NamedTextColor.RED));
            player.sendMessage(Component.text("§eEspere 3 minutos para renascer. Seus itens serão dropados no local da morte."));

            if (Economia.getSaldo(player) >= 250) {
                player.sendMessage(Component.text("§aOu pague 250 moly para renascer imediatamente com seus itens no inventário.").append(Component.text(" §b[PAGAR]").clickEvent(ClickEvent.runCommand("/nexus pagar_renascer"))).color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("§cVocê não tem moly suficiente para pagar o renascimento."));
            }

            new BukkitRunnable() {
                private final UUID playerUUID = player.getUniqueId();
                private long secondsPassed = 0;

                @Override
                public void run() {
                    if (player.isOnline() && player.getGameMode() == GameMode.SPECTATOR) {
                        long currentTime = System.currentTimeMillis();
                        long timeElapsed = currentTime - deathTimers.get(playerUUID);
                        secondsPassed = timeElapsed / 1000;
                        long remainingTime = 180 - secondsPassed;

                        if (remainingTime <= 0) {
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleport(player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : player.getWorld().getSpawnLocation());
                            player.sendMessage(Component.text("§aO tempo acabou! Você renasceu sem seus itens.").color(NamedTextColor.GREEN));
                            deathTimers.remove(playerUUID);
                            hasPaid.remove(playerUUID);
                            this.cancel();
                        } else {
                            player.sendActionBar(Component.text("§eTempo restante para renascer: " + remainingTime + "s"));
                        }
                    } else {
                        deathTimers.remove(playerUUID);
                        hasPaid.remove(playerUUID);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (hasPaid.getOrDefault(player.getUniqueId(), false)) {
            player.sendMessage(Component.text("§aVocê pagou para renascer e manteve seus itens.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("§cVocê renasceu e perdeu seus itens.").color(NamedTextColor.RED));
        }
        deathTimers.remove(player.getUniqueId());
        hasPaid.remove(player.getUniqueId());
    }

    public static void handlePaidRespawn(Player player) {
        if (deathTimers.containsKey(player.getUniqueId())) {
            if (Economia.getSaldo(player) >= 250) {
                Economia.removerSaldo(player, 250, "Renascimento instantâneo");
                hasPaid.put(player.getUniqueId(), true);

                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : player.getWorld().getSpawnLocation());

                player.sendMessage(Component.text("§aPagamento efetuado! Você renasceu com seus itens.").color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("§cVocê não tem moly suficiente para pagar o renascimento.").color(NamedTextColor.RED));
            }
        }
    }
}