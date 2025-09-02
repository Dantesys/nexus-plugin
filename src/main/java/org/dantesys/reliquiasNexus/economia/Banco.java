package org.dantesys.reliquiasNexus.economia;

import java.util.UUID;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public class Banco {
    private final String nome;
    private final UUID dono;
    private double saldo;
    private double maxEmprestimo;
    private double taxaJuros;
    private double taxaSucesso;
    private String descricao;
    private boolean aprovado;

    public Banco(String nome, UUID dono, double saldoInicial) {
        this.nome = nome;
        this.dono = dono;
        this.saldo = saldoInicial;
        this.maxEmprestimo = 5000;
        this.taxaJuros = 0.2;
        this.taxaSucesso = 0.7;
        this.descricao = "Novo banco";
        this.aprovado = false;
    }

    public static Banco getNexusCentralBank() {
        return ReliquiasNexus.getNexusCentralBank();
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public UUID getDono() { return dono; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public double getMaxEmprestimo() { return maxEmprestimo; }
    public void setMaxEmprestimo(double max) { this.maxEmprestimo = max; }
    public double getTaxaJuros() { return taxaJuros; }
    public void setTaxaJuros(double taxa) { this.taxaJuros = taxa; }
    public double getTaxaSucesso() { return taxaSucesso; }
    public void setTaxaSucesso(double taxa) { this.taxaSucesso = taxa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String desc) { this.descricao = desc; }
    public boolean isAprovado() { return aprovado; }
    public void setAprovado(boolean aprovado) { this.aprovado = aprovado; }
}