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
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Collection;

public class Sculk {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
            player.setMetadata("wardenImunity", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            sonar(level,player);
        }else if(level<16){//11-15
            sonicboom(level,player);
        }else{//16-20
            ultimatesonicboom(level,player);
        }
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,level*20,0));
        player.setMetadata("wardenImunity", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
    }
    private static void sonar(int level, Player player){
        for(Entity e:player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20*level,0));
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20*level,level));
                vivo.damage((double) level /10);
                vivo.getWorld().playSound(vivo,Sound.ENTITY_WARDEN_EMERGE,1f,0.8f);
            }
        }
    }
    private static void sonicboom(int level, Player player){
        final int finalRange = 30+level;
        final double finalDamage = 10+level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.SONIC_BOOM,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo){
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
    private static void ultimatesonicboom(int level, Player player){
        World world = player.getWorld();
        Location center = player.getLocation();

        double raio = 5 + level;
        double dano = level * 0.75;

        // Som central
        world.playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 3f, 1f);

        // Efeito em área
        for (Entity e : player.getNearbyEntities(raio, raio, raio)) {
            if (e instanceof LivingEntity vivo && vivo != player) {
                vivo.damage(dano);
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*level, 1));
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*level, 0));
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20*level, 0));
            }
        }

        // Partícula gigante de expansão no chão (círculos concêntricos)
        int camadas = (int) (raio / 2);
        for (int i = 1; i <= camadas; i++) {
            double r = (double) i * 2; // espaçamento entre círculos
            int pontos = (int) (r * 8);
            for (int j = 0; j < pontos; j++) {
                double angulo = 2 * Math.PI * j / pontos;
                double x = r * Math.cos(angulo);
                double z = r * Math.sin(angulo);
                Location loc = center.clone().add(x, 0.1, z);
                world.spawnParticle(Particle.SONIC_BOOM, loc, 0, 0, 0, 0, 0);
            }
        }

        // Colunas sonoras aleatórias dentro da área
        int colunas = 8 + level / 2; // mais nível = mais colunas
        for (int i = 0; i < colunas; i++) {
            double x = (Math.random() - 0.5) * 2 * raio;
            double z = (Math.random() - 0.5) * 2 * raio;
            Location base = center.clone().add(x, 0, z);

            // Criar coluna de 4-6 blocos de altura
            int altura = 4 + (int) (Math.random() * 3);
            for (int y = 0; y < altura; y++) {
                Location colunaLoc = base.clone().add(0, y, 0);
                world.spawnParticle(Particle.SONIC_BOOM, colunaLoc, 0, 0, 0, 0, 0);
                world.spawnParticle(Particle.SCULK_SOUL, colunaLoc, 2, 0.2, 0.2, 0.2, 0.05);
                if (Math.random() < 0.2) { // chance de explosão
                    world.spawnParticle(Particle.EXPLOSION, colunaLoc, 1);
                }
            }
        }
    }
}
