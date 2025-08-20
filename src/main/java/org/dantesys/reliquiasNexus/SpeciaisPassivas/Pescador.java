package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Pescador {
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,600,0));
            player.setMetadata("arpao", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<6){//1-5
            buff(level,player);
        }else if(level<11){//6-10
            fishrain(level,player);
        }else if(level<16){//11-15
            roughSea(level,player);
        }else{//16-20
            fishnado(level,player);
        }
    }
    private static EntityType peixe(){
        List<EntityType> m = List.of(
                EntityType.SQUID,
                EntityType.COD,
                EntityType.DOLPHIN,
                EntityType.PUFFERFISH,
                EntityType.SALMON,
                EntityType.TROPICAL_FISH,
                EntityType.AXOLOTL,
                EntityType.GLOW_SQUID,
                EntityType.TADPOLE,
                EntityType.TURTLE
        );
        return m.get(ThreadLocalRandom.current().nextInt(m.size()));
    }
    private static void buff(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,level*20,0));
        player.setMetadata("arpao", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
    }
    private static void fishrain(int level, Player player){
        Location centro = player.getLocation();
        World mundo = player.getWorld();

        int quantidade = 3 + (level * 2);

        for (int i = 0; i < quantidade; i++) {
            // escolher uma criatura marinha aleatória
            EntityType tipo = peixe();

            // spawnar acima do player em posição aleatória
            Location spawnLoc = centro.clone().add(
                    (Math.random() * 8) - 4, 8 + Math.random() * 3, (Math.random() * 8) - 4
            );

            LivingEntity mob = (LivingEntity) mundo.spawnEntity(spawnLoc, tipo);

            // jogar o mob para baixo
            mob.setVelocity(new Vector(0, -1 - Math.random(), 0));

            // quando cair no chão ou bater em alguém → dano
            Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 20,
                    ()->{
                    },()-> {
            },(t)->{
                if (!mob.isValid() || mob.isOnGround()) {
                    for (Entity e : mob.getNearbyEntities(1.5, 1.5, 1.5)) {
                        if (e instanceof LivingEntity alvo && alvo != player) {
                            alvo.damage(level * 1.5, mob);
                            alvo.getWorld().playSound(alvo.getLocation(),
                                    Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 1f);

                            // efeitos especiais
                            if (mob.getType() == EntityType.PUFFERFISH) {
                                alvo.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
                            } else if (mob.getType() == EntityType.SQUID) {
                                alvo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                            }
                        }
                    }
                    mob.remove(); // some depois do impacto
                    t.stop();
                }
            });
            timer.scheduleTimer(2L);
        }

        mundo.playSound(centro, Sound.WEATHER_RAIN_ABOVE, 1.5f, 0.9f);
        mundo.playSound(centro, Sound.ENTITY_DOLPHIN_SPLASH, 1.5f, 1.3f);
    }
    private static void roughSea(int level, Player player){
        double alcance = 3 + level; // raio da onda aumenta com o level
        double forca = 1 + (level * 0.2); // forca do knockback

        Location centro = player.getLocation();
        World mundo = player.getWorld();

        // aplicar efeito nos mobs próximos
        for (Entity e : player.getNearbyEntities(alcance, alcance, alcance)) {
            if (e instanceof LivingEntity alvo && alvo != player) {
                // empurra para fora do centro
                Vector direcao = alvo.getLocation().toVector().subtract(centro.toVector()).normalize().multiply(forca);
                alvo.setVelocity(direcao);

                // aplicar efeitos de Slowness e Weakness
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40 + level * 10, 0));
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40 + level * 10, 0));

                // som do impacto
                alvo.getWorld().playSound(alvo.getLocation(), Sound.ENTITY_DOLPHIN_SPLASH, 1f, 1f);
            }
        }

        // partículas circulares
        int particulas = 20 + level * 5;
        for (int i = 0; i < particulas; i++) {
            double angulo = 2 * Math.PI * i / particulas;
            double x = Math.cos(angulo) * alcance;
            double z = Math.sin(angulo) * alcance;
            Location particulaLoc = centro.clone().add(x, 0.5, z);
            mundo.spawnParticle(Particle.SPLASH, particulaLoc, 1, 0, 0, 0, 0);
        }

        // som geral da onda
        mundo.playSound(centro, Sound.ENTITY_GENERIC_SPLASH, 1.2f, 1f);
    }
    private static void fishnado(int level, Player player){
        World mundo = player.getWorld();
        Location centro = player.getLocation();
        int quantidade = 10 + level * 2;
        int duracao = 100; // duração em ticks (~5 segundos)
        double raio = 3 + level * 0.5;

        List<LivingEntity> criaturas = new ArrayList<>();

        // spawn inicial das criaturas ao redor do jogador
        for (int i = 0; i < quantidade; i++) {
            EntityType tipo = peixe();
            double angulo = 2 * Math.PI * i / quantidade;
            // spawn em posição elevada para dar efeito de "subir"
            Location spawnLoc = centro.clone().add(Math.cos(angulo) * raio, 2 + Math.random() * 3, Math.sin(angulo) * raio);
            LivingEntity mob = (LivingEntity) mundo.spawnEntity(spawnLoc, tipo);
            criaturas.add(mob);
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > duracao) {
                    criaturas.forEach(Entity::remove);
                    cancel();
                    return;
                }

                for (int i = 0; i < criaturas.size(); i++) {
                    LivingEntity mob = criaturas.get(i);
                    if (!mob.isValid()) continue;

                    // movimento circular + subida e queda
                    double anguloAtual = 2 * Math.PI * i / quantidade + Math.toRadians(ticks * 10);
                    double x = Math.cos(anguloAtual) * raio;
                    double z = Math.sin(anguloAtual) * raio;
                    double y = 2 + Math.sin(ticks * 0.2) * 3; // sobe e desce
                    Location novaPos = centro.clone().add(x, y, z);
                    Vector direcao = novaPos.toVector().subtract(mob.getLocation().toVector()).multiply(0.3);
                    mob.setVelocity(direcao);

                    // partículas de água
                    mob.getWorld().spawnParticle(Particle.SPLASH, mob.getLocation(), 1, 0, 0, 0, 0);

                    // checar mobs próximos
                    for (Entity e : mob.getNearbyEntities(1.5, 1.5, 1.5)) {
                        if (e instanceof LivingEntity alvo && alvo != player) {
                            alvo.damage(level * 1.5, player);

                            // efeitos especiais por tipo
                            switch (mob.getType()) {
                                case PUFFERFISH -> alvo.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
                                case SQUID, GLOW_SQUID -> alvo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                                case AXOLOTL -> alvo.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0));
                                case TURTLE -> alvo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1));
                            }

                            // som de splash
                            alvo.getWorld().playSound(alvo.getLocation(), Sound.ENTITY_DOLPHIN_SPLASH, 1f, 1f);
                        }
                    }
                }

                // som geral do tornado
                mundo.playSound(centro, Sound.ENTITY_GENERIC_SPLASH, 1.2f, 1f);
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 2L);
    }
}
