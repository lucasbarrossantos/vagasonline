package com.vagas.api.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OportunidadeModel {

	private Long id;
	private String titulo;
    private String descricao;
    private String localTrabalho;
    private LocalDate dataCriacao = LocalDate.now();
    private LocalDate dataEncerramento;
	
}
