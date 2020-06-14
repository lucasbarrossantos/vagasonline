package com.vagas.domain.exception;

public class OportunidadeNaoEncontradaException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

    public OportunidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public OportunidadeNaoEncontradaException(Long id) {
        this(String.format("Erro ao tentar buscar uma oportunidade com código %d. " +
                "Não existe um registro com esse id!", id));
    }

}
