package br.edu.fesa.sintatico;

import br.edu.fesa.lexico.TipoToken;
import br.edu.fesa.lexico.Token;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        if (!tokens.isEmpty()) {
            this.currentToken = tokens.get(0);
        }
    }

    public RetornoParser parse() {
        try {
            ArvoreSintatica arvore = parseE();
            
            // Verifica se chegou ao final ou tem ponto e vírgula
            if (currentToken.type != TipoToken.EOF && 
                currentToken.type != TipoToken.SEMICOLON) {
                throw new SyntaxException("Esperado fim de expressao ou ';'", currentToken);
            }
            
            // Consome o ponto e vírgula se existir
            if (currentToken.type == TipoToken.SEMICOLON) {
                advance();
            }
            
            return new RetornoParser(true, null, tokens, arvore);
        } catch (SyntaxException e) {
            return new RetornoParser(false, e.getMessage(), tokens, null);
        }
    }

    private ArvoreSintatica parseE() throws SyntaxException {
        ArvoreSintatica left = parseT();

        while (currentToken.type == TipoToken.OP_ARITHMETIC && 
               (currentToken.lexeme.equals("+") || currentToken.lexeme.equals("-"))) {
            Token op = currentToken;
            advance();
            ArvoreSintatica right = parseT();
            left = new ArvoreSintatica(
                op.lexeme, 
                TipoNo.OPERADOR, 
                left, 
                right, 
                op.line, 
                op.column
            );
        }

        return left;
    }

    private ArvoreSintatica parseT() throws SyntaxException {
        ArvoreSintatica left = parseF();

        while (currentToken.type == TipoToken.OP_ARITHMETIC && 
               (currentToken.lexeme.equals("*") || currentToken.lexeme.equals("/"))) {
            Token op = currentToken;
            advance();
            ArvoreSintatica right = parseF();
            left = new ArvoreSintatica(
                op.lexeme, 
                TipoNo.OPERADOR, 
                left, 
                right, 
                op.line, 
                op.column
            );
        }

        return left;
    }

    private ArvoreSintatica parseF() throws SyntaxException {
        if (currentToken.type == TipoToken.IDENTIFIER) {
            Token id = currentToken;
            advance();

            if (currentToken.type == TipoToken.LPAREN) {
                // Chamada de função: id ( E )
                advance();
                ArvoreSintatica args = parseE();
                consume(TipoToken.RPAREN);
                return new ArvoreSintatica(
                    id.lexeme,
                    TipoNo.CHAMADA_FUNCAO,
                    args,
                    null,
                    id.line,
                    id.column
                );
            } else {
                // Identificador simples
                return new ArvoreSintatica(
                    id.lexeme, 
                    TipoNo.IDENTIFICADOR, 
                    id.line, 
                    id.column
                );
            }
        } else if (currentToken.type == TipoToken.LPAREN) {
            // ( E )
            advance();
            ArvoreSintatica expr = parseE();
            consume(TipoToken.RPAREN);
            return expr;
        } else if (currentToken.type == TipoToken.NUMBER) {
            Token num = currentToken;
            advance();
            return new ArvoreSintatica(
                num.lexeme, 
                TipoNo.NUMERO, 
                num.line, 
                num.column
            );
        } else {
            throw new SyntaxException(
                "Esperado identificador, numero ou '(', encontrado: " + currentToken.lexeme, 
                currentToken
            );
        }
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(TipoToken.EOF, "$", -1, -1);
        }
    }

    private void consume(TipoToken expected) throws SyntaxException {
        if (currentToken.type == expected) {
            advance();
        } else {
            throw new SyntaxException(expected, currentToken);
        }
    }

    private static class SyntaxException extends Exception {
        public SyntaxException(TipoToken expected, Token found) {
            super(String.format(
                "Erro sintatico na linha %d, coluna %d: Esperado %s, encontrado %s '%s'",
                found.line, found.column, expected, found.type, found.lexeme
            ));
        }

        public SyntaxException(String message, Token token) {
            super(String.format(
                "Erro sintatico na linha %d, coluna %d: %s",
                token.line, token.column, cleanMessage(message)
            ));
        }

        private static String cleanMessage(String message) {
            return message.replace("�", "")
                         .replace("´", "")
                         .replace("`", "")
                         .replace("^", "");
        }
    }
}