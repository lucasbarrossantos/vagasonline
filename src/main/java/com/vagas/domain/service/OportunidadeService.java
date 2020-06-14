package com.vagas.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vagas.api.model.OportunidadeModel;
import com.vagas.api.model.input.OportunidadeInput;
import com.vagas.api.modelmapper.OportunidadeModelMapper;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.exception.OportunidadeNaoEncontradaException;
import com.vagas.domain.model.Empresa;
import com.vagas.domain.model.Oportunidade;
import com.vagas.domain.repository.OportunidadeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OportunidadeService {
	private static final String MSG_OPORTUNIDADE_EM_USO = "Oportunidade de código %d não pode ser removido, pois está em uso";

	private final OportunidadeRepository oportunidadeRepository;
	private final EmpresaService empresaService;
	private final OportunidadeModelMapper modelMapper;

	@Transactional
	public Mono<Tuple2<Tuple2<Empresa, Empresa>, OportunidadeModel>> salvar(final Oportunidade oportunidade,
			Long empresaId) {
		return empresaService.buscarOuFalhar(empresaId).subscribeOn(Schedulers.elastic())
				.zipWhen(empresa -> empresaService.buscarOuFalhar(oportunidade.getEmpresa().getId())
						.subscribeOn(Schedulers.elastic()))
				.zipWhen(empresa -> Mono.just(oportunidadeRepository.saveAndFlush(oportunidade))
						.map(modelMapper::toModel));
	}

	public Mono<Tuple2<Empresa, Page<OportunidadeModel>>> listarTodos(Long empresaId, Pageable pageable) {
		return empresaService.buscarOuFalhar(empresaId).subscribeOn(Schedulers.elastic())
				.zipWhen(empresa -> Mono.just(oportunidadeRepository.findAllByEmpresaId(empresaId, pageable))
						.subscribeOn(Schedulers.elastic()).map(empresaPage -> {
							List<OportunidadeModel> empresaResumo = modelMapper
									.toCollectionModel(empresaPage.getContent());
							return new PageImpl<>(empresaResumo, pageable, empresaPage.getTotalElements());
						}));
	}

	public Mono<Oportunidade> buscarOuFalhar(Long id) {
		return Mono.fromCallable(() -> oportunidadeRepository.findById(id).orElseThrow(() -> {
			log.error(String.format(
					"Erro em OportunidadeController.buscarOuFalhar(?) ao tentar buscar o oportunidade de código %d",
					id));
			return new OportunidadeNaoEncontradaException(id);
		})).subscribeOn(Schedulers.elastic());
	}

	@Transactional
	public Mono<Tuple2<Tuple2<Empresa, Empresa>, OportunidadeModel>> update(OportunidadeInput oportunidadeInput,
			Oportunidade oportunidade, Long empresaId) {
		modelMapper.copyToDomainObject(oportunidadeInput, oportunidade);
		return salvar(oportunidade, empresaId);
	}

	@Transactional
	public Mono<Tuple2<Empresa, Boolean>> excluir(Long empresaId, Long oportunidadeId) {
		return empresaService.buscarOuFalhar(empresaId).zipWhen(empresa -> Mono.fromCallable(() -> {
			try {
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

			return Boolean.TRUE;
		}));

	}
}
