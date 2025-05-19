package br.edu.fesa.lexico;

public enum TipoToken {
    // Palavras reservadas
    KEYWORD,
    
    // Identificadores
    IDENTIFIER,
    
    // Literais
    NUMBER,
    STRING,
    BOOLEAN,
    
    // Operadores
    OP_ARITHMETIC,
    OP_RELATIONAL,
    OP_LOGICAL,
    OP_ASSIGNMENT,
    
    // Delimitadores
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LBRACKET, RBRACKET,
    SEMICOLON, COMMA, DOT,
    
    // Comentários
    LINE_COMMENT,
    BLOCK_COMMENT,
    
    // Erro
    ERROR,
    
    // Fim de arquivo
    EOF
}