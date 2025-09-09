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

        // Atualizar tags de todos os jogadores online ao iniciar
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTag(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Atualiza a tag do jogador para que ele veja a própria tag
        updatePlayerTag(player);

        // Atualiza tags de todos os jogadores para incluir o novo jogador
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updatePlayerTag(onlinePlayer);
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Component tag = playerList.getPlayerTag(player);

        // Mensagem com cor normal para membros, cor da tag para outros
        Component messageComponent;
        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");

        if (rank.equalsIgnoreCase("dono")) {
            messageComponent = Component.text(event.getMessage()).color(NamedTextColor.RED);
        } else if (rank.equalsIgnoreCase("staff")) {
            messageComponent = Component.text(event.getMessage()).color(NamedTextColor.AQUA);
        } else {
            messageComponent = Component.text(event.getMessage()).color(NamedTextColor.WHITE);
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

    public void updatePlayerTag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");
        String r = rank.substring(0, 1).toUpperCase();
        String corrigido = r + rank.substring(1);
        String teamName = player.getUniqueId()+corrigido;
        Color cor = plugin.getConfig().getColor("cargo."+rank, Color.WHITE);
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            Component prefix = playerList.getPlayerTag(player);
            team.prefix(prefix);
            team.color(NamedTextColor.nearestTo(TextColor.color(cor.asRGB())));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        // Remove o jogador de todos os outros times
        for (Team otherTeam : scoreboard.getTeams()) {
            if (otherTeam.hasEntry(player.getName())) {
                otherTeam.removeEntry(player.getName());
            }
        }

        // Garante que o jogador está no time correto para ver sua própria tag
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }

        // Define o scoreboard para o jogador
        player.setScoreboard(scoreboard);
    }

    public void updateAllPlayerTags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTag(player);
        }
    }
}