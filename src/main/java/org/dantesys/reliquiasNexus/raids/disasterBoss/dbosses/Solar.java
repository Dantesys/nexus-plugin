package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragSolar;


public class Solar extends DBossBase{
    public Solar(LivingEntity boss) {
        super(boss,"Solaris");
    }

    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Blaze blaze = (Blaze) boss.getWorld().spawnEntity(
                    boss.getLocation().add(Math.random() * 2, 0, Math.random() * 2),
                    EntityType.BLAZE
            );
            blaze.setCustomName("Minion");
            blaze.setCustomNameVisible(true);
        }
    }
    @Override
    protected void specialFull() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 2f, 1f);
        boss.getWorld().spawnParticle(Particle.FLAME, loc, 100, 3, 1, 3, 0.2);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 10) {
                p.damage(8, boss);
                p.setFireTicks(60); // 3 segundos de fogo
            }
        }
        cdFull = 15 * 20;
    }
    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 2f, 0.8f);
        boss.getWorld().spawnParticle(Particle.LAVA, loc, 80, 2, 1, 2, 0.2);
        // Projétil explosivo
        Fireball fb = boss.getWorld().spawn(loc.add(0,1,0), Fireball.class);
        fb.setDirection(boss.getLocation().getDirection());
        fb.setYield(3);
        fb.setIsIncendiary(true);
        cdHalf = 20 * 20;
    }
    @Override
    protected void specialLow() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 2f, 1f);
        boss.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 150, 5, 2, 5, 0.3);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 8) {
                p.damage(12, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            }
        }
        cdLow = 25 * 20;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragSolar.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
