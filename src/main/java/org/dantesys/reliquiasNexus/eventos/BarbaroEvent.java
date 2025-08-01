package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import java.util.Map;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class BarbaroEvent implements Listener {
    Map<Integer, EntityType> mobsPorLevel = Map.of(
            1, EntityType.PILLAGER,
            2, EntityType.VINDICATOR,
            3, EntityType.EVOKER,
            4, EntityType.RAVAGER,
            5, EntityType.PIGLIN_BRUTE,
            6, EntityType.WARDEN
    );
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;
        ItemStack stack = killer.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if(nome==null || !nome.equals("barbaro")) return;
            PersistentDataContainer data = killer.getPersistentDataContainer();
            int kills = data.getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
            int level = data.getOrDefault(BARBARO.key, PersistentDataType.INTEGER, 1);
            if(event.getEntity().getType().equals(mobsPorLevel.get(level))){
                data.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, kills + 1);
                tentarEvoluir(killer,stack,level);
            }
        }
    }
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.giveExp(-10 * levelAtual);
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
            dataPlayer.set(BARBARO.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    if(meta.hasEnchants()){
                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                    }
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus do Barbaro evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP ou derrotar mais "+(levelAtual -kills)+" "+mobsPorLevel.get(levelAtual).name()+" para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
        return player.getTotalExperience() >= xpRequerido && kills >= levelAtual;
    }
    @EventHandler
    public void furia(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && container.has(SPECIAL.key, PersistentDataType.INTEGER) && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus item = ItemsRegistro.getFromNome(nome);
                if(tempo<=0 && item!=null && item.equals(ItemsRegistro.barbaro)){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int l = dataPlayer.getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
                    item.setLevel(l);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,200*l,9));
                    container.set(SPECIAL.key,PersistentDataType.INTEGER,(item.getMaxLevel()/item.getLevel())*60);
                }
            }
        }
    }
}
