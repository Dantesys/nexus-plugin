package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Collection;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;
import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public class CeifadorEvent implements Listener {
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
        double recuperacaoNescessaria = 10 * levelAtual;
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.setLevel(player.getLevel()-(10*levelAtual));
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
            dataPlayer.set(CEIFADOR.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    if(meta.hasEnchants()){
                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                    }
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus do Ceifador evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP e roubar mais "+((int) recuperacaoNescessaria-recuperacao)+" de vida para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        double recuperacaoNescessaria = 10 * levelAtual;
        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
        return player.getLevel() >= xpRequerido && recuperacao >= recuperacaoNescessaria;
    }
    @EventHandler
    public void roubaVida(EntityDamageByEntityEvent event){
        Entity atacante = event.getDamager();
        if(atacante instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            PersistentDataContainerView data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("ceifador")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        double dano = event.getDamage();
                        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                        int level = player.getPersistentDataContainer().getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                        double cura = dano/2;
                        player.heal(cura);
                        recuperacao+=cura;
                        player.getPersistentDataContainer().set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, recuperacao);
                        tentarEvoluir(player,n.getItem(level),level);
                    }
                }
            }
        }
    }
    @EventHandler
    public void corteMortal(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus item = ItemsRegistro.getFromNome(nome);
                if(tempo<=0 && item!=null && item.equals(ItemsRegistro.ceifador)){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int l = dataPlayer.getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                    item.setLevel(l);
                    final int finalRange = 50;
                    final Location location = player.getLocation();
                    final Vector direction = location.getDirection().normalize();
                    final double[] tp = {0};
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                            ()->{
                            },()-> {
                    },(t)->{
                        tp[0] = tp[0]+3.4;
                        double x = direction.getX()*tp[0];
                        double y = direction.getY()*tp[0]+1.4;
                        double z = direction.getZ()*tp[0];
                        location.add(x,y,z);
                        location.getWorld().spawnParticle(Particle.SOUL,location,5,0,0,0,0);
                        location.getWorld().playSound(location, Sound.BLOCK_SOUL_SOIL_STEP,0.5f,0.7f);
                        Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
                        Random rd = new Random();
                        int pct = rd.nextInt(1,100);
                        while(pressf.iterator().hasNext()){
                            Entity surdo = pressf.iterator().next();
                            if(surdo instanceof LivingEntity vivo){
                                if(vivo instanceof Player pl){
                                    if(pct>99 && pl != player){
                                        vivo.setHealth(0);
                                        player.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
                                    }else if(pl != player){
                                        vivo.setHealth(1d);
                                    }
                                }else{
                                    if(pct>99){
                                        vivo.setHealth(0);
                                        player.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
                                    }else{
                                        vivo.setHealth(1d);
                                    }
                                }
                            }
                            pressf.remove(surdo);
                        }
                        location.subtract(x,y,z);
                        if(t.getSegundosRestantes()>finalRange){
                            t.stop();
                        }
                    });
                    timer.scheduleTimer(1L);
                    container.set(SPECIAL.key,PersistentDataType.INTEGER,120);
                }
            }
        }
    }
}
