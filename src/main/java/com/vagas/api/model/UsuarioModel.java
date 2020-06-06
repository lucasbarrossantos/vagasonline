package com.vagas.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioModel {

    private String login;
    private String email;
    private String senha;
    private String confirmarSenha;
    private Boolean isAtivo;

}
