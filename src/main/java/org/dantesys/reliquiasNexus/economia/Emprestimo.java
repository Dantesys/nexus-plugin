package org.dantesys.reliquiasNexus.economia;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Emprestimo {
    // Classes internas para o sistema econômico
    private final UUID jogador;
    private final String banco;
    private final double valor;
    private final long dataContracao;
    private int minutosPassados;

    public Emprestimo(UUID jogador, String banco, double valor) {
        this.jogador = jogador;
        this.banco = banco;
        this.valor = valor;
        this.dataContracao = System.currentTimeMillis();
        this.minutosPassados = 0;
    }

    // Getters
    public UUID getJogador() { return jogador; }
    public String getBanco() { return banco; }
    public double getValor() { return valor; }
    public long getDataContracao() { return dataContracao; }
    public int getMinutosPassados() { return minutosPassados; }
    public void setMinutosPassados(int minutos) { this.minutosPassados = minutos; }

    public double getValorDevido() {
        long minutosAtuais = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - dataContracao);
        double jurosCompostos = Math.pow(1.01, minutosAtuais); // 1% de juros por minuto
        return valor * jurosCompostos;
    }
}