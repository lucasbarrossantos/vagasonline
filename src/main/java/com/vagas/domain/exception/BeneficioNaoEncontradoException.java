package com.vagas.domain.exception;

public class BeneficioNaoEncontradoException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

	public BeneficioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public BeneficioNaoEncontradoException(Long id) {
        this(String.format("Erro ao tentar buscar o benefício com código %d. Não existe um registro com esse id!", id));
    }

}
