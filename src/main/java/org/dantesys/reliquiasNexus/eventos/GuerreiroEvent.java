package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Collection;

import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class GuerreiroEvent implements Listener {
    @EventHandler
    public void corte(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && container.has(SPECIAL.key, PersistentDataType.INTEGER) && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.get(SPECIAL.key,PersistentDataType.INTEGER);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            Nexus item = ItemsRegistro.getFromNome(nome);
            if(tempo<=0 && item!=null && item.equals(ItemsRegistro.guerreiro)){
                int range = 10*item.getLevel();
                double damage = 10*item.getLevel();
                final int finalRange = range;
                final double finalDamage = damage;
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
                    location.getWorld().spawnParticle(Particle.SWEEP_ATTACK,location,1,0,0,0,0);
                    location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP,0.5f,0.7f);
                    Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
                    while(pressf.iterator().hasNext()){
                        Entity surdo = pressf.iterator().next();
                        if(surdo instanceof LivingEntity vivo){
                            if(vivo instanceof Player pl){
                                if(pl != player){
                                    vivo.damage(finalDamage);
                                }
                            }else{
                                vivo.damage(finalDamage);
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
                container.set(SPECIAL.key,PersistentDataType.INTEGER,(item.getMaxLevel()/item.getLevel())*60);
            }
        }
    }
}
