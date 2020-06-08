package com.vagas.domain.exception;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontradoException(Long id) {
        this(String.format("Erro ao tentar buscar o usuário com código %d. " +
                "Não existe um registro com esse id!", id));
    }

}
