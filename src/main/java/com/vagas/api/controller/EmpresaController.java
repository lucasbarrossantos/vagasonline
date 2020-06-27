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

import com.vagas.api.model.EmpresaModel;
import com.vagas.api.model.EmpresaResumoModel;
import com.vagas.api.model.input.EmpresaInput;
import com.vagas.api.modelmapper.EmpresaModelMapper;
import com.vagas.domain.service.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/empresa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaModelMapper modelMapper;

    @GetMapping
    public DeferredResult<HttpEntity<Page<EmpresaResumoModel>>> findAll(Pageable pageable) {
        DeferredResult<HttpEntity<Page<EmpresaResumoModel>>> deferredResult = new DeferredResult<>();
        empresaService.listarTodos(pageable).subscribe(response -> deferredResult
                        .setResult(new ResponseEntity<>(
                                new PageImpl<>(modelMapper.toCollectionResumeModel(response.getContent()),
                                        pageable, response.getTotalElements())
                                , HttpStatus.OK)),
                deferredResult::setErrorResult);
        return deferredResult;
    }

    @PostMapping
    public DeferredResult<HttpEntity<EmpresaModel>> salvar(@RequestBody @Valid EmpresaInput empresaInput) {
        DeferredResult<HttpEntity<EmpresaModel>> deferredResult = new DeferredResult<>();
        this.empresaService.salvar(modelMapper.toDomainObject(empresaInput))
                .doOnError(error -> log.error("Erro em EmpresaController.salvar() ao tentar salvar a empresa"))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<HttpEntity<EmpresaModel>> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<EmpresaModel>> deferredResult = new DeferredResult<>();
        empresaService.buscarOuFalhar(id)
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(modelMapper.toModel(response),
                                HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PutMapping("/{id}")
    public DeferredResult<HttpEntity<EmpresaModel>> atualizar(@PathVariable("id") Long id,
                                                              @RequestBody @Valid EmpresaInput empresaInput) {
        DeferredResult<HttpEntity<EmpresaModel>> deferredResult = new DeferredResult<>();
        empresaService.buscarOuFalhar(id)
                .subscribe(response ->
                        {
                            modelMapper.copyToDomainObject(empresaInput, response);
                            empresaService.update(empresaInput, response)
                                    .subscribe(empresaSalvo ->
                                                    deferredResult.setResult(
                                                            new ResponseEntity<>(modelMapper.toModel(empresaSalvo),
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
        empresaService.excluir(id)
                .subscribe(success -> deferredResult.setResult(new ResponseEntity<>("", HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

}
