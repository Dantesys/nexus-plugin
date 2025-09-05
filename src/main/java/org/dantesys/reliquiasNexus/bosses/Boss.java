package org.dantesys.reliquiasNexus.bosses;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;

import java.util.Random;

public class Boss {

    private final BossRarity rarity;
    private final Location location;
    private final JavaPlugin plugin;
    private final String bossName;
    private final boolean isSuperBoss;

    public Boss(BossRarity rarity, Location location, JavaPlugin plugin, boolean isSuperBoss) {
        this.rarity = rarity;
        this.location = location;
        this.plugin = plugin;
        this.isSuperBoss = isSuperBoss;
        this.bossName = isSuperBoss ? "§4Esqueleto Cósmico" : BossNames.getRandomName(rarity);
    }

    public LivingEntity spawn() {
        LivingEntity bossEntity;

        if (isSuperBoss) {
            bossEntity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        } else {
            // Choose a random mob type: Zombie or Skeleton
            Random random = new Random();
            if (random.nextBoolean()) {
                bossEntity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            } else {
                bossEntity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.SKELETON);
            }
        }

        // Define o nome do boss com a cor da raridade
        bossEntity.customName(Component.text(bossName).color(rarity.color));
        bossEntity.setCustomNameVisible(true);
        bossEntity.setGlowing(true);

        // Toca o som do portal do fim
        location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f);

        // Define atributos - vida lendário 1.5x, outros mantêm os multiplicadores normais
        double maxHealth = 100.0 * rarity.healthMultiplier;
        bossEntity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        bossEntity.setHealth(maxHealth);

        // Todos os bosses andam com velocidade normal (1x)
        bossEntity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.23);
        bossEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(10.0 * rarity.damageMultiplier);

        // Obtém equipamento
        ItemStack weapon;
        if (bossEntity instanceof Skeleton) {
            weapon = BossItem.getBow(rarity);
        } else {
            weapon = BossItem.getSword(rarity);
        }

        ItemStack helmet = BossItem.getHelmet(rarity);
        ItemStack chestplate = BossItem.getChestplate(rarity);
        ItemStack leggings = BossItem.getLeggings(rarity);
        ItemStack boots = BossItem.getBoots(rarity);

        // Aplica cor para armaduras de couro
        applyArmorColor(helmet, rarity.armorColor);
        applyArmorColor(chestplate, rarity.armorColor);
        applyArmorColor(leggings, rarity.armorColor);
        applyArmorColor(boots, rarity.armorColor);

        // Aplica equipamento
        bossEntity.getEquipment().setItemInMainHand(weapon);
        bossEntity.getEquipment().setHelmet(helmet);
        bossEntity.getEquipment().setChestplate(chestplate);
        bossEntity.getEquipment().setLeggings(leggings);
        bossEntity.getEquipment().setBoots(boots);

        // Torna equipamento indropável
        bossEntity.getEquipment().setHelmetDropChance(0.0F);
        bossEntity.getEquipment().setChestplateDropChance(0.0F);
        bossEntity.getEquipment().setLeggingsDropChance(0.0F);
        bossEntity.getEquipment().setBootsDropChance(0.0F);
        bossEntity.getEquipment().setItemInMainHandDropChance(0.0F);

        // Impede que o boss troque de equipamento
        bossEntity.setCanPickupItems(false);

        return bossEntity;
    }

    private void applyArmorColor(ItemStack armor, org.bukkit.Color color) {
        if (armor.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
            meta.setColor(color);
            armor.setItemMeta(meta);
        }
    }

    public String getBossName() {
        return bossName;
    }

    public BossRarity getRarity() {
        return rarity;
    }
}