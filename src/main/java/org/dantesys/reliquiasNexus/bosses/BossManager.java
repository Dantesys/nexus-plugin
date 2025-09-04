package org.dantesys.reliquiasNexus.bosses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossManager {

    private final ReliquiasNexus plugin;
    private final Random random = new Random();
    private BukkitRunnable spawnTask;
    private LivingEntity currentBoss;
    private Boss currentBossData;
    private boolean isSpawning = false;

    public BossManager(ReliquiasNexus plugin) {
        this.plugin = plugin;
        startBossSpawnTask();
    }

    public void startBossSpawnTask() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }

        spawnTask = new BukkitRunnable() {
            int timeUntilSpawn = 120; // 2 minutos em segundos

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    timeUntilSpawn = 120;
                    return;
                }

                // Verifica se já existe um boss vivo
                if (currentBoss != null && !currentBoss.isDead()) {
                    return;
                }

                if (timeUntilSpawn <= 0) {
                    if (!isSpawning) {
                        isSpawning = true;
                        spawnRandomBoss();
                        timeUntilSpawn = 120;
                        isSpawning = false;
                    }
                } else if (timeUntilSpawn == 60) {
                    Bukkit.broadcast(Component.text("§eUm boss está prestes a aparecer em 1 minuto!"));
                }
                timeUntilSpawn--;
            }
        };
        spawnTask.runTaskTimer(plugin, 0L, 20L);
    }

    public void spawnBoss(BossRarity rarity) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        // Verifica se já existe um boss vivo
        if (currentBoss != null && !currentBoss.isDead()) {
            return;
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Player targetPlayer = players.get(random.nextInt(players.size()));

        Location spawnLocation = findSafeLocation(targetPlayer.getLocation(), 100);
        if (spawnLocation == null) {
            // Tenta spawnar novamente se não encontrar local seguro
            spawnBoss(rarity);
            return;
        }

        Boss boss = new Boss(rarity, spawnLocation, plugin);
        currentBoss = boss.spawn();
        currentBossData = boss;

        // Mensagem de spawn no estilo da imagem
        String rarityColor = getColorCode(rarity.color);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(spawnLocation.getWorld()) && p.getLocation().distance(spawnLocation) < 200) {
                p.sendMessage(Component.text(rarityColor + boss.getBossName() + " §7despertou por perto!"));
            }
        }

        // Adiciona listener de morte para dropar o item
        Listener deathListener = new Listener() {
            @EventHandler
            public void onEntityDeath(EntityDeathEvent event) {
                if (event.getEntity().equals(currentBoss)) {
                    // Dropa o item do boss
                    ItemStack drop = boss.getDropItem();
                    if (drop != null) {
                        currentBoss.getWorld().dropItemNaturally(currentBoss.getLocation(), drop);
                        Bukkit.broadcast(Component.text("§a✧ " + boss.getBossName() + " dropou um item raro! ✧"));
                    }

                    // Mensagem de morte
                    Bukkit.broadcast(Component.text("§a✔ " + boss.getBossName() + " foi derrotado!"));

                    // Remove o listener
                    HandlerList.unregisterAll(this);
                    currentBoss = null;
                    currentBossData = null;
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(deathListener, plugin);

        // Task para mostrar action bar aos jogadores próximos
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentBoss == null || currentBoss.isDead()) {
                    this.cancel();
                    return;
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(currentBoss.getWorld()) && p.getLocation().distance(currentBoss.getLocation()) < 50) {
                        String rarityColor = getColorCode(rarity.color);
                        p.sendActionBar(Component.text(rarityColor + "⚠ " + boss.getBossName() + " está por perto! ⚠"));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private String getColorCode(net.kyori.adventure.text.format.TextColor color) {
        if (color.equals(net.kyori.adventure.text.format.NamedTextColor.GRAY)) return "§7";
        if (color.equals(net.kyori.adventure.text.format.NamedTextColor.GREEN)) return "§a";
        if (color.equals(net.kyori.adventure.text.format.NamedTextColor.AQUA)) return "§b";
        if (color.equals(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE)) return "§d";
        if (color.equals(net.kyori.adventure.text.format.NamedTextColor.GOLD)) return "§6";
        return "§f";
    }

    public void spawnRandomBoss() {
        BossRarity rarity = chooseRandomRarity();
        spawnBoss(rarity);
    }

    private BossRarity chooseRandomRarity() {
        int totalWeight = BossRarity.COMMUN.weight + BossRarity.INCOMMON.weight + BossRarity.RARE.weight + BossRarity.EPIC.weight + BossRarity.LEGENDARY.weight;
        int randomNumber = random.nextInt(totalWeight);

        if (randomNumber < BossRarity.COMMUN.weight) {
            return BossRarity.COMMUN;
        } else if (randomNumber < BossRarity.COMMUN.weight + BossRarity.INCOMMON.weight) {
            return BossRarity.INCOMMON;
        } else if (randomNumber < BossRarity.COMMUN.weight + BossRarity.INCOMMON.weight + BossRarity.RARE.weight) {
            return BossRarity.RARE;
        } else if (randomNumber < BossRarity.COMMUN.weight + BossRarity.INCOMMON.weight + BossRarity.RARE.weight + BossRarity.EPIC.weight) {
            return BossRarity.EPIC;
        } else {
            return BossRarity.LEGENDARY;
        }
    }

    private Location findSafeLocation(Location center, int radius) {
        // Implementação básica - retorna o centro
        // Você pode adicionar lógica para encontrar um local seguro aqui
        return center;
    }

    public void setCurrentBoss(LivingEntity boss) {
        this.currentBoss = boss;
    }

    public LivingEntity getCurrentBoss() {
        return currentBoss;
    }

    public void stop() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
    }
}