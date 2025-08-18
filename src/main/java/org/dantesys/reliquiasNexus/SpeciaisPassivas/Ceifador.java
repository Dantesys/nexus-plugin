package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.dantesys.reliquiasNexus.util.NexusKeys.DRENO;

public class Ceifador {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600,0));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            deathfear(level,player);
        }else if(level<16){//11-15
            nigthslash(level,player);
        }else{//16-20
            soulcolector(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.getPersistentDataContainer().set(DRENO.key, PersistentDataType.INTEGER,5+level);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,100+(level*20),0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,100+(level*20),0));
    }
    private static void deathfear(int level, Player player){
        final int finalRange = 5+level;
        final Location location = player.getLocation();
        final World world = player.getWorld();
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
                world.spawnParticle(Particle.GLOW_SQUID_INK,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20*level,2));
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*level,level));
                        }
                    }else{
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,20*level,2));
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*level,level));
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void nigthslash(int level, Player player){
        final int finalRange = 15+level;
        final double finalDamage = level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 5,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.SWEEP_ATTACK,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    player.heal(finalDamage);
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
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private static void soulcolector(int level, Player player){
        final int finalRange = 5;
        final Location location = player.getLocation();
        final World world = player.getWorld();
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
                world.spawnParticle(Particle.GLOW_SQUID_INK,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    double vida = vivo.getHealth()*2;
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            if(vida<level){
                                Location ld = vivo.getLocation();
                                ItemStack stack = new ItemStack(Material.TOTEM_OF_UNDYING);
                                ItemMeta meta = stack.getItemMeta();
                                meta.displayName(Component.text(vivo.getName()));
                                World wd = vivo.getWorld();
                                wd.dropItemNaturally(ld,stack);
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(level);
                                player.heal(level);
                            }
                        }
                    }else{
                        if(vida<level){
                            Location ld = vivo.getLocation();
                            ItemStack stack = new ItemStack(Material.TOTEM_OF_UNDYING);
                            ItemMeta meta = stack.getItemMeta();
                            meta.displayName(Component.text(vivo.getName()));
                            World wd = vivo.getWorld();
                            wd.dropItemNaturally(ld,stack);
                            vivo.setHealth(0);
                        }else{
                            vivo.damage(level);
                            player.heal(level);
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
}
