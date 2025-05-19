package br.edu.fesa.semantico;

import br.edu.fesa.semantico.Semantico.TipoDado;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TabelaDeSimbolos {
    private final Stack<Map<String, Simbolo>> escopos = new Stack<>();
    
    public TabelaDeSimbolos() {
        entrarEscopo(); // Escopo global
    }
    
    public void entrarEscopo() {
        escopos.push(new HashMap<>());
    }
    
    public void sairEscopo() {
        if (escopos.size() > 1) {
            escopos.pop();
        }
    }
    
    public void declarar(String nome, TipoDado tipo, int linha, int coluna) {
        if (existeNoEscopoAtual(nome)) {
            throw new RuntimeException("Variável '" + nome + "' já declarada neste escopo");
        }
        escopos.peek().put(nome, new Simbolo(nome, tipo, linha, coluna));
    }
    
    public boolean existeNoEscopoAtual(String nome) {
        return escopos.peek().containsKey(nome);
    }
    
    public Simbolo buscar(String nome) {
        for (int i = escopos.size() - 1; i >= 0; i--) {
            if (escopos.get(i).containsKey(nome)) {
                return escopos.get(i).get(nome);
            }
        }
        return null;
    }
    
    public List<Simbolo> getTodosSimbolos() {
        List<Simbolo> simbolos = new ArrayList<>();
        escopos.forEach(escopo -> simbolos.addAll(escopo.values()));
        return simbolos;
    }
    
     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TABELA DE SIMBOLOS ===\n");
        
        int escopoNum = escopos.size();
        for (Map<String, Simbolo> escopo : escopos) {
            sb.append("\nEscopo #").append(escopoNum--).append(":\n");
            
            if (escopo.isEmpty()) {
                sb.append("  (vazio)\n");
            } else {
                for (Map.Entry<String, Simbolo> entry : escopo.entrySet()) {
                    Simbolo simbolo = entry.getValue();
                    sb.append("  ")
                      .append(simbolo.getNome())
                      .append(": ")
                      .append(simbolo.getTipo())
                      .append(" (Linha: ")
                      .append(simbolo.getLinhaDeclaracao())
                      .append(", Coluna: ")
                      .append(simbolo.getColunaDeclaracao())
                      .append(")")
                      .append(simbolo.foiUtilizada() ? "" : " [NAO UTILIZADA]")
                      .append("\n");
                }
            }
        }
        
        return sb.toString();
    }
}