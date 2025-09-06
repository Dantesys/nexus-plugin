package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Cozinheiro {
    private static final List<Material> foods = Arrays.asList(
            Material.BREAD, Material.APPLE, Material.COOKED_BEEF,
            Material.COOKED_CHICKEN, Material.COOKED_PORKCHOP
    );
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            chuvaComida(level,player);
        }else if(level<16){//8-15
            banqueteDoGlutao(level,player);
        }else{//16-20
            boloDoCaos(level,player);
        }
    }
    private static void chuvaComida(int level, Player player){
        World world = player.getWorld();
        Location loc = player.getLocation();

        for (int i = 0; i < 10*level; i++) {
            Material food = foods.get(new Random().nextInt(foods.size()));
            ItemStack drop = new ItemStack(food, 1);

            Location dropLoc = loc.clone().add(Math.random() * 4 - 2, 6, Math.random() * 4 - 2);
            Item item = world.dropItem(dropLoc, drop);
            item.setPickupDelay(20);

            // Marca o item como parte da habilidade
            item.setMetadata("chuva_de_comida", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));
            Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 20,
                    ()->{
                    },()-> {
            },(t)->{
                if (!item.isValid() || item.isOnGround()) {
                    t.stop();
                    return;
                }

                for (Entity nearby : item.getNearbyEntities(1, 1, 1)) {
                    if (nearby instanceof LivingEntity && !(nearby instanceof Player)) {
                        ((LivingEntity) nearby).damage(2 + level, item); // escala com level
                        nearby.getWorld().playSound(nearby.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                        nearby.getWorld().spawnParticle(Particle.EGG_CRACK, nearby.getLocation(), 10, 0.3, 0.3, 0.3);
                        item.remove();
                        t.stop();
                        break;
                    }
                }
            });
            timer.scheduleTimer(2L);
        }
    }
    private static void banqueteDoGlutao(int level, Player player){
        Location center = player.getLocation();
        World world = center.getWorld();

        world.playSound(center, Sound.ENTITY_PLAYER_BURP, 2f, 0.8f);
        world.spawnParticle(Particle.HAPPY_VILLAGER, center, 100, 3, 1, 3, 0.2);

        int radius = 6 + (level / 2);

        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof Player p && !p.equals(player)) {
                if (p.getFoodLevel() >= 20) {
                    // fome cheia -> explode
                    world.createExplosion(p.getLocation(), 0F, false, false, player);
                    p.damage(6, player); // dano direto
                } else {
                    // não está cheio -> perde vida e enche a fome
                    p.damage(4, player);
                    p.setFoodLevel(4+p.getFoodLevel());
                }
            }
            else if (entity instanceof LivingEntity mob && !(mob instanceof Player)) {
                Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 20,
                        ()->{
                        },()-> {
                },(t)->{
                    double scale = mob.getAttribute(Attribute.SCALE).getBaseValue();
                    if(scale>=1.5d){
                        world.createExplosion(mob.getLocation(), 0F, false, false, player);
                        Material food = foods.get(new Random().nextInt(foods.size()));
                        ItemStack drop = new ItemStack(food, 1 + new Random().nextInt(2));
                        mob.getWorld().dropItemNaturally(mob.getLocation(), drop);
                        mob.remove();
                        t.stop();
                    }else{
                        mob.getAttribute(Attribute.SCALE).setBaseValue(scale+0.1d);
                    }
                });
                timer.scheduleTimer(5L);
            }
        }
    }

    private static void boloDoCaos(int level, Player dono) {
        Location center = dono.getLocation();
        World world = center.getWorld();
        int radius = 8;

        // Efeito inicial
        world.spawnParticle(Particle.LARGE_SMOKE, center, 200, radius / 2.0, 1, radius / 2.0, 0.2);
        world.playSound(center, Sound.ENTITY_PLAYER_BURP, 2f, 0.7f);

        // Lista de alvos
        List<Entity> targets = world.getNearbyEntities(center, radius, radius, radius).stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(dono))
                .toList();

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                ticks++;
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        // Puxar para o centro
                        Vector dir = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.3);
                        le.setVelocity(dir);

                        // Dano periódico
                        le.damage((double) level /4, dono);

                        // Efeito visual
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 1, false, false, true));
                        le.getWorld().spawnParticle(Particle.FLAME, le.getLocation().add(0,1,0), 5, 0.3, 0.3, 0.3, 0.1);
                    }
                }

                if (ticks >= 5 * 20) { // 5 segundos
                    // Explosão final
                    for (Entity e : targets) {
                        if (e instanceof LivingEntity le && !(le instanceof Player)) {
                            world.createExplosion(le.getLocation(), 0F, false, false, dono);
                            le.remove();
                        }
                    }

                    // Spawn do bolo
                    ItemStack cake = new ItemStack(Material.CAKE);
                    cake.getItemMeta().getPersistentDataContainer().set(new NamespacedKey("nexus","bolo_do_caos"), PersistentDataType.BOOLEAN,true);
                    world.dropItemNaturally(center, cake);
                    world.spawnParticle(Particle.HAPPY_VILLAGER, center, 50, 2, 1, 2, 0.2);
                    world.playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 2f, 1f);

                    cancel();
                }
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0, 1);
    }

}
