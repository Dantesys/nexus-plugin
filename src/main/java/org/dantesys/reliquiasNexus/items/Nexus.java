package org.dantesys.reliquiasNexus.items;

import org.bukkit.Material;
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
    private final int maxLevel;
    private final List<Material> materialLevel;
    private final List<Material> visual;
    private final List<PotionEffectType> passiva;
    private final ItemStack itemBase;
    private final Attribute attribute;
    public Nexus(ItemStack stack, int max, List<Material> mateirais,List<PotionEffectType> passiva,String nome,Attribute attribute,List<Material> visual){
        this.item=stack;
        this.maxLevel=max;
        this.materialLevel=mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
        this.attribute=attribute;
        this.visual=visual;
    }
    public Nexus(ItemStack stack, int max, List<Material> mateirais,List<PotionEffectType> passiva,String nome,Attribute attribute){
        this.item=stack;
        this.maxLevel=max;
        this.materialLevel=mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
        this.attribute=attribute;
        this.visual=this.materialLevel;
    }
    public List<PotionEffect> getEfeitos(){
        List<PotionEffect> efeitos = new ArrayList<>();
        for(PotionEffectType efeito:passiva){
            int aplificador = (level/maxLevel)*3;
            efeitos.add(new PotionEffect(efeito,level*20,aplificador));
        }
        return efeitos;
    }
    public int getLevel(){
        return this.level;
    }
    public int getMaxLevel(){
        return this.maxLevel;
    }
    public String getNome(){
        return this.nome;
    }
    private boolean podeUpar(){
        return this.level<this.maxLevel;
    }
    public ItemStack getItem(int level){
        if(level==1){
            return this.itemBase;
        }
        this.item = this.itemBase;
        for(int i=1;i<level;i++){
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
        if(podeUpar()){
            this.level++;
            this.item = this.item.withType(getMaterialPorLevel());
            ItemMeta item = this.item.getItemMeta();
            item.setItemModel(getVisualPorLevel());
            item.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey(),this.level, AttributeModifier.Operation.ADD_NUMBER));
            this.item.setItemMeta(item);
        }
    }
    private Material getMaterialPorLevel(){
        return this.materialLevel.get(this.level-1);
    }
    private NamespacedKey getVisualPorLevel(){
        return this.visual.get(this.level-1).getKey();
    }

}