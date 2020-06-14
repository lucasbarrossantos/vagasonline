package com.vagas.api.model.input;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OportunidadeInput {
	
	private String titulo;
    private String descricao;
    private String localTrabalho;
    private LocalDate dataCriacao = LocalDate.now();
    private LocalDate dataEncerramento;
    private EmpresaId empresa;

}
