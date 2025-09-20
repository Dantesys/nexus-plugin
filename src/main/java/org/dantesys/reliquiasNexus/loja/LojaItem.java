package org.dantesys.reliquiasNexus.loja;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LojaItem {
    private final ItemStack item;
    private final double precoBase;
    private int estoque;
    private double precoVenda;
    private String player;
    public LojaItem(ItemStack item,double preco){
        this.item=item;
        this.precoBase=preco;
        this.player=null;
    }
    public LojaItem(ItemStack item,double preco,String player){
        this(item,preco);
        this.player=player;
    }
    public String getPlayer(){
        return this.player;
    }
    public double getPreco(boolean base){
        return base?precoBase:precoVenda;
    }
    public ItemStack getItem(){
        return this.item;
    }
    public int getEstoque(){
        return this.estoque;
    }
    public void compra(){
        estoque--;
    }
    public void gerarVenda(){
        this.estoque = (int) (1+(Math.random()*20));
        double variacao = 0.95+(Math.random()*0.1);
        this.precoVenda=Math.round(precoBase*variacao*100.0)/100.0;
    }
}
