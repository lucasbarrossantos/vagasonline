package com.vagas.api.controller;

import com.vagas.api.model.UsuarioModel;
import com.vagas.api.model.input.UsuarioInput;
import com.vagas.api.modelmapper.UsuarioModelMapper;
import com.vagas.domain.service.UsuarioService;
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
@RequestMapping("/usuario")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsuarioController {
    private final Logger log = LoggerFactory.getLogger(UsuarioController.class);

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
                .subscribe(response -> deferredResult.setResult(new ResponseEntity<>(response, HttpStatus.OK)),
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
