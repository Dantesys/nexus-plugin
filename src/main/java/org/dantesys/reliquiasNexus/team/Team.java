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
        if (!inviterRank.equals("lider") && !inviterRank.equals("sub-lider") && !inviterRank.equals("confiavel")) {
            inviter.sendMessage(Component.text("❌ Apenas o líder, sub-líder ou confiável podem convidar jogadores.").color(NamedTextColor.RED));
            return;
        }

        if (getTeamName(target) != null) {
            inviter.sendMessage(Component.text("❌ " + target.getName() + " já está em um time.").color(NamedTextColor.RED));
            return;
        }

        teamInvites.put(target.getUniqueId(), teamName);

        inviter.sendMessage(Component.text("✅ Convite enviado para " + target.getName() + ".").color(NamedTextColor.GREEN));

        // Mensagem de convite com botões clicáveis
        target.sendMessage(Component.text("§6Você foi convidado para o time " + teamName + " por " + inviter.getName() + "!").color(NamedTextColor.GOLD));
        Component inviteMessage = Component.text("§a[ACEITAR] ").clickEvent(ClickEvent.runCommand("/nexus team aceitar " + inviter.getName()));
        inviteMessage = inviteMessage.append(Component.text("§c[RECUSAR]").clickEvent(ClickEvent.runCommand("/nexus team recusar " + inviter.getName())));
        target.sendMessage(inviteMessage);
    }

    public static void aceitarConvite(Player player, String inviterName) {
        Player inviter = Bukkit.getPlayer(inviterName);
        if (inviter == null || !teamInvites.containsKey(player.getUniqueId()) || !Objects.equals(teamInvites.get(player.getUniqueId()), getTeamName(inviter))) {
            player.sendMessage(Component.text("❌ Este convite não é mais válido.").color(NamedTextColor.RED));
            return;
        }

        String teamName = getTeamName(inviter);

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
        if (inviter.isOnline()) {
            inviter.sendMessage(Component.text("✅ " + player.getName() + " aceitou seu convite e se juntou ao time.").color(NamedTextColor.GREEN));
        }
    }

    public static void recusarConvite(Player player, String inviterName) {
        Player inviter = Bukkit.getPlayer(inviterName);
        if (inviter == null || !teamInvites.containsKey(player.getUniqueId()) || !Objects.equals(teamInvites.get(player.getUniqueId()), getTeamName(inviter))) {
            player.sendMessage(Component.text("❌ Este convite não é mais válido.").color(NamedTextColor.RED));
            return;
        }

        teamInvites.remove(player.getUniqueId());
        player.sendMessage(Component.text("❌ Você recusou o convite do time de " + inviter.getName() + ".").color(NamedTextColor.RED));
        if (inviter.isOnline()) {
            inviter.sendMessage(Component.text("❌ " + player.getName() + " recusou seu convite para o time.").color(NamedTextColor.RED));
        }
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

    public static void excluirTeam(Player player) {
        String teamName = getTeamName(player);
        if (teamName == null) {
            player.sendMessage(Component.text("❌ Você não está em um time.").color(NamedTextColor.RED));
            return;
        }

        String playerRank = getPlayerRank(player);
        if (!playerRank.equals("lider")) {
            player.sendMessage(Component.text("❌ Apenas o líder pode excluir o time.").color(NamedTextColor.RED));
            return;
        }

        FileConfiguration config = ReliquiasNexus.getNexusConfig();

        // Remove all members from the team data
        if (config.contains("teams." + teamName + ".members")) {
            for (String memberUUID : Objects.requireNonNull(config.getConfigurationSection("teams." + teamName + ".members")).getKeys(false)) {
                Player memberPlayer = Bukkit.getPlayer(UUID.fromString(memberUUID));
                if (memberPlayer != null && memberPlayer.isOnline()) {
                    setTeamData(memberPlayer, null, null);
                    memberPlayer.sendMessage(Component.text("❌ O seu time foi excluído por " + player.getName() + ".").color(NamedTextColor.RED));
                }
            }
        }

        config.set("teams." + teamName, null);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();

        player.sendMessage(Component.text("✅ O time " + teamName + " foi excluído com sucesso!").color(NamedTextColor.GREEN));
    }

    public static void setRank(Player sender, Player target, String rank) {
        String teamName = getTeamName(sender);
        if (teamName == null) {
            sender.sendMessage(Component.text("❌ Você precisa estar em um time para definir o cargo.").color(NamedTextColor.RED));
            return;
        }

        if (!getTeamName(target).equals(teamName)) {
            sender.sendMessage(Component.text("❌ " + target.getName() + " não está no seu time.").color(NamedTextColor.RED));
            return;
        }

        String senderRank = getPlayerRank(sender);

        if (senderRank.equals("membro") || senderRank.equals("confiavel")) {
            sender.sendMessage(Component.text("❌ Você não tem permissão para alterar cargos.").color(NamedTextColor.RED));
            return;
        }

        if (rank.equals("lider") && !senderRank.equals("lider")) {
            sender.sendMessage(Component.text("❌ Apenas o líder pode promover para líder.").color(NamedTextColor.RED));
            return;
        }

        if (!rank.equals("lider") && !rank.equals("sub-lider") && !rank.equals("confiavel") && !rank.equals("membro")) {
            sender.sendMessage(Component.text("❌ Cargo inválido. Use: lider, sub-lider, confiavel ou membro.").color(NamedTextColor.RED));
            return;
        }

        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        config.set("teams." + teamName + ".members." + target.getUniqueId().toString(), rank);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();

        setTeamData(target, teamName, rank);
        sender.sendMessage(Component.text("✅ O cargo de " + target.getName() + " foi definido para " + rank + ".").color(NamedTextColor.GREEN));
        target.sendMessage(Component.text("✅ O seu cargo no time " + teamName + " foi alterado para " + rank + ".").color(NamedTextColor.GREEN));
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