package org.dantesys.reliquiasNexus.raids;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.raids.boss.BossManager;
import org.dantesys.reliquiasNexus.raids.invasao.InvasaoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidManager implements Listener {
    private final JavaPlugin plugin;
    private int cooldown;
    private final Random rd = new Random();
    private CommomEvent event;

    public RaidManager(JavaPlugin plugin) {
        this.plugin = plugin;
        resetCooldown();
    }

    private void resetCooldown() {
        int min = 15 * 60 * 20; // 15 min
        int max = 45 * 60 * 20; // 45 min
        cooldown = rd.nextInt(max - min + 1) + min;
    }
    public void startRaid(Player player, String tipo){
        Location l = player.getLocation();
        event = switch (tipo){
            case "boss" -> new BossManager(l,plugin);
            default -> new InvasaoManager(l,plugin);
        };
        event.start();
    }
    private void startRaid(Player player){
        int escolhido = rd.nextInt(100);
        Location l = player.getLocation();
        if (escolhido <= 50) {
            event = new InvasaoManager(l,plugin);
        } else {
            event = new BossManager(l,plugin);
        }
        event.start();
    }
    @EventHandler
    public void tickEvent(ServerTickEndEvent event){
        if (this.event == null) {
            cooldown--;
            if (cooldown == 1200) {
                Bukkit.broadcast(Component.text(
                        ReliquiasNexus.getLang().getString("raid.pre", "A barreira entre os mundos está se quebrando...")
                ));
            }
            if (cooldown <= 0) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.isEmpty()) {
                    resetCooldown();
                    return;
                }
                Player escolhido = players.get(rd.nextInt(players.size()));
                startRaid(escolhido);
            }
            return;
        }
        if (this.event.isFinished()) {
            this.event = null;
            resetCooldown();
        }
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(event != null && event.isRunning()) {
            event.handleDamage(e); // repassa para o evento atual
        }
    }
    @EventHandler
    public void entityDeath(EntityDeathEvent e){
        if(event != null && event.isRunning() && event instanceof BossManager boss){
            boss.onBossDeath(e);
        }
    }
}