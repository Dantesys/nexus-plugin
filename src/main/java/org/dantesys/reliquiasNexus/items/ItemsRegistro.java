package org.dantesys.reliquiasNexus.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static org.dantesys.reliquiasNexus.util.NexusKeys.DONO;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class ItemsRegistro {
    public static Nexus guerreiro;
    public static Nexus ceifador;
    public static Nexus vida;
    public static Nexus mares;
    public static Nexus barbaro;
    public static Nexus fazendeiro;
    private static final List<Nexus> reliquias = new ArrayList<>();
    public static void init(){
        createGuerreiro();
        reliquias.add(guerreiro);
        createCeifador();
        reliquias.add(ceifador);
        createVida();
        reliquias.add(vida);
        createMares();
        reliquias.add(mares);
        createBarbaro();
        reliquias.add(barbaro);
        createFazendeiro();
        reliquias.add(fazendeiro);
    }
    private static void createGuerreiro(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.WOODEN_SWORD);
        mat.add(1,Material.STONE_SWORD);
        mat.add(2,Material.IRON_SWORD);
        mat.add(3,Material.GOLDEN_SWORD);
        mat.add(4,Material.DIAMOND_SWORD);
        mat.add(5,Material.NETHERITE_SWORD);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        efeitos.add(PotionEffectType.STRENGTH);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Guerreiro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"guerreiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        guerreiro = new Nexus(item,mat.size(),mat,efeitos,"guerreiro", Attribute.ATTACK_DAMAGE);
    }
    private static void createCeifador(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.WOODEN_SWORD);
        mat.add(1,Material.STONE_SWORD);
        mat.add(2,Material.IRON_SWORD);
        mat.add(3,Material.GOLDEN_SWORD);
        mat.add(4,Material.DIAMOND_SWORD);
        mat.add(5,Material.NETHERITE_SWORD);
        List<Material> visual = new ArrayList<>();
        visual.add(0,Material.WOODEN_HOE);
        visual.add(1,Material.STONE_HOE);
        visual.add(2,Material.IRON_HOE);
        visual.add(3,Material.GOLDEN_HOE);
        visual.add(4,Material.DIAMOND_HOE);
        visual.add(5,Material.NETHERITE_HOE);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.NIGHT_VISION);
        efeitos.add(PotionEffectType.INVISIBILITY);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Ceifador"));
        meta.setItemModel(Material.WOODEN_HOE.getKey());
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"ceifador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        ceifador = new Nexus(item,mat.size(),mat,efeitos,"ceifador", Attribute.ATTACK_DAMAGE,visual);
    }
    private static void createVida(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.TOTEM_OF_UNDYING);
        mat.add(1,Material.TOTEM_OF_UNDYING);
        mat.add(2,Material.TOTEM_OF_UNDYING);
        mat.add(3,Material.TOTEM_OF_UNDYING);
        List<Material> visual = new ArrayList<>();
        visual.add(0,Material.GOLD_NUGGET);
        visual.add(1,Material.RAW_GOLD);
        visual.add(2,Material.GOLD_INGOT);
        visual.add(3,Material.TOTEM_OF_UNDYING);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.REGENERATION);
        efeitos.add(PotionEffectType.LUCK);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da vida"));
        meta.setItemModel(Material.GOLD_NUGGET.getKey());
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"vida");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        vida = new Nexus(item,mat.size(),mat,efeitos,"vida", Attribute.MAX_HEALTH,visual);
    }
    private static void createMares(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.TRIDENT);
        mat.add(1,Material.TRIDENT);
        mat.add(2,Material.TRIDENT);
        List<Material> visual = new ArrayList<>();
        visual.add(0,Material.STICK);
        visual.add(1,Material.ARROW);
        visual.add(3,Material.TRIDENT);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.CONDUIT_POWER);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus dos Mares"));
        meta.setItemModel(Material.STICK.getKey());
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mares");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mares = new Nexus(item,mat.size(),mat,efeitos,"mares", Attribute.ATTACK_DAMAGE,visual);
    }
    private static void createBarbaro(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.WOODEN_AXE);
        mat.add(1,Material.STONE_AXE);
        mat.add(2,Material.IRON_AXE);
        mat.add(3,Material.GOLDEN_AXE);
        mat.add(4,Material.DIAMOND_AXE);
        mat.add(5,Material.NETHERITE_AXE);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.STRENGTH);
        efeitos.add(PotionEffectType.SPEED);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Barbaro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"barbaro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        barbaro = new Nexus(item,mat.size(),mat,efeitos,"barbaro", Attribute.ATTACK_DAMAGE);
    }
    private static void createFazendeiro(){
        List<Material> mat = new ArrayList<>();
        mat.add(0,Material.WOODEN_HOE);
        mat.add(1,Material.STONE_HOE);
        mat.add(2,Material.IRON_HOE);
        mat.add(3,Material.GOLDEN_HOE);
        mat.add(4,Material.DIAMOND_HOE);
        mat.add(5,Material.NETHERITE_HOE);
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.LUCK);
        ItemStack item = new ItemStack(mat.getFirst(),1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Fazendeiro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fazendeiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fazendeiro = new Nexus(item,mat.size(),mat,efeitos,"fazendeiro", Attribute.BLOCK_INTERACTION_RANGE);
    }
    public static List<Nexus> getValidReliquia(FileConfiguration config){
        List<Nexus> validos = new ArrayList<>();
        for(Nexus n:reliquias){
            String uuidStr = config.getString("nexus."+n.getNome());
            if(uuidStr == null || uuidStr.isBlank()){
                validos.add(n);
            }
        }
        return validos;
    }
    public static Nexus getFromNome(String nome){
        return switch (nome){
            case "guerreiro" -> guerreiro;
            case "ceifador" -> ceifador;
            case "vida" -> vida;
            case "mares" -> mares;
            case "barbaro" -> barbaro;
            case "fazendeiro" -> fazendeiro;
            default -> null;
        };
    }
}