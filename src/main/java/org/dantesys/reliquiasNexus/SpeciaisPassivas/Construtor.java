package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

public class Construtor {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            canteirodeObras(level,player);
        }else if(level<16){//8-15
            pilarElevador(level,player);
        }else{//16-20
            formigaVoadora(level,player);
        }
    }
    private static void canteirodeObras(int level, Player player){
        World world = player.getWorld();
        Location centro = player.getLocation();
        int tamanho = level*3;
        int raio = tamanho / 2;
        double dano = 2 + level; // exemplo de dano que escala com o level
        double forcaKnockback = 0.5 + 0.1 * level; // força do knockback que escala
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 30,
                ()->{
            },()-> {},(t)->{
        });
        timer.scheduleTimer(1L);
        // Cria as partículas na área
        for (int x = -raio; x <= raio; x++) {
            for (int z = -raio; z <= raio; z++) {
                Location local = centro.clone().add(x, 0, z);
                world.spawnParticle(Particle.CRIT, local.add(0.5, 0.5, 0.5), 5, 0.3, 0.3, 0.3);
            }
        }

        // Aplica dano e knockback às entidades dentro do raio
        for (Entity entity : world.getNearbyEntities(centro, raio, 2, raio)) {
            if (entity instanceof LivingEntity living && entity != player) {
                // Dano
                living.damage(dano, player);

                // Knockback
                Vector direcao = living.getLocation().toVector().subtract(centro.toVector()).normalize();
                living.setVelocity(direcao.multiply(forcaKnockback).setY(0.5));
            }
        }

    }
    private static void pilarElevador(int level, Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        int altura = level * 2; // altura do pilar escalando com o level
        Material bloco = Material.SCAFFOLDING;
        double dano = 1 + level * 0.5;
        double forcaKnockback = 0.2 + 0.05 * level;

        // Cria o pilar de andaimes
        for (int y = 0; y <= altura; y++) {
            Location local = centro.clone().add(0, y, 0);
            world.spawnParticle(Particle.CRIT, local.add(0.5, 0.5, 0.5), 5, 0.3, 0.3, 0.3);
            local.getBlock().setType(bloco);
        }

        // Aplica dano e knockback às entidades dentro da base do pilar
        for (Entity entity : world.getNearbyEntities(centro, 2, 2, 2)) {
            if (entity instanceof LivingEntity living && entity != player) {
                living.damage(dano, player);
                Vector direcao = living.getLocation().toVector().subtract(centro.toVector()).normalize();
                living.setVelocity(direcao.multiply(forcaKnockback).setY(0.5));
            }
        }

        // Temporizador para remover o pilar após alguns segundos
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                () -> player.sendMessage("Pilar de Andaimes ativado!"),
                () -> {
                    player.sendMessage("Pilar de Andaimes removido!");
                    for (int y = 0; y <= altura; y++) {
                        Location local = centro.clone().add(0, y, 0);
                        if (local.getBlock().getType() == bloco) {
                            local.getBlock().setType(Material.AIR);
                        }
                    }
                },
                (t) -> {}
        ).scheduleTimer(20L); // duração em ticks
    }
    private static void formigaVoadora(int level, Player player) {
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10+level,
                () -> {
                    player.getAttribute(Attribute.SCALE).setBaseValue(0.0625);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                },
                () -> {
                    player.getAttribute(Attribute.SCALE).setBaseValue(1);
                    player.setAllowFlight(false);
                    player.setFlying(false);
                },
                (t) -> {
                    player.sendActionBar(Component.text("Fly ends in "+t.getSegundosRestantes()+"s"));
                }
        ).scheduleTimer(20L);
    }
}
