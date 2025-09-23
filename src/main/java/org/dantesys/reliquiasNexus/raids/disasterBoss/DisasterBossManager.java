package org.dantesys.reliquiasNexus.raids.disasterBoss;


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
import org.dantesys.reliquiasNexus.raids.boss.bosses.*;
import org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SALDO;

public class DisasterBossManager extends CommomEvent{

private final JavaPlugin plugin;
private final Random random = new Random();
private final Map<UUID, Double> damagePorJogador = new HashMap<>();
private double spawnMinionCK=0.84;
private DBossBase chefao;
private BossBar bossBar;
private int timer;
private Location finaloc;
public DisasterBossManager(Location location, JavaPlugin plugin) {
    super(location, plugin);
    this.plugin = plugin;
    finaloc=location;
}

@Override
public void start() {
    changeStatus(EventStatus.RUNNING);
    spawnBoss();
    setupBossBar();

    // Runnable que atualiza boss a cada tick
    this.timer = 20 * 60 * 5;
    new BukkitRunnable() {
        @Override
        public void run() {
            if(chefao.getBoss() == null || !chefao.getBoss().isValid()){
                chefao.getDrops().forEach(item -> finaloc.getWorld().dropItemNaturally(finaloc,item));
                broadcast(Component.text("✅ O boss foi derrotado!", NamedTextColor.GREEN));
                changeStatus(EventStatus.WIN);  // 1. Marca como finalizado
                distributeRewards();            // 2. Dá as recompensas
                stop();                         // 3. Remove boss e barra
                cancel();
            }
            if(timer<=0){
                broadcast(Component.text("❌ O boss fugiu!", NamedTextColor.DARK_RED));
                changeStatus(EventStatus.LOSE);
                stop();
                cancel();
                return;
            }
            timer--;
            finaloc=chefao.getBoss().getLocation();
            chefao.getBoss().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,100,0));
            chefao.useSpecial();      // usa habilidade se cooldown zerado
            spawnMN();
            updateBossBar();          // atualiza barra de vida
        }
    }.runTaskTimer(plugin, 0L, 1L);
}
private void spawnMN(){
    double max = chefao.getBoss().getAttribute(Attribute.MAX_HEALTH).getValue();
    double vida = chefao.getBoss().getHealth();
    if(vida/max<=spawnMinionCK){
        spawnMinionCK-=0.16;
        chefao.spawnMinios();
    }
}
@Override
public void stop() {
    if(chefao != null && chefao.getBoss().isValid()) chefao.getBoss().remove();
    if(bossBar != null) bossBar.removeAll();
}

private void spawnBoss() {
    int escolha = random.nextInt(8);

    chefao = switch(escolha){
        case 1 -> new Water(spawnEntity(ElderGuardian.class));
        case 2 -> new Tempest(spawnEntity(Breeze.class));
        case 3 -> new Earth(spawnEntity(Ravager.class));
        case 4 -> new Toxic(spawnEntity(Witch.class));
        case 5 -> new Snow(spawnEntity(Stray.class));
        case 6 -> new Solar(spawnEntity(Blaze.class));
        case 7 -> new Umbra(spawnEntity(WitherSkeleton.class));
        default -> new Magma(spawnEntity(MagmaCube.class));
    };

    // Custom name e atributos
    chefao.getBoss().getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(1000);
    chefao.getBoss().setHealth(chefao.getBoss().getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
}

private <T extends LivingEntity> T spawnEntity(Class<T> clazz){
    return getLocation().getWorld().spawn(getLocation(), clazz);
}

private void setupBossBar() {
    bossBar = Bukkit.createBossBar("Boss Lv." + chefao.getBoss().getCustomName() + " - " +(this.timer/20)+" s", BarColor.RED, BarStyle.SEGMENTED_6);
    bossBar.setVisible(true);
    // Adiciona todos os players online
    Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
}

private void updateBossBar() {
    if(chefao == null || !chefao.getBoss().isValid() || bossBar == null) return;
    double health = chefao.getBoss().getHealth();
    double max = chefao.getBoss().getAttribute(Attribute.MAX_HEALTH).getValue();
    bossBar.setProgress(Math.max(0, health / max));
    bossBar.setTitle("Boss Lv." + chefao.getBoss().getCustomName() + " - " + (timer/20) + " s");
}

@Override
public void handleDamage(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof LivingEntity mob)) return;
    if(!mob.getPersistentDataContainer().has(NEXUS.key)) return;
    Player damager = null;
    if(e.getDamager() instanceof Player p) {
        damager = p;
    } else if(e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player shooter) {
        damager = shooter;
    }
    if(damager != null) {
        damagePorJogador.merge(damager.getUniqueId(), e.getFinalDamage(), Double::sum);
    }
}

private void distributeRewards() {
    if(damagePorJogador.isEmpty()) return;

    // Dinheiro base por dificuldade
    int recompensaBase;
    switch(dificuldade) {
        case FACIL -> recompensaBase = 1;
        case DIFICIL -> recompensaBase = 5;
        case EXPERT -> recompensaBase = 7;
        case INSANO -> recompensaBase = 10;
        default -> recompensaBase = 3;
    }
    for (Map.Entry<UUID, Double> entry : damagePorJogador.entrySet()) {
        UUID uuid = entry.getKey();
        Player p = Bukkit.getPlayer(uuid);
        if (p == null || !p.isOnline()) continue;

        double dano = entry.getValue();

        // recompensa proporcional
        double recompensa = recompensaBase * dano * 2; // esse 100 é um multiplicador opcional
        if (recompensa < 10) recompensa = 10;

        double saldo = p.getPersistentDataContainer().getOrDefault(SALDO.key, PersistentDataType.DOUBLE, 0.0);
        p.getPersistentDataContainer().set(SALDO.key, PersistentDataType.DOUBLE, saldo + recompensa);

        String precoStr = String.format("%.2f", recompensa);
        p.sendMessage(Component.text("💰  " +
                ReliquiasNexus.getLang().getString("raid.win", "Você ganhou <money> <name> pela invasão!")
                        .replace("<money>", precoStr)
                        .replace("<name>", ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","moly"))
        ).color(NamedTextColor.GREEN));
    }
}
}