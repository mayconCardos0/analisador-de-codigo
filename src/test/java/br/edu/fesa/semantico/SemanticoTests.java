package br.edu.fesa.semantico;

import br.edu.fesa.lexico.TipoToken;
import br.edu.fesa.lexico.Token;
import br.edu.fesa.sintatico.ArvoreSintatica;
import br.edu.fesa.sintatico.TipoNo;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SemanticoTests {

    @Test
    void deveAceitarVariavelDeclarada() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "x", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 3),
            new Token(TipoToken.NUMBER, "5", 1, 5),
            new Token(TipoToken.EOF, "$", 1, 6)
        );
        
        // Construir árvore sintática simulada
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("5", TipoNo.NUMERO, 1, 5),
            1, 1
        );
        
        Semantico semantico = new Semantico(arvore, tokens);
        assertTrue(semantico.analisar().isSucesso());
    }

    @Test
    void deveDetectarVariavelNaoDeclarada() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "x", 1, 1),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 3),
            new Token(TipoToken.IDENTIFIER, "y", 1, 5),
            new Token(TipoToken.EOF, "$", 1, 6)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("+", TipoNo.OPERADOR,
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("y", TipoNo.IDENTIFICADOR, 1, 5),
            1, 3
        );
        
        ResultadoAnalise resultado = new Semantico(arvore, tokens).analisar();
        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getErros().get(0).getMensagem().contains("nao declarada"));
    }

    @Test
    void deveDetectarTiposIncompativeis() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "x", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 3),
            new Token(TipoToken.STRING, "\"texto\"", 1, 5),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 13),
            new Token(TipoToken.NUMBER, "5", 1, 15),
            new Token(TipoToken.EOF, "$", 1, 16)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("+", TipoNo.OPERADOR,
                new ArvoreSintatica("\"texto\"", TipoNo.IDENTIFICADOR, 1, 5),
                new ArvoreSintatica("5", TipoNo.NUMERO, 1, 15),
                1, 13
            ),
            1, 3
        );
        
        ResultadoAnalise resultado = new Semantico(arvore, tokens).analisar();
        assertFalse(resultado.isSucesso());
    }

    @Test
    void deveAceitarConversaoImplicitaIntParaFloat() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "x", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 3),
            new Token(TipoToken.NUMBER, "5", 1, 5),
            new Token(TipoToken.OP_ARITHMETIC, "+", 1, 7),
            new Token(TipoToken.NUMBER, "3.14", 1, 9),
            new Token(TipoToken.EOF, "$", 1, 13)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("+", TipoNo.OPERADOR,
                new ArvoreSintatica("5", TipoNo.NUMERO, 1, 5),
                new ArvoreSintatica("3.14", TipoNo.NUMERO, 1, 9),
                1, 7
            ),
            1, 3
        );
        
        assertTrue(new Semantico(arvore, tokens).analisar().isSucesso());
    }

    @Test
    void deveDetectarFuncaoNaoDefinida() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "resultado", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 10),
            new Token(TipoToken.IDENTIFIER, "funcaoNaoExistente", 1, 12),
            new Token(TipoToken.LPAREN, "(", 1, 30),
            new Token(TipoToken.NUMBER, "42", 1, 31),
            new Token(TipoToken.RPAREN, ")", 1, 33),
            new Token(TipoToken.EOF, "$", 1, 34)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("resultado", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("funcaoNaoExistente", TipoNo.CHAMADA_FUNCAO,
                new ArvoreSintatica("42", TipoNo.NUMERO, 1, 31),
                null,
                1, 12
            ),
            1, 10
        );
        
        ResultadoAnalise resultado = new Semantico(arvore, tokens).analisar();
        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getErros().get(0).getMensagem().contains("nao definida"));
    }

    @Test
    void deveDetectarVariavelNaoUtilizada() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "x", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 3),
            new Token(TipoToken.NUMBER, "5", 1, 5),
            new Token(TipoToken.EOF, "$", 1, 6)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("5", TipoNo.NUMERO, 1, 5),
            1, 3
        );
        
        ResultadoAnalise resultado = new Semantico(arvore, tokens).analisar();
        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getErros().get(0).getMensagem().contains("nao utilizada"));
    }

    @Test
    void deveAceitarChamadaDeFuncaoPredefinida() {
        List<Token> tokens = List.of(
            new Token(TipoToken.IDENTIFIER, "resultado", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 10),
            new Token(TipoToken.IDENTIFIER, "sqrt", 1, 12),
            new Token(TipoToken.LPAREN, "(", 1, 16),
            new Token(TipoToken.NUMBER, "16", 1, 17),
            new Token(TipoToken.RPAREN, ")", 1, 19),
            new Token(TipoToken.EOF, "$", 1, 20)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("resultado", TipoNo.IDENTIFICADOR, 1, 1),
            new ArvoreSintatica("sqrt", TipoNo.CHAMADA_FUNCAO,
                new ArvoreSintatica("16", TipoNo.NUMERO, 1, 17),
                null,
                1, 12
            ),
            1, 10
        );
        
        assertTrue(new Semantico(arvore, tokens).analisar().isSucesso());
    }

    @Test
    void deveDetectarAtribuicaoInvalida() {
        List<Token> tokens = List.of(
            new Token(TipoToken.NUMBER, "42", 1, 1),
            new Token(TipoToken.OP_ASSIGNMENT, "=", 1, 4),
            new Token(TipoToken.IDENTIFIER, "x", 1, 6),
            new Token(TipoToken.EOF, "$", 1, 7)
        );
        
        ArvoreSintatica arvore = new ArvoreSintatica("=", TipoNo.ATRIBUICAO,
            new ArvoreSintatica("42", TipoNo.NUMERO, 1, 1),
            new ArvoreSintatica("x", TipoNo.IDENTIFICADOR, 1, 6),
            1, 4
        );
        
        ResultadoAnalise resultado = new Semantico(arvore, tokens).analisar();
        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getErros().get(0).getMensagem().contains("deve ser um identificador"));
    }
}