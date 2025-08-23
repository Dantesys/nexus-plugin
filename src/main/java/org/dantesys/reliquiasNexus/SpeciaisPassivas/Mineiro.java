package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Mineiro {
    private static final Map<Material, Material> MIDAS_EVOLUTIONS = new HashMap<>() {{
        put(Material.COAL_ORE, Material.IRON_ORE);
        put(Material.IRON_ORE, Material.GOLD_ORE);
        put(Material.GOLD_ORE, Material.DIAMOND_ORE);
        put(Material.DIAMOND_ORE, Material.EMERALD_ORE);
        // Versões deepslate também
        put(Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE);
        put(Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE);
        put(Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_DIAMOND_ORE);
        put(Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE);
    }};
    public static void getPassivabyLevel(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,600,1));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,600,2));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,600,3));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            midas(level,player);
        }else if(level<16){//8-15
            collapse(level,player);
        }else{//16-20
            heartMountain(level,player);
        }
    }
    private static void midas(int level, Player player){
        Block target = player.getTargetBlockExact(3+level);
        if (target != null){
            if (MIDAS_EVOLUTIONS.containsKey(target.getType())) {
                Material upgraded = MIDAS_EVOLUTIONS.get(target.getType());
                target.setType(upgraded);
                player.getWorld().playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0.5, 0.5, 0.5), 20, 0.3, 0.3, 0.3, 0.01);
            }
        }
    }
    private static void collapse(int level, Player player){
        Location center = player.getLocation();
        double raio = 5+level;

        for (Entity e : center.getWorld().getNearbyEntities(center, raio, raio, raio)) {
            if (e instanceof LivingEntity && e != player) {
                e.setVelocity(new Vector(0, 1.0, 0));
                ((LivingEntity) e).damage(level, player);
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*level, level-10));
            }
        }
        center.getWorld().spawnParticle(Particle.BLOCK, center, 100, raio, 3, raio, Material.STONE.createBlockData());
        center.getWorld().playSound(center, Sound.BLOCK_STONE_BREAK, 1f, 0.8f);
    }
    private static void heartMountain(int level, Player player){
        World world = player.getWorld();
        Location loc = player.getLocation();
        double raioTerremoto = (double) level /2;
        double danoTerremoto = (double) level /3;
        for (Entity e : world.getNearbyEntities(loc, raioTerremoto, raioTerremoto, raioTerremoto)) {
            if (e instanceof LivingEntity && e != player) {
                ((LivingEntity) e).damage(danoTerremoto, player);
                e.setVelocity(new Vector(0, 1.0, 0)); // knockback para cima
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
            }
        }
        world.spawnParticle(Particle.BLOCK, loc, 100, raioTerremoto, 1, raioTerremoto, Material.STONE.createBlockData());
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
        Location frente = loc.clone().add(player.getLocation().getDirection().multiply(7));
        int tamanho = 5;
        int altura = 5;
        for (int x = -tamanho/2; x <= tamanho/2; x++) {
            for (int y = 0; y < altura; y++) {
                for (int z = 0; z < tamanho; z++) {
                    Block b = frente.clone().add(x, y, z).getBlock();
                    if (isStone(b.getType())) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10*level, 9));
    }
    private static boolean isOre(Material mat) {
        return switch(mat) {
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 IRON_ORE, DEEPSLATE_IRON_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 COPPER_ORE, DEEPSLATE_COPPER_ORE -> true;
            default -> false;
        };
    }
    private static boolean isStone(Material mat){
        return switch(mat){
            case STONE, DEEPSLATE, ANDESITE, TUFF, DIORITE, GRANITE, GRAVEL -> true;
            default -> false;
        };
    }
}
