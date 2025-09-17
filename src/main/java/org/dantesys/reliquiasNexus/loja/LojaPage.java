package org.dantesys.reliquiasNexus.loja;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.LOJAPLAYER;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SALDO;

public class LojaPage {
    private int numero;
    private List<LojaItem> itens;
    private final ItemStack back;
    private final ItemStack next;

    public LojaPage(List<LojaItem> itens, ItemStack back, ItemStack next) {
        this.itens = itens;
        this.back = back;
        this.next = next;
        this.numero=0;
    }

    public LojaPageResult comprar(LojaItem item, Player player){
        if(item!=null){
            double preco = item.getPreco(true);
            double saldo = player.getPersistentDataContainer().getOrDefault(SALDO.key, PersistentDataType.DOUBLE,0.0);
            if(saldo>=preco){
                if (player.getInventory().firstEmpty() != -1){
                    ItemMeta meta = item.getItem().getItemMeta();
                    String uuidStr = meta.getPersistentDataContainer().get(LOJAPLAYER.key,PersistentDataType.STRING);
                    if(uuidStr!=null){
                        UUID uuid = UUID.fromString(uuidStr);
                        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                        Player donoPlayer = p.getPlayer();
                        if(p.isOnline() && donoPlayer!=null){
                            double saldoDono = donoPlayer.getPersistentDataContainer()
                                    .getOrDefault(SALDO.key, PersistentDataType.DOUBLE, 0.0);
                            donoPlayer.getPersistentDataContainer()
                                    .set(SALDO.key, PersistentDataType.DOUBLE, saldoDono + preco);
                            Component nomeComp = item.getItem().displayName();
                            String nome = PlainTextComponentSerializer.plainText().serialize(nomeComp);
                            String precoStr = String.format("%.2f", item.getPreco(true));
                            donoPlayer.sendMessage(Component.text(ReliquiasNexus.getLang().getString("loja.vendeu","Você vendeu <item> por <valor>").replace("<item>",nome).replace("valor>",precoStr)).color(NamedTextColor.GREEN));
                        }else{
                            File file = new File(ReliquiasNexus.getPlugin(ReliquiasNexus.class).getDataFolder(), "vendas.yml");
                            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                            double pendente = config.getDouble(uuidStr, 0.0);
                            config.set(uuidStr, pendente + preco);
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    player.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,saldo-preco);
                    player.getInventory().addItem(item.getItem());
                    itens.remove(item);
                    return LojaPageResult.SUCCESS;
                }else return LojaPageResult.INVFULL;
            }else return LojaPageResult.SEMSALDO;
        }
        return LojaPageResult.NULO;
    }
    public LojaItem getItem(ItemStack item){
        for(LojaItem loja:itens){
            if(loja.getItem().equals(item)){
                return loja;
            }
        }
        return null;
    }
    public void updateItens(List<LojaItem> itens){
        this.itens=itens;
    }
    public void showPage(Inventory inv){
        if(!itens.isEmpty()){
            int maxPage = itens.size()/21;
            List<LojaItem> sub = itens.subList(numero==maxPage && maxPage<=1?0:numero*21,numero==maxPage?itens.size():(21+(numero*21)));
            int slot=10;
            int cont=1;
            for(LojaItem item: sub){
                inv.setItem(slot,item.getItem());
                cont++;
                if(cont>7){
                    slot+=3;
                    cont=1;
                }else{
                    slot++;
                }
            }
            if(hasBack()){
                inv.setItem(18,back);
            }
            if(hasNext()){
                inv.setItem(26,next);
            }
        }
    }
    public boolean hasNext(){
        return numero<itens.size()/21;
    }
    public boolean hasBack(){
        return numero>0;
    }
    public void nextPage(){
        if(numero<itens.size()/21)numero++;
    }
    public void backPage(){
        if(numero>0)numero--;
    }
}
