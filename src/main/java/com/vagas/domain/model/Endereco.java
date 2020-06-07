package com.vagas.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Endereco {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 100, nullable = false)
    private String logradouro;

    @Column(length = 100, nullable = false)
    private String cep;

    @Column(length = 100, nullable = false)
    private String cidade;

    @Column(length = 100, nullable = false)
    private String bairro;

    @Column(length = 100)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 2, nullable = false)
    private String uf;

    @OneToOne(mappedBy = "endereco")
    private Empresa empresa;

}
