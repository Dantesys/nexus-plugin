package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragWater;

public class Water extends DBossBase{
    public Water(LivingEntity boss) {
        super(boss, "Leviatã");
    }

    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            Guardian minion = boss.getWorld().spawn(loc, Guardian.class);
            minion.setCustomName("Minion");
        }
    }

    @Override
    protected void specialFull() {
        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),30)) {
            if (p.getLocation().distance(boss.getLocation()) <= 10) {
                p.teleport(boss.getLocation()); // puxado pela corrente
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            }
        }
        cdFull = 200;
    }

    @Override
    protected void specialHalf() {
        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),30)) {
            if (p.getLocation().distance(boss.getLocation()) <= 12) {
                p.setVelocity(p.getVelocity().setY(1.5));
            }
        }
        cdHalf = 300; // 15 segundos
    }

    @Override
    protected void specialLow() {
        for (Player p : boss.getWorld().getNearbyPlayers(boss.getLocation(),30)) {
            if (p.getLocation().distance(boss.getLocation()) <= 15) {
                p.damage(6.0, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
            }
        }
        cdLow = 400; // 20 segundos
    }

    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragWater.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
