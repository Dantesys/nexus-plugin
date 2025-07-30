package org.dantesys.reliquiasNexus.items;

import org.bukkit.Material;
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
    private int maxLevel = 7;
    private final List<Material> materialLevel;
    private final List<PotionEffectType> passiva;
    private final ItemStack itemBase;
    private final Attribute attribute;
    public Nexus(ItemStack stack,List<Material> mateirais,List<PotionEffectType> passiva,String nome,Attribute attribute){
        this.item=stack;
        this.materialLevel = mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
        this.attribute=attribute;
    }
    public Nexus(ItemStack stack, int max, List<Material> mateirais,List<PotionEffectType> passiva,String nome,Attribute attribute){
        this.item=stack;
        this.maxLevel=max;
        this.materialLevel=mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
        this.attribute=attribute;
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
    public ItemStack getItem(){
        return this.item;
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
    public boolean upgrade(){
        if(podeUpar()){
            this.level++;
            this.item = this.item.withType(getMaterialPorLevel());
            ItemMeta item = this.item.getItemMeta();
            item.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey(),+this.level, AttributeModifier.Operation.ADD_NUMBER));
            return true;
        }
        return false;
    }
    private Material getMaterialPorLevel(){
        return this.materialLevel.get(this.level-1);
    }

}