package com.vagas.api.model.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class BeneficioInput {

    @NotBlank
    private String titulo;
    private String descricao;

}
