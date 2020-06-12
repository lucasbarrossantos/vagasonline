package com.vagas.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Empresa {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    @Column(length = 14)
    private String cnpj;

    private String site;

    private String email;

    private String telefone;

    private String latitude;

    private String longitude;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Endereco endereco;

    @OneToMany(mappedBy = "empresa")
    private Set<Oportunidade> oportunidades = new HashSet<>();

}
