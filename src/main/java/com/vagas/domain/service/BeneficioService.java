package com.vagas.domain.service;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.modelmapper.BeneficioModelMapper;
import com.vagas.domain.exception.BeneficioNaoEncontradoException;
import com.vagas.domain.exception.EntidadeEmUsoException;
import com.vagas.domain.exception.NegocioException;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.repository.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioService {
    private final Logger log = LoggerFactory.getLogger(BeneficioService.class);

    private static final String MSG_BENEFICIO_EM_USO
            = "Benefício de código %d não pode ser removido, pois está em uso";

    private final BeneficioRepository beneficioRepository;
    private final BeneficioModelMapper modelMapper;

    @Transactional
    public Mono<Beneficio> salvar(final Beneficio beneficio) {
        return Mono
                .fromSupplier(() -> beneficioRepository.save(beneficio))
                .doOnError(error -> log.error("Erro em BeneficioService.salvar() ao tentar salvar o benefício", error));
    }

    public Mono<Page<BeneficioModel>> listarTodos(Pageable pageable) {
        return Mono.fromSupplier(beneficioRepository.findAll(pageable))
                .map(beneficioStream -> modelMapper.toCollectionModel(beneficioStream.collect(Collectors.toList())))
                .map(beneficioModels -> new PageImpl<>(beneficioModels, pageable, beneficioModels.size()));
    }

    public Mono<Beneficio> buscarOuFalhar(Long id) {
        return Mono.just(beneficioRepository.findById(id))
                .flatMap(beneficio -> beneficio.map(Mono::just)
                        .orElseThrow(() -> new BeneficioNaoEncontradoException(id)));
    }

    public Mono<Beneficio> update(BeneficioInput beneficioInput, Beneficio beneficio) {
        modelMapper.copyToDomainObject(beneficioInput, beneficio);
        return salvar(beneficio);
    }

    @Transactional
    public Mono<Void> excluir(Long id) {
        return Mono.fromRunnable(() -> {
            try {
                beneficioRepository.deleteById(id);
                beneficioRepository.flush();
            } catch (EmptyResultDataAccessException e) {
                throw new BeneficioNaoEncontradoException(id);

            } catch (DataIntegrityViolationException e) {
                throw new EntidadeEmUsoException(
                        String.format(MSG_BENEFICIO_EM_USO, id));
            }
        });
    }
}
