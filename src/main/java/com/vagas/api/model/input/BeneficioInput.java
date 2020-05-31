package com.vagas.api.model.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BeneficioInput {

    @NotBlank
    private String titulo;
    private String descricao;

}
