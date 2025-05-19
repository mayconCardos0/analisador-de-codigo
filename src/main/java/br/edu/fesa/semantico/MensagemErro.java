package br.edu.fesa.semantico;

public class MensagemErro {
    private final int linha;
    private final int coluna;
    private final String codigo;
    private final String mensagem;
    
    public MensagemErro(int linha, int coluna, String codigo, String mensagem) {
        this.linha = linha;
        this.coluna = coluna;
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
    
    @Override
    public String toString() {
        return String.format("[%d:%d] %s: %s", linha, coluna, codigo, mensagem);
    }
    
    // Getters
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
    public String getCodigo() { return codigo; }
    public String getMensagem() { return mensagem; }
}