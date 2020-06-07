package com.vagas.api.model.input;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmpresaInput {

    @NotBlank
    private String nome;
    @CNPJ
    private String cnpj;
    private String site;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String telefone;
    private String latitude;
    private String longitude;
    @Valid
    private EnderecoInput endereco;

}
