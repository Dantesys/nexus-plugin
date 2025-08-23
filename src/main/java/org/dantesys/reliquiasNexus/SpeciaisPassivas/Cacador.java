package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class Cacador {
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
        }else if(level<15) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600,0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,0));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            explosive(level,player);
        }else if(level<16){//8-15
            freeze(level,player);
        }else{//16-20
            minigun(level,player);
        }
    }
    private static void explosive(int level, Player player){
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.YELLOW);
        arrow.setMetadata(SPECIAL.key.getKey(), new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class),level*2));
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(level));
    }
    private static void freeze(int level, Player player){
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.BLACK);
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS,600+20*level,level),true);
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(level));
    }
    private static void minigun(int level, Player player){
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10+level,
                ()->{
                    String msg = ReliquiasNexus.getLang().getString("special.cacador.ativado");
                    if(msg==null){
                        msg="Modo Minigum Ativado!";
                    }
                    player.sendActionBar(Component.text(msg));
                },
                ()->{},
                (t)->{
                    String msg = ReliquiasNexus.getLang().getString("special.cacador.tempo");
                    if(msg==null){
                        msg="Modo Minigun acaba em <tempo> segundos!";
                    }
                    msg=msg.replace("<tempo>",""+t.getSegundosRestantes());
                    player.sendActionBar(Component.text(msg));
                    Vector vec = player.getEyeLocation().getDirection();
                    Arrow flecha = player.launchProjectile(Arrow.class);
                    flecha.setCritical(true);
                    flecha.setGlowing(true);
                    flecha.setColor(Color.YELLOW);
                    flecha.setVelocity(vec.multiply(level/2));
                }
        );
        timer.scheduleTimer(5L);
    }
}
