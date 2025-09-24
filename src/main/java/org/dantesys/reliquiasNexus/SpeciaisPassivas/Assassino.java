package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.eventos.PassivaEvent;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

public class Assassino {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            dashVenenoso(level,player);
        }else if(level<16){//8-15
            execucaoSilenciosa(level,player);
        }else{//16-20
            trueInvisibility(level,player);
        }
    }
    private static void dashVenenoso(int level, Player player){
        final int finalRange = 5;
        final double finalDamage = level;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        Location destino = player.getLocation().add(direction.multiply(5));
        destino.setYaw(player.getLocation().getYaw());
        destino.setPitch(player.getLocation().getPitch());
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
                ()->{
                },()-> player.teleport(destino),(t)->{
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
                            vivo.damage(finalDamage,player);
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.POISON,200,level));
                        }
                    }else{
                        vivo.damage(finalDamage,player);
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.POISON,200,level));
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
    private static void execucaoSilenciosa(int level, Player player) {
        // raio de busca do alvo
        double maxDistance = 15;
        Entity target = player.getTargetEntity((int) maxDistance, false);

        if (target instanceof LivingEntity vivo) {
            // Pega a posição atrás do alvo
            Location behind = vivo.getLocation().clone();
            Vector dir = vivo.getLocation().getDirection().normalize();
            behind.subtract(dir.multiply(1.5)); // 1.5 blocos atrás
            behind.setDirection(player.getLocation().getDirection()); // mantém visão do player

            // Teleporta o player
            player.teleport(behind);

            // Aplica efeitos no alvo
            int duration = 20 * level; // 20 ticks * nível
            vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 4)); // Slowness V
            vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0)); // Cegueira I

            // Partículas/Sons para impacto
            vivo.getWorld().playSound(vivo.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            vivo.getWorld().spawnParticle(Particle.LARGE_SMOKE, vivo.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);

        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * level, 4));
        }
    }
    private static void trueInvisibility(int level, Player player) {
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 30,
                () -> PassivaEvent.setAssassino(player.getUniqueId()),
                PassivaEvent::removerAssassino,
                t -> {
                    for(Entity e : player.getNearbyEntities(level,2,level)){
                        if(e instanceof LivingEntity vivo && vivo!=player){
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20*level,0));
                            vivo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20*level,0));
                        }
                    }
                }
        ).scheduleTimer(1L);
    }
}
