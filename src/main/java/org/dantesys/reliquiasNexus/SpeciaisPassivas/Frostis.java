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

public class Frostis {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            pilarCristalCongelante(level,player);
        }else if(level<16){//8-15
            coneGeloPrismatico(level,player);
        }else{//16-20
            tempestadeCristalGelado(level,player);
        }
    }
    private static void pilarCristalCongelante(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getTargetBlockExact(5).getLocation();
        int raio = 2 + level;
        double dano = 1 + 0.5 * level;

        // Temporizador para duração do pilar
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                () -> {}, // antes
                () -> {}, // depois
                t -> { // a cada tick
                    for(Entity e : world.getNearbyEntities(centro, raio, 2, raio)) {
                        if(e instanceof LivingEntity vivo && vivo != player) {
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                            vivo.setFreezeTicks(vivo.getFreezeTicks() + 20 * level);
                            vivo.damage(dano, player);
                        }
                    }
                    // Partículas do pilar
                    world.spawnParticle(Particle.SNOWFLAKE, centro.add(0.5,0.5,0.5), 5, 0.3, 0.3, 0.3);
                }).scheduleTimer(1L);
    }
    private static void coneGeloPrismatico(int level,Player player) {
        Location loc = player.getLocation();
        Vector dir = loc.getDirection().normalize();
        World world = loc.getWorld();
        double dano = 1 + 0.5 * level;

        for(int i=1; i<=5 + level; i++) {
            Location point = loc.clone().add(dir.clone().multiply(i));
            world.spawnParticle(Particle.CRIT, point, 2, 0.2, 0.2, 0.2);
            for(Entity e : world.getNearbyEntities(point, 1, 1, 1)) {
                if(e instanceof LivingEntity vivo && vivo != player) {
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 3));
                    vivo.setFreezeTicks(vivo.getFreezeTicks() + 10 * level);
                    vivo.damage(dano, player);
                }
            }
        }
    }
    private static void tempestadeCristalGelado(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        int raio = 5 + level;
        double dano = 1 + level;

        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                () -> {}, // antes
                () -> {}, // depois
                t -> {
                    for(Entity e : world.getNearbyEntities(centro, raio, raio, raio)) {
                        if(e instanceof LivingEntity vivo && vivo != player) {
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1));
                            vivo.setFreezeTicks(vivo.getFreezeTicks() + 20);
                            vivo.damage(dano, player);
                            world.spawnParticle(Particle.SNOWFLAKE, vivo.getLocation().add(0,1,0), 5, 0.3, 0.3, 0.3);
                        }
                    }
                }).scheduleTimer(1L);
    }

}
