package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Ladrao {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600,0));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            smokebomb(level,player);
        }else if(level<16){//8-15
            gravity(level,player);
        }else{//16-20
            fuga(level,player);
        }
    }
    private static void smokebomb(int level, Player player){
        for(Entity e: player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                if(vivo instanceof Player pl){
                    if(pl != player){
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,600+20*level,level));
                    }
                }else{
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,600+20*level,level));
                }
            }
        }
    }
    private static void gravity(int level, Player player){
        for(Entity e: player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                if(vivo instanceof Player pl){
                    if(pl != player){
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,600+20*level,level));
                    }
                }else{
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,600+20*level,level));
                }
            }
        }
    }
    private static void fuga(int level, Player player){
        Location loc = player.getRespawnLocation();
        if(loc==null)loc=player.getWorld().getSpawnLocation();
        player.heal(level);
        player.teleport(loc);
    }
}
