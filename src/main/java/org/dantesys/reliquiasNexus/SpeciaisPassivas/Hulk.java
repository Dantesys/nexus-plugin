package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public class Hulk {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,0));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,1));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,2));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            colossalJump(level,player);
        }else if(level<16){//8-15
            crackedEarth(level,player);
        }else{//16-20
            smash(level,player);
        }
    }
    private static void colossalJump(int level, Player player){
        player.setVelocity(player.getVelocity().setY(1.5+((double) level/10)));
        player.setMetadata("saltoColossal", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
    }
    private static void crackedEarth(int level, Player player){
        Vector direcao = player.getLocation().getDirection().normalize();
        Location inicio = player.getLocation().clone().add(0, 0.5, 0);
        double alcance = (double)level/2;
        double raioImpacto = 2.0;
        for (double i = 1; i <= alcance; i += 0.5) {
            Location pos = inicio.clone().add(direcao.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.BLOCK, pos, 5, 0.2, 0.1, 0.2, Material.DIRT.createBlockData());
            player.getWorld().playSound(pos, Sound.BLOCK_GRAVEL_STEP, 1f, 1f);
            for (Entity entity : pos.getNearbyEntities(raioImpacto, 1.5, raioImpacto)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(4.0, player); // dano base
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600+20*level, 1));
                    entity.setVelocity(entity.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .normalize().multiply(1.0).setY(0.5));
                }
            }
        }
    }
    private static void smash(int level, Player player){
        player.setVelocity(player.getVelocity().setY(2.5 + level * 0.1));
        player.setMetadata("hulkUltimate", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
    }
}
