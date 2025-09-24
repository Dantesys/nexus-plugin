package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Vida {
    public static void getPassivabyLevel(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,600,level));
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            resistance(level,player);
        }else if(level<16){//-15
            instaheal(level,player);
        }else{//16-20
            imunity(level,player);
        }
    }
    private static void resistance(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600+20*level,level-1));
    }
    private static void instaheal(int level, Player player){
        double max = player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        player.setHealth(max);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,600+20*level,level-1));
    }
    private static void imunity(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600+20*level,9));
    }
}
