package org.dantesys.reliquiasNexus.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.economia.Banco;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Economia {
    private static final Map<UUID, Double> saldos = new ConcurrentHashMap<>();
    private static final Map<String, Double> saldosTimes = new ConcurrentHashMap<>();
    private static final Map<UUID, Double> emprestimos = new ConcurrentHashMap<>();
    private static final Map<UUID, List<String>> historico = new ConcurrentHashMap<>();


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
    }

    public static void setSaldoTime(String teamName, double valor) {
        saldosTimes.put(teamName, valor);
    }

    public static void adicionarSaldo(Player player, double valor, String motivo) {
        double novoSaldo = getSaldo(player) + valor;
        saldos.put(player.getUniqueId(), novoSaldo);
        adicionarAoHistorico(player.getUniqueId(), "+" + String.format("%,.2f", valor) + " moly - " + motivo);
    }

    public static void adicionarSaldo(Banco banco, double valor, String motivo) {
        banco.setSaldo(banco.getSaldo() + valor);
    }

    public static void removerSaldo(Player player, double valor, String motivo) {
        double novoSaldo = getSaldo(player) - valor;
        if (novoSaldo < 0) novoSaldo = 0;
        saldos.put(player.getUniqueId(), novoSaldo);
        adicionarAoHistorico(player.getUniqueId(), "-" + String.format("%,.2f", valor) + " moly - " + motivo);
    }

    public static void adicionarSaldoTime(String teamName, double valor, String motivo) {
        double novoSaldo = getSaldoTime(teamName) + valor;
        saldosTimes.put(teamName, novoSaldo);
    }

    public static void removerSaldoTime(String teamName, double valor, String motivo) {
        double novoSaldo = getSaldoTime(teamName) - valor;
        if (novoSaldo < 0) novoSaldo = 0;
        saldosTimes.put(teamName, novoSaldo);
    }

    // Métodos de empréstimo
    public static boolean temEmprestimo(UUID jogador) {
        return emprestimos.containsKey(jogador) && emprestimos.get(jogador) > 0;
    }

    public static double getEmprestimo(UUID jogador) {
        return emprestimos.getOrDefault(jogador, 0.0);
    }

    public static void setEmprestimo(UUID jogador, double valor) {
        emprestimos.put(jogador, valor);
        adicionarAoHistorico(jogador, "Empréstimo definido: " + String.format("%,.2f", valor) + " moly");
    }

    public static void finalizarEmprestimo(UUID jogador) {
        emprestimos.remove(jogador);
        adicionarAoHistorico(jogador, "Empréstimo finalizado");
    }

    public static List<String> getHistorico(Player player) {
        return historico.getOrDefault(player.getUniqueId(), List.of("Sem histórico disponível"));
    }

    public static List<String> getHistorico(UUID playerId) {
        return historico.getOrDefault(playerId, List.of("Sem histórico disponível"));
    }

    private static void adicionarAoHistorico(UUID playerId, String transacao) {
        List<String> historicoPlayer = historico.getOrDefault(playerId, new ArrayList<>());
        historicoPlayer.addFirst("[" + new Date() + "] " + transacao);

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
            setEmprestimo(player.getUniqueId(), valorComJuros);
            player.sendMessage(Component.text("✅ Empréstimo de " + String.format("%,.2f", valor) +
                    " moly concedido! Total a pagar: " + String.format("%,.2f", valorComJuros) + " moly (50% de juros)").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("❌ O banco central não tem fundos suficientes para o empréstimo.").color(NamedTextColor.RED));
        }
    }

    public static boolean pagarEmprestimo(Player player) {
        UUID playerId = player.getUniqueId();
        if (!temEmprestimo(playerId)) {
            player.sendMessage(Component.text("❌ Você não tem nenhum empréstimo para pagar.").color(NamedTextColor.RED));
            return false;
        }

        double divida = getEmprestimo(playerId);
        double saldoPlayer = getSaldo(player);

        if (saldoPlayer >= divida) {
            // Remove o valor do jogador
            removerSaldo(player, divida, "Pagamento de Empréstimo");

            // Adiciona o valor ao banco
            Banco centralBank = Banco.getNexusCentralBank();
            centralBank.setSaldo(centralBank.getSaldo() + divida);

            // Finaliza o empréstimo
            finalizarEmprestimo(playerId);

            player.sendMessage(Component.text("✅ Você pagou sua dívida de " + String.format("%,.2f", divida) + " moly!").color(NamedTextColor.GREEN));
            return true;
        } else {
            player.sendMessage(Component.text("❌ Saldo insuficiente para pagar a dívida de " +
                    String.format("%,.2f", divida) + " moly. Seu saldo: " + String.format("%,.2f", saldoPlayer) + " moly").color(NamedTextColor.RED));
            return false;
        }
    }

    // Método para carregar dados do arquivo de configuração
    public static void carregarDados() {
        // Implementação para carregar saldos, empréstimos e histórico do arquivo
    }

    // Método para salvar dados no arquivo de configuração
    public static void salvarDados() {
        // Implementação para salvar saldos, empréstimos e histórico no arquivo
    }

    public static void adicionarSaldoTime(String teamName, double amount) {
    }

    public static void removerSaldo(Player player, double amount) {

    }

    public static void setPlugin(ReliquiasNexus reliquiasNexus) {

    }
}