package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Mineiro {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
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
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            prospectorEye(level,player);
        }else if(level<16){//11-15
            collapse(level,player);
        }else{//16-20
            heartMountain(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,level*20,level-1));
        if(level>2){
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,level*20,level-2));
        }
    }
    private static void prospectorEye(int level, Player player){
        int raio = 15+level;
        Location base = player.getLocation();
        Block target = null;
        double menorDist = Double.MAX_VALUE;
        for (int x = -raio; x <= raio; x++) {
            for (int y = -raio; y <= raio; y++) {
                for (int z = -raio; z <= raio; z++) {
                    Block b = base.clone().add(x, y, z).getBlock();
                    if (isOre(b.getType())) {
                        double dist = base.distance(b.getLocation());
                        b.getWorld().spawnParticle(Particle.END_ROD, b.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0);
                        if (dist < menorDist) {
                            menorDist = dist;
                            target = b;
                        }
                    }
                }
            }
        }

        if (target != null) {
            player.sendActionBar(Component.text("§bOre in " + (int) menorDist + " blocks!"));
            player.setCompassTarget(target.getLocation());
        } else {
            player.sendActionBar(Component.text("§7No Ore Found!"));
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
