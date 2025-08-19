package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static org.dantesys.reliquiasNexus.util.NexusKeys.RENASCER;

public class Fenix {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            if(level<10){
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,0));
            }else if(level<15){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,1));
            }else if(level<20){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,2));
            }else{
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,3));
            }
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            auroraExplosion(level,player);
        }else if(level<16){//11-15
            risingFlames(level,player);
        }else{//16-20
            supernova(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,level*20,level-1));
        if(level>2){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,level*20,level-2));
        }
    }
    private static void auroraExplosion(int level, Player player){
        player.spawnParticle(Particle.FLAME,player.getLocation(),level);
        for(Entity e: player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                vivo.damage(level);
                vivo.setFireTicks(20*level);
                vivo.getWorld().spawnParticle(Particle.FLAME,vivo.getLocation(),level);
            }
        }
    }
    private static void risingFlames(int level, Player player){
        auroraExplosion(level,player);
        Vector impulso = player.getLocation().getDirection().multiply(0);
        impulso.setY(1.2 + (level * 0.1));
        player.setVelocity(impulso);
    }
    private static void supernova(int level, Player player){
        player.spawnParticle(Particle.FLAME,player.getLocation(),level);
        for(Entity e: player.getNearbyEntities(20,20,20)){
            if(e instanceof LivingEntity vivo){
                vivo.damage((double) level /2);
                vivo.setFireTicks(20*level);
                vivo.getWorld().spawnParticle(Particle.FLAME,vivo.getLocation(),level);
            }
        }
        player.getWorld().createExplosion(player,level,true,false);
        player.getPersistentDataContainer().set(RENASCER.key, PersistentDataType.INTEGER,120);
        player.getAttribute(Attribute.SCALE).setBaseValue(0.5);
    }
}
