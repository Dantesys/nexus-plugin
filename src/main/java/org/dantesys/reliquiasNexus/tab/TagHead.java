package org.dantesys.reliquiasNexus.tab;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import static org.dantesys.reliquiasNexus.util.NexusKeys.COR;

public class TagHead implements Listener {
    private final ReliquiasNexus plugin;
    private final PlayerList playerList;

    public TagHead(ReliquiasNexus plugin, PlayerList playerList) {
        this.plugin = plugin;
        this.playerList = playerList;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTag(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> updatePlayerTag(player), 20L);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Component tag = playerList.getPlayerTag(player);

        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "Membro");
        String cor = plugin.getConfig().getString("cargos." + rank, "#ffffff");
        boolean corAtiva = player.getPersistentDataContainer().getOrDefault(COR.key, PersistentDataType.BOOLEAN,false);
        TextColor textColor = corAtiva?TextColor.fromHexString(cor):TextColor.fromHexString("#ffffff");
        Component finalMessage = Component.text()
                .append(tag.color(TextColor.fromHexString(cor)))
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(Component.text(event.getMessage()).color(textColor))
                .build();
        event.setCancelled(true);
        Bukkit.getServer().sendMessage(finalMessage);
    }

    public void updatePlayerTag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "Membro");
        String teamName = player.getUniqueId() + rank;
        String cor = plugin.getConfig().getString("cargos." + rank, "#ffffff");

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(playerList.getPlayerTag(player));
            team.color(NamedTextColor.nearestTo(TextColor.fromHexString(cor)));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        for (Team otherTeam : scoreboard.getTeams()) {
            if (otherTeam.hasEntry(player.getName())) otherTeam.removeEntry(player.getName());
        }

        team.addEntry(player.getName());
        player.setScoreboard(scoreboard);
    }

    public void updateAllPlayerTags() {
        for (Player player : Bukkit.getOnlinePlayers()) updatePlayerTag(player);
    }
}