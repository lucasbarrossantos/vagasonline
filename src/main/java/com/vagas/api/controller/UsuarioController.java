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

import com.vagas.api.model.UsuarioModel;
import com.vagas.api.model.input.UsuarioInput;
import com.vagas.api.modelmapper.UsuarioModelMapper;
import com.vagas.domain.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelMapper modelMapper;

    @GetMapping
    public Mono<Page<UsuarioModel>> findAll(Pageable pageable) {
        return usuarioService.listarTodos(pageable);
    }

    @PostMapping
    public DeferredResult<?> salvar(@RequestBody @Valid UsuarioInput usuarioInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        this.usuarioService.salvar(modelMapper.toDomainObject(usuarioInput))
                .doOnError(error -> log.error("Erro em UsuarioController.salvar() ao tentar salvar o usuário"))
                .subscribe(response ->
                                deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<?> findById(@PathVariable("id") Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        usuarioService.buscarOuFalhar(id)
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(modelMapper.toModel(response), HttpStatus.OK)),
                        deferredResult::setErrorResult);
        return deferredResult;
    }

    @PutMapping("/{id}")
    public DeferredResult<?> atualizar(@PathVariable("id") Long id,
                                       @RequestBody @Valid UsuarioInput usuarioInput) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        usuarioService.buscarOuFalhar(id)
                .subscribe(response -> usuarioService.update(usuarioInput, response)
                                .subscribe(usuarioSalvo ->
                                                deferredResult.setResult(new ResponseEntity<>(usuarioSalvo, HttpStatus.OK)),
                                        deferredResult::setErrorResult)
                        , deferredResult::setErrorResult);
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeferredResult<?> remover(@PathVariable Long id) {
        DeferredResult<HttpEntity<?>> deferredResult = new DeferredResult<>();
        usuarioService.excluir(id)
                .subscribe(data -> log.info("Usuário excluído com sucesso!"), error -> {
                    log.error("Erro ao tentar excluir o usuário. UsuarioController.remover()");
                    deferredResult.setErrorResult(error);
                });
        return deferredResult;
    }

}
