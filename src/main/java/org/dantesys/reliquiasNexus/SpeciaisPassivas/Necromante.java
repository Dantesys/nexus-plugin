package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.List;

import static org.dantesys.reliquiasNexus.util.NexusKeys.SLAVE;

public class Necromante {
    private static EntityType sortearEsqueleto() {
        double r = Math.random() * 100.0; // valor entre 0 e 100
        if (r < 50) {
            return EntityType.SKELETON; // 50%
        } else if (r < 70) {
            return EntityType.STRAY; // 20%
        } else if (r < 74) {
            return EntityType.WITHER_SKELETON; // 4%
        } else if (r < 99) {
            return EntityType.BOGGED; // 20%
        } else {
            return EntityType.WITHER; // 1%
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            chamadaDasSombras(level,player);
        }else if(level<16){//8-15
            cemiterio(level,player);
        }else{//16-20
            exercitoDoSubmundo(level,player);
        }
    }
    public static void chamadaDasSombras(int level,Player player) {
        World world = player.getWorld();

        // Raio para buscar esqueletos slaves
        double raio = 50.0;

        // Pega todos os esqueletos no raio
        List<LivingEntity> esqueletos = world.getNearbyEntities(player.getLocation(), raio, raio, raio).stream()
                .filter(ent -> ent instanceof LivingEntity)
                .map(ent -> (LivingEntity) ent)
                .filter(ent -> ent.getPersistentDataContainer().has(SLAVE.key)) // só os marcados como slaves
                .toList();

        if (esqueletos.isEmpty()) {
            player.sendMessage("§8[§5Necromante§8] §7As sombras estão vazias...");
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,20*level+200,0));
            return;
        }

        // Teleporta cada um para perto do jogador
        for (LivingEntity mob : esqueletos) {
            Location tp = player.getLocation().clone().add(
                    (Math.random() - 0.5) * 2, // deslocamento X aleatório
                    0,
                    (Math.random() - 0.5) * 2  // deslocamento Z aleatório
            );

            mob.teleport(tp);

            // Aplica buffs sombrios
            mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0)); // 10s
            mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0)); // 5s

            // Partículas sombrias na chegada
            world.spawnParticle(Particle.LARGE_SMOKE, tp, 20, 0.5, 0.5, 0.5, 0.01);
            world.spawnParticle(Particle.SOUL, tp, 15, 0.3, 0.5, 0.3, 0.02);
        }

        // Efeito sonoro do teleporte
        world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);

        player.sendMessage("§8[§5Necromante§8] §7Seu exército respondeu ao chamado das sombras!");
    }
    private static void cemiterio(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        double raio = 5.0; // raio do círculo
        int duracaoSegundos = 10+level;
        int maxEsqueletos = 15;
        int[] count = {0};

        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), duracaoSegundos,
                () -> {}, // antes: nada
                () -> {}, // depois: nada
                (t) -> {
                    if(count[0] >= maxEsqueletos){
                        t.stop();
                        return;
                    }

                    // Gera uma posição aleatória dentro do círculo
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * raio;
                    Location spawnLoc = centro.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);

                    // Spawna o esqueleto
                    LivingEntity esqueleto = (LivingEntity) world.spawnEntity(spawnLoc, sortearEsqueleto());

                    // Marca como slave do necromante
                    esqueleto.getPersistentDataContainer().set(SLAVE.key, PersistentDataType.INTEGER, 1);
                    if (esqueleto.getType() == EntityType.WITHER) {
                        esqueleto.customName(Component.text("Mini-Wither"));
                        esqueleto.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
                        esqueleto.setHealth(50);
                        esqueleto.getAttribute(Attribute.SCALE).setBaseValue(0.5);
                        esqueleto.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5); // mais fraco
                    }
                    // Partículas sombrias
                    world.spawnParticle(Particle.LARGE_SMOKE, spawnLoc, 15, 0.5, 0.5, 0.5, 0.02);
                    world.spawnParticle(Particle.SOUL, spawnLoc, 10, 0.3, 0.3, 0.3, 0.02);

                    // Som de invocação
                    world.playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1f, 1f);

                    count[0]++;
                }
        ).scheduleTimer(2L); // executa a cada 2 ticks
    }
    private static void exercitoDoSubmundo(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        int raio = 10 + level; // raio aumenta com level
        int maxEsqueletos = 5 + level * 2; // mais esqueletos com level
        List<Entity> spawned = new ArrayList<>();

        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 5, // duração em segundos
                () -> {}, // antes: nada
                () -> {}, // depois: nada
                (t) -> {
                    if(spawned.size() >= maxEsqueletos){
                        t.stop();
                        return;
                    }

                    // posição aleatória dentro do círculo
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.random() * raio;
                    Location spawnLoc = centro.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);

                    // faz o esqueleto "surgir do chão"
                    spawnLoc.add(0, -1, 0);

                    // gera o esqueleto usando a função que você já tem
                    LivingEntity esqueleto = (LivingEntity) world.spawnEntity(spawnLoc, sortearEsqueleto());

                    // marca como slave
                    esqueleto.getPersistentDataContainer().set(SLAVE.key, PersistentDataType.INTEGER, 1);

                    // teleporta para o spawn no chão e aplica armadura
                    esqueleto.teleport(spawnLoc);
                    if (esqueleto.getType() == EntityType.WITHER) {
                        esqueleto.customName(Component.text("Mini-Wither"));
                        esqueleto.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
                        esqueleto.setHealth(50);
                        esqueleto.getAttribute(Attribute.SCALE).setBaseValue(0.5);
                        esqueleto.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5); // mais fraco
                    }

                    // partículas saindo do chão
                    world.spawnParticle(Particle.LARGE_SMOKE, spawnLoc, 10, 0.5, 1, 0.5, 0.02);
                    world.spawnParticle(Particle.SOUL, spawnLoc, 8, 0.3, 1, 0.3, 0.02);

                    // som de invocação
                    world.playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1f, 1f);

                    spawned.add(esqueleto);
                }
        ).scheduleTimer(2L); // executa a cada 2 ticks
    }
}