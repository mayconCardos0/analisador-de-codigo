package br.edu.fesa.semantico;

import br.edu.fesa.semantico.Semantico.TipoDado;

public class Simbolo {
    private final String nome;
    private final TipoDado tipo;
    private final int linhaDeclaracao;
    private final int colunaDeclaracao;
    private boolean utilizada = false;
    private boolean inicializada = false;
    
    public Simbolo(String nome, TipoDado tipo, int linha, int coluna) {
        this.nome = nome;
        this.tipo = tipo;
        this.linhaDeclaracao = linha;
        this.colunaDeclaracao = coluna;
    }
    
    // Getters e setters
    public void marcarComoUtilizada() { this.utilizada = true; }
    public void marcarComoInicializada() { this.inicializada = true; }
    public boolean foiUtilizada() { return utilizada; }
    public boolean foiInicializada() { return inicializada; }
    public String getNome() { return nome; }
    public TipoDado getTipo() { return tipo; }
    public int getLinhaDeclaracao() { return linhaDeclaracao; }
    public int getColunaDeclaracao() { return colunaDeclaracao; }
}