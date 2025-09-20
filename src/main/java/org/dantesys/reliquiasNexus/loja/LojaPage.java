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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if(item!=null && item.getPlayer()!=null){
            double preco = item.getPreco(true);
            double saldo = player.getPersistentDataContainer().getOrDefault(SALDO.key, PersistentDataType.DOUBLE,0.0);
            if(saldo>=preco){
                if (player.getInventory().firstEmpty() != -1){
                    UUID uuid = UUID.fromString(item.getPlayer());
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
                        donoPlayer.sendMessage(Component.text(ReliquiasNexus.getLang().getString("loja.vendeu","Você vendeu <item> por <valor>").replace("<item>",nome).replace("<valor>",precoStr)).color(NamedTextColor.GREEN));
                    }else{
                        File file = new File(ReliquiasNexus.getPlugin(ReliquiasNexus.class).getDataFolder(), "vendas.yml");
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        double pendente = config.getDouble(uuid.toString(), 0.0);
                        config.set(uuid.toString(), pendente + preco);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
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
    public LojaItem getItem(int slot){
        return switch(slot){
            case 10 -> itens.get((numero * 21));
            case 11 -> itens.get(1+(numero * 21));
            case 12 -> itens.get(2+(numero * 21));
            case 13 -> itens.get(3+(numero * 21));
            case 14 -> itens.get(4+(numero * 21));
            case 15 -> itens.get(5+(numero * 21));
            case 16 -> itens.get(6+(numero * 21));
            case 19 -> itens.get(7+(numero * 21));
            case 20 -> itens.get(8+(numero * 21));
            case 21 -> itens.get(9+(numero * 21));
            case 22 -> itens.get(10+(numero * 21));
            case 23 -> itens.get(11+(numero * 21));
            case 24 -> itens.get(12+(numero * 21));
            case 25 -> itens.get(13+(numero * 21));
            case 28 -> itens.get(14+(numero * 21));
            case 29 -> itens.get(15+(numero * 21));
            case 30 -> itens.get(16+(numero * 21));
            case 31 -> itens.get(17+(numero * 21));
            case 32 -> itens.get(18+(numero * 21));
            case 33 -> itens.get(19+(numero * 21));
            case 34 -> itens.get(20+(numero * 21));
            default -> null;
        };
    }
    public void updateItens(List<LojaItem> itens){
        if(itens==null || itens.isEmpty()){
            List<Map<String, Object>> itensSalvosPlayers = (List<Map<String, Object>>) ReliquiasNexus.getLoja().getList("players");
            if(itensSalvosPlayers == null) return;
            List<LojaItem> lojaPlayer = new ArrayList<>();
            for(Map<String, Object> map : itensSalvosPlayers){
                ItemStack item = (ItemStack) map.get("item");
                double preco = 0;
                Object precoObj = map.get("preco");
                Object playerObj = map.get("player");
                String uuid = null;
                if(precoObj instanceof Double){
                    preco = (Double) precoObj;
                } else if(precoObj instanceof Integer){
                    preco = ((Integer) precoObj).doubleValue();
                }
                if(playerObj instanceof String){
                    uuid= (String) playerObj;
                }
                LojaItem lojaItem = new LojaItem(item,preco,uuid);
                lojaPlayer.add(lojaItem);
            }
            this.itens=lojaPlayer;
        }else{
            this.itens=itens;
        }
    }
    public void showPage(Inventory inv){
        if(itens!=null && !itens.isEmpty()){
            int maxPage = itens.size()/21;
            List<LojaItem> sub = itens.subList(numero==maxPage && maxPage<=1?0:numero*21,numero==maxPage?itens.size():(21+(numero*21)));
            int slot=10;
            int cont=1;
            for(LojaItem item: sub){
                ItemStack show = item.getItem().clone();
                ItemMeta meta = show.getItemMeta();
                String precoStr = String.format("Preço de $ %.2f "+ReliquiasNexus.getNexusConfig().getString("recursos.moneyName","moly"), item.getPreco(true));
                List<Component>lore=List.of(Component.text(precoStr));
                meta.lore(lore);
                show.setItemMeta(meta);
                inv.setItem(slot,show);
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
