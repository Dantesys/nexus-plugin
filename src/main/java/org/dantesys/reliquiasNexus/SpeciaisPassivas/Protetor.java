package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import static org.dantesys.reliquiasNexus.util.NexusKeys.CHARGE;
import static org.dantesys.reliquiasNexus.util.NexusKeys.PROTECAO;

public class Protetor {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,0));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,1));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,2));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            steelBlood(level,player);
        }else if(level<16){//8-15
            avengerBastion(level,player);
        }else{//16-20
            reflect(level,player);
        }
    }
    private static void steelBlood(int level, Player player){
        player.spawnParticle(Particle.EGG_CRACK,player.getLocation(),level);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600+20*level,level));
    }
    private static void avengerBastion(int level, Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), level,
                () -> {
                    String msg = ReliquiasNexus.getLang().getString("special.protetor.ativado");
                    if(msg==null){
                        msg="Habilidade do Nexus do Protetor Ativado!";
                    }
                    player.sendActionBar(Component.text(msg));
                    dataPlayer.set(CHARGE.key,PersistentDataType.FLOAT,0f);
                },
                () -> {
                    player.getWorld().createExplosion(player,player.getLocation(),dataPlayer.getOrDefault(CHARGE.key,PersistentDataType.FLOAT,(float)level));
                },
                (t) -> {
                    String msg = ReliquiasNexus.getLang().getString("special.protetor.tempo");
                    if(msg==null){
                        msg="Modo Reversão acaba em <tempo> segundos!";
                    }
                    msg=msg.replace("<tempo>",""+t.getSegundosRestantes());
                    player.sendActionBar(Component.text(msg));
                }
        );
        timer.scheduleTimer(20L);
    }
    private static void reflect(int level, Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), level,
                () -> {
                    String msg = ReliquiasNexus.getLang().getString("special.protetor.ativado");
                    if(msg==null){
                        msg="Habilidade do Nexus do Protetor Ativado!";
                    }
                    player.sendActionBar(Component.text(msg));
                    dataPlayer.set(PROTECAO.key,PersistentDataType.BOOLEAN,true);
                },
                () -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    dataPlayer.set(PROTECAO.key,PersistentDataType.BOOLEAN,false);
                },
                (t) -> {
                    String msg = ReliquiasNexus.getLang().getString("special.protetor.tempo");
                    if(msg==null){
                        msg="Modo Reversão acaba em <tempo> segundos!";
                    }
                    msg=msg.replace("<tempo>",""+t.getSegundosRestantes());
                    player.sendActionBar(Component.text(msg));
                }
        );
        timer.scheduleTimer(20L);
    }
}
