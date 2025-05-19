package br.edu.fesa.semantico;

import br.edu.fesa.lexico.Token;
import br.edu.fesa.sintatico.ArvoreSintatica;
import java.util.List;

public class ResultadoAnalise {
    private final boolean sucesso;
    private final List<MensagemErro> erros;
    private final TabelaDeSimbolos tabelaSimbolos;
    
    public ResultadoAnalise(boolean sucesso, List<MensagemErro> erros, TabelaDeSimbolos tabelaSimbolos) {
        this.sucesso = sucesso;
        this.erros = erros;
        this.tabelaSimbolos = tabelaSimbolos;
    }
    
    // Getters
    public boolean isSucesso() { return sucesso; }
    public List<MensagemErro> getErros() { return erros; }
    public TabelaDeSimbolos getTabelaSimbolos() { return tabelaSimbolos; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n=== RESULTADO DA ANALISE ===\n");
        sb.append("Status: ").append(sucesso ? "SUCESSO" : "FALHA").append("\n\n");
        
        sb.append(tabelaSimbolos.toString()).append("\n");
        
        if (!erros.isEmpty()) {
            sb.append("\n=== ERROS ENCONTRADOS ===\n");
            for (MensagemErro erro : erros) {
                sb.append(erro.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
}