package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class JoinQuitEvent implements Listener {
    private final ReliquiasNexus plugin;
    public JoinQuitEvent(ReliquiasNexus plugin){
        this.plugin=plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
        boolean novato = container.getOrDefault(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,true);
        container.set(SPECIAL.key,PersistentDataType.INTEGER,qtd);
        if(qtd==0 && novato){
            container.set(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,false);
            List<Nexus> reliquias = ItemsRegistro.getValidReliquia(ReliquiasNexus.getNexusConfig());
            Random rng = new Random();
            int escolhido = rng.nextInt(reliquias.size());
            Nexus n = reliquias.get(escolhido);
            String nome = n.getNome();
            ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
            plugin.saveConfig();
            container.set(QTD.key,PersistentDataType.INTEGER,1);
            int level =1;
            NamespacedKey key = NexusKeys.getKey(nome);
            if(key!=null && container.has(key,PersistentDataType.INTEGER)){
                level=container.getOrDefault(key,PersistentDataType.INTEGER,1);
            }else if(key!=null){
                container.set(key,PersistentDataType.INTEGER,1);
            }
            ItemStack stack = n.getItem(level);
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);
            player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
            String msg=ReliquiasNexus.getLang().getString("joinquit.joinnew");
            if(msg==null){
                msg="Bem-vindo ao jogo, Jogador <player>";
            }
            msg=msg.replace("<player>",player.getName());
            event.joinMessage(Component.text("§2"+msg));
            String r=ReliquiasNexus.getLang().getString("joinquit.relic");
            if(r==null){
                r="Você recebeu a reliquia do <relic>";
            }
            r=r.replace("<relic>",nome);
            player.sendMessage(Component.text("§2"+r));
        }else{
            String msg=ReliquiasNexus.getLang().getString("joinquit.join");
            if(msg==null){
                msg="Bem-vindo devolta, Jogador <player>";
            }
            msg=msg.replace("<player>",player.getName());
            event.joinMessage(Component.text("§2"+msg));
        }
        setAtributoJoin(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ReliquiasNexus.saiu(player);
        String msg=ReliquiasNexus.getLang().getString("joinquit.quit");
        if(msg==null){
            msg="O Jogador <player> saiu do jogo!";
        }
        msg=msg.replace("<player>",player.getName());
        event.quitMessage(Component.text("§4"+msg));
    }
    private void setAtributoJoin(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        List<NamespacedKey> keys = NexusKeys.getKeyLevel();
        for(NamespacedKey key: keys){
            if(dataPlayer.has(key,PersistentDataType.INTEGER)){
                String nome = key.getKey();
                int level = dataPlayer.getOrDefault(key,PersistentDataType.INTEGER,1);
                switch (nome){
                    case "barbaro", "guerreiro" -> player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(level-1);
                    case "ceifador" -> player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+level-1);
                    case "fazendeiro" -> player.getAttribute(Attribute.LUCK).setBaseValue(level);
                    case "mares" -> {
                        player.getAttribute(Attribute.SUBMERGED_MINING_SPEED).setBaseValue(0.2+(level/10));
                        player.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(level/10);
                    }
                    case "vida" -> player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+((level-1)*2));
                    case "espiao" -> player.getAttribute(Attribute.SCALE).setBaseValue(1-(level*0.025));
                    case "arqueiro" -> player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.3+(level*0.035));
                    case "cacador" -> player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.6+(level*0.7));
                    case "tempestade" -> player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+level);
                    case "mineiro" -> {
                        player.getAttribute(Attribute.MINING_EFFICIENCY).setBaseValue(level);
                        player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(1+level);
                        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+level);
                    }
                    case "fenix" -> {
                        player.getAttribute(Attribute.ARMOR).setBaseValue(level);
                        player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(level);
                    }
                    case "protetor" -> player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+level);
                    case "hulk" -> {
                        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+(level/2));
                        player.getAttribute(Attribute.ARMOR).setBaseValue(level/2);
                        player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(level/2);
                        player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+(level/2));
                        player.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue((level/10));
                        player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42+(level/10));
                        player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue((level/20));
                        player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+(level*2));
                        player.getAttribute(Attribute.SCALE).setBaseValue(1+(level*0.025));
                        player.getAttribute(Attribute.SWEEPING_DAMAGE_RATIO).setBaseValue(level*0.05);
                    }
                    case "sculk" -> {
                        player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.3+(level*0.035));
                        player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(60000000-(level*3000000));
                    }
                    case "pescador" -> player.getAttribute(Attribute.LUCK).setBaseValue(level*51);
                    case "flash" -> {
                        player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4+level);
                        player.getAttribute(Attribute.MOVEMENT_EFFICIENCY).setBaseValue(level*0.05);
                        player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.245*level+0.7);
                        player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.035*level+0.3);
                        player.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0.007*level+0.6);
                    }
                    case "mago" -> player.getAttribute(Attribute.MAX_ABSORPTION).setBaseValue(level);
                    case "ladrao","domador" -> player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+level);
                }
            }
        }
    }
}