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
    public Nexus(ItemStack stack,List<Material> mateirais,List<PotionEffectType> passiva,String nome){
        this.item=stack;
        this.materialLevel = mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
    }
    public Nexus(ItemStack stack, int max, List<Material> mateirais,List<PotionEffectType> passiva,String nome){
        this.item=stack;
        this.maxLevel=max;
        this.materialLevel=mateirais;
        this.itemBase=stack;
        this.nome=nome;
        this.passiva = passiva;
    }
    public List<PotionEffect> getEfeitos(){
        List<PotionEffect> efeitos = new ArrayList<>();
        for(PotionEffectType efeito:passiva){
            int aplificador = (level/maxLevel)*4;
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
        return level>1? this.item: this.itemBase;
    }
    public boolean upgrade(Attribute attribute){
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