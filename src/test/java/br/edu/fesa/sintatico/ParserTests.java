package br.edu.fesa.sintatico;

import br.edu.fesa.lexico.TipoToken;
import br.edu.fesa.lexico.Token;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ParserTests {
    
    @Test
    void deveAceitarExpressaoSimplesComOperadores() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 3),
            new Token(TipoToken.IDENTIFIER, "b", 1, 5),
            new Token(TipoToken.EOF, "$", 1, 6)
        );
        
        Parser parser = new Parser(tokens);
        assertTrue(parser.parse().isSuccess());
    }

    @Test
    void deveAceitarExpressoesComParentesesAninhados() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "*", 1, 3),
            new Token(TipoToken.LPAREN, "(", 1, 5),
            new Token(TipoToken.IDENTIFIER, "b", 1, 6),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 8),
            new Token(TipoToken.IDENTIFIER, "c", 1, 10),
            new Token(TipoToken.RPAREN, ")", 1, 11),
            new Token(TipoToken.EOF, "$", 1, 12)
        );
        
        assertTrue(new Parser(tokens).parse().isSuccess());
    }

    @Test
    void deveAceitarChamadaDeFuncaoValida() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "soma", 1, 1),
            new Token(TipoToken.LPAREN, "(", 1, 5),
            new Token(TipoToken.IDENTIFIER, "a", 1, 6),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 8),
            new Token(TipoToken.IDENTIFIER, "b", 1, 10),
            new Token(TipoToken.RPAREN, ")", 1, 11),
            new Token(TipoToken.EOF, "$", 1, 12)
        );
        
        assertTrue(new Parser(tokens).parse().isSuccess());
    }

    @Test
    void deveAceitarExpressaoComNumeros() {
        List<Token> tokens = List.of(
            new Token(TipoToken.NUMBER, "42", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 4),
            new Token(TipoToken.NUMBER, "3.14", 1, 6),
            new Token(TipoToken.EOF, "$", 1, 10)
        );
        
        assertTrue(new Parser(tokens).parse().isSuccess());
    }

    @Test
    void deveAceitarExpressaoComplexa() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "*", 1, 3),
            new Token(TipoToken.LPAREN, "(", 1, 5),
            new Token(TipoToken.IDENTIFIER, "b", 1, 6),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 8),
            new Token(TipoToken.LPAREN, "(", 1, 10),
            new Token(TipoToken.IDENTIFIER, "c", 1, 11),
            new Token(TipoToken.OP_ARITHMETIC, "/", 1, 13),
            new Token(TipoToken.NUMBER, "2", 1, 15),
            new Token(TipoToken.RPAREN, ")", 1, 16),
            new Token(TipoToken.RPAREN, ")", 1, 17),
            new Token(TipoToken.EOF, "$", 1, 18)
        );
        
        assertTrue(new Parser(tokens).parse().isSuccess());
    }

    @Test
    void naoDeveAceitarParentesesAbertos() {
        List<Token> tokens = List.of(
            new Token(TipoToken.LPAREN, "(", 1, 1),
            new Token(TipoToken.IDENTIFIER, "a", 1, 2),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 4),
            new Token(TipoToken.IDENTIFIER, "b", 1, 6),
            new Token(TipoToken.EOF, "$", 1, 7)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
    }

    @Test
    void naoDeveAceitarOperadoresConsecutivos() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 3),
            new Token(TipoToken.OP_ARITHMETIC, "*", 1, 5),
            new Token(TipoToken.IDENTIFIER, "b", 1, 7),
            new Token(TipoToken.EOF, "$", 1, 8)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getErrorMessage().contains("Esperado identificador"));
    }

    @Test
    void naoDeveAceitarChamadaDeFuncaoInvalida() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "soma", 1, 1),
            new Token(TipoToken.IDENTIFIER, "a", 1, 6),
            new Token(TipoToken.EOF, "$", 1, 7)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
    }

    @Test
    void naoDeveAceitarExpressaoVazia() {
        List<Token> tokens = List.of(
            new Token(TipoToken.EOF, "$", 1, 1)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
    }

    @Test
    void deveReportarPosicaoCorretaEmErros() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 3),
            new Token(TipoToken.OP_ARITHMETIC, "*", 1, 5), // Erro aqui
            new Token(TipoToken.IDENTIFIER, "b", 1, 7),
            new Token(TipoToken.EOF, "$", 1, 8)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getErrorMessage().contains("linha 1, coluna 5"));
    }

    @Test
    void deveAceitarMultiplasExpressoesComPontoEVirgula() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "a", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 3),
            new Token(TipoToken.IDENTIFIER, "b", 1, 5),
            new Token(TipoToken.SEMICOLON, ";", 1, 6),
            new Token(TipoToken.IDENTIFIER, "c", 2, 1),
            new Token(TipoToken.OP_ARITHMETIC, "*", 2, 3),
            new Token(TipoToken.NUMBER, "2", 2, 5),
            new Token(TipoToken.EOF, "$", 2, 6)
        );
        
        assertTrue(new Parser(tokens).parse().isSuccess());
    }

    @Test
    void naoDeveAceitarAtribuicoesInvalidas() {
        List<Token> tokens = List.of(
            new Token(TipoToken.NUMBER, "42", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 4),
            new Token(TipoToken.IDENTIFIER, "a", 1, 6),
            new Token(TipoToken.EOF, "$", 1, 7)
        );
        
        RetornoParser resultado = new Parser(tokens).parse();
        assertFalse(resultado.isSuccess());
    }
}