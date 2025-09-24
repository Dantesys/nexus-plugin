package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

public class Dragao {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            soproDraconico(level,player);
        }else if(level<16){//8-15
            iraDraconica(level,player);
        }else{//16-20
            formaDraconicaUltimate(level,player);
        }
    }
    public static void soproDraconico(int level,Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();

        // Alcance e ângulo do cone variam conforme o level
        double alcance = 6 + (level * 2); // aumenta alcance com o level
        double anguloMax = Math.toRadians(30); // cone de 60° (30° pra cada lado)

        // Partículas
        for (double i = 1; i <= alcance; i += 0.5) {
            Location point = loc.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.DRAGON_BREATH, point, 5, 0.2, 0.2, 0.2, 0.01);
        }

        // Efeitos nos inimigos
        for (Entity e : player.getNearbyEntities(alcance, alcance, alcance)) {
            if (e instanceof LivingEntity entity && !(e instanceof Player)) {
                Vector toEntity = entity.getLocation().toVector().subtract(loc.toVector());
                double angle = direction.angle(toEntity.normalize());

                if (angle <= anguloMax) {
                    // Está dentro do cone
                    double dano = 4 + (level * 2); // escala com level
                    entity.damage(dano, player);

                    // Aplica efeito negativo
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));

                    // Partícula de impacto
                    entity.getWorld().spawnParticle(Particle.EXPLOSION, entity.getLocation().add(0, 1, 0), 10);
                }
            }
        }

        // Som de dragão
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2f, 1f);
    }
    public static void iraDraconica(int level, Player player) {
        World world = player.getWorld();

        // Explosão inicial de partículas
        world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 200, 2, 2, 2, 0.1);
        world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5f, 1f);
        world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 3f, 0.8f);

        // Buffs temporários (escala com o level)
        int duracao = 10 + level * 2;// 10s + 2s por level
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duracao, 1 + level/3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duracao, 1 + level/4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duracao, 0));

        // Aura de dano por alguns segundos
        Temporizador temporizador = new Temporizador(
                ReliquiasNexus.getPlugin(ReliquiasNexus.class),
                duracao,

                // Antes (rugido inicial)
                () -> {
                    world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 200, 2, 2, 2, 0.1);
                    world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5f, 1f);
                    world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 3f, 0.8f);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duracao * 20, 1 + level/3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duracao * 20, 1 + level/4));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duracao * 20, 0));
                },

                // Depois (explosão final)
                () -> {
                    world.spawnParticle(Particle.EXPLOSION, player.getLocation(), 3);
                    world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3f, 0.6f);
                },

                // A cada segundo (aura de fogo + dano em área)
                (timer) -> {
                    world.spawnParticle(Particle.FLAME, player.getLocation(), 30, 1.5, 1, 1.5, 0.05);

                    for (Entity e : player.getNearbyEntities(4, 4, 4)) {
                        if (e instanceof LivingEntity entity && !(e instanceof Player)) {
                            entity.damage(1.5 + (level * 0.5), player);
                            entity.setFireTicks(40);
                        }
                    }
                }
        );

        // Inicia o temporizador (20 ticks = 1s)
        temporizador.scheduleTimer(20L);
    }
    private static void formaDraconicaUltimate(int level,Player player) {
        World world = player.getWorld();

        int duracaoSegundos = 15; // duração fixa da ultimate
        double danoBase = 3 + level; // dano por impacto
        double empurrao = 1 + 0.1 * level; // força de knockback
        double velocidadeExtra = 0.2 + 0.02 * level; // boost de velocidade

        // Marca velocidade original
        double velocidadeOriginal = player.getWalkSpeed();
        player.setWalkSpeed((float) Math.min(velocidadeOriginal + velocidadeExtra, 1f));

        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), duracaoSegundos * 20,
                () -> {
                    world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2f, 1f);
                },
                () -> {
                    player.setWalkSpeed((float) velocidadeOriginal);
                },
                t -> {
                    // Empurra e danifica mobs próximos ao se mover
                    for (Entity e : player.getNearbyEntities(5, 3, 5)) {
                        if (e instanceof LivingEntity mob && mob != player) {
                            Vector direcao = mob.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                            mob.setVelocity(direcao.multiply(empurrao).setY(0.5));
                            mob.damage(danoBase, player);
                            mob.setFireTicks(40+level); // 2 segundos de fogo
                            world.spawnParticle(Particle.FLAME, mob.getLocation().add(0,1,0), 20, 0.5,0.5,0.5,0.1);
                        }
                    }

                    // Rajadas de partículas ao redor do jogador
                    world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0,1,0), 40, 1.5,1.5,1.5,0.15);
                }
        ).scheduleTimer(1L);
    }

}
