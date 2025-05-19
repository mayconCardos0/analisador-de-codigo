package br.edu.fesa;

import br.edu.fesa.lexico.Lexer;
import br.edu.fesa.lexico.Token;
import br.edu.fesa.sintatico.Parser;
import br.edu.fesa.sintatico.RetornoParser;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class AnalisadorTests {
    @Test
    void deveProcessarExpressaoComplexaCorretamente() {
        String entrada = "func(a + id)";
        
        List<Token> tokens = new Lexer(entrada).generateTokens();
        RetornoParser resultado = new Parser(tokens).parse();
        
        assertTrue(resultado.isSuccess());
    }

    @Test
    void deveAceitarTodosCasosValidos() {
        String[] entradasValidas = {
            "x+y*z",
            "a*(b+c)",
            "var1*(var2+var3)",
            "(a+b)*(c/d)",
            "5",
            "xyz",
        };
        
        for (String entrada : entradasValidas) {
            List<Token> tokens = new Lexer(entrada).generateTokens();
            RetornoParser resultado = new Parser(tokens).parse();
            
            assertTrue(resultado.isSuccess(), 
                "Falha na entrada valida: " + entrada);
        }
    }

    @Test
    void naoDeveAceitarCasosInvalidos() {
        String[] entradasInvalidas = {
            "x+y*",       // Operador no final
            "*a+b",       // Operador no início
            "(a+b",       // Parêntese não fechado
            "a+)b",       // Parêntese fechado sem abrir
            "a++b"        // Dois operadores seguidos
        };
        
        for (String entrada : entradasInvalidas) {
            List<Token> tokens = new Lexer(entrada).generateTokens();
            RetornoParser resultado = new Parser(tokens).parse();
            
            assertFalse(resultado.isSuccess(), 
                "Entrada invalida deveria falhar: " + entrada);
        }
    }
}
