package org.dantesys.reliquiasNexus.tab;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.util.UUID;

public class PlayerList {
    private final ReliquiasNexus plugin;

    public PlayerList(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public void updatePlayerList(Player player) {
        // Header e Footer
        Component header = Component.text()
                .append(Component.text("Nexus Etern\n")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(String.format("TPS: %.1f | Online: %d", Bukkit.getTPS()[0], Bukkit.getOnlinePlayers().size()))
                        .color(NamedTextColor.GRAY))
                .build();

        Component footer = Component.text()
                .append(Component.text("----------------\n")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Nexus")
                        .color(NamedTextColor.YELLOW))
                .build();

        player.sendPlayerListHeaderAndFooter(header, footer);

        // Atualizar informações do jogador no scoreboard
        updatePlayerInfo(player);
    }

    private void updatePlayerInfo(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard.getObjective("playerlist") == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("playerlist", "dummy");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            player.setScoreboard(scoreboard);
        }

        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");

        String teamName;
        Component prefix;

        switch (rank.toLowerCase()) {
            case "dono":
                teamName = "000dono";
                prefix = Component.text("[Dono] ").color(NamedTextColor.RED);
                break;
            case "staff":
                teamName = "001staff";
                prefix = Component.text("[Staff] ").color(NamedTextColor.AQUA);
                break;
            case "ajudante":
                teamName = "002ajudante";
                prefix = Component.text("[Ajudante] ").color(NamedTextColor.GREEN);
                break;
            case "membro":
            default:
                teamName = "999membro";
                prefix = Component.text("[Membro] ").color(NamedTextColor.GRAY);
                break;
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(prefix);
            team.setAllowFriendlyFire(false); // Adicionado para evitar dano entre membros do mesmo time, se for o caso
            team.setCanSeeFriendlyInvisibles(true); // Membros do time podem ver uns aos outros
        }

        // Remove o jogador de todos os outros times
        for (Team otherTeam : scoreboard.getTeams()) {
            if (otherTeam.hasEntry(player.getName())) {
                otherTeam.removeEntry(player.getName());
            }
        }

        team.addEntry(player.getName());

        // Atualizar o nome na tablist para o ping real
        int playerPing = player.getPing();
        player.playerListName(
                prefix.append(Component.text(player.getName() + " | " + playerPing + "ms").color(NamedTextColor.WHITE))
        );
    }

    public void updateForAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updatePlayerList(p);
        }
    }

    public void updateServerInfo() {
        double tps = Bukkit.getTPS()[0];
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Component header = Component.text()
                .append(Component.text("Nexus Etern\n")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(String.format("TPS: %.1f | Online: %d", tps, onlinePlayers))
                        .color(NamedTextColor.GRAY))
                .build();

        Component footer = Component.text()
                .append(Component.text("----------------\n")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Nexus")
                        .color(NamedTextColor.YELLOW))
                .build();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }
}