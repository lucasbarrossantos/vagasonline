package com.vagas.api.controller;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.modelmapper.BeneficioModelMapper;
import com.vagas.domain.service.BeneficioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/beneficio")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioController {
    private final Logger log = LoggerFactory.getLogger(BeneficioController.class);

    private final BeneficioService beneficioService;
    private final BeneficioModelMapper modelMapper;

    @GetMapping
    public Mono<Page<BeneficioModel>> findAll(@PageableDefault(size = 2) Pageable pageable) {
        return beneficioService.listarTodos(pageable);
    }

    @PostMapping
    @ResponseBody
    public DeferredResult<?> salvar(@RequestBody @Valid BeneficioInput beneficioInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        this.beneficioService.salvar(modelMapper.toDomainObject(beneficioInput))
                .doOnError(error -> log.error("Erro em BeneficioController.salvar() ao tentar salvar o benefício", error))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)));
        return deferredResult;
    }

}
