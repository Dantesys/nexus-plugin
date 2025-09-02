package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.Cronosombra;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.Espiao;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.Flash;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class EvoluirEvent implements Listener {
    private final ReliquiasNexus plugin;

    public EvoluirEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }
    private void evo(String nome, int levelAtual,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        switch (nome){
            case "barbaro" -> {
                dataPlayer.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(BARBARO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+levelAtual);
            }
            case "ceifador" -> {
                dataPlayer.set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                dataPlayer.set(CEIFADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+levelAtual);
            }
            case "fazendeiro" -> {
                dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(FAZENDEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.LUCK).setBaseValue(levelAtual);
            }
            case "guerreiro" -> {
                dataPlayer.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(GUERREIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+levelAtual);
            }
            case "mares" -> {
                dataPlayer.set(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(MARES.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SUBMERGED_MINING_SPEED).setBaseValue(0.2+(levelAtual/10));
                player.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(levelAtual/10);
            }
            case "vida" -> {
                dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                dataPlayer.set(VIDA.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+(levelAtual*2));
            }
            case "espiao" -> {
                dataPlayer.set(MISSAOESPIAO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(ESPIAO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SCALE).setBaseValue(1-(levelAtual*0.025));
            }
            case "arqueiro" -> {
                dataPlayer.set(MISSAOARQUEIRO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(ARQUEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.3+(levelAtual*0.035));
            }
            case "cacador" -> {
                dataPlayer.set(MISSAOCACADOR.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(CACADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.6+(levelAtual*0.7));
            }
            case "tempestade" -> {
                dataPlayer.set(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(TEMPESTADE.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+levelAtual);
            }
            case "mineiro" -> {
                dataPlayer.set(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(MINEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MINING_EFFICIENCY).setBaseValue(levelAtual);
                player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(1+levelAtual);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "fenix" -> {
                dataPlayer.set(MISSAOFENIX.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(FENIX.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ARMOR).setBaseValue(levelAtual);
                player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(levelAtual);
            }
            case "protetor" -> {
                dataPlayer.set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(PROTETOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+levelAtual);
            }
            case "hulk" -> {
                dataPlayer.set(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                dataPlayer.set(HULK.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+(levelAtual/2));
                player.getAttribute(Attribute.ARMOR).setBaseValue(levelAtual/2);
                player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(levelAtual/2);
                player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+(levelAtual/2));
                player.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue((levelAtual/10));
                player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42+(levelAtual/10));
                player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue((levelAtual/20));
                player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+(levelAtual*2));
                player.getAttribute(Attribute.SCALE).setBaseValue(1+(levelAtual*0.025));
                player.getAttribute(Attribute.SWEEPING_DAMAGE_RATIO).setBaseValue(levelAtual*0.05);
            }
            case "sculk" -> {
                dataPlayer.set(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(SCULK.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.3+(levelAtual*0.035));
                player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(60000000-(levelAtual*3000000));
            }
            case "pescador" -> {
                dataPlayer.set(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(PESCADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.LUCK).setBaseValue(levelAtual*51);
            }
            case "flash" -> {
                dataPlayer.set(MISSAOFLASH.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(FLASH.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4+levelAtual);
                player.getAttribute(Attribute.MOVEMENT_EFFICIENCY).setBaseValue(levelAtual*0.05);
                player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(((0.245*levelAtual)/2)+0.7);
                player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(0.035*levelAtual+0.3);
                player.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0.007*levelAtual+0.6);
            }
            case "mago" -> {
                dataPlayer.set(MISSAOMAGO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(MAGO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_ABSORPTION).setBaseValue(levelAtual);
            }
            case "ladrao" -> {
                dataPlayer.set(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(LADRAO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+levelAtual);
            }
            case "domador" -> {
                dataPlayer.set(MISSAODOMADOR.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(DOMADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+levelAtual);
            }
            case "cozinheiro" -> {
                dataPlayer.set(MISSAOCOZINHEIRO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(COZINHEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+(levelAtual/4));
            }
            case "construtor" -> {
                dataPlayer.set(MISSAOCONSTRUTOR.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(CONSTRUTOR.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "abissal" -> {
                dataPlayer.set(MISSAOABISSAL.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(ABISSAL.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "cronosombra" -> {
                dataPlayer.set(MISSAOCRONOSOMBRA.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(CRONOSOMBRA.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "assassino" -> {
                dataPlayer.set(MISSAOASSASSINO.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(ASSASSINO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+levelAtual);
            }
            case "frostis" -> {
                dataPlayer.set(MISSAOFROSTIS.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(FROSTIS.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "necromante" -> {
                dataPlayer.set(MISSAONECROMANTE.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(NECROMANTE.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+levelAtual);
            }
            case "alquimista" -> {
                dataPlayer.set(MISSAOALQUIMISTA.key, PersistentDataType.INTEGER, 0);
                dataPlayer.set(ALQUIMISTA.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3+levelAtual);
                player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5+levelAtual);
            }
            case "golem" -> {
                dataPlayer.set(MISSAOGOLEM.key, PersistentDataType.DOUBLE, 0d);
                dataPlayer.set(GOLEM.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.ARMOR).setBaseValue(levelAtual);
                player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(levelAtual);
                player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+(levelAtual/3));
                player.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue((levelAtual/10));
                player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue((levelAtual/20));
                player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+(levelAtual*2));
                player.getAttribute(Attribute.SWEEPING_DAMAGE_RATIO).setBaseValue(levelAtual*0.05);
            }
            case "dragao" -> {
                dataPlayer.set(MISSAODRAGAO.key, PersistentDataType.DOUBLE, 0d);
                dataPlayer.set(DRAGAO.key,PersistentDataType.INTEGER,levelAtual+1);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20+levelAtual);
                player.getAttribute(Attribute.ARMOR).setBaseValue(levelAtual/5);
                player.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(levelAtual/5);
                player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(2+(levelAtual/5));
                player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42+(levelAtual/20));
                player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(3+(levelAtual*2));
                player.getAttribute(Attribute.SWEEPING_DAMAGE_RATIO).setBaseValue(levelAtual*0.05);
            }
        }
    }
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual,int slot) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        int xp = ReliquiasNexus.getNexusConfig().getInt("xptolevel");
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String msg = "";
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                String condicao = podeEvoluir(player,nome,levelAtual);
                int level = player.getLevel();
                if(condicao==null){
                    if(level>=levelAtual*xp){
                        player.setLevel(player.getLevel()-(xp*levelAtual));
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null){
                            int max = ReliquiasNexus.getNexusConfig().getInt("levelMax");
                            if(levelAtual+1<max){
                                nexusItem=n.getItem(levelAtual+1);
                                if(meta.hasEnchants()){
                                    meta.getEnchants().forEach((nexusItem::addEnchantment));
                                }
                                evo(nome,levelAtual,player);
                                player.getInventory().setItem(slot,nexusItem);
                                msg = ReliquiasNexus.getLang().getString("evo.sucesso");
                                if(msg==null){
                                    msg="Sua Reliquia do <relic> evoluiu para o nível <level>!";
                                }
                                msg=msg.replace("<relic>",nome);
                                msg=msg.replace("<level>",""+(levelAtual+1));
                                msg="§a"+msg;
                            }
                            else{
                                if(levelAtual+1==max){
                                    nexusItem=n.getItem(levelAtual+1);
                                    if(meta.hasEnchants()){
                                        meta.getEnchants().forEach((nexusItem::addEnchantment));
                                    }
                                    evo(nome,levelAtual,player);
                                    player.getInventory().setItem(slot,nexusItem);
                                }
                                msg = ReliquiasNexus.getLang().getString("evo.max");
                                if(msg==null){
                                    msg="Você chegou ao level máximo da sua reliquia <relic>!";
                                }
                                msg=msg.replace("<relic>",nome);
                                msg="§2"+msg;
                            }
                        }
                    }
                    else{
                        msg = ReliquiasNexus.getLang().getString("evo.needxp");
                        if(msg==null){
                            msg="Você precisa de mais <xp> leveis XP para evoluir sua reliquia do <relic>!";
                        }
                        msg=msg.replace("<relic>",nome);
                        msg=msg.replace("<xp>",""+(levelAtual*xp-level));
                        msg="§c"+msg;
                    }
                }
                else{
                    if(level>=levelAtual*xp){
                        msg = ReliquiasNexus.getLang().getString("evo.cond");
                        if(msg==null){
                            msg="Você precisa <condicao> para evoluir sua reliquia do <relic>!";
                        }
                        msg=msg.replace("<relic>",nome);
                        msg=msg.replace("<condicao>",condicao);
                        msg="§c"+msg;
                    }
                    else{
                        msg = ReliquiasNexus.getLang().getString("evo.condexp");
                        if(msg==null){
                            msg="Você precisa de mais <xp> leveis XP e <condicao> para evoluir sua reliquia do <relic>!";
                        }
                        msg=msg.replace("<relic>",nome);
                        msg=msg.replace("<condicao>",condicao);
                        msg=msg.replace("<xp>",""+(levelAtual*xp-level));
                        msg="§c"+msg;
                    }
                }
            }
            player.sendActionBar(Component.text(msg));
        }

    }
    private String podeEvoluir(Player player, String nome,int level){
        String condicao=null;
        switch (nome){
            case "barbaro" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.demb");
                    if(condicao==null){
                        condicao="derrotar mais <cond> monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "ceifador" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao<recuperacaoN){
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao = ReliquiasNexus.getLang().getString("condicao.ceifador");
                    if(condicao==null){
                        condicao="roube mais <cond> pontos de vida";
                    }
                    condicao=condicao.replace("<cond>",""+(qtd));
                }
            }
            case "fazendeiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao = ReliquiasNexus.getLang().getString("condicao.farme");
                    if(condicao==null){
                        condicao="colha mais <cond> plantações";
                    }
                    condicao=condicao.replace("<cond>",""+(qtd));
                }
            }
            case "guerreiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao = ReliquiasNexus.getLang().getString("condicao.demb");
                    if(condicao==null){
                        condicao="derrotar mais <cond> monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(killsN-kills));
                }
            }
            case "mares" -> {
                int killsN = 10 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao = ReliquiasNexus.getLang().getString("condicao.deamb");
                    if(condicao==null){
                        condicao="derrotar mais <cond> seres aquaticos, monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(killsN-kills));
                }
            }
            case "vida" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao<recuperacaoN){
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao = ReliquiasNexus.getLang().getString("condicao.vida");
                    if(condicao==null){
                        condicao="recupere mais <cond> pontos de vida";
                    }
                    condicao=condicao.replace("<cond>",""+(qtd));
                }
            }
            case "espiao" -> {
                int hab = player.getPersistentDataContainer().getOrDefault(MISSAOESPIAO.key, PersistentDataType.INTEGER, 0);
                if(hab < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.special");
                    if(condicao==null){
                        condicao="use o Special mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-hab));
                }
            }
            case "arqueiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOARQUEIRO.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao = ReliquiasNexus.getLang().getString("condicao.arrow");
                    if(condicao==null){
                        condicao="atingir mais <cond> monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(killsN-kills));
                }
            }
            case "cacador" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCACADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao = ReliquiasNexus.getLang().getString("condicao.arrow");
                    if(condicao==null){
                        condicao="atingir mais <cond> monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(killsN-kills));
                }
            }
            case "tempestade" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao = ReliquiasNexus.getLang().getString("condicao.demb");
                    if(condicao==null){
                        condicao="derrotar mais <cond> monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+(killsN-kills));
                }
            }
            case "mineiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao = ReliquiasNexus.getLang().getString("condicao.mine");
                    if(condicao==null){
                        condicao="quebre mais <cond> minerios";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
            case "fenix" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFENIX.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao = ReliquiasNexus.getLang().getString("condicao.fenix");
                    if(condicao==null){
                        condicao="use mais <cond> foguetes";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
            case "protetor" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao = ReliquiasNexus.getLang().getString("condicao.protetor");
                    if(condicao==null){
                        condicao="se proteja com a reliquia mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
            case "hulk" -> {
                double colheitasN = 20 * level;
                double colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = (int) (colheitasN-colheitas);
                    condicao = ReliquiasNexus.getLang().getString("condicao.dano");
                    if(condicao==null){
                        condicao="receba mais <cond> de dano por monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
            case "sculk" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.sculk");
                    if(condicao==null){
                        condicao="seja atacado mais <cond> vezes por um Warden";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "pescador" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.pescador");
                    if(condicao==null){
                        condicao="pesque mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "flash" -> {
                int hab = player.getPersistentDataContainer().getOrDefault(MISSAOFLASH.key, PersistentDataType.INTEGER, 0);
                if(hab < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.special");
                    if(condicao==null){
                        condicao="use o Special mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-hab));
                }
            }
            case "mago" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMAGO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.pocao");
                    if(condicao==null){
                        condicao="beba mais <cond> poções";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "ladrao" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.ladrao");
                    if(condicao==null){
                        condicao="roube mais <cond> itens";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "domador" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAODOMADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.domador");
                    if(condicao==null){
                        condicao="dome mais <cond> animais/pets";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "cozinheiro" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCOZINHEIRO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.cozinheiro");
                    if(condicao==null){
                        condicao="se alimente mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "construtor" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCONSTRUTOR.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.construtor");
                    if(condicao==null){
                        condicao="coloque mais <cond> blocos";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "abissal" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOABISSAL.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.abissal");
                    if(condicao==null){
                        condicao="se teleporte com enderpearl mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "cronosombra" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCRONOSOMBRA.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.special");
                    if(condicao==null){
                        condicao="use o Special mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "assassino" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOASSASSINO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.assassino");
                    if(condicao==null){
                        condicao="derrote mobs com dano critico mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "frostis" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOFROSTIS.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.frostis");
                    if(condicao==null){
                        condicao="congele mobs, slowness IV, mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "necromante" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAONECROMANTE.key, PersistentDataType.INTEGER, 0);
                if(kills < level*10){
                    condicao = ReliquiasNexus.getLang().getString("condicao.necromante");
                    if(condicao==null){
                        condicao="derrote mobs não esqueléticos mais <cond> vezes";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "alquimista" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOALQUIMISTA.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao = ReliquiasNexus.getLang().getString("condicao.pocao");
                    if(condicao==null){
                        condicao="beba mais <cond> poções";
                    }
                    condicao=condicao.replace("<cond>",""+(level-kills));
                }
            }
            case "golem" -> {
                double colheitasN = 30 * level;
                double colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOGOLEM.key, PersistentDataType.DOUBLE, 0d);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = (int) (colheitasN-colheitas);
                    condicao = ReliquiasNexus.getLang().getString("condicao.dano");
                    if(condicao==null){
                        condicao="receba mais <cond> de dano por monstros ou bosses";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
            case "dragao" -> {
                double colheitas = player.getPersistentDataContainer().getOrDefault(MISSAODRAGAO.key, PersistentDataType.DOUBLE, 0d);
                if(colheitas>=level){
                    condicao="";
                }else{
                    int qtd = (int) (level-colheitas);
                    condicao = ReliquiasNexus.getLang().getString("condicao.dragao");
                    if(condicao==null){
                        condicao="derrote mais <cond> bosses";
                    }
                    condicao=condicao.replace("<cond>",""+qtd);
                }
            }
        }
        return condicao;
    }
    @EventHandler
    public void teleporteEnderPearl(PlayerTeleportEvent event){
        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)){
            Player player = event.getPlayer();
            PlayerInventory inv = player.getInventory();
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            for (int i = 0; i <= 8; i++) {
                ItemStack stack = inv.getItem(i);
                if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("abissal")){
                                int level = dataPlayer.getOrDefault(ABISSAL.key,PersistentDataType.INTEGER,1);
                                int missao = dataPlayer.getOrDefault(MISSAOABISSAL.key,PersistentDataType.INTEGER,1);
                                missao++;
                                dataPlayer.set(MISSAOABISSAL.key,PersistentDataType.INTEGER,missao);
                                tentarEvoluir(player,item.getItem(level),level,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("construtor")){
                            int level = dataPlayer.getOrDefault(CONSTRUTOR.key,PersistentDataType.INTEGER,1);
                            int missao = dataPlayer.getOrDefault(MISSAOCONSTRUTOR.key,PersistentDataType.INTEGER,1);
                            missao++;
                            dataPlayer.set(MISSAOCONSTRUTOR.key,PersistentDataType.INTEGER,missao);
                            tentarEvoluir(player,item.getItem(level),level,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack comida = event.getItem();
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("cozineiro")){
                            if (comida.getType().isEdible()
                                    && comida.getType() != Material.POTION
                                    && comida.getType() != Material.MILK_BUCKET) {

                                // Recupera os dados da comida
                                FoodComponent foodData = comida.getItemMeta().getFood();
                                if (foodData != null) {
                                    int hunger = foodData.getNutrition(); // quantidade de fome (meio pernil = 1)
                                    float saturation = foodData.getSaturation(); // saturação base
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,hunger*20,(int) saturation));
                                    int comidas = dataPlayer.getOrDefault(MISSAOCOZINHEIRO.key, PersistentDataType.INTEGER, 0);
                                    int level = dataPlayer.getOrDefault(COZINHEIRO.key,PersistentDataType.INTEGER,1);
                                    comidas++;
                                    dataPlayer.set(MISSAOCOZINHEIRO.key,PersistentDataType.INTEGER,comidas);
                                    tentarEvoluir(player,item.getItem(level),level,getSlotOfItem(player,stack));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void pescar(FishHookStateChangeEvent event){
        if(event.getNewHookState().equals(FishHook.HookState.HOOKED_ENTITY)){
            FishHook vara = event.getEntity();
            UUID uuid = vara.getOwnerUniqueId();
            Entity e = vara.getHookedEntity();
            if(uuid!=null && e instanceof WaterMob){
                Player player = Bukkit.getPlayer(uuid);
                if(player!=null){
                    ItemStack stack = player.getInventory().getItemInMainHand();
                    if(stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                        String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                        if(nome!=null && nome.equals("pescador")){
                            Nexus n = ItemsRegistro.getFromNome(nome);
                            if(n!=null){
                                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                                int peixes = dataPlayer.getOrDefault(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                                int level = dataPlayer.getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
                                n.setLevel(level);
                                peixes++;
                                dataPlayer.set(MISSAOPESCADOR.key,PersistentDataType.INTEGER,peixes);
                                tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void esconder(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int cd = dataPlayer.getOrDefault(SPECIAL.key, PersistentDataType.INTEGER, 0);
        if(player.hasMetadata("arpao")){
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().getOrDefault(NEXUS.key,PersistentDataType.STRING,"");
                if(nome.equals("pescador")){
                    player.removeMetadata("arpao",plugin);
                    event.setCancelled(true);
                    FishHook hook = player.launchProjectile(FishHook.class);
                    hook.setShooter(player);
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                            ()->{},()-> {},(t)->{
                        if (hook.isDead() || !hook.isValid()) {
                            t.stop();
                            return;
                        }
                        if (hook.getHookedEntity() != null) {
                            Entity alvo = hook.getHookedEntity();
                            Vector puxar = player.getLocation().toVector().subtract(alvo.getLocation().toVector()).normalize().multiply(1.5);
                            alvo.setVelocity(puxar);
                            alvo.getWorld().playSound(alvo.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1f);
                            hook.remove();
                            t.stop();
                        }

                        // Se bateu em bloco (hook landed)
                        if (hook.isOnGround()) {
                            Location destino = hook.getLocation();
                            Vector puxar = destino.toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5);
                            player.setVelocity(puxar);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 1f);
                            hook.remove();
                            t.stop();
                        }
                    });
                    timer.scheduleTimer(2L);
                }
            }
        }
        if (player.isSneaking() && cd <= 0) {
            ItemStack stack = player.getInventory().getHelmet();
            if(stack!=null){
                if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("espiao")){
                                int l=dataPlayer.getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOESPIAO.key,PersistentDataType.INTEGER,0);
                                usos++;
                                Espiao.getSpecialbyLevel(l,player);
                                dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                                dataPlayer.set(MISSAOESPIAO.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                            if(item.getNome().equals("cronosombra")){
                                int l=dataPlayer.getOrDefault(CRONOSOMBRA.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOCRONOSOMBRA.key,PersistentDataType.INTEGER,0);
                                usos++;
                                Cronosombra.getSpecialbyLevel(l,player);
                                dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                                dataPlayer.set(MISSAOCRONOSOMBRA.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
            stack = player.getInventory().getBoots();
            if(stack!=null){
                if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("flash")){
                                int l=dataPlayer.getOrDefault(FLASH.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOFLASH.key,PersistentDataType.INTEGER,0);
                                usos++;
                                Flash.getSpecialbyLevel(l,player);
                                dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                                dataPlayer.set(MISSAOFLASH.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void recuperaVida(EntityRegainHealthEvent event){
        Entity e = event.getEntity();
        if(e instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(stack.getType() != Material.TOTEM_OF_UNDYING){
                stack = player.getInventory().getItemInOffHand();
            }
            if(stack.getType() != Material.TOTEM_OF_UNDYING)return;
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("vida")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                        int level = dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                        n.setLevel(level);
                        recuperacao+=event.getAmount();
                        dataPlayer.set(MISSAOVIDA.key,PersistentDataType.DOUBLE,recuperacao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
            }else{
                stack = player.getInventory().getItemInOffHand();
                meta = stack.getItemMeta();
                data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)) {
                    String nome = data.get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank() && nome.equals("vida")) {
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if (n != null) {
                            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                            double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                            int level = dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1);
                            n.setLevel(level);
                            recuperacao += event.getAmount();
                            dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, recuperacao);
                            tentarEvoluir(player, n.getItem(level), level, getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().getPersistentDataContainer().has(SLAVE.key)){
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;
        ItemStack stack = killer.getInventory().getItemInMainHand();
        PersistentDataContainer data = killer.getPersistentDataContainer();
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if(nome==null) return;
            if(nome.equals("barbaro")){
                int kills = data.getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(BARBARO.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level, getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("guerreiro")){
                int kills = data.getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(GUERREIRO.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("mares")){
                int kills = data.getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(MARES.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof WaterMob || event.getEntity() instanceof Boss || event.getEntity() instanceof Monster){
                    data.set(MISSAOMARES.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("tempestade")){
                int kills = data.getOrDefault(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(TEMPESTADE.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("assassino")){
                int kills = data.getOrDefault(MISSAOASSASSINO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(ASSASSINO.key, PersistentDataType.INTEGER, 1);
                boolean isCrit = !killer.isOnGround() && killer.getFallDistance() > 0.0F &&
                        !killer.isInsideVehicle() && !killer.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                        !killer.isSprinting() && killer.getAttackCooldown() > 0.9F;
                if (isCrit) {
                    data.set(MISSAOASSASSINO.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
        }
        if(temReliquia(killer,"necromante")){
            stack=getReliquia(killer,"necromante");
            Entity entity = event.getEntity();
            if(!eOsso(entity)){
                Location loc = entity.getLocation();
                spawnEsqueletoNecromante(killer,loc);
                int kills = data.getOrDefault(MISSAONECROMANTE.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(NECROMANTE.key, PersistentDataType.INTEGER, 1);
                data.set(MISSAONECROMANTE.key, PersistentDataType.INTEGER, kills + 1);
                tentarEvoluir(killer,stack,level, getSlotOfItem(killer,stack));
            }
        }
        if(temReliquia(killer,"dragao")){
            stack=getReliquia(killer,"dragao");
            Entity entity = event.getEntity();
            if(entity instanceof Boss || entity.getType() == EntityType.WARDEN || entity.getType() == EntityType.ELDER_GUARDIAN){
                int kills = data.getOrDefault(MISSAODRAGAO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(DRAGAO.key, PersistentDataType.INTEGER, 1);
                data.set(MISSAODRAGAO.key, PersistentDataType.INTEGER, kills + 1);
                tentarEvoluir(killer,stack,level, getSlotOfItem(killer,stack));
            }
        }
    }
    private void spawnEsqueletoNecromante(Player player, Location loc) {
        EntityType tipo = sortearEsqueleto();
        LivingEntity mob = (LivingEntity) loc.getWorld().spawnEntity(loc, tipo);

        // Marca como invocado pelo necromante
        mob.getPersistentDataContainer().set(
                SLAVE.key,
                PersistentDataType.STRING,
                "necromante"
        );

        // Nome customizado
        mob.setCustomName("Slave " + player.getName());
        mob.setCustomNameVisible(true);

        // Exemplo para o Wither "menor e fraco"
        if (tipo == EntityType.WITHER) {
            mob.setCustomName("Mini-Wither de " + player.getName());
            mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
            mob.setHealth(50);
            mob.getAttribute(Attribute.SCALE).setBaseValue(0.5);
            mob.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5); // mais fraco
        }

        // Opcional: para ele atacar inimigos do player
        if (mob instanceof Monster monstro) {
            monstro.setTarget(null); // depois dá pra aplicar target dinâmico
        }
    }
    private EntityType sortearEsqueleto() {
        double r = Math.random() * 100.0; // valor entre 0 e 100
        if (r < 50) {
            return EntityType.SKELETON; // 50%
        } else if (r < 70) {
            return EntityType.STRAY; // 20%
        } else if (r < 74) {
            return EntityType.WITHER_SKELETON; // 4%
        } else if (r < 79) {
            return EntityType.SKELETON_HORSE; // 5%
        } else if (r < 99) {
            return EntityType.BOGGED; // 20%
        } else {
            return EntityType.WITHER; // 1%
        }
    }
    private boolean eOsso(Entity entity){
        if (entity == null) return false;

        EntityType type = entity.getType();

        return type == EntityType.SKELETON
                || type == EntityType.STRAY
                || type == EntityType.WITHER_SKELETON
                || type == EntityType.SKELETON_HORSE
                || type == EntityType.BOGGED
                || type == EntityType.WITHER;
    }
    private boolean temReliquia(Player player,String nome) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(item -> item.getPersistentDataContainer().getOrDefault(NEXUS.key,PersistentDataType.STRING,"").equals(nome));
    }
    private ItemStack getReliquia(Player player, String nome) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getPersistentDataContainer()
                        .getOrDefault(NEXUS.key, PersistentDataType.STRING, "")
                        .equals(nome))
                .findFirst()  // pega o primeiro item que bate com o nome
                .orElse(null); // retorna null se não tiver
    }
    @EventHandler
    public void roubaVida(EntityDamageByEntityEvent event){
        Entity atacante = event.getDamager();
        Entity atacado = event.getEntity();
        if(atacante instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            PersistentDataContainerView data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome==null || nome.isBlank())return;
                if(nome.equals("ceifador")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        double dano = event.getDamage();
                        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                        int level = player.getPersistentDataContainer().getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                        double cura = 0;
                        if(level<6){
                            if(player.getPersistentDataContainer().has(DRENO.key,PersistentDataType.INTEGER)){
                                int tempo = player.getPersistentDataContainer().getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                                if(tempo<=0){
                                    cura=dano/2;
                                }
                            }
                        }else{
                            cura = dano/2;
                        }
                        player.heal(cura);
                        recuperacao+=cura;
                        player.getPersistentDataContainer().set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, recuperacao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
                if(nome.equals("ladrao")){
                    if(atacado instanceof LivingEntity furto){
                        ItemStack roubar=null;
                        Random rd = new Random();
                        if(furto instanceof Player roubado){
                            PlayerInventory pinv = roubado.getInventory();
                            ItemStack p = pinv.getItemInOffHand();
                            if(p.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                                String pnome = p.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                                if(pnome!=null && pnome.equals("protetor")){
                                    String msg = ReliquiasNexus.getLang().getString("ladrao.protetor");
                                    if(msg==null){
                                        msg="Você não pode roubar de quem tem a reliquia do protetor!";
                                    }
                                    player.sendMessage(msg);
                                }else{
                                    roubar=rouboPlayer(player,pinv);
                                }
                            }else{
                                roubar=rouboPlayer(player,pinv);
                            }
                        }else{
                            EntityEquipment equipa = furto.getEquipment();
                            if (equipa != null) {
                                int escolhido = rd.nextInt(0,6);
                                EquipmentSlot slot = switch (escolhido){
                                    case 1 -> EquipmentSlot.OFF_HAND;
                                    case 2 -> EquipmentSlot.FEET;
                                    case 3 -> EquipmentSlot.LEGS;
                                    case 4 -> EquipmentSlot.CHEST;
                                    case 5 -> EquipmentSlot.HEAD;
                                    default -> EquipmentSlot.HAND;
                                };
                                roubar = equipa.getItem(slot);
                                if(!roubar.isEmpty()){
                                    String msg = ReliquiasNexus.getLang().getString("ladrao.item");
                                    if(msg==null){
                                        msg="Você roubou uma item!";
                                    }
                                    player.sendMessage(msg);
                                    equipa.setItem(slot,new ItemStack(Material.AIR));
                                }else{
                                    String msg = ReliquiasNexus.getLang().getString("ladrao.nada");
                                    if(msg==null){
                                        msg="Você não conseguiu roubar nada!";
                                    }
                                    player.sendMessage(msg);
                                }
                            }
                        }
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n != null && roubar != null && !roubar.isEmpty()){
                            BundleMeta meta = (BundleMeta) stack.getItemMeta();
                            meta.addItem(roubar);
                            stack.setItemMeta(meta);
                            int qtd = player.getPersistentDataContainer().getOrDefault(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                            int level = player.getPersistentDataContainer().getOrDefault(LADRAO.key,PersistentDataType.INTEGER,1);
                            qtd++;
                            player.getPersistentDataContainer().set(MISSAOLADRAO.key, PersistentDataType.INTEGER, qtd);
                            tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
            if(temReliquia(player,"frostis")){
                if(atacado instanceof LivingEntity vivo){
                    int l = player.getPersistentDataContainer().getOrDefault(FROSTIS.key,PersistentDataType.INTEGER,1);
                    PotionEffect efeito = vivo.getPotionEffect(PotionEffectType.SLOWNESS);
                    int amplificador = 0;
                    int tempofrio = vivo.getFreezeTicks()+(20*l);
                    if(efeito!=null){
                        amplificador=efeito.getAmplifier()+1;
                    }
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,100,amplificador));
                    vivo.setFreezeTicks(tempofrio);
                    if(amplificador>=3){
                        int q = player.getPersistentDataContainer().getOrDefault(MISSAOFROSTIS.key,PersistentDataType.INTEGER,0);
                        q++;
                        player.getPersistentDataContainer().set(MISSAOFROSTIS.key,PersistentDataType.INTEGER,q);
                        ItemStack reliquia = getReliquia(player,"frostis");
                        if(reliquia!=null){
                            tentarEvoluir(player,stack,l,getSlotOfItem(player,reliquia));
                        }
                    }
                }
            }
        }
        if(atacado instanceof Player player){
            ItemStack stack = player.getInventory().getLeggings();
            PersistentDataContainerView data;
            if(stack!=null){
                data = stack.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null && !nome.isBlank() && nome.equals("hulk")){
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null && (atacante instanceof Boss || atacante instanceof Monster)){
                            double protecao = player.getPersistentDataContainer().getOrDefault(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                            int level = player.getPersistentDataContainer().getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
                            protecao+=event.getDamage();
                            player.getPersistentDataContainer().set(MISSAOHULK.key, PersistentDataType.DOUBLE, protecao);
                            tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
            if(temReliquia(player,"golem")){
                int level = player.getPersistentDataContainer().getOrDefault(GOLEM.key,PersistentDataType.INTEGER,1);
                if(atacante instanceof LivingEntity vivo){
                    vivo.damage(event.getFinalDamage()*(level*0.02));
                }
                stack = getReliquia(player,"golem");
                if(stack!=null && (atacante instanceof Boss || atacante instanceof Monster)){
                    double protecao = player.getPersistentDataContainer().getOrDefault(MISSAOGOLEM.key, PersistentDataType.DOUBLE, 0d);
                    protecao+=event.getDamage();
                    player.getPersistentDataContainer().set(MISSAOGOLEM.key, PersistentDataType.DOUBLE, protecao);
                    tentarEvoluir(player,stack,level,getSlotOfItem(player,stack));
                }
            }
            if(player.isBlocking()){
                stack = player.getInventory().getItemInMainHand();
                if(stack.getType().equals(Material.SHIELD)){
                    data = stack.getPersistentDataContainer();
                    if(data.has(NEXUS.key,PersistentDataType.STRING)){
                        String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                        if(nome!=null && !nome.isBlank() && nome.equals("protetor")){
                            Nexus n = ItemsRegistro.getFromNome(nome);
                            if(n!=null){
                                int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                                int level = player.getPersistentDataContainer().getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                                protecao++;
                                player.getPersistentDataContainer().set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, protecao);
                                tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }else{
                    stack = player.getInventory().getItemInOffHand();
                    if(stack.getType().equals(Material.SHIELD)){
                        data = stack.getPersistentDataContainer();
                        if(data.has(NEXUS.key,PersistentDataType.STRING)){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            if(nome!=null && !nome.isBlank() && nome.equals("protetor")){
                                Nexus n = ItemsRegistro.getFromNome(nome);
                                if(n!=null){
                                    int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                                    int level = player.getPersistentDataContainer().getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                                    protecao++;
                                    player.getPersistentDataContainer().set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, protecao);
                                    tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                                }
                            }
                        }
                    }
                }
            }
            stack = player.getInventory().getItemInMainHand();
            data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("sculk")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null && atacante instanceof Warden war){
                        int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                        int level = player.getPersistentDataContainer().getOrDefault(SCULK.key,PersistentDataType.INTEGER,1);
                        protecao++;
                        if(player.hasMetadata("wardenImunity")){
                            event.setDamage(0);
                            war.setAnger(player,0);
                            player.removeMetadata("wardenImunity",plugin);
                        }
                        player.getPersistentDataContainer().set(MISSAOSCULK.key, PersistentDataType.INTEGER, protecao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
            }
        }
    }
    private ItemStack rouboPlayer(Player player,PlayerInventory pinv){
        Random rd = new Random();
        int escolhido = rd.nextInt(0,pinv.getContents().length);
        ItemStack roubar = pinv.getItem(escolhido);
        if(roubar!=null && !roubar.isEmpty()){
            boolean expurgo = ReliquiasNexus.getNexusConfig().getBoolean("expurgo");
            if(roubar.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                if(expurgo){
                    PersistentDataContainer container = player.getPersistentDataContainer();
                    String rnome = roubar.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                    roubar.getItemMeta().getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                    ReliquiasNexus.setConfigSave("nexus."+rnome,player.getUniqueId().toString());
                    plugin.saveConfig();
                    int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,1);
                    qtd++;
                    container.set(QTD.key, PersistentDataType.INTEGER,qtd);
                    pinv.setItem(escolhido,new ItemStack(Material.AIR));
                    String msg = ReliquiasNexus.getLang().getString("ladrao.reliquia");
                    if(msg==null){
                        msg="Você roubou uma reliquia!";
                    }
                    player.sendMessage(msg);
                    LimitadorEvent.checkLimit(player);
                }
                else{
                    String msg = ReliquiasNexus.getLang().getString("ladrao.expurgo");
                    if(msg==null){
                        msg="Você não pode roubar uma reliquia fora do expurgo!";
                    }
                    player.sendMessage(msg);
                    return null;
                }
            }
            else{
                String msg = ReliquiasNexus.getLang().getString("ladrao.item");
                if(msg==null){
                    msg="Você roubou uma item!";
                }
                player.sendMessage(msg);
            }
        }
        else{
            String msg = ReliquiasNexus.getLang().getString("ladrao.nada");
            if(msg==null){
                msg="Você não conseguiu roubar nada!";
            }
            player.sendMessage(msg);
        }
        return roubar;
    }
    @EventHandler
    public void colher(BlockBreakEvent event){
        Block bloco = event.getBlock();
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if(meta==null)return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank() && nome.equals("fazendeiro")){
                int level = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                if(bloco.getBlockData() instanceof Ageable ageable && ageable.getAge()==ageable.getMaximumAge()){
                    int colher = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                    colher++;
                    dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, colher);
                    tentarEvoluir(player,stack,level,getSlotOfItem(player,stack));
                }
            }
            if(nome!=null && !nome.isBlank() && nome.equals("mineiro")){
                int level = dataPlayer.getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
                if(eMinerio(bloco.getBlockData().getMaterial())){
                    int colher = player.getPersistentDataContainer().getOrDefault(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                    colher++;
                    dataPlayer.set(MISSAOMINEIRO.key, PersistentDataType.INTEGER, colher);
                    tentarEvoluir(player,stack,level,getSlotOfItem(player,stack));
                }
            }
        }
    }
    private boolean eMinerio(Material m){
        List<Material> mat = List.of(
                Material.COAL_ORE,
                Material.COPPER_ORE,
                Material.DIAMOND_ORE,
                Material.LAPIS_ORE,
                Material.GOLD_ORE,
                Material.EMERALD_ORE,
                Material.IRON_ORE,
                Material.REDSTONE_ORE,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.ANCIENT_DEBRIS
        );
        return mat.contains(m);
    }
    @EventHandler
    public void atingiu(ProjectileHitEvent event){
        Projectile projetiu = event.getEntity();
        Entity entity = event.getHitEntity();
        if(entity instanceof Boss || entity instanceof Monster){
            if(projetiu instanceof Arrow flecha){
                UUID uuid = flecha.getOwnerUniqueId();
                if(uuid!=null && !flecha.getPersistentDataContainer().has(SPECIAL.key,PersistentDataType.STRING)){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player!=null){
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        ItemMeta meta = stack.getItemMeta();
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        if(data.has(NEXUS.key,PersistentDataType.STRING)){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            if(nome!=null && nome.equals("arqueiro")){
                                int q = player.getPersistentDataContainer().getOrDefault(MISSAOARQUEIRO.key,PersistentDataType.INTEGER,0);
                                q++;
                                player.getPersistentDataContainer().set(MISSAOARQUEIRO.key,PersistentDataType.INTEGER,q);
                                int l = player.getPersistentDataContainer().getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                            if(nome!=null && nome.equals("cacador")){
                                int q = player.getPersistentDataContainer().getOrDefault(MISSAOCACADOR.key,PersistentDataType.INTEGER,0);
                                q++;
                                player.getPersistentDataContainer().set(MISSAOCACADOR.key,PersistentDataType.INTEGER,q);
                                int l = player.getPersistentDataContainer().getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void booster(PlayerElytraBoostEvent event){
        Player player = event.getPlayer();
        ItemStack peitoral = player.getInventory().getChestplate();
        if(peitoral!=null){
            ItemMeta meta = peitoral.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && nome.equals("fenix")){
                    int l = player.getPersistentDataContainer().getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
                    Random r = new Random();
                    int i = r.nextInt(0,100);
                    if(i>=100-l){
                        event.setShouldConsume(false);
                        Firework fr = event.getFirework();
                        event.getFirework().setTicksFlown(fr.getTicksFlown()+l);
                    }
                    int q = player.getPersistentDataContainer().getOrDefault(MISSAOFENIX.key,PersistentDataType.INTEGER,0);
                    q++;
                    player.getPersistentDataContainer().set(MISSAOFENIX.key,PersistentDataType.INTEGER,q);
                    tentarEvoluir(player,peitoral,l,getSlotOfItem(player,peitoral));
                }
            }
        }
    }
    public int getSlotOfItem(Player player, ItemStack targetItem) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].isSimilar(targetItem)) {
                return i;
            }
        }
        return -1;
    }
    @EventHandler
    public void correr(PlayerMoveEvent event){
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getBoots();
        if(stack!=null && player.isSprinting()){
            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("flash")){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,40,0));
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void bebeu(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        ItemStack pocao = event.getItem();
        if(pocao.getType().equals(Material.POTION)){
            for (int i = 0; i <= 8; i++) {
                ItemStack stack = inv.getItem(i);
                if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("mago")){
                                int l=dataPlayer.getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOMAGO.key,PersistentDataType.INTEGER,0);
                                usos++;
                                dataPlayer.set(MISSAOMAGO.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                            if(item.getNome().equals("alquimista")){
                                int l=dataPlayer.getOrDefault(ALQUIMISTA.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOALQUIMISTA.key,PersistentDataType.INTEGER,0);
                                usos++;
                                dataPlayer.set(MISSAOMAGO.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void domar(EntityTameEvent event){
        UUID uuid = event.getOwner().getUniqueId();
        Player player = Bukkit.getPlayer(uuid);
        if(player==null)return;
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("domador")){
                            int l=dataPlayer.getOrDefault(DOMADOR.key,PersistentDataType.INTEGER,1);
                            int usos=dataPlayer.getOrDefault(MISSAODOMADOR.key,PersistentDataType.INTEGER,0);
                            usos++;
                            dataPlayer.set(MISSAODOMADOR.key,PersistentDataType.INTEGER,usos);
                            tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
    }
}