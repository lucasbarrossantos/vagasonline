package com.vagas.domain.exception;

public class EmpresaNaoEncontradaException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

    public EmpresaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public EmpresaNaoEncontradaException(Long id) {
        this(String.format("Erro ao tentar buscar a empresa com código %d. " +
                "Não existe um registro com esse id!", id));
    }

}
