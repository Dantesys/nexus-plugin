package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

public class FazendeiroEvent implements Listener {
    Map<Integer, Material> upador = Map.of(
            1, Material.WHEAT,
            2, Material.BEETROOTS,
            3, Material.POTATOES,
            4, Material.CARROTS,
            5, Material.MELON,
            6, Material.PUMPKIN
    );
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
        int killsRequeridos = 5 * levelAtual;
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.giveExp(-10 * levelAtual);
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
            dataPlayer.set(FAZENDEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    if(meta.hasEnchants()){
                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                    }
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus do Fazendeiro evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP ou colher mais "+(killsRequeridos-kills)+" "+upador.get(levelAtual).name()+" para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        int killsRequeridos = 5 * levelAtual;
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
        return player.getTotalExperience() >= xpRequerido && kills >= killsRequeridos;
    }
    @EventHandler
    public void colher(BlockBreakEvent event){
        Block bloco = event.getBlock();
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank() && nome.equals("fazendeiro")){
                int level = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                if(bloco.getBlockData() instanceof Ageable ageable && ageable.getAge()==ageable.getMaximumAge()){
                    if(bloco.getType() == upador.get(level)){
                        int colher = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                        colher++;
                        dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, colher);
                        tentarEvoluir(player,stack,level);
                    }
                }
            }
        }
    }
    @EventHandler
    public void vouPlantar(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && container.has(SPECIAL.key, PersistentDataType.INTEGER) && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus item = ItemsRegistro.getFromNome(nome);
                if(tempo<=0 && item!=null && item.equals(ItemsRegistro.fazendeiro)){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int l = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                    item.setLevel(l);
                    final int finalRange = 5*l;
                    final Location location = player.getLocation();
                    final World world = player.getWorld();
                    final double damage = 5*l;
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                            ()->{
                            },()-> {
                    },(t)->{
                        double area = (double) finalRange /(t.getSegundosRestantes());
                        for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                            double x = (area * Math.cos(i)) + location.getX();
                            double z = (location.getZ() + area * Math.sin(i));
                            Location particle = new Location(world, x, location.getY() + 1, z);
                            world.spawnParticle(Particle.PALE_OAK_LEAVES,particle,1);
                        }
                        Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
                        while(pressf.iterator().hasNext()){
                            Entity surdo = pressf.iterator().next();
                            if(surdo instanceof LivingEntity vivo){
                                if(vivo instanceof Player p){
                                    if(p!=player)vivo.damage(damage);
                                }else if(vivo instanceof Boss){
                                    vivo.damage(damage);
                                }else if(vivo instanceof Tameable domado){
                                    if(!domado.isTamed()){
                                        Location locV = vivo.getLocation();
                                        world.dropItemNaturally(locV,new ItemStack(upador.get(l)));
                                        vivo.remove();
                                    }
                                }else{
                                    Location locV = vivo.getLocation();
                                    world.dropItemNaturally(locV,new ItemStack(upador.get(l)));
                                    vivo.remove();
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
