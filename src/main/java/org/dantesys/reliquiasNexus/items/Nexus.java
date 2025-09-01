package org.dantesys.reliquiasNexus.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public class Nexus{
    private final String nome;
    private ItemStack item;
    private int level = 1;
    private final ItemStack itemBase;
    public Nexus(ItemStack stack,String nome){
        this.item=stack;
        this.itemBase=stack;
        this.nome=nome;
    }

    public String getNome(){
        return this.nome;
    }
    public ItemStack getItem(int level){
        int max = ReliquiasNexus.getNexusConfig().getInt("levelMax");
        if(max>1 && level>max){
            level=max;
        }
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
        int max = ReliquiasNexus.getNexusConfig().getInt("levelMax");
        if(l>level){
            if(l>max && max>1){
                l=max;
            }
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
    }

    public void aplicaEfeitoPassivo(Player player, int level) {

    }
}