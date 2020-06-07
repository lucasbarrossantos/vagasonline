package com.vagas.api.controller;

import com.vagas.api.model.EmpresaResumoModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.api.modelmapper.EmpresaModelMapper;
import com.vagas.domain.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/empresa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaController {
    private final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    private final EmpresaService empresaService;
    private final EmpresaModelMapper modelMapper;

    @GetMapping
    public Mono<Page<EmpresaResumoModel>> findAll(Pageable pageable) {
        return empresaService.listarTodos(pageable);
    }

    @PostMapping
    public DeferredResult<?> salvar(@RequestBody @Valid EmpresaInput empresaInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        this.empresaService.salvar(modelMapper.toDomainObject(empresaInput))
                .doOnError(error -> log.error("Erro em EmpresaController.salvar() ao tentar salvar a empresa"))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<?> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        empresaService.buscarOuFalhar(id)
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PutMapping("/{id}/endereco/{enderecoId}")
    public DeferredResult<?> atualizar(@PathVariable("id") Long id, @PathVariable("enderecoId") Long enderecoId,
                                       @RequestBody @Valid EmpresaInput empresaInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        empresaService.buscarOuFalhar(id)
                .subscribe(response -> empresaService.update(empresaInput, response, enderecoId)
                                .subscribe(empresaSalvo ->
                                                deferredResult.setResult(new ResponseEntity<>(empresaSalvo, HttpStatus.OK)),
                                        deferredResult::setErrorResult)
                        , deferredResult::setErrorResult);
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<?> remover(@PathVariable Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        empresaService.excluir(id)
                .subscribe(success -> deferredResult.setResult(new ResponseEntity<>("", HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

}