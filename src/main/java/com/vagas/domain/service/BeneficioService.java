package com.vagas.domain.service;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.modelmapper.BeneficioModelMapper;
import com.vagas.domain.model.Beneficio;
import com.vagas.domain.repository.BeneficioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
}
