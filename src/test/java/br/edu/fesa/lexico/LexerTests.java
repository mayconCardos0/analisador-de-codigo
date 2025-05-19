package br.edu.fesa.lexico;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LexerTests {
    
    @Test
    void deveGerarTokensIdentificadoresCorretamente() {
        Lexer lexer = new Lexer("abc xyz123 var_1");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(4, tokens.size()); // 3 identificadores + EOF
        assertEquals(TipoToken.IDENTIFIER, tokens.get(0).getType());
        assertEquals("abc", tokens.get(0).getLexeme());
        assertEquals(TipoToken.IDENTIFIER, tokens.get(1).getType());
        assertEquals("xyz123", tokens.get(1).getLexeme());
        assertEquals(TipoToken.IDENTIFIER, tokens.get(2).getType());
        assertEquals("var_1", tokens.get(2).getLexeme());
    }

    @Test
    void deveReconhecerTodosOperadoresMatematicosDaLinguagem() {
        Lexer lexer = new Lexer("+-*/");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(5, tokens.size()); // 4 operadores + EOF
        assertEquals(TipoToken.OP_ARITHMETIC, tokens.get(0).getType());
        assertEquals("+", tokens.get(0).getLexeme());
        assertEquals(TipoToken.OP_ARITHMETIC, tokens.get(1).getType());
        assertEquals("-", tokens.get(1).getLexeme());
        assertEquals(TipoToken.OP_ARITHMETIC, tokens.get(2).getType());
        assertEquals("*", tokens.get(2).getLexeme());
        assertEquals(TipoToken.OP_ARITHMETIC, tokens.get(3).getType());
        assertEquals("/", tokens.get(3).getLexeme());
    }

    @Test
    void deveDistinguirParentesesAbertosEFechados() {
        Lexer lexer = new Lexer("()");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(3, tokens.size()); // 2 parênteses + EOF
        assertEquals(TipoToken.LPAREN, tokens.get(0).getType());
        assertEquals("(", tokens.get(0).getLexeme());
        assertEquals(TipoToken.RPAREN, tokens.get(1).getType());
        assertEquals(")", tokens.get(1).getLexeme());
    }

    @Test
    void deveIgnorarEspacosEmBranco() {
        Lexer lexer = new Lexer("  a  +  b  ");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(4, tokens.size()); // a + b + EOF
        assertEquals("a", tokens.get(0).getLexeme());
        assertEquals("+", tokens.get(1).getLexeme());
        assertEquals("b", tokens.get(2).getLexeme());
        assertEquals("$", tokens.get(3).getLexeme());
    }

    @Test
    void deveReconhecerNumerosInteirosEDecimais() {
        Lexer lexer = new Lexer("42 3.14 0.5");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(4, tokens.size()); // 3 números + EOF
        assertEquals(TipoToken.NUMBER, tokens.get(0).getType());
        assertEquals("42", tokens.get(0).getLexeme());
        assertEquals(TipoToken.NUMBER, tokens.get(1).getType());
        assertEquals("3.14", tokens.get(1).getLexeme());
        assertEquals(TipoToken.NUMBER, tokens.get(2).getType());
        assertEquals("0.5", tokens.get(2).getLexeme());
    }

    @Test
    void deveReconhecerPalavrasChave() {
        Lexer lexer = new Lexer("if else while for return");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(6, tokens.size()); // 5 palavras-chave + EOF
        assertEquals(TipoToken.KEYWORD, tokens.get(0).getType());
        assertEquals("if", tokens.get(0).getLexeme());
        assertEquals(TipoToken.KEYWORD, tokens.get(1).getType());
        assertEquals("else", tokens.get(1).getLexeme());
        assertEquals(TipoToken.KEYWORD, tokens.get(2).getType());
        assertEquals("while", tokens.get(2).getLexeme());
        assertEquals(TipoToken.KEYWORD, tokens.get(3).getType());
        assertEquals("for", tokens.get(3).getLexeme());
        assertEquals(TipoToken.KEYWORD, tokens.get(4).getType());
        assertEquals("return", tokens.get(4).getLexeme());
    }

    @Test
    void deveReconhecerOperadoresRelacionais() {
        Lexer lexer = new Lexer("< > <= >= == !=");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(7, tokens.size()); // 6 operadores + EOF
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(0).getType());
        assertEquals("<", tokens.get(0).getLexeme());
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(1).getType());
        assertEquals(">", tokens.get(1).getLexeme());
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(2).getType());
        assertEquals("<=", tokens.get(2).getLexeme());
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(3).getType());
        assertEquals(">=", tokens.get(3).getLexeme());
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(4).getType());
        assertEquals("==", tokens.get(4).getLexeme());
        assertEquals(TipoToken.OP_RELATIONAL, tokens.get(5).getType());
        assertEquals("!=", tokens.get(5).getLexeme());
    }

    @Test
    void deveMarcarErroParaCaracteresInvalidos() {
        Lexer lexer = new Lexer("a @ b");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(4, tokens.size()); // a, erro, b + EOF
        assertEquals(TipoToken.IDENTIFIER, tokens.get(0).getType());
        assertEquals(TipoToken.ERROR, tokens.get(1).getType());
        assertEquals("@", tokens.get(1).getLexeme());
        assertEquals(TipoToken.IDENTIFIER, tokens.get(2).getType());
    }

    @Test
    void deveManterContadorDeLinhasEColunasCorretamente() {
        Lexer lexer = new Lexer("a + b\nc = 42");
        List<Token> tokens = lexer.generateTokens();
        
        // Verifica posições dos tokens
        assertEquals(1, tokens.get(0).getLine()); // a
        assertEquals(1, tokens.get(0).getColumn());
        
        assertEquals(1, tokens.get(1).getLine()); // +
        assertEquals(3, tokens.get(1).getColumn());
        
        assertEquals(1, tokens.get(2).getLine()); // b
        assertEquals(5, tokens.get(2).getColumn());
        
        assertEquals(2, tokens.get(3).getLine()); // c
        assertEquals(1, tokens.get(3).getColumn());
        
        assertEquals(2, tokens.get(4).getLine()); // =
        assertEquals(3, tokens.get(4).getColumn());
        
        assertEquals(2, tokens.get(5).getLine()); // 42
        assertEquals(5, tokens.get(5).getColumn());
    }

    @Test
    void deveReconhecerComentariosDeLinha() {
        Lexer lexer = new Lexer("a + b // comentário\nc");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(5, tokens.size()); // a, +, b, c + EOF
        assertEquals("a", tokens.get(0).getLexeme());
        assertEquals("+", tokens.get(1).getLexeme());
        assertEquals("b", tokens.get(2).getLexeme());
        assertEquals("c", tokens.get(3).getLexeme());
    }

    @Test
    void deveReconhecerComentariosDeBloco() {
        Lexer lexer = new Lexer("a /* comentário \n multi-linha */ b");
        List<Token> tokens = lexer.generateTokens();
        
        assertEquals(3, tokens.size()); // a, b + EOF
        assertEquals("a", tokens.get(0).getLexeme());
        assertEquals("b", tokens.get(1).getLexeme());
    }
}