package com.vagas.domain.service;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.modelmapper.BeneficioModelMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioService {
	private static final String MSG_BENEFICIO_EM_USO = "Benefício de código %d não pode ser removido, pois está em uso";

	private final BeneficioRepository beneficioRepository;
	private final BeneficioModelMapper modelMapper;

	@Transactional
	public Mono<BeneficioModel> salvar(final Beneficio beneficio) {
		return Mono.fromSupplier(() -> beneficioRepository.save(beneficio))
				.publishOn(Schedulers.parallel())
				.map(modelMapper::toModel)
				.doOnError(error -> log.error("Erro em BeneficioService.salvar() ao tentar salvar o benefício", error));
	}

	public Mono<Page<BeneficioModel>> listarTodos(Pageable pageable) {
		return Mono.fromSupplier(() -> beneficioRepository.findAll(pageable))
				.subscribeOn(Schedulers.elastic())
				.map(beneficioPage -> {
					List<BeneficioModel> empresaResumo = modelMapper.toCollectionModel(beneficioPage.getContent());
					return new PageImpl<>(empresaResumo, pageable, beneficioPage.getTotalElements());
				});
	}

	public Mono<Beneficio> buscarOuFalhar(Long id) {
		return Mono.fromCallable(() -> beneficioRepository.findById(id).orElseThrow(() -> {
			log.error(String.format(
					"Erro em BeneficioController.buscarOuFalhar(?) ao tentar buscar o benefício de código %d", id));
			return new BeneficioNaoEncontradoException(id);
		})).subscribeOn(Schedulers.elastic());
	}

	public Mono<BeneficioModel> update(BeneficioInput beneficioInput, Beneficio beneficio) {
		modelMapper.copyToDomainObject(beneficioInput, beneficio);
		return salvar(beneficio);
	}

	@Transactional
	public Mono<Boolean> excluir(Long id) {
		return Mono.fromCallable(() -> {
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

			return Boolean.TRUE;
		}).subscribeOn(Schedulers.elastic());
	}
}
