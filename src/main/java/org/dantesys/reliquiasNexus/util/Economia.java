package org.dantesys.reliquiasNexus.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class Economia {

    public static double getSaldo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(SALDO.key, PersistentDataType.DOUBLE, 0.0);
    }

    public static void adicionarSaldo(Player player, double valor, String fonte) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double saldoAtual = getSaldo(player);
        data.set(SALDO.key, PersistentDataType.DOUBLE, saldoAtual + valor);
        adicionarHistorico(player, "Ganhou " + valor + " moly de " + fonte);
    }

    public static void removerSaldo(Player player, double valor) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double saldoAtual = getSaldo(player);
        data.set(SALDO.key, PersistentDataType.DOUBLE, saldoAtual - valor);
        adicionarHistorico(player, "Gastou " + valor + " moly");
    }

    public static boolean temEmprestimo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.has(EMPRESTIMO_ATUAL.key, PersistentDataType.DOUBLE);
    }

    public static double getEmprestimo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double divida = data.getOrDefault(EMPRESTIMO_ATUAL.key, PersistentDataType.DOUBLE, 0.0);
        long vencimento = data.getOrDefault(EMPRESTIMO_VENCIMENTO.key, PersistentDataType.LONG, 0L);
        double juros = data.getOrDefault(EMPRESTIMO_JUROS.key, PersistentDataType.DOUBLE, 0.0);

        if (vencimento > 0 && Instant.now().getEpochSecond() > vencimento) {
            // Empréstimo vencido, aplica penalidade
            return divida * (1 + juros);
        }
        return divida;
    }

    public static void concederEmprestimo(Player player, double valor, int horasParaPagar) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(EMPRESTIMO_ATUAL.key, PersistentDataType.DOUBLE, valor * 1.1);
        data.set(EMPRESTIMO_VENCIMENTO.key, PersistentDataType.LONG, Instant.now().getEpochSecond() + (long)horasParaPagar * 3600);
        data.set(EMPRESTIMO_JUROS.key, PersistentDataType.DOUBLE, 0.1);
        adicionarSaldo(player, valor, "Empréstimo");
    }

    public static void finalizarEmprestimo(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.remove(EMPRESTIMO_ATUAL.key);
        data.remove(EMPRESTIMO_VENCIMENTO.key);
        data.remove(EMPRESTIMO_JUROS.key);
        adicionarHistorico(player, "Pagou dívida de empréstimo");
    }

    public static List<String> getHistorico(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        String historicoString = data.getOrDefault(HISTORICO_MOLY.key, PersistentDataType.STRING, "");
        if (historicoString.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(historicoString.split(";")));
    }

    private static void adicionarHistorico(Player player, String entrada) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        List<String> historico = getHistorico(player);
        historico.add(entrada + " (" + Instant.now().toString() + ")");
        String novoHistorico = String.join(";", historico);
        data.set(HISTORICO_MOLY.key, PersistentDataType.STRING, novoHistorico);
    }

    public static double getSaldoTime(String teamName) {
        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        return config.getDouble("teams." + teamName + ".balance", 0.0);
    }

    public static void adicionarSaldoTime(String teamName, double valor) {
        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        double saldoAtual = getSaldoTime(teamName);
        config.set("teams." + teamName + ".balance", saldoAtual + valor);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();
        // TODO: Adicionar histórico do time
    }

    public static void removerSaldoTime(String teamName, double valor) {
        FileConfiguration config = ReliquiasNexus.getNexusConfig();
        double saldoAtual = getSaldoTime(teamName);
        config.set("teams." + teamName + ".balance", saldoAtual - valor);
        ReliquiasNexus.getPlugin(ReliquiasNexus.class).saveConfig();
        // TODO: Adicionar histórico do time
    }

    public static long getVencimentoEmprestimo(Player player) {
        return 0;
    }
}