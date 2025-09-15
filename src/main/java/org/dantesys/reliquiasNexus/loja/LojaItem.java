package org.dantesys.reliquiasNexus.loja;

import org.bukkit.inventory.ItemStack;

public class LojaItem {
    private final ItemStack item;
    private final double precoBase;
    private int estoque;
    private double precoVenda;
    public LojaItem(ItemStack item,double preco){
        this.item=item;
        this.precoBase=preco;
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
