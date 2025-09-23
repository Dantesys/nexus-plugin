package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragUmbra;

public class Umbra extends DBossBase{
    public Umbra(LivingEntity boss) {
        super(boss,"Shadow");
    }
    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            WitherSkeleton minion = boss.getWorld().spawn(loc, WitherSkeleton.class);
            minion.setCustomName("Minion");
        }
    }
    @Override
    protected void specialFull() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 2f, 1f);
        boss.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 120, 3, 1, 3, 0.2);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 10) {
                p.damage(6, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
            }
        }
        cdFull = 15 * 20;
    }
    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 2f, 0.8f);
        boss.getWorld().spawnParticle(Particle.SMOKE, loc, 100, 4, 2, 4, 0.2);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 8) {
                p.damage(10, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1));
            }
        }
        cdHalf = 20 * 20;
    }
    @Override
    protected void specialLow() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 2f, 0.6f);
        boss.getWorld().spawnParticle(Particle.PORTAL, loc, 150, 5, 3, 5, 0.3);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 8) {
                p.damage(12, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            }
        }
        cdLow = 25 * 20;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragUmbra.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
