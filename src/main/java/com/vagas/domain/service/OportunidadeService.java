package com.vagas.domain.service;

import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.exception.OportunidadeNaoEncontradaException;
import com.vagas.domain.model.Oportunidade;
import com.vagas.domain.repository.OportunidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OportunidadeService {
    private static final String MSG_OPORTUNIDADE_EM_USO = "Oportunidade de código %d não pode ser removido, pois está em uso";

    private final OportunidadeRepository oportunidadeRepository;
    private final EmpresaService empresaService;

    @Transactional
    public Oportunidade salvar(final Oportunidade oportunidade, Long empresaId) {
        empresaService.buscarOuFalhar(empresaId);
        empresaService.buscarOuFalhar(oportunidade.getEmpresa().getId());
        return oportunidadeRepository.saveAndFlush(oportunidade);
    }

    public Page<Oportunidade> listarTodos(Long empresaId, Pageable pageable) {
        empresaService.buscarOuFalhar(empresaId);
        Page<Oportunidade> oportunidadePage = oportunidadeRepository.findAllByEmpresaId(empresaId, pageable);

        return new PageImpl<>(oportunidadePage.getContent(), pageable,
                oportunidadePage.getTotalElements());
    }

    public Oportunidade buscarOuFalhar(Long id) {
        return oportunidadeRepository.findById(id).orElseThrow(() -> {
            log.error(String.format(
                    "Erro em OportunidadeController.buscarOuFalhar(?) ao tentar buscar o oportunidade de código %d",
                    id));
            return new OportunidadeNaoEncontradaException(id);
        });
    }

    @Transactional
    public Oportunidade update(Oportunidade oportunidade, Long empresaId) {
        return salvar(oportunidade, empresaId);
    }

    @Transactional
    public void excluir(Long empresaId, Long oportunidadeId) {
        try {
            empresaService.buscarOuFalhar(empresaId);
            oportunidadeRepository.deleteById(oportunidadeId);
            oportunidadeRepository.flush();
        } catch (EmptyResultDataAccessException e) {
            log.error(String.format(
                    "Erro ao tentar excluir a oportunidade! OportunidadeService.excluir(?), nenhuma oportunidade encontrada com o id %s",
                    oportunidadeId));
            throw new OportunidadeNaoEncontradaException(oportunidadeId);

        } catch (DataIntegrityViolationException e) {
            log.error(
                    "Erro ao tentar excluir a oportunidade! OportunidadeService.excluir(?), violação de chave primária, id não encontrada no Banco de Dados");
            throw new EntidadeEmUsoException(String.format(MSG_OPORTUNIDADE_EM_USO, oportunidadeId));
        }
    }
}
