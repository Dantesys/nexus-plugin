package org.dantesys.reliquiasNexus.tab;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Atualiza a tag do jogador para que ele veja a própria tag
        updatePlayerTag(player);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Component tag = playerList.getPlayerTag(player);

        // Mensagem com cor normal para membros, cor da tag para outros
        Component messageComponent;
        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");

        if (rank.equalsIgnoreCase("membro")) {
            messageComponent = Component.text(event.getMessage()).color(NamedTextColor.WHITE);
        } else {
            messageComponent = Component.text(event.getMessage()).color(tag.color());
        }

        Component finalMessage = Component.text()
                .append(tag)
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(messageComponent)
                .build();

        // Cancela a mensagem original e envia a formatada
        event.setCancelled(true);
        Bukkit.getServer().sendMessage(finalMessage);
    }

    // Mudei de private para public
    public void updatePlayerTag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");
        String teamName;

        switch (rank.toLowerCase()) {
            case "dono":
                teamName = "000dono";
                break;
            case "staff":
                teamName = "001staff";
                break;
            case "ajudante":
                teamName = "002ajudante";
                break;
            case "beta":
                teamName = "003beta";
                break;
            case "amigo":
                teamName = "004amigo";
                break;
            case "membro":
            default:
                teamName = "999membro";
                break;
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            Component prefix = playerList.getPlayerTag(player);
            team.prefix(prefix);
        }

        // Garante que o jogador está no time correto para ver sua própria tag
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }

    public void updateAllPlayerTags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTag(player);
        }
    }
}