package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.eventos.PassivaEvent;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.morte;
import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class Morte {
    // Efeito passivo: Aura de Morte
    public static void aplicaEfeitoPassivo(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(22);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1, false, false));

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
        player.sendMessage(Component.text("§eVocê tem 10 segundos para marcar sua presa com um soco.").color(NamedTextColor.YELLOW));
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
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 20L); // 10 segundos
    }

    public static void onHitPunhoDaMorte(Player player, LivingEntity target) {
        if (player.hasMetadata("punhoDaMorteAtivo")) {
            player.removeMetadata("punhoDaMorteAtivo", ReliquiasNexus.getPlugin(ReliquiasNexus.class));
            player.sendMessage(Component.text("§aVocê acertou sua presa! A contagem de 10 segundos para fugir começou.").color(NamedTextColor.GREEN));

            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
            target.sendMessage(Component.text("§aFuja o mais rápido que puder.").color(NamedTextColor.GREEN));
            target.setMetadata("alvoDaMorte", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), player.getUniqueId()));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (target.hasMetadata("alvoDaMorte") && target.getLocation().distance(player.getLocation()) < 50) {
                        target.setHealth(0);
                        player.sendMessage(Component.text("§4A sua presa não conseguiu fugir da Morte.").color(NamedTextColor.DARK_RED));
                        target.sendMessage(Component.text("§cVocê não foi rápido o suficiente.").color(NamedTextColor.RED));

                        int almasColetadas = player.getPersistentDataContainer().getOrDefault(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas + 1);
                        player.sendMessage(Component.text("§eAlmas Coletadas: " + (almasColetadas + 1) + "/25").color(NamedTextColor.GOLD));
                    } else if (target.hasMetadata("alvoDaMorte")) {
                        player.sendMessage(Component.text("§aSua presa foi rápida e escapou da Morte.").color(NamedTextColor.GREEN));
                    }
                    target.removeMetadata("alvoDaMorte", ReliquiasNexus.getPlugin(ReliquiasNexus.class));
                }
            }.runTaskLater(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 200L); // 10 segundos
        }
    }

    // Habilidade 2 (25-49 Almas): Morte Súbita
    public static void getMorteSubita(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 1.5f, 0.8f);
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 100, 1, 1, 1);
        player.sendTitle("§4A Morte se aproxima!", "", 10, 70, 20);

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity livingEntity && livingEntity != player) {
                int bonusPoder = (almasColetadas - 25) / 25;
                int randomChance = ThreadLocalRandom.current().nextInt(100);

                if (randomChance < (15 + bonusPoder * 5)) {
                    // Hit kill
                    livingEntity.setHealth(0);
                    player.sendMessage(Component.text("§4Você ceifou a alma de " + livingEntity.getName() + ".").color(NamedTextColor.DARK_RED));
                    player.getPersistentDataContainer().set(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas + 1);
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

    // Nova Habilidade de 50 Almas: Domínio de Almas
    public static void getDominioDeAlmas(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        if (almasColetadas >= 50) {
            player.getPersistentDataContainer().set(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas - 50);
            player.sendMessage(Component.text("§4Você usou 50 almas para exercer o seu domínio!").color(NamedTextColor.DARK_RED));

            double radius = 15.0; // Raio de efeito
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity livingEntity && livingEntity != player) {
                    Location targetLocation = livingEntity.getLocation();
                    Vector knockback = targetLocation.toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5);
                    knockback.setY(1.5);
                    livingEntity.setVelocity(knockback);

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 1)); // Fica levitando por 3 segundos
                    livingEntity.getWorld().playSound(targetLocation, Sound.ENTITY_PLAYER_HURT, 1.0f, 0.5f);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (livingEntity.isValid() && livingEntity.hasPotionEffect(PotionEffectType.LEVITATION)) {
                                livingEntity.removePotionEffect(PotionEffectType.LEVITATION);
                                livingEntity.setHealth(0); // Dano de explosão

                                // Causa uma pequena explosão no local do mob/player
                                livingEntity.getWorld().createExplosion(livingEntity.getLocation(), 2.0f, false, false);
                                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                            }
                        }
                    }.runTaskLater(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 60L);
                }
            }
        } else {
            player.sendMessage(Component.text("§cVocê precisa de pelo menos 50 almas para usar essa habilidade!").color(NamedTextColor.RED));
        }
    }

    // Habilidade 3 (100+ Almas): Devorador de Almas
    public static void getDevoradorDeAlmas(Player player) {
        int almasColetadas = player.getPersistentDataContainer().getOrDefault(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        if (almasColetadas >= 100) {
            player.getPersistentDataContainer().set(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, almasColetadas - 100);
            player.sendMessage(Component.text("§4Você devorou 100 almas e se tornou invisível!").color(NamedTextColor.DARK_RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
        } else {
            player.sendMessage(Component.text("§cVocê precisa de pelo menos 100 almas para usar essa habilidade!").color(NamedTextColor.RED));
        }
    }

    public static void startMissaoMorte(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        ReliquiasNexus plugin = ReliquiasNexus.getPlugin(ReliquiasNexus.class);
        String donoMorte = plugin.getNexusConfig().getString("nexus.morte");

        if (donoMorte != null && !donoMorte.equals(player.getUniqueId().toString())) {
            player.sendMessage(Component.text("§cNão foi possível começar essa missão, a relíquia da Morte já tem um dono.").color(NamedTextColor.RED));
            return;
        }

        int missaoProgress = pdc.getOrDefault(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 0);

        if (missaoProgress == 0) {
            pdc.set(NexusKeys.MISSAOMORTE.key, PersistentDataType.INTEGER, 1);
            player.sendMessage(Component.text("§eEtapa 1: Morra 3 vezes.").color(NamedTextColor.YELLOW));
        } else if (missaoProgress == 1) {
            player.sendMessage(Component.text("§eEtapa 2: Seja morto por um Wither.").color(NamedTextColor.YELLOW));
        } else if (missaoProgress == 2) {
            player.sendMessage(Component.text("§eEtapa 3: Mate um Wither.").color(NamedTextColor.YELLOW));
        } else if (missaoProgress == 3) {
            pdc.set(NexusKeys.MISSAO_META.key, PersistentDataType.INTEGER, 25);
            pdc.set(NexusKeys.MISSAO_PROGRESO.key, PersistentDataType.INTEGER, 0);
            player.sendMessage(Component.text("§eEtapa 4: Mate 25 mobs hostis.").color(NamedTextColor.YELLOW));
        } else if (missaoProgress == 4) {
            pdc.set(NexusKeys.MISSAO_META.key, PersistentDataType.INTEGER, 75);
            pdc.set(NexusKeys.MISSAO_PROGRESO.key, PersistentDataType.INTEGER, 0);
            player.sendMessage(Component.text("§eEtapa 5: Mate 75 Zumbis.").color(NamedTextColor.YELLOW));
        } else if (missaoProgress >= 5) {
            player.sendMessage(Component.text("§cVocê já completou a missão da Morte!").color(NamedTextColor.RED));
        }
    }
}