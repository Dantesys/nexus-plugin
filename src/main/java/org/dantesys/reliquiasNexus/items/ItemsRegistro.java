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
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        efeitos.add(PotionEffectType.STRENGTH);
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Guerreiro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"guerreiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        guerreiro = new Nexus(item,efeitos,"guerreiro", Attribute.ATTACK_DAMAGE);
    }
    private static void createCeifador(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.NIGHT_VISION);
        efeitos.add(PotionEffectType.INVISIBILITY);
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Ceifador"));
        meta.setItemModel(Material.NETHERITE_HOE.getKey());
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"ceifador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        ceifador = new Nexus(item,efeitos,"ceifador", Attribute.ATTACK_DAMAGE);
    }
    private static void createVida(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.REGENERATION);
        efeitos.add(PotionEffectType.LUCK);
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da vida"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"vida");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        vida = new Nexus(item,efeitos,"vida", Attribute.MAX_HEALTH);
    }
    private static void createMares(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.CONDUIT_POWER);
        ItemStack item = new ItemStack(Material.TRIDENT,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus dos Mares"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mares");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mares = new Nexus(item,efeitos,"mares", Attribute.ATTACK_DAMAGE);
    }
    private static void createBarbaro(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.STRENGTH);
        efeitos.add(PotionEffectType.SPEED);
        ItemStack item = new ItemStack(Material.NETHERITE_AXE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Barbaro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"barbaro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        barbaro = new Nexus(item,efeitos,"barbaro", Attribute.ATTACK_DAMAGE);
    }
    private static void createFazendeiro(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.LUCK);
        ItemStack item = new ItemStack(Material.NETHERITE_HOE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Fazendeiro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fazendeiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fazendeiro = new Nexus(item,efeitos,"fazendeiro", Attribute.BLOCK_INTERACTION_RANGE);
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