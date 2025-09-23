package org.dantesys.reliquiasNexus.raids.invasao;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.raids.CommomEvent;
import org.dantesys.reliquiasNexus.raids.EventStatus;
import org.dantesys.reliquiasNexus.raids.RaidDificuldade;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.SALDO;

public class InvasaoManager extends CommomEvent{
    private final Map<UUID, Double> danoPorJogador = new HashMap<>();
    private final List<LivingEntity> mobs = new ArrayList<>();
    private final Random random = new Random();
    private BossBar bossBar;
    private int timer;
    private int mobCount;

    public InvasaoManager(Location location, JavaPlugin plugin) {
        super(location,plugin);
    }

    @Override
    public void start() {
        changeStatus(EventStatus.RUNNING);

        List<EntityType> possibleMobs = new ArrayList<>();

        // Define mobs por dificuldade
        switch (dificuldade) {
            case FACIL -> {
                mobCount = 8;
                possibleMobs.add(EntityType.SLIME);
                possibleMobs.add(EntityType.MAGMA_CUBE);
            }
            case MEDIO -> {
                mobCount = 10;
                possibleMobs.add(EntityType.ZOMBIE);
                possibleMobs.add(EntityType.HUSK);
            }
            case DIFICIL -> {
                mobCount = 12;
                possibleMobs.add(EntityType.SKELETON);
                possibleMobs.add(EntityType.BOGGED);
                possibleMobs.add(EntityType.STRAY);
                possibleMobs.add(EntityType.WITHER_SKELETON);
            }
            case EXPERT -> {
                mobCount = 15;
                possibleMobs.add(EntityType.BLAZE);
                possibleMobs.add(EntityType.BREEZE);
                possibleMobs.add(EntityType.SPIDER);
                possibleMobs.add(EntityType.CAVE_SPIDER);
            }
            case INSANO -> {
                mobCount = 25;
                possibleMobs.add(EntityType.PILLAGER);
                possibleMobs.add(EntityType.VINDICATOR);
                possibleMobs.add(EntityType.EVOKER);
                possibleMobs.add(EntityType.ILLUSIONER);
            }
            default -> {
                mobCount = 10;
                possibleMobs.add(EntityType.CREEPER);
            }
        }
        // duração total em ticks
        // exemplo: 5 minutos
        this.timer = 20 * 60 * 5;

        this.bossBar = Bukkit.createBossBar(
                "RAID!",
                BarColor.RED,
                BarStyle.SOLID
        );
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(p -> bossBar.addPlayer(p));

        // Spawn mobs
        for (int i = 0; i < mobCount; i++) {
            Location spawnLoc = getLocation().clone().add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
            EntityType chosenType = possibleMobs.get(random.nextInt(possibleMobs.size()));
            LivingEntity mob = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, chosenType);

            // Vida base + aumento por dificuldade
            double baseHealth = 20 + dificuldade.ordinal() * 10;
            mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(baseHealth);
            mob.setHealth(baseHealth);
            mobs.add(mob);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                mobs.removeIf(m -> m == null || m.isDead());
                if(mobs.isEmpty()) {
                    broadcast(Component.text("✅ Todos os mobs foram derrotados!", NamedTextColor.GREEN));
                    changeStatus(EventStatus.WIN); // 1. marca o evento como terminado
                    distribuirRecompensas();       // 2. distribui as recompensas
                    bossBar.removeAll();           // 3. limpa a barra
                    cancel();                      // 4. para o runnable
                    return;
                }
                timer--;
                updateBossBar();
                mobs.forEach(mob -> mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,200,0)));
                if(timer <= 0) {
                    broadcast(Component.text("❌ Os mobs tomaram conta da área!", NamedTextColor.DARK_RED));
                    spawnMobsExtras();
                    bossBar.removeAll();
                    changeStatus(EventStatus.LOSE);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    private void updateBossBar() {
        // Atualizar BossBar
        if(mobs.isEmpty())return;
        double progresso = Math.max(0, (double)mobs.size() / (double)mobCount);
        bossBar.setProgress(progresso);
        String texto = "Mobs restantes: " + mobs.size() + " | Tempo: " + (timer/20) + "s";
        bossBar.setTitle(texto);
    }
    private void spawnMobsExtras() {
        int extraCount = dificuldade.ordinal() * 5; // mais mobs dependendo da dificuldade
        List<EntityType> possibleMobs = new ArrayList<>();

        // Escolher mobs semelhantes aos da invasão original
        switch (dificuldade) {
            case FACIL -> {
                possibleMobs.add(EntityType.SLIME);
                possibleMobs.add(EntityType.MAGMA_CUBE);
            }
            case MEDIO -> {
                possibleMobs.add(EntityType.ZOMBIE);
                possibleMobs.add(EntityType.HUSK);
            }
            case DIFICIL -> {
                possibleMobs.add(EntityType.SKELETON);
                possibleMobs.add(EntityType.BOGGED);
                possibleMobs.add(EntityType.STRAY);
                possibleMobs.add(EntityType.WITHER_SKELETON);
            }
            case EXPERT -> {
                possibleMobs.add(EntityType.BLAZE);
                possibleMobs.add(EntityType.SPIDER);
                possibleMobs.add(EntityType.CAVE_SPIDER);
                possibleMobs.add(EntityType.BREEZE);
            }
            case INSANO -> {
                possibleMobs.add(EntityType.PILLAGER);
                possibleMobs.add(EntityType.VINDICATOR);
                possibleMobs.add(EntityType.EVOKER);
                possibleMobs.add(EntityType.ILLUSIONER);
                possibleMobs.add(EntityType.RAVAGER);
            }
            default -> possibleMobs.add(EntityType.CREEPER);
        }

        for (int i = 0; i < extraCount; i++) {
            Location spawnLoc = getLocation().clone().add(random.nextInt(10)-5, 0, random.nextInt(10)-5);
            EntityType chosenType = possibleMobs.get(random.nextInt(possibleMobs.size()));
            LivingEntity mob = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, chosenType);

            // Vida base menor que na invasão original
            double baseHealth = 20 + dificuldade.ordinal() * 5;
            mob.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(baseHealth);
            mob.setHealth(baseHealth);


            mobs.add(mob);
        }

        broadcast(Component.text("⚠️ Mobs restantes ocupam a área! Tome cuidado!").color(NamedTextColor.RED));
    }

    @Override
    public void stop() {
        if(bossBar != null) {
            bossBar.removeAll();
        }
        mobs.forEach(m -> { if(m.isValid()) m.remove(); });
        mobs.clear();
    }
    private void distribuirRecompensas() {
        if(danoPorJogador.isEmpty()) return;

        // Dinheiro base por dificuldade
        int recompensaBase;
        switch(dificuldade) {
            case FACIL -> recompensaBase = 1;
            case DIFICIL -> recompensaBase = 5;
            case EXPERT -> recompensaBase = 7;
            case INSANO -> recompensaBase = 10;
            default -> recompensaBase = 3;
        }
        for (Map.Entry<UUID, Double> entry : danoPorJogador.entrySet()) {
            UUID uuid = entry.getKey();
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || !p.isOnline()) continue;
            double dano = entry.getValue();

            // recompensa proporcional
            double recompensa = recompensaBase * dano * 1.5; // esse 100 é um multiplicador opcional
            if (recompensa < 10) recompensa = 10;

            double saldo = p.getPersistentDataContainer().getOrDefault(SALDO.key, PersistentDataType.DOUBLE, 0.0);
            p.getPersistentDataContainer().set(SALDO.key, PersistentDataType.DOUBLE, saldo + recompensa);

            String precoStr = String.format("%.2f", recompensa);
            p.sendMessage(Component.text("💰  " +
                    ReliquiasNexus.getLang().getString("raid.win", "Você ganhou <money> <name> pela raid!")
                            .replace("<money>", precoStr)
                            .replace("<name>", ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","moly"))
            ).color(NamedTextColor.GREEN));
        }

    }
    @Override
    public void handleDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof LivingEntity mob)) return;
        if(!mobs.contains(mob)) return;
        Player damager = null;
        if(e.getDamager() instanceof Player p) {
            damager = p;
        } else if(e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
            damager = shooter;
        }
        if(damager != null) {
            danoPorJogador.merge(damager.getUniqueId(), e.getFinalDamage(), Double::sum);
        }
    }

}
