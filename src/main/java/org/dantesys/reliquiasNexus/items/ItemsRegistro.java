package org.dantesys.reliquiasNexus.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
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
    private static final List<Nexus> reliquias = new ArrayList<>();
    public static void init(){
        createGuerreiro();
        reliquias.add(guerreiro);
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
        ItemStack item = new ItemStack(Material.WOODEN_SWORD,1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Nexus do Guerreiro"));
        meta.setUnbreakable(true);
        meta.setGlider(true);
        meta.setRarity(ItemRarity.RARE);
        meta.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"guerreiro");
        meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING,"");
        item.setItemMeta(meta);
        guerreiro = new Nexus(item,6,mat,efeitos,"guerreiro", Attribute.ATTACK_DAMAGE);
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
            default -> null;
        };
    }
}