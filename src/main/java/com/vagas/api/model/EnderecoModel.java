package com.vagas.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoModel {

    private Long id;
    private String logradouro;
    private String cep;
    private String cidade;
    private String bairro;
    private String numero;
    private String complemento;
    private String uf;

}
