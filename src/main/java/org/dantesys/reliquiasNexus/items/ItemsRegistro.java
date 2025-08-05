package org.dantesys.reliquiasNexus.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
    public static Nexus espiao;
    public static Nexus arqueiro;
    public static Nexus cacador;
    public static Nexus tempestade;
    public static Nexus mineiro;
    public static Nexus fenix;
    public static Nexus protetor;
    public static Nexus hulk;
    public static Nexus sculk;
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
        createEspiao();
        reliquias.add(espiao);
        createArqueiro();
        reliquias.add(arqueiro);
        createCacador();
        reliquias.add(cacador);
        createTempestade();
        reliquias.add(tempestade);
        createMineiro();
        reliquias.add(mineiro);
        createFenix();
        reliquias.add(fenix);
        createProtetor();
        reliquias.add(protetor);
        createHulk();
        reliquias.add(hulk);
        createSculk();
        reliquias.add(sculk);
    }
    private static void createGuerreiro(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        efeitos.add(PotionEffectType.STRENGTH);
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Guerreiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
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
        meta.setEnchantmentGlintOverride(true);
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
        meta.setEnchantmentGlintOverride(true);
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
        meta.setEnchantmentGlintOverride(true);
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
        meta.setEnchantmentGlintOverride(true);
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
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fazendeiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fazendeiro = new Nexus(item,efeitos,"fazendeiro", Attribute.BLOCK_INTERACTION_RANGE);
    }
    private static void createEspiao(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.SPEED);
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Espiao"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"espiao");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        meta.addAttributeModifier(Attribute.SCALE,new AttributeModifier(new NamespacedKey("nexus_atributo","tamanho"),-0.75, AttributeModifier.Operation.ADD_NUMBER));
        item.setItemMeta(meta);
        espiao = new Nexus(item,efeitos,"espiao", Attribute.ARMOR);
    }
    private static void createArqueiro(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.INVISIBILITY);
        efeitos.add(PotionEffectType.NIGHT_VISION);
        ItemStack item = new ItemStack(Material.BOW,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Arqueiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"arqueiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        arqueiro = new Nexus(item,efeitos,"arqueiro", Attribute.SNEAKING_SPEED);
    }
    private static void createCacador(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        ItemStack item = new ItemStack(Material.CROSSBOW,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Caçador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"cacador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        cacador = new Nexus(item,efeitos,"cacador", Attribute.STEP_HEIGHT);
    }
    private static void createTempestade(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.JUMP_BOOST);
        ItemStack item = new ItemStack(Material.MACE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Caçador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"tempestade");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        tempestade = new Nexus(item,efeitos,"tempestade", Attribute.SAFE_FALL_DISTANCE);
    }
    private static void createMineiro(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.HASTE);
        efeitos.add(PotionEffectType.NIGHT_VISION);
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Mineiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.addAttributeModifier(Attribute.SCALE,new AttributeModifier(new NamespacedKey("nexus_passiva","tamanho"),-0.25, AttributeModifier.Operation.ADD_NUMBER));
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mineiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mineiro = new Nexus(item,efeitos,"mineiro", Attribute.BLOCK_BREAK_SPEED);
    }
    private static void createFenix(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.JUMP_BOOST);
        efeitos.add(PotionEffectType.FIRE_RESISTANCE);
        ItemStack item = new ItemStack(Material.ELYTRA,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da Fenix"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fenix");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fenix = new Nexus(item,efeitos,"fenix", Attribute.ARMOR);
    }
    private static void createProtetor(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        efeitos.add(PotionEffectType.FIRE_RESISTANCE);
        ItemStack item = new ItemStack(Material.SHIELD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Protetor"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"protetor");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        protetor = new Nexus(item,efeitos,"protetor", Attribute.MAX_HEALTH);
    }
    private static void createHulk(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.RESISTANCE);
        efeitos.add(PotionEffectType.STRENGTH);
        ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Hulk"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.addAttributeModifier(Attribute.SCALE,new AttributeModifier(new NamespacedKey("nexus_hulk","tamanho"),+0.25, AttributeModifier.Operation.ADD_NUMBER));
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"hulk");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        hulk = new Nexus(item,efeitos,"hulk", Attribute.ATTACK_DAMAGE);
    }
    private static void createSculk(){
        List<PotionEffectType> efeitos = new ArrayList<>();
        efeitos.add(PotionEffectType.NIGHT_VISION);
        ItemStack item = new ItemStack(Material.ECHO_SHARD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Sculk"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"sculk");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        sculk = new Nexus(item,efeitos,"sculk", Attribute.LUCK);
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
            case "espiao" -> espiao;
            case "arqueiro" -> arqueiro;
            case "cacador" -> cacador;
            case "tempestade" -> tempestade;
            case "mineiro" -> mineiro;
            case "fenix" -> fenix;
            case "protetor" -> protetor;
            case "hulk" -> hulk;
            case "sculk" -> sculk;
            default -> null;
        };
    }
}