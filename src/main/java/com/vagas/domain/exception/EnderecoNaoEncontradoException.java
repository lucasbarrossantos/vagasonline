package com.vagas.domain.exception;

public class EnderecoNaoEncontradoException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

    public EnderecoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public EnderecoNaoEncontradoException(Long id) {
        this(String.format("Erro ao tentar buscar o endereço com código %d. " +
                "Não existe um registro com esse id!", id));
    }

}
