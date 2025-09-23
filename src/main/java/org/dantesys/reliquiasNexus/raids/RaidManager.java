package org.dantesys.reliquiasNexus.raids;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.raids.boss.BossManager;
import org.dantesys.reliquiasNexus.raids.disasterBoss.DisasterBossManager;
import org.dantesys.reliquiasNexus.raids.invasao.InvasaoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidManager implements Listener {
    private final JavaPlugin plugin;
    private int cooldown;
    private final Random rd = new Random();
    private CommomEvent event;
    private int escolhido=0;

    public RaidManager(JavaPlugin plugin) {
        this.plugin = plugin;
        resetCooldown();
    }

    private void resetCooldown() {
        int min = 30 * 60 * 20;
        int max = 60 * 60 * 20;
        cooldown = rd.nextInt(max - min + 1) + min;
    }
    public void startRaid(Player player, String tipo){
        Location l = player.getLocation();
        event = switch (tipo){
            case "boss" -> new BossManager(l,plugin);
            case "disaster" -> new DisasterBossManager(l,plugin);
            default -> new InvasaoManager(l,plugin);
        };
        event.start();
    }
    private void startRaid(Player player){
        Location l = player.getLocation();
        if (escolhido <= 50) {
            event = new InvasaoManager(l,plugin);
        } else if (escolhido <= 75) {
            event = new BossManager(l,plugin);
        } else {
            event = new DisasterBossManager(l,plugin);
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
                if(Bukkit.getOnlinePlayers().isEmpty()){
                    resetCooldown();
                }
            }
            if (cooldown == 600) {
                escolhido = rd.nextInt(100);
                if (escolhido <= 50) {
                    Bukkit.broadcast(Component.text(
                            ReliquiasNexus.getLang().getString("raid.preinvasao", "Você ouve o som de tropas caminhando!")
                    ));
                    Bukkit.getOnlinePlayers().forEach(player-> player.playSound(player.getLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 1.0f, 1.0f));
                } else if (escolhido <= 75) {
                    Bukkit.broadcast(Component.text(
                            ReliquiasNexus.getLang().getString("raid.preboss", "Você o som de um rugido!")
                    ));
                    Bukkit.getOnlinePlayers().forEach(player-> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f));

                } else {
                    Bukkit.broadcast(Component.text(
                            ReliquiasNexus.getLang().getString("raid.predisaster", "Você sente uma presença ameaçadora!")
                    ));
                    Bukkit.getOnlinePlayers().forEach(player-> player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f));
                }
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
}