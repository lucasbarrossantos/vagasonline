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
import com.vagas.api.exceptionhandler.Error;

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
    public DeferredResult<?> salvar(@RequestBody @Valid BeneficioInput beneficioInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        this.beneficioService.salvar(modelMapper.toDomainObject(beneficioInput))
                .doOnError(error -> log.error("Erro em BeneficioController.salvar() ao tentar salvar o benefício", error))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)));
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<?> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        beneficioService.buscarOuFalhar(id)
                .doOnError(error -> Error.erroDeNegocio(deferredResult, error, HttpStatus.NOT_FOUND))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)));
        return deferredResult;
    }

    @PutMapping("/{id}")
    public DeferredResult<?> atualizar(@PathVariable("id") Long id,
                                       @RequestBody @Valid BeneficioInput beneficioInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        beneficioService.buscarOuFalhar(id)
                .subscribe(response -> beneficioService.update(beneficioInput, response)
                        .subscribe(beneficioSalvo ->
                                deferredResult.setResult(new ResponseEntity<>(beneficioSalvo, HttpStatus.OK))));
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<?> remover(@PathVariable Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        beneficioService.excluir(id)
                .doOnError(error -> {
                    Error.erroDeNegocio(deferredResult, error, HttpStatus.BAD_REQUEST);
                    log.error("Erro ao tentar excluído o benefício!", error);
                })
                .subscribe(data -> log.info("Benefício excluído com sucesso!"));
        return deferredResult;
    }

}
