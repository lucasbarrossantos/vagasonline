package com.vagas.api.controller;

import javax.validation.Valid;

import com.vagas.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import com.vagas.api.model.UsuarioModel;
import com.vagas.api.model.input.UsuarioInput;
import com.vagas.api.modelmapper.UsuarioModelMapper;
import com.vagas.domain.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<UsuarioModel>> findAll(Pageable pageable) {
        Page<Usuario> usuarioPage = usuarioService.listarTodos(pageable);
        return new ResponseEntity<>(
                new PageImpl<>(modelMapper.toCollectionModel(usuarioPage.getContent()),
                        pageable, usuarioPage.getTotalElements())
                , HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> salvar(@RequestBody @Valid UsuarioInput usuarioInput) {
        Usuario salvarSalvo = usuarioService.salvar(modelMapper.toDomainObject(usuarioInput));
        return new ResponseEntity<>(modelMapper.toModel(salvarSalvo), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> findById(@PathVariable("id") Long id) {
        Usuario usuario = usuarioService.buscarOuFalhar(id);
        return new ResponseEntity<>(modelMapper.toModel(usuario), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> atualizar(@PathVariable("id") Long id,
                                                  @RequestBody @Valid UsuarioInput usuarioInput) {
        Usuario usuario = usuarioService.buscarOuFalhar(id);
        modelMapper.copyToDomainObject(usuarioInput, usuario);
        Usuario usuarioAtualizado = usuarioService.update(usuarioInput, usuario);
        return new ResponseEntity<>(modelMapper.toModel(usuarioAtualizado), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
