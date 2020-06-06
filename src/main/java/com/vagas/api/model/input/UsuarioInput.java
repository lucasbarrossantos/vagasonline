package com.vagas.api.model.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UsuarioInput {

    @NotBlank
    private String login;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    @NotBlank
    private String confirmarSenha;
    private Boolean isAtivo;

}
