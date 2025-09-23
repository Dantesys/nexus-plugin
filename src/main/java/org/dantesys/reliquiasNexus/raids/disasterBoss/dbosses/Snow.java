package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Stray;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragSnow;

public class Snow extends DBossBase{
    public Snow(LivingEntity boss) {
        super(boss,"Blizar");
    }
    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            Stray minion = boss.getWorld().spawn(loc, Stray.class);
            minion.setCustomName("Minion");
        }
    }
    @Override
    protected void specialFull() {
        boss.getWorld().playSound(boss.getLocation(), Sound.WEATHER_RAIN_ABOVE, 2f, 0.5f);
        boss.getWorld().spawnParticle(Particle.SNOWFLAKE, boss.getLocation(), 200, 6, 3, 6, 0.3);
        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),8)) {
            p.damage(7, boss);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1));
        }
        cdFull=10*20;
    }
    @Override
    protected void specialHalf() {
        boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_GLASS_BREAK, 2f, 0.8f);
        boss.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, boss.getLocation(), 120, 4, 2, 4, 0.2);

        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),10)) {
            p.damage(10, boss);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
        }
        cdHalf=15*20;
    }
    @Override
    protected void specialLow() {
        boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.6f);
        boss.getWorld().spawnParticle(Particle.CLOUD, boss.getLocation(), 250, 8, 4, 8, 0.5);

        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),15)) {
            p.damage(15, boss);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 4));
            p.setVelocity(p.getLocation().getDirection().multiply(-1).setY(0.6));
        }
        cdLow=25*20;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragSnow.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
