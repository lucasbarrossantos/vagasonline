package com.vagas.domain.exception;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontradoException(Long id) {
        this(String.format("Erro ao tentar buscar o usuário com código %d. " +
                "Não existe um registro com esse id!", id));
    }

}
