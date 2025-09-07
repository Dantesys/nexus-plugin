package org.dantesys.reliquiasNexus.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.UUID;

public class PlayerListManager implements Listener {
    private final PlayerList playerList;
    private final TagHead tagHead;
    private final ReliquiasNexus plugin;

    public PlayerListManager(ReliquiasNexus plugin) {
        this.plugin = plugin;
        this.playerList = new PlayerList(plugin);
        this.tagHead = new TagHead(plugin, playerList);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Iniciar task para atualizar informações do servidor periodicamente
        startUpdateTask();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerList.updatePlayerList(player);
            tagHead.updatePlayerTag(player);
            playerList.updateForAllPlayers();
        }, 20L); // Delay de 1 segundo
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Atualizar a player list para todos os jogadores restantes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerList.updateForAllPlayers();
        }, 20L);
    }

    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                playerList.updateServerInfo();
                playerList.updateForAllPlayers();
                tagHead.updateAllPlayerTags();
            }
        }.runTaskTimer(plugin, 0L, 100L); // Atualiza a cada 5 segundos
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    public TagHead getTagHead() {
        return tagHead;
    }

    public void updateAllPlayerLists() {
        playerList.updateForAllPlayers();
        tagHead.updateAllPlayerTags();
    }

    public void setPlayerRank(UUID playerUuid, String rank) {
        plugin.getConfig().set("players." + playerUuid.toString() + ".rank", rank);
        plugin.saveConfig();
        Player player = Bukkit.getPlayer(playerUuid);
        if (player != null) {
            playerList.updatePlayerList(player);
            tagHead.updatePlayerTag(player);
            updateAllPlayerLists();
        }
    }
}