package org.dantesys.reliquiasNexus.bosses;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class BossDrop {

    private static final Random random = new Random();

    public static List<ItemStack> getRandomDrops(BossRarity rarity) {
        int dropCount = random.nextInt(3) + 2; // Drops 2 to 4 items
        List<ItemStack> drops = new ArrayList<>();

        for (int i = 0; i < dropCount; i++) {
            switch (rarity) {
                case COMMUN:
                    drops.add(getCommonDrop());
                    break;
                case INCOMMON:
                    drops.add(getUncommonDrop());
                    break;
                case RARE:
                    drops.add(getRareDrop());
                    break;
                case EPIC:
                    drops.add(getEpicDrop());
                    break;
                case LEGENDARY:
                    drops.add(getLegendaryDrop());
                    break;
                default:
                    drops.add(getCommonDrop());
                    break;
            }
        }
        return drops;
    }

    // Métodos de drop de cada raridade...
    private static ItemStack getCommonDrop() {
        List<ItemStack> commonDrops = Arrays.asList(
                createEnchantedItem(Material.LEATHER_HELMET, "Capacete de Couro Encantado", Enchantment.PROTECTION, 1),
                createEnchantedItem(Material.LEATHER_CHESTPLATE, "Peitoral de Couro Encantado", Enchantment.PROTECTION, 1),
                createEnchantedItem(Material.LEATHER_LEGGINGS, "Calças de Couro Encantado", Enchantment.PROTECTION, 1),
                createEnchantedItem(Material.LEATHER_BOOTS, "Botas de Couro Encantado", Enchantment.PROTECTION, 1),
                createEnchantedItem(Material.WOODEN_SWORD, "Espada de Madeira Encantada", Enchantment.SHARPNESS, 1),
                createItem(Material.GOLD_NUGGET, 3, "Pepitas de Ouro"),
                createItem(Material.IRON_NUGGET, 5, "Pepitas de Ferro")
        );
        return commonDrops.get(random.nextInt(commonDrops.size()));
    }

    private static ItemStack getUncommonDrop() {
        List<ItemStack> uncommonDrops = Arrays.asList(
                createEnchantedItem(Material.CHAINMAIL_HELMET, "Capacete de Malha Encantado", Enchantment.PROTECTION, 2),
                createEnchantedItem(Material.CHAINMAIL_CHESTPLATE, "Peitoral de Malha Encantado", Enchantment.PROTECTION, 2),
                createEnchantedItem(Material.CHAINMAIL_LEGGINGS, "Calças de Malha Encantado", Enchantment.PROTECTION, 2),
                createEnchantedItem(Material.CHAINMAIL_BOOTS, "Botas de Malha Encantado", Enchantment.PROTECTION, 2),
                createEnchantedItem(Material.STONE_SWORD, "Espada de Pedra Encantada", Enchantment.SHARPNESS, 2),
                createItem(Material.GOLD_INGOT, 2, "Barra de Ouro"),
                createItem(Material.IRON_INGOT, 3, "Barra de Ferro"),
                createItem(Material.EXPERIENCE_BOTTLE, 3, "Frascos de Experiência")
        );
        return uncommonDrops.get(random.nextInt(uncommonDrops.size()));
    }

    private static ItemStack getRareDrop() {
        List<ItemStack> rareDrops = Arrays.asList(
                createEnchantedItem(Material.GOLDEN_HELMET, "Capacete de Ouro Encantado", Enchantment.PROTECTION, 3),
                createEnchantedItem(Material.GOLDEN_CHESTPLATE, "Peitoral de Ouro Encantado", Enchantment.PROTECTION, 3),
                createEnchantedItem(Material.GOLDEN_LEGGINGS, "Calças de Ouro Encantado", Enchantment.PROTECTION, 3),
                createEnchantedItem(Material.GOLDEN_BOOTS, "Botas de Ouro Encantado", Enchantment.PROTECTION, 3),
                createEnchantedItem(Material.IRON_SWORD, "Espada de Ferro Encantada", Enchantment.SHARPNESS, 3),
                createEnchantedItem(Material.IRON_SWORD, "Espada de Fogo", Enchantment.FIRE_ASPECT, 1),
                createItem(Material.DIAMOND, 1, "Diamante"),
                createItem(Material.EMERALD, 2, "Esmeraldas"),
                createItem(Material.EXPERIENCE_BOTTLE, 5, "Frascos de Experiência")
        );
        return rareDrops.get(random.nextInt(rareDrops.size()));
    }

    private static ItemStack getEpicDrop() {
        List<ItemStack> epicDrops = Arrays.asList(
                createEnchantedItem(Material.DIAMOND_HELMET, "Capacete de Diamante Encantado", Enchantment.PROTECTION, 4),
                createEnchantedItem(Material.DIAMOND_CHESTPLATE, "Peitoral de Diamante Encantado", Enchantment.PROTECTION, 4),
                createEnchantedItem(Material.DIAMOND_LEGGINGS, "Calças de Diamante Encantado", Enchantment.PROTECTION, 4),
                createEnchantedItem(Material.DIAMOND_BOOTS, "Botas de Diamante Encantado", Enchantment.PROTECTION, 4),
                createEnchantedItem(Material.DIAMOND_SWORD, "Espada de Diamante Encantada", Enchantment.SHARPNESS, 4),
                createEnchantedItem(Material.DIAMOND_SWORD, "Espada de Fogo Superior", Enchantment.FIRE_ASPECT, 2),
                createItem(Material.DIAMOND, 3, "Diamantes"),
                createItem(Material.EMERALD, 5, "Esmeraldas"),
                createItem(Material.NETHERITE_SCRAP, 1, "Fragmento de Netherite"),
                createItem(Material.EXPERIENCE_BOTTLE, 8, "Frascos de Experiência")
        );
        return epicDrops.get(random.nextInt(epicDrops.size()));
    }

    private static ItemStack getLegendaryDrop() {
        List<ItemStack> legendaryDrops = Arrays.asList(
                createEnchantedItem(Material.NETHERITE_HELMET, "Capacete de Netherite Lendário",
                        new Enchantment[]{Enchantment.PROTECTION, Enchantment.AQUA_AFFINITY, Enchantment.RESPIRATION},
                        new int[]{5, 1, 3}),
                createEnchantedItem(Material.NETHERITE_CHESTPLATE, "Peitoral de Netherite Lendário",
                        new Enchantment[]{Enchantment.PROTECTION, Enchantment.THORNS, Enchantment.UNBREAKING},
                        new int[]{5, 3, 5}),
                createEnchantedItem(Material.NETHERITE_LEGGINGS, "Calças de Netherite Lendárias",
                        new Enchantment[]{Enchantment.PROTECTION, Enchantment.MENDING, Enchantment.UNBREAKING},
                        new int[]{5, 1, 5}),
                createEnchantedItem(Material.NETHERITE_BOOTS, "Botas de Netherite Lendárias",
                        new Enchantment[]{Enchantment.PROTECTION, Enchantment.FEATHER_FALLING, Enchantment.SOUL_SPEED},
                        new int[]{5, 4, 3}),
                createEnchantedItem(Material.NETHERITE_SWORD, "Espada de Netherite Lendária",
                        new Enchantment[]{Enchantment.SHARPNESS, Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK},
                        new int[]{15, 5, 3}),
                createItem(Material.NETHERITE_INGOT, 1, "Barra de Netherite"),
                createItem(Material.DIAMOND, 5, "Diamantes"),
                createItem(Material.EMERALD_BLOCK, 2, "Blocos de Esmeralda"),
                createItem(Material.EXPERIENCE_BOTTLE, 12, "Frascos de Experiência"),
                createItem(Material.ENCHANTED_GOLDEN_APPLE, 1, "Maçã Dourada Encantada")
        );
        return legendaryDrops.get(random.nextInt(legendaryDrops.size()));
    }

    private static ItemStack createItem(Material material, int amount, String displayName) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createEnchantedItem(Material material, String displayName, Enchantment enchantment, int level) {
        ItemStack item = createItem(material, 1, displayName);
        item.addUnsafeEnchantment(enchantment, level);
        return item;
    }

    private static ItemStack createEnchantedItem(Material material, String displayName, Enchantment[] enchantments, int[] levels) {
        ItemStack item = createItem(material, 1, displayName);
        for (int i = 0; i < enchantments.length; i++) {
            item.addUnsafeEnchantment(enchantments[i], levels[i]);
        }
        return item;
    }
}