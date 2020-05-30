package com.vagas.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Oportunidade {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titulo;
    private String descricao;
    private String localTrabalho;
    private LocalDate dataCriacao = LocalDate.now();
    private LocalDate dataEncerramento;

    @ManyToMany
    @JoinTable(name = "oportunidade_beneficio",
            joinColumns = @JoinColumn(name = "oportunidade_id"),
            inverseJoinColumns = @JoinColumn(name = "beneficio_id"))
    private Set<Beneficio> beneficios = new HashSet<>();

    @ManyToOne
    private Empresa empresa;
}
