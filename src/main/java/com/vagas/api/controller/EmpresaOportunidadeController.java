package com.vagas.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.vagas.api.model.OportunidadeModel;
import com.vagas.api.model.input.OportunidadeInput;
import com.vagas.api.modelmapper.OportunidadeModelMapper;
import com.vagas.domain.service.OportunidadeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/empresa/{empresaId}/oportunidade")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmpresaOportunidadeController {
	
	private final OportunidadeService oportunidadeService;
    private final OportunidadeModelMapper modelMapper;

    @GetMapping
    public DeferredResult<HttpEntity<Page<OportunidadeModel>>> findAll(@PathVariable Long empresaId, Pageable pageable) {
    	DeferredResult<HttpEntity<Page<OportunidadeModel>>> deferredResult = new DeferredResult<>();
    	
    	oportunidadeService.listarTodos(empresaId, pageable)
    			.subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response.getT2(), HttpStatus.OK)),
                        deferredResult::setErrorResult);
    	
    	return deferredResult;
    }

    @PostMapping
    public DeferredResult<HttpEntity<OportunidadeModel>> salvar(@RequestBody @Valid OportunidadeInput oportunidadeInput, 
    		@PathVariable Long empresaId) {
        DeferredResult<HttpEntity<OportunidadeModel>> deferredResult = new DeferredResult<>();
        this.oportunidadeService.salvar(modelMapper.toDomainObject(oportunidadeInput), empresaId)
                .doOnError(error -> log.error("Erro em OportunidadeController.salvar() ao tentar salvar o benefício"))
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response.getT2(), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<HttpEntity<OportunidadeModel>> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<OportunidadeModel>> deferredResult = new DeferredResult<>();
        oportunidadeService.buscarOuFalhar(id)
                .subscribe(response -> deferredResult
                                .setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PutMapping("/{id}")
    public DeferredResult<HttpEntity<OportunidadeModel>> atualizar(@PathVariable("id") Long id, @PathVariable Long empresaId,
                                       @RequestBody @Valid OportunidadeInput oportunidadeInput) {
        DeferredResult<HttpEntity<OportunidadeModel>> deferredResult = new DeferredResult<>();
        oportunidadeService.buscarOuFalhar(id)
                .subscribe(response -> oportunidadeService.update(oportunidadeInput, response, empresaId)
                                .subscribe(oportunidadeSalvo ->
                                                deferredResult.setResult(new ResponseEntity<>(oportunidadeSalvo.getT2(), HttpStatus.OK)),
                                        deferredResult::setErrorResult)
                        , deferredResult::setErrorResult);
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<HttpEntity<String>> remover(@PathVariable Long id, @PathVariable Long empresaId) {
        DeferredResult<HttpEntity<String>> deferredResult = new DeferredResult<>();
        oportunidadeService.excluir(empresaId, id)
                .subscribe(data -> deferredResult.setResult(new ResponseEntity<>("", HttpStatus.OK)), deferredResult::setErrorResult);
        return deferredResult;
    }

}
