package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class Arqueiro {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            if(level<10){
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
            }else{
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600,0));
            }
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            explosive(level,player);
        }else if(level<16){//11-15
            toxic(level,player);
        }else{//16-20
            supersonicArrow(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,level*20,level-1));
        if(level>2){
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,level*20,level-2));
        }
    }
    private static void explosive(int level, Player player){
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.YELLOW);
        arrow.setMetadata(SPECIAL.key.getKey(), new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class),level));
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(level));
    }
    private static void toxic(int level, Player player){
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.BLACK);
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON,20*level,level),true);
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS,20*level,level),true);
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(level/2));
    }
    private static void supersonicArrow(int level, Player player){
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.YELLOW);
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(level*2));
    }
}
