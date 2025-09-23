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

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragToxic;

public class Toxic extends DBossBase{
    public Toxic(LivingEntity boss) {
        super(boss,"Plaguebringer");
    }
    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            CaveSpider minion = boss.getWorld().spawn(loc, CaveSpider.class);
            minion.setCustomName("Minion");
        }
    }
    @Override
    protected void specialFull() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITCH_THROW, 2f, 1f);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 12) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 1));
                p.damage(4, boss);
            }
        }
        cdFull = 15 * 20;
    }
    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_SILVERFISH_AMBIENT, 2f, 1f);

        for (int i = 0; i < 5; i++) {
            Silverfish s = (Silverfish) boss.getWorld().spawnEntity(
                    loc.clone().add(Math.random() * 3, 0, Math.random() * 3),
                    EntityType.SILVERFISH
            );
            s.setCustomName("Parasita");
            s.setCustomNameVisible(true);
        }
        cdHalf = 20 * 20;
    }
    @Override
    protected void specialLow() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_WITCH_CELEBRATE, 2f, 1f);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 15) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
                p.damage(6, boss);
            }
        }

        // Partículas de veneno (bola verde simulada)
        loc.getWorld().spawnParticle(Particle.ITEM_SLIME, loc, 50, 1, 1, 1, 0.2);

        cdLow = 25 * 20;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragToxic.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
