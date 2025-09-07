package org.dantesys.reliquiasNexus.economia;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class economia {
    private static final Map<UUID, Double> saldos = new ConcurrentHashMap<>();
    private static final Map<String, Double> saldosTimes = new ConcurrentHashMap<>();
    private static final Map<UUID, Double> emprestimos = new ConcurrentHashMap<>();
    private static final Map<UUID, List<String>> historico = new ConcurrentHashMap<>();

    private static ReliquiasNexus plugin;

    public static void setPlugin(ReliquiasNexus pluginInstance) {
        plugin = pluginInstance;
        carregarDados(); // Carrega dados quando o plugin é definido
    }

    public static double getSaldo(Player player) {
        return saldos.getOrDefault(player.getUniqueId(), 0.0);
    }

    public static double getSaldo(UUID playerId) {
        return saldos.getOrDefault(playerId, 0.0);
    }

    public static double getSaldoTime(String teamName) {
        return saldosTimes.getOrDefault(teamName, 0.0);
    }

    public static void setSaldo(Player player, double valor) {
        saldos.put(player.getUniqueId(), valor);
        adicionarAoHistorico(player.getUniqueId(), "Saldo definido para: " + String.format("%,.2f", valor) + " moly");
        salvarDados(); // Salva após alteração
    }

    public static void setSaldoTime(String teamName, double valor) {
        saldosTimes.put(teamName, valor);
    }

    public static void adicionarSaldo(Player player, double valor, String motivo) {
        double novoSaldo = getSaldo(player) + valor;
        saldos.put(player.getUniqueId(), novoSaldo);
        adicionarAoHistorico(player.getUniqueId(), "+" + String.format("%,.2f", valor) + " moly - " + motivo);
        salvarDados(); // Salva após alteração
    }

    public static void adicionarSaldo(Banco banco, double valor, String motivo) {
        banco.setSaldo(banco.getSaldo() + valor);
    }

    public static void removerSaldo(Player player, double valor, String motivo) {
        double novoSaldo = getSaldo(player) - valor;
        saldos.put(player.getUniqueId(), novoSaldo);
        adicionarAoHistorico(player.getUniqueId(), "-" + String.format("%,.2f", valor) + " moly - " + motivo);
        salvarDados(); // Salva após alteração
    }

    public static void adicionarSaldoTime(String teamName, double valor, String motivo) {
        double novoSaldo = getSaldoTime(teamName) + valor;
        saldosTimes.put(teamName, novoSaldo);
    }

    public static void removerSaldoTime(String teamName, double valor, String motivo) {
        double novoSaldo = getSaldoTime(teamName) - valor;
        saldosTimes.put(teamName, novoSaldo);
    }

    public static boolean temEmprestimo(UUID jogador) {
        return emprestimos.containsKey(jogador);
    }

    public static double getEmprestimo(UUID jogador) {
        return emprestimos.getOrDefault(jogador, 0.0);
    }

    public static void finalizarEmprestimo(UUID jogador) {
        emprestimos.remove(jogador);
        adicionarAoHistorico(jogador, "Empréstimo finalizado");
        salvarDados(); // Salva após alteração
    }

    public static List<String> getHistorico(Player player) {
        return historico.getOrDefault(player.getUniqueId(), Arrays.asList("Sem histórico disponível"));
    }

    public static List<String> getHistorico(UUID playerId) {
        return historico.getOrDefault(playerId, Arrays.asList("Sem histórico disponível"));
    }

    private static void adicionarAoHistorico(UUID playerId, String transacao) {
        List<String> historicoPlayer = historico.getOrDefault(playerId, new ArrayList<>());
        historicoPlayer.add(0, "[" + new Date() + "] " + transacao);

        // Manter apenas as últimas 20 transações
        if (historicoPlayer.size() > 20) {
            historicoPlayer = historicoPlayer.subList(0, 20);
        }

        historico.put(playerId, historicoPlayer);
    }

    public static void processarVendaMinerio(Player player, double valor) {
        Banco centralBank = Banco.getNexusCentralBank();
        double bonusBanco = valor * 0.5; // 50% a mais para o banco

        // Adiciona valor ao jogador
        adicionarSaldo(player, valor, "Venda de Minério");

        // Adiciona valor + bônus ao banco
        centralBank.setSaldo(centralBank.getSaldo() + valor + bonusBanco);

        player.sendMessage(Component.text("✅ Você vendeu minérios por " + String.format("%,.2f", valor) + " moly!").color(NamedTextColor.GREEN));
    }

    public static void processarRecompensaMissao(Player player, double valor) {
        Banco centralBank = Banco.getNexusCentralBank();
        double bonusBanco = valor * 0.5; // 50% a mais para o banco

        // Verifica se o banco tem saldo suficiente
        if (centralBank.getSaldo() >= valor) {
            // Adiciona valor ao jogador
            adicionarSaldo(player, valor, "Recompensa de Missão");

            // Remove valor do banco mas adiciona o bônus
            centralBank.setSaldo(centralBank.getSaldo() - valor + bonusBanco);

            player.sendMessage(Component.text("✅ Você recebeu " + String.format("%,.2f", valor) + " moly pela missão!").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("❌ O banco central não tem fundos suficientes para pagar a recompensa.").color(NamedTextColor.RED));
        }
    }

    public static void processarEmprestimo(Player player, double valor) {
        Banco centralBank = Banco.getNexusCentralBank();
        double valorComJuros = valor * 1.5; // 50% de juros

        if (centralBank.getSaldo() >= valor) {
            // Adiciona valor ao jogador
            adicionarSaldo(player, valor, "Empréstimo Bancário");

            // Remove valor do banco
            centralBank.setSaldo(centralBank.getSaldo() - valor);

            // Registra o empréstimo com juros
            emprestimos.put(player.getUniqueId(), valorComJuros);
            adicionarAoHistorico(player.getUniqueId(), "Empréstimo contraído: " + String.format("%,.2f", valor) + " moly (Juros: " + String.format("%,.2f", valorComJuros) + " moly)");
            salvarDados(); // Salva após alteração
        } else {
            player.sendMessage(Component.text("❌ O banco central não tem fundos suficientes para o empréstimo.").color(NamedTextColor.RED));
        }
    }

    // Método para carregar dados do arquivo de configuração
    public static void carregarDados() {
        if (plugin == null) return;

        saldos.clear();
        emprestimos.clear();
        historico.clear();

        // Carregar saldos dos jogadores
        if (plugin.getConfig().contains("player_balances")) {
            ConfigurationSection balancesSection = plugin.getConfig().getConfigurationSection("player_balances");
            if(balancesSection != null) {
                for (String uuidStr : balancesSection.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        double balance = balancesSection.getDouble(uuidStr);
                        saldos.put(uuid, balance);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("UUID inválido encontrado em player_balances: " + uuidStr);
                    }
                }
            }
        }

        // Carregar empréstimos (se existirem)
        if (plugin.getConfig().contains("emprestimos")) {
            ConfigurationSection loansSection = plugin.getConfig().getConfigurationSection("emprestimos");
            if(loansSection != null) {
                for (String uuidStr : loansSection.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        double loanAmount = loansSection.getDouble(uuidStr);
                        emprestimos.put(uuid, loanAmount);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("UUID inválido encontrado em emprestimos: " + uuidStr);
                    }
                }
            }
        }

        // Carregar histórico (se existir)
        if (plugin.getConfig().contains("historico")) {
            ConfigurationSection historySection = plugin.getConfig().getConfigurationSection("historico");
            if(historySection != null) {
                for (String uuidStr : historySection.getKeys(false)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        List<String> history = historySection.getStringList(uuidStr);
                        historico.put(uuid, history);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("UUID inválido encontrado em historico: " + uuidStr);
                    }
                }
            }
        }

        plugin.getLogger().info("Dados econômicos carregados: " + saldos.size() + " saldos, " + emprestimos.size() + " empréstimos");
    }

    // Método para salvar dados no arquivo de configuração
    public static void salvarDados() {
        if (plugin == null) return;

        // Limpar seções antes de salvar para evitar dados antigos
        if (plugin.getConfig().contains("player_balances")) {
            plugin.getConfig().set("player_balances", null);
        }
        if (plugin.getConfig().contains("emprestimos")) {
            plugin.getConfig().set("emprestimos", null);
        }
        if (plugin.getConfig().contains("historico")) {
            plugin.getConfig().set("historico", null);
        }

        // Salvar saldos dos jogadores
        for (Map.Entry<UUID, Double> entry : saldos.entrySet()) {
            plugin.getConfig().set("player_balances." + entry.getKey().toString(), entry.getValue());
        }

        // Salvar empréstimos
        for (Map.Entry<UUID, Double> entry : emprestimos.entrySet()) {
            plugin.getConfig().set("emprestimos." + entry.getKey().toString(), entry.getValue());
        }

        // Salvar histórico
        for (Map.Entry<UUID, List<String>> entry : historico.entrySet()) {
            plugin.getConfig().set("historico." + entry.getKey().toString(), entry.getValue());
        }

        plugin.saveConfig();
        plugin.getLogger().info("Dados econômicos salvos: " + saldos.size() + " saldos, " + emprestimos.size() + " empréstimos");
    }
}