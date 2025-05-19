package br.edu.fesa.sintatico;

import br.edu.fesa.semantico.Semantico.TipoDado;
import java.util.ArrayList;
import java.util.List;

public class ArvoreSintatica {

    private String valor;
    private TipoNo tipo;
    private int linha;
    private int coluna;
    private ArvoreSintatica esquerda;
    private ArvoreSintatica direita;
    private List<ArvoreSintatica> filhos = new ArrayList<>();

    private TipoDado tipoInferido;
    
    public ArvoreSintatica(String valor, TipoNo tipo, int linha, int coluna) {
        this.valor = valor;
        this.tipo = tipo;
        this.linha = linha;
        this.coluna = coluna;
    }

    public ArvoreSintatica(String valor, TipoNo tipo, ArvoreSintatica esquerda, ArvoreSintatica direita, int linha, int coluna) {
        this(valor, tipo, linha, coluna);
        this.esquerda = esquerda;
        this.direita = direita;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoNo getTipo() {
        return tipo;
    }

    public void setTipo(TipoNo tipo) {
        this.tipo = tipo;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public ArvoreSintatica getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(ArvoreSintatica esquerda) {
        this.esquerda = esquerda;
    }

    public ArvoreSintatica getDireita() {
        return direita;
    }

    public void setDireita(ArvoreSintatica direita) {
        this.direita = direita;
    }

    public List<ArvoreSintatica> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<ArvoreSintatica> filhos) {
        this.filhos = filhos;
    }

    public TipoDado getTipoInferido() {
        return tipoInferido;
    }

    public void setTipoInferido(TipoDado tipoInferido) {
        this.tipoInferido = tipoInferido;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildTreeString(sb, 0);
        return sb.toString();
    }

    private void buildTreeString(StringBuilder sb, int depth) {
        // Indentação conforme o nível de profundidade
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        // Mostra o tipo e valor do nó
        sb.append("[").append(tipo).append(": ").append(valor);
        
        // Mostra o tipo inferido se existir
        if (tipoInferido != null) {
            sb.append(" (").append(tipoInferido).append(")");
        }
        sb.append("]\n");
        
        // Processa os filhos recursivamente
        if (esquerda != null) {
            esquerda.buildTreeString(sb, depth + 1);
        }
        if (direita != null) {
            direita.buildTreeString(sb, depth + 1);
        }
        for (ArvoreSintatica filho : filhos) {
            filho.buildTreeString(sb, depth + 1);
        }
    }
}