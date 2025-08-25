package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Abissal {
    private static final NamespacedKey worldKey = new NamespacedKey("nexus", "rasgo_world");
    private static final NamespacedKey xKey = new NamespacedKey("nexus", "rasgo_x");
    private static final NamespacedKey yKey = new NamespacedKey("nexus", "rasgo_y");
    private static final NamespacedKey zKey = new NamespacedKey("nexus", "rasgo_z");
    private static final NamespacedKey yawKey = new NamespacedKey("nexus", "rasgo_yaw");
    private static final NamespacedKey pitchKey = new NamespacedKey("nexus", "rasgo_pitch");
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            rasgoAbissal(level,player);
        }else if(level<16){//8-15
            vacuo(level,player);
        }else{//16-20
            blackHole(level,player);
        }
    }
    private static void marcarRasgo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();

        Location loc = player.getLocation();

        data.set(worldKey, PersistentDataType.STRING, loc.getWorld().getName());
        data.set(xKey, PersistentDataType.DOUBLE, loc.getX());
        data.set(yKey, PersistentDataType.DOUBLE, loc.getY());
        data.set(zKey, PersistentDataType.DOUBLE, loc.getZ());
        data.set(yawKey, PersistentDataType.FLOAT, loc.getYaw());
        data.set(pitchKey, PersistentDataType.FLOAT, loc.getPitch());

        player.sendMessage("§5[Rasgo do Vazio] §7Ponto marcado.");
    }
    private static void retornarRasgo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(data.has(worldKey, PersistentDataType.STRING)) {
            String worldName = data.get(worldKey, PersistentDataType.STRING);
            World world = Bukkit.getWorld(worldName);

            if(world == null) {
                player.sendMessage("§cO mundo salvo não existe mais!");
                return;
            }

            double x = data.get(xKey, PersistentDataType.DOUBLE);
            double y = data.get(yKey, PersistentDataType.DOUBLE);
            double z = data.get(zKey, PersistentDataType.DOUBLE);
            float yaw = data.get(yawKey, PersistentDataType.FLOAT);
            float pitch = data.get(pitchKey, PersistentDataType.FLOAT);

            Location destino = new Location(world, x, y, z, yaw, pitch);

            // Partículas antes de teleportar
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 1, 1, 1);

            player.teleport(destino);

            // Partículas depois
            world.spawnParticle(Particle.DRAGON_BREATH, destino, 150, 1, 1, 1);
            world.playSound(destino, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.6f);

            // Limpando o ponto
            data.remove(worldKey);
            data.remove(xKey);
            data.remove(yKey);
            data.remove(zKey);
            data.remove(yawKey);
            data.remove(pitchKey);

            player.sendMessage("§5[Rasgo do Vazio] §7Você retornou ao ponto marcado!");
        } else {
            player.sendMessage("§cNenhum ponto foi marcado.");
        }
    }
    private static boolean temLocalMarcado(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.has(worldKey, PersistentDataType.STRING);
    }
    private static void rasgoAbissal(int level, Player player){
        if(temLocalMarcado(player)){
            retornarRasgo(player);
        }else{
            marcarRasgo(player);
        }
        for(Entity e:player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo && vivo != player){
                vivo.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,200,0));
            }
        }
    }
    private static void vacuo(int level, Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        double dano = 1 + level * 0.5;
        double forcaKnockback = 0.2 + 0.05 * level;
        // Temporizador para remover o pilar após alguns segundos
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 30,
                () -> {},
                () -> {},
                (t) -> {
                    // Cria o pilar de andaimes
                    for (Entity entity : world.getNearbyEntities(centro, 10, 10, 10)) {
                        world.spawnParticle(Particle.PORTAL, entity.getLocation().add(0,1,0), 5, 0.3, 0.3, 0.3, 0);
                        if (entity instanceof LivingEntity living && entity != player) {
                            living.damage(dano, player);
                            Vector direcao = centro.toVector().subtract(living.getLocation().toVector()).normalize();
                            living.setVelocity(direcao.multiply(forcaKnockback).setY(0.5));
                        }
                    }
                }
        ).scheduleTimer(1L); // duração em ticks
    }
    private static void blackHole(int level, Player player) {
        World world = player.getWorld();
        Location centro = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(5));
        AtomicInteger raio= new AtomicInteger(5);
        double dano = 1 + level * 0.1;
        double forcaKnockback = 0.2 + 0.05 * level;
        List<Entity> jafoi = new ArrayList<>();
        new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 30,
                () -> {},
                () -> world.createExplosion(centro,jafoi.size(),false,false,player),
                (t) -> {
                    world.spawnParticle(Particle.PORTAL, centro, 50, raio.get()/2.0, 1, raio.get()/2.0, 0);
                    for (Entity entity : world.getNearbyEntities(centro, raio.get(), raio.get(), raio.get())) {
                        if(!jafoi.contains(entity)){
                            raio.getAndUpdate(r -> Math.min(r + 1, 50));
                            jafoi.add(entity);
                        }
                        world.spawnParticle(Particle.PORTAL, entity.getLocation().add(0,1,0), 5, 0.3, 0.3, 0.3, 0);
                        if (entity instanceof LivingEntity living && entity != player) {
                            living.damage(dano, player);
                            Vector direcao = centro.toVector().subtract(living.getLocation().toVector()).normalize();
                            living.setVelocity(direcao.multiply(forcaKnockback).setY(0.2));
                        }
                    }
                    if(raio.get()>=50){
                        t.stop();
                    }
                }
        ).scheduleTimer(1L); // duração em ticks
    }
}
