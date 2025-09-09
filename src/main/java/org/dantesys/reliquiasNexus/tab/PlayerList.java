package org.dantesys.reliquiasNexus.tab;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

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
        if (scoreboard.getObjective("playerlist") == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("playerlist","dummy");
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            player.setScoreboard(scoreboard);
        }

        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");

        String teamName;
        Component prefix = switch (rank.toLowerCase()) {
            case "dono" -> {
                teamName = "000dono";
                yield Component.text("[Dono] ").color(NamedTextColor.RED);
            }
            case "staff" -> {
                teamName = "001staff";
                yield Component.text("[Staff] ").color(NamedTextColor.AQUA);
            }
            case "ajudante" -> {
                teamName = "002ajudante";
                yield Component.text("[Ajudante] ").color(NamedTextColor.GREEN);
            }
            case "beta" -> {
                teamName = "003beta";
                yield Component.text("[Beta] ").color(NamedTextColor.DARK_BLUE);
            }
            case "amigo" -> {
                teamName = "004amigo";
                yield Component.text("[Amigo] ").color(NamedTextColor.DARK_PURPLE);
            }
            default -> {
                teamName = "999membro";
                yield Component.text("[Membro] ").color(NamedTextColor.GRAY);
            }
        };

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(prefix);
            team.setAllowFriendlyFire(false);
            team.setCanSeeFriendlyInvisibles(true);
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

    // Método para obter o prefixo da tag de um jogador
    public Component getPlayerTag(Player player) {
        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");
        Color cor = plugin.getConfig().getColor("cargo."+rank, Color.WHITE);
        String r = rank.substring(0, 1).toUpperCase();
        String corrigido = r + rank.substring(1);
        return Component.text("["+corrigido+"]").color(TextColor.color(cor.asRGB()));
    }
}