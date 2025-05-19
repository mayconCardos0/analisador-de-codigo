package br.edu.fesa.lexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int column = 1;
    private char currentChar;
    
    private enum State {
        DEFAULT,
        STRING,
        LINE_COMMENT,
        BLOCK_COMMENT
    }
    
    private State state = State.DEFAULT;
    
    // Palavras reservadas do C#
    private static final Map<String, TipoToken> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("if", TipoToken.KEYWORD);
        KEYWORDS.put("else", TipoToken.KEYWORD);
        KEYWORDS.put("while", TipoToken.KEYWORD);
        KEYWORDS.put("for", TipoToken.KEYWORD);
        KEYWORDS.put("return", TipoToken.KEYWORD);
        KEYWORDS.put("class", TipoToken.KEYWORD);
        KEYWORDS.put("public", TipoToken.KEYWORD);
        KEYWORDS.put("private", TipoToken.KEYWORD);
        KEYWORDS.put("true", TipoToken.BOOLEAN);
        KEYWORDS.put("false", TipoToken.BOOLEAN);
        KEYWORDS.put("var", TipoToken.KEYWORD);
    }

    // Padrões regex para validação
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?([eE][+-]?\\d+)?$");
    
    public Lexer(String input) {
        this.input = input;
        this.currentChar = input.length() > 0 ? input.charAt(pos) : '\0';
    }

     public List<Token> generateTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        
        do {
            token = nextToken();
            tokens.add(token);
            
            if (token.type == TipoToken.ERROR && 
                (token.lexeme.startsWith("String nao fechada") || 
                 token.lexeme.startsWith("Sequencia de escape invalida"))) {
                break;
            }
            
        } while (token.type != TipoToken.EOF);
        
        return tokens;
    }
    
    public Token nextToken() {
        while (currentChar != '\0') {
            switch (state) {
                case DEFAULT:
                    return processDefaultState();
                case STRING:
                    return processString();
                case LINE_COMMENT:
                    skipLineComment();
                    continue;
                case BLOCK_COMMENT:
                    skipBlockComment();
                    continue;
            }
        }
        return new Token(TipoToken.EOF, "$", line, column);
    }

    private Token processDefaultState() {
        skipWhitespace();
        
        if (currentChar == '\0') {
            return new Token(TipoToken.EOF, "$", line, column);
        }

        int startLine = line;
        int startColumn = column;
        
        // Identificadores e palavras-chave
        if (Character.isLetter(currentChar) || currentChar == '_') {
            return readIdentifierOrKeyword(startLine, startColumn);
        }
        
        // Números
        if (Character.isDigit(currentChar)) {
            return readNumber(startLine, startColumn);
        }
        
        // Strings
        if (currentChar == '"') {
            state = State.STRING;
            return readString(startLine, startColumn);
        }
        
        // Comentários
        if (currentChar == '/') {
            char nextChar = peek();
            if (nextChar == '/') {
                state = State.LINE_COMMENT;
                advance();
                advance();
                return nextToken();
            } else if (nextChar == '*') {
                state = State.BLOCK_COMMENT;
                advance();
                advance();
                return nextToken();
            }
        }
        
        // Operadores e delimitadores
        return readOperatorOrDelimiter(startLine, startColumn);
    }

    private Token processString() {
        // Já consumimos a aspa inicial no estado DEFAULT
        int startLine = line;
        int startColumn = column - 1; // Ajuste para pegar a posição da aspa
        
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        
        while (true) {
            if (currentChar == '\0') {
                state = State.DEFAULT;
                return new Token(TipoToken.ERROR, "String não fechada", startLine, startColumn);
            }
            
            if (escape) {
                switch (currentChar) {
                    case 'n': builder.append('\n'); break;
                    case 't': builder.append('\t'); break;
                    case '"': builder.append('"'); break;
                    case '\\': builder.append('\\'); break;
                    default:
                        state = State.DEFAULT;
                        return new Token(TipoToken.ERROR, "Sequência de escape inválida: \\" + currentChar, 
                                       startLine, startColumn);
                }
                escape = false;
                advance();
                continue;
            }
            
            if (currentChar == '\\') {
                escape = true;
                advance();
                continue;
            }
            
            if (currentChar == '"') {
                advance();
                state = State.DEFAULT;
                return new Token(TipoToken.STRING, builder.toString(), startLine, startColumn);
            }
            
            builder.append(currentChar);
            advance();
        }
    }

    private Token readIdentifierOrKeyword(int startLine, int startColumn) {
        StringBuilder builder = new StringBuilder();
        
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            builder.append(currentChar);
            advance();
        }
        
        String id = builder.toString();
        
        // Valida o identificador com o padrão regex
        if (!IDENTIFIER_PATTERN.matcher(id).matches()) {
            return new Token(TipoToken.ERROR, id, startLine, startColumn);
        }
        
        TipoToken type = KEYWORDS.getOrDefault(id, TipoToken.IDENTIFIER);
        return new Token(type, id, startLine, startColumn);
    }

    private Token readNumber(int startLine, int startColumn) {
        StringBuilder builder = new StringBuilder();
        
        while (Character.isDigit(currentChar) || 
               currentChar == '.' || 
               currentChar == 'e' || currentChar == 'E' ||
               currentChar == '+' || currentChar == '-') {
            
            builder.append(currentChar);
            advance();
        }
        
        String numStr = builder.toString();
        if (!NUMBER_PATTERN.matcher(numStr).matches()) {
            return new Token(TipoToken.ERROR, numStr, startLine, startColumn);
        }
        
        return new Token(TipoToken.NUMBER, numStr, startLine, startColumn);
    }

    private Token readString(int startLine, int startColumn) {
        // Este método agora apenas inicia o processamento da string
        // O processamento real é feito em processString()
        return processString();
    }

    private void skipLineComment() {
        while (currentChar != '\n' && currentChar != '\0') {
            advance();
        }
        if (currentChar == '\n') {
            advance();
        }
        state = State.DEFAULT;
    }

    private void skipBlockComment() {
        while (currentChar != '\0') {
            if (currentChar == '*' && peek() == '/') {
                advance();
                advance();
                state = State.DEFAULT;
                return;
            }
            advance();
        }
        state = State.DEFAULT;
    }

    private Token readOperatorOrDelimiter(int startLine, int startColumn) {
        char c = currentChar;
        advance();
        
        switch (c) {
            // Operadores aritméticos
            case '+': case '-': case '*': case '/': case '%':
                return new Token(TipoToken.OP_ARITHMETIC, String.valueOf(c), startLine, startColumn);
                
            // Operadores relacionais
            case '>':
                if (currentChar == '=') {
                    advance();
                    return new Token(TipoToken.OP_RELATIONAL, ">=", startLine, startColumn);
                }
                return new Token(TipoToken.OP_RELATIONAL, ">", startLine, startColumn);
            case '<':
                if (currentChar == '=') {
                    advance();
                    return new Token(TipoToken.OP_RELATIONAL, "<=", startLine, startColumn);
                }
                return new Token(TipoToken.OP_RELATIONAL, "<", startLine, startColumn);
                
            // Operadores lógicos
            case '!':
                if (currentChar == '=') {
                    advance();
                    return new Token(TipoToken.OP_RELATIONAL, "!=", startLine, startColumn);
                }
                return new Token(TipoToken.OP_LOGICAL, "!", startLine, startColumn);
            case '&':
                if (currentChar == '&') {
                    advance();
                    return new Token(TipoToken.OP_LOGICAL, "&&", startLine, startColumn);
                }
                break;
            case '|':
                if (currentChar == '|') {
                    advance();
                    return new Token(TipoToken.OP_LOGICAL, "||", startLine, startColumn);
                }
                break;
                
            // Atribuição
            case '=':
                if (currentChar == '=') {
                    advance();
                    return new Token(TipoToken.OP_RELATIONAL, "==", startLine, startColumn);
                }
                return new Token(TipoToken.OP_ASSIGNMENT, "=", startLine, startColumn);
                
            // Delimitadores
            case '(': return new Token(TipoToken.LPAREN, "(", startLine, startColumn);
            case ')': return new Token(TipoToken.RPAREN, ")", startLine, startColumn);
            case '{': return new Token(TipoToken.LBRACE, "{", startLine, startColumn);
            case '}': return new Token(TipoToken.RBRACE, "}", startLine, startColumn);
            case '[': return new Token(TipoToken.LBRACKET, "[", startLine, startColumn);
            case ']': return new Token(TipoToken.RBRACKET, "]", startLine, startColumn);
            case ';': return new Token(TipoToken.SEMICOLON, ";", startLine, startColumn);
            case ',': return new Token(TipoToken.COMMA, ",", startLine, startColumn);
            case '.': return new Token(TipoToken.DOT, ".", startLine, startColumn);
        }
        
        return new Token(TipoToken.ERROR, String.valueOf(c), startLine, startColumn);
    }

    private void processLineComment() {
        while (currentChar != '\n' && currentChar != '\0') {
            advance();
        }
        if (currentChar == '\n') {
            advance();
        }
        state = State.DEFAULT;
    }

    private void processBlockComment() {
        while (currentChar != '\0') {
            if (currentChar == '*' && peek() == '/') {
                advance();
                advance();
                state = State.DEFAULT;
                return;
            }
            advance();
        }
        // Se chegou aqui, comentário não fechado
        state = State.DEFAULT;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private void advance() {
        if (currentChar == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        
        pos++;
        if (pos < input.length()) {
            currentChar = input.charAt(pos);
        } else {
            currentChar = '\0';
        }
    }
    
    private char peek() {
        if (pos + 1 < input.length()) {
            return input.charAt(pos + 1);
        }
        return '\0';
    }
}