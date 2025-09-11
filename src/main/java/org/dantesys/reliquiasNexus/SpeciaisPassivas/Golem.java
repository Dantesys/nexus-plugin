package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Golem {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            spawnNanoGolems(level,player);
        }else if(level<16){//8-15
            spawnMiniGolems(level,player);
        }else{//16-20
            spawnGolems(level,player);
        }
    }
    public static void spawnNanoGolems(int level,Player player) {
        World world = player.getWorld();
        int amplifier = Math.min(level - 1, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,200+level*20,amplifier));
        for (int i = 0; i < 4; i++) {
            IronGolem golem = (IronGolem) world.spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
            // Nome
            golem.customName(Component.text("§eSegurança"));
            golem.setCustomNameVisible(true);
            // Atributos reduzidos
            golem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(25); // Vida reduzida
            golem.setHealth(25);
            golem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(3); // Dano reduzido
            golem.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(1);
            golem.getAttribute(Attribute.MOVEMENT_EFFICIENCY).setBaseValue(1);
            golem.getAttribute(Attribute.SCALE).setBaseValue(0.25);
            golem.setRemoveWhenFarAway(false); // Não some longe
            golem.setPlayerCreated(true);
        }
    }
    public static void spawnMiniGolems(int level,Player player) {
        World world = player.getWorld();
        int amplifier = Math.min(level - 1, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,200+level*20,amplifier));
        for (int i = 0; i < 2; i++) {
            IronGolem golem = (IronGolem) world.spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
            // Nome
            golem.customName(Component.text("§eSegurança"));
            golem.setCustomNameVisible(true);
            // Atributos reduzidos
            golem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50); // Vida reduzida
            golem.setHealth(50);
            golem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(6); // Dano reduzido
            golem.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.75);
            golem.getAttribute(Attribute.MOVEMENT_EFFICIENCY).setBaseValue(1);
            golem.getAttribute(Attribute.SCALE).setBaseValue(0.5);
            golem.setRemoveWhenFarAway(false); // Não some longe
            golem.setPlayerCreated(true);
        }
    }
    public static void spawnGolems(int level,Player player) {
        World world = player.getWorld();
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,200+level*20,0));
        spawnNanoGolems(level,player);
        spawnMiniGolems(level,player);
        IronGolem golem = (IronGolem) world.spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
        // Nome
        golem.customName(Component.text("§eSegurança"));
        golem.setCustomNameVisible(true);
        // Atributos reduzidos
        golem.getAttribute(Attribute.MAX_HEALTH).setBaseValue(75); // Vida reduzida
        golem.setHealth(75);
        golem.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(9); // Dano reduzido
        golem.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
        golem.getAttribute(Attribute.MOVEMENT_EFFICIENCY).setBaseValue(1);
        golem.getAttribute(Attribute.SCALE).setBaseValue(0.75);
        golem.setRemoveWhenFarAway(false); // Não some longe
        golem.setPlayerCreated(true);
    }
}
