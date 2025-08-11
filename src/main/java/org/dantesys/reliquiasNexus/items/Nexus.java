package org.dantesys.reliquiasNexus.items;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Nexus{
    private final String nome;
    private ItemStack item;
    private int level = 1;
    private final List<PotionEffectType> passiva;
    private final ItemStack itemBase;
    private final Attribute attribute;
    public Nexus(ItemStack stack, List<PotionEffectType> passiva,String nome,Attribute attribute){
        this.item=stack;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
        this.attribute=attribute;
    }
    public List<PotionEffect> getEfeitos(){
        List<PotionEffect> efeitos = new ArrayList<>();
        for(PotionEffectType efeito:passiva){
            efeitos.add(new PotionEffect(efeito,(level*20)+200,2));
        }
        return efeitos;
    }

    public String getNome(){
        return this.nome;
    }
    public ItemStack getItem(int level){
        if(level==1){
            return this.itemBase;
        }
        this.item = this.itemBase;
        for(int i=2;i<level;i++){
            upgrade();
        }
        return this.item;
    }
    public void setLevel(int l){
        if(l>level){
            for(int i=level;i<l;i++){
                upgrade();
            }
        }else if(l<level){
            if(l==1){
                this.item=this.itemBase;
            }else{
                this.level=1;
                for(int i=1;i<l;i++){
                    upgrade();
                }
            }
        }
        this.level=l;
    }
    public void upgrade(){
        this.level=level+1;
        ItemMeta item = this.item.getItemMeta();
        NamespacedKey key = new NamespacedKey("nexus_leveled", "boost_lvl_" + this.level);
        item.addAttributeModifier(attribute, new AttributeModifier(key, level, AttributeModifier.Operation.ADD_NUMBER));
        this.item.setItemMeta(item);
    }
}