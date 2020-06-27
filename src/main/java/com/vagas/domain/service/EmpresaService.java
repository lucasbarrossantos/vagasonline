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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaService {

	private static final String MSG_EMPRESA_EM_USO = "Empresa de código %d não pode ser removida, pois está em uso";

	private final EmpresaRepository empresaRepository;

	@Transactional
	public Mono<Empresa> salvar(final Empresa empresa) {
		return Mono.fromSupplier(() -> empresaRepository.save(empresa))
				.publishOn(Schedulers.parallel())
				.doOnError(error -> log.error("Erro em BeneficioService.salvar() ao tentar salvar o benefício", error));
	}

	public Mono<Page<Empresa>> listarTodos(Pageable pageable) {
		return Mono.fromSupplier(() -> empresaRepository.findAll(pageable))
				.subscribeOn(Schedulers.elastic());
	}

	public Mono<Empresa> buscarOuFalhar(Long id) {
		return Mono.fromCallable(() -> empresaRepository.findById(id).orElseThrow(() -> {
			log.error(String
					.format("Erro em EmpresaController.buscarOuFalhar(?) ao tentar buscar o empresa de código %d", id));
			return new EmpresaNaoEncontradaException(id);
		})).subscribeOn(Schedulers.elastic());
	}

	@Transactional
	public Mono<Empresa> update(EmpresaInput empresaInput, Empresa empresa) {
		return salvar(empresa);
	}

	@Transactional
	public Mono<Tuple2<Empresa, Boolean>> excluir(Long id) {
		return buscarOuFalhar(id).zipWhen(empresa -> {
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

			return Mono.just(Boolean.TRUE);
		}).subscribeOn(Schedulers.elastic());
	}
}
