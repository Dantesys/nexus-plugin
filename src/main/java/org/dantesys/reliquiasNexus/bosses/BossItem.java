package org.dantesys.reliquiasNexus.bosses;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BossItem {

    public static ItemStack getSword(BossRarity rarity) {
        Material material;
        int sharpnessLevel = 0;
        int fireAspectLevel = 0;
        int knockbackLevel = 0;

        switch (rarity) {
            case COMMUN:
                material = Material.WOODEN_SWORD;
                sharpnessLevel = 1;
                break;
            case INCOMMON:
                material = Material.STONE_SWORD;
                sharpnessLevel = 2;
                break;
            case RARE:
                material = Material.GOLDEN_SWORD;
                sharpnessLevel = 3;
                knockbackLevel = 1;
                break;
            case EPIC:
                material = Material.DIAMOND_SWORD;
                sharpnessLevel = 4;
                fireAspectLevel = 1;
                knockbackLevel = 1;
                break;
            case LEGENDARY:
                material = Material.NETHERITE_SWORD;
                sharpnessLevel = 5;
                fireAspectLevel = 2;
                knockbackLevel = 2;
                break;
            default:
                material = Material.WOODEN_SWORD;
                break;
        }

        ItemStack sword = new ItemStack(material);
        if (sharpnessLevel > 0) {
            sword.addUnsafeEnchantment(Enchantment.SHARPNESS, sharpnessLevel);
        }
        if (fireAspectLevel > 0) {
            sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, fireAspectLevel);
        }
        if (knockbackLevel > 0) {
            sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, knockbackLevel);
        }
        return sword;
    }

    public static ItemStack getHelmet(BossRarity rarity) {
        Material material;
        int protectionLevel = 0;
        int unbreakingLevel = 0;

        switch (rarity) {
            case COMMUN:
                material = Material.LEATHER_HELMET;
                protectionLevel = 1;
                break;
            case INCOMMON:
                material = Material.CHAINMAIL_HELMET;
                protectionLevel = 2;
                unbreakingLevel = 1;
                break;
            case RARE:
                material = Material.GOLDEN_HELMET;
                protectionLevel = 3;
                unbreakingLevel = 2;
                break;
            case EPIC:
                material = Material.DIAMOND_HELMET;
                protectionLevel = 4;
                unbreakingLevel = 3;
                break;
            case LEGENDARY:
                material = Material.NETHERITE_HELMET;
                protectionLevel = 5;
                unbreakingLevel = 4;
                break;
            default:
                material = Material.LEATHER_HELMET;
                break;
        }

        ItemStack helmet = new ItemStack(material);
        if (protectionLevel > 0) {
            helmet.addUnsafeEnchantment(Enchantment.PROTECTION, protectionLevel);
        }
        if (unbreakingLevel > 0) {
            helmet.addUnsafeEnchantment(Enchantment.UNBREAKING, unbreakingLevel);
        }
        return helmet;
    }

    public static ItemStack getChestplate(BossRarity rarity) {
        Material material;
        int protectionLevel = 0;
        int unbreakingLevel = 0;

        switch (rarity) {
            case COMMUN:
                material = Material.LEATHER_CHESTPLATE;
                protectionLevel = 1;
                break;
            case INCOMMON:
                material = Material.CHAINMAIL_CHESTPLATE;
                protectionLevel = 2;
                unbreakingLevel = 1;
                break;
            case RARE:
                material = Material.GOLDEN_CHESTPLATE;
                protectionLevel = 3;
                unbreakingLevel = 2;
                break;
            case EPIC:
                material = Material.DIAMOND_CHESTPLATE;
                protectionLevel = 4;
                unbreakingLevel = 3;
                break;
            case LEGENDARY:
                material = Material.NETHERITE_CHESTPLATE;
                protectionLevel = 5;
                unbreakingLevel = 4;
                break;
            default:
                material = Material.LEATHER_CHESTPLATE;
                break;
        }

        ItemStack chestplate = new ItemStack(material);
        if (protectionLevel > 0) {
            chestplate.addUnsafeEnchantment(Enchantment.PROTECTION, protectionLevel);
        }
        if (unbreakingLevel > 0) {
            chestplate.addUnsafeEnchantment(Enchantment.UNBREAKING, unbreakingLevel);
        }
        return chestplate;
    }

    public static ItemStack getLeggings(BossRarity rarity) {
        Material material;
        int protectionLevel = 0;
        int unbreakingLevel = 0;

        switch (rarity) {
            case COMMUN:
                material = Material.LEATHER_LEGGINGS;
                protectionLevel = 1;
                break;
            case INCOMMON:
                material = Material.CHAINMAIL_LEGGINGS;
                protectionLevel = 2;
                unbreakingLevel = 1;
                break;
            case RARE:
                material = Material.GOLDEN_LEGGINGS;
                protectionLevel = 3;
                unbreakingLevel = 2;
                break;
            case EPIC:
                material = Material.DIAMOND_LEGGINGS;
                protectionLevel = 4;
                unbreakingLevel = 3;
                break;
            case LEGENDARY:
                material = Material.NETHERITE_LEGGINGS;
                protectionLevel = 5;
                unbreakingLevel = 4;
                break;
            default:
                material = Material.LEATHER_LEGGINGS;
                break;
        }

        ItemStack leggings = new ItemStack(material);
        if (protectionLevel > 0) {
            leggings.addUnsafeEnchantment(Enchantment.PROTECTION, protectionLevel);
        }
        if (unbreakingLevel > 0) {
            leggings.addUnsafeEnchantment(Enchantment.UNBREAKING, unbreakingLevel);
        }
        return leggings;
    }

    public static ItemStack getBoots(BossRarity rarity) {
        Material material;
        int protectionLevel = 0;
        int unbreakingLevel = 0;
        int featherFallingLevel = 0;

        switch (rarity) {
            case COMMUN:
                material = Material.LEATHER_BOOTS;
                protectionLevel = 1;
                break;
            case INCOMMON:
                material = Material.CHAINMAIL_BOOTS;
                protectionLevel = 2;
                unbreakingLevel = 1;
                break;
            case RARE:
                material = Material.GOLDEN_BOOTS;
                protectionLevel = 3;
                unbreakingLevel = 2;
                featherFallingLevel = 1;
                break;
            case EPIC:
                material = Material.DIAMOND_BOOTS;
                protectionLevel = 4;
                unbreakingLevel = 3;
                featherFallingLevel = 2;
                break;
            case LEGENDARY:
                material = Material.NETHERITE_BOOTS;
                protectionLevel = 5;
                unbreakingLevel = 4;
                featherFallingLevel = 3;
                break;
            default:
                material = Material.LEATHER_BOOTS;
                break;
        }

        ItemStack boots = new ItemStack(material);
        if (protectionLevel > 0) {
            boots.addUnsafeEnchantment(Enchantment.PROTECTION, protectionLevel);
        }
        if (unbreakingLevel > 0) {
            boots.addUnsafeEnchantment(Enchantment.UNBREAKING, unbreakingLevel);
        }
        if (featherFallingLevel > 0) {
            boots.addUnsafeEnchantment(Enchantment.FEATHER_FALLING, featherFallingLevel);
        }
        return boots;
    }
}