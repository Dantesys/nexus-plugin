package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragTempest;

public class Tempest extends DBossBase{
    public Tempest(LivingEntity boss) {
        super(boss, "Zephyr");
    }

    @Override
    public void spawnMinios() {
        for (int i = 0; i < 2; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            Phantom minion = boss.getWorld().spawn(loc, Phantom.class);
            minion.setCustomName("Minion");
        }
        Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
        Breeze minion = boss.getWorld().spawn(loc, Breeze.class);
        minion.setCustomName("Minion");
    }

    @Override
    protected void specialFull() {
        Location loc = boss.getLocation();
        boss.getWorld().strikeLightningEffect(loc); // efeito visual
        boss.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2f, 1f);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 30) {
                p.damage(8, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                boss.getWorld().strikeLightningEffect(p.getLocation());
            }
        }
        cdFull = 15 * 20; // 15s cooldown
    }

    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 2f, 1f);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 30) {
                Vector puxar = loc.toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.5);
                p.setVelocity(puxar);
                p.damage(6, boss);
            }
        }
        cdHalf = 20 * 20; // 20s cooldown
    }

    @Override
    protected void specialLow() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 2f, 1f);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 30) {
                Vector knock = p.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2);
                p.setVelocity(knock);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
            }
        }
        cdLow = 25 * 20; // 25s cooldown
    }

    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragTempest.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
