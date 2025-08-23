package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Flash {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,1));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,2));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,3));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            dash(level,player);
        }else if(level<16){//8-15
            flashStep(level,player);
        }else{//16-20
            tornado(level,player);
        }
    }
    private static void dash(int level, Player player){
        final int finalRange = 5+level;
        final double finalDamage = level/2d;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> player.teleport(location),(t)->{
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
    private static void flashStep(int level, Player player){
        Location origem = player.getLocation();
        Vector direcao = player.getLocation().getDirection().normalize();
        double distancia = 5 + level; // distância aumenta com level
        Location destino = origem.clone().add(direcao.multiply(distancia));
        player.teleport(destino);
        for (Entity e : origem.getNearbyEntities(3, 3, 3)) { // raio da mini-explosão
            if (e instanceof LivingEntity alvo && alvo != player) {
                alvo.damage(level); // dano leve
                alvo.setVelocity(alvo.getLocation().toVector().subtract(origem.toVector()).normalize().multiply(0.5)); // knockback
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0));
            }
        }
        // partículas e som
        player.getWorld().spawnParticle(Particle.CLOUD, origem, 10, 1, 0.5, 1, 0);
        player.getWorld().playSound(origem, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
    }
    private static void tornado(int level, Player player){
        Location centro = player.getLocation();
        int duracao = level * 20; // ticks (~3-5s)
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                ticks++;
                if (ticks > duracao) {
                    // quando acabar, executar explosão final
                    tornadoFinal(centro, player, level,level);
                    cancel();
                    return;
                }

                // Atração de mobs e partículas
                for (Entity e : centro.getNearbyEntities(level, level, level)) {
                    if (e instanceof LivingEntity alvo && alvo != player) {
                        // vetor para o centro
                        Vector direcao = centro.toVector().subtract(alvo.getLocation().toVector()).normalize();
                        alvo.setVelocity(direcao.multiply(0.3)); // força de atração

                        // dano periódico leve
                        if (ticks % 20 == 0) { // a cada 1 segundo
                            alvo.damage(level * 1.5, player);
                        }

                        // partículas de redemoinho
                        alvo.getWorld().spawnParticle(Particle.SWEEP_ATTACK, alvo.getLocation(), 1, 0, 0, 0, 0);
                    }
                }

                // partículas ao redor do jogador
                centro.getWorld().spawnParticle(Particle.CLOUD, centro, 5, (double) level /2, 1, (double) level /2, 0);
                centro.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, centro, 3, (double) level /2, 1, (double) level /2, 0);

                // som contínuo de vento
                if (ticks % 10 == 0) {
                    centro.getWorld().playSound(centro, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);
                }
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 2L);
    }
    private static void tornadoFinal(Location centro, Player player, int level, int raio){
        for (Entity e : centro.getNearbyEntities(raio*1.2, raio*1.2, raio*1.2)) {
            if (e instanceof LivingEntity alvo && alvo != player) {
                Vector knockback = alvo.getLocation().toVector().subtract(centro.toVector()).normalize().multiply(1.5);
                alvo.setVelocity(knockback);
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 0));
                alvo.damage(level * 3.0, player);
            }
        }
        centro.getWorld().spawnParticle(Particle.EXPLOSION, centro, 10, raio, 1, raio, 0.1);
        centro.getWorld().playSound(centro, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
    }
}
