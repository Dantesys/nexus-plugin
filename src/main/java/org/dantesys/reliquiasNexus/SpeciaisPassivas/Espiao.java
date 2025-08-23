package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

public class Espiao {
    public static void getPassivabyLevel(int level, Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,600,0));
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            eyeVigilance(level,player);
        }else if(level<16){//8-15
            emergencyEscape(level,player);
        }else{//16-20
            soulEscape(level,player);
        }
    }
    private static void eyeVigilance(int level, Player player){
        for(Entity e: player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                if(vivo instanceof Player pl){
                    if(pl != player){
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600+20*level,0));
                    }
                }else{
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600+20*level,0));
                }
            }
        }
    }
    private static void emergencyEscape(int level, Player player){
        World w = player.getWorld();
        Location loc = player.getLocation();
        w.spawnParticle(Particle.LARGE_SMOKE,loc,level);
        Location back = player.getRespawnLocation();
        if(back==null){
            back = player.getBedLocation();
        }
        player.teleport(back);
    }
    private static void soulEscape(int level, Player player){
        Location loc = player.getLocation();
        Component comp = player.playerListName();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class),
                level,
                () -> {
                    String msg = ReliquiasNexus.getLang().getString("espiao.ativado");
                    if(msg==null){
                        msg="Habilidade do Espião Ativado!";
                    }
                    player.sendActionBar(Component.text(msg));
                    player.setGameMode(GameMode.SPECTATOR);
                    player.playerListName(Component.text(""));
                },
                () -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(loc);
                    player.playerListName(comp);
                },
                (t) -> {
                    String msg = ReliquiasNexus.getLang().getString("espiao.tempo");
                    if(msg==null){
                        msg="Modo Fantasma acaba em <tempo> segundos!";
                    }
                    msg=msg.replace("<tempo>",""+t.getSegundosRestantes());
                    player.sendActionBar(Component.text(msg));
                }
        );
        timer.scheduleTimer(20L);
    }
}
