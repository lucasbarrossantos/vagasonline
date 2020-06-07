package com.vagas.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaResumoModel {

    private Long id;
    private String nome;
    private String cnpj;
    private String site;
    private String email;
    private String telefone;
    private String latitude;
    private String longitude;

}
