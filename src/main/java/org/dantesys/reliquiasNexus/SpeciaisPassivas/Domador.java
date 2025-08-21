package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class Domador {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            lobinho(level,player);
        }else if(level<11){//6-10
            lobo(level,player);
        }else if(level<16){//11-15
            alcateia(level,player);
        }else{//16-20
            alcateiaOmega(level,player);
        }
    }
    private static void lobinho(int level, Player player){
        Location loc = player.getLocation();
        Wolf wolf = player.getWorld().spawn(loc,Wolf.class);
        wolf.setOwner(player);
        wolf.getAttribute(Attribute.MAX_HEALTH).setBaseValue(level);
        wolf.getAttribute(Attribute.SCALE).setBaseValue(0.5);
    }
    private static void lobo(int level, Player player){
        Location loc = player.getLocation();
        Wolf wolf = player.getWorld().spawn(loc,Wolf.class);
        wolf.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(level);
        wolf.setOwner(player);
    }
    private static void alcateia(int level, Player player){
        Location loc = player.getLocation();
        for(int i=0;i<level/2;i++){
            Wolf wolf = player.getWorld().spawn(loc,Wolf.class);
            wolf.setOwner(player);
        }
    }
    private static void alcateiaOmega(int level, Player player){
        Location loc = player.getLocation();
        Wolf wolf = player.getWorld().spawn(loc,Wolf.class);
        wolf.setOwner(player);
        wolf.getAttribute(Attribute.ARMOR).setBaseValue(level);
        wolf.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(level);
        wolf.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(level);
        wolf.getAttribute(Attribute.MAX_HEALTH).setBaseValue(level);
        wolf.getAttribute(Attribute.SCALE).setBaseValue(1.25);
        for(int i=0;i<level/2;i++){
            Wolf wolf2 = player.getWorld().spawn(loc,Wolf.class);
            wolf2.setOwner(player);
        }
    }
}
