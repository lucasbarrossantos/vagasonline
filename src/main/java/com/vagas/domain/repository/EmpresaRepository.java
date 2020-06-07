package com.vagas.domain.repository;

import com.vagas.domain.model.Empresa;
import com.vagas.domain.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("select e.endereco from Empresa as e where e.id = ?1")
    Optional<Endereco> enderecoByEmpresaId(Long empresaId);

}
