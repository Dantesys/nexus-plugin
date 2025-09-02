package org.dantesys.reliquiasNexus.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
    public static Nexus pescador;
    public static Nexus flash;
    public static Nexus mago;
    public static Nexus ladrao;
    public static Nexus domador;
    public static Nexus cozinheiro;
    public static Nexus construtor;
    public static Nexus abissal;
    public static Nexus cronosombra;
    public static Nexus assassino;
    public static Nexus frostis;
    public static Nexus necromante;
    public static Nexus alquimista;
    public static Nexus golem;
    public static Nexus dragao;
    public static Nexus morte;
    public static Nexus livro;
    public static Nexus carrasco;
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
        createPescador();
        reliquias.add(pescador);
        createFlash();
        reliquias.add(flash);
        createMago();
        reliquias.add(mago);
        createLadrao();
        reliquias.add(ladrao);
        createDomador();
        reliquias.add(domador);
        createCozinheiro();
        reliquias.add(cozinheiro);
        createConstrutor();
        reliquias.add(construtor);
        createAbissal();
        reliquias.add(abissal);
        createCronosombra();
        reliquias.add(cronosombra);
        createAssassino();
        reliquias.add(assassino);
        createFrostis();
        reliquias.add(frostis);
        createNecromante();
        reliquias.add(necromante);
        createAlquimista();
        reliquias.add(alquimista);
        createGolem();
        reliquias.add(golem);
        createDragao();
        reliquias.add(dragao);
        createMorte();
        reliquias.add(morte);
        createCarrasco();
        reliquias.add(carrasco);
        createLivro();
    }
    private static void createGuerreiro(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Guerreiro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","guerreiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"guerreiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        guerreiro = new Nexus(item,"guerreiro");
    }
    private static void createCeifador(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Ceifador"));
        meta.setItemModel(new NamespacedKey("nexus_texture","ceifador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"ceifador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        ceifador = new Nexus(item,"ceifador");
    }
    private static void createVida(){
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da vida"));
        meta.setItemModel(new NamespacedKey("nexus_texture","vida"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"vida");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        vida = new Nexus(item,"vida");
    }
    private static void createMares(){
        ItemStack item = new ItemStack(Material.TRIDENT,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus dos Mares"));
        meta.setItemModel(new NamespacedKey("nexus_texture","mares"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mares");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mares = new Nexus(item,"mares");
    }
    private static void createBarbaro(){
        ItemStack item = new ItemStack(Material.NETHERITE_AXE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Barbaro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","barbaro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"barbaro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        barbaro = new Nexus(item,"barbaro");
    }
    private static void createFazendeiro(){
        ItemStack item = new ItemStack(Material.NETHERITE_HOE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Fazendeiro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","fazendeiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fazendeiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fazendeiro = new Nexus(item,"fazendeiro");
    }
    private static void createEspiao(){
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Espiao"));
        meta.setItemModel(new NamespacedKey("nexus_texture","espiao"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"espiao");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        espiao = new Nexus(item,"espiao");
    }
    private static void createArqueiro(){
        ItemStack item = new ItemStack(Material.BOW,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Arqueiro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","arqueiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"arqueiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        arqueiro = new Nexus(item,"arqueiro");
    }
    private static void createCacador(){
        ItemStack item = new ItemStack(Material.CROSSBOW,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Caçador"));
        meta.setItemModel(new NamespacedKey("nexus_texture","cacador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"cacador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        cacador = new Nexus(item,"cacador");
    }
    private static void createTempestade(){
        ItemStack item = new ItemStack(Material.MACE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da Tempestade"));
        meta.setItemModel(new NamespacedKey("nexus_texture","tempestade"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"tempestade");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        tempestade = new Nexus(item,"tempestade");
    }
    private static void createMineiro(){
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Mineiro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","mineiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.addAttributeModifier(Attribute.SCALE,new AttributeModifier(new NamespacedKey("nexus_passiva","tamanho"),-0.25, AttributeModifier.Operation.ADD_NUMBER));
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mineiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mineiro = new Nexus(item,"mineiro");
    }
    private static void createFenix(){
        ItemStack item = new ItemStack(Material.ELYTRA,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da Fenix"));
        meta.setItemModel(new NamespacedKey("nexus_texture","fenix"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"fenix");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        fenix = new Nexus(item,"fenix");
    }
    private static void createProtetor(){
        ItemStack item = new ItemStack(Material.SHIELD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Protetor"));
        meta.setItemModel(new NamespacedKey("nexus_texture","protetor"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"protetor");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        protetor = new Nexus(item,"protetor");
    }
    private static void createHulk(){
        ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Hulk"));
        meta.setItemModel(new NamespacedKey("nexus_texture","hulk"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.addAttributeModifier(Attribute.SCALE,new AttributeModifier(new NamespacedKey("nexus_hulk","tamanho"),+0.25, AttributeModifier.Operation.ADD_NUMBER));
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"hulk");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        hulk = new Nexus(item,"hulk");
    }
    private static void createSculk(){
        ItemStack item = new ItemStack(Material.ECHO_SHARD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Sculk"));
        meta.setItemModel(new NamespacedKey("nexus_texture","sculk"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"sculk");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        sculk = new Nexus(item,"sculk");
    }
    private static void createPescador(){
        ItemStack item = new ItemStack(Material.FISHING_ROD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Pescador"));
        meta.setItemModel(new NamespacedKey("nexus_texture","pescador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"pescador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        pescador = new Nexus(item,"pescador");
    }
    private static void createFlash(){
        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Flash"));
        meta.setItemModel(new NamespacedKey("nexus_texture","flash"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"flash");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        flash = new Nexus(item,"flash");
    }
    private static void createMago(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Mago"));
        meta.setItemModel(new NamespacedKey("nexus_texture","mago_base"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"mago");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        mago = new Nexus(item,"mago");
    }
    private static void createLadrao(){
        ItemStack item = new ItemStack(Material.BLACK_BUNDLE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Ladrão"));
        meta.setItemModel(new NamespacedKey("nexus_texture","ladrao"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"ladrao");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        ladrao = new Nexus(item,"ladrao");
    }
    private static void createDomador(){
        ItemStack item = new ItemStack(Material.NETHERITE_SHOVEL,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Domador"));
        meta.setItemModel(new NamespacedKey("nexus_texture","domador"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"domador");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        domador = new Nexus(item,"domador");
    }
    private static void createCozinheiro(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Cozinheiro"));
        meta.setItemModel(new NamespacedKey("nexus_texture","cozinheiro"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"cozinheiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        cozinheiro = new Nexus(item,"cozinheiro");
    }
    private static void createConstrutor(){
        ItemStack item = new ItemStack(Material.MACE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Construtor"));
        meta.setItemModel(new NamespacedKey("nexus_texture","construtor"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"construtor");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        construtor = new Nexus(item,"construtor");
    }
    private static void createAbissal(){
        ItemStack item = new ItemStack(Material.NETHER_STAR,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Abissal"));
        meta.setItemModel(new NamespacedKey("nexus_texture","abissal"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"abissal");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        abissal = new Nexus(item,"abissal");
    }
    private static void createCronosombra(){
        ItemStack item = new ItemStack(Material.CLOCK,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus da Cronosombra"));
        meta.setItemModel(new NamespacedKey("nexus_texture","cronosombra"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"cronosombra");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        cronosombra = new Nexus(item,"cronosombra");
    }
    private static void createAssassino(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Assassino"));
        meta.setItemModel(new NamespacedKey("nexus_texture","assassino"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"assassino");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        assassino = new Nexus(item,"assassino");
    }
    private static void createFrostis(){
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Frostis"));
        meta.setItemModel(new NamespacedKey("nexus_texture","frostis"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"frostis");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        frostis = new Nexus(item,"frostis");
    }
    private static void createNecromante(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Necromante"));
        meta.setItemModel(new NamespacedKey("nexus_texture","necromante"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"necromante");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        necromante = new Nexus(item,"necromante");
    }
    private static void createAlquimista(){
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Alquimista"));
        meta.setItemModel(new NamespacedKey("nexus_texture","alquimista"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"alquimista");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        alquimista = new Nexus(item,"alquimista");
    }
    private static void createGolem(){
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Golem"));
        meta.setItemModel(new NamespacedKey("nexus_texture","golem"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"golem");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        golem = new Nexus(item,"golem");
    }
    private static void createDragao(){
        ItemStack item = new ItemStack(Material.DRAGON_HEAD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Dragão"));
        meta.setItemModel(new NamespacedKey("nexus_texture","dragao"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"dragao");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        dragao = new Nexus(item,"dragao");
    }
    private static void createMorte(){
        ItemStack item = new ItemStack(Material.GHAST_TEAR,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§4Nexus da Morte"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.EPIC);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"morte");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        morte = new Nexus(item,"morte");
    }
    private static void createCarrasco(){
        ItemStack item = new ItemStack(Material.GOLDEN_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§eEspada do Carrasco"));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING, "carrasco");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING, "server");
        item.setItemMeta(meta);
        carrasco = new Nexus(item, "carrasco");
    }
    private static void createLivro(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK,1);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.displayName(Component.text("§6Tutorial"));
        meta.setUnbreakable(true);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.RARE);
        meta.setItemModel(Material.KNOWLEDGE_BOOK.getKey());
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"nexus");
        item.setItemMeta(meta);
        livro = new Nexus(item,"livro");
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
            case "pescador" -> pescador;
            case "flash" -> flash;
            case "mago" -> mago;
            case "ladrao" -> ladrao;
            case "domador" -> domador;
            case "cozinheiro" -> cozinheiro;
            case "construtor" -> construtor;
            case "abissal" -> abissal;
            case "cronosombra" -> cronosombra;
            case "assassino" -> assassino;
            case "frostis" -> frostis;
            case "necromante" -> necromante;
            case "alquimista" -> alquimista;
            case "golem" -> golem;
            case "dragao" -> dragao;
            case "morte" -> morte;
            case "carrasco" -> carrasco;
            default -> null;
        };
    }
}