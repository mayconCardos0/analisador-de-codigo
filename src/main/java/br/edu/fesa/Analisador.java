package br.edu.fesa;

import br.edu.fesa.lexico.Lexer;
import br.edu.fesa.lexico.Token;
import br.edu.fesa.semantico.ResultadoAnalise;
import br.edu.fesa.semantico.Semantico;
import br.edu.fesa.sintatico.Parser;
import br.edu.fesa.sintatico.RetornoParser;
import java.util.List;

public class Analisador {

    public static void main(String[] args) {
        String input = "x * y + func(50 + 60)"; 
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.generateTokens();

        for (Token token : tokens) {
            System.out.println(token);
            // Saída: Token(TIPO, 'valor', linha:X, coluna:Y)
        }
        System.out.println("\n");
        
        Parser parser = new Parser(tokens);
        RetornoParser result = parser.parse();

        if (result.isSuccess()) {
            System.out.println(result.getArvoreSintatica());
            
            Semantico analisador = new Semantico(result.getArvoreSintatica(), tokens);
            ResultadoAnalise resultado = analisador.analisar();
            System.out.println(resultado.toString());
            
        }
        else{
            System.out.println(result.getErrorMessage());
        }
    }
}
