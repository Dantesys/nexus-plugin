package org.dantesys.reliquiasNexus.bosses;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
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
import org.bukkit.Particle;
import org.bukkit.Color;
import org.bukkit.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossManager {

    private final ReliquiasNexus plugin;
    private final Random random = new Random();
    private BukkitRunnable spawnTask;
    private LivingEntity currentBoss;
    private Boss currentBossData;
    private BossBar bossBar;
    private long spawnTime;
    private boolean isSpawning = false;
    private BukkitRunnable despawnTask;
    private BukkitRunnable beaconTask;

    public BossManager(ReliquiasNexus plugin) {
        this.plugin = plugin;
        this.bossBar = BossBar.bossBar(Component.empty(), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        startBossSpawnTask();
    }

    public void startBossSpawnTask() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }

        spawnTask = new BukkitRunnable() {
            int timeUntilSpawn = 1800; // 30 minutos

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    timeUntilSpawn = 1800;
                    return;
                }

                if (currentBoss != null && !currentBoss.isDead()) {
                    return;
                }

                if (timeUntilSpawn <= 0) {
                    if (!isSpawning) {
                        isSpawning = true;
                        spawnRandomBoss();
                        timeUntilSpawn = 1800;
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

    public void spawnBoss(BossRarity rarity, boolean isSuperBoss) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        if (currentBoss != null && !currentBoss.isDead()) {
            return;
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Player targetPlayer = players.get(random.nextInt(players.size()));

        Location spawnLocation = findSafeLocation(targetPlayer.getLocation(), 100);
        if (spawnLocation == null) {
            spawnBoss(rarity, isSuperBoss);
            return;
        }

        Boss boss = new Boss(rarity, spawnLocation, plugin, isSuperBoss);
        currentBoss = boss.spawn();
        currentBossData = boss;
        spawnTime = System.currentTimeMillis();

        String rarityColor = getColorCode(rarity.color);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(spawnLocation.getWorld()) && p.getLocation().distance(spawnLocation) < 200) {
                p.sendMessage(Component.text(rarityColor + boss.getBossName() + " §7despertou por perto!"));
            }
        }

        // Show boss bar
        bossBar.name(Component.text(rarityColor + boss.getBossName()));
        bossBar.color(getBossBarColor(rarity.color));
        bossBar.progress(1.0f);
        Bukkit.getOnlinePlayers().forEach(p -> p.showBossBar(bossBar));

        // Start the boss beacon with rarity color
        startBossBeacon(spawnLocation, rarity);

        // Add death listener
        Listener deathListener = new Listener() {
            @EventHandler
            public void onEntityDeath(EntityDeathEvent event) {
                if (event.getEntity().equals(currentBoss)) {
                    List<ItemStack> drops = BossDrop.getRandomDrops(rarity);
                    if (drops != null) {
                        for (ItemStack drop : drops) {
                            currentBoss.getWorld().dropItemNaturally(currentBoss.getLocation(), drop);
                        }
                        Bukkit.broadcast(Component.text("§a✧ " + boss.getBossName() + " dropou " + drops.size() + " itens raros! ✧"));
                    }
                    Bukkit.broadcast(Component.text("§a✔ " + boss.getBossName() + " foi derrotado!"));

                    // Hide and unregister boss bar
                    Bukkit.getOnlinePlayers().forEach(p -> p.hideBossBar(bossBar));
                    HandlerList.unregisterAll(this);
                    currentBoss = null;
                    currentBossData = null;
                    if (despawnTask != null) {
                        despawnTask.cancel();
                    }
                    if (beaconTask != null) {
                        beaconTask.cancel();
                    }
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(deathListener, plugin);

        // Update boss bar
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentBoss == null || currentBoss.isDead()) {
                    this.cancel();
                    return;
                }
                double health = currentBoss.getHealth();
                double maxHealth = currentBoss.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                bossBar.progress((float) (health / maxHealth));

                // Action bar for nearby players
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(currentBoss.getWorld()) && p.getLocation().distance(currentBoss.getLocation()) < 50) {
                        p.sendActionBar(Component.text(rarityColor + "⚠ " + boss.getBossName() + " está por perto! ⚠"));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        // Despawn task
        despawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentBoss != null && !currentBoss.isDead()) {
                    currentBoss.remove();
                    bossBar = null;
                    Bukkit.broadcast(Component.text("§7O " + boss.getBossName() + " desapareceu por falta de desafio!"));
                }
                Bukkit.getOnlinePlayers().forEach(p -> p.hideBossBar(bossBar));
                currentBoss = null;
                currentBossData = null;
                if (beaconTask != null) {
                    beaconTask.cancel();
                }
            }
        };
        despawnTask.runTaskLater(plugin, 20L * 60 * 20); // 20 minutos
    }

    private String getColorCode(TextColor color) {
        if (color.equals(NamedTextColor.GRAY)) return "§7";
        if (color.equals(NamedTextColor.GREEN)) return "§a";
        if (color.equals(NamedTextColor.AQUA)) return "§b";
        if (color.equals(NamedTextColor.LIGHT_PURPLE)) return "§d";
        if (color.equals(NamedTextColor.GOLD)) return "§6";
        return "§f";
    }

    private BossBar.Color getBossBarColor(TextColor color) {
        if (color.equals(NamedTextColor.GRAY)) return BossBar.Color.WHITE;
        if (color.equals(NamedTextColor.GREEN)) return BossBar.Color.GREEN;
        if (color.equals(NamedTextColor.AQUA)) return BossBar.Color.BLUE;
        if (color.equals(NamedTextColor.LIGHT_PURPLE)) return BossBar.Color.PURPLE;
        if (color.equals(NamedTextColor.GOLD)) return BossBar.Color.YELLOW;
        return BossBar.Color.WHITE;
    }

    public void spawnRandomBoss() {
        BossRarity rarity = chooseRandomRarity();
        spawnBoss(rarity, false);
    }

    public void spawnSuperBoss() {
        spawnBoss(BossRarity.LEGENDARY, true);
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
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();
        int spawnY = world.getHighestBlockYAt(centerX, centerZ) + 1;
        return new Location(world, centerX, spawnY, centerZ);
    }

    private void startBossBeacon(Location location, BossRarity rarity) {
        if (beaconTask != null) {
            beaconTask.cancel();
        }

        beaconTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentBoss == null || currentBoss.isDead()) {
                    this.cancel();
                    return;
                }

                // Use different particle effects based on rarity
                Particle particleType;
                switch (rarity) {
                    case COMMUN:
                        particleType = Particle.SMOKE;
                        break;
                    case INCOMMON:
                        particleType = Particle.HAPPY_VILLAGER;
                        break;
                    case RARE:
                        particleType = Particle.ANGRY_VILLAGER;
                        break;
                    case EPIC:
                        particleType = Particle.WITCH;
                        break;
                    case LEGENDARY:
                        particleType = Particle.FLAME;
                        break;
                    default:
                        particleType = Particle.CLOUD;
                        break;
                }

                // Spawn particle beam above boss location
                for (int i = 0; i < 20; i++) {
                    Location particleLoc = location.clone().add(0, i, 0);
                    location.getWorld().spawnParticle(
                            particleType,
                            particleLoc,
                            2,
                            0.1, 0.1, 0.1,
                            0.05
                    );
                }

                // Also play ender signal effect
                location.getWorld().playEffect(location.clone().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);
            }
        };
        beaconTask.runTaskTimer(plugin, 0L, 5L);
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
        if (despawnTask != null) {
            despawnTask.cancel();
        }
        if (beaconTask != null) {
            beaconTask.cancel();
        }
    }
}