package com.vagas.domain.service;

import com.vagas.domain.exception.BeneficioNaoEncontradoException;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.repository.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioService {
	private static final String MSG_BENEFICIO_EM_USO = "Benefício de código %d não pode ser removido, pois está em uso";

	private final BeneficioRepository beneficioRepository;

	@Transactional
	public Beneficio salvar(final Beneficio beneficio) {
		return beneficioRepository.save(beneficio);
	}

	public Page<Beneficio> listarTodos(Pageable pageable) {
		return beneficioRepository.findAll(pageable);
	}

	public Optional<Beneficio> buscarOuFalhar(Long id) {
		return beneficioRepository.findById(id);
	}

	@Transactional
	public Beneficio update(Beneficio beneficio) {
		return salvar(beneficio);
	}

	@Transactional
	public void excluir(Long id) {

			try {
				beneficioRepository.deleteById(id);
				beneficioRepository.flush();
			} catch (EmptyResultDataAccessException e) {
				log.error(String.format(
						"Erro ao tentar excluir o benefício! BeneficioService.excluir(?), nenhum benefício encontrado com o id %s",
						id));
				throw new BeneficioNaoEncontradoException(id);

			} catch (DataIntegrityViolationException e) {
				log.error(
						"Erro ao tentar excluir o benefício! BeneficioService.excluir(?), violação de chave primária, id não encontrado no Banco de Dados");
				throw new EntidadeEmUsoException(String.format(MSG_BENEFICIO_EM_USO, id));
			}
	}
}
