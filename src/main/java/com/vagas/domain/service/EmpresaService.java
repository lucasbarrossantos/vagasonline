package com.vagas.domain.service;

import com.vagas.api.model.input.EmpresaInput;
import com.vagas.domain.exception.EmpresaNaoEncontradaException;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.model.Empresa;
import com.vagas.domain.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaService {

    private static final String MSG_EMPRESA_EM_USO = "Empresa de código %d não pode ser removida, pois está em uso";

    private final EmpresaRepository empresaRepository;

    @Transactional
    public Empresa salvar(final Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Page<Empresa> listarTodos(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    public Empresa buscarOuFalhar(Long id) {
        return empresaRepository.findById(id).orElseThrow(() -> {
            log.error(String
                    .format("Erro em EmpresaController.buscarOuFalhar(?) ao tentar buscar o empresa de código %d", id));
            return new EmpresaNaoEncontradaException(id);
        });
    }

    @Transactional
    public Empresa update(EmpresaInput empresaInput, Empresa empresa) {
        return salvar(empresa);
    }

    @Transactional
    public void excluir(Long id) {
        try {

            empresaRepository.deleteById(id);
            empresaRepository.flush();

        } catch (EmptyResultDataAccessException e) {
            log.error(String.format(
                    "Erro ao tentar excluir a empresa! EmpresaService.excluir(?), nenhum empresa encontrado com o id %s",
                    id));
            throw new EmpresaNaoEncontradaException(id);

        } catch (DataIntegrityViolationException e) {
            log.error("Erro ao tentar excluir a empresa! EmpresaService.excluir(?), "
                    + "violação de chave primária, id não encontrado no Banco de Dados");
            throw new EntidadeEmUsoException(String.format(MSG_EMPRESA_EM_USO, id));
        }
    }
}
