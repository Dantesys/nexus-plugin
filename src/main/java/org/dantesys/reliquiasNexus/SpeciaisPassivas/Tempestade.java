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

import java.util.Collection;

public class Tempestade {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            if(level<10){
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,600,0));
            }else if(level<15){
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,600,1));
            }else if(level<20){
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,600,2));
            }else{
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,600,3));
            }
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            electricWhip(level,player);
        }else if(level<16){//11-15
            electricExplosion(level,player);
        }else{//16-20
            tempest(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,level*20,level-1));
    }
    private static void electricWhip(int level, Player player){
        final int finalRange = 5+level;
        final double finalDamage = level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo){
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(finalDamage);
                            vivo.getWorld().strikeLightningEffect(vivo.getLocation());
                            t.stop();
                        }
                    }else{
                        vivo.damage(finalDamage);
                        vivo.getWorld().strikeLightningEffect(vivo.getLocation());
                        t.stop();
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                location.getWorld().strikeLightningEffect(location);
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void electricExplosion(int level, Player player){
        final int finalRange = 5+level;
        final double finalDamage = level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo){
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(finalDamage);
                            vivo.getWorld().strikeLightningEffect(vivo.getLocation());
                            vivo.getWorld().createExplosion(vivo, (float) finalDamage,false,false);
                            t.stop();
                        }
                    }else{
                        vivo.damage(finalDamage);
                        vivo.getWorld().strikeLightningEffect(vivo.getLocation());
                        vivo.getWorld().createExplosion(vivo, (float) finalDamage,false,false);
                        t.stop();
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                location.getWorld().strikeLightningEffect(location);
                location.getWorld().createExplosion(location, (float) finalDamage,false,false);
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void tempest(int level, Player player){
        World w = player.getWorld();
        w.setStorm(true);
        w.setThundering(true);
        final int finalRange = level;
        final double damage = level;
        final World world = player.getWorld();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            for(Entity e: player.getNearbyEntities(finalRange,finalRange,finalRange)){
                if(e instanceof LivingEntity vivo){
                    vivo.damage(damage);
                    world.strikeLightningEffect(vivo.getLocation());
                }
            }
        });
        timer.scheduleTimer(20L);
    }
}
