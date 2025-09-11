package org.dantesys.reliquiasNexus.tab;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

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

        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "membro");
        NamedTextColor color = switch(rank.toLowerCase()) {
            case "dono" -> NamedTextColor.RED;
            case "staff" -> NamedTextColor.AQUA;
            default -> NamedTextColor.WHITE;
        };

        Component finalMessage = Component.text()
                .append(tag)
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(Component.text(event.getMessage()).color(color))
                .build();

        event.setCancelled(true);
        Bukkit.getServer().sendMessage(finalMessage);
    }

    public void updatePlayerTag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "membro");
        String teamName = player.getUniqueId() + rank;
        Color cor = plugin.getConfig().getColor("cargo." + rank, Color.WHITE);

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(playerList.getPlayerTag(player));
            team.color(NamedTextColor.nearestTo(TextColor.color(cor.asRGB())));
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