package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Collection;
import java.util.Map;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class MaresEvent implements Listener {
    Map<Integer, EntityType> mobsPorLevel = Map.of(
            1, EntityType.DROWNED,
            2, EntityType.GUARDIAN,
            3, EntityType.ELDER_GUARDIAN
    );
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;
        ItemStack stack = killer.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if(nome==null || !nome.equals("mares")) return;
            PersistentDataContainer data = killer.getPersistentDataContainer();
            int kills = data.getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
            int level = data.getOrDefault(MARES.key, PersistentDataType.INTEGER, 1);
            if(event.getEntity().getType().equals(mobsPorLevel.get(level))){
                data.set(MISSAOMARES.key, PersistentDataType.INTEGER, kills + 1);
                tentarEvoluir(killer,stack,level);
            }
        }
    }
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
        int killsRequeridos = 5 * levelAtual;
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.giveExp(-10 * levelAtual);
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
            dataPlayer.set(MARES.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    if(meta.hasEnchants()){
                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                    }
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus dos Mares evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP ou derrotar mais "+(killsRequeridos-kills)+" "+mobsPorLevel.get(levelAtual).name()+" para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        int killsRequeridos = 5 * levelAtual;
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
        return player.getTotalExperience() >= xpRequerido && kills >= killsRequeridos;
    }
    @EventHandler
    public void raio(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && container.has(SPECIAL.key, PersistentDataType.INTEGER) && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus item = ItemsRegistro.getFromNome(nome);
                if(tempo<=0 && item!=null && item.equals(ItemsRegistro.mares)){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int l = dataPlayer.getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
                    item.setLevel(l);
                    World w = event.getPlayer().getWorld();
                    w.setStorm(true);
                    w.setThundering(true);
                    final int finalRange = 30*l;
                    final double damage = 5*l;
                    final Location location = player.getLocation();
                    final World world = player.getWorld();
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                            ()->{
                            },()-> {
                    },(t)->{
                        double area = (double) finalRange /(t.getSegundosRestantes());
                        for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                            double x = (area * Math.cos(i)) + location.getX();
                            double z = (location.getZ() + area * Math.sin(i));
                            Location particle = new Location(world, x, location.getY() + 1, z);
                            world.spawnParticle(Particle.END_ROD,particle,1);
                        }
                        Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
                        while(pressf.iterator().hasNext()){
                            Entity surdo = pressf.iterator().next();
                            if(surdo instanceof LivingEntity vivo){
                                if(vivo instanceof Player p){
                                    if(p!=player){
                                        vivo.getWorld().strikeLightning(vivo.getLocation());
                                        vivo.setRemainingAir(0);
                                        vivo.damage(damage);
                                    }
                                }else{
                                    vivo.getWorld().strikeLightning(vivo.getLocation());
                                    vivo.setRemainingAir(0);
                                    vivo.damage(damage);
                                }
                            }
                            pressf.remove(surdo);
                        }
                    });
                    timer.scheduleTimer(1L);
                    container.set(SPECIAL.key,PersistentDataType.INTEGER,(item.getMaxLevel()/item.getLevel())*60);
                }
            }
        }
    }
}
