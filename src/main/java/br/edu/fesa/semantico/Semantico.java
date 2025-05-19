package br.edu.fesa.semantico;

import br.edu.fesa.lexico.Token;
import br.edu.fesa.semantico.Semantico.TipoDado;
import br.edu.fesa.sintatico.ArvoreSintatica;
import br.edu.fesa.sintatico.TipoNo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Semantico {

    public enum TipoDado {
        INTEIRO,
        DECIMAL,
        BOOLEANO,
        STRING,
        INDEFINIDO
    }

    private enum TipoOperador {
        ARITMETICO(TipoDado.INTEIRO, TipoDado.DECIMAL),
        RELACIONAL(TipoDado.INTEIRO, TipoDado.DECIMAL),
        LOGICO(TipoDado.BOOLEANO),
        COMPARACAO(null);

        private final TipoDado[] tiposAceitos;

        TipoOperador(TipoDado... tipos) {
            this.tiposAceitos = tipos;
        }

        public boolean aceitaTipo(TipoDado tipo) {
            if (tiposAceitos == null) {
                return true;
            }
            for (TipoDado t : tiposAceitos) {
                if (t == tipo) {
                    return true;
                }
            }
            return false;
        }
    }

    private final TabelaDeSimbolos tabelaSimbolos = new TabelaDeSimbolos();
    private final List<MensagemErro> erros = new ArrayList<>();
    private final ArvoreSintatica arvore;
    private final List<Token> tokens;
    private final Map<String, Funcao> funcoes = new HashMap<>();
    private final Map<String, TipoOperador> operadores = new HashMap<>();

    public Semantico(ArvoreSintatica arvore, List<Token> tokens) {
        this.arvore = arvore;
        this.tokens = tokens;
        inicializarOperadores();
        inicializarFuncoesPredefinidas();
    }

    private void inicializarOperadores() {
        operadores.put("+", TipoOperador.ARITMETICO);
        operadores.put("-", TipoOperador.ARITMETICO);
        operadores.put("*", TipoOperador.ARITMETICO);
        operadores.put("/", TipoOperador.ARITMETICO);
        operadores.put("<", TipoOperador.RELACIONAL);
        operadores.put(">", TipoOperador.RELACIONAL);
        operadores.put("<=", TipoOperador.RELACIONAL);
        operadores.put(">=", TipoOperador.RELACIONAL);
        operadores.put("&&", TipoOperador.LOGICO);
        operadores.put("||", TipoOperador.LOGICO);
        operadores.put("!", TipoOperador.LOGICO);
        operadores.put("==", TipoOperador.COMPARACAO);
        operadores.put("!=", TipoOperador.COMPARACAO);
    }

    private void inicializarFuncoesPredefinidas() {
        // Funções matemáticas
        funcoes.put("sqrt", new Funcao(TipoDado.DECIMAL, TipoDado.DECIMAL));
        funcoes.put("pow", new Funcao(TipoDado.DECIMAL, TipoDado.DECIMAL, TipoDado.DECIMAL));

        // Funções básicas
        funcoes.put("print", new Funcao(TipoDado.INDEFINIDO, TipoDado.INDEFINIDO));
    }

    public ResultadoAnalise analisar() {
        try {
            construirTabelaSimbolos(arvore);
            analisarArvore(arvore);
            verificarVariaveisNaoUtilizadas();

            return new ResultadoAnalise(erros.isEmpty(), cleanErrors(erros), tabelaSimbolos);
        } catch (Exception e) {
            erros.add(new MensagemErro(-1, -1, "ERRO_INTERNO",
                    cleanMessage("Erro durante analise semantica: " + e.getMessage())));
            return new ResultadoAnalise(false, cleanErrors(erros), tabelaSimbolos);
        }
    }

    private List<MensagemErro> cleanErrors(List<MensagemErro> erros) {
        List<MensagemErro> cleaned = new ArrayList<>();
        for (MensagemErro erro : erros) {
            cleaned.add(new MensagemErro(
                    erro.getLinha(),
                    erro.getColuna(),
                    erro.getCodigo(),
                    cleanMessage(erro.getMensagem())
            ));
        }
        return cleaned;
    }

    private String cleanMessage(String message) {
        return message.replace("�", "")
                .replace("´", "")
                .replace("`", "")
                .replace("^", "")
                .replace("ã", "a")
                .replace("á", "a")
                .replace("à", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("ú", "u")
                .replace("ç", "c");
    }

    private void construirTabelaSimbolos(ArvoreSintatica no) {
        if (no == null) {
            return;
        }

        if (no.getTipo() == TipoNo.ATRIBUICAO && no.getEsquerda() != null
                && no.getEsquerda().getTipo() == TipoNo.IDENTIFICADOR) {

            String nomeVar = no.getEsquerda().getValor();
            TipoDado tipoVar = determinarTipoExpressao(no.getDireita());

            try {
                tabelaSimbolos.declarar(nomeVar, tipoVar,
                        no.getEsquerda().getLinha(), no.getEsquerda().getColuna());
            } catch (RuntimeException e) {
                erros.add(new MensagemErro(
                        no.getEsquerda().getLinha(), no.getEsquerda().getColuna(),
                        "VAR_DUPLICADA", cleanMessage(e.getMessage())));
            }
        }

        construirTabelaSimbolos(no.getEsquerda());
        construirTabelaSimbolos(no.getDireita());
        no.getFilhos().forEach(this::construirTabelaSimbolos);
    }

    private TipoDado analisarArvore(ArvoreSintatica no) {
        if (no == null) {
            return TipoDado.INDEFINIDO;
        }

        TipoDado tipo = switch (no.getTipo()) {
            case IDENTIFICADOR ->
                verificarIdentificador(no);
            case NUMERO ->
                verificarNumero(no);
            case OPERADOR ->
                verificarOperacao(no);
            case CHAMADA_FUNCAO ->
                verificarChamadaFuncao(no);
            case ATRIBUICAO ->
                verificarAtribuicao(no);
            default ->
                TipoDado.INDEFINIDO;
        };

        no.setTipoInferido(tipo);
        return tipo;
    }

    private TipoDado verificarIdentificador(ArvoreSintatica no) {
        Simbolo simbolo = tabelaSimbolos.buscar(no.getValor());
        if (simbolo == null) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "VAR_NAO_DECLARADA",
                    cleanMessage("Variavel '" + no.getValor() + "' nao declarada")));
            return TipoDado.INDEFINIDO;
        }
        simbolo.marcarComoUtilizada();
        return simbolo.getTipo();
    }

    private TipoDado verificarNumero(ArvoreSintatica no) {
        try {
            return no.getValor().contains(".") ? TipoDado.DECIMAL : TipoDado.INTEIRO;
        } catch (Exception e) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "NUMERO_INVALIDO",
                    cleanMessage("Numero mal formado: '" + no.getValor() + "'")));
            return TipoDado.INDEFINIDO;
        }
    }

    private TipoDado verificarOperacao(ArvoreSintatica no) {
        TipoOperador tipoOp = operadores.get(no.getValor());
        if (tipoOp == null) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "OPERADOR_DESCONHECIDO",
                    cleanMessage("Operador desconhecido: '" + no.getValor() + "'")));
            return TipoDado.INDEFINIDO;
        }

        TipoDado tipoEsq = analisarArvore(no.getEsquerda());
        TipoDado tipoDir = no.getDireita() != null
                ? analisarArvore(no.getDireita()) : TipoDado.INDEFINIDO;

        if (!tipoOp.aceitaTipo(tipoEsq)) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "TIPO_OPERANDO",
                    cleanMessage("Tipo invalido para operando esquerdo do operador '"
                            + no.getValor() + "': " + tipoEsq)));
        }

        if (no.getDireita() != null && !tipoOp.aceitaTipo(tipoDir)) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "TIPO_OPERANDO",
                    cleanMessage("Tipo invalido para operando direito do operador '"
                            + no.getValor() + "': " + tipoDir)));
        }

        if (tipoOp == TipoOperador.COMPARACAO && tipoEsq != tipoDir) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "TIPOS_INCOMPATIVEIS",
                    cleanMessage("Tipos incompatíveis para comparacao: " + tipoEsq + " e " + tipoDir)));
        }

        return determinarTipoRetornoOperador(tipoOp, tipoEsq);
    }

    private TipoDado determinarTipoRetornoOperador(TipoOperador tipoOp, TipoDado tipoEsq) {
        return switch (tipoOp) {
            case RELACIONAL, COMPARACAO, LOGICO ->
                TipoDado.BOOLEANO;
            default ->
                tipoEsq;
        };
    }

    private TipoDado verificarChamadaFuncao(ArvoreSintatica no) {
        Funcao funcao = funcoes.get(no.getValor());
        if (funcao == null) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "FUNCAO_NAO_DEFINIDA",
                    "Função '" + no.getValor() + "' não definida"));
            return TipoDado.INDEFINIDO;
        }

        List<TipoDado> tiposArg = coletarTiposArgumentos(no.getEsquerda());
        if (tiposArg.size() != funcao.getParametros().size()) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "ARGUMENTOS_INCORRETOS",
                    "Número incorreto de argumentos para '" + no.getValor()
                    + "'. Esperado: " + funcao.getParametros().size()
                    + ", encontrado: " + tiposArg.size()));
        } else {
            verificarTiposArgumentos(no, funcao, tiposArg);
        }

        return funcao.getTipoRetorno();
    }

    private List<TipoDado> coletarTiposArgumentos(ArvoreSintatica args) {
        List<TipoDado> tipos = new ArrayList<>();
        while (args != null) {
            tipos.add(analisarArvore(args));
            args = args.getDireita();
        }
        return tipos;
    }

    private void verificarTiposArgumentos(ArvoreSintatica no, Funcao funcao, List<TipoDado> tiposArg) {
        for (int i = 0; i < tiposArg.size(); i++) {
            if (!tiposCompativeis(funcao.getParametros().get(i), tiposArg.get(i))) {
                erros.add(new MensagemErro(
                        no.getLinha(), no.getColuna(),
                        "TIPO_ARGUMENTO",
                        "Tipo incorreto para argumento " + (i + 1) + " de '"
                        + no.getValor() + "'. Esperado: " + funcao.getParametros().get(i)
                        + ", encontrado: " + tiposArg.get(i)));
            }
        }
    }

    private TipoDado verificarAtribuicao(ArvoreSintatica no) {
        if (no.getEsquerda().getTipo() != TipoNo.IDENTIFICADOR) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "ATRIBUICAO_INVALIDA",
                    "Lado esquerdo da atribuição deve ser um identificador"));
            return TipoDado.INDEFINIDO;
        }

        TipoDado tipoVar = analisarArvore(no.getEsquerda());
        TipoDado tipoExpr = analisarArvore(no.getDireita());

        if (!tiposCompativeis(tipoVar, tipoExpr)) {
            erros.add(new MensagemErro(
                    no.getLinha(), no.getColuna(),
                    "ATRIBUICAO_INCOMPATIVEL",
                    "Tipos incompatíveis na atribuição: " + tipoVar + " e " + tipoExpr));
        }

        // Marca variável como inicializada
        Simbolo s = tabelaSimbolos.buscar(no.getEsquerda().getValor());
        if (s != null) {
            s.marcarComoInicializada();
        }

        return tipoVar;
    }

    private TipoDado determinarTipoExpressao(ArvoreSintatica no) {
        if (no == null) {
            return TipoDado.INDEFINIDO;
        }

        return switch (no.getTipo()) {
            case IDENTIFICADOR -> {
                Simbolo s = tabelaSimbolos.buscar(no.getValor());
                yield s != null ? s.getTipo() : TipoDado.INDEFINIDO;
            }
            case NUMERO ->
                no.getValor().contains(".") ? TipoDado.DECIMAL : TipoDado.INTEIRO;
            case OPERADOR ->
                determinarTipoOperacao(no);
            case CHAMADA_FUNCAO -> {
                Funcao f = funcoes.get(no.getValor());
                yield f != null ? f.getTipoRetorno() : TipoDado.INDEFINIDO;
            }
            default ->
                TipoDado.INDEFINIDO;
        };
    }

    private TipoDado determinarTipoOperacao(ArvoreSintatica no) {
        TipoOperador tipoOp = operadores.get(no.getValor());
        if (tipoOp == null) {
            return TipoDado.INDEFINIDO;
        }

        TipoDado tipoEsq = determinarTipoExpressao(no.getEsquerda());
        if (tipoOp == TipoOperador.LOGICO || tipoOp == TipoOperador.RELACIONAL
                || tipoOp == TipoOperador.COMPARACAO) {
            return TipoDado.BOOLEANO;
        }
        return tipoEsq;
    }

    private boolean tiposCompativeis(TipoDado esperado, TipoDado encontrado) {
        if (esperado == TipoDado.INDEFINIDO || encontrado == TipoDado.INDEFINIDO) {
            return true;
        }
        if (esperado == TipoDado.DECIMAL && encontrado == TipoDado.INTEIRO) {
            return true;
        }
        return esperado == encontrado;
    }

    private void verificarVariaveisNaoUtilizadas() {
        tabelaSimbolos.getTodosSimbolos().stream()
                .filter(s -> !s.foiUtilizada())
                .forEach(s -> erros.add(new MensagemErro(
                s.getLinhaDeclaracao(), s.getColunaDeclaracao(),
                "VAR_NAO_UTILIZADA",
                "Variavel '" + s.getNome() + "' declarada mas nao utilizada")));
    }
}

class Funcao {

    private final TipoDado tipoRetorno;
    private final List<TipoDado> parametros;

    public Funcao(TipoDado tipoRetorno, TipoDado... parametros) {
        this.tipoRetorno = tipoRetorno;
        this.parametros = List.of(parametros);
    }

    public TipoDado getTipoRetorno() {
        return tipoRetorno;
    }

    public List<TipoDado> getParametros() {
        return parametros;
    }
}
