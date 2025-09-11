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
        Component header = Component.text()
                .append(Component.text("Nexus Etern\n")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.text(String.format("TPS: %.1f | Online: %d", Bukkit.getTPS()[0], Bukkit.getOnlinePlayers().size()))
                        .color(NamedTextColor.GRAY))
                .build();

        Component footer = Component.text()
                .append(Component.text("----------------\n").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Nexus").color(NamedTextColor.YELLOW))
                .build();

        player.sendPlayerListHeaderAndFooter(header, footer);

        updatePlayerName(player);
    }

    private void updatePlayerName(Player player) {
        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "membro");
        Component prefix = getPlayerTag(player);
        int ping = player.getPing();
        player.playerListName(prefix.append(Component.text(player.getName() + " | " + ping + "ms").color(NamedTextColor.WHITE)));
    }

    public void updateForAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updatePlayerList(p);
        }
    }

    public void updateServerInfo() {
        Component header = Component.text()
                .append(Component.text("Nexus Etern\n").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text(String.format("TPS: %.1f | Online: %d", Bukkit.getTPS()[0], Bukkit.getOnlinePlayers().size()))
                        .color(NamedTextColor.GRAY))
                .build();

        Component footer = Component.text()
                .append(Component.text("----------------\n").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Nexus").color(NamedTextColor.YELLOW))
                .build();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }

    public Component getPlayerTag(Player player) {
        String rank = plugin.getConfig().getString("players." + player.getUniqueId() + ".rank", "membro");
        Color cor = plugin.getConfig().getColor("cargo." + rank, Color.WHITE);
        String corrigido = rank.substring(0, 1).toUpperCase() + rank.substring(1);
        return Component.text("[" + corrigido + "]").color(TextColor.color(cor.asRGB()));
    }
}