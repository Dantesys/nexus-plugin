package org.dantesys.reliquiasNexus.eventos;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.Collection;
import java.util.Map;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class GuerreiroEvent implements Listener {
    Map<Integer, EntityType> mobsPorLevel = Map.of(
            1, EntityType.ZOMBIE,
            2, EntityType.SKELETON,
            3, EntityType.CREEPER,
            4, EntityType.ENDERMAN,
            5, EntityType.PIGLIN_BRUTE,
            6, EntityType.WITHER_SKELETON
    );
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;
        ItemStack stack = killer.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if(nome==null || !nome.equals("guerreiro")) return;
            PersistentDataContainer data = killer.getPersistentDataContainer();
            int kills = data.getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
            int level = data.getOrDefault(GUERREIRO.key, PersistentDataType.INTEGER, 1);
            if(event.getEntity().getType().equals(mobsPorLevel.get(level))){
                data.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, kills + 1);
                tentarEvoluir(killer,stack,level);
            }
        }
    }
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
        int killsRequeridos = 5 * levelAtual;
        if (podeEvoluir(player, levelAtual) && data.has(NEXUS.key,PersistentDataType.STRING)) {
            player.giveExp(-10 * levelAtual);
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            dataPlayer.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
            dataPlayer.set(GUERREIRO.key,PersistentDataType.INTEGER,levelAtual+1);
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            ItemMeta metinha = nexusItem.getItemMeta();
            if(nome!=null && !nome.isBlank()){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    nexusItem=n.getItem(levelAtual+1);
                    nexusItem.editMeta(consumer -> {
                        metinha.getEnchants().forEach((enchantment, integer) -> {
                            consumer.addEnchant(enchantment,integer,false);
                        });
                        metinha.getAttributeModifiers().forEach(consumer::addAttributeModifier);
                    });
                    nexusItem.setItemMeta(metinha);
                    player.getInventory().setItemInMainHand(nexusItem);
                    player.sendMessage("§aSeu Nexus do Guerreiro evoluiu para o nível " + (levelAtual + 1) + "!");
                }
            }
        } else {
            player.sendMessage("§cVocê precisa de "+(10*levelAtual)+" leveis XP ou derrotar mais "+(killsRequeridos-kills)+" "+mobsPorLevel.get(levelAtual).name()+" para evoluir sua relíquia.");
        }
    }
    private boolean podeEvoluir(Player player, int levelAtual) {
        int xpRequerido = 10 * levelAtual;
        int killsRequeridos = 5 * levelAtual;
        int kills = player.getPersistentDataContainer().getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
        return player.getTotalExperience() >= xpRequerido && kills >= killsRequeridos;
    }
    @EventHandler
    public void corte(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(player.isSneaking() && container.has(SPECIAL.key, PersistentDataType.INTEGER) && stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            int tempo = container.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                Nexus item = ItemsRegistro.getFromNome(nome);
                if(tempo<=0 && item!=null && item.equals(ItemsRegistro.guerreiro)){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int l = dataPlayer.getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
                    item.setLevel(l);
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
}
