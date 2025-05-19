# Analisador para Linguagem de Programação

Este projeto implementa um analisador léxico, sintático e semantico para processamento de código fonte, com suporte a múltiplos estados, tratamento de erros e rastreamento preciso de posição.

## Características Principais

### Estados do Lexer
- `DEFAULT`: Estado principal para identificar a maioria dos tokens
- `STRING`: Estado para processar strings com tratamento de caracteres de escape
- `LINE_COMMENT`: Estado para ignorar comentários de linha
- `BLOCK_COMMENT`: Estado para ignorar comentários de bloco

### Tratamento de Erros
- Caracteres inválidos são marcados como `ERROR`
- Detecção de strings não fechadas
- Identificação de números mal formados

### Rastreamento de Posição
- Atualização de linha e coluna a cada avanço no código fonte
- Registro da posição inicial (linha e coluna) de cada token

### Integração com Parser
- Cada token contém:
  - Tipo
  - Lexema
  - Posição (linha, coluna)

## Conjuntos First e Follow

### Conjuntos First
- `First(E) = { id, ( }`
- `First(T) = { id, ( }`
- `First(F) = { id, ( }`

### Conjuntos Follow
- `Follow(E) = { ), $, +, -, *, / }`
- `Follow(T) = { +, -, ), $, *, / }`
- `Follow(F) = { +, -, *, /, ), $ }`

