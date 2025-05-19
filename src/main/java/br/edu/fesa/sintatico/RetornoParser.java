package br.edu.fesa.sintatico;

import br.edu.fesa.lexico.Token;
import java.util.List;

public class RetornoParser {
    private final boolean success;
    private final String errorMessage;
    private final List<Token> tokens;
    private final ArvoreSintatica arvoreSintatica;

    public RetornoParser(boolean success, String errorMessage, List<Token> tokens, ArvoreSintatica arvoreSintatica) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.tokens = tokens;
        this.arvoreSintatica = arvoreSintatica;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public ArvoreSintatica getArvoreSintatica() {
        return arvoreSintatica;
    }
}