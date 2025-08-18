package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Vida {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,600,0));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            resistance(level,player);
        }else if(level<16){//11-15
            instaheal(level,player);
        }else if(level<21){//16-20
            imunity(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,600,level-1));
    }
    private static void resistance(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,20*level,level-1));
    }
    private static void instaheal(int level, Player player){
        double max = player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        player.setHealth(max);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,20*level,level-1));
    }
    private static void imunity(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,20*level,9));
    }
}
