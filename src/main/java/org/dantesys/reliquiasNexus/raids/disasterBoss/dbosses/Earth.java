package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragEarth;

public class Earth extends DBossBase{
    public Earth(LivingEntity boss) {
        super(boss,"Terramoth");
    }
    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Zombie z = (Zombie) boss.getWorld().spawnEntity(
                    boss.getLocation().add(Math.random() * 2, 0, Math.random() * 2),
                    EntityType.ZOMBIE
            );
            z.getEquipment().setHelmet(new ItemStack(Material.OBSIDIAN));
            z.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            z.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            z.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
            z.setCustomName("Minion");
            z.setCustomNameVisible(true);
        }
    }
    @Override
    protected void specialFull() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.BLOCK_STONE_BREAK, 2f, 0.8f);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 10) {
                p.damage(7, boss);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1));
            }
        }
        cdFull = 15 * 20;
    }
    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.BLOCK_ANVIL_PLACE, 2f, 1f);

        // Gera blocos temporários
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    Location blockLoc = loc.clone().add(x, 0, z);
                    if (blockLoc.getBlock().getType() == Material.AIR) {
                        blockLoc.getBlock().setType(Material.BEDROCK);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (blockLoc.getBlock().getType() == Material.BEDROCK) {
                                    blockLoc.getBlock().setType(Material.AIR);
                                }
                            }
                        }.runTaskLater(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 200); // dura 10s
                    }
                }
            }
        }
        cdHalf = 20 * 20;
    }
    @Override
    protected void specialLow() {
        Location loc = boss.getLocation();
        boss.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, 2f, 1f);

        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= 20) {
                p.setVelocity(new Vector(0, 1.5, 0)); // joga para cima
                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0)); // náusea
            }
        }
        cdLow = 25 * 20;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragEarth.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
