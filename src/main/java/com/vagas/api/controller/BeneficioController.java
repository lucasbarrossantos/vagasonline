package com.vagas.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.vagas.api.model.BeneficioModel;
import com.vagas.api.model.input.BeneficioInput;
import com.vagas.api.modelmapper.BeneficioModelMapper;
import com.vagas.domain.service.BeneficioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/beneficio")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final BeneficioModelMapper modelMapper;

    @GetMapping
    public DeferredResult<HttpEntity<Page<BeneficioModel>>> findAll(Pageable pageable) {
        DeferredResult<HttpEntity<Page<BeneficioModel>>> deferredResult = new DeferredResult<>();
        beneficioService.listarTodos(pageable)
                .subscribe(response -> deferredResult
                                .setResult(new ResponseEntity<>(
                                        new PageImpl<>(modelMapper.toCollectionModel(response.getContent()),
                                                pageable, response.getTotalElements())
                                        , HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeferredResult<HttpEntity<BeneficioModel>> salvar(@RequestBody @Valid BeneficioInput beneficioInput) {
        DeferredResult<HttpEntity<BeneficioModel>> deferredResult = new DeferredResult<>();
        this.beneficioService.salvar(modelMapper.toDomainObject(beneficioInput))
                .doOnError(error -> log.error("Erro em BeneficioController.salvar() ao tentar salvar o benefício"))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<HttpEntity<BeneficioModel>> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<BeneficioModel>> deferredResult = new DeferredResult<>();
        beneficioService.buscarOuFalhar(id)
                .subscribe(response -> deferredResult
                                .setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PutMapping("/{id}")
    public DeferredResult<HttpEntity<BeneficioModel>> atualizar(@PathVariable("id") Long id,
                                                                @RequestBody @Valid BeneficioInput beneficioInput) {
        DeferredResult<HttpEntity<BeneficioModel>> deferredResult = new DeferredResult<>();
        beneficioService.buscarOuFalhar(id)
                .subscribe(response -> {
                            modelMapper.copyToDomainObject(beneficioInput, response);
                            beneficioService.update(response)
                                    .subscribe(beneficioSalvo ->
                                                    deferredResult.setResult(
                                                            new ResponseEntity<>(modelMapper.toModel(beneficioSalvo),
                                                                    HttpStatus.OK)),
                                            deferredResult::setErrorResult);
                        }
                        , deferredResult::setErrorResult);
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<HttpEntity<String>> remover(@PathVariable Long id) {
        DeferredResult<HttpEntity<String>> deferredResult = new DeferredResult<>();
        beneficioService.excluir(id)
                .subscribe(data -> deferredResult.setResult(new ResponseEntity<>("", HttpStatus.OK)), deferredResult::setErrorResult);
        return deferredResult;
    }

}
