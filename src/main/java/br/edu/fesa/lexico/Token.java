package br.edu.fesa.lexico;

public class Token {
    public TipoToken type;
    public String lexeme;
    public int line;
    public int column;

    public Token(TipoToken type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public Token(TipoToken tipoToken, String lexeme) {
        this.type = tipoToken;
        this.lexeme = lexeme;
    }

    public TipoToken getType() {
        return type;
    }

    public void setType(TipoToken type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
    
    
    
        @Override
    public String toString() {
        return String.format("Token(%s, '%s', linha:%d, coluna:%d)",
            type,
            lexeme.replace("\n", "\\n").replace("\t", "\\t"),
            line,
            column);
    }
}