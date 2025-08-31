package org.dantesys.reliquiasNexus.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Party implements Listener {

    private static final Map<UUID, UUID> partyMembers = new HashMap<>(); // Player UUID -> Party Leader UUID
    private final ReliquiasNexus plugin;

    public Party(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    public static void convidarParaParty(Player inviter, Player target) {
        if (inviter.getUniqueId().equals(target.getUniqueId())) {
            inviter.sendMessage(Component.text("❌ Você não pode convidar a si mesmo.").color(NamedTextColor.RED));
            return;
        }

        if (partyMembers.containsKey(inviter.getUniqueId()) || partyMembers.containsKey(target.getUniqueId())) {
            inviter.sendMessage(Component.text("❌ Um dos jogadores já está em uma party.").color(NamedTextColor.RED));
            return;
        }

        inviter.sendMessage(Component.text("✅ Você convidou " + target.getName() + " para a sua party.").color(NamedTextColor.GREEN));
        target.sendMessage(Component.text("✅ Você recebeu um convite de " + inviter.getName() + " para uma party.").color(NamedTextColor.GREEN));

        // Lógica de aceitar convite (simplificada)
        // Você pode implementar um sistema de clique aqui, mas por enquanto, vamos apenas criar a party.
        criarParty(inviter, target);
    }

    private static void criarParty(Player leader, Player member) {
        partyMembers.put(leader.getUniqueId(), leader.getUniqueId());
        partyMembers.put(member.getUniqueId(), leader.getUniqueId());
        leader.sendMessage(Component.text("✅ A party com " + member.getName() + " foi criada!").color(NamedTextColor.GREEN));
        member.sendMessage(Component.text("✅ Você entrou na party de " + leader.getName() + "!").color(NamedTextColor.GREEN));
        atualizarScoreboardParty();
    }

    public static void dissolverParty(Player player) {
        if (!partyMembers.containsKey(player.getUniqueId())) return;

        UUID leaderUuid = partyMembers.get(player.getUniqueId());

        for (Map.Entry<UUID, UUID> entry : partyMembers.entrySet()) {
            if (entry.getValue().equals(leaderUuid)) {
                Player member = Bukkit.getPlayer(entry.getKey());
                if (member != null && member.isOnline()) {
                    member.sendMessage(Component.text("❌ Sua party foi dissolvida.").color(NamedTextColor.RED));
                }
            }
        }
        partyMembers.entrySet().removeIf(entry -> entry.getValue().equals(leaderUuid));
        atualizarScoreboardParty();
    }

    private static void atualizarScoreboardParty() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (partyMembers.containsKey(player.getUniqueId())) {
                        UUID leaderUuid = partyMembers.get(player.getUniqueId());
                        Player leader = Bukkit.getPlayer(leaderUuid);
                        if (leader == null || !leader.isOnline()) {
                            dissolverParty(player);
                            return;
                        }

                        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        Objective objective = scoreboard.registerNewObjective("partyhealth", "health", Component.text("§6Party"));
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                        // Changed line 96
                        objective.getScore(LegacyComponentSerializer.legacySection().serialize(Component.text("§a" + leader.getName() + "§f:").append(Component.text(" " + Math.round(leader.getHealth()))))).setScore(0);

                        for (Map.Entry<UUID, UUID> entry : partyMembers.entrySet()) {
                            if (entry.getValue().equals(leaderUuid) && !entry.getKey().equals(leaderUuid)) {
                                Player member = Bukkit.getPlayer(entry.getKey());
                                if (member != null && member.isOnline()) {
                                    // Changed line 102
                                    objective.getScore(LegacyComponentSerializer.legacySection().serialize(Component.text("§a" + member.getName() + "§f:").append(Component.text(" " + Math.round(member.getHealth()))))).setScore(1);
                                }
                            }
                        }

                        player.setScoreboard(scoreboard);
                    } else {
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }
                }
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0L, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dissolverParty(event.getPlayer());
    }
}