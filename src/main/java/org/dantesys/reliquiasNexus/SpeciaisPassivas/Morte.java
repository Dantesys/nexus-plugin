package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.bukkit.attribute.Attribute;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static org.dantesys.reliquiasNexus.util.NexusKeys.MISSAOMORTE;
import static org.dantesys.reliquiasNexus.util.NexusKeys.MORTE;
import static org.dantesys.reliquiasNexus.items.ItemsRegistro.morte;

public class Morte {
    // Efeito passivo: Aura de Morte
    public static void aplicaEfeitoPassivo(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(22);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                int bonusPoder = almasColetadas >= 100 ? (almasColetadas - 100) / 25 : 0;
                double radius = 5 + (almasColetadas / 10);

                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity livingEntity && livingEntity != player) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0 + bonusPoder));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0 + bonusPoder));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1 + bonusPoder));
                    }
                }
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 20L);
    }

    // Habilidade 1 (0-24 Almas): Punho da Morte
    public static void startPunhoDaMorte(Player player) {
        player.sendMessage(Component.text("§eVocê tem 10 segundos para marcar sua presa com um soco."));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 1.0f);

        player.setMetadata("punhoDaMorteAtivo", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), true));

        new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if (!player.hasMetadata("punhoDaMorteAtivo")) {
                    this.cancel();
                    return;
                }
                player.sendTitle("", "§e" + count + "s", 0, 20, 0);
                if (count <= 0) {
                    player.removeMetadata("punhoDaMorteAtivo", ReliquiasNexus.getPlugin(ReliquiasNexus.class));
                    player.sendMessage("§cO tempo para marcar sua presa acabou.");
                    this.cancel();
                }
                count--;
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 20L);
    }

    public static void onHitPunhoDaMorte(Player player, LivingEntity target) {
        if (player.hasMetadata("punhoDaMorteAtivo")) {
            player.removeMetadata("punhoDaMorteAtivo", ReliquiasNexus.getPlugin(ReliquiasNexus.class));
            player.sendMessage("§aVocê acertou sua presa! A contagem de 10 segundos para fugir começou.");

            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
            target.sendMessage(Component.text("§aFuja o mais rápido que puder."));
            target.setMetadata("alvoDaMorte", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), player.getUniqueId()));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (target.hasMetadata("alvoDaMorte") && target.getLocation().distance(player.getLocation()) < 50) {
                        target.setHealth(0);
                        player.sendMessage("§4A sua presa não conseguiu fugir da Morte.");
                        target.sendMessage("§cVocê não foi rápido o suficiente.");

                        int almasColetadas = player.getPersistentDataContainer().getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas + 1);
                    } else if (target.hasMetadata("alvoDaMorte")) {
                        player.sendMessage("§aSua presa foi rápida e escapou da Morte.");
                    }
                    target.removeMetadata("alvoDaMorte", ReliquiasNexus.getPlugin(ReliquiasNexus.class));
                }
            }.runTaskLater(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 200L); // 10 segundos
        }
    }

    // Habilidade 2 (25-99 Almas): Morte Súbita
    public static void getMorteSubita(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 1.5f, 0.8f);
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 100, 1, 1, 1);
        player.sendTitle("§4A Morte se aproxima!", "", 10, 70, 20);

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity livingEntity && livingEntity != player) {

                int bonusPoder = almasColetadas >= 100 ? (almasColetadas - 100) / 25 : (almasColetadas - 25) / 25;

                int randomChance = ThreadLocalRandom.current().nextInt(100);

                if (randomChance < (15 + bonusPoder * 5)) {
                    // Hit kill
                    livingEntity.setHealth(0);
                    player.sendMessage("§4Você ceifou a alma de " + livingEntity.getName() + ".");
                    player.getPersistentDataContainer().set(MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas + 1);
                } else if (randomChance < (50 + bonusPoder * 5)) {
                    // Tira 75% da vida
                    livingEntity.damage(livingEntity.getHealth() * 0.75);
                } else if (randomChance < (75 + bonusPoder * 5)) {
                    // Tira 30% da vida
                    livingEntity.damage(livingEntity.getHealth() * 0.30);
                } else {
                    // Aplica efeitos negativos
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2 + bonusPoder));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                }
            }
        }
    }

    // Habilidade 3 (100+ Almas): Devorador de Almas
    public static void getDevoradorDeAlmas(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        if (almasColetadas >= 5) {
            player.getPersistentDataContainer().set(MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas - 5);
            player.sendMessage("§4Você devorou 5 almas e se tornou invisível!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
        } else {
            player.sendMessage("§cVocê precisa de pelo menos 5 almas para usar essa habilidade!");
        }
    }

    private static boolean temReliquia(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                if (item.getItemMeta().getPersistentDataContainer().getOrDefault(MORTE.key, PersistentDataType.STRING, "").equals("morte")) {
                    return true;
                }
            }
        }
        return false;
    }
}