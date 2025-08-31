package org.dantesys.reliquiasNexus.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class Team implements Listener {

    private final ReliquiasNexus plugin;
    public Team(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    private static final Map<UUID, String> teamInvites = new HashMap<>();

    public static void abrirMenuTeam(Player player) {
        // Implementar menu principal do time
        player.sendMessage(Component.text("§6--- Comandos do Time ---").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("§e/nexus team criar <nome>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§e/nexus team convidar <player>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§e/nexus team depositar <valor>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§e/nexus team sair").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§e/nexus team excluir").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§e/nexus team setcargo <player> <cargo>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("§6------------------------").color(NamedTextColor.GOLD));
    }

    public static void criarTeam(Player player, String teamName) {
        if (getTeamName(player) != null) {
            player.sendMessage(Component.text("❌ Você já está em um time!").color(NamedTextColor.RED));
            return;
        }

        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        if (config.contains("teams." + teamName)) {
            player.sendMessage(Component.text("❌ Este nome de time já existe!").color(NamedTextColor.RED));
            return;
        }

        config.set("teams." + teamName + ".leader", player.getUniqueId().toString());
        config.set("teams." + teamName + ".members." + player.getUniqueId().toString(), "lider");
        config.set("teams." + teamName + ".balance", 0.0);
        ReliquiasNexus.setConfigSave("teams." + teamName + ".balance", 0.0);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();

        setTeamData(player, teamName, "lider");
        player.sendMessage(Component.text("✅ O time " + teamName + " foi criado com sucesso!").color(NamedTextColor.GREEN));
    }

    public static void convidarPlayer(Player inviter, Player target) {
        String teamName = getTeamName(inviter);
        if (teamName == null) {
            inviter.sendMessage(Component.text("❌ Você precisa estar em um time para convidar alguém.").color(NamedTextColor.RED));
            return;
        }

        String inviterRank = getPlayerRank(inviter);
        if (!inviterRank.equals("lider") && !inviterRank.equals("sub-lider")) {
            inviter.sendMessage(Component.text("❌ Apenas o líder ou sub-líder podem convidar jogadores.").color(NamedTextColor.RED));
            return;
        }

        if (getTeamName(target) != null) {
            inviter.sendMessage(Component.text("❌ " + target.getName() + " já está em um time.").color(NamedTextColor.RED));
            return;
        }

        teamInvites.put(target.getUniqueId(), teamName);

        inviter.sendMessage(Component.text("✅ Convite enviado para " + target.getName() + ".").color(NamedTextColor.GREEN));
        target.sendMessage(Component.text("§6Você foi convidado para o time " + teamName + " por " + inviter.getName() + "!").color(NamedTextColor.GOLD));
        target.sendMessage(Component.text("§aClique para aceitar").color(NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/nexus team aceitar " + teamName)));
    }

    public static void aceitarConvite(Player player, String teamName) {
        if (!teamInvites.containsKey(player.getUniqueId()) || !Objects.equals(teamInvites.get(player.getUniqueId()), teamName)) {
            player.sendMessage(Component.text("❌ Você não tem um convite para este time.").color(NamedTextColor.RED));
            return;
        }

        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        if (!config.contains("teams." + teamName)) {
            player.sendMessage(Component.text("❌ Este time não existe mais.").color(NamedTextColor.RED));
            return;
        }

        config.set("teams." + teamName + ".members." + player.getUniqueId().toString(), "membro");
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();

        setTeamData(player, teamName, "membro");

        teamInvites.remove(player.getUniqueId());
        player.sendMessage(Component.text("✅ Você se juntou ao time " + teamName + "!").color(NamedTextColor.GREEN));
    }

    public static void sairTeam(Player player) {
        String teamName = getTeamName(player);
        if (teamName == null) {
            player.sendMessage(Component.text("❌ Você não está em um time.").color(NamedTextColor.RED));
            return;
        }

        String playerRank = getPlayerRank(player);
        if (playerRank.equals("lider")) {
            player.sendMessage(Component.text("❌ O líder não pode sair do time. Use /nexus team excluir para apagar o time.").color(NamedTextColor.RED));
            return;
        }

        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        config.set("teams." + teamName + ".members." + player.getUniqueId().toString(), null);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();

        setTeamData(player, null, null);
        player.sendMessage(Component.text("✅ Você saiu do time " + teamName + ".").color(NamedTextColor.GREEN));
    }

    public static void depositar(Player player, double amount) {
        String teamName = getTeamName(player);
        if (teamName == null) {
            player.sendMessage(Component.text("❌ Você não está em um time.").color(NamedTextColor.RED));
            return;
        }

        if (Economia.getSaldo(player) < amount) {
            player.sendMessage(Component.text("❌ Você não tem moly suficiente para depositar.").color(NamedTextColor.RED));
            return;
        }

        Economia.removerSaldo(player, amount);
        Economia.adicionarSaldoTime(teamName, amount);
        player.sendMessage(Component.text("✅ Você depositou " + amount + " moly no banco do time.").color(NamedTextColor.GREEN));
    }

    public static String getTeamName(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(NexusKeys.TEAM_NAME.key, PersistentDataType.STRING)) {
            return data.get(NexusKeys.TEAM_NAME.key, PersistentDataType.STRING);
        }
        return null;
    }

    public static String getPlayerRank(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(NexusKeys.TEAM_RANK.key, PersistentDataType.STRING)) {
            return data.get(NexusKeys.TEAM_RANK.key, PersistentDataType.STRING);
        }
        return "membro";
    }

    private static void setTeamData(Player player, String teamName, String rank) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (teamName == null) {
            data.remove(NexusKeys.TEAM_NAME.key);
            data.remove(NexusKeys.TEAM_RANK.key);
        } else {
            data.set(NexusKeys.TEAM_NAME.key, PersistentDataType.STRING, teamName);
            data.set(NexusKeys.TEAM_RANK.key, PersistentDataType.STRING, rank);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Verificar se o jogador tem um time salvo no config e atualizar o PDC
        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        if (config.getConfigurationSection("teams") != null) {
            for (String teamName : Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false)) {
                if (config.contains("teams." + teamName + ".members." + player.getUniqueId().toString())) {
                    String rank = config.getString("teams." + teamName + ".members." + player.getUniqueId().toString());
                    setTeamData(player, teamName, rank);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // A responsabilidade de dissolver a party está na classe Party.java
    }
}