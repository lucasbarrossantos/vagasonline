package com.vagas.api.model.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EnderecoInput {

    @NotBlank
    private String logradouro;
    @NotBlank
    private String cep;
    @NotBlank
    private String cidade;
    @NotBlank
    private String bairro;
    @NotBlank
    private String numero;
    private String complemento;
    @NotBlank
    private String uf;

}
