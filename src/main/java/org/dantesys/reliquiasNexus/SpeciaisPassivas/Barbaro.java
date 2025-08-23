package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Barbaro {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,0));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,1));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,2));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            warCry(level,player);
        }else if(level<16){//8-15
            spinningStrike(level,player);
        }else{//16-20
            berserk(level,player);
        }
    }
    private static void warCry(int level, Player player){
        final World world = player.getWorld();
        final double damage = (double) level /2;
        world.playSound(player,Sound.ENTITY_ENDER_DRAGON_GROWL,2f,0.8f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600+20*level,level));
        for(Entity e: player.getNearbyEntities(5,5,5)){
            if(e instanceof LivingEntity vivo){
                if(vivo instanceof Player pl){
                    if(pl != player){
                        vivo.damage(damage);
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,600+20*level,level-6));
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,600+20*level,level-6));
                    }
                }else{
                    vivo.damage(damage);
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,600+20*level,level-6));
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,600+20*level,level-6));
                }
            }
        }
    }
    private static void spinningStrike(int level, Player player){
        final int finalRange = 5;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = level*1.25;
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.SWEEP_ATTACK,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(damage);
                        }
                    }else{
                        vivo.damage(damage);
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void berserk(int level, Player player){
        player.setHealth(player.getHealth()/2);
        warCry(level,player);
        spinningStrike(level,player);
    }
}
