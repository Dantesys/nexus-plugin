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
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mares {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            if(level<10){
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER,600,0));
            }else if(level<15){
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER,600,1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,600,0));
            }else if(level<20){
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER,600,2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,600,1));
            }else{
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER,600,3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,600,2));
            }
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            waterStrike(level,player);
        }else if(level<16){//11-15
            highTide(level,player);
        }else{//16-20
            oceanFury(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER,level*20,level-1));
        if(level>2){
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,level*20,level-2));
        }
    }
    private static void waterStrike(int level, Player player){
        final int finalRange = 10;
        final double finalDamage = level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.FALLING_WATER,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(finalDamage);
                        }
                    }else{
                        vivo.damage(finalDamage);
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                for(LivingEntity e: atingidos){
                    e.teleport(location);
                }
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void highTide(int level, Player player){
        final int finalRange = 5;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = level;
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
                world.spawnParticle(Particle.FALLING_WATER,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(damage);
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,level*20,2));
                        }
                    }else{
                        vivo.damage(damage);
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,level*20,2));
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void oceanFury(int level, Player player){
        final int finalRange = 50;
        final double damage = level;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.BUBBLE_POP,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player p){
                        if(p!=player){
                            vivo.setRemainingAir(0);
                            vivo.damage(damage);
                        }
                    }else{
                        vivo.setRemainingAir(0);
                        vivo.damage(damage);
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
}
