package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.HashSet;
import java.util.Set;

public class Cronosombra {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            sombraCongelante(level,player);
        }else if(level<16){//8-15
            eclipseTemporal(level,player);
        }else{//16-20
            paradoxoTemporal(level,player);
        }
    }
    private static void sombraCongelante(int level, Player player){
        World world = player.getWorld();
        Location centro = player.getLocation();
        double raio = 3 + level; // raio aumenta com o nível
        double danoMax = 2 + level * 0.5; // dano máximo no centro
        double lentidaoMax = 60 + level * 5; // duração máxima em ticks

        // percorre blocos/posições dentro do círculo
        for (Entity entity : world.getNearbyEntities(centro, raio, 2, raio)) {
            if (entity instanceof LivingEntity living && living != player) {
                // distância do centro
                double distancia = living.getLocation().distance(centro);
                double fator = 1 - (distancia / raio); // decresce com a distância

                // aplica dano proporcional
                double dano = danoMax * fator;
                living.damage(dano, player);

                // aplica lentidão proporcional
                int duracao = (int)(lentidaoMax * fator);
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duracao, 1));

                // partículas visuais
                world.spawnParticle(Particle.SMOKE, living.getLocation().add(0,1,0),
                        5, 0.3, 0.3, 0.3, 0);
            }
        }

        // partículas no centro
        for (int i = 0; i < 20; i++) {
            Location particleLoc = centro.clone().add(
                    Math.random() * raio - raio/2,
                    1,
                    Math.random() * raio - raio/2
            );
            world.spawnParticle(Particle.PORTAL, particleLoc, 1, 0, 0, 0, 0);
        }
    }
    private static void eclipseTemporal(int level, Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        double raio = 3 + level / 3.0;
        int duracao = 60 + level * 4; // duração em ticks

        for(Entity entity : world.getNearbyEntities(centro, raio, raio, raio)){
            if(entity instanceof Player p && p != player){
                // inimigos
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duracao, level / 5));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duracao / 2, 0));
            }else if(entity instanceof LivingEntity p){
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duracao, level / 5));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duracao / 2, 0));
            }
        }

        // partículas visuais da bolha
        for(int i=0; i<20; i++){
            Location particleLoc = centro.clone().add(
                    Math.random()*raio - raio/2,
                    1,
                    Math.random()*raio - raio/2
            );
            world.spawnParticle(Particle.PORTAL, particleLoc, 1, 0,0,0,0);
        }
    }
    private static void paradoxoTemporal(int level, Player player) {
        World world = player.getWorld();
        double raio = 5 + level; // aumenta com o level
        int duracao = 40 + level * 10; // em ticks, 20 ticks = 1s

        Set<LivingEntity> congelados = new HashSet<>();

        // marca todas as entidades no raio
        for(Entity entity : world.getNearbyEntities(player.getLocation(), raio, raio, raio)) {
            if(entity instanceof LivingEntity living && living != player){
                congelados.add(living);
            }
        }

        // Temporizador para manter as entidades paradas
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), duracao, () -> {}, () -> {
            // Ao finalizar, remove restrições
            // Pode adicionar um pequeno efeito de knockback ou dano final se quiser
        }, t -> {
            for(LivingEntity living : congelados){
                // força o movimento para 0
                living.setVelocity(new Vector(0,0,0));
                // partículas ao redor
                world.spawnParticle(Particle.LARGE_SMOKE, living.getLocation().add(0,1,0), 3, 0.2,0.2,0.2,0);
            }
        }).scheduleTimer(1L);
    }
}
