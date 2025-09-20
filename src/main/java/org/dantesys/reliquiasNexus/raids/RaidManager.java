package org.dantesys.reliquiasNexus.raids;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidManager implements Listener {
    private int cooldown;
    private final Random rd = new Random();
    private void startRaid(Player player){

    }
    @EventHandler
    public void tickEvent(ServerTickEndEvent event){
        cooldown--;
        if(cooldown==1200){
            Bukkit.broadcast(Component.text(ReliquiasNexus.getLang().getString("raid.pre","A barreira entre os mundos está se quebrando...")));
        }
        if(cooldown<=0){
            List<Player> playerOn = new ArrayList<>(Bukkit.getOnlinePlayers());
            if(playerOn.isEmpty()){
                cooldown=36000;
            }
            int indice = rd.nextInt(playerOn.size());
            startRaid(playerOn.get(indice));
        }
    }
}
