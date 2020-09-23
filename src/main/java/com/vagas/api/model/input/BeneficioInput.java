package com.vagas.api.model.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BeneficioInput {

    @NotBlank
    private String titulo;
    private String descricao;

}
