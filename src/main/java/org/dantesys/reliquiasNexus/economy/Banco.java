package org.dantesys.reliquiasNexus.economia;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Emprestimo {
    // Classes internas para o sistema econômico
    private final UUID jogador;
    private final String banco;
    private final double valor;
    private final double valorTotal;
    private final long dataContracao;
    private int diasAtraso;

    public Emprestimo(UUID jogador, String banco, double valor, double juros) {
        this.jogador = jogador;
        this.banco = banco;
        this.valor = valor;
        this.valorTotal = valor * (1 + juros);
        this.dataContracao = System.currentTimeMillis();
        this.diasAtraso = 0;
    }

    // Getters
    public UUID getJogador() { return jogador; }
    public String getBanco() { return banco; }
    public double getValor() { return valor; }
    public double getValorTotal() { return valorTotal; }
    public long getDataContracao() { return dataContracao; }
    public int getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(int dias) { this.diasAtraso = dias; }

    public double getValorDevido() {
        long diasPassados = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - dataContracao);
        if (diasPassados > 3) {
            int diasAtraso = (int) (diasPassados - 3);
            double multa = valorTotal * (0.05 * diasAtraso);
            return valorTotal + multa;
        }
        return valorTotal;
    }
}
